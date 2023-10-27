package edu.up.cs301.stadiumcheckers.players;

import android.view.View;
import android.widget.Button;
import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.R;

public class SCHumanPlayer extends GameHumanPlayer implements View.OnClickListener{
    /**
     * constructor
     *
     * @param name the name of the player
     */
    public SCHumanPlayer(String name) {
        super(name);
    }

    @Override
    public View getTopView() {
        return myActivity.findViewById((R.id.human_player_view);
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

    }
}
