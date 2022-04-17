package com.cs309.nerdsbattle.nerds_battle.characteredit;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.*;
import com.cs309.nerdsbattle.nerds_battle.shop.Item;

import java.util.ArrayList;

/**
 * Activity that control item changing events
 */
public class CharacterEditActivity extends AppCompatActivity implements updateInter {

    private Player currentUser;
    private ArrayList<Item> userItem;

    private GridView gridView;
    private CharacterEditAdapter hairStyleAdapter;
    private CharacterEditAdapter glassesAdapter;
    private CharacterEditAdapter uniformAdapter;
    private CharacterEditAdapter meleeAdapter;
    private CharacterEditAdapter rangeAdapter;
    private ToggleButton hairStyleBtn;
    private ToggleButton glassesBtn;
    private ToggleButton uniformBtn;
    private ToggleButton meleeBtn;
    private ToggleButton rangeBtn;
    private ImageView hairStyleImage;
    private ImageView glassesImage;
    private ImageView uniformImage;
    private ImageView meleeWeaponImage;
    private ImageView rangeWeaponImage;
    private ArrayList<CharacterEditItem> hairStyle;
    private ArrayList<CharacterEditItem> glasses;
    private ArrayList<CharacterEditItem> uniform;
    private ArrayList<CharacterEditItem> meleeWeapon;
    private ArrayList<CharacterEditItem> rangeWeapon;
    private ArrayList<Integer> equippedPositions;

    private static final int MESSAGE_DATA_RECEIVED = 1;
    private Thread updateThread;
    private updateHandler updateHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_edit);

        Bundle extras = getIntent().getExtras();
        if(extras == null) return;
        currentUser = extras.getParcelable("currentUser");
      //  ArrayList<String> userItemArrList = currentUser.getItems();

        //ServerRequest sr = new ServerRequest(getApplicationContext());
//        userItem = sr.getItemList(userItemArrList,ServerRequest.REQUEST_TAG_GET_ITEMLIST);
        //String itemListCheck = sr.getItemList(userItemArrList,ServerRequest.REQUEST_TAG_GET_ITEMLIST);


        //Every time that you want to make a serverRequest, you always have to have these things.
        //updateHandler allows you to do things in the update method below and is necessary
        //updateThread is the thread the request HAS to be sent in, otherwise the app will crash.
        //Inside the thread is the runnable, inside the runnable is the request, the variable that gets the returned value from the request and an initiation of the update handler.
        //At the end, .start is called to start the thread.
        updateHandler = new updateHandler(this);
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServerRequest sr = new ServerRequest(getApplicationContext());
                userItem = sr.getItemList(currentUser.getItems(),ServerRequest.REQUEST_TAG_GET_ITEMLIST);
                updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
            }
        });
        updateThread.start();

        // Item categories
        hairStyleBtn = (ToggleButton) findViewById(R.id.CharEdit_HairStyle);
        glassesBtn = (ToggleButton) findViewById(R.id.CharEdit_Glasses);
        uniformBtn = (ToggleButton) findViewById(R.id.CharEdit_Uniform);
        meleeBtn = (ToggleButton) findViewById(R.id.CharEdit_MeleeWeapon);
        rangeBtn = (ToggleButton) findViewById(R.id.CharEdit_RangeWeapon);
        hairStyleImage = (ImageView) findViewById(R.id.CharEdit_HairStyleImage);
        glassesImage = (ImageView) findViewById(R.id.CharEdit_GlassesImage);
        uniformImage = (ImageView) findViewById(R.id.CharEdit_UniformImage);
        meleeWeaponImage = (ImageView) findViewById(R.id.CharEdit_MeleeImage);
        rangeWeaponImage = (ImageView) findViewById(R.id.CharEdit_RangeImage);

