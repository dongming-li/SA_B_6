package com.cs309.nerdsbattle.nerds_battle.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.cs309.nerdsbattle.nerds_battle.R;

import java.util.ArrayList;

import static com.cs309.nerdsbattle.nerds_battle.map.MapEditorLayout.cleanbitmap;
import static com.cs309.nerdsbattle.nerds_battle.map.MapEditorLayoutOnTouchEvent.isValidLocation;

/**
 * Map of the game
 */
public class GameMap {

    private int backgroundID;

    private String mapTitle;

    /**
     * Contains all of the obstacles for this particular Map.
     */
    private ArrayList<Obstacle> obstacles = new ArrayList<>();

    /**
     * Represents the images to be drawn on the canvas.
     */
    public static final int[] backgroundImages = {
            R.drawable.background_classroomfloor
    };

    /**
     * Constructs a map.
     * @param mapTitle  Map title
     * @param backgroundID  Background ID of the map
     * @param obstacles     The existing obstacles in the map
     */
    public GameMap(String mapTitle, int backgroundID, ArrayList<Obstacle> obstacles) {
        this.mapTitle = mapTitle;
        this.backgroundID = backgroundID;
        this.obstacles = obstacles;
    }

    /**
     * Getting the map's name.
     * @return  Name of map
     */
    public String getMapTitle(){
        return mapTitle;
    }

    /**
     * Add obstacle to the map.
     * @param obstacle  an obstacle
     */
    public void addObstacle(Obstacle obstacle){
        obstacles.add(obstacle);
    }

    /**
     * Get all the obstacles containing in the map.
     * @return  all obstacles
     */
    public ArrayList<Obstacle> getObstacles(){
        return obstacles;
    }

    //I think this method should just be called once and should only draw the map with the obstacles
    //Then there will be other methods to draw the players and projectiles etc etc

    /**
     * Draws a game map during the map editing in MapEditorActivity.
     * @param bm    Bitmap of the map.
     * @param context   Context of MapEditorActivity
     * @return  Bitmap of map
     */
    public Bitmap draw(Bitmap bm, Context context) {
        //This is the basic idea...
        Bitmap bitmap = Bitmap.createBitmap(cleanbitmap);
        Canvas canvas = new Canvas(bitmap);
//        Drawable backgroundDrawable = context.getDrawable(R.drawable.t_shirt);  //Get the drawable for the background image
//        backgroundDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight()); //Set the bounds to fill the entire canvas
//        backgroundDrawable.draw(canvas); //Draw the background drawable on the canvas

        // Loops through the obstacles and draws them on the map one by one
        for (Obstacle o : obstacles) {
            int xPos = (int) (canvas.getWidth() * o.getxPos()); //canvas width should be screen width, xPos is between 0.0 and 1.0
            int yPos = (int) (canvas.getHeight() * o.getyPos()); //canvas height should be screen height, yPos is between 0.0 and 1.0
            int w = (int) (canvas.getWidth() * o.getWidth()); //o.width is between 0.0 and 1.0
            int l = (int) (canvas.getHeight() * o.getLength()); //o.length is between 0.0 and 1.0
            double rotation = o.getRotation(); //Not sure how we will implement yet


            Drawable obstacleDrawable = context.getDrawable(Obstacle.layoutobstacleImages[o.getPhotoID()]); //Get the drawable corresponding to that PhotoID
            obstacleDrawable.setBounds(xPos-w/2, yPos-l/2, xPos + w/2, yPos + l/2); //Set the bounds using the values calculated above
            if(o == MapEditorLayoutOnTouchEvent.obstacle && isValidLocation == -1) {
                obstacleDrawable.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            }
            else if(o == MapEditorLayoutOnTouchEvent.obstacle && isValidLocation == 1) {
                obstacleDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            }
            else{
                obstacleDrawable.setColorFilter(null);
                obstacleDrawable.setColorFilter(0x00000000,PorterDuff.Mode.MULTIPLY);
            }
            obstacleDrawable.draw(canvas);  //Draw the obstacle drawable on the canvas
            Log.v("Obstacle Vals", xPos + "  " + yPos + "  " + w + "  " + l);
            //The activity that calls this should make a new canvas using a new bitmap
            //That bitmap will have width of screenwidth and height of screenheight
        }
        return bitmap;
    }

    /**
     * Draws a game map with all all obstacles, the width and height
     * of the desired bitmap must be provided.
     *
     * @param bm
     *   unused parameter.
     * @param context
     *   context of the activity.
     * @param width
     *   Width of the wanted bitmap
     * @param height
     *   Height of the wanted bitmap.
     * @return
     *   Bitmap of the map with desired height and width.
     */
    public Bitmap draw(Bitmap bm, Context context, int width, int height) {

        Bitmap temp_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap bitmap = Bitmap.createBitmap(temp_bitmap);
        Canvas canvas = new Canvas(bitmap);

        Drawable backgroundDrawable = context.getDrawable(backgroundImages[0]);
        backgroundDrawable.setBounds(0,0,canvas.getWidth(), canvas.getHeight());
        backgroundDrawable.draw(canvas);

        // Loops through the obstacles and draws them on the map one by one
        for (Obstacle o : obstacles) {
            int xPos = (int) (canvas.getWidth() * o.getxPos()); //canvas width should be screen width, xPos is between 0.0 and 1.0
            int yPos = (int) (canvas.getHeight() * o.getyPos()); //canvas height should be screen height, yPos is between 0.0 and 1.0
            int w = (int) (canvas.getWidth() * o.getWidth()); //o.width is between 0.0 and 1.0
            int l = (int) (canvas.getHeight() * o.getLength()); //o.length is between 0.0 and 1.0
            double rotation = o.getRotation(); //Not sure how we will implement yet


            //Get the obstable drawable and size it correctly for the given map.
            Drawable obstacleDrawable = context.getDrawable(Obstacle.layoutobstacleImages[o.getPhotoID()]); //Get the drawable corresponding to that PhotoID
            obstacleDrawable.setBounds(xPos-w/2, yPos-l/2, xPos + w/2, yPos + l/2); //Set the bounds using the values calculated above
            obstacleDrawable.setColorFilter(null);

            obstacleDrawable.draw(canvas);  //Draw the obstacle drawable on the canvas
            Log.v("Obstacle Vals", xPos + "  " + yPos + "  " + w + "  " + l);
            //The activity that calls this should make a new canvas using a new bitmap
        }
        return bitmap;
    }
}
