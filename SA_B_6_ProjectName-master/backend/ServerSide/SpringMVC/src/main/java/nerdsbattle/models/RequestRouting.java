package nerdsbattle.models;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

public interface RequestRouting {

  /**
   * Extract the parameter names and values from the
   * request and convert it to a HashMap to be used
   * in a select query.
   * @param req
   * The HttpServletRequest that sent the request.
   * @return
   * A HashMap in which each key is a column to retrieve and
   * if a key has a value, that is to be used as a where clause
   * condition.
   */
	public HashMap<String, String> processSelectRequest(HttpServletRequest req);
	
	/**
	 * Extract the parameter names and values from the
	 * request and convert it to a HashMap to be used 
	 * in an update query.
	 * @param req
	 * The HttpServletRequest that send the request.
	 * @return
	 * A HashMap in which each key is a column and the value
	 * associated with it is to be inserted/updated into/at that
	 *  column in the specified table in the database.
	 */
	public HashMap<String, String> processUpdateRequest(HttpServletRequest req);
	
	/**
	 * Determine if the provided HashMap of values to 
	 * insert into a specified table is a valid entry.
	 * @param values
	 * A HashMap in which each key is a column and each 
	 * value is to be inserted into the associated column
	 * in the specified table in the database.
	 * @return
	 * True if the entry is valid. False otherwise.
	 */
	public boolean isValidEntry(HashMap<String, String> values);
}
