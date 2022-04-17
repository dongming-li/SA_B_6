package com.cs309.nerdsbattle.nerds_battle.map;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateInter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.cs309.nerdsbattle.nerds_battle.map.MapEditorLayout.cleanbitmap;

/**
 * Activity that handles the editing of a map
 */
public class MapEditorActivity extends AppCompatActivity implements updateInter, MapEditorOnFragmentFinishLoad {

    private ArrayList<Obstacle> obstaclesList;

    /**
     * The map that is being editing
     */
    public static GameMap gameMap;

    /**
     * Selected obstacle to be placed on the map
     */
    public static Obstacle newSelectedObstacle;
    private Button saveButton;
    private ArrayList<Obstacle> table;
    private ArrayList<Obstacle> chair;
    private ArrayList<Obstacle> bookcase;
    private ToggleButton tableBtn;
    private ToggleButton chairBtn;
    private ToggleButton bookcaseBtn;
    private MapEditorAdapter tableAdapter;
    private MapEditorAdapter chairAdapter;
    private MapEditorAdapter bookcaseAdapter;
    private GridView obstaclesGridView;
    private MapEditorLayout mapEditorLayout;
//    private TextView test;
    private int previousObstacleIndex;
    private int previousCategory;
    private int previousActivity;
    private String mapName;
    private PopupWindow saveButtonPopup;
    private Player currentUser;
    private int doneAdding;

    private static final int MESSAGE_DATA_RECEIVED = 1;
    private Thread updateThread;
    private com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler updateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_editor);
        mapEditorLayout = (MapEditorLayout) findViewById(R.id.mapEditorLayout);
        mapEditorLayout.setBackground();
        previousObstacleIndex = -1;

        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        String from = extras.getString("PreviousActivity");
        if (from.equals("MapEditNameActivity")) {
            mapName = extras.getString("MapName");
            previousActivity = 0;
        }
        else if(from.equals("MapActivity")) {
            mapName = extras.getString("MapName");
            previousActivity = 1;
        }
        currentUser = extras.getParcelable("currentUser");


//        mapEditorLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                gameMap = new GameMap("",0,new ArrayList<Obstacle>());
//                Obstacle o = new Obstacle("",0,0.05,0.12,0.1,0.5,0);
//                Obstacle o2 = new Obstacle("",0,0.05,0.12,0.4,0.8,0);
//                gameMap.addObstacle(o);
//                gameMap.addObstacle(o2);
//                Bitmap bm = gameMap.draw(cleanbitmap, getApplicationContext());
//                mapEditorLayout.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bm));
//                MapEditorLayoutOnTouchEvent myOnTouchEvent = new MapEditorLayoutOnTouchEvent(getApplicationContext());
//                mapEditorLayout.setOnTouchListener(myOnTouchEvent);
//            }
//        });

        obstaclesGridView = (GridView) findViewById(R.id.gridViewObstacles);

        updateHandler = new updateHandler(this);
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerRequest sr = new ServerRequest(getApplicationContext());
                obstaclesList = sr.getObstacleList(ServerRequest.REQUEST_TAG_GET_OBSTACLES);
                if(previousActivity==1){
                    gameMap = sr.getMapsWithObstacles(mapName);
                }
                updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
            }
        });
        updateThread.start();



//        MapEditorLayoutOnTouchEvent myOnTouchEvent = new MapEditorLayoutOnTouchEvent(getApplicationContext());
//        mapEditorLayout.setOnTouchListener(myOnTouchEvent);

