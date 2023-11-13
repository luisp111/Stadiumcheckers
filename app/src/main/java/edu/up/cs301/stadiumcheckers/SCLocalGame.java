package edu.up.cs301.stadiumcheckers;

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

    SCState scs;

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
        scs = (SCState) super.state;
        int team = scs.getCurrentTeamTurn();
        if (action instanceof SCResetAction){
            SCResetAction resetAction = (SCResetAction) action;

            int resetTeam = resetAction.getCurrentTeamTurn();
            Position resetPosition = resetAction.getPosition();
            int resetSlot = resetAction.getSlot();
            if(resetTeam == team){
                if(scs.isValidResetAction(resetPosition, resetSlot, resetTeam)){
                    scs.resetMarble(resetTeam,resetPosition, resetSlot);
                    return true;
                }
            }
        }

        return false;
    }

}
