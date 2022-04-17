package nerdsbattle.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.HashMap; 

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Super class that allows for easy connectivity to
 * the DB.
 * @author Sammy
 *
 */
abstract class MyModel implements RequestRouting{

		
	//The URL for the database and the credentials for the database
	private final String dbUrl    = "jdbc:mysql://mysql.cs.iastate.edu:3306/db309sab6?useSSL=false";
	private final String user     = "dbu309sab6";
	private final String password = "GaaSFT05";
	
	//Each handler that extends this class will fill in their own values for the following 3 variables
	protected String table;
	protected String primaryKey;

	//The connection to the database
	protected Connection con1;


	/**
	 * Make a connection to the database, specified in the URL
	 * using the specified username and password.
	 */
	public void connect() {
		if(con1 == null) {
		// Load and register a JDBC driver
			try {
				// Load the driver (registers itself)
				Class.forName("com.mysql.jdbc.Driver");
			} 
			catch (Exception E) {
				System.err.println("Unable to load driver.");
				E.printStackTrace();
			}
			
			try {
				//Attempt to make the connection to the database using the provided URL and credentials
				con1 = DriverManager.getConnection(dbUrl, user, password);
				//Display in console indicating a successful connection to the database
				System.out.println("*** Connected to the database ***");
				
			} catch (SQLException e) {
				e.printStackTrace();
			}	
		}
	}
	
	/**
	 * Close the connection to the database.
	 */
	public void disconnect() {
		if(con1 != null) {
			try {
				con1.close();
			} catch (SQLException e) { 
				e.printStackTrace();
			}
			con1 = null;
		}
	}

