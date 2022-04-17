package com.cs309.nerdsbattle.nerds_battle.battle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.map.GameMap;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.parsers.MapParser;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.color.holo_red_dark;

/**
 * A {@link Fragment} subclass for allowing users to select a map.
 * Use the {@link BattleMapSelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Matthew Kelly
 */
public class BattleMapSelectionFragment extends Fragment {

    /**
     * Fragment initialization parameters.
     */
    private static final String ARG_PLAYERUSERNAME = "playerUsername";
    private static final String ARG_BATTLEID = "battleID";

    /**
     * Bitmaps Maps and GameMap corresponding objects.
     */
    private Bitmap maps[] = new Bitmap[4];
    private ArrayList<GameMap> gameMaps;

    /**
     * REFRESH_RATE defines how often to update the timer to show how much time has elapsed.
     * refresh every 100 milliseconds
     */
    private final int REFRESH_RATE = 100;

    /**
     * Whether or not the map is selected.
     */
    private boolean mapSelected = false;

    /**
     * GameMaps represented by JSON.
     */
    private JSONArray jsonGameMaps;

    /**
     * Timer variables.
     */
    private float total_time = 15f;
    private long time;
    private long prevTime = 0;

    /**
     * Handler used to provide a timer.
     */
    private Handler mHandler;

    /**
     * Whether or not bitmap gameviews are loaded.
     */
    private boolean mapsLoaded = false;

    /**
     * Current Map being viewed.
     */
    private int mapIndex = 0;

    /**
     * MapSelection UI.
     */
    private TextView timer;
    private Button selectionButton;
    private ImageView mapImage;

    /**
     * Whether or not the timer has changed colors.
     */
    private boolean colorChanged = false;

    /**
     * Swipe Detector Threshold variables.
     */
    private static final int SWIPE_DISP_THRESHOLD = 100;
    private static final int SWIPE_VEL_THRESHOLD = 100;

    /**
     * Username and battleID for the battle.
     */
    private String playerUsername = null;
    private String battleID = null;

