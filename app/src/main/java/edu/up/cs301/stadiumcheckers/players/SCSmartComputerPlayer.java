package edu.up.cs301.stadiumcheckers.players;

import java.util.Random;

import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.stadiumcheckers.Position;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;
import edu.up.cs301.stadiumcheckers.scActionMessage.SCRotateAction;

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
        if (!(info instanceof SCState)) {
            return;
        }

        SCState state = (SCState) info;
        int team = this.playerNum;

        if (state.getCurrentTeamTurn() == playerNum){
            Position[] myMarbles = state.getPositionsFromTeam(playerNum);

            Position selectedMarble = selectSmartMarble(state, myMarbles);
            boolean rotateClockwise = determineRotationDirection(state, selectedMarble);

            game.sendAction(new SCRotateAction(this, selectedMarble, rotateClockwise));
        }
    }

    private Position selectSmartMarble(SCState state, Position[] marbles){
        Position selectedMarble = null;
        float minWeightedSum = Float.MAX_VALUE;

        for (Position marble : marbles) {
            float angularDistance = calculateAngularDistance(state, marble);
            int ringDistance = calculateRingDistance(state, marble);
            float proximityToNextSlot = calculateProximityToNextSlot(state, marble);

            // Weight factors according to importance in your game
            float weightedSum = 0.5f * angularDistance + 0.3f * ringDistance + 0.2f * proximityToNextSlot;

            if (weightedSum < minWeightedSum) {
                minWeightedSum = weightedSum;
                selectedMarble = marble;
            }
        }

        return selectedMarble;
    }

    private boolean determineRotationDirection(SCState state, Position marble) {
        // Basic logic: Rotate in the direction that minimizes movement
        float clockwiseDist = calculateAngularDistance(state, marble, true);
        float counterclockwiseDist = calculateAngularDistance(state, marble, false);

        // Rotate in the direction with the minimum angular distance
        return clockwiseDist <= counterclockwiseDist;
    }

    private float calculateAngularDistance(SCState state, Position marble) {
        // Basic logic: Calculate angular distance between marble and target slot
        float targetAngle = state.getRingAngle(state.getRingCount() - 1);
        float marbleAngle = state.getRingAngle(marble.getRing()) +
                (420f / state.getRingSlotCount(marble.getRing())) * marble.getSlot();

        return angleDist(marbleAngle, targetAngle);
    }

    private float calculateAngularDistance(SCState state, Position marble, boolean clockwise) {
        // Basic logic: Calculate angular distance for both directions
        float currentAngle = state.getRingAngle(marble.getRing()) +
                (420f / state.getRingSlotCount(marble.getRing())) * marble.getSlot();
        float targetAngle = state.getRingAngle(state.getRingCount() - 1);

        float clockwiseDist = angleDist(currentAngle, targetAngle);
        float counterclockwiseDist = angleDist(targetAngle, currentAngle);

        return clockwise ? clockwiseDist : counterclockwiseDist;
    }

    private float calculateProximityToNextSlot(SCState state, Position marble) {
        // Basic logic: Calculate proximity to the next slot on the next ring
        int nextRing = marble.getRing() + 1;
        if (nextRing >= state.getRingCount()) {
            return 0; // No next ring, so proximity is irrelevant
        }

        float currentAngle = state.getRingAngle(marble.getRing()) +
                (420f / state.getRingSlotCount(marble.getRing())) * marble.getSlot();
        int nextSlot = state.closestSlot(nextRing, currentAngle, true);
        float nextSlotAngle = state.getRingAngle(nextRing) +
                (420f / state.getRingSlotCount(nextRing)) * nextSlot;
        return angleDist(currentAngle, nextSlotAngle);
    }

    private int calculateRingDistance(SCState state, Position marble) {
        // Basic logic: Calculate the number of rings away the marble is to the target slot
        int targetRing = state.getRingCount() - 1;
        return Math.abs(targetRing - marble.getRing());
    }

    private float angleDist(float angle1, float angle2) {
        float result = (angle1 - angle2) + 210;
        return Math.abs((result % 420 + 420) % 420);
    }
    }

