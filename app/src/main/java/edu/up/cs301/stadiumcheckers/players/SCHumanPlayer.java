package edu.up.cs301.stadiumcheckers.players;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.BreakIterator;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.utilities.Logger;
import edu.up.cs301.game.R;
import edu.up.cs301.stadiumcheckers.Position;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;

public class SCHumanPlayer extends GameHumanPlayer implements View.OnClickListener {
    private static final String TAG = "SCHumanPlayer";
    // the edittext
    EditText multiLineEditText;
    // id for layout to use
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
        multiLineEditText = myActivity.findViewById(R.id.multiLineEditText);
        Logger.log("set listener", "OnClick");
        myActivity.findViewById(R.id.runTest).setOnClickListener(this);
    }

    /**
     * callback method when button is clicked
     *
     * @param view the item that was clicked
     */
    @Override
    public void onClick(View view) {
        multiLineEditText.setText("");
        SCState firstInstance = new SCState();

        SCState secondInstance = new SCState(firstInstance);

        int turnCount = firstInstance.getTurnCount();
        multiLineEditText.append("Current turn count: ");
        multiLineEditText.append("" + turnCount);
        multiLineEditText.append("\n");

        firstInstance.setTurnCount(turnCount + 1);
        multiLineEditText.append("Setting the turn count up by 1.\n");

        multiLineEditText.append("Current turn count: ");
        multiLineEditText.append("" + firstInstance.getTurnCount());
        multiLineEditText.append("\n");

        int ring = 0;
        int slotCount = firstInstance.getRingSlotCount(ring);
        multiLineEditText.append("Number of slots on ring 0: ");
        multiLineEditText.append("" + slotCount);
        multiLineEditText.append("\n");

        float ringAngle = firstInstance.getRingAngle(ring);
        multiLineEditText.append("Angle of ring 0: ");
        multiLineEditText.append("" + ringAngle);
        multiLineEditText.append("\n");

        float newAngle = 45.0f;
        firstInstance.setRingAngle(ring, newAngle);
        multiLineEditText.append("Setting ring angle of slot 0 to 45\n");

        multiLineEditText.append("Angle of ring 0: ");
        multiLineEditText.append("" + firstInstance.getRingAngle(ring));
        multiLineEditText.append("\n");

        float[] ringAngles = firstInstance.getRingAngles();
        multiLineEditText.append("Angle of all rings: ");
        for (float angle : ringAngles) {
            multiLineEditText.append("\t" + angle);
            multiLineEditText.append("\n");
        }

        firstInstance.setCurrentTeamTurn(1);
        multiLineEditText.append("It's now team 1's turn\n");

        Position position = new Position(0, 4);
        multiLineEditText.append("The team is " + firstInstance.getTeamFromPosition(position) + "!\n");

        firstInstance.rotateRing(1, position, true);
        multiLineEditText.append("Team 1 in " + position + " rotated the ring clockwise\n");

        position = new Position(0, 5);
        firstInstance.rotateRing(1, position, false);
        multiLineEditText.append("Team 1 in " + position + " rotated the ring counter-clockwise\n");

        position = new Position(0, 7);
        firstInstance.rotateRing(1, position, true);
        multiLineEditText.append("Team 1 in " + position + " rotated the ring clockwise\n");

        Position[] positions = firstInstance.getPositionsFromTeam(1);
        multiLineEditText.append("The positions from team 1 are: ");
        for (Position pos : positions) {
            multiLineEditText.append("\t" + pos);
            multiLineEditText.append("\n");
        }

        firstInstance.resetMarble(1, position, 1);
        multiLineEditText.append(("Team 1's marble in " + position + " has been reset back in slot 1\n"));
        SCState thirdInstance = new SCState();

        SCState fourthInstance = new SCState(thirdInstance);

        String secondInstanceString = secondInstance.toString();

        String fourthInstanceString = fourthInstance.toString();
        if (secondInstanceString.equals(fourthInstanceString)) {
            multiLineEditText.append("Strings are identical\n");
        } else {
            multiLineEditText.append("Strings are not identical\n");
        }
        multiLineEditText.append("Second Instance String: " + secondInstanceString);
        multiLineEditText.append("Fourth Instance String: " + fourthInstanceString);
        multiLineEditText.append("First Instance String: " + firstInstance);
    }
}