//        MapEditorLayoutOnDragListener mapEditorLayoutOnDragListener = new MapEditorLayoutOnDragListener(getApplicationContext());
//        mapEditorLayout.setOnDragListener(mapEditorLayoutOnDragListener);

    }

    /**
     * Updates the map once the request was received from database
     */
    public void update() {
        mapEditorLayout.post(new Runnable() {
            @Override
            public void run() {
//                gameMap = new GameMap("",0,new ArrayList<Obstacle>());
//                Obstacle o = new Obstacle("",0,0.05,0.12,0.1,0.5,0);
//                Obstacle o2 = new Obstacle("",0,0.05,0.12,0.4,0.8,0);
//                gameMap.addObstacle(o);
//                gameMap.addObstacle(o2);
//                Bitmap bm = gameMap.draw(cleanbitmap, getApplicationContext());
//                mapEditorLayout.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bm));
//                MapEditorLayoutOnTouchEvent myOnTouchEvent = new MapEditorLayoutOnTouchEvent(getApplicationContext());
//                mapEditorLayout.setOnTouchListener(myOnTouchEvent);


                if(previousActivity == 0){
                    gameMap = new GameMap(mapName,0,new ArrayList<Obstacle>());
                }
                else if(previousActivity == 1){
                    Bitmap bm = gameMap.draw(cleanbitmap, getApplicationContext());
                    mapEditorLayout.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bm));
                }
                MapEditorLayoutOnTouchEvent myOnTouchEvent = new MapEditorLayoutOnTouchEvent(getApplicationContext());
                mapEditorLayout.setOnTouchListener(myOnTouchEvent);
            }
        });

        table = new ArrayList<Obstacle>();
        chair = new ArrayList<Obstacle>();
        bookcase = new ArrayList<Obstacle>();
        while(obstaclesList==null) {}


        for(Obstacle o : obstaclesList){
            switch(o.getType()) {
                case "Table" :
                    table.add(o);
                    break;
                case "Chair" :
                    chair.add(o);
                    break;
                case "Bookcase" :
                    bookcase.add(o);
                    break;
                default:
                    break;
            }
        }

        tableAdapter = new MapEditorAdapter(table,this);
        chairAdapter = new MapEditorAdapter(chair,this);
        bookcaseAdapter = new MapEditorAdapter(bookcase,this);

        setUpFragment1Part2();

//        obstaclesGridViewAdapter = new MapEditorAdapter(obstaclesList,this);
//        obstaclesGridView.setAdapter(obstaclesGridViewAdapter);
//        obstaclesGridView.setOnItemLongClickListener(itemLongClickListener);
//        obstaclesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
////                Toast.makeText(getApplicationContext(),""+i,Toast.LENGTH_LONG);
//                test.setText(""+i);
//
//            }
//        });

