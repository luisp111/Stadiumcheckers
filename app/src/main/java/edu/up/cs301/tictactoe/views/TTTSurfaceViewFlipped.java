package edu.up.cs301.tictactoe.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;

import edu.up.cs301.tictactoe.views.TTTSurfaceView;

/**
 * A SurfaceView which allows which an animation to be drawn on it by a
 * Animator.
 *
 * @author Steve Vegdahl
 * @version 23 September 2016
 *
 *
 */
public class TTTSurfaceViewFlipped extends TTTSurfaceView {
    //Tag for logging
    private static final String TAG = "TTTSurfaceViewFlipped";
    /*
	 * Instance variables
	 */

    /**
     * Constructor for the TTTSurfaceView class.
     *
     * @param context - a reference to the activity this animation is run under
     */
    public TTTSurfaceViewFlipped(Context context) {
        super(context);
    }// ctor

    /**
     * An alternate constructor for use when a subclass is directly specified
     * in the layout.
     *
     * @param context - a reference to the activity this animation is run under
     * @param attrs   - set of attributes passed from system
     */
    public TTTSurfaceViewFlipped(Context context, AttributeSet attrs) {
        super(context, attrs);
    }// ctor

    /**
     * @return
     * 		the color to paint the tic-tac-toe lines, and the X's and O's
     */
    public int foregroundColor() {
        return Color.RED;
    }

    /**
     * @return
     * 		the color to paint the tic-tac-toe lines, and the X's and O's
     */
    public int backgroundColor() {
        return Color.YELLOW;
    }

    /**
     * Draw a symbol (X or O) on the canvas in a particular location
     *
     * @param g
     *            the graphics object on which to draw
     * @param sym
     *            the symbol to draw (X or O)
     * @param col
     *            the column number of the square on which to draw (0, 1 or 2)
     * @param col
     *            the row number of the square on which to draw (0, 1 or 2)
     */
    protected void drawSymbol(Canvas g, char sym, int col, int row) {

        // draw symbol in inverted position
        super.drawSymbol(g, sym, 2 - col, 2 - row);
    }

    /**
     * maps a point from the canvas' pixel coordinates to "square" coordinates
     *
     * @param x
     * 		the x pixel-coordinate
     * @param y
     * 		the y pixel-coordinate
     * @return
     *		a Point whose components are in the range 0-2, indicating the
     *		column and row of the corresponding square on the tic-tac-toe
     * 		board, or null if the point does not correspond to a square
     */
    public Point mapPixelToSquare(int x, int y) {

        // get point that superclass would have
        Point p = super.mapPixelToSquare(x, y);

        // return "inverted" point
        return p == null ? null : new Point(2-p.x, 2-p.y);
    }

}