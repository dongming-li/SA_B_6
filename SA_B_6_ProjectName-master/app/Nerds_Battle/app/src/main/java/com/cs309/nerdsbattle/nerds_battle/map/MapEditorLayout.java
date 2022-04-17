package com.cs309.nerdsbattle.nerds_battle.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Handles all the functions when editing a map
 */
public class MapEditorLayout extends View{

//    private static final int MESSAGE_DATA_RECEIVED = 1;
//    private Thread updateThread;
//    private com.cs309.nerdsbattle.nerds_battle.server_inter.updateHandler updateHandler;
    private Canvas canvas;

    /**
     * a clean bitmap that contains the most basic information of the map
     */
    public static Bitmap cleanbitmap;

    /**
     * Width of the map in Android Studio dp unit
     */
    public static int BattleScreenWidth;

    /**
     * Height of the map in Android Studio dp unit
     */
    public static int BattleScreenHeight;

    /**
     * Constructs a layout for map editing
     * @param context   Context of MapEditorActivity
     * @param attrs
     */
    public MapEditorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

//        MapEditorLayoutOnTouchEvent myOnTouchEvent = new MapEditorLayoutOnTouchEvent(context);
//        setOnTouchListener(myOnTouchEvent);
//
//        MapEditorLayoutOnDragListener mapEditorLayoutOnDragListener = new MapEditorLayoutOnDragListener(context,this);
//        setOnDragListener(mapEditorLayoutOnDragListener);


//        this.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
////                imageView.setImageResource(R.drawable.brown_square);
//
//                ClipData data = ClipData.newPlainText("", "");
//                MapEditorLayoutDragShadow shadowBuilder = new MapEditorLayoutDragShadow(view,getContext());
////                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
//                view.startDrag(data,shadowBuilder,null,0);
//                return true;
//            }
//        });

//        this.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                ClipData data = ClipData.newPlainText("", "");
//                MapEditorLayoutDragShadow shadowBuilder = new MapEditorLayoutDragShadow(view,getContext());
//                view.startDrag(data,shadowBuilder,null,0);
//            }
//        });


//        this.post(new Runnable(){
//            @Override
//            public void run() {
//                int width = getWidth();
//                int height = getHeight();
//                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                canvas = new Canvas(bitmap);
//                Drawable background = getContext().getDrawable(R.drawable.attackbtn);
//                background.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//                background.draw(canvas);
//                setBackground(new BitmapDrawable(getResources(),bitmap));
//            }
//        });

//        setWillNotDraw(false);
//        setBackgroundResource(R.drawable.attackbtn);

//        int width = getWidth(); // this will return 0 and give error
//        int height = getHeight(); // this will return 0 and give error
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        canvas = new Canvas(bitmap);
//        Drawable background = context.getDrawable(R.drawable.attackbtn);
//        background.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        background.draw(canvas);


//        this.thiscontext = context;
//        updateHandler = new updateHandler(this);
//        updateThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ServerRequest sr = new ServerRequest(thiscontext);
//                gameMap = sr.getMapsWithObstacles("ClassroomA");
//                updateHandler.sendEmptyMessage(MESSAGE_DATA_RECEIVED);
//            }
//        });
//        updateThread.start();
    }

    /**
     * Set up the background
     */
    public void setBackground(){
        this.post(new Runnable(){
            @Override
            public void run() {
                int width = getWidth();
                int height = getHeight();
                cleanbitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                canvas = new Canvas(cleanbitmap);
                BattleScreenWidth = canvas.getWidth();
                BattleScreenHeight = canvas.getHeight();
                Drawable background = getContext().getDrawable(GameMap.backgroundImages[0]);
                background.setBounds(0, 0, BattleScreenWidth, BattleScreenHeight);
                background.draw(canvas);
                setBackground(new BitmapDrawable(getResources(),cleanbitmap));
            }
        });
    }

//    public void updateGameMap(GameMap gameMap){
//        this.gameMap = gameMap;
////        this.setOnTouchListener(new MapEditorLayoutOnTouchEvent(gameMap));
//    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//
//
//        while(gameMap == null) {
//        }
//        gameMap.draw(thiscontext,canvas);
//
//        super.onDraw(canvas);
//
//    }

    //    @Override
//    protected void onDraw(Canvas canvas) {
//        // Drawing commands go here
//        Path rect = new Path();
//        rect.addRect(100, 100, 250, 50, Path.Direction.CW);
//        Paint cPaint = new Paint();
//        cPaint.setColor(Color.LTGRAY);
//        canvas.drawPath(rect, cPaint);
//    }


//    public void update(){
//        while(gameMap == null) {
//        }
//        Log.v("MAP LOADED", gameMap.getMapTitle());
//        int width = getWidth();
//        int height = getHeight();
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        gameMap.draw(bitmap,thiscontext);
//        setBackground(new BitmapDrawable(getResources(),bitmap));
//    }

}
