package com.cs309.nerdsbattle.nerds_battle.shop;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;

import java.util.ArrayList;
import java.util.Map;

/**
 * Displays the shop to the user. Uses two custom adapters {@link ShopAdapter} and {@link ShopListAdapter}
 * to provide the custom layout.
 *
 * @author Matthew Kelly
 */
public class ShopActivity extends AppCompatActivity {

    private static final int SHOP_ITEMDISP_ACTIVITY = 1;

    ExpandableListView expandableListView;

    protected Shop shop;
    protected Player player;

    private ActionBar actionBar;
    private TextView actionBarTitle;
    private TextView actionBarMoney;

    private ArrayList<String> arrayList;

    /**
     * Method to display custom action bar.
     *
     * @param heading
     *    New title of the action bar.
     */
    private void setActionBar(String heading) {
        actionBar = getSupportActionBar();

        //Custom layout parameters to fix issue with everything being pushed to one side.
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final View actionBarLayout = (View) getLayoutInflater().inflate(R.layout.shop_action_bar, null);

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout, layoutParams);
        actionBarTitle = (TextView) findViewById(R.id.shop_action_bar_title);
        actionBarTitle.setText(heading);
        actionBarMoney = (TextView) findViewById(R.id.shop_action_bar_money);
        actionBarMoney.setText(player.getMoney() + " QP");

        // Custom tool bar also used to fix the issue mentioned above.
        Toolbar toolbar=(Toolbar)actionBar.getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.getContentInsetEnd();
        toolbar.setPadding(0, 0, 0, 0);

        //Custom Background color.
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.shop_action_background)));

        actionBar.show();
    }

    /**
     * Adds the layout to the activity and sets all variables, adapters, and UI when the Activity is created.
     *
     * @param savedInstanceState
     *  Bundle containing data if the onSavedInstanceState() method is used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shop = new Shop();
        Bundle extras = getIntent().getExtras();

        if(extras == null)
            return;

        player = extras.getParcelable("User");
        setContentView(R.layout.activity_shop);
        setActionBar("Shop");

        //Setup an arraylist for the expandable listview.
        arrayList = new ArrayList<>();
        arrayList.add("Melee");
        arrayList.add("Range");
        arrayList.add("Glasses");
        arrayList.add("Clothing");
        arrayList.add("Hair Styles");
        Shop shop = new Shop();
        expandableListView = (ExpandableListView) findViewById(R.id.list_View);

        //Set custom adapter.
        expandableListView.setAdapter(new ShopListAdapter(this, arrayList, shop));
    }

    /**
     * Launches the {@link ShopItemDisplayActivity} when an item in a gridview is clicked.
     *
     * @param item
     *   Item to display.
     */
    public void purchase(Item item) {
        Intent itemDisp = new Intent(ShopActivity.this, ShopItemDisplayActivity.class);
        itemDisp.putExtra("item", item);
        itemDisp.putExtra("player", player);
        startActivityForResult(itemDisp, SHOP_ITEMDISP_ACTIVITY);
    }

    /**
     * Method that setup the intent to be sent back to the home activity.
     */
    public void sendResult() {
        Intent intent2main = new Intent();
        intent2main.putExtra("User", player);
        setResult(RESULT_OK, intent2main);
    }

    /**
     * Overrides onBackPressed to send back the result to the home activity.
     */
    @Override
    public void onBackPressed() {
        sendResult();
        super.onBackPressed();
    }

    /**
     * Updates the shop interface based on the result of the {@link ShopItemDisplayActivity}.
     *
     * @param requestCode
     *   code of the activity sending the result.
     * @param resultCode
     *   code of the result.
     * @param data
     *   player data and updated item within an intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("ShopActivity", "onActivityResult: Got Here " + requestCode + " " + resultCode + " " + RESULT_OK);
        if (requestCode == SHOP_ITEMDISP_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                player = data.getParcelableExtra("User");
                Item temp = data.getParcelableExtra("Item");

                //Update Action bar
                actionBarMoney.setText(player.getMoney() + " QP");
                actionBar.show();

                //Find item and update item in the shop.
                for (int i = 0; i < 5; i++) {
                    if (shop.get(i) != null) {
                        for (int j = 0; j < shop.get(i).size(); j++) {
                            if (shop.get(i).get(j).equals(temp)) {
                                shop.get(i).get(j).setOwned(temp.isOwned());
                            }
                        }
                    }
                }
                expandableListView.invalidate();
            }
        }
    }
}

