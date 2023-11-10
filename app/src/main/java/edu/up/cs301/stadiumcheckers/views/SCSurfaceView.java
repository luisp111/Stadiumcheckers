package edu.up.cs301.stadiumcheckers.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import edu.up.cs301.game.GameFramework.utilities.FlashSurfaceView;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;

/**
 * Stadium Checkers
 *
 * @author Jaden Barker
 * @author James Pham
 * @author Luis Perez
 * @author Mohammad Surur
 * @author Dylan Sprigg
 */
public class SCSurfaceView extends FlashSurfaceView {
    // Tag for logging
    private static final String TAG = "SCSurfaceView";
    // regular, average paint
    private Paint defaultPaint;
    // the game's state
    protected SCState state;

    // bounds for displaying the view
    // base the display off of these values instead of hardcoding positions
    // so as to allow for zooming in the future perhaps
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;

    // actual screen measurements
    private int screenX;
    private int screenY;

    /**
     * Constructor for the TTTSurfaceView class.
     *
     * @param context - a reference to the activity this animation is run under
     */
    public SCSurfaceView(Context context) {
        super(context);
        init();
    }

    /**
     * An alternate constructor for use when a subclass is directly specified
     * in the layout.
     *
     * @param context - a reference to the activity this animation is run under
     * @param attrs   - set of attributes passed from system
     */
    public SCSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setState(SCState state) {
        this.state = state;
    }

    @Override
    public void onDraw(Canvas canvas) {
        /*
        if (state == null) {
            return;
        }
        */
        canvas.drawRect(0, 0, 1000, 1000, defaultPaint);
        //canvas.drawText("doing fine", 100, 100, defaultPaint);
    }

    private void init() {
        defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultPaint.setColor(0xFFFFA500);
        defaultPaint.setStyle(Paint.Style.FILL);
    }
}
