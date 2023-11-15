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
    // the number of slots for each ring (going from outside -> inside)
    private final int[] ringSlotCounts = {20, 6, 7, 6, 5, 6, 4, 5, 4};
    // all marbles in the game, indexed by team
    // so <team, marble> -> the getPositionFromTeam method returns an empty array if team is invalid
    private final HashMap<Integer, Position[]> marblesByTeam;
    // all marbles in the game, indexed by position
    // so <marble, team> -> the getTeamFromPosition method returns -1 if the position is empty
    private final HashMap<Position, Integer> marblesByPosition;
    // the angles of each ring (going from outside -> inside)
    // angles are a range of 0-420 so as to be divisible by 7
    private final float[] ringAngles;
    // the total number of turns the game has gone through
    private int turnCount;
    // the current team's turn
    private int currentTeamTurn;
    // random
    private final Random random;

    // ALSO IMPORTANT: for any Position value,
    // the ring value goes from 0 to (ring count), 0 being the starting ring
    // a ring value is -2 when the ball needs to be reset
    // a ring value is -1 when the ball is secured (players need all 5 secured to win)

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
     * Doesn't copy colorHighlight as that value is irrelevant non-clientside
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

    /**
     * Gets the number of slots for a current ring
     *
     * @param ring the ring to check
     * @return the number of slots for the ring, or -1 if the ring entered was invalid
     */
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

    public int getRingCount() {
        return ringSlotCounts.length;
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

    /**
     * Gets a team value from a certain position
     * Allows inferring a marble's existence
     *
     * @param pos the position to check
     * @return the team, or -1 if the slot is empty
     */
    public int getTeamFromPosition(Position pos) {
        Integer team = marblesByPosition.get(pos);
        if (team == null) {
            return -1;
        }
        return team;
    }

    /**
     * Gets a list of positions from a team
     * The order of positions returned is consistent every time,
     * allowing one to "index" marbles by team and marble order
     *
     * @param team the team to check
     * @return an array of positions of the team's marbles
     */
    public Position[] getPositionsFromTeam(int team) {
        Position[] positions = marblesByTeam.get(team);
        if (positions == null) {
            return new Position[0];
        }
        return positions;
    }

    /**
     * Not to be confused with {@link #getPositionsFromTeam(int)}
     *
     * @return the entire marblesByTeam hashmap
     */
    public HashMap<Integer, Position[]> getMarblesByTeam() {
        return marblesByTeam;
    }

    /**
     * Not to be confused with {@link #getTeamFromPosition(Position)}
     *
     * @return the entire marblesByPosition hashmap
     */
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
            // ring is invalid
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
            // not your turn / not your marble
            return false;
        }

        int ring = position.getRing();
        if (ring >= ringSlotCounts.length - 1 || ring < 0) {
            // ring is invalid
            return false;
        }

        for (Position p : getPositionsFromTeam(team)) {
            if (p.getRing() == -2) {
                // you have marbles to reset first
                return false;
            }
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
     * place a marble that has been invalidated back at the starting row
     *
     * @param team     the team trying to rotate the ring
     * @param position position of the target marble to reset
     * @param slot     the slot that the marble is set to return to on the starting row
     * @return whether the action was successful
     */
    public boolean resetMarble(int team, Position position, int slot) {
        if (position.getRing() != -2) {
            // the marble isn't actually out of the game
            return false;
        }

        if (currentTeamTurn != team || getTeamFromPosition(position) != team) {
            // not your turn / not your marble
            return false;
        }

        Position endPosition = new Position(slot);
        if (getTeamFromPosition(endPosition) != -1) {
            // target position is occupied
            return false;
        }

        changeMarblePosition(position, endPosition);

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
        //TODO: drop shortest dist marble only, double dropping
        // might require splitting this into 2 methods for lower and upper
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
