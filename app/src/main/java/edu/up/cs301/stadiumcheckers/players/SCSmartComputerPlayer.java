package edu.up.cs301.stadiumcheckers.players;

import java.util.Random;

import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;

public class SCSmartComputerPlayer extends GameComputerPlayer {
    private static final String TAG = "SCDumbComputerPlayer";

    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public SCSmartComputerPlayer(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        // considerations to make for marble selection:
        // angular distance between marble and target slot
        //      (closer is better)
        // how many rings away the marble is to target slot
        //      (closer is better when angular distance is low)
        // how close the marble is to the next slot on the next ring
        //      (rotate the direction that causes the least movement)

        // always rotate last ring towards target slot
        // when resetting a marble, always choose the middle slot first
    }
}
