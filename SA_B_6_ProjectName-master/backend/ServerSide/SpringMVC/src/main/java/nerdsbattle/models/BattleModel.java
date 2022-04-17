package nerdsbattle.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import nerdsbattle.BattleMap;
import nerdsbattle.EquippedItem;
import nerdsbattle.EquippedType;

/**
 * A child of the DatabaseHandler designed specifically to 
 * interact with the database only for tasks related to
 * battle.
 * @author Sammy
 *
 */
public class BattleModel extends MyModel  implements RequestRouting{
	
	//String array of the names of the columns in the Battle table
	private static String[] bCols;
	
	/**
	 * Construct a new BattleHandler.
	 * This calls the super constructor and
	 * assigns the table value to "Battle" and
	 * the primary key to "BattleID", then it
	 * retrieves the names of the columns
	 */
	public BattleModel() {
		super();
		table = "Battles";
		primaryKey = "BattleID";
		if(bCols == null) bCols = retrieveColumns();
		
	}
	
	/**
	 * Retrieve specific data about the equipped items
	 * for a specified user
	 * @param username
	 * The username for the player to retieve the equipped
	 * items for
	 * @return
	 * An array of EquippedItem's. There is a slot for Range, Melee,
	 * Clothing, Glasses, and HairStyle. If that player does not have
	 * an item for a slot, that slot is NULL
	 */
	public EquippedItem[] retrieveEquippedItemData(String username) {
	
		EquippedItem[] res = new EquippedItem[EquippedType.values().length];
		//Connect to DB
		connect();
		
		try {
			//Get the 'Equipped' column value for the specified player
			String query = "SELECT Ranged, Melee, HairStyle, Glasses, Clothing FROM Equipped WHERE User ='" + username + "'";
			ResultSet rs1 = con1.createStatement().executeQuery(query);
			
			if(rs1.next()) {
				  
			  HashMap<String, String> equipped = new HashMap<String, String>();
			  
			  equipped.put("Range",  rs1.getString("Ranged"));
			  equipped.put("Melee",  rs1.getString("Melee"));
			  equipped.put("Clothing",  rs1.getString("Clothing"));
			  equipped.put("HairStyle",  rs1.getString("HairStyle"));
			  equipped.put("Glasses",  rs1.getString("Glasses"));
			  
			  
			  for(String slot : equipped.keySet()) {
			    if(equipped.get(slot) != null) {
			      String query2 = "SELECT Attributes FROM Items WHERE Title = '" + equipped.get(slot) + "'";
			      ResultSet rs2 = con1.createStatement().executeQuery(query2);
			      
			      if(rs2.next()) {
			        int slotindex = getEquippedSlot(slot);
			        String attrs = rs2.getString("Attributes");
			        res[slotindex] = new EquippedItem(attrs);
			      }
			      
			    }
			  }
			}
			
		} catch (SQLException e1) { 
			e1.printStackTrace();
		}
		//Close connection to DB
		disconnect();
		return res;
	}
	
	private int getEquippedSlot(String type) {
    switch(type) {
      case "Range"    : return EquippedType.RANGE.ordinal();
      case "Melee"    : return EquippedType.MELEE.ordinal();
      case "Clothing" : return EquippedType.CLOTHING.ordinal();
      case "Glasses"  : return EquippedType.GLASSES.ordinal();
      case "HairStyle": return EquippedType.HAIRSTYLE.ordinal();
    }
    return -1;
	}
	
	/**
	 * Retrieve data for a map and the specific data for the 
	 * obstacles that are a part of that map, using only the
	 * map name provided.
	 * @param mapname
	 * The name of the map to retrieve values for.
	 * @return
	 * A new BattleMap object that has a 2 dimensional int array
	 * that is used as a 'Game Board' in which the obstacles are
	 * placed in their proper locations using the dimensions and
	 * position attributes.
	 */
	public BattleMap getBattleMapWithObstacles(String mapname) {
		String res = "[]";
		//Get the obstacles for the specified map
		String query = "SELECT Obstacles FROM Maps WHERE Title='" + mapname + "'";
		//Open connection to DB
		connect();
		try {
			Statement s = con1.createStatement();
			ResultSet rs = s.executeQuery(query);
			if(rs.next()) {  
				//Obstacles is a string representation of a JSONArray, so use the string to recreate that JSONArray
				JSONArray curArr = new JSONArray(rs.getString("Obstacles"));
				//The new array we will be filling up with extra data
				JSONArray newArr = new JSONArray();
				
				for(int i=0; i < curArr.length(); i++) {
					//The new object to represent an obstacle
					JSONObject newObs = new JSONObject();
					//The current obstacle in the curArr
					JSONObject curObs = curArr.getJSONObject(i);
							
					String obTitle = curObs.getString("Title");
					JSONObject pos = curObs.getJSONObject("Position");
					
					//Retrieve the Height, Length, and Width for the specified obstacle
					query = "SELECT Height, Length, Width FROM Obstacles WHERE Title='" + obTitle + "'";
					
					ResultSet rs2 = s.executeQuery(query);
					if(rs2.next()) {
						//Add the values to the new JSONObject
						newObs.put("Length", rs2.getDouble("Length"));
						newObs.put("Width", rs2.getDouble("Width"));
						newObs.put("Height", rs2.getInt("Height")); 
						newObs.put("x", pos.getDouble("x"));
						newObs.put("y", pos.getDouble("y"));
						newObs.put("r", pos.getDouble("r"));
					}
					//Add the new JSONObject to the new JSONArray
					newArr.put(newObs);
				}
				//Set the result to the string version of the new JSONArray
				res = newArr.toString();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//Close connection to DB
		disconnect();
		//Return a new BattleMap, constructed using our String representation of our new JSONArray
		return new BattleMap(res);
	}
	
	@Override
	public HashMap<String, String> processSelectRequest(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, String> processUpdateRequest(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidEntry(HashMap<String, String> values) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public JSONObject generateJsonObject(ResultSet rs) {
		JSONObject result = new JSONObject();
		try {
				//BattleID (int)
				//Player1 (String)
				//Player2 (String)
				//Map (String)
				//Winner (String)
				//Duration (Time?) --> How long the battle took to complete
				//DateTime (DateTime?) --> When the battle was initiated

			
				result.put("BattleID", rs.getInt("BattleID"));
				result.put("Player1", rs.getString("Player1"));
				result.put("Player2", rs.getString("Player2"));
				result.put("Winner", rs.getString("Winner"));
				result.put("Map", rs.getString("Map"));
				result.put("Duration", rs.getString("Duration"));
				result.put("DateTime", rs.getString("DateTime"));
				

			
		} catch (SQLException e) { 
			e.printStackTrace();
		}
		return result;
	}
	
	
}
