package nerdsbattle;

import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import nerdsbattle.models.BattleModel;
import nerdsbattle.models.MapModel;

/**
 * Represents a battle between clients. Has an array of players,
 * a communicator to handle communication, and numerous methods
 * that handle battle interaction.
 * @author Sammy
 *
 */
public class Battle {
  //Object that sends any updates/data back to the clients through their output streams
  private BattleCommunicator communicator;
  //Map each player to a unique number
  private HashMap<String, Integer> playerToNum;
  //The battlers in this battle
  private Battler[] players;
  //The username of the winner
	private String winner;
	//The map in which the battle is taking place
	private BattleMap map;
	//The name of the map that was chosen
	private String mapName;
	//A generated ID for this battle
	private String battleID;
	//The time this battle was started
	private long timeStarted;
	//The name of the default map
	private final String DEFAULT_MAP = "ClassroomA";
	//Booleans to represent different states of the battle
	private boolean hasStarted;
  private boolean isComplete;
  private boolean mapSelected;
	
  /**
   * Constructs a new Battle object with the given battleID.
   * Initializes all the instance variables.
   * @param battleID
   * A unique, generated ID for this battle.
   */
	public Battle(String battleID) {
	  this.playerToNum = new HashMap<String, Integer>(2);
	  this.players = new Battler[2];
	  this.battleID = battleID;
	  this.hasStarted = false;
	  //Create a new communicator and pass it this battle to refer to
	  this.communicator = new BattleCommunicator(this);
	  this.isComplete = false;
	  this.mapSelected = false;
	}
	
	/**
	 * Synchronized method to add a player to this game, given a username and
	 * an output stream. This is synchronized because it generates a unique ID
	 * based on the current size of the playerToNum hashmap.
	 * @param playerName
	 * The username of the player to add.
	 * @param out
	 * The clients output stream, used to send updates/data.
	 */
	public synchronized void addPlayer(String playerName, OutputStream out) {
	  int index = playerToNum.size();
	  int playerNumber = (index + 1) * 10;
	  players[index] = new Battler(playerName, out);
	  playerToNum.put(playerName, playerNumber);
	}
	
	/**
	 * Get all the players currently in this battle
	 * @return
	 * An array of battler objects containing the battlers in this battle
	 */
	public Battler[] getPlayers() {
	  return players;
	}
	
	/**
	 * Get a specific player from this battle.
	 * @param player
	 * The username of the player to get.
	 * @return
	 * The battler object that is associated with the desired player 
	 * or null if the player doesn't exist.
	 */
	public Battler getPlayer(String player) {
	  return playerToNum.containsKey(player) ? players[(playerToNum.get(player)/10)-1] : null;
	}
	
	/**
	 * Called to finalize the decision for the map selection.
	 * First looks at all the players selections. If the selection
	 * is not null, it adds to a list of options. If the list isn't
	 * empty, a random index is chosen from the list of options. If
	 * the list is empty, a default map 'ClassroomA' is chosen.
	 */
	public void determineMap() {
	  //If a map hasn't been selected yet (since this method is actually called twice per battle).
	  if(!this.mapSelected) {
  	  ArrayList<String> mapOptions = new ArrayList<String>();

  	  for(Battler player : players) {
  	    if(player.getChosenMap() != null) {
  	      mapOptions.add(player.getChosenMap());
  	    }
  	  }
  	  //If the list is empty, choose the default map. Otherwise choose a random index and get the element at that index from the list.
  	  this.mapName = mapOptions.isEmpty() ? DEFAULT_MAP : mapOptions.get(new Random().nextInt(mapOptions.size()));
  	  //Create a new BattleHandler to interact with the database and get the map with all of its obstacles and their data.
  	  this.map = new BattleModel().getBattleMapWithObstacles(mapName);
  	  //Indicate that a map has been found.
  	  this.mapSelected = true;
  	  //Initiate the battle.
  	  this.startBattle();
	  }
	}
	
	public void sendRandomMaps(String username) {
	  JSONArray choices = new MapModel().retrieveRandomMaps(3, true);
	  JSONObject out = new JSONObject();
	  out.put("Message", "Random Maps");
	  out.put("Maps", choices);
	  OutputStream outStream = getPlayer(username).getOutputStream();
	  communicator.sendRaw(outStream, out.toString());
	}
	
	/**
	 * Notify each client that the battle has started. Store the time started
	 * in the variable timeStarted. Indicate that the map has started, place
	 * the players on opposite sides of the map. Send the initial information
	 * about the battle.
	 */
	public void startBattle() {
	 if(!hasStarted) {
	  timeStarted = System.currentTimeMillis();
	  hasStarted = true;
	  map.placePlayersInitially();
	  communicator.sendStringToAll("Battle start");
	  communicator.sendUpdates();
	 }
	}
	
	/**
	 * Check whether this battle has started.
	 * @return
	 * True if the battle has started. False otherwise.
	 */
	public boolean hasStarted() {
	  return hasStarted;
	}
	
