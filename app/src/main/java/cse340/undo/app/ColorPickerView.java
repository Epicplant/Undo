package cse340.undo.app;


import android.content.Context;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * ColorPickerView serves to provide an interface for a general CircleColorPickerView
 * which is a view that allows users to choose colors and provides a method to register
 * event listeners.
 */
public abstract class ColorPickerView extends AbstractColorPickerView {
    /* ********************************************************************************************** *
     * All of your applications state (the model) and methods that directly manipulate it are here    *
     * This does not include mState which is the literal state of your PPS, which is inherited
     * ********************************************************************************************** */

    /**
     * Your model is private to the application, but the application needs a way to set
     * the color of the CircleColorPickerView, so we provide a setter of the color for the app
     */
    public void setPickerColor(@ColorInt int newPickerColor) {
        mCurrentPickerColor = newPickerColor;
        invalidate();
    }

    /**
     * Update the local model (color) for this colorpicker view
     *
     * @param x The x location that the user selected
     * @param y The y location that the user selected
     */
    abstract protected void updateModel(float x, float y);


    /* ********************************************************************************************** *
     *                               <End of model declarations />
     * ********************************************************************************************** */


    /** Used the state to keep track of the PPS state for ColorPicker. */
    protected enum State { START, SELECTING }
    protected State mState = State.START;

    /** Used the EssentialGeometry to keep track of whether the pointer is inside the color picker. */
    protected enum EssentialGeometry { INSIDE, OUTSIDE }

    /**
     * Calculate the essential geometry given an event.
     *
     * @param event Motion event to compute geometry for, most likely a touch.
     * @return EssentialGeometry value.
     */
    protected EssentialGeometry essentialGeometry(MotionEvent event) {
        return EssentialGeometry.INSIDE;
    }

    /* ********************************************************************************************** *
     *                               <End of other fields and constants declarations />
     * ********************************************************************************************** */

    /* ********************************************************************************************** *
     *                               Constructor
     * ********************************************************************************************** */

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* ********************************************************************************************** *
     *                               Event Handling
     * ********************************************************************************************** */



    /**
     * This method should only be implemented once, in ColorPickerVIew
     * Your CircleColorPickerView and MyColorPickerView will both use this same method
     *
     * @param event The event that is passed in
     * @return Whether you consumed it or not
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        EssentialGeometry geometry = essentialGeometry(event);

        // Make sure to make calls to updateModel, invalidate, and invokeColorChangeListeners
        // Do NOT invalidate when unnecessary (should only be called when the picker's color model
        // changes)

        float xCord = event.getX();
        float yCord = event.getY();

        switch(mState){
            case START:
                //Button press inside of a color picker
                if(event.getAction() == MotionEvent.ACTION_DOWN && geometry == geometry.INSIDE) {
                    updateModel(xCord, yCord);
                    mState = mState.SELECTING;
                    invalidate();
                    return true;
                }
            case SELECTING:
                //Ending button press on color picker
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    invokeColorChangeListeners(mCurrentPickerColor);
                    mState = mState.START;
                    invalidate();
                    return true;
                    //Moving cursor/button press inside of color picker
                } else if(event.getAction() == MotionEvent.ACTION_MOVE && geometry == geometry.INSIDE) {
                    updateModel(xCord, yCord);
                    invalidate();
                    return true;
                }
            default: break;
        }

        return false;
    }
}
