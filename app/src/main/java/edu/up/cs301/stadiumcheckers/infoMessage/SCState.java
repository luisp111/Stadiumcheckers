package edu.up.cs301.stadiumcheckers.infoMessage;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.stadiumcheckers.Position;

public class SCState extends GameState {
    private final int[] ringSlotCounts = {20, 6, 7, 6, 5, 6, 4, 5, 4};
    private final HashMap<Integer, Position[]> marblesByTeam;
    private final HashMap<Position, Integer> marblesByPosition;
    private final float[] ringAngles; //angles are a range of 0-420 so as to be divisible by 7
    private int turnCount;
    private int currentTeamTurn;

    /**
     * Basic constructor for state
     */
    public SCState() {
        turnCount = 0;
        currentTeamTurn = 0;

        marblesByTeam = new HashMap<>();
        marblesByPosition = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            Position[] marbles = new Position[5];
            for (int j = 0; j < 5; j++) {
                marbles[j] = new Position(j + i * 4);
                marblesByPosition.put(marbles[j], i);
            }
            marblesByTeam.put(i, marbles);
        }

        ringAngles = new float[ringSlotCounts.length];
        for (int i = 0; i < ringSlotCounts.length; i++) {
            ringAngles[i] = 0;
        }
    }

    /**
     * Copy constructor for state
     *
     * @param state the constructor to copy values from
     */
    public SCState(SCState state) {
        turnCount = state.getTurnCount();
        currentTeamTurn = state.getCurrentTeamTurn();
        marblesByTeam = state.getMarblesByTeam();
        marblesByPosition = state.getMarblesByPosition();
        ringAngles = state.getRingAngles();
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public int getRingSlotCount(int ring) {
        if (ring >= ringSlotCounts.length || ring < 0) {
            return -1;
        }
        return ringSlotCounts[ring];
    }

    public float getRingAngle(int ring) {
        return getRingAngle(ring, true);
    }

    /**
     * getRingAngle
     * @param ring the ring to find the angle of
     * @param asDegrees set to true to return degrees (0-360), false for original value (0-420)
     * @return the angle
     */
    public float getRingAngle(int ring, boolean asDegrees) {
        if (ring >= ringSlotCounts.length || ring < 0) {
            return -1f;
        }

        if (asDegrees) {
            return ringAngles[ring] * (360 / 420f);
        }
        return ringAngles[ring];
    }

    public void setRingAngle(int ring, float angle) {
        if (ring >= ringSlotCounts.length || ring < 0) {
            return;
        }
        ringAngles[ring] = angle;
    }

    public float[] getRingAngles() {
        return ringAngles;
    }

    public int getCurrentTeamTurn() {
        return currentTeamTurn;
    }

    public void setCurrentTeamTurn(int currentTeamTurn) {
        this.currentTeamTurn = currentTeamTurn;
    }

    public int getTeamFromPosition(Position pos) {
        Integer team = marblesByPosition.get(pos);
        if (team == null) {
            return -1;
        }
        return team;
    }

    public Position[] getPositionsFromTeam(int team) {
        Position[] positions = marblesByTeam.get(team);
        if (positions == null) {
            return new Position[0];
        }
        return positions;
    }

    public HashMap<Integer, Position[]> getMarblesByTeam() {
        return marblesByTeam;
    }

    public HashMap<Position, Integer> getMarblesByPosition() {
        return marblesByPosition;
    }

    /**
     * Have a team rotate a ring until a selected marble drops, changing the board's data correctly
     *
     * @param team      the team trying to rotate the ring
     * @param position  position of the target marble to drop
     * @param direction true for clockwise, false for counterclockwise
     * @return whether the action was successful
     */
    public boolean rotateRing(int team, Position position, boolean direction) {
        if (currentTeamTurn != team || getTeamFromPosition(position) != team) {
            return false;
        }

        int ring = position.getRing();
        if (ring >= ringSlotCounts.length - 1 || ring < 0) {
            return false;
        }

        int innerSector = 420 / ringSlotCounts[ring + 1];
        int currentSector = 420 / ringSlotCounts[ring];
        float innerAngle = ringAngles[ring + 1] % innerSector;
        float currentAngle = ringAngles[ring] % currentSector;

        //TODO: figure out which marbles to drop and what slots to drop them to

        return true;
    }

    /**
     * place a marble that has been invalidated back at the starting row
     *
     * @param team     the team trying to rotate the ring
     * @param position position of the target marble to reset
     * @param slot     the slot that the marble is set to return to on the starting row
     * @return whether the action was successful
     */
    public boolean resetMarble(int team, Position position, int slot) {
        if (currentTeamTurn != team || getTeamFromPosition(position) != team) {
            return false;
        }

        Position endPosition = new Position(0, slot);
        if (getTeamFromPosition(endPosition) != -1) {
            return false;
        }

        marblesByPosition.put(endPosition, team);
        marblesByPosition.remove(position);

        Position[] positions = getPositionsFromTeam(team);
        for (int i = 0; i < 5; i++) {
            if (positions[i] == position) {
                positions[i] = endPosition;
                break;
            }
        }
        marblesByTeam.put(team, positions);

        return true;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n--------------------\n");
        builder.append("total turn count: ").append(turnCount).append("\n");
        builder.append("current turn: ").append(currentTeamTurn).append("\n\n");

        builder.append("ring slots:");
        for (int s : ringSlotCounts) {
            builder.append(" ").append(s);
        }

        builder.append("\nring angles:");
        for (float f : ringAngles) {
            builder.append(" ").append(f);
        }

        builder.append("\n\nmarbles by team:\n");
        for (int i = 0; i < 4; i++) {
            builder.append("\t1:\n");
            for (Position p : getPositionsFromTeam(i)) {
                builder.append("\t\t").append(p).append("\n");
            }
        }

        builder.append("\nmarbles by position:\n");
        for (Map.Entry<Position, Integer> e : marblesByPosition.entrySet()) {
            builder.append("\t").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }

        builder.append("--------------------\n");
        return builder.toString();
    }


}
