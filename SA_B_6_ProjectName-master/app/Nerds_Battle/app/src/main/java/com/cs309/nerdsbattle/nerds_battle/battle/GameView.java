package com.cs309.nerdsbattle.nerds_battle.battle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.cs309.nerdsbattle.nerds_battle.Player;
import com.cs309.nerdsbattle.nerds_battle.R;
import com.cs309.nerdsbattle.nerds_battle.server_inter.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static java.lang.Math.PI;
import static java.lang.Math.atan2;

/**
 * GameView is the primary battle UI. It contains the canvas that draws all players on the screen and handles input from the BattleActivity.
 */
public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback, View.OnClickListener, JoystickView.JoystickListner {

    /**
     * Joystick values.
     */
    private float joystickX = 0;
    private float joystickY = 0;

    /**
     * Background drawable.
     */
    private Drawable drawable;

    /**
     * Players in game.
     */
    private ArrayList<BattleModel> battleModels = new ArrayList<>(4);

    /**
     * Range attack variables.
     */
    private int rangeItemCounter = -1;
    private double targetX = -1;
    private double targetY = -1;

    /**
     * Main game thread.
     */
    Thread gameThread = null;

    /**
     * Sprite sheet characteristics.
     */
    private static final int BMP_ROWS = 21;
    private static final int BMP_COLUMNS = 13;

    /**
     * Holder for the surface view.
     */
    SurfaceHolder ourHolder;

    /**
     * Static variables designating rows on the sprite sheet.
     */
    private static final int LEFT_WALK = 9;
    private static final int RIGHT_WALK = 11;
    private static final int UP_WALK = 8;
    private static final int DOWN_WALK = 10;
    volatile boolean playing;

    /**
     * Canvas and pain updates used in drawing and updating the surface view.
     */
    Canvas canvas;
    Paint paint;

    /**
     * FPS counter.
     */
    long fps;

    /**
     * Elapsed time during the frame.
     */
    private long timeThisFrame;

    /**
     * Spritesheet bitmap.
     */
    private Bitmap bitmapCharacter;

    /**
     * Crosshair bitmap.
     */
    private Bitmap bitmapCrosshair;

    /**
     * Whether or not the player is moving.
     */
    public boolean isMoving = false;

    /**
     * Max speed of movement.
     */
    float walkSpeedPerSecond = 250;

    /**
     * Width and height of a frame on the sprite sheet.
     */
    private int frameWidth = 100;
    private int frameHeight = 50;

    /**
     * BattleID of the battle being displayed.
     */
    private String battleID = "null";

    /**
     * Max frames.
     */
    private int frameCount = 9;

    /**
     * Current frame.
     */
    private int currentFrame = 0;

    /**
     * Time of last frame change.
     */
    private long lastFrameChangeTime = 0;

    /**
     * Frame change time.
     */
    private int frameLengthInMilliseconds = 100;

    /**
     * Starting motion of character.
     */
    private int motion = DOWN_WALK;

    /**
     * A rectangle to define an area of the
     * sprite sheet that represents 1 frame
     */
    private Rect frameToDraw = new Rect(
            0,
            0,
            frameWidth,
            frameHeight);

    /**
     * Scale used when displaying the player characters.
     */
    private int scale = 2;

    /**
     * A rect that defines an area of the screen
     * on which to draw.
     */
    RectF whereToDraw = new RectF(
	0,
	0,
	0,
	0
	);


    /**
     * Width of the screen.
     */
    private int width;

    /**
     * Height of the screen.
     */
    private int height;

    /**
     * Activity context.
     */
    private Context context;

    /**
     * ThreadPoolExecutor for server requests and updating.
     */
    private MyThreadPoolExecutor threadPoolExecutor;

    /**
     * Most resent player updates JSONArray.
     */
    private JSONArray playerUpdates;

    private Player player;
    private String direction;

    /**
     * Gameview Constructor.
     * @param context
     */
    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // Load sprite from drawables
        bitmapCharacter = BitmapFactory.decodeResource(this.getResources(), R.drawable.sprite);
        bitmapCrosshair = BitmapFactory.decodeResource(this.getResources(), R.drawable.crosshair);
        bitmapCharacter.setWidth(100);
        bitmapCharacter.setHeight(100);
        width = bitmapCharacter.getWidth() / BMP_COLUMNS;
        height = bitmapCharacter.getHeight() / BMP_ROWS;

        frameToDraw = new Rect(0, motion * height, width, motion * height + height);


        threadPoolExecutor = new MyThreadPoolExecutor(10, 20, Long.MAX_VALUE, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(25), new ThreadPoolExecutor.DiscardOldestPolicy());

        playing = true;
    }

    /**
     * Gameview Constructor.
     *
     * @param context
     * @param attrs
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // Load sprite from drawables
        bitmapCharacter = BitmapFactory.decodeResource(this.getResources(), R.drawable.sprite);
        bitmapCrosshair = BitmapFactory.decodeResource(this.getResources(), R.drawable.crosshair);
        width = bitmapCharacter.getWidth() / BMP_COLUMNS;
        height = bitmapCharacter.getHeight() / BMP_ROWS;

        frameToDraw = new Rect(0, motion * height, width, motion * height + height);

        threadPoolExecutor = new MyThreadPoolExecutor(10, 20, Long.MAX_VALUE, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(25), new ThreadPoolExecutor.DiscardOldestPolicy());

        playing = true;
    }

    /**
     * Gameview Constructor.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getHolder().addCallback(this);

        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        // Load sprite from drawables
        bitmapCharacter = BitmapFactory.decodeResource(this.getResources(), R.drawable.sprite);
        bitmapCrosshair = BitmapFactory.decodeResource(this.getResources(), R.drawable.crosshair);
        bitmapCharacter.setWidth(100);
        bitmapCharacter.setHeight(100);
        width = bitmapCharacter.getWidth() / BMP_COLUMNS;
        height = bitmapCharacter.getHeight() / BMP_ROWS;

        frameToDraw = new Rect(0, motion * height, width, motion * height + height);

        threadPoolExecutor = new MyThreadPoolExecutor(10, 20, Long.MAX_VALUE, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(25), new ThreadPoolExecutor.DiscardOldestPolicy());

        playing = true;
    }

    /**
     * Method ran by the game thread to update, draw and keep time.
     */
    @Override
    public void run() {
        while (playing) {
            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            update();

            // Draw the frame
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

        }

    }

    /**
     * Sends requests to server if the player is moving.
     */
    public void update() {

        // If isMoving (the player is touching the joystick)
        // then move him based on his speed, current fps, and the angle.
        if (isMoving) {
            double angle = atan2(joystickY, joystickX) * 180/PI;
            if (angle < 22.5 && angle >= -22.5) {
                direction = "R";
            } else if (angle < 67.5 && angle >= 22.5) {
                direction = "DR";
            } else if (angle < 112.5 && angle >= 67.5) {
                direction = "D";
            } else if (angle < 157.5 && angle >= 112.5) {
                direction = "DL";
            } else if (angle < -157.5 || angle >= 157.7) {
                direction = "L";
            } else if (angle < -112.5 && angle >= -157.5) {
                direction = "UL";
            } else if (angle < -67.5 && angle >= -112.5) {
                direction = "U";
            } else if (angle < -22.5 && angle >= -67.5) {
                direction = "UR";
            }

            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("Direction", direction);
                    params.put("Username", player.getUsername());
                    params.put("BattleID", battleID);
                    ServerRequest serverRequest = new ServerRequest(context);
                    serverRequest.move(ServerRequest.PLAYERMOVEDIRECTION, ServerRequest.REQUEST_TAG_PLAYER_MOVE_DIRECTION, params);

                }
            });

