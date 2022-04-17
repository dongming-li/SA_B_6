package com.cs309.nerdsbattle.nerds_battle.battle;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.GestureDetector;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.RunnableFuture;

import static java.lang.System.currentTimeMillis;

/**
 * Battle Activity holds all Battle Fragments and the communication thread to the server.
 *
 * @author Matthew Kelly
 */
public class BattleActivity extends AppCompatActivity {

    /**
     * User player.
     */
    private Player player;

    /**
     * Battle ID if match making completed.
     */
    private String battleId = null;

    /**
     * XP gain after a battle.
     */
    private int xp_gain;
    /**
     * QP gain after a battle.
     */
    private int qp_gain;
    /**
     * Post Battle outcome message.
     */
    private String outcome;

    /**
     * Messages available after winning a battle.
     */
    private String winningMessages[] = {
            "Great Win Nerd!",
            "A+ Battle, nice win!",
            "Good win, keep studying!",
            "Must have been a athlete... easy win!",
            "To easy, great win!",
            "That's a W!",
            "Superior nerds always win!"
    };

    /**
     * Messages available after losing a battle.
     */
    private String losingMessages[] = {
            "... keep studying",
            "Close loss, keep trying",
            "Devastating Defeat!",
            "You Lose",
            "Keep your head up!",
            "You must have studied the wrong class!"
    };

    /**
     * Messages available after a draw battle.
     */
    private String drawMessages[] = {
            "Didn't know I could draw...",
            "DRAW! BS! I so won that",
            "Well I didn't lose"
    };

    /**
     * Handler for the communication thread to communicate with the UI thread.
     */
    private Handler mHandler;

    /**
     * Activity fragment manager used to swap fragments.
     */
    private FragmentManager fragmentManager;

    /**
     * Created fragments that also need additional input to setup after initial creation.
     */

    /**
     * Battle Map Selection Fragment used by the Shop Activity.
     */
    private BattleMapSelectionFragment battleMapSelectionFragment = null;

    /**
     * Battle Game Fragment used by the Shop Activity.
     */
    private BattleGameFragment battleGameFragment = null;

    private JSONObject gameMap;

    /**
     * Thread that handles the HttpURLConnection.
     */
    private Thread communicationThread;

    /**
     * Creates the battle activity, adds first fragment, and starts communication with the server.
     * @param savedInstanceState
     *  bundle used if using the onSavedInstanceState method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        battleGameFragment = BattleGameFragment.newInstance(player, battleId);

        mHandler = new Handler();

        player = getIntent().getParcelableExtra("User");

        getSupportActionBar().hide();

        fragmentManager = getSupportFragmentManager();
      
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.battleActivityFrameLayout, BattleWaitFragment.newInstance());
        fragmentTransaction.commit();

        //Thread that handles the stream communication with the server.
        communicationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                //Open stream.
                try {
                    url = new URL("http://proj-309-sa-b-6.cs.iastate.edu:8080/SpringMVC/FindGame?Username=" + player.getUsername());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection urlConnection = null;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Setup reader for the stream.
                BufferedReader bufferedReader = null;
                try{
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                } catch (IOException e){
                    e.printStackTrace();
                }
                String line = null;
                String message = null;
                Boolean battleStart = false;
                while (true) {
                    try {
                        line = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        line += "\n";
                    }
                    if (line != null && line.length() > 0) {
                        JSONObject values = null;
                        try {
                            values = new JSONObject(line);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (battleId == null) {
                            //Check for an opponent found and set battle id if so.
                            try {
                                message = values.getString("Message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (message.equals("No opponent found")) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }
                            else if (message.equals("Opponent found")) {
                                try {
                                    battleId = values.getString("BattleID");
                                    mHandler.post(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          //Swap for map selection fragment once finished match making.
                                                          FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                          battleMapSelectionFragment =  BattleMapSelectionFragment.newInstance(player.getUsername(), battleId);
                                                          fragmentTransaction.replace(R.id.battleActivityFrameLayout, battleMapSelectionFragment);
                                                          fragmentTransaction.commit();
                                                      }
                                                  }
                                    );
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                
                            }
                        }
                        if (!battleStart) {
                            try {
                                //Check for battle start and random maps.
                                message = values.getString("Message");
                                if (message.equals("Battle start")) {
                                    battleStart = true;
                                    gameMap = values.getJSONObject("Map");
                                    mHandler.post(new Runnable() {
                                                      @Override
                                                      public void run() {
                                                          //Once battle started swap for game fragment.
                                                          FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                          battleGameFragment = BattleGameFragment.newInstance(player, battleId);
                                                          fragmentTransaction.replace(R.id.battleActivityFrameLayout, battleGameFragment);
                                                          battleMapSelectionFragment = null;
                                                          fragmentTransaction.commit();
                                                          battleGameFragment.addMap(gameMap);
                                                      }
                                                  }
                                    );
                                } else if (message.equals("Random Maps")) {
                                    //Add random maps to the map selection activity.
                                    battleMapSelectionFragment.addGameMaps(values.getJSONArray("Maps"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //Checks for game messages, and ending game messages.
                            message = null;
                            JSONArray playerUpdates = null;
                            try {
                                playerUpdates = values.getJSONArray("Message");
                            } catch (JSONException e) {
                                try {
                                    message = values.getString("Message");
                                } catch (JSONException er) {
                                    er.printStackTrace();
                                }
                            }
                            if (message != null /* && message.contains("Game over") */) {
                                //Game is over setup for post battle screen.
                                if (message.contains("DRAW")) {
                                    xp_gain = 75;
                                    qp_gain = 75;
                                    outcome = drawMessages[(Math.abs((new Random(currentTimeMillis())).nextInt())%drawMessages.length)];
                                } else if (message.contains(player.getUsername())) {
                                    xp_gain = 100;
                                    qp_gain = 100;
                                    outcome = winningMessages[(Math.abs((new Random(currentTimeMillis())).nextInt())%winningMessages.length)];
                                } else {
                                    xp_gain = 50;
                                    qp_gain = 50;
                                    outcome = losingMessages[(Math.abs((new Random(currentTimeMillis())).nextInt())%losingMessages.length)];
                                }
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Swap for post battle fragment.
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.battleActivityFrameLayout, BattlePostBattleFragment.newInstance(player, xp_gain, qp_gain, outcome));
                                        battleGameFragment = null;
                                        fragmentTransaction.commit();
                                    }
                                });
                            }
                            else {
                                //Send game updates to the game fragment.
                                Log.d("Communication Thread", "run: " + playerUpdates.toString());
                                if (battleGameFragment != null) {
                                    battleGameFragment.handleResponse(playerUpdates);
                                }
                            }
                        }
                        Log.d("BattleActivity", "run: " + line);
                    }
                }
            }
        });
        communicationThread.start();
    }

    /**
     * This method executes when the app closes.
     */
    @Override
    protected void onStop() {
        //Stops the communication thread.
        if (communicationThread != null) {
            communicationThread.interrupt();
            communicationThread = null;
        }
        super.onStop();
    }
}
