package edu.up.cs301.stadiumcheckers.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;

import edu.up.cs301.game.GameFramework.utilities.FlashSurfaceView;
import edu.up.cs301.game.GameFramework.utilities.Logger;
import edu.up.cs301.stadiumcheckers.Position;
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
    // tracker for the positions of a player's 5 balls
    private HashMap<Integer, Point> positions = new HashMap<>();
    // id of selected ball if there is one
    // id is based on the order of the ball returned from state.getPositionsFromTeam()
    private int selectedBall = -1;
    private float selectedBallMag;
    private float selectedBallAng;

    // positions of clockwise/counterclock arrows
    private Point cPos;
    private Point ccPos;

    // bounds for displaying the view
    // base the display off of these values instead of hardcoding positions
    // so as to allow for zooming in the future perhaps
    private int minX;
    private int maxX = 0;
    private int minY;
    private int maxY;

    // the projected width of the board
    private int rBase;

    // half of the bound width and height
    // for deducing the center position
    private int widthH;
    private int heightH;

    // fun paints
    private Paint ringPaint;
    private Paint ringPaint2;
    private Paint slotPaint;
    private Paint whitePaint;
    private Paint blackPaint;
    private Paint[] colorPaints;
    private Paint[] colorPaints2;

    // for making the board have alternating colors
    private boolean ringB = false;

    // for the status on the top left
    private String[] teamNames;
    private String statusText;

    // team color that should have highlighted balls
    // -1 == no balls highlighted
    private int colorHighlight = -1;
    // whether you can move or reset on your turn
    private boolean resetMode = false;

    // variables for animating
    private final Float[] animAngles = new Float[9];
    private boolean keepUpdating = false;
    private SCSurfaceViewThread thread;

    /**
     * Constructor for the SCSurfaceView class.
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

    public HashMap<Integer, Point> getPositions() {
        return positions;
    }

    public int getSelectedBall() {
        return selectedBall;
    }

    public void setSelectedBall(int selectedBall) {
        this.selectedBall = selectedBall;
    }

    public SCState getState() {
        return state;
    }

    public int getRBase() {
        return rBase;
    }

    public void setColorHighlight(int colorHighlight) {
        this.colorHighlight = colorHighlight;
    }

    public Point getcPos() {
        return cPos;
    }

    public Point getCcPos() {
        return ccPos;
    }

    public void setResetMode(boolean resetMode) {
        this.resetMode = resetMode;
    }

    public boolean getResetMode() {
        return resetMode;
    }

    public boolean shouldKeepUpdating() {
        return keepUpdating;
    }

    public void setScreenSize(int width, int height) {
        minX = 0;
        minY = 0;
        maxX = width;
        maxY = height;
        widthH = width / 2;
        heightH = height / 2;
    }

    /**
     * Sets a new state to display
     *
     * @param state the state to display
     */
    @SuppressLint("DefaultLocale")
    public void setState(SCState state) {
        selectedBall = -1;
        colorHighlight = -1;
        if (teamNames != null) {
            statusText = String.format("TURN %d: %s",
                    state.getTurnCount(), teamNames[state.getCurrentTeamTurn()]);
        }
        if (animAngles[0] == null) {
            for (int i = 0; i < state.getRingCount(); i++) {
                animAngles[i] = state.getRingAngle(i);
            }
        }
        this.state = state;
    }

    /**
     * sets the team names
     *
     * @param teamNames the name of each team member
     */
    public void setTeamNames(String[] teamNames) {
        this.teamNames = Arrays.copyOf(teamNames, teamNames.length);
    }

    /**
     * called to cause a redraw
     */
    @Override
    public void invalidate() {
        super.invalidate();
        if (thread == null || !thread.isAlive()) {
            keepUpdating = true;
            thread = new SCSurfaceViewThread(this);
            thread.start();
        }
    }

    /**
     * callback method, called whenever it's time to redraw
     *
     * @param canvas the canvas to draw on
     */
    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (state == null) {
            Log.d(TAG, "onDraw: Tried rendering w/o a state??");
            return;
        }

        updateDimensions(canvas);
        int w = (maxX - minX);
        int h = (maxY - minY);
        rBase = (Math.min(w, h) * 3) / 8;
        ringB = false;

        // hardcoded :( info display on top left
        if (teamNames != null) {
            canvas.drawRect(0, 0, 450, 50, whitePaint);
            canvas.drawCircle(450, 0, 50, whitePaint);
            canvas.drawText(statusText, 10, 38, colorPaints2[state.getCurrentTeamTurn()]);
        }

        drawSecuredMarbles(canvas, w, h);

        // draw marbles needing to be reset
        double angleBase = Math.PI / 10;
        for (int i = 0; i < 4; i++) {
            int k = 0;
            Position[] teamPos = state.getPositionsFromTeam(i);
            for (int j = 0; j < 5; j++) {
                if (teamPos[j].getRing() != -2) {
                    continue;
                }

                double angle = angleBase * k / 3 + angleBase * i * 5;
                float x = widthH + (float) (rBase * Math.sin(angle) * 1.24);
                float y = heightH + (float) (rBase * Math.cos(angle) * 1.24);
                drawMarble(x, y, i, -1, canvas);
                k++;
            }
        }

        drawOuterRing(canvas);

        // draw rings
        int end = 1;
        if (Logger.getDebugValue()) {
            end = 0;
        }
        for (int i = (state.getRingCount() - 2); i >= end; i--) {
            int r = rBase * (i + 1) / 8;
            drawRing(canvas, r, 8 - i);
        }

        drawOuterRingMarbles(canvas);

        // arrows for selecting rotation
        if (selectedBall >= 0) {
            float shift = 45f / selectedBallMag;

            float rX = widthH + (float) (selectedBallMag * Math.cos(selectedBallAng + shift));
            float rY = heightH + (float) (selectedBallMag * Math.sin(selectedBallAng + shift));
            drawArrow(rX, rY, selectedBallAng + (float) (Math.PI / 2) + shift, canvas);
            cPos = new Point((int) rX, (int) rY);

            float lX = widthH + (float) (selectedBallMag * Math.cos(selectedBallAng - shift));
            float lY = heightH + (float) (selectedBallMag * Math.sin(selectedBallAng - shift));
            drawArrow(lX, lY, selectedBallAng - (float) (Math.PI / 2) - shift, canvas);
            ccPos = new Point((int) lX, (int) lY);
        }

        if (!Logger.getDebugValue()) {
            drawInnerRing(canvas, rBase / 8f);
        }
    }

    /**
     * draws the secured marbles of each team
     *
     * @param canvas canvas to draw on
     * @param w width of drawable area
     * @param h height of drawable area
     */
    private void drawSecuredMarbles(Canvas canvas, int w, int h) {
        float top = h - rBase / 1.6f + rBase / 16f;
        for (int i = 0; i < 4; i++) {
            float x = w - (rBase / 8f) * (1.5f * i + 1);

            canvas.drawRect(w - (rBase / 8f) * (1.5f * i + 0.5f), top,
                    w - (rBase / 8f) * (1.5f * i + 1.5f), h, colorPaints[i]);
            canvas.drawCircle(x, top,
                    (rBase / 16f), colorPaints[i]);

            int k = 0;
            Position[] teamPos = state.getPositionsFromTeam(i);
            for (int j = 0; j < 5; j++) {
                if (teamPos[j].getRing() != -1) {
                    continue;
                }
                drawMarble(x, h - (rBase / 8f) * (k + 0.5f), i, -1, canvas);
                k++;
            }
        }
    }

    /**
     * draws a ring based on state data
     *
     * @param canvas the canvas to draw on
     * @param r      the radius to draw the ring
     * @param ring   the actual ring value this ring represents
     */
    private void drawRing(Canvas canvas, int r, int ring) {
        if (ringB) {
            canvas.drawCircle(widthH, heightH, r, ringPaint2);
        } else {
            canvas.drawCircle(widthH, heightH, r, ringPaint);
        }

        RectF oval = new RectF();
        oval.set(widthH - r, heightH - r, widthH + r, heightH + r);

        float angle = animAngles[ring];
        int slotCount = state.getRingSlotCount(ring);
        float sector = 360f / slotCount;
        float sweep = 5f * rBase / r;
        for (int i = 0; i < slotCount; i++) {
            float mA = sector * (-i) - angle - sweep / 2 + 126f; // fun magic number to align rings

            if (Logger.getDebugValue()) {
                if (i == 0) {
                    canvas.drawArc(oval, mA, sweep, true, blackPaint);
                } else if (i == 1) {
                    canvas.drawArc(oval, mA, sweep, true, whitePaint);
                } else {
                    canvas.drawArc(oval, mA, sweep, true, slotPaint);
                }
            } else {
                canvas.drawArc(oval, mA, sweep, true, slotPaint);
            }

            Position pos = new Position(ring, i);
            int team = state.getTeamFromPosition(pos);
            if (team == -1) {
                continue;
            }

            mA = (float) ((mA + sweep / 2) * (Math.PI / 180));

            int num = 0;
            if (colorHighlight == team || Logger.getDebugValue()) {
                Position[] positions = state.getPositionsFromTeam(team);
                for (int j = 0; j < 5; j++) {
                    if (positions[j].equals(pos)) {
                        num = j;
                    }
                }

                if (colorHighlight == team && selectedBall == num) {
                    selectedBallAng = mA;
                    if (ring == state.getRingCount() - 2) {
                        selectedBallMag = rBase * (8.5f - ring) / 8f;
                    } else {
                        selectedBallMag = rBase * (7.5f - ring) / 8f;
                    }
                }
            }

            float mR = rBase * (8.5f - ring) / 8f;
            float x = widthH + (float) (mR * Math.cos(mA));
            float y = heightH + (float) (mR * Math.sin(mA));
            drawMarble(x, y, team, num, canvas);
        }

        ringB = !ringB;
    }

    /**
     * draws the starting ring
     *
     * @param canvas the canvas to draw on
     */
    private void drawOuterRing(Canvas canvas) {
        int j = 0;
        double angleBase = Math.PI / 10;
        for (int c = 0; c < 4; c++) {
            for (int i = 0; i < 5; i++) {
                double angle = angleBase * (j - 2);
                float x = widthH + (float) (rBase * Math.sin(angle) * 1.02);
                float y = heightH + (float) (rBase * Math.cos(angle) * 1.02);
                canvas.drawCircle(x, y, rBase / 15f, colorPaints[c]);

                if (resetMode && c == colorHighlight) {
                    Position pos = new Position(0, i + c * 5);
                    int team = state.getTeamFromPosition(pos);
                    if (team == -1) {
                        x = widthH + (float) (rBase * Math.sin(angle) * 1.12);
                        y = heightH + (float) (rBase * Math.cos(angle) * 1.12);
                        drawArrow(x, y, -(float) (angle + Math.PI / 2), canvas);
                        positions.put(i, new Point((int) x, (int) y));
                    }
                }
                j++;
            }
        }
    }

    /**
     * draw the marbles to go on the outer ring
     * separate from drawOuterRing to draw in the correct order
     * (outer ring -> ring -> outer ring marbles)
     *
     * @param canvas the canvas to draw on
     */
    private void drawOuterRingMarbles(Canvas canvas) {
        int j = 0;
        double angleBase = Math.PI / 10;
        for (int c = 0; c < 4; c++) {
            for (int i = 0; i < 5; i++) {
                j++;

                Position pos = new Position(0, i + c * 5);
                int team = state.getTeamFromPosition(pos);
                if (team == -1) {
                    continue;
                }

                int num = 0;
                if (colorHighlight == team || Logger.getDebugValue()) {
                    Position[] positions = state.getPositionsFromTeam(team);
                    for (int k = 0; k < 5; k++) {
                        if (positions[k].equals(pos)) {
                            num = k;
                        }
                    }

                    if (colorHighlight == team && selectedBall == num) {
                        selectedBallAng = (float) (-angleBase * (j - 3) + Math.PI / 2);
                        selectedBallMag = rBase * 7.5f / 8f;
                    }
                }

                double angle = angleBase * (j - 3);
                float x = widthH + (float) (rBase * Math.sin(angle) * 1.02);
                float y = heightH + (float) (rBase * Math.cos(angle) * 1.02);
                drawMarble(x, y, team, num, canvas);
            }
        }
    }

    /**
     * draws the very inner ring that the marbles go towards
     *
     * @param canvas the canvas to draw on
     * @param r      the radius of the ring
     */
    private void drawInnerRing(Canvas canvas, float r) {
        double angleBase = Math.PI / 2;

        canvas.drawCircle(widthH, heightH, r, blackPaint);

        Path circlePath = new Path();
        circlePath.addCircle(widthH, heightH, r, Path.Direction.CW);
        canvas.clipPath(circlePath);

        for (int i = 0; i < 4; i++) {
            double angle = angleBase * i;
            canvas.drawCircle(widthH + (float) (r * Math.sin(angle) * 0.8),
                    heightH + (float) (r * Math.cos(angle) * 0.8),
                    rBase / 15f, colorPaints[i]);
        }
    }

    /**
     * draws a marble
     *
     * @param x      x-position to draw at
     * @param y      y-position to draw at
     * @param team   the team of the marble
     * @param num    id based on the order of the ball returned from state.getPositionsFromTeam()
     * @param canvas the canvas to draw on
     */
    private void drawMarble(float x, float y, int team, int num, Canvas canvas) {
        if (!resetMode && colorHighlight == team && (selectedBall < 0 || selectedBall == num) && num >= 0) {
            canvas.drawCircle(x, y, rBase / 20f, whitePaint);
        } else {
            canvas.drawCircle(x, y, rBase / 20f, blackPaint);
        }

        canvas.drawCircle(x, y, rBase / 24f, colorPaints2[team]);

        if (!resetMode && colorHighlight == team && num >= 0) {
            positions.put(num, new Point((int) x, (int) y));
            canvas.drawText("" + num, x, y + rBase / 48f, whitePaint);
        } else if (Logger.getDebugValue()) {
            canvas.drawText("" + num, x, y + rBase / 48f, whitePaint);
        }
    }

    /**
     * draws an arrow centered on the coords
     *
     * @param x      x-position of arrow
     * @param y      y-position of arrow
     * @param angle  angle that arrow points to (0 = right)
     * @param canvas canvas to draw on
     */
    private void drawArrow(float x, float y, float angle, Canvas canvas) {
        Path path = new Path();

        float ninety = (float) (Math.PI / 2);
        float threeHalves = 153.4349f * (float) (Math.PI / 180);

        // in distance, angle
        float[][] linePos = {
                {rBase / 15f, threeHalves},
                {rBase / 32f, ninety},
                {rBase / 16f, ninety},
                {rBase / 16f, 0},
                {rBase / 16f, -ninety},
                {rBase / 32f, -ninety},
                {rBase / 15f, -threeHalves},
        };

        int max = linePos.length - 1;
        float x1 = (float) Math.cos(linePos[max][1] + angle) * linePos[max][0] + x;
        float y1 = (float) Math.sin(linePos[max][1] + angle) * linePos[max][0] + y;
        path.moveTo(x1, y1);

        for (float[] pos : linePos) {
            float xi = (float) Math.cos(pos[1] + angle) * pos[0] + x;
            float yi = (float) Math.sin(pos[1] + angle) * pos[0] + y;
            path.lineTo(xi, yi);
        }
        path.close();

        canvas.drawPath(path, whitePaint);
    }

    /**
     * update the instance variables that relate to the drawing surface
     *
     * @param canvas an object that references the drawing surface
     */
    private void updateDimensions(Canvas canvas) {
        positions = new HashMap<>();


        boolean keepUpdating = false;
        boolean[] lastRot = state.getLastRingRotations();
        for (int i = 1; i < state.getRingCount() - 1; i++) {
            float ang = state.getRingAngle(i);
            if (lastRot[i]) {
                if (animAngles[i] < ang) {
                    animAngles[i] = (7 * animAngles[i] + ang - 360) / 8;
                    if (animAngles[i] < 0) {
                        animAngles[i] += 360;
                    }
                } else {
                    animAngles[i] = (7 * animAngles[i] + ang) / 8;
                }
            } else {
                if (animAngles[i] > ang) {
                    animAngles[i] = (7 * animAngles[i] + ang + 360) / 8;
                    animAngles[i] %= 360;
                } else {
                    animAngles[i] = (7 * animAngles[i] + ang) / 8;
                }
            }

            if (Math.abs(animAngles[i] - ang) > 0.1f) {
                keepUpdating = true;
            } else {
                animAngles[i] = ang;
            }
        }
        this.keepUpdating = keepUpdating;

        if (maxX == 0) {
            setScreenSize(canvas.getWidth(), canvas.getHeight());
        }
    }

    /**
     * convenience method for initializing vars that the instance needs
     */
    private void init() {
        setWillNotDraw(false);

        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setColor(0xFFFFD700);
        ringPaint.setStyle(Paint.Style.FILL);

        ringPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint2.setColor(0xFF00D7FF);
        ringPaint2.setStyle(Paint.Style.FILL);

        slotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        slotPaint.setColor(0xFFFFA500);
        slotPaint.setStyle(Paint.Style.FILL);

        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(0xFFFFFFFF);
        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setTextSize(30);
        whitePaint.setTextAlign(Paint.Align.CENTER);

        blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blackPaint.setColor(0xFF000000);
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setTextSize(40);

        // team paint

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

        // secondary team paint

        Paint redPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        redPaint2.setColor(0xFFFF1010);
        redPaint2.setStyle(Paint.Style.FILL);
        redPaint2.setTextSize(40);

        Paint greenPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint2.setColor(0xFF10FF10);
        greenPaint2.setStyle(Paint.Style.FILL);
        greenPaint2.setTextSize(40);

        Paint yellowPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellowPaint2.setColor(0xFFF0F010);
        yellowPaint2.setStyle(Paint.Style.FILL);
        yellowPaint2.setTextSize(40);

        Paint bluePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        bluePaint2.setColor(0xFF1010FF);
        bluePaint2.setStyle(Paint.Style.FILL);
        bluePaint2.setTextSize(40);

        colorPaints2 = new Paint[]{redPaint2, yellowPaint2, greenPaint2, bluePaint2};
    }
}
