package edu.up.cs301.stadiumcheckers.infoMessage;

import java.util.HashMap;

import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.stadiumcheckers.Position;

public class SCState extends GameState {
    private final HashMap<Integer, Position[]> marblesByTeam;
    private final HashMap<Position, Integer> marblesByPosition;
    private final int[] ringSlotCounts = {20, 6, 7, 6, 5, 6, 4, 5, 4};
    private final float[] ringAngles;
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
     * @param state the constructor to copy values from
     */
    public SCState(SCState state) {
        turnCount = state.getTurnCount();
        currentTeamTurn = state.getCurrentTeamTurn();
        marblesByTeam = state.getMarblesByTeam();
        marblesByPosition = state.getMarblesByPosition();

        ringAngles = new float[ringSlotCounts.length];
        for (int i = 0; i < ringSlotCounts.length; i++) {
            ringAngles[i] = state.getRingAngle(i);
        }
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
        if (ring >= ringSlotCounts.length || ring < 0) {
            return -1f;
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

    public int getTeamFromPosition(int ring, int slot) {
        return getTeamFromPosition(new Position(ring, slot));
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
     * Changes the position of a marble, if possible
     * @param start position of marble to move
     * @param end where the marble should go
     * @return whether the action was successful
     */
    public boolean changeMarblePosition(Position start, Position end) {
        int team = getTeamFromPosition(start);
        if (team == -1 || getTeamFromPosition(end) != -1) {
            return false;
        }

        marblesByPosition.put(end, team);
        marblesByPosition.remove(start);

        Position[] positions = getPositionsFromTeam(team);
        for (int i = 0; i < 5; i++) {
            if (positions[i] == start) {
                positions[i] = end;
                break;
            }
        }
        marblesByTeam.put(team, positions);

        return true;
    }
}