//        Button backButton = (Button) findViewById(R.id.CharEditBack);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               onBackPressed();
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        if(equippedPositions.get(0)!=-1) {
            String hairStyleName = hairStyle.get(equippedPositions.get(0)).getCharacterEditItem().getTitle();
            currentUser.equipItem(hairStyleName, "HairStyle");
        }
        if(equippedPositions.get(1)!=-1) {
            String glassesName = glasses.get(equippedPositions.get(1)).getCharacterEditItem().getTitle();
            currentUser.equipItem(glassesName, "Glasses");
        }
        if(equippedPositions.get(2)!=-1) {
            String uniformName = uniform.get(equippedPositions.get(2)).getCharacterEditItem().getTitle();
            currentUser.equipItem(uniformName, "Clothing");
        }
        if(equippedPositions.get(3)!=-1) {
            String meleeWeaponName = meleeWeapon.get(equippedPositions.get(3)).getCharacterEditItem().getTitle();
            currentUser.equipItem(meleeWeaponName, "Melee");
        }
        if(equippedPositions.get(4)!=-1) {
            String rangeWeaponName = rangeWeapon.get(equippedPositions.get(4)).getCharacterEditItem().getTitle();
            currentUser.equipItem(rangeWeaponName, "Range");
        }

        Intent i = new Intent();
        i.putExtra("User",currentUser);
        setResult(RESULT_OK, i);
        super.onBackPressed();
    }

    @Override
    public void update() {

//        TextView test = (TextView) findViewById(R.id.CharEdittest);
//        test.setText(userItem.get(0).getTitle()+", "+userItem.get(1).getTitle());

        // intialization
        gridView = (GridView) findViewById(R.id.CharacterEdit_Items);
        hairStyle = new ArrayList<>();
        glasses = new ArrayList<>();
        uniform = new ArrayList<>();
        meleeWeapon = new ArrayList<>();
        rangeWeapon = new ArrayList<>();
        equippedPositions = new ArrayList<>();
//        equippedPositions = new ArrayList<>();
//        equippedPositions.add(-1);
//        equippedPositions.add(-1);
//        equippedPositions.add(-1);
//        equippedPositions.add(-1);
//        equippedPositions.add(-1);
        equippedPositions.add(-1);
        equippedPositions.add(-1);
        equippedPositions.add(-1);
        equippedPositions.add(-1);
        equippedPositions.add(-1);
        arrangeItems();
        updateEquippedPositions();
        hairStyleAdapter = new CharacterEditAdapter(this, hairStyle);
        glassesAdapter = new CharacterEditAdapter(this, glasses);
        uniformAdapter = new CharacterEditAdapter(this, uniform);
        meleeAdapter = new CharacterEditAdapter(this, meleeWeapon);
        rangeAdapter = new CharacterEditAdapter(this, rangeWeapon);


        hairStyleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    // turn off other categories when this button is on
                    glassesBtn.setChecked(false);
                    uniformBtn.setChecked(false);
                    meleeBtn.setChecked(false);
                    rangeBtn.setChecked(false);
                    gridView.setAdapter(hairStyleAdapter);
                    // wait until the adapter is received
                    gridView.post(new Runnable() {
                        @Override
                        public void run() {
//                            if(equippedPositions.get(0)!=-1)
//                                gridView.getChildAt(equippedPositions.get(0)).setBackgroundColor(Color.parseColor("#D3D3D3"));
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//                                    equip(0,view);
                                    hairStyle.get(position).setCharEditItemState(1);
                                    if(equippedPositions.get(0)!=-1)
                                        hairStyle.get(equippedPositions.get(0)).setCharEditItemState(0);
                                    equippedPositions.set(0,position);
                                    hairStyleAdapter.notifyDataSetChanged();
                                    Item temp = hairStyle.get(position).getCharacterEditItem();
                                    hairStyleImage.setImageResource(Item.itemImages[temp.getPhotoID()]);
                                    hairStyleImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                                }
                            });
                        }
                    });
                }
                else {
                    gridView.setAdapter(null);
                }
            }
        });

        glassesBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    hairStyleBtn.setChecked(false);
                    uniformBtn.setChecked(false);
                    meleeBtn.setChecked(false);
                    rangeBtn.setChecked(false);
                    gridView.setAdapter(glassesAdapter);
                    gridView.post(new Runnable() {
                        @Override
                        public void run() {
//                            if(equippedPositions.get(1)!=-1)
//                                gridView.getChildAt(equippedPositions.get(1)).setBackgroundColor(Color.parseColor("#D3D3D3"));

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
//                                    equip(1,position);
                                    glasses.get(position).setCharEditItemState(1);
                                    if(equippedPositions.get(1)!=-1)
                                        glasses.get(equippedPositions.get(1)).setCharEditItemState(0);
                                    equippedPositions.set(1,position);
                                    glassesAdapter.notifyDataSetChanged();
                                    Item temp = glasses.get(position).getCharacterEditItem();
                                    glassesImage.setImageResource(Item.itemImages[temp.getPhotoID()]);
                                    glassesImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                                }
                            });

                            // plan: do editing in adapter. use a static maybe