	/**
	 * Get the map for this battle.
	 * @return
	 * The BattleMap object being used by this battle.
	 */
	public BattleMap getMap() {
		return map;
	}
	
	/**
	 * Set the battleID for this battle
	 * @param battleID
	 * A unique, generated battleID for this battle.
	 */
	public void setBattleID(String battleID) {
	  this.battleID = battleID;
	}
	
	/**
	 * Extract the necessary information from this battle
	 * and store it in the Battle table in the database.
	 */
	public void saveToDatabase() {
		HashMap<String, String> vals = new HashMap<String, String>();
		int i = 1;
		for(Battler player : players) {
		  vals.put("Player" + i, player.getUsername());
		  i++;
		}
		
		//Winner must be determined
		vals.put("Winner", winner);
		vals.put("Map", mapName);
		vals.put("DateTime", LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStarted), ZoneId.systemDefault()).toString());
		vals.put("BattleID", battleID);
		
		
		//vals.put("Duration", LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() - timeStarted), ZoneId.systemDefault()));
	}
	
	/**
	 * Check if this battle is complete.
	 * @return
	 * True if this battle is complete. False otherwise.
	 */
  public boolean isComplete() {
    return isComplete;
  }
  
  /**
   * Method to finalize this battle, declaring the provided string
   * the winner of the battle.
   * @param winner
   * The username of the winner or DRAW if neither player defeats the
   * other within the time limit.
   */
  public void complete(String winner) {
    //saveToDatabase();
    //Do whatever else is needed.
    communicator.sendStringToAll("Game over. " + winner + " is the winner");
    isComplete = true;
    this.winner = winner;
  }
  
  /**
   * Moves the specified player one unit in the specified direction.
   * Sends updates to the clients if needed.
   * @param player
   * The username of the player to move.
   * @param direction
   * The direction to move the player. Down, Up, Left, and Right.
   */
  public void moveDirection(String player, String direction) {
    boolean isChanged = false;
    int playerNumber = playerToNum.get(player);
    int yChange = 0;
    int xChange = 0;
    switch(direction) {
      case "D" : yChange = 1;  break;
      case "U" : yChange = -1; break;
      case "L" : xChange = -1; break;
      case "R" : xChange = 1;  break;
      case "UL" : yChange = -1; xChange = -1; break;
      case "UR" : yChange = -1; xChange =  1; break;
      case "DL" : yChange =  1; xChange = -1; break;
      case "DR" : yChange =  1; xChange =  1; break;
    }
    isChanged = map.movePlayerFromCurrent(playerNumber, yChange, xChange);
    if(isChanged) {
      communicator.sendUpdates();
    }
  }
  
  /**
   * Moves the specified player to the coordinates provided by a 
   * percentage of the height (y) and a percentage of the width (x).
   * @param player
   * The username of the player to move.
   * @param y
   * A value between 1.0 and 0.0 representing the percentage of the
   * height of the BattleMap that the new Y coordinate will be.
   * @param x
   * A value between 1.0 and 0.0 representing the percentage of the
   * width of the BattleMap that the new X coordinate will be.
   */
  public void movePlayer(String player, double y, double x) {
    //If the player successfully moves, send updates to both clients
    if(map.movePlayer(playerToNum.get(player), y, x)) {
      //Send updates
      communicator.sendUpdates();
    }
  }
  
  /**
   * Get the unique player number for the spcified player.
   * @param player
   * The username of the player to get the number for.
   * @return
   * The player number for the specified player. Or -1 if
   * the player is not recognized.
   */
  public int getPlayerNum(String player) {
    if(playerToNum.containsKey(player)) {
      return playerToNum.get(player);
    }
    return -1;
  }
  
