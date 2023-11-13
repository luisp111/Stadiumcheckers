package edu.up.cs301.stadiumcheckers;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.Map;

import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;
import edu.up.cs301.stadiumcheckers.scActionMessage.SCResetAction;

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
    // Tag for logging
    private static final String TAG = "SCLocalGame";

    public SCLocalGame() {
        super();
        super.state = new SCState();
    }

    public SCLocalGame(SCState state) {
        super();
        super.state = new SCState(state);
    }

    /**
     * increment the turn value for the next player
     */
    private void incrementTurn() {
        if (!(super.state instanceof SCState)) {
            return;
        }
        SCState state = (SCState) super.state;

        state.setCurrentTeamTurn((state.getCurrentTeamTurn() + 1) % players.length);
        state.setTurnCount(state.getTurnCount() + 1);
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

    @SuppressLint("DefaultLocale")
    @Override
    protected String checkIfGameOver() {
        if (!(super.state instanceof SCState)) {
            return null;
        }
        SCState state = (SCState) super.state;

        for (Map.Entry<Integer, Position[]> e : state.getMarblesByTeam().entrySet()) {
            if (e.getKey() >= players.length) {
                Log.d(TAG, "checkIfGameOver: Tried checking win state for an invalid player");
                continue;
            }

            int cnt = 0;
            for (Position p : e.getValue()) {
                if (p.getRing() == -2) {
                    cnt++;
                }
            }
            if (cnt >= 5) {
                return String.format("%s has won the game in %d turns", playerNames[e.getKey()],
                        state.getTurnCount());
            }
        }

        return null;
    }

    @Override
    protected boolean makeMove(GameAction action) {
        if (!(super.state instanceof SCState)) {
            return false;
        }
        SCState state = (SCState) super.state;

        int team = state.getCurrentTeamTurn();
        if (action instanceof SCResetAction) {
            SCResetAction resetAction = (SCResetAction) action;

            int resetTeam = resetAction.getCurrentTeamTurn();
            Position resetPosition = resetAction.getPosition();
            int resetSlot = resetAction.getSlot();
            if (resetTeam == team) {
                if (state.isValidResetAction(resetPosition, resetSlot, resetTeam)) {
                    // try calling canMove at the top of this method
                    // instead of checking directly here
                    state.resetMarble(resetTeam, resetPosition, resetSlot);
                    return true;
                }
            }
        }

        return false;
    }
}
