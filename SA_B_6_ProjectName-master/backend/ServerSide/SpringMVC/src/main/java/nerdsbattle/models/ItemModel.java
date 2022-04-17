package nerdsbattle.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

/**
 * A child of the DatabaseHandler designed specifically to 
 * interact with the database only for tasks related to
 * items.
 * @author Sammy
 *
 */
public class ItemModel extends MyModel implements RequestRouting{
	//String array to hold the names of the columns in the Item table
	private static String[] iCols;
	
	/**
	 * Create a new ItemHandler object. Calls the
	 * super constructor, then sets the value of
	 * table to "Item" and the primary key to 
	 * "Title". Then it retrieves the names of 
	 * the columns in 'Item'.
	 */
	public ItemModel() {
		super();
		table = "Items";
		primaryKey = "Title";
		if(iCols == null) iCols = retrieveColumns();
	}
	

	
	@Override
	public String getColumnsFromEntryWhere(HashMap<String, String> data){
		String res = "";
		connect();
		try {
			Statement s = con1.createStatement();
			ResultSet rs;
			
			String where   = data.get("Where");
			String columns = data.get("Columns");

			//Select the specified data
			String query = "SELECT ";
		
			query += (columns.isEmpty() ? "*" : columns); 
			
			//Indicate the table from which the data should be selected
			query = query + " FROM " + table;

			//If a where clause is specified, return values for a specific entry. Else, return all values for all entries
			if(!where.isEmpty()){
				query += " WHERE ";
				query += where;
			}
			
			query += " ORDER BY Value";
			
			System.out.println(query);
			connect();
			
			rs = s.executeQuery(query);

			res = buildDataResponse(rs);

			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		disconnect();
		return res;	
	}
	
	/**
	 * Get the value in a specified column from the database for a 
	 * specified item.
	 * @param item
	 * The title of the item to retrieve a value from.
	 * @param column
	 * The column to retrieve from the database for the specified item.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 * @return
	 * An Object containing the values stored in that column for the
	 * specified item
	 */
	public Object getValueFromItem(String item, String column, boolean connect) {
	  if(connect) connect();
	  Object o = null;
	  String query = "SELECT " + column + " FROM Items WHERE Title = '" + item + "'";
	  try {
      ResultSet rs = con1.createStatement().executeQuery(query);
      if(rs.next()) {
        o = rs.getObject(column);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
	  
	  if(connect) disconnect();
	  return o;
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
		return values.containsKey("Title") && values.containsKey("Type") && values.containsKey("Attributes") && values.containsKey("PhotoID") && values.containsKey("Value");
	}
	
	@Override
	public String[] getColumns(){
		return iCols;
	}
	
	@Override
	public JSONObject generateJsonObject(ResultSet rs) {
		JSONObject result = new JSONObject();
		try {
				//Title (String)
				//Type (String)
				//Attributes (JSON Object)
				//PhotoID (int)
				//Value (int)
				//Description (String)
			
				result.put("Title", rs.getString("Title"));
				result.put("Type", rs.getString("Type"));
				result.put("Attributes", rs.getString("Attributes") != null ? new JSONObject(rs.getString("Attributes")) : new JSONObject());
				result.put("PhotoID", rs.getInt("PhotoID"));
				result.put("Value", rs.getInt("Value"));
				result.put("Description", rs.getString("Description"));
			
		} catch (SQLException e) { 
			e.printStackTrace();
		}
		return result;
	}
	
}
