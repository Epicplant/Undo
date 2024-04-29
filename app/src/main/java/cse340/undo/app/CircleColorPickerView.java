package cse340.undo.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cse340.undo.R;


/**
 * This is a subclass of AbstractColorPickerView, that is, this View implements a ColorPicker.
 *
 * There are several class fields, enums, callback classes, and helper functions which have
 * been implemented for you.
 *
 * PLEASE READ AbstractColorPickerView.java to learn about these.
 */
public class CircleColorPickerView extends ColorPickerView {

    /**
     * Update the local model (color) for this colorpicker view
     *
     * @param x The x location that the user selected
     * @param y The y location that the user selected
     */
    protected void updateModel(float x, float y) {
        // TODxO update the picker's color model based on the location.
        // hint: we give you a very helpful function to call
        float angle = getTouchAngle(x, y);
        int rgb = getColorFromAngle(angle);
        super.setPickerColor(rgb);

    }

    /* ********************************************************************************************** *
     *                               <End of model declarations />
     * ********************************************************************************************** */

    /* ********************************************************************************************** *
     * You may create any constants you wish here.                                                     *
     * You may also create any fields you want, that are not necessary for the state but allow       *
     * for better optimized or cleaner code                                                           *
     * ********************************************************************************************** */
    /** Helper fields for keeping track of view geometry. */
    protected float mCenterX, mCenterY, mRadius;

    /** Ratio between radius of the thumb handle and mRadius, the radius of the wheel. */
    protected static final float RADIUS_TO_THUMB_RATIO = 0.085f;
    private float mCenterCircleRadius, mThumbRadius;

    private android.graphics.Paint mPaintBrush;
    private android.graphics.Paint mPaintBrushThumb;
    private android.graphics.Paint mPaintBrushThumbOpaque;

    /* ********************************************************************************************** *
     *                               <End of other fields and constants declarations />
     * ********************************************************************************************** */

    /**
     * Constructor of the ColorPicker View
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view. This value may be null.
     */
    public CircleColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageResource(R.drawable.color_wheel);

        mPaintBrush = new android.graphics.Paint();
        mPaintBrushThumb = new android.graphics.Paint();
        mPaintBrushThumbOpaque = new android.graphics.Paint();

        int pickedColor = Color.parseColor("#80FFFFFF");

        mPaintBrushThumb.setColor(Color.WHITE);
        mPaintBrushThumbOpaque.setColor(pickedColor);
        //set paintbrush
    }

    /**
     * Draw the ColorPicker on the Canvas
     * @param canvas the canvas that is drawn upon
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint chosenBrush;
        if(mState == mState.SELECTING) {
            chosenBrush = mPaintBrushThumbOpaque;
        } else {
            chosenBrush = mPaintBrushThumb;
        }


        float mAngle = getAngleFromColor(mCurrentPickerColor);
        mPaintBrush.setColor(mCurrentPickerColor);
        canvas.drawCircle(mCenterX, mCenterY,
                mRadius-(mThumbRadius*2), mPaintBrush);


        canvas.drawCircle((float) (mCenterX + mRadius * Math.cos(mAngle) - (mThumbRadius * Math.cos(mAngle))),
                (float) (mCenterY + mRadius * Math.sin(mAngle) - (mThumbRadius * Math.sin(mAngle))),
                mThumbRadius, chosenBrush);
    }

    /**
     * Called when this view should assign a size and position to all of its children.
     * @param changed This is a new size or position for this view
     * @param left Left position, relative to parent
     * @param top Top position, relative to parent
     * @param right Right position, relative to parent
     * @param bottom Bottom position, relative to parent
     */
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);


        float length = Math.min(right-left, bottom-top);
        mRadius = length/2;
        mCenterX = mRadius;
        mCenterY = mRadius;
        mThumbRadius = mRadius*RADIUS_TO_THUMB_RATIO;

    }

    /**
     * Calculate the essential geometry given an event.
     *
     * @param event Motion event to compute geometry for, most likely a touch.
     * @return EssentialGeometry value.
     */
    @Override
    protected EssentialGeometry essentialGeometry(MotionEvent event) {

        float xCoordinate = event.getX();
        float yCoordinate = event.getY();

        if ((xCoordinate - mCenterX) * (xCoordinate - mCenterX) +
                (yCoordinate - mCenterY) * (yCoordinate - mCenterY) <= mRadius * mRadius) {
            return EssentialGeometry.INSIDE;
        } else {
            return EssentialGeometry.OUTSIDE;
        }


    }

    /* ********************************************************************************************** *
     *                               <Helper Functions />
     * ********************************************************************************************** */

    /**
     * Converts an angle on the wheel to a color.
     *
     * @param angle Position on the wheel in radians.
     * @return Color corresponding to that position as RGB.
     * @see #getTouchAngle(float, float)
     */
    @ColorInt
    public static int getColorFromAngle(double angle) {
        float hue = ((float) Math.toDegrees(angle) + 360 + 90) % 360;
        return Color.HSVToColor(new float[]{ hue, 1f, 1f });
    }

    /**
     * Converts from a color to angle on the wheel.
     *
     * @param color RGB color as integer.
     * @return Position of this color on the wheel in radians.
     * @see #getTouchAngle(float, float)
     */
    public static float getAngleFromColor(int color) {
        float[] HSL = new float[3];
        ColorUtils.colorToHSL(color, HSL);
        return (float) Math.toRadians(HSL[0] - 90f);
    }

    /***
     * Calculate the angle of the selection on color wheel given a touch.
     *
     * @param touchX Horizontal position of the touch event.
     * @param touchY Vertical position of the touch event.
     * @return Angle of the touch, in radians.
     */
    protected float getTouchAngle(float touchX, float touchY) {
        // NOTE: This function REQUIRES that you properly use mCenterX, mCenterY, etc.

        // Assumes (for cardinal directions on the color wheel):
        // [ E => 0, South => Pi/2, W => -Pi, N => -Pi/2 ]

        return (float) Math.atan2(touchY - mCenterY, touchX - mCenterX);
    }

}
