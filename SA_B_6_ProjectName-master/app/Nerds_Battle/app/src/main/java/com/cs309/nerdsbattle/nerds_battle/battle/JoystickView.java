package com.cs309.nerdsbattle.nerds_battle.battle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * The JoystickView implements a simple joystick for use within other apps.
 *
 * @author Matthew Kelly
 */
public class JoystickView extends View {

    /**
     * X Coordinate of the center of the joystick.
     */
    private float centerX;
    /**
     * Y Coordinate of the center of the joystick.
     */
    private float centerY;
    /**
     * Radius of the larger circle of the joystick.
     */
    private float baseRadius;
    /**
     * Radius of the inner circle of the joystick.
     */
    private float innerRadius;

    /**
     * Current X coordinate.
     */
    private float curX;

    /**
     * Current Y coordinate.
     */
    private float curY;

    /**
     * Whether or not the joystick is moving.
     */
    private boolean isMoving = false;

    /**
     * Listener for the joystick.
     */
    private JoystickListner joystickCallback;

    /**
     * Sets up width and height of the joystick and its inner circle.
     */
    private void setupDimensions() {
        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2;
        baseRadius = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 2;
        innerRadius = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 6;
    }

    /**
     * JoystickView constructor, only necessary constructor, but the other three
     * allow for use within XML files.
     *
     * @param context
     */
    public JoystickView(Context context) {
        super(context);

        setFocusable(true);

    }

    /**
     * Set the listener for this joystick, only 1 allowed.
     * @param listener
     *   object that implements the JoystickListener interface.
     */
    public void setListener(JoystickListner listener) {
        joystickCallback = listener;
    }

    /**
     * JoystickView constructor.
     *
     * @param context
     * @param attributes
     * @param style
     */
    public JoystickView(Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
        setFocusable(true);

    }

    /**
     * JoystickView constructor.
     *
     * @param context
     * @param attributes
     */
    public JoystickView(Context context, AttributeSet attributes) {
        super(context, attributes);
        setFocusable(true);

    }

    /**
     * Draws the joystick on/within the SurfaceView.
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Paint colors = new Paint();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (isMoving) {
            colors.setARGB(127, 50, 50, 50);
        } else {
            colors.setARGB(64, 50, 50, 50);
        }
        canvas.drawCircle(centerX, centerY, baseRadius, colors);
        if (isMoving) {
            colors.setARGB(127, 0, 0, 255);
        } else {
            colors.setARGB(64, 0, 0, 255);
        }
        canvas.drawCircle(curX, curY, innerRadius, colors);

        canvas.save();
    }

    /**
     * Life cycle method that scales the dimensions of the widget.
     * Correctly scales the joystick based upon orientation in the screen.
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        int d = Math.min(measuredHeight, measuredWidth);

        innerRadius = (int) (d * .25);

        setMeasuredDimension(d, d);
        setupDimensions();
    }

    /**
     * Helper method to ensure correct dimensions during onMeasure
     *
     * @param measureSpec
     * @return
     *   measured value
     */
    private int measure(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        }
        else {
            result = specSize;
        }
        return result;
    }

    /**
     * Sets up motion events for the JoystickView class.

     * @param e MotionEvent of the touch event.
     * @return
     *   true if this motion is captured, false otherwise.
     */
    public boolean onTouchEvent(MotionEvent e) {
        //Make sure the view being touched is this joystick.
        //If the finger is being held down.
        if (e.getAction() == e.ACTION_MOVE) {
            isMoving = true;
            //Calculate where the press is, using a right triangle and calculating hypotenuse.
            float displacement = (float) Math.sqrt((Math.pow(e.getX() - centerX, 2)) + Math.pow(e.getY() - centerY, 2));
            //Finger press is within the base circle.
            if (displacement < baseRadius) {
                curX = e.getX();
                curY = e.getY();
                //drawJoystick(e.getX(), e.getY(), 127);
                if (joystickCallback != null) {
                    joystickCallback.onJoystickMoved((e.getX() - centerX) / baseRadius, (e.getY() - centerY) / baseRadius, getId());
                }
            }
            //Finger press is outside the base circle.
            else {
                float ratio = baseRadius / displacement;
                //float y_adj = (e.getY() > centerY) ? -innerRadius : innerRadius;
                //float x_adj = (e.getX() > centerX) ? -innerRadius : innerRadius;
                float constrainedX = (centerX + (e.getX() - centerX) * ratio);
                //float constrainedX = e.getX() - centerX;
                float constrainedY = (centerY + (e.getY() - centerY) * ratio);
                curX = constrainedX;
                curY = constrainedY;
                //drawJoystick(constrainedX, constrainedY, 127);
                if (joystickCallback != null) {
                    joystickCallback.onJoystickMoved((constrainedX - centerX) / baseRadius, (constrainedY - centerY) / baseRadius, getId());
                }
            }
            invalidate();
        }
        //Finger is lifted up.
        else if (e.getAction() == MotionEvent.ACTION_UP) {
            isMoving = false;
            //Return the inner circle back to the center of the joystick.
            curX = centerX;
            curY = centerY;
            //drawJoystick(centerX, centerY, 64);
            if (joystickCallback != null) {
                joystickCallback.onJoystickMoved(0, 0, getId());
            }
            invalidate();
        }

        return true;
    }

    /**
     * Listener for the joystick object.
     * Interface used to get position of the joystick
     */
    public interface JoystickListner {
        /**
         * Method used to get the xPercent and yPercent of the joystick when it is moved.
         *
         * @param xPercent
         *   xPercent of the joystick.
         * @param yPercent
         *   yPercent of the joystick.
         * @param id
         *   id of the joystick.
         */
        void onJoystickMoved(float xPercent, float yPercent, int id);
    }
}

