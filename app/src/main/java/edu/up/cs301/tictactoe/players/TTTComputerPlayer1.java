package edu.up.cs301.tictactoe.players;

import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.infoMessage.NotYourTurnInfo;
import edu.up.cs301.game.GameFramework.utilities.Logger;
import edu.up.cs301.tictactoe.tttActionMessage.TTTMoveAction;

/**
 * This is a really dumb computer player that always just makes a random move
 * it's so stupid that it sometimes tries to make moves on non-blank spots.
 * 
 * @author Steven R. Vegdahl
 * @versio2 July 2013 
 */
public class TTTComputerPlayer1 extends GameComputerPlayer
{
    /*
     * Constructor for the TTTComputerPlayer1 class
     */
    public TTTComputerPlayer1(String name) {
        // invoke superclass constructor
        super(name); // invoke superclass constructor
    }

    /**
     * Called when the player receives a game-state (or other info) from the
     * game.
     * 
     * @param info
     * 		the message from the game
     */
    @Override
    protected void receiveInfo(GameInfo info) {
    	
    	// if it was a "not your turn" message, just ignore it
    	if (info instanceof NotYourTurnInfo) return;
        Logger.log("TTTComputer", "My turn!");
    	// pick x and y positions at random (0-2)
    	int xVal = (int)(3*Math.random());
    	int yVal = (int)(3*Math.random());

    	// delay for a second to make opponent think we're thinking
    	sleep(1);

    	// Submit our move to the game object. We haven't even checked it it's
    	// our turn, or that that position is unoccupied. If it was not our turn,
    	// we'll get a message back that we'll ignore. If it was an illegal move,
    	// we'll end up here again (and possibly again, and again). At some point,
    	// we'll end up randomly pick a move that is legal.
        Logger.log("TTTComputer", "Sending move");
    	game.sendAction(new TTTMoveAction(this, yVal, xVal));
    	
    }
}
