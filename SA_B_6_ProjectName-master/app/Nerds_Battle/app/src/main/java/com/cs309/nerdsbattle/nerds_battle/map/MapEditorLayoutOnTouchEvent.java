package com.cs309.nerdsbattle.nerds_battle.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import static com.cs309.nerdsbattle.nerds_battle.map.MapEditorActivity.gameMap;
import static com.cs309.nerdsbattle.nerds_battle.map.MapEditorActivity.newSelectedObstacle;
import static com.cs309.nerdsbattle.nerds_battle.map.MapEditorLayout.BattleScreenHeight;
import static com.cs309.nerdsbattle.nerds_battle.map.MapEditorLayout.BattleScreenWidth;
import static com.cs309.nerdsbattle.nerds_battle.map.MapEditorLayout.cleanbitmap;

/**
 * Tracking the user's pointer and handling the different motions from user.
 * It implements OnTouchListener.
 */
public class MapEditorLayoutOnTouchEvent implements View.OnTouchListener {

    private double x;
    private double y;
    private double xnew;
    private double ynew;

    /**
     * Obstacle that the user is selecting
     */
    public static Obstacle obstacle;
    private Obstacle obstacle2;
    private Obstacle newObstacle;
    private int obstacleIndex;
    private Context context;
    private GameMap gameMap2;

    /**
     * Holding the information of location. 1 = valid, -1 = invalid
     */
    public static int isValidLocation;

    /**
     * Constructs the OntouchListener.
     * @param context   context from MapEditorLayout
     */
    public MapEditorLayoutOnTouchEvent(Context context) {
        this.context = context;
        ArrayList<Obstacle> obstacles = gameMap.getObstacles();
        ArrayList<Obstacle> copiedObstacles = new ArrayList<Obstacle>();
        for(int i=0; i<obstacles.size(); i++) {
            Obstacle o = new Obstacle(obstacles.get(i));
            copiedObstacles.add(o);
        }
        gameMap2 = new GameMap("",0,copiedObstacles);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        x = motionEvent.getX();
        y = motionEvent.getY();

        if(MapEditorActivity.newSelectedObstacle != null){
            newObstacle = new Obstacle(newSelectedObstacle);
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:{
                    ArrayList<Obstacle> obstacles = gameMap.getObstacles();
                    if(obstacles.size()==20){
                        Toast t = Toast.makeText(context,"obstacles reached limit",Toast.LENGTH_SHORT);
                        t.show();
                        break;
                    }
                    isValidLocation = 1;
                    for(int i=0; i<obstacles.size(); i++){
                        Obstacle o = obstacles.get(i);
                        double obstacleXmin = o.getxPos()*MapEditorLayout.BattleScreenWidth - o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
                        double obstacleXmax = o.getxPos()*MapEditorLayout.BattleScreenWidth + o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
                        double obstacleYmin = o.getyPos()*MapEditorLayout.BattleScreenHeight - o.getLength()*MapEditorLayout.BattleScreenHeight/2;
                        double obstacleYmax = o.getyPos()*MapEditorLayout.BattleScreenHeight + o.getLength()*MapEditorLayout.BattleScreenHeight/2;

                        if((x > obstacleXmin && x < obstacleXmax) && (y > obstacleYmin && y < obstacleYmax)){
                            isValidLocation = -1;
                            break;
                        }
                    }
                    newObstacle.setxPos(x/BattleScreenWidth);
                    newObstacle.setyPos(y/BattleScreenHeight);
                    gameMap2.addObstacle(newObstacle);
                    ArrayList<Obstacle> obstacles2 = gameMap2.getObstacles();
                    obstacle = obstacles2.get(obstacles.size());
                    Bitmap bm = gameMap2.draw(cleanbitmap, context);
                    view.setBackground(new BitmapDrawable(context.getResources(), bm));
                    break;
                }
                case MotionEvent.ACTION_MOVE:{
                    ArrayList<Obstacle> obstacles = gameMap.getObstacles();
                    isValidLocation = 1;
                    for(int i=0; i<obstacles.size(); i++){
                        Obstacle o = obstacles.get(i);
                        double obstacleXmin = o.getxPos()*MapEditorLayout.BattleScreenWidth - o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
                        double obstacleXmax = o.getxPos()*MapEditorLayout.BattleScreenWidth + o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
                        double obstacleYmin = o.getyPos()*MapEditorLayout.BattleScreenHeight - o.getLength()*MapEditorLayout.BattleScreenHeight/2;
                        double obstacleYmax = o.getyPos()*MapEditorLayout.BattleScreenHeight + o.getLength()*MapEditorLayout.BattleScreenHeight/2;

                        if(x < 0 && y < 0 && (x > obstacleXmin && x < obstacleXmax) && (y > obstacleYmin && y < obstacleYmax)){
                            isValidLocation = -1;
                            break;
                        }
                    }
                    obstacle.setxPos(x/BattleScreenWidth);
                    obstacle.setyPos(y/BattleScreenHeight);
                    Bitmap bm = gameMap2.draw(cleanbitmap, context);
                    view.setBackground(new BitmapDrawable(context.getResources(), bm));
                    break;
                }
                case MotionEvent.ACTION_UP:{
                    if(isValidLocation == 1){
                        isValidLocation = 0; // this is needed here to prevent drawing with color
                        Obstacle o = new Obstacle(obstacle);
                        gameMap.addObstacle(o);
                        Bitmap bm = gameMap2.draw(cleanbitmap, context);
                        view.setBackground(new BitmapDrawable(context.getResources(), bm));
                    }
                    else{
                        isValidLocation = 0;
                        gameMap2.getObstacles().remove(obstacle);
                        Bitmap bm = gameMap2.draw(cleanbitmap, context);
                        view.setBackground(new BitmapDrawable(context.getResources(), bm));
                    }
                    obstacle = null;
                    break;
                }
                default:
                    break;
            }
        }

