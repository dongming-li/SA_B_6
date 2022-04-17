package com.cs309.nerdsbattle.nerds_battle.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * ResponseParser does initial parsing of data from the server.
 *
 * @author Sammy Sherman
 */
public class ResponseParser {

	/**
	 * Parse a provided string. The string should be a
	 * JSONArray of JSONObjects.
	 * @param response
	 * The string to parse. Should be a JSONArray of
	 * JSONObjects
	 * @return
	 * An ArrayList of JSONObjects, parsed from the
	 * provided string.
	 */
	public ArrayList<JSONObject> parseResponse(String response){
		ArrayList<JSONObject> objects = new ArrayList<>();
		try {
			JSONArray jsonArray = new JSONArray(response);
			for(int i=0; i<jsonArray.length(); i++) {
				objects.add(jsonArray.getJSONObject(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return objects;
	}
}
