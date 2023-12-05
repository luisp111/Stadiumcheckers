package edu.up.cs301.stadiumcheckers.infoMessage;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.HashMap;

import edu.up.cs301.stadiumcheckers.Position;

public class SCStateTest {
    @Test
    public void getTurnCount() {
        SCState state = new SCState();
        assertEquals(0, state.getTurnCount());
    }

    @Test
    public void setTurnCount() {
        SCState state = new SCState();
        state.setTurnCount(1);
        assertEquals(1, state.getTurnCount());
    }

    @Test
    public void getRingSlotCount() {
        SCState state = new SCState();
        assertEquals(20, state.getRingSlotCount(0));
        assertEquals(-1, state.getRingSlotCount(-1));
        assertEquals(-1, state.getRingSlotCount(99999));
    }

    @Test
    public void getRingAngle() {
        SCState state = new SCState();
        assertEquals(0f, state.getRingAngle(0), 0.001f);
        assertEquals(-1f, state.getRingAngle(-1), 0.001f);
        assertEquals(-1f, state.getRingAngle(99999), 0.001f);
        assertEquals(state.getRingAngle(1, true), state.getRingAngle(1), 0.001f);
        assertEquals(0f, state.getRingAngle(0, false), 0.001f);
    }

    @Test
    public void setRingAngle() {
        SCState state = new SCState();
        state.setRingAngle(-1, 63f);
        state.setRingAngle(99999, 63f);
        state.setRingAngle(1, 63f);
        assertEquals(54f, state.getRingAngle(1), 0.001f);
        assertEquals(-1f, state.getRingAngle(-1), 0.001f);
        assertEquals(-1f, state.getRingAngle(99999), 0.001f);
        assertEquals(state.getRingAngle(1, true), state.getRingAngle(1), 0.001f);
        assertEquals(63f, state.getRingAngle(1, false), 0.001f);
    }

    @Test
    public void getRingCount() {
        SCState state = new SCState();
        assertEquals(9, state.getRingCount());
    }

    @Test
    public void getRingAngles() {
        SCState state = new SCState();
        state.setRingAngle(1, 63f);
        float[] angles = state.getRingAngles();
        assertEquals(63f, angles[1], 0.001f);
        assertEquals(0f, angles[0], 0.001f);
        assertEquals(42f, angles[state.getRingCount() - 1], 0.001f);
    }

    @Test
    public void getCurrentTeamTurn() {
        SCState state = new SCState();
        assertEquals(0, state.getCurrentTeamTurn());
    }

    @Test
    public void setCurrentTeamTurn() {
        SCState state = new SCState();
        assertEquals(0, state.getCurrentTeamTurn());
        state.setCurrentTeamTurn(1);
        assertEquals(1, state.getCurrentTeamTurn());
    }

    @Test
    public void getTeamFromPosition() {
        SCState state = new SCState();
        assertEquals(-1, state.getTeamFromPosition(new Position(-999, 999)));
        assertEquals(-1, state.getTeamFromPosition(new Position(0, 25)));
        assertEquals(0, state.getTeamFromPosition(new Position(0, 0)));
        assertEquals(1, state.getTeamFromPosition(new Position(0, 5)));
        assertEquals(2, state.getTeamFromPosition(new Position(0, 10)));
        assertEquals(3, state.getTeamFromPosition(new Position(0, 15)));
    }

    @Test
    public void getPositionsFromTeam() {
        SCState state = new SCState();
        assertEquals(0, state.getPositionsFromTeam(-1).length);
        Position[] red = state.getPositionsFromTeam(0);
        assertEquals(5, red.length);
        assertEquals(0, state.getTeamFromPosition(red[0]));
        assertEquals(0, state.getTeamFromPosition(red[1]));
        assertEquals(0, state.getTeamFromPosition(red[2]));
        assertEquals(0, state.getTeamFromPosition(red[3]));
        assertEquals(0, state.getTeamFromPosition(red[4]));
    }

    @Test
    public void getMarblesByTeam() {
        SCState state = new SCState();
        HashMap<Integer, Position[]> mTeam = state.getMarblesByTeam();
        assertArrayEquals(mTeam.get(0), state.getPositionsFromTeam(0));
        assertArrayEquals(mTeam.get(1), state.getPositionsFromTeam(1));
        assertArrayEquals(mTeam.get(2), state.getPositionsFromTeam(2));
        assertArrayEquals(mTeam.get(3), state.getPositionsFromTeam(3));
    }

    @Test
    public void getMarblesByPosition() {
        SCState state = new SCState();
        HashMap<Position, Integer> mPos = state.getMarblesByPosition();
        Position test = new Position(0, 0);
        Integer t = mPos.get(test);
        assertNotNull(t);
        assertEquals(state.getTeamFromPosition(test), (int) t);
    }

    @Test
    public void closestSlot() {
        SCState state = new SCState();
        assertEquals(0, state.closestSlot(0, 0, true));
        assertEquals(0, state.closestSlot(0, 0, false));
    }

    @Test
    public void rotateRing() {
        // TODO: finish unit testing
    }

    @Test
    public void resetMarble() {
        SCState state = new SCState(2); // test state 2
        Position test = new Position(0);
        assertFalse(state.resetMarble(0, test, 0));
        Position test1 = new Position(-2, 0);
        assertFalse(state.resetMarble(1, test1, 0));
        Position test2 = new Position(-2, 5);
        assertFalse(state.resetMarble(0, test2, 0));
        assertTrue(state.resetMarble(0, test1, 0));
        assertEquals(0, state.getTeamFromPosition(new Position(0)));
        Position test3 = new Position(-2, 1);
        assertFalse(state.resetMarble(0, test3, 0));
        assertTrue(state.resetMarble(0, test3, 1));
        assertEquals(0, state.getTeamFromPosition(new Position(state.getRingCount() - 2,0)));
    }

    @Test
    public void angleDist() {
        SCState state = new SCState();
        float a1 = 50f;
        float a2 = 210f;
        assertEquals(state.angleDist(a1, a2, false), state.angleDist(a1, a2), 0.001f);
        assertEquals(state.angleDist(a1, a2), state.angleDist(a2, a1), 0.001f);
        assertEquals(160f, state.angleDist(a1, a2), 0.001f);
        assertEquals(state.angleDist(a1, a2, true), -state.angleDist(a2, a1, true), 0.001f);
        assertEquals(-160f, state.angleDist(a1, a2, true), 0.001f);
    }

    @Test
    public void getPosAngle() {
        SCState state = new SCState();
        assertEquals(0f, state.getPosAngle(new Position(0, 0)), 0.001f);
        assertEquals(21f, state.getPosAngle(new Position(0, 1)), 0.001f);
        assertEquals(42f, state.getPosAngle(new Position(state.getRingCount() - 1, 0)), 0.001f);
    }
}