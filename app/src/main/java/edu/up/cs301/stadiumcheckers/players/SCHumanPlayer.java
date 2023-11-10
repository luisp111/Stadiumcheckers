package edu.up.cs301.stadiumcheckers.players;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.BreakIterator;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.utilities.FlashSurfaceView;
import edu.up.cs301.game.GameFramework.utilities.Logger;
import edu.up.cs301.game.R;
import edu.up.cs301.stadiumcheckers.Position;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;
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
public class SCHumanPlayer extends GameHumanPlayer {
    private static final String TAG = "SCHumanPlayer";
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
        Logger.log(TAG, "receiving");
    }

    /**
     * sets the current player as the activity's gui
     *
     * @param activity the activity
     */
    @Override
    public void setAsGui(GameMainActivity activity) {
        activity.setContentView(layoutId);
        myActivity = activity;
    }
}
