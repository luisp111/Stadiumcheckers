package edu.up.cs301.stadiumcheckers;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.Map;

import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;
import edu.up.cs301.stadiumcheckers.scActionMessage.SCResetAction;
import edu.up.cs301.stadiumcheckers.scActionMessage.SCRotateAction;

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

    /**
     * Basic constructor for game
     */
    public SCLocalGame() {
        super();
        super.state = new SCState();
    }

    /**
     * Constructor for game including state definition
     *
     * @param state the state for the new instance to track
     */
    public SCLocalGame(SCState state) {
        super();
        super.state = new SCState(state);
    }

    /**
     * Increment the turn value for the next player
     */
    private void incrementTurn() {
        if (!(super.state instanceof SCState)) {
            return;
        }
        SCState state = (SCState) super.state;

        state.setCurrentTeamTurn((state.getCurrentTeamTurn() + 1) % players.length);
        state.setTurnCount(state.getTurnCount() + 1);
    }

    /**
     * sends the current game state to a player
     *
     * @param p the player to notify
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        p.sendInfo(super.state);
    }

    /**
     * determines whether it's a certain player's turn or not
     *
     * @param playerId the player's player-number (ID)
     * @return true if it's their turn, false if it's not
     */
    @Override
    protected boolean canMove(int playerId) {
        if (super.state instanceof SCState) {
            SCState state = (SCState) super.state;
            return state.getCurrentTeamTurn() == playerId;
        }
        return false;
    }

    /**
     * checks if the game has ended
     *
     * @return any string to end the game, null to continue
     */
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
                if (p.getRing() == -1) {
                    cnt++;
                }
            }
            if (cnt >= 5) {
                return String.format("%s has won the game in %d turns!\n", playerNames[e.getKey()],
                        state.getTurnCount());
            }
        }

        return null;
    }

    /**
     * make a move on behalf of a player
     *
     * @param action The move that the player has sent to the game
     * @return true if the action was successful, false otherwise
     */
    @Override
    protected boolean makeMove(GameAction action) {
        if (!(super.state instanceof SCState)) {
            return false;
        }
        SCState state = (SCState) super.state;

        if (action instanceof SCResetAction) {
            SCResetAction resetAction = (SCResetAction) action;

            return state.resetMarble(state.getCurrentTeamTurn(), resetAction.getPosition(),
                    resetAction.getSlot());
        }

        if (action instanceof SCRotateAction) {
            SCRotateAction rotateAction = (SCRotateAction) action;

            boolean ret = state.rotateRing(state.getCurrentTeamTurn(), rotateAction.getPosition(),
                    rotateAction.getDirection());

            if (ret) {
                incrementTurn();
            }
            return ret;
        }

        return false;
    }
}
