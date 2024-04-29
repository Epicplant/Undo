package cse340.undo.app;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintSet;
import android.util.DisplayMetrics;
import android.view.View;

import cse340.undo.R;
import cse340.undo.actions.ChangeColorAction;
import cse340.undo.actions.ChangeOpacityAction;
import cse340.undo.actions.ChangeThicknessAction;
import cse340.undo.actions.AbstractReversibleAction;

public class ReversibleDrawingActivity extends AbstractReversibleDrawingActivity {
    private static final int DEFAULT_COLOR = Color.RED;
    private static final int DEFAULT_THICKNESS = 10;

    /** List of menu item FABs for thickness menu. */
    @IdRes
    private static final int[] THICKNESS_MENU_ITEMS = { R.id.fab_thickness_0,
        R.id.fab_thickness_10, R.id.fab_thickness_20, R.id.fab_thickness_30
    };

    /** List of menu item FABs for color menu. */
    @IdRes
    private static final int[] COLOR_MENU_ITEMS = {
        R.id.fab_red, R.id.fab_blue, R.id.fab_green
    };

    /** List of menu item FABs for opacity menu. */
    @IdRes
    private static final int[] OPACITY_MENU_ITEMS = {
            R.id.fab_opacity_0_percent, R.id.fab_opacity_25_percent, R.id.fab_opacity_50_percent,
            R.id.fab_opacity_75_percent, R.id.fab_opacity_100_percent
    };


    /** State variables used to track whether menus are open. */
    private boolean isThicknessMenuOpen;
    private boolean isColorMenuOpen;
    private boolean isOpacityMenuOpen;

    @SuppressLint("PrivateResource")
    private int mMiniFabSize;

    /** Place to store CircleColorPickerView*/
    protected CircleColorPickerView mCircleColorPickerView;

    /** Color Change Listener*/
    private  AbstractColorPickerView.ColorChangeListener mListenerOne;

    /**
     * Creates a new AbstractReversibleDrawingActivity with the default history limit.
     */
    public ReversibleDrawingActivity() {
        super();
    }

    /**
     * Creates a new AbstractReversibleDrawingActivity with the given history limit.
     *
     * @param history Maximum number of history items to maintain.
     */
    public ReversibleDrawingActivity(int history) {
        super(history);
    }

    @Override
    @SuppressLint("PrivateResource")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We are providing draw with a default thickness and color for the first line
        Paint p = mDrawingView.getCurrentPaint();
        p.setColor(DEFAULT_COLOR);
        p.setStrokeWidth(DEFAULT_THICKNESS);
        mDrawingView.setCurrentPaint(p);
        mMiniFabSize = getResources().getDimensionPixelSize(R.dimen.design_fab_size_mini);

        // TODO: initialize color picker and register color change listener

        // Add thickness and color menus to the ConstraintLayout. Pass in onColorMenuSelected
        // and onThicknessMenuSelected as the listeners for these menus

        mCircleColorPickerView = findViewById(R.id.Circle_Color_Picker_View);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mCircleColorPickerView.setY(getResources().getDimension(R.dimen.color_picker_y_offset));
        mCircleColorPickerView.setX(displayMetrics.widthPixels/4);
        mCircleColorPickerView.setPickerColor(DEFAULT_COLOR);

        mListenerOne = new AbstractColorPickerView.ColorChangeListener() {
            @Override
            public void onColorSelected(int color) {
                ChangeColorAction colorAction = new ChangeColorAction(color);
                doAction(colorAction);
            }
        };


       mCircleColorPickerView.addColorChangeListener(mListenerOne);

        addCollapsibleMenu(R.layout.color_menu, ConstraintSet.BOTTOM, ConstraintSet.END, COLOR_MENU_ITEMS, this::onColorMenuSelected);
        // TODO: you may have to edit this after integrating the color picker
        findViewById(R.id.fab_color).setOnClickListener((v) -> {

            enableCollapsibleMenu(R.id.fab_thickness, THICKNESS_MENU_ITEMS, isColorMenuOpen);
            enableCollapsibleMenu(R.id.fab_opacity, OPACITY_MENU_ITEMS, isColorMenuOpen);
            enableFAB(R.id.fab_undo, isColorMenuOpen);
            enableFAB(R.id.fab_redo, isColorMenuOpen);
            if(isColorMenuOpen) {
                mCircleColorPickerView.setFocusable(false);
                mCircleColorPickerView.setVisibility(View.INVISIBLE);
                isColorMenuOpen = false;
           } else {
                mCircleColorPickerView.setFocusable(true);
                mCircleColorPickerView.setVisibility(View.VISIBLE);
                isColorMenuOpen = true;
            }
        });

