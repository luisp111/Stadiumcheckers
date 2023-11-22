package edu.up.cs301.stadiumcheckers.players;

import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.stadiumcheckers.Position;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;
import edu.up.cs301.stadiumcheckers.scActionMessage.SCRotateAction;

public class SCMastermindComputerPlayer extends GameComputerPlayer {
    private static final String TAG = "SCMastermindComputerPlayer";

    // weightings for ai consideration
    // how strongly the ai considers total distance to the end ring
    private static final float absoluteDistanceWeight = 8f;
    // how strongly the ai considers the total angular distance to the target slot
    private static final float angularDistanceWeight = -1f;
    // how strongly the ai considers the number of secured marbles on a turn
    private static final float securedMarblesWeight = 1000f;

    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public SCMastermindComputerPlayer(String name) {
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

        Position[] marbles = state.getPositionsFromTeam(playerNum);
        int targetAngle = playerNum * 105 + 42;

        // exhaustively deduce the best move
        float maxTotal = -999999;
        Position select = marbles[0];
        boolean selectDir = true;
        for (Position pos : marbles) {
            if (pos.getRing() < 0) {
                continue;
            }

            SCState cState = new SCState(state);
            cState.rotateRing(playerNum, pos, true);
            SCState ccState = new SCState(state);
            ccState.rotateRing(playerNum, pos, false);

            SCState[] states = {cState, ccState};
            float mTot = -999999;
            boolean dir = false;
            for (SCState s : states) {
                int absDist = 0;
                int secMarbs = 0;
                int angDist = 0;
                for (Position p : s.getPositionsFromTeam(playerNum)) {
                    if (p.getRing() == -1) {
                        secMarbs++;
                        continue;
                    }
                    if (p.getRing() == -2) {
                        absDist--;
                        continue;
                    }
                    angDist += state.angleDist(targetAngle, state.getPosAngle(p));
                    absDist += p.getRing();
                }

                float tot = absDist * absoluteDistanceWeight;
                tot += secMarbs * securedMarblesWeight;
                tot += angDist * angularDistanceWeight;
                if (tot > mTot) {
                    mTot = tot;
                    dir = !dir;
                }
            }

            if (mTot > maxTotal) {
                maxTotal = mTot;
                select = pos;
                selectDir = dir;
            }
        }

        game.sendAction(new SCRotateAction(this, select, selectDir));
    }
}