//                            View v = new View(getApplicationContext());
//                            glassesAdapter.getView(4,v,gridView);
//                            v.performClick();
                        }
                    });
                }
                else {
                    gridView.setAdapter(null);
                }
            }
        });

        uniformBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    hairStyleBtn.setChecked(false);
                    glassesBtn.setChecked(false);
                    meleeBtn.setChecked(false);
                    rangeBtn.setChecked(false);
                    gridView.setAdapter(uniformAdapter);
                    gridView.post(new Runnable() {
                        @Override
                        public void run() {
//                            if(equippedPositions.get(2)!=-1)
//                                gridView.getChildAt(equippedPositions.get(2)).setBackgroundColor(Color.parseColor("#D3D3D3"));
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                    uniform.get(position).setCharEditItemState(1);
                                    if(equippedPositions.get(2)!=-1)
                                        uniform.get(equippedPositions.get(2)).setCharEditItemState(0);
                                    equippedPositions.set(2,position);
                                    uniformAdapter.notifyDataSetChanged();
                                    Item temp = uniform.get(position).getCharacterEditItem();
                                    uniformImage.setImageResource(Item.itemImages[temp.getPhotoID()]);
                                    uniformImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                                }
                            });
                        }
                    });
                }
                else {
                    gridView.setAdapter(null);
                }
            }
        });

        meleeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
//                    unCheckedAll();
                    hairStyleBtn.setChecked(false);
                    glassesBtn.setChecked(false);
                    uniformBtn.setChecked(false);
                    rangeBtn.setChecked(false);
                    gridView.setAdapter(meleeAdapter);
                    gridView.post(new Runnable() {
                        @Override
                        public void run() {
//                            if(equippedPositions.get(3)!=-1)
//                                gridView.getChildAt(equippedPositions.get(3)).setBackgroundColor(Color.parseColor("#D3D3D3"));
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                    meleeWeapon.get(position).setCharEditItemState(1);
                                    if(equippedPositions.get(3)!=-1)
                                        meleeWeapon.get(equippedPositions.get(3)).setCharEditItemState(0);
                                    equippedPositions.set(3,position);
                                    meleeAdapter.notifyDataSetChanged();
                                    Item temp = meleeWeapon.get(position).getCharacterEditItem();
                                    meleeWeaponImage.setImageResource(Item.itemImages[temp.getPhotoID()]);
                                    meleeWeaponImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                                }
                            });
                        }
                    });
                }
                else {
                    gridView.setAdapter(null);
                }
            }
        });

        rangeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    hairStyleBtn.setChecked(false);
                    glassesBtn.setChecked(false);
                    uniformBtn.setChecked(false);
                    meleeBtn.setChecked(false);
                    gridView.setAdapter(rangeAdapter);
                    gridView.post(new Runnable() {
                        @Override
                        public void run() {
//                            if(equippedPositions.get(4)!=-1)
//                                gridView.getChildAt(equippedPositions.get(4)).setBackgroundColor(Color.parseColor("#D3D3D3"));
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                    rangeWeapon.get(position).setCharEditItemState(1);
                                    if(equippedPositions.get(4)!=-1)
                                        rangeWeapon.get(equippedPositions.get(4)).setCharEditItemState(0);
                                    equippedPositions.set(4,position);
                                    rangeAdapter.notifyDataSetChanged();
                                    Item temp = rangeWeapon.get(position).getCharacterEditItem();
                                    rangeWeaponImage.setImageResource(Item.itemImages[temp.getPhotoID()]);
                                    rangeWeaponImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                                }
                            });
                        }
                    });
                }
                else {
                    gridView.setAdapter(null);
                }
            }
        });

    }

    /**
     * Separate user items into its own category.
     */
    public void arrangeItems(){
        for(Item item : userItem) {
            CharacterEditItem characterEditItem = new CharacterEditItem(item,0);
            if (item.getType().equals("HairStyle"))
                hairStyle.add(characterEditItem);
            else if (item.getType().equals("Glasses"))
                glasses.add(characterEditItem);
            else if (item.getType().equals("Clothing"))
                uniform.add(characterEditItem);
            else if (item.getType().equals("Melee"))
                meleeWeapon.add(characterEditItem);
            else if (item.getType().equals("Range"))
                rangeWeapon.add(characterEditItem);
        }
    }