        // Only draw a stroke when none of the collapsible menus are open
        mDrawingView.setOnTouchListener((view, event) -> {
            if (isThicknessMenuOpen) {
                isThicknessMenuOpen = toggleMenu(THICKNESS_MENU_ITEMS, isThicknessMenuOpen);
                enableCollapsibleMenu(R.id.fab_color, COLOR_MENU_ITEMS, !isThicknessMenuOpen);
                enableCollapsibleMenu(R.id.fab_opacity, OPACITY_MENU_ITEMS, !isThicknessMenuOpen);
                return true;
            } else if (isColorMenuOpen) {
                isColorMenuOpen = toggleMenu(COLOR_MENU_ITEMS, isColorMenuOpen);
                enableCollapsibleMenu(R.id.fab_thickness, THICKNESS_MENU_ITEMS, !isColorMenuOpen);
                enableCollapsibleMenu(R.id.fab_opacity, OPACITY_MENU_ITEMS, !isColorMenuOpen);
                mCircleColorPickerView.setFocusable(false);
                mCircleColorPickerView.setVisibility(View.INVISIBLE);
                return false;
            } else if(isOpacityMenuOpen) {
                isOpacityMenuOpen = toggleMenu(OPACITY_MENU_ITEMS, isOpacityMenuOpen);
                enableCollapsibleMenu(R.id.fab_color, COLOR_MENU_ITEMS, !isOpacityMenuOpen);
                enableCollapsibleMenu(R.id.fab_thickness, THICKNESS_MENU_ITEMS, !isOpacityMenuOpen);
                return true;
            } else {
                return mDrawingView.onTouchEvent(event);
            }
        });

        registerActionListener(this::onAction);
        registerActionUndoListener(this::onActionUndo);

        addCollapsibleMenu(R.layout.thickness_menu, ConstraintSet.BOTTOM, ConstraintSet.END, THICKNESS_MENU_ITEMS,
                this::onThicknessMenuSelected);
        findViewById(R.id.fab_thickness).setOnClickListener((v) ->{
            enableCollapsibleMenu(R.id.fab_color, COLOR_MENU_ITEMS, isThicknessMenuOpen);
            enableCollapsibleMenu(R.id.fab_opacity, OPACITY_MENU_ITEMS, isThicknessMenuOpen);
            isThicknessMenuOpen = toggleMenu(THICKNESS_MENU_ITEMS, isThicknessMenuOpen);
        });

        addCollapsibleMenu(R.layout.opacity_menu, ConstraintSet.BOTTOM, ConstraintSet.END,
                OPACITY_MENU_ITEMS, this::onOpacityMenuSelected);

