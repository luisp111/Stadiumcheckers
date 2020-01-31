package edu.up.cs301;

import org.junit.Test;

import edu.up.cs301.game.GameFramework.utilities.Saving;
import edu.up.cs301.game.R;
import edu.up.cs301.tictactoe.TTTState;
import edu.up.cs301.tictactoe.TTTMainActivity;

import static org.junit.Assert.*;

public class FrameworkTests {

    // Make saveGame return the string value and reloadGame or something accept one
    //NOTICE: Tests the toString conversions of saving
    @Test
    public void save_game_test() {
        //Make a game state here
        TTTState tttState = new TTTState();
        tttState.setPiece(0,0,'X');
        tttState.setPiece(1,1, 'O');
        tttState.setPiece(0,2,'X');

        TTTMainActivity tttMainActivity = new TTTMainActivity();
        //Call save game (In Main)
        Saving.writeToFile(tttState, R.string.app_name + "_test", tttMainActivity);
        //System.out.println(saved_string);
        //Test by remaking the state
        TTTState newtttState = new TTTState((TTTState) Saving.readFromFile("test", tttMainActivity));
        //Check the game states are the same
        assertTrue(tttState.equals(newtttState));
    }
}
