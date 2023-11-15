package edu.up.cs301.stadiumcheckers.players;
import java.util.Random;

import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.stadiumcheckers.Position;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;
import edu.up.cs301.stadiumcheckers.scActionMessage.SCRotateAction;

public class SCDumbComputerPlayer extends GameComputerPlayer {
    private final Random random;
    // 0 = random, 1 = lowest always, 2 = highest always
    int type;
    private static final String TAG = "SCDumbComputerPlayer";

    /**
     * Main constructor
     */
    public SCDumbComputerPlayer(String name, int type) {
        super(name);
        this.random = new Random();
        this.type = type;
    }

    /*
    public Position selectNextMove(ScState state){
        Position[] marbles = getPositionsFromTeam(state.getCurrentTeamTurn());
        return marbles[random.nextInt(marbles.length)];
    }
     */

    /**
     * Receive game info (and immediately play if able)
     *
     * @param info the object representing the information from the game
     */
    @Override
    public void receiveInfo(GameInfo info){
        if (!(info instanceof SCState)) {
            return;
        }
        SCState state = (SCState) info;

        if (state.getCurrentTeamTurn() != playerNum) {
            return;
        }

        // Gets all marbles for current team
        Position[] marbles = state.getPositionsFromTeam(playerNum);

        // randomly selects a marble
        Position selectedMarble;
        switch (type) {
            case 0: // random
            default:
                int t = marbles.length;
                do {
                    selectedMarble = marbles[random.nextInt(marbles.length)];
                    t--;
                } while (selectedMarble.getRing() < 0 && t > 0);

                if (t <= 0) {
                    return; // no slots!
                }

                break;
            case 1: // lowest first
                selectedMarble = marbles[0];
                for (Position p : marbles) {
                    if (p.getRing() > selectedMarble.getRing()) {
                        selectedMarble = p;
                    }
                }

                break;
            case 2: // highest first
                selectedMarble = marbles[0];
                for (Position p : marbles) {
                    if (selectedMarble.getRing() < 0
                            || (p.getRing() > 0 && p.getRing() < selectedMarble.getRing())) {
                        selectedMarble = p;
                    }
                }
        }

        // Randomly decide the direction of rotation (clockwise or counterclockwise
        boolean rotateClockwise = random.nextBoolean();

        // Add reaction time
        try {
            Thread.sleep(Math.max(1400 - (long) state.getTurnCount() * 10, 500)); // Adjust the sleep duration as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        game.sendAction(new SCRotateAction(this, selectedMarble, rotateClockwise));
    }
}
//check for positions from what I get goes by -2 and then do the reset action
// then move random marble with rotate
//use the getPositions from team
//getPositionsFromTeam
//