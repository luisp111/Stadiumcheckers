package edu.up.cs301.stadiumcheckers.scActionMessage;

import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
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
    private final int slot;
    private final Position position;

    public SCResetAction(GamePlayer player, Position Position, int slot) {
        super(player);

        this.position = Position;
        this.slot = slot;
    }

    public int getSlot(){return slot;}

    public Position getPosition() {
        return position;
    }
}
