package com.cs309.nerdsbattle.nerds_battle.shop;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler;
import com.cs309.nerdsbattle.nerds_battle.server_inter.updateInter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import static com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler.MESSAGE_DATA_RECEIVED;

/**
 * Display an Item with description, name, stats and compares it to the currently equipped item.
 *
 * @author Matthew Kelly
 */
public class ShopItemDisplayActivity extends AppCompatActivity implements updateInter {

    private Item item;
    private Player player;
    private ArrayList<Item> userItems = null;

    private Thread purchaseThread = null;
    private Thread updateThread = null;

    private com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler updateHandler;

    private TextView title;
    private TextView desc;
    private ImageView image;
    private TextView stats;
    private TextView cost;
    private Button purchaseBtn;
    private ActionBar actionBar;
    private TextView actionBarTitle;
    private TextView actionBarMoney;

    private String msg = null;

    /**
     * Handles the updating of the ActionBar for the activity. Uses a custom layout with Title and player QP.
     *
     * @param heading
     *   new title of the ActionBar.
     */
    private void setActionBar(String heading) {
        actionBar = getSupportActionBar();
        //actionBar.setCustomView(R.layout.shop_title);

        //Inflate the layout and get the correct parameters.
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final View actionBarLayout = (View) getLayoutInflater().inflate(R.layout.shop_action_bar, null);

        //Configure the ActionBar
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        //Set the custom action bar layout.
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout, layoutParams);

        //Update the values within the ActionBar.
        actionBarTitle = (TextView) findViewById(R.id.shop_action_bar_title);
        actionBarTitle.setText(heading);
        actionBarMoney = (TextView) findViewById(R.id.shop_action_bar_money);
        actionBarMoney.setText(player.getMoney() + " QP");

        //Fixes layout, without this everything is pushed to the left.
        Toolbar toolbar=(Toolbar)actionBar.getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.getContentInsetEnd();
        toolbar.setPadding(0, 0, 0, 0);


        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.shop_action_background)));
        actionBar.show();
    }

    /**
     * Adds the layout to the activity and sets all variables and UI when the Activity is created.
     *
     * @param savedInstanceState
     *  Bundle containing data if the onSavedInstanceState() method is used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_item_display);

        updateHandler = new updateHandler(this);

        //Get passed objects.
        item = getIntent().getParcelableExtra("item");
        player = getIntent().getParcelableExtra("player");

        setActionBar("Shop");

        //Set UI component values
        title = (TextView) findViewById(R.id.shop_itemdisp_title);
        desc = (TextView) findViewById(R.id.shop_itemdisp_description);
        image = (ImageView) findViewById(R.id.shop_itemdisp_image);
        image.setImageResource(Item.itemImages[item.getPhotoID()]);
        stats = (TextView) findViewById(R.id.shop_itemdisp_stats);
        cost = (TextView) findViewById(R.id.shop_itemdisp_cost);
        title.setText(item.getTitle());
        desc.setText("Description: " + item.getDesc());
        cost.setText("Cost: " + item.getValue() + " QP");

        //Fetch equipped items from DB
        if (userItems == null) {
            updateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ServerRequest sr = new ServerRequest(getApplicationContext());
                    userItems = sr.getItemList(player.getItems(), ServerRequest.REQUEST_TAG_GET_ITEMLIST);
                    updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
                }
            });
            updateThread.start();
        }

        //Search for an equipped item of the same type.
        Item temp = null;
        if (userItems != null) {
            for (int i = 0; i < userItems.size(); i++) {
                if (userItems.get(i).getType().equals(item.getType())) {
                    temp = userItems.get(i);
                    break;
                }
            }
        }
        stats.setText("Stats:\n" + item.compareStats(temp));

        //Set listener for purchase btn
        purchaseBtn = (Button) findViewById(R.id.shop_itemdisp_purchasebtn);
        if (item.isOwned()) {
            purchaseBtn.setClickable(false);
            purchaseBtn.setText("Owned");
        }
        else {
            purchaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    purchaseThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ServerRequest serverRequest = new ServerRequest(getApplicationContext());
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("Username", player.getUsername());
                            map.put("Item", item.getTitle());

                            msg = serverRequest.purchaseItem(ServerRequest.PURCHASEITEM, ServerRequest.REQUEST_TAG_PURCHASEITEM, map);
                            updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
                        }
                    });
                    purchaseThread.start();
                }
            });
        }
    }

    /**
     * Handles the updating of the UI when a thread finishes executing.
     */
    public void update() {
        //Checks to see if message is null, meaning the update is in regards to equipped items of the player.
        if (msg == null) {
            //Search for an equipped item of the same type.
            Item temp = null;
            if (userItems != null) {
                for (int i = 0; i < userItems.size(); i++) {
                    if (userItems.get(i).getType().equals(item.getType())) {
                        temp = userItems.get(i);
                    }
                    if (userItems.get(i).equals(item)) {
                        purchaseBtn.setClickable(false);
                        purchaseBtn.setText("Owned");
                        item.setOwned(true);
                    }
                }
            }
            //Update the stats text.
            stats.setText("Stats:\n" + item.compareStats(temp));
        }
        else {
            //Check the result of the purchase request.
            if (msg.equals("Success")) {
                player.addItem(item.getTitle(), item.getType());
                purchaseBtn.setClickable(false);
                purchaseBtn.setText("Owned");
                item.setOwned(true);
                player.setMoney(player.getMoney()-item.getValue());
                actionBarMoney.setText(""+player.getMoney());
                actionBar.show();
                Toast.makeText(this, "Item purchased, QP remaining: " + player.getMoney(), Toast.LENGTH_SHORT).show();
            }
            else if (msg.equals("Item already owned")) {
                purchaseBtn.setClickable(false);
                purchaseBtn.setText("Owned");
                item.setOwned(true);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
            else if (msg.equals("Insufficient funds")) {
                Toast.makeText(this, "Insufficient QP", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Sends result to the ShopActivity.
     */
    public void sendResult() {
        Intent intent2main = new Intent();
        intent2main.putExtra("User", player);
        intent2main.putExtra("Item", item);
        setResult(RESULT_OK, intent2main);
    }

    /**
     * Override onBackPressed to send back the result from this activity.
     */
    @Override
    public void onBackPressed() {
        sendResult();
        super.onBackPressed();
    }
}
