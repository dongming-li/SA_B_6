package com.cs309.nerdsbattle.nerds_battle.parsers;

import android.util.Log;

import com.cs309.nerdsbattle.nerds_battle.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * PlayerParser parses the Http JSON Response from the server when fetching players.
 *
 * @author Sammy Sherman
 */
public class PlayerParser {
	private ResponseParser rp;

	/**
	 * Creates a new PlayerParser and and response parser.
	 */
	public PlayerParser(){
		rp = new ResponseParser();
	}

	/**
	 * Parses string for only one Player if expected or desired.
	 *
	 * @param toParse
	 *   String that contains a Player in JSON Format.
	 * @return
	 *   Player in the 0th spot of the input string.
	 *
	 */	public Player parsePlayer(String toParse){
		return parsePlayers(toParse).get(0);
	}

	/**
	 * Parses string for multiple players.
	 *
	 * @param toParse
	 *   String that contains Players in JSON Format.
	 * @return
	 *   Players contained in input string.
	 *
	 */	@SuppressWarnings("unchecked")
	public ArrayList<Player> parsePlayers(String toParse){
		ArrayList<JSONObject> parsed = rp.parseResponse(toParse);
		ArrayList<Player> players = new ArrayList<>();
		Log.v("Parsed Size", "" + parsed.size());
		for(int i=0; i < parsed.size(); i++){
			JSONObject map = parsed.get(i);

			try {
				//Get all values for a player, all valid players contain the below fields.
				String username = map.getString("Username");
				String password = map.getString("Password");
				String GPA      = map.getString("GPA");
				int XP       = map.getInt("XP");
				int money	 = map.getInt("Money");
				int accuracy = map.getInt("Accuracy");
				int defense  = map.getInt("Defense");
				int speed    = map.getInt("Speed");
				int strength = map.getInt("Strength");

				JSONArray fr = map.getJSONArray("Friends");
				JSONArray it = map.getJSONArray("Items");
				JSONObject eq = map.getJSONObject("Equipped");

				players.add(new Player(username, password, GPA, XP, money, accuracy, defense, speed, strength, fr, it, eq, true));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return players;
	}
	
}
