package com.cs309.nerdsbattle.nerds_battle.battle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cs309.nerdsbattle.nerds_battle.HomeActivity;
import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Activity that allows the user to select between the different battle tyes such as
 * 1c1, 2v2, or against a computer (coming soon).
 *
 * @author Matthew Kelly
 */
public class BattleSelectionActivity extends AppCompatActivity {

    /**
     * UI buttons
     */
    private Button onlineBtn1p;
    private Button onlineBtn2p;
    private Button computerBtn;

    /**
     * User's player.
     */
    private Player player;

    /**
     * onCreate lifecycle for the BattleSelectionActivity, setups up all listeners for buttons.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_selection);

        player = getIntent().getParcelableExtra("User");

        onlineBtn1p = (Button) findViewById(R.id.onlineBtn1p);
        onlineBtn2p = (Button) findViewById(R.id.onlineBtn2p);
        computerBtn = (Button) findViewById(R.id.computerBtn);

        onlineBtn1p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Starting Battle", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BattleSelectionActivity.this, BattleActivity.class);
                intent.putExtra("User", player);
                startActivity(intent);
                finish();
            }
        });

        onlineBtn2p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "2v2 Battles coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        computerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Computer Battle coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        getSupportActionBar().hide();
    }
}