        else{
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    ArrayList<Obstacle> obstacles = gameMap2.getObstacles();
                    for(int i=0; i<obstacles.size(); i++){
                        Obstacle o = obstacles.get(i);
                        double obstacleXmin = o.getxPos()*MapEditorLayout.BattleScreenWidth - o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
                        double obstacleXmax = o.getxPos()*MapEditorLayout.BattleScreenWidth + o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
                        double obstacleYmin = o.getyPos()*MapEditorLayout.BattleScreenHeight - o.getLength()*MapEditorLayout.BattleScreenHeight/2;
                        double obstacleYmax = o.getyPos()*MapEditorLayout.BattleScreenHeight + o.getLength()*MapEditorLayout.BattleScreenHeight/2;

                        if((x > obstacleXmin && x < obstacleXmax) && (y > obstacleYmin && y < obstacleYmax)){
                            obstacle = obstacles.get(i);
                            obstacleIndex = i;
                            break;
                        }
                    }
                }
                case MotionEvent.ACTION_MOVE: {
//                    Obstacle o = gameMap2.getObstacles().get(0);
                    if(obstacle == null){
                        break;
                    }
                    obstacle.setxPos(x/BattleScreenWidth);
                    obstacle.setyPos(y/BattleScreenHeight);

                    ArrayList<Obstacle> obstacles = gameMap2.getObstacles();
                    obstacle2 = null;
                    for(int i=0; i<obstacles.size(); i++){
                        Obstacle o = obstacles.get(i);
                        double obstacleXmin = o.getxPos()*MapEditorLayout.BattleScreenWidth - o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
                        double obstacleXmax = o.getxPos()*MapEditorLayout.BattleScreenWidth + o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
                        double obstacleYmin = o.getyPos()*MapEditorLayout.BattleScreenHeight - o.getLength()*MapEditorLayout.BattleScreenHeight/2;
                        double obstacleYmax = o.getyPos()*MapEditorLayout.BattleScreenHeight + o.getLength()*MapEditorLayout.BattleScreenHeight/2;

                        if(o != obstacle && (x > obstacleXmin && x < obstacleXmax) && (y > obstacleYmin && y < obstacleYmax)){
                            obstacle2 = obstacles.get(i);
                            break;
                        }
                    }
                    if(obstacle2 != null){
                        isValidLocation = -1;
                    }
                    else{
                        isValidLocation = 1;
                    }
                    if(x < 0 || y < 0){
                        isValidLocation = -1;
                    }

                    Bitmap bm = gameMap2.draw(cleanbitmap, context);
                    view.setBackground(new BitmapDrawable(context.getResources(), bm));
                    break;
                }
                case MotionEvent.ACTION_UP: {
//                    ArrayList<Obstacle> obstacles = gameMap2.getObstacles();
//                    obstacle2 = null;
//                    for(int i=0; i<obstacles.size(); i++){
//                        Obstacle o = obstacles.get(i);
//                        double obstacleXmin = o.getxPos()*MapEditorLayout.BattleScreenWidth - o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
//                        double obstacleXmax = o.getxPos()*MapEditorLayout.BattleScreenWidth + o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
//                        double obstacleYmin = o.getyPos()*MapEditorLayout.BattleScreenHeight - o.getLength()*MapEditorLayout.BattleScreenHeight/2;
//                        double obstacleYmax = o.getyPos()*MapEditorLayout.BattleScreenHeight + o.getLength()*MapEditorLayout.BattleScreenHeight/2;
//
//                        if(o != obstacle && (x > obstacleXmin && x < obstacleXmax) && (y > obstacleYmin && y < obstacleYmax)){
//                            obstacle2 = obstacles.get(i);
//                            break;
//                        }
//                    }

//                    if(obstacle2 != null){
//                        Bitmap bm = gameMap.draw(cleanbitmap, context);
//                        view.setBackground(new BitmapDrawable(context.getResources(), bm));
//                    }

                    if(obstacle == null){
                        break;
                    }
                    if(x < 0 && y > 1207){
                        gameMap.getObstacles().remove(obstacleIndex);
                        gameMap2.getObstacles().remove(obstacleIndex);
                        Bitmap bm = gameMap.draw(cleanbitmap, context);
                        view.setBackground(new BitmapDrawable(context.getResources(), bm));
                        break;
                    }

                    if(isValidLocation == -1) {
                        isValidLocation = 0;
                        Obstacle o = gameMap.getObstacles().get(obstacleIndex);
                        obstacle.setxPos(o.getxPos());
                        obstacle.setyPos(o.getyPos());
                        Bitmap bm = gameMap.draw(cleanbitmap, context);
                        view.setBackground(new BitmapDrawable(context.getResources(), bm));
                    }
                    else {
                        isValidLocation = 0;
                        Obstacle o = gameMap.getObstacles().get(obstacleIndex);
                        o.setxPos(x/BattleScreenWidth);
                        o.setyPos(y/BattleScreenHeight);
                        Bitmap bm = gameMap.draw(cleanbitmap, context);
                        view.setBackground(new BitmapDrawable(context.getResources(), bm));
                    }
                    obstacle = null;
                }