	/**
	 * Retrieve the names of the columns for a specified table.
	 * Gets all entries for a specific table, then uses the 
	 * ResultSetMetaData to get the names of the columns and
	 * store them in a String array.
	 * @return
	 * A String[] containing the names of the columns in this table
	 */
	protected String[] retrieveColumns() {
		connect();
		String[] res = null;
		try {
			Statement s = con1.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM " + table);
			
			ResultSetMetaData rsmd = rs.getMetaData();
			res = new String[rsmd.getColumnCount()];
			for(int i=1; i<=rsmd.getColumnCount(); i++) {
				res[i-1] = rsmd.getColumnName(i);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconnect();
		return res;
	}
	
	/**
	 * Returns the columns for the specified table.
	 * This is overridden in child classes
	 * @return
	 * The String array representing all the names of
	 * the columns in this table
	 */
	public String[] getColumns(){
		return null;
	}
	
	/**
	 * Retrieve the specific values that meet the conditions specified 
	 * by the keys and values of the provided hashmap.
	 * @param data
	 * HashMap used to determine the query to the database. The keys 
	 * are columns and the values are used to specify where conditions.
	 * @return
	 * A String representation of a JSONArray containing the results
	 * of the query.
	 */
	public String getColumnsFromEntryWhere(HashMap<String, String> data){
		String res = "";
		connect();
		try {
			Statement s = con1.createStatement();

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
			
			System.out.println(query);
	    ResultSet rs = s.executeQuery(query);
			res = buildDataResponse(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}	
		disconnect();
		return res;	
	}
	
	/**
	 * Inserts a new entry into the specified table
	 * using the provided hashmap's data.
	 * @param values
	 * A hashmap of values to be inserted in which the key is the column
	 * and the value is the value to insert into that column.
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 */
	public void insertEntry(HashMap<String, String> values, boolean connect){
		if(connect) connect();
		try {
			Statement s = con1.createStatement();
			
			String cols = "";
			String vals = "";
		
			for(String title : values.keySet()) {
				if(!cols.isEmpty()) cols += ",";
				if(!vals.isEmpty()) vals += ",";
				cols += title;
				vals += "'" + values.get(title) + "'";
			}
			String query = "INSERT INTO " + table + " (" + cols + ") VALUES (" + vals + ")";
			System.out.println(query);
			s.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(connect) disconnect();
	}
	
	/**
	 * @param key
	 * The key of the entry to be deleted
	 * @param connect
	 * Boolean to indicate whether this method needs to connect to the
   * database or not (i.e called from within a method that is already connected).
	 */
	public void deleteEntry(String key, boolean connect){
		if(connect) connect();
		try {
			Statement s = con1.createStatement();
			connect();
			s.execute("DELETE FROM " + table + " WHERE " + primaryKey + " = '" + key + "'");
			System.out.println("Data in " + table + " containing the key: '" + key +"' successfully deleted");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(connect) disconnect();
	}
	
	/**
	 *@param data
	 *A HashMap of values to use for updating an entry in the table. 
	 *The key of each entry in the HashMap is the column to update and the value
	 *is the value to be updated with in that column. 
	 */
	public void updateEntry(HashMap<String, String> data){
		connect();
		try {
			Statement s = con1.createStatement();
			
			String key    = data.get(primaryKey);
			String query  = "UPDATE " + table + " SET ";
			String values = "";
			
			for(String title : data.keySet()) {
				if(!title.equals(primaryKey)) {
					if(!values.isEmpty()) values += ", ";
					values += title + " ='" + data.get(title) + "'";
				}
			}
			 
			query += values;
			query += " WHERE " + primaryKey + " = '" + key + "'";
			connect();
			s.execute(query);
			System.out.println(query);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		disconnect();
	}

	@Override
	public HashMap<String, String> processSelectRequest(HttpServletRequest req) {
		@SuppressWarnings("unchecked")
		Enumeration<String> attributes = req.getParameterNames();
		 
		String columns = "";
		String where   = ""; 
		while(attributes.hasMoreElements()) {
			String attrib = attributes.nextElement();
			if(req.getParameter(attrib).isEmpty()) {
				if(!columns.isEmpty()) columns += ", ";
				columns += attrib;
			}
			else{
				if(!where.isEmpty()) where +=  " AND ";
				if(req.getParameter(attrib).contains("---")) {
					String[] split = req.getParameter(attrib).split("---");
					for(int i=0; i<split.length; i++) {
						if(i > 0) where += " OR ";
						where += attrib + " = '"+ split[i] + "'";
					}
				}
				else {
					where += attrib + " = '"+ req.getParameter(attrib) + "'";
				}
			}
		}
		HashMap<String, String> queryData = new HashMap<String, String>();
		queryData.put("Columns", columns);
		queryData.put("Where", where); 
		
		System.out.printf("Columns: %s     Where: %s\n", columns, where);
		
		return queryData;
	}

	@Override
	public HashMap<String, String> processUpdateRequest(HttpServletRequest req) {
		HashMap<String, String> res = new HashMap<String, String>();
		
		@SuppressWarnings("unchecked")
		Enumeration<String> attributes = req.getParameterNames();
		 
		while(attributes.hasMoreElements()) {
			String attrib = attributes.nextElement();
			res.put(attrib, req.getParameter(attrib));
		}
		
		return res;
	}

	@Override
	public boolean isValidEntry(HashMap<String, String> values) { 
		return false;
	}
	
	/**
	 * Generate a JSONObject using a ResultSet. More
	 * specifically, this only uses a single row of 
	 * the ResultSet. The child classes override this
	 * method to allow the JSONObject to store the
	 * proper types of entries.
	 * @param rs
	 * The ResultSet that contains the row to convert
	 * to a JSONObject
	 * @return
	 * A JSONObject that contains the values from the
	 * current row of the ResultSet
	 */
	public JSONObject generateJsonObject(ResultSet rs) {
		//Overridden in child classes
		return new JSONObject();
	}
	

	/**
	 * Build a String response that represents a JSONArray
	 * of all the entities, represented as JSONObjects,
	 * returned in the ResultSet
	 * @param rs
	 * The ResultSet that contains the results to convert
	 * to a JSONArray of JSONObjects
	 * @return
	 * A String representation of a JSONArray of JSONObjects
	 */
	public String buildDataResponse(ResultSet rs) {
		JSONArray res = new JSONArray();
		try {
			while(true){
				if(!rs.next()) break;
				res.put(generateJsonObject(rs));
				
			}
		} catch (SQLException e) { 
				e.printStackTrace();
		}
		
		return res.toString();
	}



}
