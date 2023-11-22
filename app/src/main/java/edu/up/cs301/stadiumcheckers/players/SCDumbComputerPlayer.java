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
        int targetAngle = playerNum * 105 + 42;

        // step 1: find marbles on the bottom that can get secured
        for (Position pos : marbles) {
            if (pos.getRing() != state.getRingCount() - 2) {
                continue;
            }

            float angle = state.getPosAngle(pos);
            float distS = state.angleDist(angle, targetAngle, true);
            if (Math.abs(distS) < 105) {
                game.sendAction(new SCRotateAction(this, pos, distS > 0));
                return;
            }
        }

        // selects a marble based on ai type
        Position selectedMarble = marbles[0];
        switch (type) {
            case 0: // random
            default:
                int[] base = {0, 1, 2, 3, 4};
                for (int i = 0; i < 5; i++) {
                    int r = random.nextInt(marbles.length - i) + i;
                    int tmp = base[i];
                    base[i] = base[r];
                    base[r] = tmp;

                    selectedMarble = marbles[base[i]];

                    if (selectedMarble.getRing() >= 0) {
                        break;
                    }
                }

                break;
            case 1: // lowest first
                for (Position p : marbles) {
                    if (p.getRing() > selectedMarble.getRing()) {
                        selectedMarble = p;
                    }
                }

                break;
            case 2: // highest first
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