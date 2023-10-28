package edu.up.cs301.stadiumcheckers.players;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.BreakIterator;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.R;
import edu.up.cs301.stadiumcheckers.Position;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;

public class SCHumanPlayer extends GameHumanPlayer implements View.OnClickListener {
    /**
     * constructor
     *
     * @param name the name of the player
     */

    private EditText multiLineEditText;

    public SCHumanPlayer(String name) {
        super(name);
    }

    @Override
    public View getTopView() {

        return null;
    }

    @Override
    public void receiveInfo(GameInfo info) {

    }

    @Override
    public void setAsGui(GameMainActivity activity) {
        myActivity = activity;
    }

    @Override
    public void onClick(View view) {
        multiLineEditText.setText("");
        SCState firstInstance = new SCState();

        SCState secondInstance = new SCState(firstInstance);

        int turnCount = firstInstance.getTurnCount();
        multiLineEditText.append("Gets the current turn count.\n");
        firstInstance.setTurnCount(turnCount + 1);
        multiLineEditText.append("Setting the turn Count");

        int ring = 0;
        int slotCount = firstInstance.getRingSlotCount(ring);
        multiLineEditText.append("Method to get ring slot count.\n");

        float ringAngle = firstInstance.getRingAngle(ring);
        multiLineEditText.append("Method to determine angle of a ring");

        float newAngle = 45.0f;
        firstInstance.setRingAngle(ring, newAngle);
        multiLineEditText.append("Setting ring angle to float new angle");

        float[] ringAngles = firstInstance.getRingAngles();
        multiLineEditText.append("Getting ring angles for all rings");
        firstInstance.setTurnCount(1);
        firstInstance.setRingAngle(0, 45.0f);

        firstInstance.setCurrentTeamTurn(1);
        multiLineEditText.append("The player has set the team turn to 1");

        Position position = new Position(1, 2);
        firstInstance.getTeamFromPosition(position);
        multiLineEditText.append("The team is " + firstInstance.getTeamFromPosition(position) + "!");

        firstInstance.getPositionsFromTeam(1);
        multiLineEditText.append("The positions from team 1 are: " + firstInstance.getPositionsFromTeam(1));

        firstInstance.getMarblesByTeam();
        multiLineEditText.append("The marbles are " + firstInstance.getMarblesByTeam());
        int currentTeamTurn = firstInstance.getCurrentTeamTurn();
        multiLineEditText.append("gets currents teams turn");

        firstInstance.getMarblesByPosition();
        multiLineEditText.append("The marbles are " + firstInstance.getMarblesByPosition());

        firstInstance.rotateRing(1, position, true);
        multiLineEditText.append("Team 1 in " + position + " rotated the ring clockwise");

        firstInstance.resetMarble(1, position, 1);
        multiLineEditText.append(("Team 1's marble in " + position + " has been reset back in slot 1"));
        SCState thirdInstance = new SCState();

        SCState fourthInstance = new SCState(thirdInstance);

        String secondInstanceString = secondInstance.toString();

        String fourthInstanceString = fourthInstance.toString();
        if(secondInstanceString.equals(fourthInstanceString)){
            multiLineEditText.append("String are identical");
        }else {
            multiLineEditText.append("Strings are not identical");
        }
            multiLineEditText.append("Second Instance String: " + secondInstanceString);
            multiLineEditText.append("Fourth Instance String: " + fourthInstanceString);
      }
    }