                default:
                    break;
            }
        }
//        if(hasObstacle(x,y)) {
//            switch (motionEvent.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//
////                    if(isValidArea(xnew,ynew)) {
////
////                    MapEditorLayoutStatus status = new MapEditorLayoutStatus(true,null, obstacleIndex);
////
////                    ClipData data = ClipData.newPlainText("", "");
////                    MapEditorLayoutDragShadow shadow = new MapEditorLayoutDragShadow(view, context, true);
////                    view.startDrag(data, shadow, status, 0);
////                    }
////                    else{
////                        MapEditorLayoutStatus status = new MapEditorLayoutStatus(false,null, obstacleIndex);
////
////                        ClipData data = ClipData.newPlainText("", "");
////                        MapEditorLayoutDragShadow shadow = new MapEditorLayoutDragShadow(view, context, false);
////                        view.startDrag(data, shadow, status, 0);
////                    }
//                    break;
//
//                case MotionEvent.ACTION_MOVE:
//
//                    xnew = motionEvent.getX();
//                    ynew = motionEvent.getY();
//
//                    if (isValidArea(xnew, ynew)) {
//
//                        MapEditorLayoutStatus status = new MapEditorLayoutStatus(true, null, obstacleIndex);
//
//                        ClipData data = ClipData.newPlainText("", "");
//                        MapEditorLayoutDragShadow shadow = new MapEditorLayoutDragShadow(view, context, true);
//                        view.startDrag(data, shadow, status, 0);
//                    }
//                    else {
//                        MapEditorLayoutStatus status = new MapEditorLayoutStatus(false, null, obstacleIndex);
//
//                        ClipData data = ClipData.newPlainText("", "");
//                        MapEditorLayoutDragShadow shadow = new MapEditorLayoutDragShadow(view, context, false);
//                        view.startDrag(data, shadow, status, 0);
//                    }
//                    break;
//
//            }
//        }



        return true;
    }

//    public boolean hasObstacle(double x, double y){
//        obstacleIndex = -1;
//        boolean hasObstacle = false;
//
//        ArrayList<Obstacle> obstacles = gameMap.getObstacles();
//
//        for(int i=0; i<obstacles.size(); i++){
//            Obstacle o = obstacles.get(i);
//            double obstacleXmin = o.getxPos()*MapEditorLayout.BattleScreenWidth - o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
//            double obstacleXmax = o.getxPos()*MapEditorLayout.BattleScreenWidth + o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
//            double obstacleYmin = o.getyPos()*MapEditorLayout.BattleScreenHeight - o.getLength()*MapEditorLayout.BattleScreenHeight/2;
//            double obstacleYmax = o.getyPos()*MapEditorLayout.BattleScreenHeight + o.getLength()*MapEditorLayout.BattleScreenHeight/2;
//
//            if((x > obstacleXmin && x < obstacleXmax) && (y > obstacleYmin && y < obstacleYmax)){
//                obstacleIndex = i;
//                hasObstacle = true;
//                break;
//            }
//        }
//        return hasObstacle;
//    }
//
//    public boolean isValidArea(double x, double y){
//        boolean validArea = true;
//
//        ArrayList<Obstacle> obstacles = gameMap.getObstacles();
//
//        for(int i=0; i<obstacles.size(); i++){
//            Obstacle o = obstacles.get(i);
//            double obstacleXmin = o.getxPos()*MapEditorLayout.BattleScreenWidth - o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
//            double obstacleXmax = o.getxPos()*MapEditorLayout.BattleScreenWidth + o.getWidth()*MapEditorLayout.BattleScreenWidth/2;
//            double obstacleYmin = o.getyPos()*MapEditorLayout.BattleScreenHeight - o.getLength()*MapEditorLayout.BattleScreenHeight/2;
//            double obstacleYmax = o.getyPos()*MapEditorLayout.BattleScreenHeight + o.getLength()*MapEditorLayout.BattleScreenHeight/2;
//
//            if((x > obstacleXmin && x < obstacleXmax) && (y > obstacleYmin && y < obstacleYmax)){
//                validArea = false;
//                break;
//            }
//        }
//        return validArea;
//    }

}
