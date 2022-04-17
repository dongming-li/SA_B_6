package com.cs309.nerdsbattle.nerds_battle.parsers;


import com.cs309.nerdsbattle.nerds_battle.map.Obstacle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Parser for parsing Obstacles from server responses.
 * @author Sammy Sherman
 */
public class ObstacleParser {
    private ResponseParser rp;

    /**
     * Construct a new Obstacle Parser object.
     */
    public ObstacleParser(){
        rp = new ResponseParser();
    }

    /**
     * Get only one obstacle from the response.
     * @param toParse
     * The String to parse
     * @return
     * The first Obstacle parsed from the toParse string
     */
    public Obstacle parseObstacle(String toParse){
        return parseObstacles(toParse).get(0);
    }

    /**
     * Get all the obstacles from the response.
     * @param toParse
     * The String to parse.
     * @return
     * All the Obstacles parsed from the toParse string.
     */
    public ArrayList<Obstacle> parseObstacles(String toParse){

        ArrayList<JSONObject> parsed = rp.parseResponse(toParse);
        ArrayList<Obstacle> obstacles = new ArrayList<>(parsed.size());

        for(int i=0; i<parsed.size(); i++){
            JSONObject map = parsed.get(i);

            try {
                String title   = map.getString("Title");
                String type    = map.getString("Type");
                int photoID    = map.getInt("PhotoID");
                String desc    = map.getString("Description");
                double length = map.getDouble("Length");
                double width = map.getDouble("Width");
                int height = map.getInt("Height");

                obstacles.add(new Obstacle(title,type,desc,photoID,length,width,height));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return obstacles;
    }
}