        findViewById(R.id.fab_opacity).setOnClickListener((view) ->{
            view.announceForAccessibility("Opacity Menu Toggled");
            enableCollapsibleMenu(R.id.fab_color, COLOR_MENU_ITEMS, isOpacityMenuOpen);
            enableCollapsibleMenu(R.id.fab_thickness, THICKNESS_MENU_ITEMS, isOpacityMenuOpen);
            isOpacityMenuOpen = toggleMenu(OPACITY_MENU_ITEMS, isOpacityMenuOpen);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deregisterActionListener(this::onAction);
        deregisterActionUndoListener(this::onActionUndo);
        // TODO: deregister the color change listener
        mCircleColorPickerView.removeColorChangeListener(mListenerOne);
    }

    private void onAction(AbstractReversibleAction action) {
        if (action instanceof ChangeColorAction) {
            @ColorInt int currColor = mDrawingView.getCurrentPaint().getColor();
            // TODO: update the color of the color picker if needed
           if(mCircleColorPickerView.getPickerColor() != currColor) {
               mCircleColorPickerView.setPickerColor(currColor);
           }
        }
    }

    private void onActionUndo(AbstractReversibleAction action) {
        if (action instanceof ChangeColorAction) {
            @ColorInt int currColor = mDrawingView.getCurrentPaint().getColor();
            // TODO: update the color of the color picker if needed
            if(mCircleColorPickerView.getPickerColor() != currColor) {
                mCircleColorPickerView.setPickerColor(currColor);
            }
        }
    }

    /**
     * Callback for creating an AbstractAction when the user changes the color.
     *
     * @param view The FAB the user clicked on
     */
    private void onColorMenuSelected(View view) {
        switch (view.getId()) {
            case R.id.fab_red:
                doAction(new ChangeColorAction(Color.RED));
                break;
            case R.id.fab_blue:
                doAction(new ChangeColorAction(Color.BLUE));
                break;
            case R.id.fab_green:
                doAction(new ChangeColorAction(Color.GREEN));
                break;
        }

        // Close the menu.
        isColorMenuOpen = toggleMenu(COLOR_MENU_ITEMS, isColorMenuOpen);
        enableCollapsibleMenu(R.id.fab_thickness, THICKNESS_MENU_ITEMS, !isColorMenuOpen);
        enableCollapsibleMenu(R.id.fab_opacity, OPACITY_MENU_ITEMS, !isColorMenuOpen);
    }

    /**
     * Callback for creating an action when the user changes the thickness.
     *
     * TODO: You will need to modify this to add a new thickness FAB
     * @param view The FAB the user clicked on.
     */
    private void onThicknessMenuSelected(View view) {
        switch (view.getId()) {
            case R.id.fab_thickness_0:
                doAction(new ChangeThicknessAction(0));
                break;
            case R.id.fab_thickness_10:
                doAction(new ChangeThicknessAction(10));
                break;
            case R.id.fab_thickness_20:
                doAction(new ChangeThicknessAction(20));
                break;
            case R.id.fab_thickness_30:
                doAction(new ChangeThicknessAction(30));
                break;
        }

        // Close the menu.
        isThicknessMenuOpen = toggleMenu(THICKNESS_MENU_ITEMS, isThicknessMenuOpen);
        enableCollapsibleMenu(R.id.fab_color, COLOR_MENU_ITEMS, !isThicknessMenuOpen);
        enableCollapsibleMenu(R.id.fab_opacity, OPACITY_MENU_ITEMS, !isThicknessMenuOpen);
    }

    /**
     * Toggles a collapsible menu. That is, if it's open, it closes it. If it's closed, it opens it.
     *
     * @param items List of IDs of items in the menu, all FABs.
     * @param open  Current state of the menu.
     * @return True if the menu is now open, false otherwise.
     */
    private boolean toggleMenu(@IdRes int[] items, boolean open) {
        enableFAB(R.id.fab_undo, open);
        enableFAB(R.id.fab_redo, open);
        if (!open) {
            for (int i = 0; i < items.length; i++) {
                View view = findViewById(items[i]);
                view.setFocusable(true);
                view.animate()
                    .translationY(-3 * mMiniFabSize * (i + 1.5f) / 2.5f)
                    .alpha(1)
                    .withEndAction(() -> view.setClickable(true));

            }
            return true;
        } else {
            for (int item : items) {
                View view = findViewById(item);
                view.setClickable(false);
                view.animate().translationY(0).alpha(0);
                findViewById(item).setFocusable(false);
            }
            return false;
        }
    }

    /**
     * Disables and enables collapsible menu FABs
     *
     * @param menuId The resID of the menu activation FAB
     * @param menuItems An array of resIDs for the menu item FABs
     * @param enabled true if the menu should be enabled, false if the menu should be disabled
     */
    private void enableCollapsibleMenu(@IdRes int menuId, @IdRes int[] menuItems, boolean enabled) {
        enableFAB(menuId, enabled);
        for (@IdRes int item : menuItems) {
            findViewById(item).setEnabled(enabled);
        }
    }

    /**
     * Disables and enables FABs
     *
     * @param buttonId the resID of the FAB
     * @param enabled true if the button should be enabled, false if the button should be disabled
     */
    private void enableFAB(@IdRes int buttonId, boolean enabled) {
        findViewById(buttonId).setEnabled(enabled);
        findViewById(buttonId).setBackgroundTintList(ColorStateList.valueOf(enabled ?
                getResources().getColor(R.color.colorAccent) : Color.LTGRAY));
    }


    private void onOpacityMenuSelected(View view) {
        switch (view.getId()) {
            case R.id.fab_opacity_100_percent:
                view.announceForAccessibility("Opacity set to 100 percent");
                doAction(new ChangeOpacityAction(0));
                break;
            case R.id.fab_opacity_75_percent:
                view.announceForAccessibility("Opacity set to 75 percent");
                doAction(new ChangeOpacityAction(64));
                break;
            case R.id.fab_opacity_50_percent:
                view.announceForAccessibility("Opacity set to 50 percent");
                doAction(new ChangeOpacityAction(128));
                break;
            case R.id.fab_opacity_25_percent:
                view.announceForAccessibility("Opacity set to 25 percent");
                doAction(new ChangeOpacityAction(192));
                break;
            case R.id.fab_opacity_0_percent:
                view.announceForAccessibility("Opacity set to 0 percent");
                doAction(new ChangeOpacityAction(255));
                break;
        }

        // Close the menu.
        isOpacityMenuOpen = toggleMenu(OPACITY_MENU_ITEMS, isOpacityMenuOpen);
        enableCollapsibleMenu(R.id.fab_color, COLOR_MENU_ITEMS, !isOpacityMenuOpen);
        enableCollapsibleMenu(R.id.fab_thickness, THICKNESS_MENU_ITEMS, !isOpacityMenuOpen);
    }
}