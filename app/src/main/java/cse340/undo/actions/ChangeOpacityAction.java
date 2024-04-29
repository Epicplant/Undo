package cse340.undo.actions;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import cse340.undo.app.DrawingView;

/**
 * Reversible action which changes the opacity of the DrawingView's paint.
 */
public class ChangeOpacityAction extends AbstractReversibleAction {

    /** The opacity that this action changes the current paint to. */
    protected final int mOpacity;

    /** The opacity that this action changes the current paint from. */
    protected int mPrev;

    /**
     * Creates an action that changes the paint opacity.
     *
     * @param opacity New opacity for DrawingView paint.
     */
    public ChangeOpacityAction(int opacity) {
        this.mOpacity = opacity;
    }

    /** @inheritDoc */
    @Override
    public void doAction(DrawingView view) {
        super.doAction(view);
        Paint cur = view.getCurrentPaint();
        mPrev = cur.getAlpha();
        cur.setAlpha(mOpacity);
    }

    /** @inheritDoc */
    @Override
    public void undoAction(DrawingView view) {
        super.undoAction(view);
        view.getCurrentPaint().setAlpha(mPrev);
    }

    @NonNull
    @Override
    public String toString() {
        return "Opacity is currently " + mOpacity;
    }
}
