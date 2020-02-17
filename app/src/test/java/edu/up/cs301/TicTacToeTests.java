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
import edu.up.cs301.tictactoe.players.TTTHumanPlayer1;
import edu.up.cs301.tictactoe.tttActionMessage.TTTMoveAction;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class TicTacToeTests {

    public TTTMainActivity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(TTTMainActivity.class).create().resume().get();
    }

    @Test
    public void test_checkGamePlay(){
        View view = activity.findViewById(R.id.playGameButton);
        activity.onClick(view);
        TTTLocalGame tttLocalGame = (TTTLocalGame) activity.getGame();
        GamePlayer[] gamePlayers = tttLocalGame.getPlayers();
        for(GamePlayer gp: gamePlayers){
            tttLocalGame.sendAction(new MyNameIsAction(gp,gp.getClass().toString()));
        }
        for(GamePlayer gp: gamePlayers){
            tttLocalGame.sendAction(new ReadyAction(gp));
        }
        tttLocalGame.sendAction(new TTTMoveAction(gamePlayers[0], 0,0));
        tttLocalGame.sendAction(new TTTMoveAction(gamePlayers[0], 1,1));
        TTTState match = new TTTState();
        match.setPiece(0,0,'X');
        match.setWhoseMove(1);
        assertTrue("Game States were not equal",((TTTState) tttLocalGame.getGameState()).equals(match));
        tttLocalGame.sendAction(new TTTMoveAction(gamePlayers[1], 0,0));
        assertTrue("Game States were not equal",((TTTState) tttLocalGame.getGameState()).equals(match));
        tttLocalGame.sendAction(new TTTMoveAction(gamePlayers[1], 0,1));
        tttLocalGame.sendAction(new TTTMoveAction(gamePlayers[0], 1,1));
        match.setPiece(0,1,'O');
        match.setPiece(1,1, 'X');
        assertTrue("Game States were not equal",((TTTState) tttLocalGame.getGameState()).equals(match));
        tttLocalGame.sendAction(new TTTMoveAction(gamePlayers[1], 0,3));
        tttLocalGame.sendAction(new TTTMoveAction(gamePlayers[0], 2,2));
        match.setPiece(0,2, 'O');
        match.setPiece(2,2, 'X');
        assertTrue("Game States were not equal",((TTTState) tttLocalGame.getGameState()).equals(match));
        assertEquals("Player 1 did not win",0, tttLocalGame.whoWon());
    }
}
