package edu.up.cs301.stadiumcheckers.players;

import java.util.Random;

import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.stadiumcheckers.Position;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;
import edu.up.cs301.stadiumcheckers.scActionMessage.SCRotateAction;

public class SCSmartComputerPlayer extends GameComputerPlayer {
    private static final String TAG = "SCSmartComputerPlayer";

    // weightings for ai consideration
    // how strongly the ai considers distance to the end ring
    private static final float absoluteDistanceWeight = -14f;
    // how strongly the ai considers distance to the closest available ring
    private static final float relativeDistanceWeight = 0.5f;
    // how strongly the ai considers angular distance to the target slot on the end ring
    private static final float angularDistanceWeight = -1.5f;

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
        if (!(info instanceof SCState)) {
            return;
        }
        SCState state = (SCState) info;

        if (state.getCurrentTeamTurn() != playerNum) {
            return;
        }

        // add reaction time
        try {
            Thread.sleep(Math.max(1400 - (long) state.getTurnCount() * 10, 500)); // Adjust the sleep duration as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // considerations to make for marble selection:
        // angular distance between marble and target slot
        //      (closer is better)
        // how many rings away the marble is to target slot
        //      (closer is better when angular distance is low)
        // how close the marble is to the next slot on the next ring
        //      (rotate the direction that causes the least movement)

        // always rotate last ring towards target slot
        // when resetting a marble, always choose the middle slot first

        int targetAngle = playerNum * 105 + 42;

        Position[] marbles = state.getPositionsFromTeam(playerNum);

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

        // step 2: weighted consideration of marbles to drop
        float[] scores = {-999999, -999999, -999999, -999999, -999999};
        boolean[] preferredRotation = {true, true, true, true, true};
        for (int i = 0; i < marbles.length; i++) {
            Position pos = marbles[i];

            if (pos.getRing() < 0) {
                continue;
            }

            // init score for valid marble
            scores[i] = 0;

            // angular distance
            float angle = state.getPosAngle(pos);
            float dist = state.angleDist(angle, targetAngle);
            scores[i] += dist * angularDistanceWeight;

            // relative distance
            int cSlot = state.closestSlot(pos.getRing() + 1, angle, true);
            Position cPos = new Position(pos.getRing() + 1, cSlot);
            float cDist = state.angleDist(angle, state.getPosAngle(cPos));

            int ccSlot = state.closestSlot(pos.getRing() + 1, angle, false);
            Position ccPos = new Position(pos.getRing() + 1, ccSlot);
            float ccDist = state.angleDist(angle, state.getPosAngle(ccPos));

            if (cDist < ccDist && state.getTeamFromPosition(cPos) == -1) {
                scores[i] += cDist * relativeDistanceWeight;
                preferredRotation[i] = true;
            } else {
                if (state.getTeamFromPosition(ccPos) == -1) {
                    scores[i] += ccDist * relativeDistanceWeight;
                    preferredRotation[i] = false;
                } else {
                    scores[i] += cDist * relativeDistanceWeight;
                    preferredRotation[i] = true;
                }
            }

            // absolute distance
            scores[i] += Math.abs(state.getRingCount() - 2 - pos.getRing()) * absoluteDistanceWeight;
        }

        // make a decision
        float max = -999999;
        int select = 0;
        boolean selectDir = true;
        for (int i = 0; i < marbles.length; i++) {
            if (scores[i] > max) {
                max = scores[i];
                select = i;
                selectDir = preferredRotation[i];
            }
        }

        game.sendAction(new SCRotateAction(this, marbles[select], selectDir));
    }
}
