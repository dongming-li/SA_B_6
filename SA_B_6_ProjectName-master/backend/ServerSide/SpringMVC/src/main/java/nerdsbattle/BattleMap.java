package nerdsbattle;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class used to represent the map used in a Battle.
 * @author Sammy
 *
 */
public class BattleMap {
  //Store the players position and direction. Currently 2 rows and 4 cols.
  private int[][] playerPandD; //0 --> yPos    1 --> xPos    2 --> yDir    3 --> xDir  
  //Store the items position and direction. Currently 20 rows (10 for each player) and 4 cols.
  private int[][] itemPandD; //0 --> yPos    1 --> xPos    2 --> yDir    3 --> xDir  

  //Hard-coded values for the path of a melee attack based on the direction. Will be improved
  private static final int[][][] attackPath = fillAttackPath();
  //2D array to represent the battle board.
  private int[][] BATTLE_BOARD;
  //Width and height of battle board.
  private final int _MAP_WIDTH  = 500;
  private final int _MAP_HEIGHT = 500;
  //Constants for placing in the battle board.
  private final int _NO_OBSTACLE   = 0; //Empty space
  private final int _LOW_OBSTACLE  = 1; //Obstacle that can be thrown over
  private final int _HIGH_OBSTACLE = 2; //Obstacle that cannot be thrown over
  //Constants for accessing array values.
  private final static int _Y_POS = 0;
  private final static int _X_POS = 1;
  private final int _Y_DIR = 2;
  private final int _X_DIR = 3;

  //Numbers divisible by 10 are players. ie 20 is player at index 1 because 20 / 10 - 1 = 1. (offset of 1)
  //Numbers greater than 10 but not divisible by 10 are items for each player (10 each) ie 43 is player at index 3's item at index 3 (4th item). 43/10 - 1 = 3. And 43 % 10 = 3.


  /**
   * Constructs a new BattleMap object with the given
   * string representation of a JSONArray of obstacles 
   * @param obstaclesString
   * A string representation of a JSONArray of this
   * maps obstacles.
   */
  public BattleMap(String obstaclesString) {
    this.BATTLE_BOARD = new int[_MAP_HEIGHT][_MAP_WIDTH];
    this.playerPandD = new int[2][4]; //Each row has a y, x, y dir, and x dir
    this.itemPandD = new int[20][4];  //Each row has a y, x, y dir, x dir
    this.populateMap(obstaclesString);
  }

  /**
   * Get the height of this Battle Map.
   * @return
   * The height of this map.
   */
  public int getHeight() {
    return _MAP_HEIGHT;
  }

  /**
   * Get the width of this Battle Map.
   * @return
   * The width of this map.
   */
  public int getWidth() {
    return _MAP_WIDTH;
  }

  /**
   * Hard-coded attack path for melee attacks based on
   * the current direction of the player.
   * @return
   * The hard-coded melee attack path.
   */
  private static int[][][] fillAttackPath() {
    int[][][] p = new int[3][3][6];        // Direction attacker is facing
    p[0][0] = new int[] {-1,0,-1,-1,0,-1}; // -1,-1
    p[0][1] = new int[] {-1,1,-1,0,-1,-1}; // -1, 0
    p[1][0] = new int[] {-1,-1,1,-1,1,0};  //  0,-1
    p[0][2] = new int[] {0,1,-1,1,-1,0};   // -1,+1
    p[2][0] = new int[] {0,-1,1,-1,1,0};   // +1,-1
    p[1][2] = new int[] {1,1,0,1,-1,1};    //  0,+1  
    p[2][1] = new int[] {1,-1,1,0,1,1};    // +1, 0
    p[2][2] = new int[] {1,0,1,1,0,1};     // +1,+1

    return p;
  }

  /**
   * Generate the string representation of the specified
   * players orientation on the map (position and direction).
   * @param playerNumber
   * The specified player by their unique index.
   * @return
   * The string representation of a JSONObject of the 
   * specified players orientation.
   */
  public String getPlayerOrientation(int playerNumber) {
    int index = (playerNumber / 10) - 1;

    JSONObject job = new JSONObject();
    job.put("yPos", ((playerPandD[index][_Y_POS] * 1.0) / (_MAP_HEIGHT * 1.0)));
    job.put("xPos", ((playerPandD[index][_X_POS] * 1.0) / (_MAP_WIDTH * 1.0)));
    job.put("yDir", playerPandD[index][_Y_DIR]);
    job.put("xDir", playerPandD[index][_X_DIR]);

    return job.toString();
  }

