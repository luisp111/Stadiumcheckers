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
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;

public class SCHumanPlayer extends GameHumanPlayer implements View.OnClickListener{
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

        firstInstance.setTurnCount(1);
        firstInstance.setRingAngle(0,45.0f);
        firstInstance.get



    }
