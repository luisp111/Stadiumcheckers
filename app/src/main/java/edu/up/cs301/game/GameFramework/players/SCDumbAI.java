package edu.up.cs301.game.GameFramework.players;
import java.util.Random;

import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.stadiumcheckers.Position;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;
import edu.up.cs301.stadiumcheckers.players.SCHumanPlayer;

public abstract class SCDumbAI {
    private Random random;
    private static final String TAG = "SCDumbAIr";
    /**
     * Main constructor
     *
     * @param name     the name of the player
     * @param layoutId
     */
    public SCDumbAI(){
        this.random = new Random();
    }

    /*
    public Position selectNextMove(ScState state){
        Position[] marbles = getPositionsFromTeam(state.getCurrentTeamTurn());
        return marbles[random.nextInt(marbles.length)];
    }
     */
    public void makeRandomMove(SCState gameState){
        int team = gameState.getCurrentTeamTurn();

        // Gets all marbles for current team
        Position[] marbles = gameState.getPositionsFromTeam(team);

        // randomly selects a marble
        Position selectedMarble = marbles[random.nextInt(marbles.length)];

        // Randomly decide the direction of rotation (clockwise or counterclockwise
        boolean rotateClockwise = random.nextBoolean();

        // Simulate a slow rotation to drop the marble through the rings
        for (int step = 0; step < 10; step++){
            gameState.rotateRing(team, selectedMarble, rotateClockwise);

            // Add a delay to simulate a slow rotation
            try {
                Thread.sleep(100); // Adjust the sleep duration as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    //public SCDumbAI(String name, int layoutId) {
        //super(name, layoutId);
    //}

    protected abstract void receiveInfo(GameInfo info);
}
//check for positions from what I get goes by -2 and then do the reset action
// then move random marble with rotate
//use the getPositions from team
//getPositionsFromTeam
//