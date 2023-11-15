package edu.up.cs301.stadiumcheckers.scActionMessage;

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
public class SCRotateAction extends GameAction {
    private final boolean direction;
    private final Position position;

    public SCRotateAction(Position Position, boolean direction) {
        super();

        this.position = Position;
        this.direction = direction;
    }

    public boolean getDirection(){return direction;}

    public Position getPosition() {
        return position;
    }
}
