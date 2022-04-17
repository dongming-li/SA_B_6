package com.cs309.nerdsbattle.nerds_battle.stats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.R;

import java.util.ArrayList;

/**
 * StatsAdapter is a custom adapter for the StatsActivity. Displays a name and value for stats
 * within a ListView.
 *
 * @author Matthew Kelly
 * Created by Matthew Kelly on 11/6/2017.
 */

public class StatsAdapter extends ArrayAdapter<StatsItem> {

    /**
     * Create a StatsAdapter for the specific Activity using the stats array provided.
     *
     * @param context
     *   context of the activity this adapter is used within.
     * @param stats
     *   ArrayList of StatsItem objects to display.
     */
    public StatsAdapter(Context context, ArrayList<StatsItem> stats) {
        super(context, 0, stats);
    }

    /**
     * Inflates the ListView item and sets the text using the ArrayList provided.
     *
     * @param position
     *   index of item inside the ListView.
     * @param convertView
     *   View object that is inflated.
     * @param parent
     *   parent viewgroup for this item in the ListView.
     * @return
     *   returns the inflated view.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StatsItem temp = getItem(position);

        //Inflate the view if not created.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.statsactivitylistitem, parent, false);
        }

        //Get the TextViews.
        TextView statName = (TextView) convertView.findViewById(R.id.statsActivityItemName);
        TextView statValue = (TextView) convertView.findViewById(R.id.statsActivityItemValue);

        //Set the text for each TextView.
        statName.setText(temp.name);
        statValue.setText(temp.value);

        return convertView;
    }
}
