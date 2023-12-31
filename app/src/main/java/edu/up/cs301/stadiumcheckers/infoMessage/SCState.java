package edu.up.cs301.stadiumcheckers.infoMessage;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.stadiumcheckers.Position;

/**
 * Stadium Checkers
 * <p>
 * Final release notes:
 * - I really don't feel like touching the rotation action to fix it, it's too complicated.
 * - Networking probably works.
 * - You can rotate the screen, save and load the game, and use the help button.
 *
 * @author Jaden Barker
 * @author James Pham
 * @author Luis Perez
 * @author Mohammad Surur
 * @author Dylan Sprigg
 */
public class SCState extends GameState {
    // Tag for logging
    private static final String TAG = "SCState";
    // the number of slots for each ring (going from outside -> inside)
    private final int[] ringSlotCounts = {20, 6, 7, 6, 5, 6, 4, 5, 4};
    // all marbles in the game, indexed by team
    // so <team, marble[]> -- the getPositionFromTeam method returns an empty array if team is invalid
    private final HashMap<Integer, Position[]> marblesByTeam;
    // all marbles in the game, indexed by position
    // so <marble, team> -- the getTeamFromPosition method returns -1 if the position is empty
    private final HashMap<Position, Integer> marblesByPosition;
    // the angles of each ring (going from outside -> inside)
    // angles are a range of 0-420 so as to be divisible by 7
    private final float[] ringAngles;
    // tracker for ring rotations
    private final boolean[] lastRingRotations;
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
        lastRingRotations = new boolean[ringSlotCounts.length];
        ringAngles[0] = 0;
        ringAngles[ringSlotCounts.length - 1] = 42f;
        for (int i = 1; i < ringSlotCounts.length - 1; i++) {
            ringAngles[i] = random.nextInt(420);
            lastRingRotations[i] = false;
        }
    }

    /**
     * Copy constructor for state
     * Doesn't copy colorHighlight as that value is irrelevant non-clientside
     *
     * @param state the constructor to copy values from
     */
    public SCState(SCState state) {
        if (state == null) {
            state = new SCState();
            Log.d(TAG, "SCState: Tried copying a null state!");
        }
        
        random = new Random();
        turnCount = state.getTurnCount();
        currentTeamTurn = state.getCurrentTeamTurn();
        ringAngles = Arrays.copyOf(state.getRingAngles(), state.getRingCount());
        lastRingRotations = Arrays.copyOf(state.getLastRingRotations(), state.getRingCount());

        marblesByPosition = new HashMap<>();
        synchronized(state.marblesByPosition) {
            marblesByPosition.putAll(state.getMarblesByPosition());
        }

        marblesByTeam = new HashMap<>();
        for (Map.Entry<Integer, Position[]> entry : state.getMarblesByTeam().entrySet()) {
            marblesByTeam.put(entry.getKey(), Arrays.copyOf(entry.getValue(), entry.getValue().length));
        }
    }

    /**
     * Testing constructor for state
     */
    public SCState(int test) {
        turnCount = 0;
        currentTeamTurn = 0;
        random = new Random();

        marblesByTeam = new HashMap<>();
        marblesByPosition = new HashMap<>();

        int row = 0;
        if (test == 2) {
            row = -2;
        }
        for (int i = 0; i < 4; i++) {
            Position[] marbles = new Position[5];
            for (int j = 0; j < 5; j++) {
                if (j == 4) {
                    if (test == 3) {
                        row = -1;
                    } else if (test == 4) {
                        row = -2;
                    }
                } else if (test != 2) {
                    row = 0;
                }
                marbles[j] = new Position(row, j + i * 5);
                marblesByPosition.put(marbles[j], i);
            }
            marblesByTeam.put(i, marbles);
        }

        ringAngles = new float[ringSlotCounts.length];
        lastRingRotations = new boolean[ringSlotCounts.length];
        ringAngles[0] = 0;
        ringAngles[ringSlotCounts.length - 1] = 42f;
        for (int i = 1; i < ringSlotCounts.length - 1; i++) {
            ringAngles[i] = 21f;
            lastRingRotations[i] = false;
        }
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

    public boolean[] getLastRingRotations() {
        return lastRingRotations;
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
        int result;
        if (direction) {
            result = (int) Math.ceil((angle - ringAngles[ring]) / sector) % ringSlotCounts[ring];
        } else {
            result = (int) Math.floor((angle - ringAngles[ring]) / sector) % ringSlotCounts[ring];
        }

        // make sure result is always positive
        if (result < 0) {
            return ringSlotCounts[ring] + result;
        }
        return result;
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
        // clockwise is negative

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

        float angle = getPosAngle(position);

        // special behavior for the last ring
        if (ring == ringSlotCounts.length - 2) {
            int targetSlot = closestSlot(ring + 1, angle, !direction);
            float targetAngle = getPosAngle(new Position(ring + 1, targetSlot));
            float dist = angleDist(angle, targetAngle);
            lastRingRotations[ring] = direction;

            dropMarblesLower(ring, dist, position, !direction);
            dropMarblesUpper(ring - 1, dist, position, direction);

            float setAng = targetAngle - (420f / ringSlotCounts[ring]) * position.getSlot();
            if (setAng < 0) {
                setAng += 420;
            } else if (setAng > 420) {
                setAng -= 420;
            }
            setRingAngle(ring, setAng);
            changeMarblePosition(position, new Position(ring + 1, targetSlot));

            return true;
        }

        int targetSlot = closestSlot(ring + 1, angle, direction);
        float targetAngle = getPosAngle(new Position(ring + 1, targetSlot));
        float dist = angleDist(angle, targetAngle);
        lastRingRotations[ring + 1] = direction;

        // continuously rotate and drop marbles until the target marble has dropped
        int skips = 0;
        for (int i = 0; i < ringSlotCounts[ring + 1] * 2; i++) {
            // rotate to next slot
            if (i > 0 || (dist < 0.001 && getTeamFromPosition(new Position(ring + 1, targetSlot)) != -1)) {
                if (direction) {
                    skips--;
                } else {
                    skips++;
                }

                dist = 420f / ringSlotCounts[ring + 1];
            }

            dropMarblesLower(ring + 1, dist, position, !direction);
            dropMarblesUpper(ring, dist, position, direction);

            float setAng = angle - (420f / ringSlotCounts[ring + 1]) * (targetSlot + skips);
            if (setAng < 0) {
                setAng += 420;
            } else if (setAng > 420) {
                setAng -= 420;
            }
            setRingAngle(ring + 1, setAng);

            if (changeMarblePosition(position, position)) {
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

        if ((int) (slot / 5f) != team) {
            // you don't own that slot
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
     * @param sourcePos  position of source marble
     * @param direction  the direction the ring rotates in
     */
    private void dropMarblesUpper(int ring, float targetDist, Position sourcePos, boolean direction) {
        HashMap<DropCandidate, Float> marblesToDrop = new HashMap<>();
        for (int i = 0; i < ringSlotCounts[ring]; i++) {
            Position pos = new Position(ring, i);

            // check if slot is valid and get it
            DropResult result = dropConditions(pos, targetDist, sourcePos, direction);
            if (result == null) {
                continue;
            }

            // compare slot against running candidates
            DropCandidate cand = new DropCandidate(result.pos, result.targetPos);
            Float val = marblesToDrop.get(cand);
            if (val == null) {
                marblesToDrop.put(cand, result.dist);
                continue;
            }

            // extra checks to see if about-to-be-overridden candidates can drop first
            if (val > result.dist) {
                float angle1 = getPosAngle(cand.pos);
                Position posDest = new Position(ring + 2,
                        closestSlot(ring + 2, angle1, direction));

                if (getTeamFromPosition(posDest) == -1) {
                    // destination slot is occupied, marble cannot drop
                    marblesToDrop.put(cand, result.dist);
                    continue;

                }

                float dist = angleDist(angle1, getPosAngle(posDest));

                if (dist > targetDist) {
                    // distance to slot is larger than target marble's, marble cannot drop
                    marblesToDrop.put(cand, result.dist);
                    continue;
                }

                // marble can double drop!
                changeMarblePosition(cand.pos, posDest);
                marblesToDrop.put(cand, result.dist);
            }
        }

        // drop all candidates
        for (DropCandidate cand : marblesToDrop.keySet()) {
            changeMarblePosition(cand.pos, cand.targetPos);
        }
    }

    /**
     * PRIVATE
     * Drops all valid marbles on a ring to act as a partial rotation action
     * Doesn't have error checking for ring bounds!
     *
     * @param ring       ring to drop marbles from
     * @param targetDist the distance the ring 'rotates'
     * @param sourcePos  position of source marble
     * @param direction  the direction the ring rotates in
     */
    private void dropMarblesLower(int ring, float targetDist, Position sourcePos, boolean direction) {
        HashMap<DropCandidate, Float> marblesToDrop = new HashMap<>();
        for (int i = 0; i < ringSlotCounts[ring]; i++) {
            Position pos = new Position(ring, i);

            // check if slot is valid and get it
            DropResult result = dropConditions(pos, targetDist, sourcePos, direction);
            if (result == null) {
                continue;
            }

            // compare slot against running candidates
            DropCandidate cand = new DropCandidate(result.pos, result.targetPos);
            Float val = marblesToDrop.get(cand);
            if (val == null || val > result.dist) {
                marblesToDrop.put(cand, result.dist);
            }
        }

        // drop all candidates
        for (DropCandidate cand : marblesToDrop.keySet()) {
            changeMarblePosition(cand.pos, cand.targetPos);
        }
    }

    /**
     * Returns proper variables for a slot to be dropped if it is possible to do so
     *
     * @param pos        position to check
     * @param targetDist maximum distance before unable to drop
     * @param sourcePos  position of source marble
     * @param direction  direction to drop in
     * @return the info needed to drop the slot or null if not possible
     */
    private DropResult dropConditions(Position pos, float targetDist, Position sourcePos, boolean direction) {
        if (pos.equals(sourcePos)) {
            // target marble is the source marble
            return null;
        }

        if (getTeamFromPosition(pos) == -1) {
            // source slot is empty, marble cannot drop
            return null;
        }

        int ring = pos.getRing();
        float angle = getPosAngle(pos);
        Position posDest = new Position(ring + 1,
                closestSlot(ring + 1, angle, direction));
        float dist = angleDist(angle, getPosAngle(posDest));

        if (getTeamFromPosition(posDest) != -1) {
            if (dist <= 0.001) {
                // if the slot *right* under you is occupied, check the next slot perhaps
                dist = 420f / ringSlotCounts[ring + 1];
                if (dist > targetDist) {
                    return null;
                }

                int slot = posDest.getSlot();
                if (direction) {
                    slot = slot - 1;
                    if (slot < 0) {
                        slot += ringSlotCounts[ring + 1];
                    }
                } else {
                    slot = (slot + 1) % ringSlotCounts[ring + 1];
                }
                posDest.setPosition(posDest.getRing(), slot);

                // destination slot is occupied, marble cannot drop
                if (getTeamFromPosition(posDest) != -1) {
                    return null;
                }
            } else {
                // destination slot is occupied, marble cannot drop
                return null;
            }
        }

        if (dist > targetDist) {
            // distance to slot is larger than target marble's, marble cannot drop
            return null;
        }

        return new DropResult(dist, pos, posDest);
    }

    /**
     * checks the distance between two angles
     *
     * @param angle1 first angle to check
     * @param angle2 second angle to check
     * @return the angular distance
     */
    public float angleDist(float angle1, float angle2) {
        return angleDist(angle1, angle2, false);
    }

    /**
     * checks the distance between two angles
     *
     * @param angle1 first angle to check
     * @param angle2 second angle to check
     * @param signed whether counterclockwise distances are negative
     * @return the angular distance
     */
    public float angleDist(float angle1, float angle2, boolean signed) {
        float result = (angle1 - angle2) + 210;

        if (signed) {
            return (result % 420 + 420) % 420 - 210;
        } else {
            return Math.abs((result % 420 + 420) % 420 - 210);
        }
    }

    /**
     * Get the angle of a particular position
     *
     * @param pos position to find the angle of
     * @return angle of the position
     */
    public float getPosAngle(Position pos) {
        return ringAngles[pos.getRing()] + (420f / ringSlotCounts[pos.getRing()]) * pos.getSlot();
    }

    /**
     * PRIVATE
     * Change the position of a marble
     * Has basic error checking
     *
     * @param start starting position
     * @param end   ending position
     */
    private boolean changeMarblePosition(Position start, Position end) {
        int team = getTeamFromPosition(start);

        if (team == -1) {
            return false;
        }

        if (getTeamFromPosition(end) != -1 && !start.equals(end)) {
            return false;
        }

        if (end.getRing() == ringSlotCounts.length - 1) {
            Log.d(TAG, String.format("changeMarblePosition: Marble of team %d tried to drop on slot %d of final ring",
                    team, end.getSlot()));
            if (team == end.getSlot()) {
                end.setPosition(-1, random.nextInt());
            } else {
                end.setPosition(-2, random.nextInt());
            }
        } else if (end.getRing() >= 0) {
            // make marble drop further if it can
            float angle = ringAngles[end.getRing()] + (420f / ringSlotCounts[end.getRing()]) * end.getSlot();
            int targetSlot = closestSlot(end.getRing() + 1, angle, true);
            float targetAngle = ringAngles[end.getRing() + 1] + (420f / ringSlotCounts[end.getRing() + 1]) * targetSlot;
            float dist = angleDist(angle, targetAngle);

            if (dist < 0.001) {
                Position end1 = new Position(end.getRing() + 1, targetSlot);
                if (getTeamFromPosition(end1) == -1) {
                    if (changeMarblePosition(start, end1)) {
                        return true;
                    }
                    end = end1;
                }
            }
        }

        marblesByPosition.remove(start);
        marblesByPosition.put(end, team);

        Position[] positions = getPositionsFromTeam(team);
        for (int i = 0; i < 5; i++) {
            if (positions[i].equals(start)) {
                positions[i] = end;
                break;
            }
        }
        marblesByTeam.put(team, positions);

        if (start.getRing() <= 0) {
            return !start.equals(end);
        }

        // make upper slot drop too if it can
        int cRing = start.getRing();
        float angle;
        if (end.getRing() < 0) {
            angle = ringAngles[cRing] + (420f / ringSlotCounts[cRing]) * start.getSlot();
        } else {
            angle = ringAngles[end.getRing()] + (420f / ringSlotCounts[end.getRing()]) * end.getSlot();
        }
        int targetSlot = closestSlot(cRing - 1, angle, true);
        float targetAngle = ringAngles[cRing - 1] + (420f / ringSlotCounts[cRing - 1]) * targetSlot;
        float dist = angleDist(angle, targetAngle);

        if (dist < 0.001) {
            Position start1 = new Position(cRing - 1, targetSlot);
            changeMarblePosition(start1, start);
        }

        return !start.equals(end);
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


    /**
     * Stores the data necessary after calculating the elegibility of a slot for being dropped
     */
    private static class DropResult {
        protected float dist;
        protected Position pos;
        protected Position targetPos;

        protected DropResult(float dist, Position pos, Position targetPos) {
            this.dist = dist;
            this.pos = pos;
            this.targetPos = targetPos;
        }
    }

    /**
     * Stores the data of a slot and its target
     */
    private static class DropCandidate {
        protected Position pos;
        protected Position targetPos;

        protected DropCandidate(Position pos, Position targetPos) {
            this.pos = pos;
            this.targetPos = targetPos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DropCandidate that = (DropCandidate) o;
            return Objects.equals(targetPos, that.targetPos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetPos);
        }
    }
}
