package cse340.undo.app;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/* **************************************************************************************************/
/* * DO NOT CHANGE THIS CLASS                                                                      **/
/* **************************************************************************************************/

/**
 * This is an abstract class which serves to provide an interface for a general CircleColorPickerView
 * which is a view that allows users to choose colors and provides a method to register
 * event listeners.
 */
public abstract class AbstractColorPickerView extends AppCompatImageView {
    /* ********************************************************************************************** *
     * All of your interactor's state (the model) and methods that directly manipulate it are here    *
     * This does not include mState which is the literal state of your PPS, which is inherited
     * ********************************************************************************************** */

    /**
     * The current color selected in the ColorPicker. Not necessarily the last
     * color that was sent to the listeners.
     */
    @ColorInt
    protected int mCurrentPickerColor;

    /**
     * Your model should be private to the application, but the application needs a way to set
     * the color of the CircleColorPickerView, so we provide a setter of the color for the app
     */
    public abstract void setPickerColor(@ColorInt int newPickerColor);

    /**
     * The application will get a notification when the color changes, so the application does not
     * need to get the mCurrentPickerColor directly (hence why it is protected). However child classes
     * may need this information
     */
    protected @ColorInt int getPickerColor() { return mCurrentPickerColor; };


    /* ********************************************************************************************** *
     *                               <End of model declarations />
     * ********************************************************************************************** */

    /* ********************************************************************************************** *
     *           Fields. You should not be adding to these                                            *
     * ********************************************************************************************** */
    /** The default color that the ColorPicker should display when launched. */
    @ColorInt
    public static final int DEFAULT_COLOR = Color.RED;

    /* ********************************************************************************************** *
     *                               <End of other fields and constants declarations />
     * ********************************************************************************************** */

    /* ********************************************************************************************** *
     *           Listeners. They will be notified when your model changes                             *
     * ********************************************************************************************** */


    /** A List of registered ColorChangeListeners */
    @NonNull
    private List<ColorChangeListener> mColorChangeListeners;

    /**
     * Class which defines a listener to be called when a new color is selected.
     */
    public interface ColorChangeListener {
        void onColorSelected(@ColorInt int color);
    }

    /**
     * Registers a new listener
     *
     * @param colorChangeListener New listener (should not be null).
     * @throws IllegalArgumentException if colorChangeListener is null
     */
    public final void addColorChangeListener(ColorChangeListener colorChangeListener) {
        if (colorChangeListener == null) {
            throw new IllegalArgumentException("colorChangeListener should never be null");
        }
        mColorChangeListeners.add(colorChangeListener);
    }

    /**
     * Removes a ColorChangeListener, if it exists
     *
     * @param colorChangeListener Listener that should be removed (should not be null).
     * @return True if the listener did exist, and was thus removed. False otherwise.
     */
    public final boolean removeColorChangeListener(ColorChangeListener colorChangeListener) {
        return mColorChangeListeners.remove(colorChangeListener);
    }

    /**
     * Method that will notify all the registered listeners that the color has changed
     * @param color The new color
     */
    protected void invokeColorChangeListeners(int color) {
        mColorChangeListeners.forEach(l -> l.onColorSelected(color));
    }

    /* ********************************************************************************************** *
     *                               <End of listeners code />
     * ********************************************************************************************** */

    /* ********************************************************************************************** *
     *                               Constructor
     * ********************************************************************************************** */

    public AbstractColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mColorChangeListeners = new ArrayList<>();
    }
}