//        mapEditorLayout.setOnDragListener(onDragListener);

    }

    /**
     * Continue setting up the top fragment containing the obstacle types buttons once the buttons
     * are ready to be called.
     */
    public void setUpFragment1Part2(){

        saveButton = (Button) findViewById(R.id.mapEditor_Save);
        tableBtn = (ToggleButton) findViewById(R.id.MapEditor_Table);
        chairBtn = (ToggleButton) findViewById(R.id.MapEditor_Chair);
        bookcaseBtn = (ToggleButton) findViewById(R.id.MapEditor_Bookcase);

        tableBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    // turn off other categories when this button is on
                    chairBtn.setChecked(false);
                    bookcaseBtn.setChecked(false);
                    previousCategory = 0;
                    newSelectedObstacle = null;
                    obstaclesGridView.setAdapter(tableAdapter);
                    // wait until the adapter is received
                    obstaclesGridView.post(new Runnable() {
                        @Override
                        public void run() {
                            obstaclesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                    selectObstacle(0,position);
                                }
                            });
                        }
                    });
                }
                else {
                    obstaclesGridView.setAdapter(null);
                    newSelectedObstacle = null;
                    previousCategory = -1;
                }
            }
        });

        chairBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    // turn off other categories when this button is on
                    tableBtn.setChecked(false);
                    bookcaseBtn.setChecked(false);
                    newSelectedObstacle = null;
                    previousCategory = 1;
                    obstaclesGridView.setAdapter(chairAdapter);
                    // wait until the adapter is received
                    obstaclesGridView.post(new Runnable() {
                        @Override
                        public void run() {
                            obstaclesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                    selectObstacle(1,position);
                                }
                            });
                        }
                    });
                }
                else {
                    obstaclesGridView.setAdapter(null);
                    newSelectedObstacle = null;
                    previousCategory = -1;
                }
            }
        });

        bookcaseBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    // turn off other categories when this button is on
                    chairBtn.setChecked(false);
                    tableBtn.setChecked(false);
                    newSelectedObstacle = null;
                    previousCategory = 2;
                    obstaclesGridView.setAdapter(bookcaseAdapter);
                    // wait until the adapter is received
                    obstaclesGridView.post(new Runnable() {
                        @Override
                        public void run() {
                            obstaclesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                    selectObstacle(2,position);
                                }
                            });
                        }
                    });
                }
                else {
                    obstaclesGridView.setAdapter(null);
                    newSelectedObstacle = null;
                    previousCategory = -1;
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.map_editor_save_map,null);
                saveButtonPopup = new PopupWindow(popupView,600,500,true);
                saveButtonPopup.showAtLocation(findViewById(R.id.MapEditorActivity), Gravity.CENTER,0,0);

                View container = (View) saveButtonPopup.getContentView().getParent();
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.3f;
                wm.updateViewLayout(container, p);

                Button confirmButton = (Button) popupView.findViewById(R.id.MapEditor_SaveMapConfirm);
                Button cancelButton = (Button) popupView.findViewById(R.id.MapEditor_SaveMapCancel);
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateMapOnDatabase();

                        Intent i = new Intent(MapEditorActivity.this, MapActivity.class);
                        i.putExtra("currentUser", currentUser);
                        if(previousActivity == 0)
                            i.putExtra("NewMap",mapName);
                        startActivity(i);
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveButtonPopup.dismiss();
                    }
                });

            }
        });

    }

    /**
     * Selecting an obstacle.
     * @param category  Category: 0 = table, 1 = chair, 2 = bookcase
     * @param position  Position
     */
    // Category: 0 = table, 1 = chair, 2 = bookcase
    private void selectObstacle(int category, int position){
        obstaclesGridView.getChildAt(position).setBackgroundColor(Color.parseColor("#D3D3D3"));
        switch (category) {
            case 0:
//                fragment2Obstacle = table.get(position);
                newSelectedObstacle = table.get(position);
                setUpFragment2Part1();
                break;
            case 1:
//                fragment2Obstacle = chair.get(position);
                newSelectedObstacle = chair.get(position);
                setUpFragment2Part1();
                break;
            case 2:
//                fragment2Obstacle = bookcase.get(position);
                newSelectedObstacle = bookcase.get(position);
                setUpFragment2Part1();
                break;
            default:
                break;
        }

        if(previousObstacleIndex != -1 && position!=previousObstacleIndex)
            obstaclesGridView.getChildAt(previousObstacleIndex).setBackgroundColor(0x00000000);
        previousObstacleIndex = position;

    }

    /**
     * Setting up the fragment containing obstacles and save buttons.
     */
    public void setUpFragment1Part1(){
        MapEditorTopFragment1 topFragment1 = new MapEditorTopFragment1();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.MapEditor_TopFragment,topFragment1);
        fragmentTransaction.commit();
    }

    /**
     * Setting up the fragment that will display the detail of an obstacle.
     */
    public void setUpFragment2Part1() {

//        Bundle bundle = new Bundle();
//        String obstacleName = obstacle.getObstacleName();
//        String obstacleAttributes = "Length: " + obstacle.getLength() + " ,  Width: " + obstacle.getWidth() + " ,  Height: " + obstacle.getHeight();
//        String obstacleDescription = obstacle.getDescription();
//        bundle.putString("Name",obstacleName);
//        bundle.putString("Attributes",obstacleAttributes);
//        bundle.putString("Description",obstacleDescription);

        MapEditorTopFragment2 topFragment2 = new MapEditorTopFragment2();
//        topFragment2.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.MapEditor_TopFragment,topFragment2);
        fragmentTransaction.commit();

    }

    /**
     * Continue setting up the fragment that contains the obstacle detail once the objects in the
     * fragment are ready to be called.
     */
    public void setUpFragment2Part2() {

        String obstacleName = newSelectedObstacle.getObstacleName();
        String obstacleAttributes = "Length: " + newSelectedObstacle.getLength() + " ,  Width: " + newSelectedObstacle.getWidth() + " ,  Height: " + newSelectedObstacle.getHeight();
        String obstacleDescription = newSelectedObstacle.getDescription();
        TextView obsName = (TextView) findViewById(R.id.MapEditor_ObstacleName);
        TextView obsAttribs = (TextView) findViewById(R.id.MapEditor_ObstacleAttributes);
        TextView obsDesc = (TextView) findViewById(R.id.MapEditor_ObstacleDescription);
        ImageView obsImage = (ImageView) findViewById(R.id.MapEditor_TopFragObsImage);
        Button cancelBtn = (Button) findViewById(R.id.MapEditor_TopFragCancelBtn);

        obsName.setText(obstacleName);
        obsAttribs.setText(obstacleAttributes);
        obsDesc.setText(obstacleDescription);
        obsImage.setImageResource(Obstacle.obstacleImages[newSelectedObstacle.getPhotoID()]);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newSelectedObstacle = null;
                setUpFragment1Part1();
            }
        });
    }

    @Override
    public void onFinish(String tag, boolean state) {

        if(tag.equals("From Top Fragment 2")) {
//            test.setText("done");
            setUpFragment2Part2();
        }

        if(tag.equals("From Top Fragment 1")){
            setUpFragment1Part2();
            switch(previousCategory){
                case -1:
                    tableBtn.setChecked(false);
                    chairBtn.setChecked(false);
                    bookcaseBtn.setChecked(false);
                    break;
                case 0:
                    tableBtn.setChecked(true);
                    break;
                case 1:
                    chairBtn.setChecked(true);
                    break;
                case 2:
                    bookcaseBtn.setChecked(true);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * Saves the map if the user is creating this map.
     * @return  HashMap of map
     */
    public HashMap<String, String> addMap(){
        String mapTitle = gameMap.getMapTitle();
        String username = "Player1";
        String backgroundID = "0";

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Title",mapTitle);
        map.put("Creator",username);
        map.put("BackgroundID",backgroundID);

        ArrayList<Obstacle> obstacles = gameMap.getObstacles();
        JSONArray jsonObstaclesArr = new JSONArray();
        for(int i=0; i<obstacles.size(); i++){
            String rotation = "0";
            String xPos = "" + obstacles.get(i).getxPos();
            String yPos = "" + obstacles.get(i).getyPos();
            String obstacleName = obstacles.get(i).getObstacleName();

            JSONObject jsonObstacle = new JSONObject();
            JSONObject jsonPosition = new JSONObject();
            try {
                jsonPosition.put("r", rotation);
                jsonPosition.put("x",xPos);
                jsonPosition.put("y",yPos);
                jsonObstacle.put("Title", obstacleName);
                jsonObstacle.put("Position", jsonPosition);
            } catch (Throwable t) {
                Log.e("MapEditorActivity", "Fail to create JSONObject");
            }
            jsonObstaclesArr.put(jsonObstacle);
        }
        map.put("Obstacles", jsonObstaclesArr.toString());

        return map;
    }

    /**
     * Saves the map if the user is editing an existing map.
     * @return  HashMap of map
     */
    public HashMap<String, String> editMap(){
        String mapTitle = gameMap.getMapTitle();
//        String username = "Player1";
//        String backgroundID = "0";

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Title",mapTitle);
//        map.put("Creator",username);
//        map.put("BackgroundID",backgroundID);

        ArrayList<Obstacle> obstacles = gameMap.getObstacles();
        JSONArray jsonObstaclesArr = new JSONArray();
        for(int i=0; i<obstacles.size(); i++){
            String rotation = "0";
            String xPos = "" + obstacles.get(i).getxPos();
            String yPos = "" + obstacles.get(i).getyPos();
            String obstacleName = obstacles.get(i).getObstacleName();

            JSONObject jsonObstacle = new JSONObject();
            JSONObject jsonPosition = new JSONObject();
            try {
                jsonPosition.put("r", rotation);
                jsonPosition.put("x",xPos);
                jsonPosition.put("y",yPos);
                jsonObstacle.put("Title", obstacleName);
                jsonObstacle.put("Position", jsonPosition);
            } catch (Throwable t) {
                Log.e("MapEditorActivity", "Fail to create JSONObject");
            }
            jsonObstaclesArr.put(jsonObstacle);
        }
        map.put("Obstacles", jsonObstaclesArr.toString());

        return map;
    }

    /**
     * Send the map information to database.
     */
    public void updateMapOnDatabase(){
        doneAdding = 0;

        Thread updateMap = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerRequest sr = new ServerRequest(getApplicationContext());

                if(previousActivity == 0){
                    Map<String, String> request = addMap();
                    sr.addNewMap(request, ServerRequest.REQUEST_TAG_ADD_MAP);
                }
                else if(previousActivity == 1){
                    Map<String, String> request = editMap();
                    sr.updateCreatedMap(request, ServerRequest.REQUEST_TAG_EDITMAP);
                }

//                Map<String, String> request = currentUser.update();
//                Log.d("Main Activity Thread", "run: " + currentUser.isInDatabase());
//                if (!request.isEmpty()) {
//                    Log.v("Request equals","" + request);
//                    sr.updatePlayer(request);
//                }
            }
        });
        updateMap.start();

    }