    /**
     * Constructor for BattleMapSelectionFragment.
     * Use the {@link BattleMapSelectionFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
    public BattleMapSelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment BattleMapSelectionFragment.
     */
    public static BattleMapSelectionFragment newInstance(String playerUsername, String battleID) {
        BattleMapSelectionFragment fragment = new BattleMapSelectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYERUSERNAME, playerUsername);
        args.putString(ARG_BATTLEID, battleID);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onCreate lifecycle method for the BattleMapSelectionFragment.
     *
     * @param savedInstanceState
     *  Bundle that saves data on recreation if using onSavedInstanceState() method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerUsername = getArguments().getString(ARG_PLAYERUSERNAME);
            battleID = getArguments().getString(ARG_BATTLEID);
        }
        mHandler = new Handler();
    }

    /**
     * Inflates the layout for the Fragment, sets up listeners and starts timer.
     *
     * @param inflater
     *  inflater for the fragment.
     * @param container
     *  ViewGroup for the fragment.
     * @param savedInstanceState
     *  bundle used to save data on recreation if using onSavedInstanceState() method.
     * @return
     *   inflated layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_battle_map_selection, container, false);

        timer = (TextView) v.findViewById(R.id.battleMapSelectionTimer);
        selectionButton = (Button) v.findViewById(R.id.battleMapSelectionBtn);
        mapImage = (ImageView) v.findViewById(R.id.battleMapSelectionMapImage);
        mapImage.setImageResource(R.drawable.questionmark);
        //Set the swipe listener
        final GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                //Allows swipe to look at maps, if a map is not already selected.
                if (!mapSelected) {
                    //Checks to see if the map bitmaps are loaded.
                    if (mapsLoaded) {
                        float xDisp = e2.getX() - e1.getX();
                        float yDisp = e2.getY() - e1.getY();
                        //Swipe checker
                        if (Math.abs(xDisp) > Math.abs(yDisp)) {
                            if (Math.abs(xDisp) > SWIPE_DISP_THRESHOLD && Math.abs(velocityX) > SWIPE_VEL_THRESHOLD) {
                                //Adjust bitmap based on swipe direction.
                                if (xDisp > 0) {
                                    mapIndex--;
                                    if (mapIndex < 0) {
                                        mapIndex = maps.length-1;
                                        while (maps[mapIndex] == null) {
                                            mapIndex--;
                                        }
                                    }
                                } else {
                                    mapIndex = (mapIndex+1)%maps.length;
                                    if (maps[mapIndex] == null) {
                                        mapIndex = 0;
                                    }
                                }
                                mapImage.setImageBitmap(maps[mapIndex]);
                            }
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        //Set the on touch for this inflated view.
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //check for a swipe event.
                return gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });

        selectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Battle ID and username need to be valid to send the request to the server.
                if (playerUsername != null && battleID != null) {
                    mapSelected = true;
                    selectionButton.setClickable(false);
                    selectionButton.setText("Waiting on Other Player");
                    Thread workerThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Sets up hashmap for post request.
                            HashMap<String, String> param = new HashMap<String, String>();
                            if (mapIndex == 0) {
                                param.put("Title", "RANDOM");
                            } else {
                                param.put("Title", gameMaps.get(mapIndex-1).getMapTitle());
                            }
                            param.put("Username", playerUsername);
                            param.put("BattleID", battleID);
                            ServerRequest serverRequest = new ServerRequest(getActivity());
                            serverRequest.selectMap(ServerRequest.SELECTMAP, ServerRequest.REQUEST_TAG_SELECT_MAP, param);
                        }
                    });
                    workerThread.start();
                }
            }
        });

        prevTime = System.currentTimeMillis();
        mHandler.post(startTimer);

        return v;
    }

    /**
     * Converts the elapsed given time and updates the display
     *
     * @param time the time to update the current display to
     */
    private void updateTimer (long time){
        int msec, sec;

        //Convert the milliseconds,seconds
        msec = (int) (time/100)%10;
        sec = (int) (time/1000)%60;

        float temp = (total_time - ((float) sec) - ((float) msec)/10);
        String time_remaining = "";
        if (temp > 3f) {
            if (!colorChanged && temp < 7.5f) {
                colorChanged = true;
                timer.setTextColor(ContextCompat.getColor(getActivity(), holo_red_dark));
            }
            time_remaining += temp;
            timer.setText(time_remaining);
            mHandler.postDelayed(startTimer, REFRESH_RATE);
        }
        else {
            time_remaining += "Battle Starting Soon";
            timer.setText(time_remaining);
        }
    }

    /**
     * Create a Runnable startTimer that makes timer runnable.
     */
    private Runnable startTimer = new Runnable() {
        public void run() {
            long temp = System.currentTimeMillis();
            time += temp - prevTime;
            prevTime = temp;
            updateTimer(time);
        }
    };

    /**
     * Loads bitmaps from GameMap objects.
     *
     * @param gameMaps
     *  list of GameMap objects randomly selected by server.
     */
    public void addGameMaps(ArrayList<GameMap> gameMaps) {
        if (!mapsLoaded) {
            this.gameMaps = gameMaps;
            Thread workerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    maps[0] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.questionmark);
                    for (int i = 0; i < BattleMapSelectionFragment.this.gameMaps.size() && i < 3; i++) {
                        maps[i+1] = BattleMapSelectionFragment.this.gameMaps.get(i).draw(null, getActivity());
                    }
                    mapsLoaded = true;
                }
            });
            workerThread.start();
        }
    }

    /**
     * Loads bitmaps from JSON objects.
     *
     * @param jsonGameMaps
     *  json array of GameMaps randomly selected by server.
     */
    public void addGameMaps(final JSONArray jsonGameMaps) {
        if (!mapsLoaded) {
            this.jsonGameMaps = jsonGameMaps;
            Thread workerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    MapParser mapParser = new MapParser();
                    BattleMapSelectionFragment.this.gameMaps = new ArrayList<GameMap>();
                    try {
                        for (int i = 0; i < BattleMapSelectionFragment.this.jsonGameMaps.length(); i++) {
                            BattleMapSelectionFragment.this.gameMaps.add(mapParser.parseMapWithObstacleData(BattleMapSelectionFragment.this.jsonGameMaps.getJSONObject(i).toString()));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    maps[0] = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.questionmark);
                    for (int i = 0; i < BattleMapSelectionFragment.this.gameMaps.size() && i < 3; i++) {
                        maps[i+1] = BattleMapSelectionFragment.this.gameMaps.get(i).draw(null, getActivity(), mapImage.getWidth(), mapImage.getHeight());
                    }
                    mapsLoaded = true;
                }
            });
            workerThread.start();
        }
    }
}
