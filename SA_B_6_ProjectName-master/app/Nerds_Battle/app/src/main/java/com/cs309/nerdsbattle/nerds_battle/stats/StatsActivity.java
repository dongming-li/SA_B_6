package com.cs309.nerdsbattle.nerds_battle.stats;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;

import java.util.ArrayList;

/**
 * StatsActivity Displays the stats collected for a specific user, in a ListView.
 *
 * @author Matthew Kelly
 */
public class StatsActivity extends AppCompatActivity {

    /**
     * ActionBar for this context.
     */
    private ActionBar actionBar;

    /**
     * Player using the application.
     */
    private Player player;

    /**
     * ListView that displays the stats.
     */
    private ListView listView;

    /**
     * ArrayList of StatsItems to be displayed in this activity.
     */
    private ArrayList<StatsItem> stats = new ArrayList<>();

    /**
     * Edits the action bar for the Activity to display a title and specific color.
     *
     * @param heading
     *   title of the action bar.
     */
    private void setActionBar(String heading) {
        actionBar = getSupportActionBar();

        actionBar.setTitle(heading);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.shop_action_background)));

        actionBar.show();
    }

    /**
     * onCreate() method for the StatsActivity creates and updates the ListView with the custom adapter.
     * The stats will be loaded from the server.
     *
     * @param savedInstanceState
     *   Bundle obtained if using onSavedInstanceState()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        setActionBar("Stats");

        // Get the player object from the intent.
        player = getIntent().getParcelableExtra("currentUser");

        listView = (ListView) findViewById(R.id.statsActivityListView);

        // Stats not yet supported server side, display static stats for proof of concept.
        stats.add(new StatsItem("Melee Total For", "1254"));
        stats.add(new StatsItem("Melee Hits For", "801"));
        stats.add(new StatsItem("Melee Misses For", "453"));
        stats.add(new StatsItem("Melee Total Against", "1007"));
        stats.add(new StatsItem("Melee Hits Against", "677"));
        stats.add(new StatsItem("Melee Misses Against", "330"));
        stats.add(new StatsItem("Range Total For", "302"));
        stats.add(new StatsItem("Range Hits For", "200"));
        stats.add(new StatsItem("Range Misses For", "102"));
        stats.add(new StatsItem("Range Total Against", "79"));
        stats.add(new StatsItem("Range Hits Against", "45"));
        stats.add(new StatsItem("Range Misses Against", "34"));
        stats.add(new StatsItem("Total Battles", "28"));
        stats.add(new StatsItem("Battle Wins", "20"));
        stats.add(new StatsItem("Battle Losses", "8"));
        stats.add(new StatsItem("Total Battles Online", "18"));
        stats.add(new StatsItem("Battle Wins Online", "14"));
        stats.add(new StatsItem("Battle Losses Online", "4"));
        stats.add(new StatsItem("Total Battles Computer", "10"));
        stats.add(new StatsItem("Battle Wins Computer", "6"));
        stats.add(new StatsItem("Battle Losses Computer", "4"));
        stats.add(new StatsItem("Favorite Melee Weapon", "Ruler"));
        stats.add(new StatsItem("Favorite Range Weapon", "Potato Cannon"));
        stats.add(new StatsItem("Fastest Win (Online)", "1 min 3 seconds"));
        stats.add(new StatsItem("Fastest Lost (Online)", "44 seconds"));
        stats.add(new StatsItem("Matches Against Friends", "7"));
        stats.add(new StatsItem("Time Spent Battling", "3 hrs 3 min 5 seconds"));
        stats.add(new StatsItem("Melee Items Used", "3"));
        stats.add(new StatsItem("Ranged Items Used", "5"));

        //Add the custom adapter for the ListView.
        listView.setAdapter(new StatsAdapter(this, stats));
    }
}
