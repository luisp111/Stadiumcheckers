package edu.up.cs301.stadiumcheckers.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.HashMap;
import java.util.Random;

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
    // the game's state
    protected SCState state;

    // bounds for displaying the view
    // base the display off of these values instead of hardcoding positions
    // so as to allow for zooming in the future perhaps
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int widthH;
    private int heightH;

    // actual screen measurements
    private int screenX;
    private int screenY;

    private Paint ringPaint;
    private Paint ringPaint2;
    private Paint slotPaint;
    private Paint[] colorPaints;
    private Paint[] colorPaints2;
    private Paint whitePaint;
    private Paint blackPaint;
    private final int[] ringSlots = {4, 5, 4, 6, 5, 6, 7, 6};
    private final HashMap<Point, Integer> positions = new HashMap<>();
    private final int[] securedMarbles = new int[4];
    private boolean ringB = false;

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

    /**
     * callback method, called whenever it's time to redraw
     * frame
     *
     * @param canvas
     * 		the canvas to draw on
     */
    @Override
    public void onDraw(Canvas canvas) {
        updateDimensions(canvas);

        /*
        if (state == null) {
            return;
        }
        */

        canvas.drawRect(0, 0, 250, 50, whitePaint);
        canvas.drawText("Turn 36: RED", 10, 38, colorPaints2[0]);

        int w = (maxX - minX);
        int h = (maxY - minY);
        int rBase = (Math.min(w, h) * 3) / 8;
        ringB = false;

        for (int i = 0; i < 4; i++) {
            canvas.drawRect(w - (rBase / 8f) * (1.5f * i), h - rBase / 1.6f,
                    w - (rBase / 8f) * (1.5f * i + 1), h, colorPaints[i]);
            for (int j = 0; j < securedMarbles[i]; j++) {
                drawMarble(w - (rBase / 8f) * (1.5f * i + 0.5f),
                        h - (rBase / 8f) * (j + 0.5f), i, rBase, canvas);
            }
        }

        drawOuterRing(canvas, rBase);
        for (int i = 7; i >= 1; i--) {
            int r = rBase * (i + 1) / 8;
            drawRing(canvas, r, i, rBase);
        }

        drawOuterRingMarbles(canvas, rBase);

        drawInnerRing(canvas, rBase / 8f, rBase);
    }

    private void drawRing(Canvas canvas, int r, int id, float rBase) {
        if (ringB) {
            canvas.drawCircle(widthH, heightH, r, ringPaint2);
        } else {
            canvas.drawCircle(widthH, heightH, r, ringPaint);
        }

        RectF oval = new RectF();
        oval.set(widthH - r, heightH - r, widthH + r, heightH + r);
        float random = (float) Math.random() * 360;
        float angle = 360f / ringSlots[id];

        float sweep = 5 * rBase / r;
        for (int i = 0; i < ringSlots[id]; i++) {
            float mA = (angle * i) + random;
            canvas.drawArc(oval, mA, sweep, true, slotPaint);

            Integer point = positions.get(new Point(id, i));
            if (point == null) {
                continue;
            }

            mA = (float) ((mA + sweep / 2) * (Math.PI / 180));
            float mR = rBase * (id + 0.5f) / 8;
            float x = widthH + (float) (mR * Math.cos(mA));
            float y = heightH + (float) (mR * Math.sin(mA));
            drawMarble(x, y, point, rBase, canvas);
        }

        ringB = !ringB;
    }

    private void drawOuterRing(Canvas canvas, float rBase) {
        int j = 0;
        double angleBase = Math.PI / 10;
        for (int c = 0; c < 4; c++) {
            for (int i = 0; i < 5; i++) {
                double angle = angleBase * j + angleBase / 2;
                float x = widthH + (float) (rBase * Math.sin(angle) * 1.02);
                float y = heightH + (float) (rBase * Math.cos(angle) * 1.02);
                canvas.drawCircle(x, y, rBase / 15, colorPaints[c]);
                j++;
            }
        }
    }

    private void drawOuterRingMarbles(Canvas canvas, float rBase) {
        int j = 0;
        double angleBase = Math.PI / 10;
        for (int c = 0; c < 4; c++) {
            for (int i = 0; i < 5; i++) {
                Integer point = positions.get(new Point(0, j));
                if (point == null) {
                    j++;
                    continue;
                }

                double angle = angleBase * j + angleBase / 2;
                float x = widthH + (float) (rBase * Math.sin(angle) * 1.02);
                float y = heightH + (float) (rBase * Math.cos(angle) * 1.02);
                drawMarble(x, y, c, rBase, canvas);
                j++;
            }
        }
    }

    private void drawInnerRing(Canvas canvas, float r, float rBase) {
        double angleBase = Math.PI / 2;

        canvas.drawCircle(widthH, heightH, r, ringPaint2);

        Path circlePath = new Path();
        circlePath.addCircle(widthH, heightH, r, Path.Direction.CW);
        canvas.clipPath(circlePath);

        for (int i = 0; i < 4; i++) {
            double angle = angleBase * i + angleBase / 2;
            canvas.drawCircle(widthH + (float) (r * Math.sin(angle) * 0.8),
                    heightH + (float) (r * Math.cos(angle) * 0.8), rBase / 15, colorPaints[i]);
        }
    }

    private void drawMarble(float x, float y, int team, float rBase, Canvas canvas) {
        if (team == 0) {
            canvas.drawCircle(x, y, rBase / 20f, whitePaint);
        } else {
            canvas.drawCircle(x, y, rBase / 20f, blackPaint);
        }
        canvas.drawCircle(x, y, rBase / 24f, colorPaints2[team]);
    }

    /**
     * update the instance variables that relate to the drawing surface
     *
     * @param canvas
     * 		an object that references the drawing surface
     */
    private void updateDimensions(Canvas canvas) {
        int screenX = canvas.getWidth();
        int screenY = canvas.getHeight();

        if (this.screenX == screenX && this.screenY == screenY) {
            return;
        }

        this.screenX = screenX;
        this.screenY = screenY;

        minX = 0;
        minY = 0;
        maxX = screenX;
        maxY = screenY;
        widthH = screenX / 2;
        heightH = screenY / 2;
    }

    private void init() {
        setWillNotDraw(false);

        // TODO: replace randomized values with values from state
        // TODO: probably find a neat way of configuring paints?

        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setColor(0xFFFFD700);
        ringPaint.setStyle(Paint.Style.FILL);

        ringPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint2.setColor(0xFF00D7FF);
        ringPaint2.setStyle(Paint.Style.FILL);

        slotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        slotPaint.setColor(0xFFFFA500);
        slotPaint.setStyle(Paint.Style.FILL);

        Paint redPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint.setColor(0xFFEF4020);
        redPaint.setStyle(Paint.Style.FILL);

        Paint greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setColor(0xFF20EF40);
        greenPaint.setStyle(Paint.Style.FILL);

        Paint yellowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowPaint.setColor(0xFFF0EF40);
        yellowPaint.setStyle(Paint.Style.FILL);

        Paint bluePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint.setColor(0xFF4020EF);
        bluePaint.setStyle(Paint.Style.FILL);

        colorPaints = new Paint[]{redPaint, yellowPaint, greenPaint, bluePaint};

        Paint redPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint2.setColor(0xFFFF1010);
        redPaint2.setStyle(Paint.Style.FILL);
        redPaint2.setTextSize(40);

        Paint greenPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint2.setColor(0xFF10FF10);
        greenPaint2.setStyle(Paint.Style.FILL);

        Paint yellowPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowPaint2.setColor(0xFFF0F010);
        yellowPaint2.setStyle(Paint.Style.FILL);

        Paint bluePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint2.setColor(0xFF1010FF);
        bluePaint2.setStyle(Paint.Style.FILL);

        colorPaints2 = new Paint[]{redPaint2, yellowPaint2, greenPaint2, bluePaint2};

        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(0xFFFFFFFF);
        whitePaint.setStyle(Paint.Style.FILL);

        blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setColor(0xFF000000);
        blackPaint.setStyle(Paint.Style.FILL);

        Random random = new Random();
        securedMarbles[0] = 0;
        for (int i = 1; i < 4; i++) {
            securedMarbles[i] = random.nextInt(5);
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5 - securedMarbles[i]; j++) {
                Point p;
                do {
                    int ring = random.nextInt(8);

                    int boundMin = 0;
                    int boundMax;
                    if (ring == 0) {
                        boundMin = i * 5;
                        boundMax = (i + 1) * 5;
                    } else {
                        boundMax = ringSlots[ring - 1];
                    }

                    int slot = random.nextInt(boundMax - boundMin) + boundMin;
                    p = new Point(ring, slot);
                } while (positions.putIfAbsent(p, i) != null);
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
