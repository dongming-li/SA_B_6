package com.cs309.nerdsbattle.nerds_battle.parsers;


import com.cs309.nerdsbattle.nerds_battle.shop.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * ItemParser parses the Http JSON Response from the server when fetching items.
 *
 * @author Sammy Sherman, Matthew Kelly
 */
public class ItemParser {

    private ResponseParser rp;

    /**
     * Creates a new ItemParser and and response parser.
     */
	public ItemParser(){
		rp = new ResponseParser();
	}
	
    /**
     * Parses string for only one item if expected or desired.
     *
     * @param toParse
     *   String that contains a Item in JSON Format.
     * @return
     *   Item in the 0th spot of the input string.
     *
     */
	public Item parseItem(String toParse){
		return parseItems(toParse).get(0);
	}

    /**
     * Parses string for multiple items.
     *
     * @param toParse
     *   String that contains Items in JSON Format.
     * @return
     *   Items contained in input string.
     *
     */	@SuppressWarnings("unchecked")
	public ArrayList<Item> parseItems(String toParse){

		ArrayList<JSONObject> parsed = rp.parseResponse(toParse);
		ArrayList<Item> items = new ArrayList<>(parsed.size());

        for(int i=0; i<parsed.size(); i++){
			JSONObject map = parsed.get(i);

			try {
			    //Get values from the JSON objects, all valid items contain all the below fields.
				String title   = map.getString("Title");
				String type    = map.getString("Type");
				int photoID    = map.getInt("PhotoID");
				int value      = map.getInt("Value");
				String desc    = map.getString("Description");
				JSONObject attribs = map.getJSONObject("Attributes");

				//Create items based on the type.
				switch(type){
					case "Melee": items.add(new MeleeItem(title, type, photoID, value, desc, attribs, true)); break;
					case "Range": items.add(new RangeItem(title, type, photoID, value, desc, attribs, true)); break;
					case "HairStyle": items.add(new HairStylesItem(title, type, photoID, value, desc, attribs, true)); break;
					case "Glasses": items.add(new GlassesItem(title, type, photoID, value, desc, attribs, true)); break;
					case "Clothing": items.add(new ClothingItem(title, type, photoID, value, desc, attribs, true)); break;
					default: items.add(new Item(title, type, photoID, value, desc, true));  //Unexpected type, just default to a normal item (or maybe not make item idk)
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return items;
	}
	
	
}