//    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
//        @Override
//        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//            Obstacle obstacle = (Obstacle) parent.getItemAtPosition(position);
////            MapEditor_PassObstacle passObstacle = new MapEditor_PassObstacle(view,obstacle);
//            MapEditorLayoutStatus status = new MapEditorLayoutStatus(false,obstacle,-1);
//
//            ClipData data = ClipData.newPlainText("", "");
//            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
//            view.startDrag(data,shadowBuilder,status,0);
//
//            return true;
//        }
//    };

    /*
    View.OnDragListener onDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
//            String area;
//            if(v == area1){
//                area = "area1";
//            }else if(v == area2){
//                area = "area2";
//            }else if(v == area3){
//                area = "area3";
//            }else{
//                area = "unknown";
//            }

            switch (dragEvent.getAction()) {

                case DragEvent.ACTION_DROP:
//                    prompt.append("ACTION_DROP: " + area  + "\n");

                    MapEditor_PassObstacle obstacle = (MapEditor_PassObstacle) dragEvent.getLocalState();
                    View v = obstacle.view;
                    Obstacle passObstacle = obstacle.obstacle;
                    x = dragEvent.getX()/MapEditorLayout.BattleScreenWidth;
                    y = dragEvent.getY()/MapEditorLayout.BattleScreenHeight;
//                    float x = dragEvent.getX();
//                    float y = dragEvent.getY();
                    test.setText("x:"+x+", y:"+y);
                    passObstacle.setxPos(x);
                    passObstacle.setyPos(y);
                    gameMap.addObstacle(passObstacle);
                    cleanbitmap = gameMap.draw(cleanbitmap,getApplicationContext());
                    mapEditorLayout.setBackground(new BitmapDrawable(getResources(),cleanbitmap));


//                    AbsListView oldParent = (AbsListView)view.getParent();
//                    ItemBaseAdapter srcAdapter = (ItemBaseAdapter)(oldParent.getAdapter());

//                    LinearLayoutAbsListView newParent = (LinearLayoutAbsListView)v;
//                    ItemBaseAdapter destAdapter = (ItemBaseAdapter)(newParent.absListView.getAdapter());
//                    List<Item> destList = destAdapter.getList();

//                    if(removeItemToList(srcList, passedItem)){
//                        addItemToList(destList, passedItem);
//                    }

//                    srcAdapter.notifyDataSetChanged();
//                    destAdapter.notifyDataSetChanged();

                    //smooth scroll to bottom
//                    newParent.absListView.smoothScrollToPosition(destAdapter.getCount()-1);

                    break;
//                case DragEvent.ACTION_DRAG_ENDED:
//                    prompt.append("ACTION_DRAG_ENDED: " + area  + "\n");
                default:
                    break;
            }

            return true;
        }
    };
    */


}
