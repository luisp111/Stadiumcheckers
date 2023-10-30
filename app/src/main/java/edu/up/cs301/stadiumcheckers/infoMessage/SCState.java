package edu.up.cs301.stadiumcheckers.infoMessage;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.up.cs301.game.GameFramework.infoMessage.GameState;
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
public class SCState extends GameState {
    private final int[] ringSlotCounts = {20, 6, 7, 6, 5, 6, 4, 5, 4};
    private final HashMap<Integer, Position[]> marblesByTeam;
    private final HashMap<Position, Integer> marblesByPosition;
    private final float[] ringAngles; //angles are a range of 0-420 so as to be divisible by 7
    private int turnCount;
    private int currentTeamTurn;
    private final Random random;

    /**
     * Basic constructor for state
     */
    public SCState() {
        turnCount = 0;
        currentTeamTurn = 0;
        random = new Random();

        marblesByTeam = new HashMap<>();
        marblesByPosition = new HashMap<>();

        for (int i = 0; i < 4; i++) {
            Position[] marbles = new Position[5];
            for (int j = 0; j < 5; j++) {
                marbles[j] = new Position(j + i * 5);
                marblesByPosition.put(marbles[j], i);
            }
            marblesByTeam.put(i, marbles);
        }

        ringAngles = new float[ringSlotCounts.length];
        ringAngles[0] = 0;
        ringAngles[ringSlotCounts.length - 1] = 0;
        for (int i = 1; i < ringSlotCounts.length - 1; i++) {
            ringAngles[i] = random.nextFloat() * 420;
        }
    }

    /**
     * Copy constructor for state
     *
     * @param state the constructor to copy values from
     */
    public SCState(SCState state) {
        random = new Random();
        turnCount = state.getTurnCount();
        currentTeamTurn = state.getCurrentTeamTurn();
        marblesByTeam = state.getMarblesByTeam();
        ringAngles = state.getRingAngles();
        marblesByPosition = state.getMarblesByPosition();
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
     *
     * @param ring      the ring to find the angle of
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

    /**
     * Set the ring angle. The angle ranges from 0 to 420, it is NOT in degrees!
     *
     * @param ring  The ring to set the angle of
     * @param angle The angle (0-420)
     */
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
     * Finds the closest slot on a ring to the specified angle in some direction
     *
     * @param ring      the ring to check
     * @param angle     angle to start checking from (0-420)
     * @param direction true for clockwise, false for counterclockwise
     * @return the id of the closest slot
     */
    public int closestSlot(int ring, float angle, boolean direction) {
        if (ring < 0 || ring >= ringSlotCounts.length) {
            return -1;
        }

        int sector = 420 / ringSlotCounts[ring]; // angle between two slots (angle of one sector)
        float snappedAngle;
        if (direction) {
            snappedAngle = ringAngles[ring] - (ringAngles[ring] + angle) % sector;
        } else {
            snappedAngle = ringAngles[ring] + (ringAngles[ring] - angle) % sector;
        }

        if (snappedAngle < 0) {
            snappedAngle += 420;
        } else if (snappedAngle > 420) {
            snappedAngle -= 420;
        }

        return (int) (snappedAngle / sector + 0.5);
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

        float angle = ringAngles[ring] + (420f / ringSlotCounts[ring]) * position.getSlot();
        int targetSlot = closestSlot(ring + 1, angle, direction);
        float dist = angle - ringAngles[ring + 1] + (420f / ringSlotCounts[ring + 1]) * targetSlot;

        // special behavior for the last ring
        if (ring == ringSlotCounts.length - 2) {
            dropMarbles(ring, dist, direction);
            dropMarbles(ring - 1, dist, !direction);
            setRingAngle(ring, angle);
            return true;
        }

        // continuously rotate and drop marbles until the target marble has dropped
        for (int i = 0; i < ringSlotCounts[ring + 1]; i++) {
            // rotate to next slot
            if (i > 0) {
                dist = 420f / ringSlotCounts[ring + 1];
                if (direction) {
                    angle += dist;
                } else {
                    angle -= dist;
                }
            }

            dropMarbles(ring + 1, dist, !direction);
            dropMarbles(ring, dist, direction);
            setRingAngle(ring + 1, angle);

            if (getTeamFromPosition(position) == -1) {
                break;
            }
        }

        return true;
    }

    /**
     * PRIVATE
     * Drops all valid marbles on a ring to act as a partial rotation action
     * Doesn't have error checking for ring bounds!
     *
     * @param ring       ring to drop marbles from
     * @param targetDist the distance the ring 'rotates'
     * @param direction  the direction the ring rotates in
     */
    private void dropMarbles(int ring, float targetDist, boolean direction) {
        for (int i = 0; i < ringSlotCounts[ring]; i++) {
            Position pos = new Position(ring, i);

            if (getTeamFromPosition(pos) == -1) {
                // source slot is empty, marble cannot drop
                continue;
            }

            float angle = ringAngles[ring] + (420f / ringSlotCounts[ring]) * i;
            Position posDest = new Position(ring + 1,
                    closestSlot(ring + 1, angle, direction));

            if (getTeamFromPosition(posDest) != -1) {
                // destination slot is occupied, marble cannot drop
                continue;
            }

            float dist = angle - ringAngles[ring + 1] + (420f / ringSlotCounts[ring + 1]) * posDest.getSlot();

            if (dist > targetDist) {
                // distance to slot is larger than target marble's, marble cannot drop
                continue;
            }

            // if the marble reaches the final slot, set the "ring" accordingly
            if (ring == ringSlotCounts.length - 2) {
                int targetRing = -2; // -2: marble fell into another team's target hole
                if (getTeamFromPosition(pos) == posDest.getSlot()) {
                    targetRing = -1; // -1: marble fell into the correct target hole
                }

                // select a random slot id just to keep two marbles from getting the same position
                posDest.setPosition(targetRing, random.nextInt());
            }

            // drop the marble
            changeMarblePosition(pos, posDest);
        }
    }

    /**
     * PRIVATE
     * Change the position of a marble
     * Doesn't have any error checking!
     *
     * @param start starting position
     * @param end   ending position
     */
    private void changeMarblePosition(Position start, Position end) {
        int team = getTeamFromPosition(start);
        marblesByPosition.put(end, team);
        marblesByPosition.remove(start);

        Position[] positions = getPositionsFromTeam(team);
        for (int i = 0; i < 5; i++) {
            if (positions[i].equals(start)) {
                positions[i] = end;
                break;
            }
        }
        marblesByTeam.put(team, positions);
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

        changeMarblePosition(position, endPosition);

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
            builder.append(" ").append(f).append(";");
        }

        builder.append("\n\nmarbles by team:\n");
        for (int i = 0; i < 4; i++) {
            builder.append("\t").append(i).append(":\n");
            for (Position p : getPositionsFromTeam(i)) {
                builder.append("\t\t").append(p).append("\n");
            }
        }

        builder.append("\nmarbles by position:\n");
        for (Map.Entry<Position, Integer> entry : marblesByPosition.entrySet()) {
            builder.append("\ts").append(entry.getKey()).append(": ");
            builder.append(entry.getValue()).append("\n");
        }

        builder.append("--------------------\n");
        return builder.toString();
    }
}
