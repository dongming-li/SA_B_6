package com.cs309.nerdsbattle.nerds_battle.server_inter;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cs309.nerdsbattle.nerds_battle.map.Obstacle;
import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.chat.Message;
import com.cs309.nerdsbattle.nerds_battle.parsers.ItemParser;
import com.cs309.nerdsbattle.nerds_battle.parsers.MapParser;
import com.cs309.nerdsbattle.nerds_battle.parsers.ObstacleParser;
import com.cs309.nerdsbattle.nerds_battle.parsers.PlayerParser;
import com.cs309.nerdsbattle.nerds_battle.shop.Item;
import com.cs309.nerdsbattle.nerds_battle.map.GameMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * ServerRequest class holds all the different methods used to communicate with the server and obtain
 * information from the various databases. None of the contained methods should be used on the main thread
 * as they contain blocking operations.
 *
 * @author Matthew Kelly, Chad Riedeman, Yee Chan, Sammy Sherman
 */
public class ServerRequest{

    /**
     * URL of the server.
     */
    public static final String WEBSERVERURL = "http://proj-309-sa-b-6.cs.iastate.edu:8080/SpringMVC/";

    /**
     * Location to be added onto the server URL used by HTTP protocol.
     */
    public static final String MELEE = "GetItems?Type=Melee";
    public static final String RANGE = "GetItems?Type=Range";
    public static final String HAIRSTYLES = "GetItems?Type=HairStyle";
    public static final String GLASSES = "GetItems?Type=Glasses";
    public static final String CLOTHING = "GetItems?Type=Clothing";
    public static final String PLAYER = "GetPlayers?Username=";
    public static final String GETMESSAGES = "GetMessages?";
    public static final String PURCHASEITEM = "PurchaseItem";
    public static final String GETMAPSCREATED = "GetMaps?Creator=";
    public static final String ADDPLAYER = "AddPlayer";
    public static final String UPDATEPLAYER = "UpdatePlayer";
    public static final String SENDMESSAGE = "Chat/Send";
    public static final String GETITEMS = "GetItems";
    public static final String MELEEATTACK = "MeleeAttack";
    public static final String RANGEATTACK = "ThrowItem?Id=";
    public static final String PLAYERMOVE = "Move";
    public static final String PLAYERMOVEDIRECTION = "MoveDirection";
    public static final String RANGEITEMMOVE = "Move";
    public static final String GETMAPANDOBSTACLES = "GetMap?Title=";
    public static final String GETSTATS = "GetStats?Username=";
    public static final String GETOBSTACLES = "GetObstacles";
    public static final String ADDFRIEND = "Friend/Add?";
    public static final String CLOSECHAT = "Chat/Close?Username=";
    public static final String SELECTMAP = "SelectMap";

    /**
     * Tags for the various requests.
     */
    public static final String REQUEST_TAG_MELEE = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getMeleeItems";
    public static final String REQUEST_TAG_RANGE = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getRangeItems";
    public static final String REQUEST_TAG_HAIRSTYLES = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getHairStylesItems";
    public static final String REQUEST_TAG_CLOTHING = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getClothingItems";
    public static final String REQUEST_TAG_GLASSES = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getGlassesItems";
    public static final String REQUEST_TAG_PLAYER = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getPlayer";
    public static final String REQUEST_TAG_GET_MESSAGES = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getMessages";
    public static final String REQUEST_TAG_ADD_PLAYER = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.addPlayer";
    public static final String REQUEST_TAG_UPDATE_PLAYER = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.UpdatePlayer";
    public static final String REQUEST_TAG_SEND_MESSAGE = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.sendMessage";
    public static final String REQUEST_TAG_GET_ITEMLIST = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getItemList";
    public static final String REQUEST_TAG_PURCHASEITEM = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.purchaseItem";
    public static final String REQUEST_TAG_GET_CREATEDMAPLIST = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getCreatedMapList";
    public static final String REQUEST_TAG_MELEE_ATTACK = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.meleeAttack";
    public static final String REQUEST_TAG_RANGE_ATTACK = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.rangeAttack";
    public static final String REQUEST_TAG_PLAYER_MOVE = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.playermove";
    public static final String REQUEST_TAG_PLAYER_MOVE_DIRECTION = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.playermovedirection";
    public static final String REQUEST_TAG_RANGE_ITEM_MOVE = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.rangeitemmove";
    public static final String REQUEST_TAG_GET_MAPS_WITH_OBSTACLES = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getMapsWithObstacles";
    public static final String REQUEST_TAG_GET_STATS = "com.cs309.nerdsbattle.nerds_battle.ServerRequest.getStats";
    public static final String REQUEST_TAG_GET_OBSTACLES = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.getObstacleList";
    public static final String REQUEST_TAG_ADD_FRIEND = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.addFriend";
    public static final String REQUEST_TAG_CLOSE_CHAT = "com.cs309.nerdsbattle.nerds_battle.ServerRequest.closeChat";
    public static final String REQUEST_TAG_SELECT_MAP = "com.cs309.nerdsbattle.nerds_battle.serverInter.ServerRequest.selectMap";
    public static final String REQUEST_TAG_ADD_MAP = "com.cs309.nerdsbattle.nerds_battle.ServerRequest.addNewMap";
    public static final String REQUEST_TAG_EDITMAP = "com.cs309.nerdsbattle.nerds_battle.ServerRequest.UpdateCreatedMap";

