package com.cs309.nerdsbattle.nerds_battle.map;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cs309.nerdsbattle.nerds_battle.R;

import java.util.ArrayList;

/**
 * Adapter used for displaying an obstacle in the gridview in MapEditorActivity
 */
public class MapEditorAdapter extends BaseAdapter{


    private ArrayList<Obstacle> obstacles;
    private Context context;

    /**
     * Constructs the adapter
     * @param obstacles     the obstacles to be displayed
     * @param context   context of MapEditorActivity
     */
    public MapEditorAdapter(ArrayList<Obstacle> obstacles, Context context) {
        this.obstacles = obstacles;
        this.context = context;
    }

    @Override
    public int getCount() {
        return  obstacles.size();
    }

    @Override
    public Object getItem(int position) {
        return obstacles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.map_editor_obstaclegrid, null);
        }
        ImageView obstacleImage = (ImageView) convertView.findViewById(R.id.map_editor_grid_obstacle_image);
        obstacleImage.setImageResource(Obstacle.obstacleImages[obstacles.get(position).getPhotoID()]);
        obstacleImage.setLayoutParams(new ConstraintLayout.LayoutParams(220, 220));
        TextView obstacleName = (TextView) convertView.findViewById(R.id.map_editor_grid_obstacle_name);
        obstacleName.setText(obstacles.get(position).getObstacleName());

//        convertView.setOnDragListener(new MapEditorLayoutOnDragListener());

        return convertView;
    }
}
