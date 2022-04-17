package com.cs309.nerdsbattle.nerds_battle.map;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.HomeActivity;
import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateInter;

import java.util.ArrayList;

/**
 * Activity that displays the user's created maps. It also allows user to select the map to be
 * added to game.
 */
public class MapActivity extends AppCompatActivity implements updateInter {

    private PopupWindow selectCreatedMap;
    private Player currentUser;
    private ArrayList<GameMap> userCreatedMap;
    private ListView createdMapsListView;
    private ArrayAdapter<String> createdMapsAdapter;
    private String mapName;
    private static final int MESSAGE_DATA_RECEIVED = 1;
    private Thread updateThread;
    private com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler updateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle extras = getIntent().getExtras();
        if(extras == null) return;
        currentUser = extras.getParcelable("currentUser");
//        if(extras.getString("NewMap") != null){
//            mapName = extras.getString("NewMap");
//        }

        updateHandler = new updateHandler(this);
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerRequest sr = new ServerRequest(getApplicationContext());
                userCreatedMap = sr.getCreatedMapList(currentUser.getUsername(),ServerRequest.REQUEST_TAG_GET_CREATEDMAPLIST);
                updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
            }
        });
        updateThread.start();



//        Button createMapButton = (Button) findViewById(R.id.Map_CreateMap);
//        createMapButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                PopupWindow popupWindow = new PopupWindow(layoutInflater.inflate(R.layout.activity_map_edit_name,null),600,800);
//                popupWindow.showAtLocation(findViewById(R.id.MapActivity), Gravity.CENTER,0,0);
//
//            }
//        });


    }

    /**
     * setting up the functionalities of this activity.
     */
    public void update() {

//        TextView test = (TextView) findViewById(R.id.Map_TestText);
//        test.setText(userCreatedMap.get(0).getMapTitle());

        Button createMapButton = (Button) findViewById(R.id.Map_CreateMap);
        createMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapActivity.this, MapEditNameActivity.class);
                i.putExtra("currentUser", currentUser);
                startActivity(i);
            }
        });

        // get map titles
        final String[] createdMapsName;
//        if(mapName == null) {
            createdMapsName = new String[userCreatedMap.size()];
            for (int i = 0; i < userCreatedMap.size(); i++) {
                createdMapsName[i] = userCreatedMap.get(i).getMapTitle();
            }
//        }
//        else{
//            createdMapsName = new String[userCreatedMap.size()+1];
//            for (int i = 0; i < userCreatedMap.size(); i++) {
//                createdMapsName[i] = userCreatedMap.get(i).getMapTitle();
//            }
//            createdMapsName[createdMapsName.length-1] = mapName;
//            mapName = null;
//        }

        // setting up list view adapter
        createdMapsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,createdMapsName);
        createdMapsListView = (ListView) findViewById(R.id.Map_CreatedMapsList);
        createdMapsListView.setAdapter(createdMapsAdapter);
        createdMapsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.map_activity_select_map,null);
                selectCreatedMap = new PopupWindow(popupView,600,600,true);
                selectCreatedMap.showAtLocation(findViewById(R.id.MapActivity), Gravity.CENTER,0,0);

                View container = (View) selectCreatedMap.getContentView().getParent();
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.3f;
                wm.updateViewLayout(container, p);

                final String mapName = createdMapsName[i];
//                final String mapName = userCreatedMap.get(i).getMapTitle();
                TextView mapNameTextView = (TextView) popupView.findViewById(R.id.MapActivity_CreatedMapName);
                mapNameTextView.setText(mapName);

                Button editCreatedMapButton = (Button) popupView.findViewById(R.id.MapActivity_EditCreatedMap);
                Button addCreatedMapToGameButton = (Button) popupView.findViewById(R.id.MapActivity_AddCreatedMapToGame);

                editCreatedMapButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(MapActivity.this, MapEditorActivity.class);
                        i.putExtra("PreviousActivity","MapActivity");
                        i.putExtra("MapName", mapName);
                        i.putExtra("currentUser", currentUser);
                        startActivity(i);
                    }
                });
                addCreatedMapToGameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        });

        // show how many available slots are left
        TextView mapSlot = (TextView) findViewById(R.id.Map_Slot);
        mapSlot.setText(createdMapsName.length+"/5");
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(MapActivity.this, HomeActivity.class);
        i.putExtra("User", currentUser);
        startActivity(i);
//        finish();
    }

}