    /**
     * Internal variables by the different methods used to wait for a response.
     */
    private Context context;
    private ArrayList<Item> items;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<GameMap> mapsCreated;
    private GameMap mapWithObstacles;
    private Player player;
    private String purchaseItem;
    private int addFriendSuccessful;
    private int doneAdding;

    /**
     * Creates a ServerRequest and sets all variables to null to be used by methods later.
     *
     * @param context
     *   application context.
     */
    public ServerRequest(Context context) {
        this.context = context;
        items = null;
        obstacles = null;
        player = null;
        purchaseItem = null;
    }

    /**
     * Purchases an item from the server.
     *
     * @param request
     *   URL for purchase item (PURCHASEITEM)
     * @param tag
     *   Tag for the request (REQUEST_TAG_PURCHASEITEM)
     * @param map
     *   Item to be purchased inside a hashmap for the post request.
     * @return
     *   Result of the purchase request.
     */
    public String purchaseItem(String request, String tag, final HashMap<String, String> map) {
        purchaseItem = "";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        purchaseItem = response.toString();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Do something when error occurs
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        purchaseItem = "Error";
                    }
                }
        ) {
            //Place the parameters here instead of passing in URL
            @Override
            protected Map<String, String> getParams() {
                //map was preloaded with parameters, return map
                return map;
            }
        };

        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);

        //Wait for a response.
        while (purchaseItem.equals("")) {}
        return purchaseItem;
    }

    /**
     * Gets all items from the server.
     *
     * @param request
     *   URL for purchase item (GETITEMS)
     * @param tag
     *   Tag for the request (REQUEST_TAG_GETITEMS)
     * @return
     *   ArrayList of items contained on the server.
     */
    public ArrayList<Item> getItems(String request, String tag) {
        items = null;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        //Parse the return.
                        ItemParser ip = new ItemParser();
                        items = ip.parseItems(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Do something when error occurs
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        //Return an empty arraylist on failure.
                        items = new ArrayList<>();
                    }
                }
        );

        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);

        while (items == null) {}
        return items;
    }


    /**
     * Obtain player information from username input.
     *
     * @param request
     *   Location plus username
     * @param tag
     *   Tag of the request.
     * @return
     *   player information for the username.
     */
    public Player getPlayer(String request, String tag) {
        player = null;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WEBSERVERURL + request,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("Password")) {
                            PlayerParser playerParser = new PlayerParser();
                            player = playerParser.parsePlayer(response);
                        }
                        else{
                            player = new Player();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        player = new Player();
                    }
                });
        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);

        while(player == null){}
        return player;
    }

    /**
     * Add a new player to database.
     *
     * @param map
     *   HashMap containing player information.
     * @param tag
     *   Tag of the request.
     * @return
     *   the new player
     */
    public Player addPlayer(final Map<String, String> map, String tag){
        player = null;
        String request = ADDPLAYER;
        Log.v("Request out", WEBSERVERURL + request);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + request,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Success")){ player = new Player(map); }
                        //error adding
                        else{ player = new Player(); }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        player = new Player();
                    }
                }
        ){
            //Place the parameters here instead of passing in URL
            @Override
            protected Map<String, String> getParams() {
                //map was preloaded with parameters, return map
                return map;
            }
        };
        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);

        while(player == null){}

        return player;
    }

    /**
     * Method used to update the player in a post request.
     * @param map
     *   HashMap containing all values to updated.
     */
    public void updatePlayer(final Map<String, String> map) {
        String request = map.get("Request");
        String tag = map.get("RequestTag");
        Log.d(TAG, "updatePlayer: " + request);

        map.remove("Request");
        map.remove("RequestTag");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { Log.d(TAG, "onResponse: " + response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { Log.e(TAG, "onErrorResponse: " + error.toString()); }
                }
        ){
            //Place the parameters here instead of passing in URL
            @Override //map was preloaded with parameters, return map
            protected Map<String, String> getParams() { return map; }
        };
        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);
    }


    /**
     * Returns an arraylist of items by giving in a string of item names
     * @param toParse
     *   String of item names
     * @param tag
     *   Tag for the request.
     * @return
     *   ArrayList of items with all the information
     */
    public ArrayList<Item> getItemList(String toParse, String tag){
        // used in CharacterEditActivity
        //Set the items to null for a fresh start
        items = null;
        String request = GETITEMS;
        try {
            JSONArray itemArrList = new JSONArray(toParse);
            request += "?Title=";
            for(int i=0; i<itemArrList.length(); i++) {
                JSONObject item = itemArrList.getJSONObject(i);

                if(i > 0) request += "---";
                request +=  item.getString("Title").replaceAll(" ","%20");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Set this to null instead so we can check easier...
        //itemListcheck = null; //Never used, so why keep?? We already wait for items to not be null...

        StringRequest stringRequest = new StringRequest(Request.Method.GET, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ItemParser itemParser = new ItemParser();
                        //Using the itemparser, parse the items from the response
                        items = itemParser.parseItems(response);
                        //itemListcheck = "Success"; <-- Doesn't effect algorithm...
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());

                        //Error occurred, set items to new arraylist so it can escape the blocking call below
                        items = new ArrayList<>();
                        //itemListcheck = "Fail"; <-- Doesn't effect algorithm...
                    }
                });
        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);


        //Since we used a thread above that has to make a call to the server, which takes time (more time than it takes to get to this point in the code)
        //We have to put a blocking call in here to wait for the thread to finish
        while(items == null){
            //While loop that does nothing while that value is null, it will only NOT be null when the request (thread) completes
        }
        //Now there will be a value in it
        return items;
    }

    /**
     * Returns an arraylist of maps created by a specified user.
     * Each has limited info about its obstacles
     * @param creator
     * The name of the creator
     * @param tag
     * The request tag for this request
     * @return
     * An arraylist of maps created by creator with limited info about its obstacles.
     */
    public ArrayList<GameMap> getCreatedMapList(String creator, String tag){
        mapsCreated = null;
        String request = GETMAPSCREATED + creator;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MapParser mapParser = new MapParser();
                        mapsCreated = mapParser.parseAllMapsWithoutObstacleData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        mapsCreated = new ArrayList<>();
                    }
                });
        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);

        while(mapsCreated == null){
        }

        return mapsCreated;
    }

    /**
     * Send a request to the Server, using Volley, to attempt to add
     * the specified friend to the specified users friends list. This
     * tells the server to prompt the provided friend to approve or
     * deny the request. The approve or deny request goes through the
     * friends open STREAM, if open, or waits for them to open a new
     * one if not open. If approved, then their friendship is added to
     * the database. Otherwise, the request is destroyed and forgetten
     * about.
     * @param username
     * The username for the user to add the friend for.
     * @param friend
     * The username for the friend to add to the users friends.
     * @param tag
     * The request tag for this request.
     */
    public void addFriend(String username, String friend, String tag) {

        String request = ADDFRIEND + "Username=" + username + "&Friend=" + friend;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "onSuccessfulResponse: " + response);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                    }
                });
        //Since the response will go through the stream, this doesnt need to return anything.
    }

    /**
     * Get a list of ALL the obstacles stored in the
     * database.
     * @param tag
     * The request tag for this request.
     * @return
     * An ArrayList of Obstacles, each of which is a
     * unique obstacle stored in the database.
     */
    public ArrayList<Obstacle> getObstacleList(String tag) {
        obstacles = null;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, WEBSERVERURL + GETOBSTACLES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ObstacleParser obstacleParser = new ObstacleParser();
                        obstacles = obstacleParser.parseObstacles(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());

                        obstacles = new ArrayList<>();
                    }
                });
        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);

        while(obstacles==null) {}

        return obstacles;
    }

    /**
     * Request used for melee and range attacks during a battle.
     *
     * @param request
     *   Url with parameters for melee or ranged attack.
     * @param tag
     *   Tag of the request.
     */
    public void attack(String request, String tag) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                    }
                }
        );

        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);
    }

    /**
     * Request used to move the player or items during a battle.
     *
     * @param request
     *   URL for the move operation.
     * @param tag
     *   Tag of the request.
     * @param map
     *   Map contain parameters for movement.
     */
    public void move(String request, String tag, final HashMap<String, String> map) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response.toString());
					}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
						                        //Do something when error occurs
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                    }
                }
        ) {
            //Place the parameters here instead of passing in URL
            @Override
            protected Map<String, String> getParams() {
                //map was preloaded with parameters, return map
                return map;
            }
        };

        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);
	}

    /**
     * Request used to move the player or items during a battle.
     *
     * @param request
     *   URL for the move operation.
     * @param tag
     *   Tag of the request
     */
    public void move(String request, String tag) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Do something when error occurs
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                    }
                }
        );

        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);
    }
	
    /**
     * Returns a single map. All its obstacles have all their info.
     * This is used when we need all the info about a map and its obstacles.
     * @param mapName
     * The name of the map
     * @return
     * A GameMap with all of its obstacle data.
     */
    public GameMap getMapsWithObstacles(String mapName){
        mapWithObstacles = null;

        String request = GETMAPANDOBSTACLES + mapName;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MapParser mapParser = new MapParser();
                        mapWithObstacles = mapParser.parseMapWithObstacleData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Do something when error occurs
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                    }
                }
        ) {
            //Place the parameters here instead of passing in URL
            @Override
            protected Map<String, String> getParams() {
                //map was preloaded with parameters, return map
                return null;
            }
        };
                        
        ServerInter.getInstance(context).addToRequestQueue(stringRequest, REQUEST_TAG_GET_MAPS_WITH_OBSTACLES);

        while(mapWithObstacles == null){}

        return mapWithObstacles;
    }

    /**
     * Get statistics for a player.
     *
     * @param request
     *    URL plus a username to request statistics.
     * @param tag
     *    Tag for the request.
     * @return
     *    JSON String containing all stats.
     */
    public String getStats(String request, String tag) {

        //Use purchase item string rather than creating another variable.
        purchaseItem = "";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                        purchaseItem = response.toString();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Do something when error occurs
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        purchaseItem = "Error";
                    }
                }
        );

        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);

        while (purchaseItem.equals("")) {}
        return purchaseItem;
    }

    /**
     * Send a message to the server. The Message stores the
     * information about the sender and convo ID, so just send
     * it to the server and let the server route it.
     * @param message
     * The Message to send to the server.
     */
  public void sendMessage(final Message message){
    StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + SENDMESSAGE,
      new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
          Log.d(TAG, "onResponse: " + response.toString());
        }
      },
      new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          //Do something when error occurs
          Log.e(TAG, "onErrorResponse: " + error.toString());
        }
      }
    ) {
      //Place the parameters here instead of passing in URL
      @Override
      protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("Content", message.getContent());
        params.put("Sender", message.getSender());
        params.put("ConvoID", "" + message.getConvoID());
        return params;
      }
    };
    ServerInter.getInstance(context).addToRequestQueue(stringRequest, REQUEST_TAG_SEND_MESSAGE);
  }

    /**
     * Send a request to the server to close the
     * currently open chat stream for the specified user.
     * @param username
     * The username for the user whose chat stream
     * should be closed.
     */
  public void closeChat(String username){
    StringRequest stringRequest = new StringRequest(Request.Method.PUT, WEBSERVERURL + CLOSECHAT + username,
      new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
          Log.d(TAG, "onResponse: " + response.toString());
        }
      },
      new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          //Do something when error occurs
          Log.e(TAG, "onErrorResponse: " + error.toString());
        }
      }
    );
    ServerInter.getInstance(context).addToRequestQueue(stringRequest, REQUEST_TAG_CLOSE_CHAT);
  }

    /**
     * Select a map for the battle.
     *
     * @param request
     *   URL for purchase item (SELECTMAP)
     * @param tag
     *   Tag for the request (REQUEST_TAG_SELECT_MAP)
     * @param map
     *   Parameter for map selection inside a hashmap for the post request.
     */
    public void selectMap(String request, String tag, final HashMap<String, String> map) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Do something when error occurs
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                    }
                }
        ) {
            //Place the parameters here instead of passing in URL
            @Override
            protected Map<String, String> getParams() {
                //map was preloaded with parameters, return map
                return map;
            }
        };
        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);
    }

    /**
     * Saves the new created map to database.
     * @param map
     *      Hashmap of map's information
     * @param tag
     *      Tag for the request
     */
    public void addNewMap(final Map<String, String> map, String tag) {
        String request = "AddMap";
        doneAdding = 0;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        doneAdding = 1;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        doneAdding = -1;
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                return map;
            }
        };
        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);

//        while (doneAdding == 0) {}

//        return doneAdding;
    }

    /**
     * Saves the changes on a map
     * @param map
     *      Hashmap of map's information
     * @param tag
     *      Tag for the request
     */
    public void updateCreatedMap(final Map<String, String> map, String tag) {
        String request = "UpdateMap";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WEBSERVERURL + request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                return map;
            }
        };
        ServerInter.getInstance(context).addToRequestQueue(stringRequest, tag);
    }

}