//            if ((deg >= 0 && deg <= 45) || (deg < 0 && deg >= -45)) {
//                motion = RIGHT_WALK;
//            } else if (deg > 45 && deg <= 135) {
//                motion = UP_WALK;
//            } else if ((deg > 135 && deg <= 180) || (deg < -135 && deg >= -180)) {
//                motion = LEFT_WALK;
//            } else {
//                motion = DOWN_WALK;
//            }
        }

    }

    /**
     * Get current frame in sprite sheet.
     */
    public void getCurrentFrame(){

//        long time  = System.currentTimeMillis();
//        if(isMoving) {// Only animate if moving
//            if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
//                lastFrameChangeTime = time;
//                currentFrame++;
//                if (currentFrame >= frameCount) {
//
//                    currentFrame = 0;
//                }
//            }
//        }
        //update the left and right values of the source of
        //the next frame on the spritesheet
//        frameToDraw.left = currentFrame * width;
//        frameToDraw.right = frameToDraw.left + width;
//        frameToDraw.bottom = motion*height+height;
//        frameToDraw.top = motion*height;
        frameToDraw.left = 0;
        frameToDraw.right = width;
        frameToDraw.bottom = DOWN_WALK * height + height;
        frameToDraw.top = DOWN_WALK * height;
    }

    /**
     * Draw the newly updated scene
     */
    public void draw() {

        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
            if (drawable != null) {
//                drawable.draw(canvas);
                canvas.drawColor(Color.argb(255, 26, 128, 182));
                //Log.d(TAG, "draw: Attempted to draw background");
            } else {
                // Draw the background color
                canvas.drawColor(Color.argb(255, 26, 128, 182));
            }

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255, 249, 129, 0));

            if (targetX > 0 && targetY > 0) {
                whereToDraw.set((float) targetX-100,
                        (float) targetY-100,
                        (float) targetX+100,
                        (float) targetY+100);
                canvas.drawBitmap(bitmapCrosshair, null, whereToDraw, paint);
            }

            getCurrentFrame();

            for (int i = 0; i < battleModels.size(); i++) {
                int xpos = (int) percentageToFloat(battleModels.get(i).getXPos(), true);
                int ypos = (int) percentageToFloat(battleModels.get(i).getYPos(), false);
                whereToDraw.set(xpos,
                        ypos,
                        xpos + frameWidth * scale,
                        ypos + frameHeight * scale);

                canvas.drawBitmap(bitmapCharacter,
                        frameToDraw,
                        whereToDraw, paint);

                whereToDraw.set(xpos,
                        ypos,
                        xpos + frameWidth * scale,
                        ypos + frameHeight * scale);
            }

            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }

    }

    /**
     * Pauses the game.
     */
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
        threadPoolExecutor.pause();

    }

    /**
     * Resumes the game.
     */
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
        threadPoolExecutor.resume();
    }

    /**
     * Survice View lifecycle method, just draws the game.
     * @param surfaceHolder
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //Canvas c = getHolder().lockCanvas();
        draw();
        //getHolder().unlockCanvasAndPost(c);
    }

    /**
     * Method to capture surface changed events.
     * @param surfaceHolder
     *  surfaceholder of the gameview.
     * @param i
     * @param i1
     * @param i2
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    /**
     * Surface View destroy method.
     * @param surfaceHolder
     *  surface holder of the gameview.
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    /**
     *  Converts a pixel value to a percentage.
     * @param value
     *   float pixel value
     * @param isXPosition
     *   whether or not the value is an x float value
     * @return
     *   percentage corresponding to the input value
     */
    public double floatToPercentage(float value, boolean isXPosition) {
        if (isXPosition) {
            return value / ((float) getWidth());
        }
        return value / ((float) getHeight());
    }

    /**
     *  Converts a percentage to a float pixel amount.
     * @param value
     *   percentage value between 0-1
     * @param isXPosition
     *   whether or not the value is an x-percentage
     * @return
     *   pixel value corresponding to the input value
     */
    public float percentageToFloat(double value, boolean isXPosition) {
        if (isXPosition) {
            return (float) (value * ((double) getWidth()));
        }
        return (float) (value * ((double) getHeight()));
    }

    /**
     * OnClick method for buttons in the Battle activity.
     *
     * @param v
     *   view that was interacted with.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnMelee) {
            meleeAttack();
        } else if (v.getId() == R.id.btnRange) {
            rangeAttack();
        }
    }

    /**
     * Sends a melee attack request to the server.
     */
    private void meleeAttack() {
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ServerRequest serverRequest = new ServerRequest(context);
                serverRequest.attack(ServerRequest.MELEEATTACK + "?Username=" + player.getUsername() + "&BattleID=" + battleID, ServerRequest.REQUEST_TAG_MELEE_ATTACK);
            }
        });
    }

    /**
     * Sends a range attack request to the server.
     */
    private void rangeAttack() {
        rangeItemCounter++;
        if (targetX < 0 || targetY < 0) {
            return;
        }
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ServerRequest serverRequest = new ServerRequest(context);
                serverRequest.attack(ServerRequest.RANGEATTACK+ rangeItemCounter + "&Username=" + player.getUsername() + "&BattleID=" + battleID + "&TargetX=" +
                        Double.toString(GameView.this.floatToPercentage((float) targetX, true)) + "&TargetY=" + Double.toString(GameView.this.floatToPercentage((float) targetY, false)), ServerRequest.REQUEST_TAG_RANGE_ATTACK);
            }
        });
    }

    /**
     * Handles updates coming from the server. Updates players which will be redrawn.
     *
     * @param toParse
     *   JSONArray sent from the server.
     */
    public void handleResponse(final JSONArray toParse) {
        this.playerUpdates = toParse;
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    for (int i = 0; i < playerUpdates.length(); i++) {
                        final JSONObject obj = playerUpdates.getJSONObject(i);
                        if (battleModels.size() < playerUpdates.length()) {
                            JSONObject orientation = obj.getJSONObject("Orientation");
                            BattleModel temp = new BattleModel(obj.getString("Username"), obj.getInt("Health"), orientation.getDouble("xDir"), orientation.getDouble("xPos"), orientation.getDouble("yDir"), orientation.getDouble("yPos"));
                            battleModels.add(temp);
                        }
                        else {
                            for (BattleModel bm : battleModels) {
                                if (bm.getUsername().equals(obj.getString("Username"))) {
                                    bm.update(obj);
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method for listening to a joystick.
     *
     * @param xPercent
     *   x percentage of the current position of the joystick.
     * @param yPercent
     *   y percentage of the current position of the joystick.
     * @param id
     *   Joystick id
     */
    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        joystickX = xPercent;
        joystickY = yPercent;

        if (joystickX == 0 && joystickY == 0) {
            isMoving = false;
        } else {
            isMoving = true;
        }
    }

    /**
     * Adds this gameview as a listener for the input joystick.
     * @param jv
     *   joystick to add a listener for.
     */
    public void addJoystick(JoystickView jv) {
        jv.setListener(this);
    }

    /**
     * Adds user player to the game.
     *
     * @param player
     *  user player.
     */
    public void addPlayer(Player player) {
        this.player = player;
    }

    /**
     * Set the battleID for the battle being displayed.
     * @param battleID
     *  battleID of the battle.
     */
    public void setBattleID(String battleID) {
        this.battleID = battleID;
    }

    /**
     * Add background drawable to the game.
     *
     * @param drawable
     *  drawable to be displayed in the background.
     */
    public void addDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    /**
     * Set the crosshair location on a double tap event.
     *
     * @param e
     *  Motion event of the double tap event.
     */
    public void doubleTap(MotionEvent e) {
        targetX = e.getX();
        targetY = e.getY();
    }
}
