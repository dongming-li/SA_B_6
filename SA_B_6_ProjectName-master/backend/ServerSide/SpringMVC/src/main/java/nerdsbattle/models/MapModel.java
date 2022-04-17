package nerdsbattle.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A child of the DatabaseHandler designed specifically to 
 * interact with the database only for tasks related to
 * Maps.
 * @author Sammy
 *
 */
public class MapModel extends MyModel{
	//A string array of the names of the columns in the Map table
	private static String[] mpCols;
	
	/**
	 * Create a new MapHandler object. Calls the super
	 * constructor, sets table to 'Map' and the 
	 * primaryKey to 'Title'. Then retrieves the
	 * names of the columns in the 'Map' table.
	 */
	public MapModel() {
		super();
		table = "Maps";
		primaryKey = "Title";
		if(mpCols == null) mpCols = retrieveColumns();
	}
	
	/**
	 * This a method used to return necessary info about a Map and its
	 * Obstacles to the client. Given a maptitle, this method will get
	 * that Map's backgroundID and Obstacles. Then, for each obstacle,
	 * it will query for that obstacles PhotoID, Length, and Width, 
	 * package those values up with its position attributes as a JSONObject
	 * and add it to a JSONArray to represent that Map's obstacles.
	 * @param maptitle
	 * The title of the map to get the data for
	 * @return
	 * A String representation of a JSONObject that represents the specified
	 * Map with the extra information about the obstacles.
	 */
	public String getMapWithObstacles(String maptitle) {
		String res = "{}";
		//Get the BackgroundID and Obstacles for the specified map
		String query = "SELECT BackgroundID, Obstacles FROM " + table + " WHERE Title='" + maptitle + "'";
		//Connect to the DB
		connect();
		try {
			Statement s = con1.createStatement();
			ResultSet rs = s.executeQuery(query);
			if(rs.next()) {
				JSONObject result = new JSONObject();
				
				//Obstacles is stored as a String representation of a JSONArray, so use that string to create a JSONArray
				JSONArray curArr = new JSONArray(rs.getString("Obstacles"));
				//Create a new JSONArray to store our new objects with their extra data
				JSONArray newArr = new JSONArray();
				//Get the backgroundID
				int backId = rs.getInt("BackgroundID");
				
				for(int i=0; i < curArr.length(); i++) {
					//For each element in the current JSONArray, make a new JSONObject
					JSONObject newObs = new JSONObject();
					
					//Retrieve the current JSONObject in the current JSONArray
					JSONObject curObs = curArr.getJSONObject(i);
					String obTitle = curObs.getString("Title");
					//Get the position values of the current object
					JSONObject pos = curObs.getJSONObject("Position");
					
					//Get the PhotoID, Length and Width for the specified obstacle.
					query = "SELECT PhotoID, Length, Width FROM Obstacles WHERE Title='" + obTitle + "'";
					
					ResultSet rs2 = s.executeQuery(query);
					if(rs2.next()) {
						//Add the necessary information to our new JSONObject
						newObs.put("Title", obTitle);
						newObs.put("Length", rs2.getDouble("Length"));
						newObs.put("Width", rs2.getDouble("Width"));
						newObs.put("PhotoID", rs2.getInt("PhotoID")); 
						newObs.put("x", pos.getDouble("x"));
						newObs.put("y", pos.getDouble("y"));
						newObs.put("r", pos.getDouble("r"));
					}
					//Add our new JSONObject to our new JSONArray
					newArr.put(newObs);
				}
				//Fill the result object with the necessary data
				result.put("Title", maptitle);
				result.put("BackgroundID", backId);
				//Place in our newly created JSONArray with the proper extra data.
				result.put("Obstacles", newArr);
				res = result.toString();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//Close connection
		disconnect();
	
		return res;
	}
	
	public JSONArray retrieveRandomMaps(int numMaps, boolean connect){
	  JSONArray maps = new JSONArray();
	  if(connect) connect();
	  
	  String query = "SELECT Title, BackgroundID FROM Maps";
	  ArrayList<Integer> indeces = new ArrayList<Integer>();
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      ArrayList<JSONObject> results = new ArrayList<JSONObject>();
      while(rs.next()) {
        JSONObject map = new JSONObject();
        map.put("Title", rs.getString("Title"));
        //map.put("BackgroundID", rs.getInt("BackgroundID"));
        indeces.add(results.size());
        results.add(map);
      }
      while(maps.length() < numMaps && maps.length() < results.size()) {
        int index = new Random().nextInt(indeces.size());
        maps.put(results.get(indeces.get(index)));
        indeces.remove(index);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  if(connect) disconnect();
	  return maps;
	}
	
	public HashMap<String, String> processInsertRequest(HttpServletRequest req){
	  HashMap<String, String> values = super.processUpdateRequest(req);
	  
	  JSONArray given = new JSONArray(values.get("Obstacles"));
	  JSONArray obstacles = new JSONArray();
	  
	  
	  for(int i=0; i < given.length(); i++) {
	    JSONObject obstacle = new JSONObject();
	    
	    JSONObject cur = given.getJSONObject(i);
	    String title = cur.getString("Title");
	    JSONObject pos = cur.getJSONObject("Position");
	    
	    JSONObject position = new JSONObject();
	    
	    position.put("x", Double.valueOf(pos.getString("x")));
	    position.put("y", Double.valueOf(pos.getString("y")));
	    position.put("r", Double.valueOf(pos.getString("r")));
	    
	    obstacle.put("Title", title);
	    obstacle.put("Position", position);
	    
	    obstacles.put(obstacle);
	  }
	  values.put("Obstacles", obstacles.toString());
	
	  return values;
	}
	
	public boolean mapExists(String maptitle, boolean connect) {
	  if(connect) connect();
	  boolean exists = false;
	  
	  String query = "SELECT Title FROM Maps WHERE Title = '" + maptitle + "'";
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      exists = rs.next();
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  
	  if(connect) disconnect();
	  return exists;
	}

	@Override
	public HashMap<String, String> processSelectRequest(HttpServletRequest req) {
		return super.processSelectRequest(req);
	}

	@Override
	public HashMap<String, String> processUpdateRequest(HttpServletRequest req) {
		return super.processUpdateRequest(req);
	}

	@Override
	public boolean isValidEntry(HashMap<String, String> values) {
		return values.containsKey("Title") && values.containsKey("Creator") && values.containsKey("BackgroundID"); 
	}
	
	@Override
	public String[] getColumns(){
		return mpCols;
	}
	
	@Override
	public JSONObject generateJsonObject(ResultSet rs) {
		JSONObject result = new JSONObject();
		try {
				//Title (String)
				//Creator (String)
				//Obstacles (JSON Array of JSON Objects)
				//BackgroundID (int)
			
				result.put("Title", rs.getString("Title"));
				result.put("Creator", rs.getString("Creator"));
				result.put("Obstacles", rs.getString("Obstacles") != null ? new JSONArray(rs.getString("Obstacles")) : new JSONArray());
				result.put("BackgroundID", rs.getInt("BackgroundID"));

			
		} catch (SQLException e) { 
			e.printStackTrace();
		}
		return result;
	}
}
