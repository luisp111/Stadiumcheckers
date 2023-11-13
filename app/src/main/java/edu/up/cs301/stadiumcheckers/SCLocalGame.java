package edu.up.cs301.stadiumcheckers;

import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
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
public class SCLocalGame extends LocalGame {
    public SCLocalGame() {
        super();
        super.state = new SCState();
    }

    public SCLocalGame(SCState state) {
        super();
        super.state = new SCState(state);
    }

    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        p.sendInfo(super.state);
    }

    @Override
    protected boolean canMove(int playerId) {
        if (super.state instanceof SCState) {
            SCState state = (SCState) super.state;
            return state.getCurrentTeamTurn() == playerId;
        }
        return false;
    }

    @Override
    protected String checkIfGameOver() {
        if (!(super.state instanceof SCState)) {
            return null;
        }
        SCState state = (SCState) super.state;

        for (GamePlayer ply : getPlayers()) {
        }
        return null;
    }

    @Override
    protected boolean makeMove(GameAction action) {
        return false;
    }
}