  /**
   * Place all the players on the map initially.
   */
  public void placePlayersInitially() {
    int numPlayers = 2;

    for(int i=0; i< numPlayers; i++) {
      if(!placePlayer(i)) {
        System.out.println("Error placing player " + i);
      }
    }
  }

  /**
   * Place a specified player on the map.
   * @param index
   * The unique index for the specified player.
   * @return
   * True if the player could be placed. False otherwise.
   */
  private boolean placePlayer(int index) {
    int leftBound = 20;
    int rightBound = _MAP_WIDTH - 20;
    int lowerBound = 5;
    int upperBound = _MAP_HEIGHT - 5;
    int yDir = 0;
    int xDir = 0;
    if(index == 0) {
      //lower half
      lowerBound = (2 * _MAP_HEIGHT / 3) + 20;
      yDir = -1;
    }
    else if(index == 1) {
      //upper half
      upperBound = (_MAP_HEIGHT / 3) - 20;
      yDir = 1;
    }

    //Scan the cells within the bound for a place to put the player.
    for(int row = lowerBound; row < upperBound; row++) {
      for(int col = leftBound; col < rightBound; col++) {
        if(BATTLE_BOARD[row][col] == _NO_OBSTACLE) {
          BATTLE_BOARD[row][col] = (index + 1) * 10;
          playerPandD[index][_Y_POS] = row;
          playerPandD[index][_X_POS] = col;
          playerPandD[index][_Y_DIR] = yDir;
          playerPandD[index][_X_DIR] = xDir;
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Generate the string representation of the specified
   * players items orientations on the map (position and direction).
   * @param playerNumber
   * The unique index of the owner of the item.
   * @return
   * The string representation of a JSONArray of the 
   * specified items orientations.
   */
  public String getItemOrientation(int playerNumber) {
    JSONArray jar = new JSONArray();
    //Calculate the index
    int index = (playerNumber / 10) - 1;

    int bound = index + 10;
    //For each of this players items, create a JSONObject and add it to the JSONArray
    for(int i = index; i < bound; i++) {
      if(!(itemPandD[i][_Y_DIR] == 0 && itemPandD[i][_X_DIR]== 0)) {
        JSONObject job = new JSONObject();
        job.put("ItemNumber", playerNumber + i);
        job.put("yPos", ((itemPandD[i][_Y_POS] * 1.0) / (_MAP_HEIGHT * 1.0)));
        job.put("xPos", ((itemPandD[i][_X_POS] * 1.0) / (_MAP_WIDTH * 1.0)));
        job.put("yDir", itemPandD[i][_Y_DIR]);
        job.put("xDir", itemPandD[i][_X_DIR]);

        jar.put(job);
      }
    }

    return jar.toString();
  }

  /**
   * Using the provided string representation of a JSONArray
   * of the obstacles in this map, retrieved from the 
   * database, fill this map with the obstacles by calculating
   * their x, y, width, and height relative to the size of
   * this map.
   * @param obstaclesString
   * A string representation of a JSONArray containing this maps
   * obstacles.
   */
  public void populateMap(String obstaclesString) {
    JSONArray obstacles = new JSONArray(obstaclesString);

    for(int i=0; i < obstacles.length(); i++) {
      JSONObject obstacle = obstacles.getJSONObject(i);
      int height = obstacle.getInt("Height"); //0, 1, or 2
      double width  = obstacle.getDouble("Width");
      double length = obstacle.getDouble("Length");
      double x   = obstacle.getDouble("x"); //0.0 -- 1.0
      double y   = obstacle.getDouble("y"); //0.0 -- 1.0
      double r   = obstacle.getDouble("r");

      fill(height, width, length, x, y, r);
    }
  }

  /**
   * Using the provided parameters, fill the area on this map.
   * @param h
   * The height of the obstacle.
   * @param w
   * The width of the obstacle relative to this map width.
   * @param l
   * The length of the obstacle relative to this map height.
   * @param x
   * The x coordinate of the obstacle relative to this map width.
   * @param y
   * The y coordinate of the obstacle relative to this map height.
   * @param r
   * The rotation of the obstacle in radians.
   */
  private void fill(int h, double w, double l, double x, double y, double r) {
    int xPos = (int) (_MAP_WIDTH * x);
    int yPos = (int) (_MAP_HEIGHT * y);
    int width = (int) (_MAP_WIDTH * w);
    int length = (int) (_MAP_HEIGHT * l);

    for(int i=0; i < length && i + yPos < _MAP_HEIGHT; i++) {
      for(int j=0; j < width && j + xPos < _MAP_WIDTH; j++) {
        BATTLE_BOARD[yPos + i][xPos + j] = h;
      }
    }
  }

  /**
   * Print the board. Used for testing purposes.
   */
  public void printBoard() {
    for(int i=0; i < _MAP_HEIGHT; i++) {
      for(int j=0; j < _MAP_WIDTH; j++) {
        switch(BATTLE_BOARD[i][j]) {
        case _NO_OBSTACLE: System.out.print(" "); break;
        case _LOW_OBSTACLE: System.out.print("#"); break;
        case _HIGH_OBSTACLE: System.out.print("X"); break;
        default: {
          if(BATTLE_BOARD[i][j] >= 10) {
            int item = BATTLE_BOARD[i][j] % 10;
            int player = (BATTLE_BOARD[i][j] / 10) - 1;

            if(item == 0 && playerPandD[player][_Y_POS] == i && playerPandD[player][_X_POS] == j) {
              //Print player
              System.out.print(player);
            }
            else {
              //Print item
              System.out.print(item == 0 ? "$" : (item == 1 ? "@" : (item == 2 ? "%" : "&")));
            } 
          }
        }
        }
      }
      System.out.println();
    }
  }

  /**
   * Move the specified player to the provided coordinates.
   * @param playerNumber
   * The unique index for the specified player.
   * @param y
   * The y coordinate relative to this map height.
   * @param x
   * The x coordinate relative to this map width.
   * @return
   * True if the player moved to the coordinates. False otherwise.
   */
  public boolean movePlayer(int playerNumber, double y, double x) {

    int xPos = (int) (_MAP_WIDTH  * x);
    int yPos = (int) (_MAP_HEIGHT * y);

    if(BATTLE_BOARD[yPos][xPos] == _NO_OBSTACLE) {
      //Player position in array
      int pos = (playerNumber / 10) - 1; 

      if(pos >= 0) {
        int yDir = playerPandD[pos][_Y_POS] - yPos > 0 ? -1 : (playerPandD[pos][_Y_POS] - yPos < 0 ? 1 : 0);
        int xDir = playerPandD[pos][_X_POS] - xPos > 0 ? -1 : (playerPandD[pos][_X_POS] - xPos < 0 ? 1 : 0);

        BATTLE_BOARD[playerPandD[pos][_Y_POS]][playerPandD[pos][_X_POS]] = _NO_OBSTACLE;
        BATTLE_BOARD[yPos][xPos] = playerNumber;
        playerPandD[pos][_Y_POS] = yPos;
        playerPandD[pos][_X_POS] = xPos;
        playerPandD[pos][_Y_DIR] = yDir;
        playerPandD[pos][_X_DIR] = xDir;

        return true;
      }
    }
    return false;
  }

  /**
   * Add a projectile to this map for the specified player towards
   * the target coordinates.
   * @param playerNum
   * The unique index for the player who threw the projectile.
   * @param tY
   * The target y coordinate.
   * @param tX
   * The target x coordinate.
   * @return
   * An int array containing the item number in index 0, starting y
   * coordinate in index 1, and starting x coordinate in index 2. Or
   * null if the projectile could not be added.
   */
  public synchronized int[] addProjectile(int playerNum, int tY, int tX) {
    int player = (playerNum / 10) - 1;

    int offset = player * 10;
    for(int i = 0; i < 10; i++) {
      if(itemPandD[offset + i][_Y_DIR] == 0 && itemPandD[offset + i][_X_DIR] == 0) {
        int item = offset + i;

        int pY = playerPandD[player][_Y_POS];
        int pX = playerPandD[player][_X_POS];

        int yDir = tY > pY ? 1 : (tY < pY ? -1 : 0);
        int xDir = tX > pX ? 1 : (tX < pX ? -1 : 0);

        itemPandD[item][_Y_POS] = pY;
        itemPandD[item][_X_POS] = pX;
        itemPandD[item][_Y_DIR] = yDir;
        itemPandD[item][_X_DIR] = xDir;

        return new int[] {item, pY, pX};
      }
    }
    return null;
  }

  /**
   * Remove a projectile from this map.
   * @param projectileNum
   * The index of the projectile to remove.
   */
  public synchronized void removeProjectile(int projectileNum) {
    if(projectileNum < itemPandD.length) {
      itemPandD[projectileNum] = new int[4]; //Reset that items values to 0
    }
  }

  /**
   * Move the specified projectile to the specified coordinates.
   * @param projectileNum
   * The index of the projectile to move.
   * @param y
   * The y coordinate to move to.
   * @param x
   * The x coordinate to move to.
   * @return
   * True if the projectile could move to the coordinates. False otherwise.
   */
  public boolean moveProjectile(int projectileNum, int y, int x) {
    System.out.println("Projectile: " + projectileNum + " Y: " + y + "   X: " + x);
    if(projectileNum < itemPandD.length) {
      if(BATTLE_BOARD[y][x] != _HIGH_OBSTACLE) {
        itemPandD[projectileNum][_Y_POS] = y;
        itemPandD[projectileNum][_X_POS] = x;
        return true;
      }
    }
    return false;
  }

  /**
   * Move a specified player one cell from their current position
   * in the specified direction.
   * @param player
   * The index of the player to move.
   * @param yChange
   * The change in the y direction. (-1, 0, 1).
   * @param xChange
   * The change in the x direction. (-1, 0, 1).
   * @return
   * True if the player moved. False otherwise.
   */
  public boolean movePlayerFromCurrent(int player, int yChange, int xChange) {
    int pos = (player / 10) - 1; 
    int iY = playerPandD[pos][_Y_POS] + yChange;
    int iX = playerPandD[pos][_X_POS] + xChange;

    if(iY < 0 || iY >= _MAP_HEIGHT || iX < 0 || iX >= _MAP_WIDTH) return false;
    
    if(BATTLE_BOARD[iY][iX] == _NO_OBSTACLE) {
      System.out.println("[" + iY + "][" + iX + "] valid spot");
      if(pos >= 0) {
        BATTLE_BOARD[playerPandD[pos][_Y_POS]][playerPandD[pos][_X_POS]] = _NO_OBSTACLE;
        BATTLE_BOARD[iY][iX] = player;
        playerPandD[pos][_Y_POS] = iY;
        playerPandD[pos][_X_POS] = iX;
        return true;
      }
    }

    return false;
  }


  //RETURNS: 


  /**
   * Check the coordinates to see its value. 
   * @param y
   * The y coordinate.
   * @param x
   * The x coordinate.
   * @return
   * -2 if nothing happened.
   * -1 if something happened but no player was damaged.
   * 0 or greater if a player was hit, this is their index.
   */
  public int checkCoords(int y, int x) {

    if(BATTLE_BOARD[y][x] == _NO_OBSTACLE) {
      return -1; //-1 means an update occurred, but no player was hit 
    }
    else if(BATTLE_BOARD[y][x] == _LOW_OBSTACLE) {
      return -1; //-1 means an update occurred, but no player was hit
    }
    else if(BATTLE_BOARD[y][x] == _HIGH_OBSTACLE) {
      // Cannot go over, item ceases to exist here, trigger an update
      return -1; // Update occurred, no player hit
    }
    else if(BATTLE_BOARD[y][x] % 10 == 0) { //Only happens when a player was hit (or if no obstacle but thats taken care of at this point)
      // Do damage to other player, item ceases to exist, trigger an update
      return (BATTLE_BOARD[y][x] / 10) - 1; //Return the index of the player who was hit
    }
    return -2; //Means nothing happened, dont update
  }

  /**
   * Melee attack in the direction the specified player is facing.
   * @param playerNumber
   * The unique index of the player who is to perform a melee attack.
   * @return
   * -1 if nothing was hit.
   *  0 or greater if a player was hit. This is their index.
   */
  public int attack(int playerNumber) {
    int player = (playerNumber / 10) - 1;
    //Get this players y and x coordinates.
    int yPos = playerPandD[player][_Y_POS];
    int xPos = playerPandD[player][_X_POS];
    //Get this players y and x direction.
    int yDir = playerPandD[player][_Y_DIR] + 1;
    int xDir = playerPandD[player][_X_DIR] + 1;
    //Determine the right most coordinates of the attack path.
    int rY = attackPath[yDir][xDir][0];
    int rX = attackPath[yDir][xDir][1];
    //Determine the center most coordinates of the attack path.
    int fY = attackPath[yDir][xDir][2];
    int fX = attackPath[yDir][xDir][3];
    //Determine the left most coordinates of the attack path.
    int lY = attackPath[yDir][xDir][4];
    int lX = attackPath[yDir][xDir][5];

    //Get the values of the map at the attack path coordinates.
    int lCell = BATTLE_BOARD[yPos + lY][xPos + lX];
    int fCell = BATTLE_BOARD[yPos + fY][xPos + fX];
    int rCell = BATTLE_BOARD[yPos + rY][xPos + rX];

    if(rCell >= 10 && rCell % 10 == 0) {
      return (rCell / 10) - 1; //Return the index of the player who was hit
    }
    else if(fCell >= 10 && fCell % 10 == 0) {
      return (fCell / 10) - 1; //Return the index of the player who was hit
    }
    else if(lCell >= 10 & lCell % 10 == 0){
      return (BATTLE_BOARD[yPos + lY][xPos + lX] / 10) - 1; //Return the index of the player who was hit
    }
    return -1;
  }
}

