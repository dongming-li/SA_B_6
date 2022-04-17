package com.cs309.nerdsbattle.nerds_battle.map;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;

/**
 * This activity takes place when the user opts to create a map. It asks user to give a name for
 * the map that is going to be created.
 */
public class MapEditNameActivity extends AppCompatActivity {

    private Button confirmMapNameBtn;
    private EditText mapName;
    private Player currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_edit_name);

        Bundle extras = getIntent().getExtras();
        if(extras == null) return;
        currentUser = extras.getParcelable("currentUser");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.5),(int) (height*0.5));

        mapName = (EditText) findViewById(R.id.MapEditName_MapName);
        confirmMapNameBtn = (Button) findViewById(R.id.MapEditName_Confirm);

        confirmMapNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mapName.getText().toString();
                Intent i = new Intent(MapEditNameActivity.this, MapEditorActivity.class);
                i.putExtra("MapName", name);
                i.putExtra("PreviousActivity","MapEditNameActivity");
                i.putExtra("currentUser", currentUser);
                startActivity(i);
            }
        });

    }
}