//  public void moveItem(String player, int itemId, double y, double x) {
//    int playerId = (playerToNum.get(player) / 10) - 1;
//    
//    int item = itemId + (playerId * 10);
//    
//    int result = map.moveItem(item, y, x);
// 
//    if(result > -2) {
//      if(result > -1) {
//        //reduce that players health by this players ranged item damage
//        players[result].reduceHealth(players[playerId].getRangeAttackValue());
//      }
//      //send updates
//      communicator.sendUpdates();
//    }
//  }
  
  /**
   * Indicates to the specified player object that it should melee attack in
   * the direction it is facing.
   * @param username
   * The username for the player who should attempt a melee attack.
   */
  public void attack(String username) {
    int playerNum = playerToNum.get(username);
    //Attack and see if the result indicates we hit someone.
    int result = map.attack(playerNum);
    //If the result is > -1, we hit another player and should do damage.
    if(result > -1) {
      //Get the player that was hit and reduce its health by the value of the melee item it was hit by.
      players[result].reduceHealth(players[(playerNum / 10) - 1].getMeleeAttackValue());
      //Send updates.
      communicator.sendUpdates();
      //If the player who was hit was killed, complete the battle with the specified winner.
      if(players[result].isDead()) {
        complete(players[(playerNum / 10) - 1].getUsername());
      }
    }
  }
  
  /**
   * Generate the JSON String that contains the necessary values
   * to display the updates on the client devices.
   * @return
   * A string representing a JSONArray that contains all the necessary
   * values in this battle.
   */
  public String getUpdates() {
    JSONArray jar = new JSONArray();
    //For each battle, make a JSONObject with their values.
    for(Battler player : players) {
      JSONObject job = new JSONObject();
      job.put("Username", player.getUsername());
      job.put("Health", player.getHealth());
      //Call a BattleMap method to get the players orientation (position & direction)
      job.put("Orientation", new JSONObject(map.getPlayerOrientation(playerToNum.get(player.getUsername()))));
      //Call a BattleMap method to get the Items orientations (position & direction)
      job.put("Items", new JSONArray(map.getItemOrientation(playerToNum.get(player.getUsername()))));
      //Add the object to the array
      jar.put(job);
    }
    //Return the JSONArray as a string
    return jar.toString();
  }
  
  /**
   * Check whether this battle has a map or not.
   * @return
   * True if a map has been selected, false otherwise.
   */
  public boolean hasMap() {
    return mapSelected;
  }
  
  /**
   * Method to simulate a projectile being thrown from a players
   * current position to a target Y and X.
   * @param player
   * The name of the player who is throwing the projectile.
   * @param yPer
   * A value between 1.0 and 0.0 representing the percentage of the BattleMap
   * height that the target y coordinate is.
   * @param xPer
   * A value between 1.0 and 0.0 representing the percentage of the BattleMap
   * width that the target x coordinate is.
   */
  public void simulateProjectile(String player, double yPer, double xPer) {
    //Convert the percentages to integer values.
    int targetY = (int) (yPer * map.getHeight());
    int targetX = (int) (xPer * map.getWidth());
    
    int playerNum = playerToNum.get(player);
    
    int playerIndex = (playerNum / 10) - 1;
    //If the player actually has a ranged item...
    if(players[playerIndex].hasRangeItem()) {
      //addProjectile returns an int array with 3 elements. 0 --> itemNumber, 1 --> startY, 2 --> startX
      int[] item = map.addProjectile(playerNum, targetY, targetX);
      //Determine the speed of the item.
      int itemSpeed = players[playerIndex].getRangeAttackSpeed();
      //If the item was successfully added, meaning the player had less than 10 items flying already, simulate the motion.
      if(item != null) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            //Use bresenhams line drawing algorithm to simulate the travel.
            if(bresenhamsLine(item[0], item[1], item[2], targetY, targetX, itemSpeed)) {
              //If the target coordinates were reached...
              int result = map.checkCoords(targetY, targetX);
              //If the item hit a player...
              if(result > -2) {
                if(result > -1) {
                  //reduce that players health by this players ranged item damage
                  players[result].reduceHealth(players[playerIndex].getRangeAttackValue());
                }
                //send updates
                communicator.sendUpdates();
              }
            }
          }
        }).start();
      }
    }
  }
  
  
  
  
  
  /**
   * An implementation of Bresenhams line drawing algorithm for all octants. This
   * is used to simulate a projectile in motion. This was taken from:
   * http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/
   * 
   * @param itemNum
   * The unique identifier for the item in motion.
   * @param y1
   * The source y coordinate
   * @param x1
   * The source x coordinate
   * @param y2
   * The target y coordinate
   * @param x2
   * The target x coordinate
   * @param speed
   * The speed of the item in motion
   * @return
   * True if the target coordinates were reached, false otherwise.
   */
  public boolean bresenhamsLine(int itemNum, int y1, int x1, int y2, int x2, int speed) { 
    int y = y1;
    int x = x1;

    int w = x2 - x;
    int h = y2 - y;

    int dx1 = w < 0 ? -1 : (w > 0 ? 1 : 0);
    int dy1 = h < 0 ? -1 : (h > 0 ? 1 : 0);
    int dx2 = w < 0 ? -1 : (w > 0 ? 1 : 0);
    int dy2 = 0;

    int longest = Math.abs(w) ;
    int shortest = Math.abs(h) ;
    if (!(longest>shortest)) {
      longest = Math.abs(h);
      shortest = Math.abs(w);
      dy2 = h < 0 ? -1 : (h > 0 ? 1 : dy2);
      dx2 = 0 ;            
    }
    int numerator = longest >> 1 ;
    for (int i=0;i<=longest;i++) {
      numerator += shortest ;
      if (!(numerator<longest)) {
        numerator -= longest ;
        x += dx1 ;
        y += dy1 ;
      } else {
        x += dx2 ;
        y += dy2 ;
      }
      //If the projectile could not move, it must've hit an object it cant go over.
      if(!map.moveProjectile(itemNum, y, x)) {
        //Remove the projectile and return false.
        map.removeProjectile(itemNum);
        return false;
      }
      //Otherwise, send the updates
      communicator.sendUpdates();
      try {
        //Then sleep for a certain amount of time depending on the items speed.
        Thread.sleep(Math.max(5, 50 - speed));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    //The target coordinates were reached.
    return true;
  }
	
}
