package edu.up.cs301.stadiumcheckers;

import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;

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
    }

    @Override
    protected boolean canMove(int playerIdx) {
        return false;
    }

    @Override
    protected String checkIfGameOver() {
        return null;
    }

    @Override
    protected boolean makeMove(GameAction action) {
        return false;
    }
}
