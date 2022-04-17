package nerdsbattle.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

/**
 * A child of the DatabaseHandler designed specifically to 
 * interact with the database only for tasks related to
 * Obstacles.
 * @author Sammy
 *
 */
public class ObstacleModel extends MyModel{
	//String array to store the names of the columns in the Obstacle table
	private static String[] oCols;

	/**
	 * Create a new ObstacleHandler object. Calls the
	 * super constructor, sets the table value to 
	 * 'Obstacle' and the primaryKey to 'Title'.
	 * Retrieves the names of the columns in the
	 * 'Obstacle' table.
	 */
	public ObstacleModel() {
		super();
		table = "Obstacles";
		primaryKey = "Title";
		if(oCols == null) oCols = retrieveColumns();
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
		return values.containsKey("Title") && values.containsKey("Type") && values.containsKey("PhotoID") && values.containsKey("Height") && values.containsKey("Width") && values.containsKey("Depth"); 	
	}
	
	@Override
	public String[] getColumns(){
		return oCols;
	}
	
	
	@Override
	public JSONObject generateJsonObject(ResultSet rs) {
		JSONObject result = new JSONObject();
		try {
				//Title (String)
				//Type (String)
				//Description (String)
				//PhotoID (int)
				//Height (int)
				//Width  (int)
				//Length (int)

			
				result.put("Title", rs.getString("Title"));
				result.put("Type", rs.getString("Type"));
				result.put("Description", rs.getString("Description"));
				result.put("PhotoID", rs.getInt("PhotoID"));
				result.put("Height", rs.getInt("Height"));
				result.put("Width", rs.getDouble("Width"));
				result.put("Length", rs.getDouble("Length"));
				

			
		} catch (SQLException e) { 
			e.printStackTrace();
		}
		return result;
	}
	
	
}