//    /**
//     * Equip the selected item
//     * @param category
//     * @param view
//     */
//    public void equip(int category, View view){
//
//        int previousPosition = equippedPositions.get(category);
////        equippedPositions.add(category,position);
//        if(previousPosition!=position) {
////            gridView.setItemChecked(previousPosition, false);
////            gridView.setItemChecked(position, true);
//            equippedPositions.set(category,position);
//            if(previousPosition!=-1)
//                gridView.getChildAt(previousPosition).setBackgroundColor(0x00000000);
//            View v = gridView.getChildAt(position);
//            v.setBackgroundColor(Color.parseColor("#D3D3D3"));
//        }
//    }


    /**
     * Getting the equipped item positions from database. Only called once at the beginning.
     */
    // only called once in the beginning
    public void updateEquippedPositions(){
        String itemsInString = currentUser.getEquipped();
        itemsInString = itemsInString.substring(1,itemsInString.length()-1);
        String anItemInString = "";
        while(!itemsInString.equals("")){
            if(itemsInString.contains(",")){
                int commaIndex = itemsInString.indexOf(",");
                anItemInString = itemsInString.substring(0,commaIndex);
                itemsInString = itemsInString.substring(commaIndex+1,itemsInString.length());
                updateEquippedPositionsHelper(anItemInString);
            }
            else{
                updateEquippedPositionsHelper(itemsInString);
                itemsInString = "";
            }
        }
    }

    /**
     * Helper method for updateEquippedPosition
     * @param item
     */
    public void updateEquippedPositionsHelper(String item){
        String itemName = item.substring(item.indexOf(":")+2,item.length()-1);
        if(item.contains("HairStyle")){
            for(int i=0;i<hairStyle.size();i++){
                if(hairStyle.get(i).getCharacterEditItem().getTitle().equals(itemName)){
                    equippedPositions.set(0,i);
//                    equipped.set(0,itemName);
                    hairStyle.get(i).setCharEditItemState(1);
                    Item temp = hairStyle.get(i).getCharacterEditItem();
                    hairStyleImage.setImageResource(Item.itemImages[temp.getPhotoID()]);
                    hairStyleImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    break;
                }
            }
        }
        if(item.contains("Glasses")){
            for(int i=0;i<glasses.size();i++){
                if(glasses.get(i).getCharacterEditItem().getTitle().equals(itemName)){
                    equippedPositions.set(1,i);
//                    equipped.set(1,itemName);
                    glasses.get(i).setCharEditItemState(1);
                    Item temp = glasses.get(i).getCharacterEditItem();
                    glassesImage.setImageResource(Item.itemImages[temp.getPhotoID()]);
                    glassesImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    break;
                }
            }
        }
        if(item.contains("Clothing")){
            for(int i=0;i<uniform.size();i++){
                if(uniform.get(i).getCharacterEditItem().getTitle().equals(itemName)){
                    equippedPositions.set(2,i);
//                    equipped.set(2,itemName);
                    uniform.get(i).setCharEditItemState(1);
                    Item temp = uniform.get(i).getCharacterEditItem();
                    uniformImage.setImageResource(Item.itemImages[temp.getPhotoID()]);
                    uniformImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    break;
                }
            }
        }
        if(item.contains("Melee")){
            for(int i=0;i<meleeWeapon.size();i++){
                if(meleeWeapon.get(i).getCharacterEditItem().getTitle().equals(itemName)){
                    equippedPositions.set(3,i);
//                    equipped.set(3,itemName);
                    meleeWeapon.get(i).setCharEditItemState(1);
                    Item temp = meleeWeapon.get(i).getCharacterEditItem();
                    meleeWeaponImage.setImageResource(Item.itemImages[temp.getPhotoID()]);
                    meleeWeaponImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    break;
                }
            }
        }
        if(item.contains("Range")){
            for(int i=0;i<rangeWeapon.size();i++){
                if(rangeWeapon.get(i).getCharacterEditItem().getTitle().equals(itemName)){
                    equippedPositions.set(4,i);
//                    equipped.set(4,itemName);
                    rangeWeapon.get(i).setCharEditItemState(1);
                    Item temp = rangeWeapon.get(i).getCharacterEditItem();
                    rangeWeaponImage.setImageResource(Item.itemImages[temp.getPhotoID()]);
                    rangeWeaponImage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    break;
                }
            }
        }
    }
}
