package edu.up.cs301.stadiumcheckers.scActionMessage;

import java.io.ObjectInputStream;
import java.util.HashMap;

import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.stadiumcheckers.Position;

/**
 * Stadium Checkers
 *
 * @author Jaden Barker
 * @author James Pham
 * @author Luis Perez
 * @author Mohammad Surur
 * @author Dylan Sprigg
 */
public class SCResetAction extends GameAction {
    private int team;
    private int slot;
    private Position position;

    private int currentTeamTurn;

    public SCResetAction(int team, Position Position, int slot) {
        super();

        this.currentTeamTurn = team;
        this.position = Position;
        this.slot = slot;
    }
    public int getCurrentTeamTurn() {
        return currentTeamTurn;
    }

    public int getSlot(){return slot;}

    public Position getPosition() {
        return position;
    }
}
