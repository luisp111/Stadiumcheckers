package edu.up.cs301.stadiumcheckers.players;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Map;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.utilities.Logger;
import edu.up.cs301.game.R;
import edu.up.cs301.stadiumcheckers.Position;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;
import edu.up.cs301.stadiumcheckers.scActionMessage.SCRotateAction;
import edu.up.cs301.stadiumcheckers.views.SCSurfaceView;

/**
 * Stadium Checkers
 *
 * @author Jaden Barker
 * @author James Pham
 * @author Luis Perez
 * @author Mohammad Surur
 * @author Dylan Sprigg
 */
public class SCHumanPlayer extends GameHumanPlayer implements View.OnTouchListener {
    // Tag for logging
    private static final String TAG = "SCHumanPlayer";
    // Id of the layout for the player
    private final int layoutId;

    /**
     * Main constructor
     *
     * @param name the name of the player
     */
    public SCHumanPlayer(String name, int layoutId) {
        super(name);
        this.layoutId = layoutId;
    }

    /**
     * returns the GUI's top view
     *
     * @return the gui's top view
     */
    @Override
    public View getTopView() {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * callback method, called when player gets a message
     *
     * @param info the message
     */
    @Override
    public void receiveInfo(GameInfo info) {
        SCSurfaceView view = myActivity.findViewById(R.id.surfaceView);
        if (info instanceof SCState) {
            SCState newState = new SCState((SCState) info);
            view.setTeamNames(allPlayerNames);
            view.setState(newState);

            if (Logger.getDebugValue()) {
                Log.d(TAG, "receiveInfo: " + newState);
            }

            if (newState.getCurrentTeamTurn() == playerNum) {
                view.setColorHighlight(playerNum);
            }
        }
        view.invalidate();
        Logger.log(TAG, "receiving");
    }

    /**
     * sets the current player as the activity's gui
     *
     * @param activity the activity
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setAsGui(GameMainActivity activity) {
        activity.setContentView(layoutId);
        myActivity = activity;
        SCSurfaceView view = activity.findViewById(R.id.surfaceView);
        view.setOnTouchListener(this);
    }

    /**
     * Behavior for when the game ends
     *
     * @param msg the "game over" message sent by the game
     */
    @Override
    protected void gameIsOver(String msg) {
        myActivity.recreate();
    }

    /**
     * Called when the player clicks their tablet
     *
     * @param view        the view that was pressed
     * @param motionEvent the MotionEvent of the press
     * @return true to repeat pressing, false to end the press
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!(view instanceof SCSurfaceView)) {
            return false;
        }
        SCSurfaceView sView = (SCSurfaceView) view;

        SCState state = sView.getState();
        if (state.getCurrentTeamTurn() != playerNum) {
            return false;
        }

        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float bound = sView.getRBase() / 16f;
        for (Map.Entry<Integer, Point> entry : sView.getPositions().entrySet()) {
            Point p = entry.getValue();
            if (x < p.x + bound && x > p.x - bound && y < p.y + bound && y > p.y - bound) {
                Log.d(TAG, "onTouch: ball selected");
                sView.setSelectedBall(entry.getKey());
                sView.invalidate();
                return false;
            }
        }

        int selectedBall = sView.getSelectedBall();
        if (selectedBall < 0) {
            return false;
        }

        Point cPos = sView.getcPos();
        if (x < cPos.x + bound && x > cPos.x - bound && y < cPos.y + bound && y > cPos.y - bound) {
            Log.d(TAG, "onTouch: clockwise selected");
            // the sView.getSelectedBall() returns the id of the ball
            // from its position that you get in SCState's getPositionsFromTeam() function
            // that means you can get the correct position by doing
            Position pos = state.getPositionsFromTeam(playerNum)[selectedBall];
            game.sendAction(new SCRotateAction(this, pos, true));
            return false;
        }

        Point ccPos = sView.getCcPos();
        if (x < ccPos.x + bound && x > ccPos.x - bound && y < ccPos.y + bound && y > ccPos.y - bound) {
            Log.d(TAG, "onTouch: counter-clockwise selected");
            Position pos = state.getPositionsFromTeam(playerNum)[selectedBall];
            game.sendAction(new SCRotateAction(this, pos, false));
            return false;
        }

        return false;
    }

}
