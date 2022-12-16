package edu.up.cs301;

import android.view.MotionEvent;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import edu.up.cs301.game.GameFramework.actionMessage.MyNameIsAction;
import edu.up.cs301.game.GameFramework.actionMessage.ReadyAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.R;
import edu.up.cs301.tictactoe.TTTLocalGame;
import edu.up.cs301.tictactoe.TTTMainActivity;
import edu.up.cs301.tictactoe.infoMessage.TTTState;
import edu.up.cs301.tictactoe.tttActionMessage.TTTMoveAction;

import static org.junit.Assert.*;

/* @author Eric Imperio
 * @version 2020
 * Use this as a template to make your own tests
 * These are good tests to use
 * Additional tests are good as well
 * NOTE: Avoid tests that simply check one action.
 *    Example: You know that the following will set the expected value.
 *        a = b + 2;
 */
@RunWith(RobolectricTestRunner.class)
public class TicTacToeTests {

    public TTTMainActivity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(TTTMainActivity.class).create().resume().get();
    }

    //This does a full game to verify it works
    // Notice that it includes invalid moves
    // You can do it this way or have multiple unit tests that do this
    // Sometimes easier to just have one since this is turn-based
    @Test
    public void test_checkGamePlay() {
        //TODO: Modify the following for your game
        //Starting the game
        View view = activity.findViewById(R.id.playGameButton);
        activity.onClick(view);
        //Getting the created game
        TTTLocalGame tttLocalGame = (TTTLocalGame) activity.getGame();
        //Getting the players
        GamePlayer[] gamePlayers = tttLocalGame.getPlayers();
        //Sending the names of the players to the game
        for (GamePlayer gp : gamePlayers) {
            tttLocalGame.sendAction(new MyNameIsAction(gp, gp.getClass().toString()));
        }
        //Telling the game everyone is ready
        for (GamePlayer gp : gamePlayers) {
            tttLocalGame.sendAction(new ReadyAction(gp));
        }
        //TODO: Start Making moves here
        GamePlayer player1 = gamePlayers[0];
        GamePlayer player2 = gamePlayers[1];
        //Can I make two moves in a row?
        tttLocalGame.sendAction(new TTTMoveAction(player1, 0, 0));
        tttLocalGame.sendAction(new TTTMoveAction(player1, 1, 1));
        //Setting the expected outcome of the two lines above
        TTTState match = new TTTState();
        match.setPiece(0, 0, 'X');
        match.setWhoseMove(1);
        //Testing that I couldn't make two moves in a row
        assertTrue("Game States were not equal", ((TTTState) tttLocalGame.getGameState()).equals(match));
        //Can I overwrite an opponents move
        tttLocalGame.sendAction(new TTTMoveAction(player2, 0, 0));
        //Make sure nothing changed
        assertTrue("Game States were not equal", ((TTTState) tttLocalGame.getGameState()).equals(match));
        //Make sure turns do in fact work
        tttLocalGame.sendAction(new TTTMoveAction(player2, 0, 1));
        tttLocalGame.sendAction(new TTTMoveAction(player1, 1, 1));
        //Expected changes from two lines above
        match.setPiece(0, 1, 'O');
        match.setPiece(1, 1, 'X');
        //Make sure those changes happened
        assertTrue("Game States were not equal", ((TTTState) tttLocalGame.getGameState()).equals(match));
        //Get to a finished game
        tttLocalGame.sendAction(new TTTMoveAction(player2, 0, 2));
        tttLocalGame.sendAction(new TTTMoveAction(player1, 2, 2));
        //Expected Changes from the two lines above
        match.setPiece(0, 2, 'O');
        match.setPiece(2, 2, 'X');
        //Check those worked
        assertTrue("Game States were not equal", ((TTTState) tttLocalGame.getGameState()).equals(match));
        //Make sure player 1 won
        assertEquals("Player 1 did not win", 0, tttLocalGame.whoWon());
        //Check if you can move after game over
        tttLocalGame.sendAction(new TTTMoveAction(player2, 1, 2));
        assertTrue("Game States were not equal", ((TTTState) tttLocalGame.getGameState()).equals(match));
    }

    //Tests focused on the state: copy constructors and equals
    //copy cons:  empty default state, in progress state, full board state

    //This tests the copy constructor when nothing is set
    @Test
    public void test_CopyConstructorOfState_Empty(){
        TTTState tttState = new TTTState();
        TTTState copyState = new TTTState(tttState);
        assertTrue("Copy Constructor did not produce equal States", tttState.equals(copyState));
    }

    //Make state that looks like a game that'd be in progress
    @Test
    public void test_CopyConstructorOfState_InProgress(){
        TTTState tttState = new TTTState();
        tttState.setWhoseMove(1);
        tttState.setPiece(1,1, 'X');
        tttState.setPiece(0,0, 'O');
        tttState.setPiece( 1, 2, 'X');
        TTTState copyState = new TTTState(tttState);
        assertTrue("Copy Constructor did not produce equal States", tttState.equals(copyState));
    }

    // Make a state that has all values set to something (preferably not default)
    @Test
    public void test_CopyConstructorOfState_Full(){
        TTTState tttState = new TTTState();
        tttState.setPiece(0,0, 'X');
        tttState.setPiece(0,1, 'O');
        tttState.setPiece( 0, 2, 'X');
        tttState.setPiece(1,0, 'O');
        tttState.setPiece( 1, 1, 'X');
        tttState.setPiece(1,2, 'O');
        tttState.setPiece( 2, 0, 'X');
        tttState.setPiece(2,1, 'O');
        tttState.setPiece( 2, 2, 'X');
        tttState.setWhoseMove(1);
        TTTState copyState = new TTTState(tttState);
        assertTrue("Copy Constructor did not produce equal States", tttState.equals(copyState));
    }

    //These follow the same structure as copy but they test your equals method
    // Copy might fail because your equals is wrong
    // DO NOT make equals use copy while copy is using equals. You won't know which is broken easily.
    //Equals
    @Test
    public void test_Equals_State_Empty(){
        TTTState tttState = new TTTState();
        TTTState otherState = new TTTState();
        assertTrue("Equals method did not agree the States where equal", tttState.equals(otherState));
    }

    @Test
    public void test_Equals_State_InProgress(){
        TTTState tttState = new TTTState();
        tttState.setWhoseMove(1);
        tttState.setPiece(1,1, 'X');
        tttState.setPiece(0,0, 'O');
        tttState.setPiece( 1, 2, 'X');
        TTTState otherState = new TTTState();
        otherState.setWhoseMove(1);
        otherState.setPiece(1,1, 'X');
        otherState.setPiece(0,0, 'O');
        otherState.setPiece( 1, 2, 'X');
        assertTrue("Equals method did not agree the States where equal", tttState.equals(otherState));
    }

    @Test
    public void test_Equals_State_Full(){
        TTTState tttState = new TTTState();
        tttState.setPiece(0,0, 'X');
        tttState.setPiece(0,1, 'O');
        tttState.setPiece( 0, 2, 'X');
        tttState.setPiece(1,0, 'O');
        tttState.setPiece( 1, 1, 'X');
        tttState.setPiece(1,2, 'O');
        tttState.setPiece( 2, 0, 'X');
        tttState.setPiece(2,1, 'O');
        tttState.setPiece( 2, 2, 'X');
        tttState.setWhoseMove(1);
        TTTState otherState = new TTTState();
        otherState.setPiece(0,0, 'X');
        otherState.setPiece(0,1, 'O');
        otherState.setPiece( 0, 2, 'X');
        otherState.setPiece(1,0, 'O');
        otherState.setPiece( 1, 1, 'X');
        otherState.setPiece(1,2, 'O');
        otherState.setPiece( 2, 0, 'X');
        otherState.setPiece(2,1, 'O');
        otherState.setPiece( 2, 2, 'X');
        otherState.setWhoseMove(1);
        assertTrue("Equals method did not agree the States where equal", tttState.equals(otherState));
    }
}

