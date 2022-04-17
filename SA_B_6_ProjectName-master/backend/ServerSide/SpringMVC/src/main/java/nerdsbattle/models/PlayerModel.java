package nerdsbattle.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import nerdsbattle.Attribute;


/**
 * A child of the DatabaseHandler designed specifically to 
 * interact with the database only for tasks related to
 * Players.
 * @author Sammy
 *
 */
public class PlayerModel extends MyModel{
	//String array to store the names of the columns in Player table
	private static String[] pCols;
	
	/**
	 * Create a new PlayerHandler object. Calls the
	 * super constructor, then sets the table value
	 * to 'Player' and the primaryKey to 'Username',
	 * and retrieves the column names from the 'Player'
	 * table.
	 */
	public PlayerModel() {
		super();
		table = "Players";
		primaryKey = "Username";
		if(pCols == null) pCols = retrieveColumns();
	}
	
	/**
	 * Retrieves the attributes for a specific user
	 * as an int[] to be used in a Battler.
	 * @param username
	 * The username of the user to retrieve the values
	 * of their attributes for.
	 * @return
	 * An int[] with a slot for Defense, Accuracy, Speed,
	 * Strength, and Health. Health starts at 100 while the
	 * other values depend on that users attributes in
	 * the database.
	 */
	public int[] retrieveAttributes(String username) {
		int[] res = new int[Attribute.values().length];
		//Connect to DB
		connect();
		
		try {
			Statement s = con1.createStatement();
			//Get Accuracy, Defense, Speed and Strength from the specified player
			String query = "SELECT Accuracy, Defense, Speed, Strength FROM Attributes WHERE User ='" + username + "'";
			ResultSet rs = s.executeQuery(query);
			if(rs.next()) {
				//Set the attributes to their corresponding values
				res[Attribute.ACCURACY.ordinal()] = rs.getInt("Accuracy");
				res[Attribute.DEFENSE.ordinal()]  = rs.getInt("Defense");
				res[Attribute.SPEED.ordinal()]    = rs.getInt("Speed");
				res[Attribute.STRENGTH.ordinal()] = rs.getInt("Strength");
				res[Attribute.HEALTH.ordinal()] = 100;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Close connection
		disconnect();
		return res;
	}
	
	/**
	 * Purchase an item, given a specific username and itemname.
	 * This will check to see if the user has enough money and 
	 * if the user already owns the item. If all is okay, the 
	 * item will be added to the users inventory (Items column)
	 * and their value will be reduced accordingly
	 * @param username
	 * The username for the user whose inventory the item should
	 * be added to.
	 * @param itemname
	 * The name of the item to be added to the users inventory.
	 * @return
	 * One of the following Strings:
	 *   "Success" : The item was successfully purchased
	 *   "Item already owned" : This item is already in users inventory
	 *   "Insufficient funds" : Not enough money to buy this item
	 *   "Error" : Fatal error occurred. 
	 */
	public String purchaseItem(String username, String itemname) {
		String out = "Error";
		//Connect to DB
		connect();
		
		if(ownsItem(username, itemname, false)) out = "Item already owned";
		else {
		  //Get the specified users Money
		  Object pmoney = getValueFromPlayer(username, "Money", false);
		  Object ivalue = new ItemModel().getValueFromItem(itemname, "Value", true);
		  int money = pmoney == null ? -1 : (int) pmoney;
		  int value = ivalue == null ? -1 : (int) ivalue;

		  //If the username and itemname were both valid
		  if(money > -1 && value > -1) {
		    //If the user can afford the item
		    if(value <= money) {
		      addItem(username, itemname, false);
		      out = "Success";
		    }
		    else {
		      //val was not less than or equal to money, so the user doesn't have enough to buy this item
		      out = "Insufficient funds";
		    }
		  }
		}
		//Close the connection
		disconnect();
		return out;
	}
	
	private boolean ownsItem(String username, String item, boolean connect) {
	  if(connect) connect();
	  boolean owns = false;
	  
	  String query = "SELECT Item FROM Inventories WHERE User = '" + username + "' AND Item = '" + item + "'";
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      owns =  rs.next();  
    } catch (SQLException e) {
      e.printStackTrace();
    } 
	  if(connect) disconnect();
	  return owns;
	}
	
	/**
	 * Set the value of a specified column for the provided player
	 * @param username
	 * The username of the player to add a value to
	 * @param column
	 * The column to add the value to
	 * @param value
	 * The value to add
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
	 * database or not (i.e called from within a method that is already connected).
	 */
	public void addValue(String username, String column, Object value, boolean connect) {
	  if(connect) connect();
	  switch(column) {
	    //If a friend is being added to a player
  	  case "Friends" : addFriend(username, (String) value, false); break;
  	  //If a player attribute is being updated
  	  case "Accuracy":
      case "Defense":
      case "Speed":
      case "Strength": addAttribute(username, column, (String) value, false); break;
      //If a player equipped item is being updated
      case "Range":
      case "Melee":
      case "Clothing":
      case "Glasses":
      case "HairStyle": addEquipped(username, column, (String) value, false); break;
      //If an item is being added to a players inventory
      case "Items": addItem(username, (String) value, false); break;
      //Else
  	  default:{
  	    String query = "UPDATE Players SET " + column + " = '" + value + "' WHERE Username = '" + username + "'";
  	    try {
          con1.createStatement().execute(query);
        } catch (SQLException e) {
          e.printStackTrace();
        }
  	  }
	  }
	  if(connect) disconnect();
	}
	
	/**
	 * Add an item to the specified players inventory
	 * @param username
	 * The username of the player whose inventory should be modified.
	 * @param item
	 * The item to add to the players inventory
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 */
	public void addItem(String username, String item, boolean connect) {
	  if(connect) connect();
	 
	  String query = "INSERT INTO Inventories (User,Item) VALUES ('" + username + "','" + item + "')";
	  try {
	    con1.createStatement().execute(query);
	  } catch (SQLException e) {
	    e.printStackTrace();
	  }
	  if(connect) disconnect();
	}
	
	
	/**
	 * Modify the specified players attribute value, specified by the
	 * attribute name.
	 * @param username
	 * The username for the player whose attribute will be modified.
	 * @param attribute
	 * The name of the attribute to be modified.
	 * @param value
	 * The value to place in that attribute.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 */
	public void addAttribute(String username, String attribute, String value, boolean connect) {
	  if(connect) connect();
	  String query = "UPDATE Attributes SET " + attribute + " = " + value + " WHERE User = '" + username + "'";
	  try {
      con1.createStatement().execute(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  if(connect) disconnect();
	}
	
	/**
	 * Modify the specified players equipped items specified by the
	 * slot name.
	 * @param username
	 * The username of the player whose equipped items are to be modified.
	 * @param slot
	 * The name of the equipped item slot to be modified. (i.e Range, Melee, Clothing, Glasses, HairStyle)
	 * @param item
	 * The name of the item to be placed in the specified slot.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 */
	public void addEquipped(String username, String slot, String item, boolean connect) {
	  if(connect) connect();

	  if(item.isEmpty()) {
	    setEquippedItem(username, slot, "default", false);
	  }
	  else {
	    boolean owns = ownsItem(username, item, false);
	    String type = (String) new ItemModel().getValueFromItem(item, "Type", true);
	    if(owns && type.equals(slot)) {
  	    item = "'" + item + "'"; //Add single quotes around it since it is a string
  	    setEquippedItem(username, slot, item, false);
  	  }
	  }
	  if(connect) disconnect();
	}
	
	private void setEquippedItem(String username, String slot, String item, boolean connect) {
	  if(connect) connect();
	  if(slot.equals("Range")) slot += "d";
    String query = "UPDATE Equipped SET " + slot + " = " + item + " WHERE User = '" + username + "'";
    try {
      con1.createStatement().execute(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  if(connect) disconnect();
	}

	
	/**
	 * Add a friend-relationship between the specified user and friend.
	 * Only needs to be called once per friendship. (i.e adds an entry
	 * to the table for BOTH players).
	 * @param username
	 * The username of the user to add a relationship with
	 * @param friend
	 * The username of the friend to add a relationship with
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 */
	 public void addFriend(String username, String friend, boolean connect) {
	    if(connect) connect();
	    
	    if(!hasFriend(username, friend, false)) {
  	    String query1 = "INSERT INTO Friends (User,Friend) VALUES ('" + username + "','" + friend +"')";
  	    String query2 = "INSERT INTO Friends (User,Friend) VALUES ('" + friend + "','" + username + "')";    
  	    try {
  	      Statement s = con1.createStatement();
  	      s.execute(query1);    
  	      s.execute(query2);    
  	    } catch (SQLException e) {
  	      e.printStackTrace();
  	    }
	    }
	    if(connect) disconnect();
	  }
	
	 /**
	  * Determine whether the specified player is a Moderator.
	  * Moderators have extra privileges that regular users lack.
	  * @param username
	  * The username of the player to check the moderator status of.
	  * @param connect
	  * Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
	  * @return
	  * True if the specified user is a moderator. False otherwise.
	  */
	 public boolean isModerator(String username, boolean connect) {
	   if(connect) connect();
	   boolean res = false;
	   String query = "SELECT * FROM Moderators WHERE Username = '" + username + "'";
	   try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      res = rs.next();
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  if(connect) disconnect(); 
	  return res;
	 }
	 
	 /**
	  * Determine whether the specified player is an Admin.
	  * Admins are Moderators who have even further privileges.
	  * @param username
	  * The username of the player to check the admin status of.
	  * @param connect
	  * Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
	  * @return
	  * True if the specified user is an Admin. False otherwise.
	  */
   public boolean isAdmin(String username, boolean connect) {
     if(connect) connect();
     boolean res = false;
     String query = "SELECT * FROM Admins WHERE Username = '" + username + "'";
     try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      res = rs.next();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    if(connect) disconnect(); 
    return res;
   }
   
   /**
    * Promote the specified user to have Moderator status.
    * This grants the regular user special privileges.
    * @param username
    * The username of the player to promote to a Moderator from a
    * regular user.
    * @param connect
    *  Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
    */
   public void promoteToModerator(String username, boolean connect) {
     if(connect) connect(); 
     if(!isModerator(username, false)) {
       String query = "INSERT INTO Moderators (Username) VALUES ('" + username + "')";
       try {
        con1.createStatement().execute(query);
      } catch (SQLException e) {
        e.printStackTrace();
      }
     }   
     if(connect) disconnect();
   }
   
   /**
    * Promote the specified user to have Admin status.
    * This grants the moderator full privileges.
    * @param username
    * The username of the player to promote to an Admin from
    * either a regular user or moderator.
    * @param connect
    * Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
    */
   public void promoteToAdmin(String username, boolean connect) {
     if(connect) connect();
     if(!isAdmin(username, false)) {
       promoteToModerator(username, false);
       String query = "INSERT INTO Admins (Username) VALUES ('" + username + "')";
       try {
         con1.createStatement().execute(query);
       } catch (SQLException e) {
         e.printStackTrace();
       }
     }  
     if(connect) disconnect();
   }
   
   /**
    * Demote the specified user to Moderator status
    * from Admin status. The player will no longer have
    * full privileges.
    * @param username
    * The username of the player to demote to Moderator status.
    * @param connect
    * Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
    */
   public void demoteToModerator(String username, boolean connect) {
     if(connect) connect();
     if(isAdmin(username, false)) {
       String query = "DELETE FROM Admins WHERE Username = '" + username + "'";
       try {
         con1.createStatement().execute(query);
       } catch (SQLException e) {
         e.printStackTrace();
       }
     }
     if(connect) disconnect();
   }
   
   /**
    * Demote the specified player to regular user status from
    * Moderator or Admin status. The player will no longer have
    * any special privileges.
    * @param username
    * The username of the player to demote to regular user status.
    * @param connect
    * Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
    */
   public void demoteToRegular(String username, boolean connect) {
     if(connect) connect();
     demoteToModerator(username, false);
     if(isModerator(username, false)) {
       String query = "DELETE FROM Moderators WHERE Username = '" + username + "'";
       try {
         con1.createStatement().execute(query);
       } catch (SQLException e) {
         e.printStackTrace();
       }
     }
     if(connect) disconnect();
   }
   
   /**
    * Determine whether the specified user is banned.
    * Banned users cannot play.
    * @param username
    * The username of the player to check the banned status of.
    * @param connect
    * Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
    * @return
    * True if the specified user is banned. False otherwise.
    */
   public boolean isBanned(String username, boolean connect) {
     if(connect) connect();
     boolean res = false;
     String query = "SELECT * FROM Banned WHERE Username = '" + username + "'";
     try {
       ResultSet rs = con1.createStatement().executeQuery(query);
       res = rs.next();
     } catch (SQLException e) {
       e.printStackTrace();
     }
     if(connect) disconnect();
     return res;
   }
   
   /**
    * Ban a specified player. A banned player cannot play the game.
    * @param username
    * The username of the player to ban.
    * @param connect
    * Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
    * @return
    * True if the player could be banned. False otherwise.
    */
   public boolean banPlayer(String username, boolean connect) {
     if(connect) connect();
     if(isBanned(username, false)) return false;
     String query = "INSERT INTO Banned (Username) VALUES ('" + username + "')";
     try {
       con1.createStatement().execute(query);
     } catch (SQLException e) {
       e.printStackTrace();
     }

     if(connect) disconnect();
     return true;
   }
   
   /**
    * Unban a specified banned player. The player will once again be
    * allowed to play.
    * @param username
    * The username of the banned player to unban.
    * @param connect
    * Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
    * @return
    * True if the player could be unbanned. False otherwise.
    */
   public boolean unbanPlayer(String username, boolean connect) {
     if(connect) connect();
     if(!isBanned(username, false)) return false;
     String query = "DELETE FROM Banned WHERE Username = '" + username + "'";
     try {
       con1.createStatement().execute(query);
     } catch (SQLException e) {
       e.printStackTrace();
     }
     if(connect) disconnect();
     return true;
   }
	 
   /**
    * Determine whether two specified players are friends.
    * @param username
    * The username of one player.
    * @param friend
    * The username of the other player. 
    * @param connect
    * Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
    * @return
    * True if the two players are friends. False otherwise.
    */
	 public boolean hasFriend(String username, String friend, boolean connect) {
	   if(connect) connect();
	   boolean has = false;
	   String query = "SELECT ID FROM Friends WHERE User = '" + username + "' AND Friend = '" + friend + "'";
	   try {
	     ResultSet rs = con1.createStatement().executeQuery(query);
	     has = rs.next();
	   } catch (SQLException e) {
	     e.printStackTrace();
	   }
	   if(connect) disconnect();
	   return has;
	 }
	
	 /**
	  * Get the value in a specified column from a specified player.
	  * @param username
	  * The username of the player to retrieve the value from.
	  * @param column
	  * The name of the column indicating which value to retrieve.
	  * @param connect
	  * Boolean to indicate whether this method needs to connect to the
    * database or not (i.e called from within a method that is already connected).
	  * @return
	  * The object associated with that column for the specified player.
	  */
	public Object getValueFromPlayer(String username, String column, boolean connect) {
	  if(connect) connect();
	  Object o = null;
	  
	  switch(column) {
	    case "isAdmin": o = isAdmin(username, false); break;
	    case "isModerator": o = isModerator(username, false); break;
	    case "isBanned": o = isBanned(username, false); break;
  	  case "Friends": o = getFriends(username, false); break;
  	  case "Accuracy":
  	  case "Defense":
  	  case "Speed":
  	  case "Strength": o = getAttributes(username, false).get(column); break;
  	  case "Attributes": o = getAttributes(username, false); break;
  	  case "Equipped": o = getEquipped(username, false); break;
  	  case "Range":
  	  case "Melee":
  	  case "Glasses":
  	  case "HairStyle":
  	  case "Clothing": o = getEquipped(username, false).get(column); break;
  	  case "Items": o = getInventory(username, false); break;
  	  
  	  default: {
  	    String query = "SELECT " + column + " FROM Players WHERE Username = '" + username + "'";
  	    try {
  	      ResultSet rs = con1.createStatement().executeQuery(query);
  	      if(rs.next()) {
  	        o = rs.getObject(column);
  	      }
  	    } catch (SQLException e) {
  	      e.printStackTrace();
  	    }
  	  }
	  }

	  if(connect) disconnect();
	  return o;
	}
	
	/**
	 * Get all the players in the database. This also retrieves
	 * their Friends, Attributes, Mod/Ad/Ban status, Equipped,
	 * and Items.
	 * @return
	 * A JSONArray of JSONObjects, each representing a player
	 * from the database.
	 */
	public JSONArray getAllPlayers() {
	  connect();
	  JSONArray jar = new JSONArray();
	  String query = "SELECT Username FROM Players"; 
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      while(rs.next()) {
        String username = rs.getString("Username");
        JSONObject user = getPlayer(username, false);
        jar.put(user);
      }
      
    } catch (SQLException e) {
      e.printStackTrace();
    }  
	  disconnect();
	  return jar;
	}

	/**
	 * Get all the players from the database, but only retrieve
	 * the specified columns from the database.
	 * @param columns
	 * The columns to retrieve from all players in the database.
	 * @return
	 * A JSONArray of JSONObjects containing the requested data
	 * from the database.
	 */
	public JSONArray getAllPlayers(ArrayList<String> columns) {
	  if(columns.isEmpty()) return getAllPlayers();
	  JSONArray jar = new JSONArray();
	  connect();
	  String query = "SELECT Username FROM Players";
	  try {
      ResultSet rs1 = con1.createStatement().executeQuery(query); 
      while(rs1.next()) {
        JSONObject job = new JSONObject();
        String username = rs1.getString("Username");
        for(String col : columns) {
          if(!col.equals("Username")) job.put(col, getValueFromPlayer(username, col, false));
        }
        job.put("Username", username);
        jar.put(job);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  
	  disconnect();
	  return jar;
	}
	
	//Return a specific player as a JSONObject
	/**
	 * Get a specified player. This also retrieve the players
	 * Friends, Attributes, Mod/Ad/Ban status, Equipped, and Items.
	 * @param username
	 * The username of the player to retrieve from the database.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * A JSONObject representing the specified player retrieved from
	 * the database.
	 */
	public JSONObject getPlayer(String username, boolean connect) {
	  if(connect) connect();
	  JSONObject job = new JSONObject();
	  job.put("Username", username);
	  
	  String query = "SELECT Password, GPA, XP, Money FROM Players WHERE Username = '" + username + "'";
	  
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      
      if(rs.next()) {
        String[] cols = {"Password", "GPA", "XP", "Money"};
        for(String col : cols) {
          job.put(col, rs.getObject(col));
        }
        
        JSONObject attributes = getAttributes(username, false);
        for(String attribute : attributes.keySet()) {
          job.put(attribute, attributes.get(attribute));
        }
        
        JSONArray items = getInventory(username, false);
        job.put("Items", items);
        JSONArray friends = getFriends(username, false);
        job.put("Friends", friends);
        JSONObject equipped = getEquipped(username, false);
        job.put("Equipped", equipped);
        boolean isAdmin = isAdmin(username, false);
        boolean isModerator = isAdmin ? true : isModerator(username, false);
        boolean isBanned = isBanned(username, false);
        
        job.put("isAdmin", isAdmin);
        job.put("isModerator", isModerator);
        job.put("isBanned", isBanned);
      }
      
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  
	  if(connect) disconnect();
	  return job;
	}
	
	/**
	 * Get the specified columns from a specific player.
	 * @param username
	 * The username of the player to retrieve the columns from.
	 * @param columns
	 * The columns for the player to retrieve from the database
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * A JSONArray with one JSONObject in it representing that player. Must be a JSONArray
	 * for proper parsing.
	 */
	public JSONArray getPlayer(String username, ArrayList<String> columns, boolean connect) {
	  JSONArray jar = new JSONArray();
	  JSONObject job = new JSONObject();
	  if(connect) connect();
	  
	  for(String col : columns) {
	    job.put(col, getValueFromPlayer(username, col, false));
	  }
	  
	  if(columns.isEmpty()) job = getPlayer(username, false);
	  jar.put(job);
	  if(connect) disconnect();
	  return jar;
	}
	
	/**
	 * Get the items that a specified player has in their inventory.
	 * @param username
	 * The username of the player whose inventory should be retrieved.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * A JSONArray of JSONObjects representing an item in the players
	 * inventory. Each object has only a Type and Title attribute.
	 */
	public JSONArray getInventory(String username, boolean connect) {
	  if(connect) connect();
	  JSONArray jar = new JSONArray();
	  
	  String query = "SELECT Item FROM Inventories WHERE User = '" + username + "'";
	  
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);     
      while(rs.next()) {
        String item = rs.getString("Item");
        String query2 = "SELECT Type FROM Items WHERE Title = '" + item + "'";       
        ResultSet rs2 = con1.createStatement().executeQuery(query2);     
        if(rs2.next()) {
          String type = rs2.getString("Type");
          JSONObject job = new JSONObject();
          job.put("Type", type);
          job.put("Title", item);
          jar.put(job);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  if(connect) disconnect();
	  return jar;
	}
	
	/**
	 * Get a list of the friends for a specified player.
	 * @param username
	 * The username of the player to retrieve the friends of.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * A JSONArray of Strings, each is the username of a friend
	 * of the specified player.
	 */
	public JSONArray getFriends(String username, boolean connect) {
	  if(connect) connect();
	  JSONArray jar = new JSONArray();
	  String query = "SELECT Friend FROM Friends WHERE User = '" + username + "'"; 
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);   
      while(rs.next()) {
        String friend = rs.getString("Friend");
        jar.put(friend);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  if(connect) disconnect();
	  return jar;
	}
	
	//Return a specific users equipped items as a JSONObject
	/**
	 * Get the equipped items for a specified player.
	 * @param username
	 * The username of the player to retrieve the equipped items of.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * A JSONObject representing an equipped item.
	 */
	public JSONObject getEquipped(String username, boolean connect) {
	  if(connect) connect();
	  String query = "SELECT Ranged, Melee, HairStyle, Clothing, Glasses FROM Equipped WHERE User = '" + username + "'";
	  JSONObject job = new JSONObject();
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);     
      if(rs.next()) {
        job.put("Range", "" + rs.getString("Ranged"));
        job.put("Melee", "" + rs.getString("Melee"));
        job.put("HairStyle", "" + rs.getString("HairStyle"));
        job.put("Clothing", "" + rs.getString("Clothing"));
        job.put("Glasses", "" + rs.getString("Glasses"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }	  
	  if(connect) disconnect();
	  return job;
	}
	
	public String getRangeTitle(String username, boolean connect) {
	  if(connect) connect();
	  String out = "";
	  String query = "SELECT Ranged FROM Equipped WHERE User = '" + username + "'";  
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      if(rs.next()) {
        out = rs.getString("Ranged");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  if(connect) disconnect();
	  return out;
	}
	
	public int getRangedPhotoID(String username, boolean connect) {
	  if(connect) connect();
	  int out = -1;
	  
	  String title = getRangeTitle(username, false);
	  if(title == null) return out;
	  
	  String query = "SELECT PhotoID FROM Items WHERE Title = '" + title + "'";
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      if(rs.next()) {
        out = rs.getInt("PhotoID");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  if(connect) disconnect();
	  return out;
	}
	
	/**
	 * Get the attributes for a specified player.
	 * @param username
	 * The username of the player whose attributes should be retrieved.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * A JSONObject representing the specified players attributes.
	 * The attributes consist of 'Accuracy','Defense','Speed','Strength'
	 */
	public JSONObject getAttributes(String username, boolean connect) {
	  if(connect) connect();
	  JSONObject job = new JSONObject();
	  
	  String query = "SELECT Accuracy, Defense, Speed, Strength FROM Attributes WHERE User = '" + username + "'";
	  
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      if(rs.next()) {
        String[] cols = {"Accuracy", "Defense", "Speed", "Strength"};
        for(String col : cols) {
          job.put(col, rs.getInt(col));
        }
      }
      
    } catch (SQLException e) { 
      e.printStackTrace();
    }
	  
	  if(connect) disconnect();
	  return job;
	}
	//Check to see if a player exists
	/**
	 * Determine whether the specified player exists in the database
	 * already.
	 * @param username
	 * The username for the player to find in the database.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * True if the player is already in the database. False otherwise.
	 */
	public boolean playerExists(String username, boolean connect) {
	  if(connect) connect();
	  boolean exists = false;
	  String query = "SELECT Username FROM Players WHERE Username = '" + username + "'";
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      exists = rs.next();
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  if(connect) disconnect();
	  return exists;
	}
	
	/**
	 * Add a player to the database. Only requires a username
	 * and password. Should probably also include email address
	 * or some other information.
	 * Also adds an entry to the equipped table for the player
	 * @param username
	 * The username of the player to be added to the database.
	 * @param password
	 * The password for the new player. Should be encrypted prior
	 * to this point.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * True if the player was able to be added. False if the
	 * player already exists and therefore could not be added.
	 */
	public boolean addPlayer(String username, String password, boolean connect) {
	  if(connect) connect();
	  if(playerExists(username, false)) return false;
	
	  HashMap<String, String> values = new HashMap<String, String>();
	  values.put("Username",username);
	  values.put("Password",password);
	  super.insertEntry(values, false);
	  generateAttributes(username, false);
	  generateEquipped(username, false);
	  if(connect) disconnect();
	  return true;
	}
	
	/**
	 * Remove a specified player from the database.
	 * This involves removing any entries from tables that
	 * have a foreign key that references the Player table
	 * and any subsequent references for those entries.
	 * @param username
	 * The username of the player to remove from the database.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * True if the player exists and was removed. False otherwise.
	 */
	public boolean removePlayer(String username, boolean connect) {
	  if(connect) connect();
	  if(!playerExists(username, false)) return false;
	  
	  demoteToRegular(username, false);
	  clearAttributes(username);
	  clearMessages(username);
	  clearConversations(username);
	  clearInventory(username);
	  clearEquipped(username);
	  clearMaps(username);
	  clearFriends(username);
	  super.deleteEntry(username, false);
	  
	  if(connect) disconnect();
	  return true;
	}
	
	private void clearAttributes(String username) {
	  clear("Attributes","User",username);
	}
	
	private void clearConversations(String username) {
	  clear("Conversationists","Username",username);
	}
	
	private void clearMessages(String username) {
	  clear("Messages","Sender",username);
	}
	
	private void clearEquipped(String username) {
	  clear("Equipped","User",username);
	}
	
	private void clearFriends(String username) {
	  clear("Friends","User",username);
	  clear("Friends","Friend",username);
	}
	
	private void clearInventory(String username) {
	  clear("Inventories","User",username);
	}
	
	private void clearMaps(String username) {
	  clear("Maps","Creator",username);
	}
	
	private void clear(String table, String key, String value) {
	  String query = "DELETE FROM " + table + " WHERE " + key + " = '" + value + "'";
	  try {
      con1.createStatement().execute(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
	}
	
	
	private void generateAttributes(String username, boolean connect) {
	  if(connect) connect();
	  String query = "INSERT INTO Attributes (User) VALUES ('" + username + "')";
	  try {
	    con1.createStatement().execute(query);
	  } catch (SQLException e) {
	    e.printStackTrace();
	  }
	  if(connect) disconnect();
	}
	
	//Adds an entry to the equipped table for this new player 
	private void generateEquipped(String username, boolean connect) {
	  if(connect) connect();
	  String query = "INSERT INTO Equipped (User) VALUES ('" + username + "')";
	  try {
      con1.createStatement().execute(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  if(connect) disconnect();
	}
		
	@Override
	public HashMap<String, String> processSelectRequest(HttpServletRequest req) {
	  return super.processSelectRequest(req);
	}

	@Override
	public HashMap<String, String> processUpdateRequest(HttpServletRequest req) {
	  
	  HashMap<String, String> res = new HashMap<String, String>();

	  @SuppressWarnings("unchecked")
	  Enumeration<String> parameters = req.getParameterNames();

	  while(parameters.hasMoreElements()) {
	    String param = parameters.nextElement();
	    System.out.println(param + ": " + req.getParameter(param));
	    switch(param) {
  	    case "Friends": //Deprecated
  	    case "Items": break; //Deprecated
  	    case "Equipped": {
  	      getSlots(req.getParameter(param), res);
  	      break; 
  	    }
  	    default:  res.put(param, req.getParameter(param));
	    }
	  }
	  
	 
		return res;
	}
	
	public void getSlots(String jsonString, HashMap<String, String> data) {
    JSONObject equipped = new JSONObject(jsonString);     
    String[] slots = {"Range", "Melee", "Glasses", "HairStyle", "Clothing"};
    for(String slot : slots) {
      String item = equipped.has(slot) ? equipped.getString(slot) : "";
      data.put(slot, item);
    }
	}
	
	
	@Override
	public void updateEntry(HashMap<String, String> data) {
	  connect();
	  
	  String username = data.get("Username");
	  for(String key : data.keySet()) {
	    if(!key.equals("Username")) addValue(username, key, data.get(key), false);
	  }
	  
	  disconnect();
	}

	@Override
	public boolean isValidEntry(HashMap<String, String> values) {
		return values.containsKey("Username") && values.containsKey("Password");
	}
	
	@Override
	public String[] getColumns(){
		return pCols;
	}
	
}
