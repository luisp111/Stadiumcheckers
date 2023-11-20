package edu.up.cs301.stadiumcheckers.infoMessage;

import static org.junit.Assert.*;

import org.junit.Test;

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
    }

    @Test
    public void getRingAngles() {
    }

    @Test
    public void getCurrentTeamTurn() {
    }

    @Test
    public void setCurrentTeamTurn() {
    }

    @Test
    public void getTeamFromPosition() {
    }

    @Test
    public void getPositionsFromTeam() {
    }

    @Test
    public void getMarblesByTeam() {
    }

    @Test
    public void getMarblesByPosition() {
    }

    @Test
    public void closestSlot() {
    }

    @Test
    public void rotateRing() {
    }

    @Test
    public void resetMarble() {
    }
}