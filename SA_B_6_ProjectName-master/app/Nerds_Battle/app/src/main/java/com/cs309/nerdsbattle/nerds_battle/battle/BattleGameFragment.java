package com.cs309.nerdsbattle.nerds_battle.battle;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.parsers.MapParser;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * A {@link Fragment} subclass that displays the battle to the user.
 * Use the {@link BattleGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Matthew Kelly
 */
public class BattleGameFragment extends Fragment {
    /**
     * Fragment initialization parameters.
     */
    private static final String ARG_PLAYER = "player";
    private static final String ARG_BATTLEID = "battleID";

    /**
     * User player.
     */
    private Player player;

    /**
     * Variables to hold messages if gameview is not created yet.
     */
    private boolean firstMessage = true;
    private JSONArray firstMessageObject;

    /**
     * BattleID for the battle.
     */
    private String battleID;

    /**
     * GameMap background in JSON.
     */
    private JSONObject gameMap;

    /**
     * UI Objects.
     */
    private GameView gameView;
    private Button btnMelee;
    private Button btnRange;
    private JoystickView joystick;

    /**
     * Background drawable.
     */
    private Drawable background;

    /**
     * Constructor for BattleGameFragment.
     * Use the {@link BattleGameFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
    public BattleGameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param player current user player
     * @return A new instance of fragment BattleGameFragment.
     */
    public static BattleGameFragment newInstance(Player player, String battleID) {
        BattleGameFragment fragment = new BattleGameFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PLAYER, player);
        args.putString(ARG_BATTLEID, battleID);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * onCreate lifecycle method for the BattleGameFragment.
     *
     * @param savedInstanceState
     *  Bundle that saves data on recreation if using onSavedInstanceState() method.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            player = getArguments().getParcelable(ARG_PLAYER);
            battleID = getArguments().getString(ARG_BATTLEID);
        }
    }

    /**
     * Inflates the layout for the Fragment and sets up listeners.
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
        View v = inflater.inflate(R.layout.fragment_battle_game, container, false);

        //Get UI.
        gameView = (GameView) v.findViewById(R.id.gameview);
        joystick = (JoystickView) v.findViewById(R.id.joystick);
        btnMelee = (Button) v.findViewById(R.id.btnMelee);
        btnRange = (Button) v.findViewById(R.id.btnRange);

        //Add UI to the gamemap.
        gameView.addJoystick(joystick);
        gameView.addPlayer(player);
        btnMelee.setOnClickListener(gameView);
        btnRange.setOnClickListener(gameView);

        gameView.setBattleID(battleID);

        final GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(getActivity(), new GestureDetector.SimpleOnGestureListener() {

            /**
             * Method that gets activated on double tap.
             *
             * @param e
             *  double tap event.
             * @return
             *  whether or not this event should be captured.
             */
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d(TAG, "onDoubleTap: double tap");
                if (gameView != null) {
                    gameView.doubleTap(e);
                }
                return true;
            }

            /**
             * Method that gets activated on a long press.
             *
             * @param e
             *  long press motion event.
             */
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);

            }

            /**
             * Method that gets activated on a Double Tap Event.
             * @param e
             *  Double Tap Event.
             * @return
             *  true to capture event.
             */
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }

            /**
             * Method to capture when the user presses down. Needed to allow double tap capturing.
             * @param e
             *  down press motion event.
             * @return
             *  true to capture a down press.
             */
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        //Set the on touch for this inflated view.
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetectorCompat.onTouchEvent(motionEvent);
            }
        });

        return v;
    }

    /**
     * This method executes when the player returns to the game
     */
    @Override
    public void onResume() {
        super.onResume();

        gameView.resume();
    }

    /**
     * This method executes when the app gets interrupted on the foreground.
     */
    @Override
    public void onPause() {
        super.onPause();

        gameView.pause();
    }

    /**
     * Passes real-time info from the server to the gamemap.
     *
     * @param toParse
     *  JSONArray values from server.
     */
    public void handleResponse(JSONArray toParse) {
        if (gameView == null) {
            if (firstMessage) {
                firstMessage = false;
                firstMessageObject = toParse;
                Thread workerThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (gameView == null) {}
                        gameView.handleResponse(firstMessageObject);
                    }
                });
            }
        } else {
            gameView.handleResponse(toParse);
        }
    }

    /**
     * Add a map to the background of the GameView.
     *
     * @param map
     *  JSON representation of a GameMap.
     */
    public void addMap(JSONObject map) {
        gameMap = map;
        Thread workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                //Check to make sure gameview is created.
                while (gameView == null){}
                while (gameView.getHeight() == 0 && gameView.getWidth() == 0) {}
                MapParser mapParser = new MapParser();
                background = new BitmapDrawable(getResources(), mapParser.parseMapWithObstacleData(gameMap.toString()).draw(null, getActivity(), gameView.getWidth(), gameView.getHeight()));
                gameView.addDrawable(background);
            }
        });
        workerThread.start();
    }

    /**
     * Set the battle ID for the battle.
     * @param newBattleId
     *  new battle id.
     */
    public void setBattleId(String newBattleId) {
        gameView.setBattleID(newBattleId);
    }
}
