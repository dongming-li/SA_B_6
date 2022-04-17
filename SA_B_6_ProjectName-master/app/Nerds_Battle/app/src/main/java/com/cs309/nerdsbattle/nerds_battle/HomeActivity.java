package com.cs309.nerdsbattle.nerds_battle;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.admin_mod.AdminActivity;
import com.cs309.nerdsbattle.nerds_battle.battle.BattleSelectionActivity;
import com.cs309.nerdsbattle.nerds_battle.characteredit.CharacterEditActivity;
import com.cs309.nerdsbattle.nerds_battle.chat.ChatActivity;
import com.cs309.nerdsbattle.nerds_battle.map.MapActivity;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;
import com.cs309.nerdsbattle.nerds_battle.shop.ShopActivity;
import com.cs309.nerdsbattle.nerds_battle.stats.StatsActivity;

import java.util.Map;

/**
 * HomeActivity provides the UI for the user to navigate to all other parts of the game.
 *
 * @author Matthew Kelly, Yee Chan, Chad Riedeman, Sammy Sherman
 */
public class HomeActivity extends AppCompatActivity {

    /**
     * Widgets on the HomeActivity
     */
    private Button btnShop;
    private Button btnChat;
    private Button btnEditChar;
    private Button btnBattle;
    private Button btnCreateMap;
    private Button btnStats;
    private Button btnAdmin;
    private TextView username;
    private TextView money;
    private TextView gpa;

    /**
     * Player Account that is using the application.
     */
    private Player currentUser;

    /**
     * ResultCode for the Shop Activity.
     */
    public static final int SHOP_ACTIVITY = 0;

    /**
     * ResultCode for the Character Edit Activity.
     */
    public static final int CHARACTER_EDIT_ACTIVITY = 1;

    /**
     * onCreate() method for the HomeActivity inflates the layout and adds all listeners.
     *
     * @param savedInstanceState
     *   Bundle passed back from onSavedInstanceState().
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle extras = getIntent().getExtras();

        if(extras == null)
            return;

        //Sets up the button to start the Battle Selection Activity
        btnBattle = (Button) findViewById(R.id.btnStartMatchMaking);
        btnBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BattleSelectionActivity.class);
                intent.putExtra("User", currentUser);
                startActivity(intent);
            }
        });
        currentUser = extras.getParcelable("User");

        //currentUser.makeModerator(true); // TODO: Just here for testing

        btnAdmin = (Button) findViewById(R.id.btnAdmin);
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, AdminActivity.class);
                i.putExtra("currentUser", currentUser);
                startActivity(i);
            }
        });

        if(!currentUser.isModerator() && !currentUser.isAdministrator()) {

            // Hide admin button
            btnAdmin.setVisibility(View.GONE);
        }

        //Sets up the button to start the Shop Activity
        btnShop = (Button) findViewById(R.id.btnShop);
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startShop = new Intent(HomeActivity.this, ShopActivity.class);
                startShop.putExtra("User", currentUser);
                //Started for result to get the updated player back from the shop.
                startActivityForResult(startShop, SHOP_ACTIVITY);
            }
        });

	    btnChat = (Button)findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, ChatActivity.class);
                i.putExtra("currentUser", currentUser);
                startActivity(i);
            }
        });

        btnEditChar = (Button)findViewById(R.id.btnEditCharacter);
        btnEditChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startCharEdit = new Intent(HomeActivity.this, CharacterEditActivity.class);
                startCharEdit.putExtra("currentUser", currentUser);
                startActivityForResult(startCharEdit,CHARACTER_EDIT_ACTIVITY);
            }
        });

        // Here for testing:
        btnCreateMap = (Button) findViewById(R.id.btnCreateMap);
        btnCreateMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(HomeActivity.this, MapEditorActivity.class);
                Intent i = new Intent(HomeActivity.this, MapActivity.class);
                i.putExtra("currentUser", currentUser);
                startActivity(i);
            }
        });

        //Sets up the button to start the Stats Activity
        btnStats = (Button) findViewById(R.id.btnStats);
        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, StatsActivity.class);
                i.putExtra("currentUser", currentUser);
                startActivity(i);
            }
        });

        username = (TextView) findViewById(R.id.Home_Username);
        money = (TextView) findViewById(R.id.Home_Money);
        gpa = (TextView) findViewById(R.id.Home_GPA);
        username.setText("     " + currentUser.getUsername());
        money.setText("Money: " + currentUser.getMoney());
        gpa.setText("GPA: " + currentUser.getGPA());

        final ImageView backgroundOne = (ImageView) findViewById(R.id.homebackground_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.homebackground_two);
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(30000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();
    }

    /**
     * Handles the response of Activities that are started for result.
     *
     * @param requestCode
     *   Status code of the result.
     * @param resultCode
     *   Code of the activity that sent the result.
     * @param data
     *   Intent that contains the passed back data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHOP_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                currentUser = data.getParcelableExtra("User");
                Thread updatePlayer = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ServerRequest sr = new ServerRequest(getApplicationContext());
                        Map<String, String> request = currentUser.update();
                        Log.d("Main Activity Thread", "run: " + currentUser.isInDatabase());
                        if (!request.isEmpty()) {
                            Log.v("Request equals","" + request);
                            sr.updatePlayer(request);
                        }
                    }
                });
                updatePlayer.start();
            }
        }

        if(requestCode == CHARACTER_EDIT_ACTIVITY){
            if(resultCode == RESULT_OK){
                currentUser = data.getParcelableExtra("User");
                Thread updatePlayer = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ServerRequest sr = new ServerRequest(getApplicationContext());
                        Map<String, String> request = currentUser.update();
                        Log.v("Items","Items:"+currentUser.getEquipped());
                        Log.v("request","Request: "+request);
                        if (!request.isEmpty()) {
                            sr.updatePlayer(request);
                        }
                    }
                });
                updatePlayer.start();
            }
        }
    }
}
