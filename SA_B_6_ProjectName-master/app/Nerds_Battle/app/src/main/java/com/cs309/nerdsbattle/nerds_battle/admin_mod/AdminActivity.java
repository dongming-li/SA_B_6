package com.cs309.nerdsbattle.nerds_battle.admin_mod;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.cs309.nerdsbattle.nerds_battle.R;

public class AdminActivity extends AppCompatActivity {

    private Button editPlayerBtn;
    private Button viewAllPlayersBtn;
    private Button viewChatsBtn;
    private Button viewCreatedMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        editPlayerBtn = (Button)findViewById(R.id.buttonEditPlayer);
        viewAllPlayersBtn = (Button)findViewById(R.id.buttonViewAllPlayers);
        viewChatsBtn = (Button)findViewById(R.id.buttonViewChats);
        viewCreatedMaps = (Button)findViewById(R.id.buttonViewMaps);


    }
}
