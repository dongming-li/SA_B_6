package com.cs309.nerdsbattle.nerds_battle.parsers;

import com.cs309.nerdsbattle.nerds_battle.map.GameMap;
import com.cs309.nerdsbattle.nerds_battle.map.Obstacle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sammy on 10/17/2017.
 */

public class MapParser {

    private ResponseParser rp;

    public MapParser(){
        rp = new ResponseParser();
    }

    /**
     * Returns a SINGLE MAP with ALL THE OBSTACLES AND THEIR DATA
     * @param toParse
     * A string representation of a JSONOBJECT from the server
     * @return
     * A SINGLE MAP with ALL THE OBSTACLES AND THEIR DATA
     */
    public GameMap parseMapWithObstacleData(String toParse){
        GameMap res = null;
        try {
            JSONObject in = new JSONObject(toParse);
            String mapTitle = in.getString("Title");
            int backgroundID = in.getInt("BackgroundID");
            JSONArray obs = in.getJSONArray("Obstacles");

            ArrayList<Obstacle> obstacles = new ArrayList<>();

            for (int i = 0; i < obs.length(); i++) {
                JSONObject curObs = obs.getJSONObject(i);
                String obTitle = curObs.getString("Title");

                int photoID = curObs.getInt("PhotoID");
                double width = curObs.getDouble("Width");
                double length = curObs.getDouble("Length");
                double x = curObs.getDouble("x");
                double y = curObs.getDouble("y");
                double r = curObs.getDouble("r");
                obstacles.add(new Obstacle(obTitle, photoID, width, length, x, y, r));
            }

            res = new GameMap(mapTitle, backgroundID, obstacles);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Returns MULTIPLE MAPS without all the OBSTACLE DATA
     * @param toParse
     * A string representation of a JSONARRAY from the server
     * @return
     * An arraylist of MULTIPLE MAPS without detailed information about the obstacles
     */
    public ArrayList<GameMap> parseAllMapsWithoutObstacleData(String toParse){

        ArrayList<JSONObject> parsed = rp.parseResponse(toParse);
        ArrayList<GameMap> maps = new ArrayList<>();

        for (int j=0; j<parsed.size(); j++) {
            JSONObject job = parsed.get(j);

            try {
                String mapTitle = job.getString("Title");
                int backgroundID = job.getInt("BackgroundID");
                JSONArray obs = job.getJSONArray("Obstacles");

                ArrayList<Obstacle> obstacles = new ArrayList<>();

                for (int i = 0; i < obs.length(); i++) {
                    JSONObject curObs = obs.getJSONObject(i);
                    String obTitle = curObs.getString("Title");
                    //Each obstacle in the map table has a position attribute, so get those values
                    JSONObject pos = curObs.getJSONObject("Position");
                    double x = pos.getDouble("x");
                    double y = pos.getDouble("y");
                    double r = pos.getDouble("r");
                    //Add a minified obstacle
                    obstacles.add(new Obstacle(obTitle, x, y, r));
                }

                GameMap aMap = new GameMap(mapTitle, backgroundID, obstacles);
                maps.add(aMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return maps;
    }
}
