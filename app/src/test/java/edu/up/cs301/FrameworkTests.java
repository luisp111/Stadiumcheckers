package edu.up.cs301;

import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.GameConfig;
import edu.up.cs301.game.GameFramework.utilities.Logger;
import edu.up.cs301.game.GameFramework.utilities.Saving;
import edu.up.cs301.game.R;
import edu.up.cs301.tictactoe.TTTMainActivity;
import edu.up.cs301.tictactoe.infoMessage.TTTState;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class FrameworkTests {

    //Change to your game Main Activity
    public TTTMainActivity activity;

    @Before
    public void setup() throws Exception {
        activity = Robolectric.buildActivity(TTTMainActivity.class).create().resume().get();
    }

    @Test
    public void test_createDefaultConfig(){
        GameConfig gameConfig = activity.createDefaultConfig();
        Pair<Boolean, String> pair = gameConfig.test_gameConfig();
        assertTrue(pair.second , pair.first);
        assertEquals("Port Num Mistach: GameConfig=" +gameConfig.getPortNum() + " != Activity=" + activity.PORT_NUMBER, gameConfig.getPortNum(), activity.PORT_NUMBER);
    }

    @Test
    public void test_createLocalGame(){
        LocalGame localGame = activity.createLocalGame(new TTTState());
        assertTrue("GameState was null", localGame.getGameState() != null);
    }

    @Test
    public void test_saveConfig(){
        GameConfig gameConfig = activity.createDefaultConfig();
        gameConfig.addPlayer("Test", 0);
        String fileName = "savedTestConfig0000.dat";
        assertTrue("Failed to Save GameConfig", gameConfig.saveConfig(fileName, activity));
        GameConfig restoreConfig = activity.createDefaultConfig();
        assertTrue("Failed to Load GameConfig",restoreConfig.restoreSavedConfig(fileName, activity));
        assertEquals("Did not properly reload GameConfig", restoreConfig, gameConfig);
    }

    @Test
    public void test_saveConfigButton(){
        GameConfig gameConfig = activity.createDefaultConfig();
        gameConfig.addPlayer("Test", 0);
        View view = activity.findViewById(R.id.saveConfigButton);
        activity.onClick(view);
        assertTrue("Failed to Load GameConfig",Robolectric.buildActivity(TTTMainActivity.class).create().resume().get() != null);
        GameConfig restoreConfig = activity.getConfig();
        assertEquals("Did not properly reload GameConfig", restoreConfig,gameConfig);
    }

    // Make saveGame return the string value and reloadGame or something accept one
    //NOTICE: Tests the toString conversions of saving
    @Test
    public void test_save_game() {
        //Make a game state here
        TTTState tttState = new TTTState();
        tttState.setPiece(0,0,'X');
        tttState.setPiece(1,1, 'O');
        tttState.setPiece(0,2,'X');

        //Call save game (In Main)
        Saving.writeToFile(tttState, R.string.app_name + "_test", activity);
        //System.out.println(saved_string);
        //Test by remaking the state
        TTTState newtttState = new TTTState((TTTState) Saving.readFromFile(R.string.app_name + "_test", activity));
        //Check the game states are the same

        assertTrue(tttState.equals(newtttState));
    }

    @Test
    public void add_delete_player_test() {
        int before = activity.tableRows.size();
        int maxPlayers = activity.getConfig().getMaxPlayers();
        if( before == maxPlayers) {
            assert true;
            return;
        }
        View view = activity.findViewById(R.id.addPlayerButton);
        for(int i = before; i < maxPlayers; i++) {
            activity.onClick(view);
            assertEquals("Couldn't add player #" + before,before+=1,activity.tableRows.size());
        }
        assertEquals("Couldn't add players to the max player size",before, maxPlayers);
        view = activity.findViewById(R.id.delPlayerButton);
        for(int i = maxPlayers; i > -1; i--) {
            activity.onClick(view);
            assertEquals("Couldn't remove player #" + before, before-=1, activity.tableRows.size());
        }
        assertEquals(0,activity.tableRows.size());
    }

    @Test
    public void startGame_test(){
        View view = activity.findViewById(R.id.playGameButton);
        activity.onClick(view);
        assertFalse("Game was null", activity.isGameNull());
    }

    @Test
    public void logger_toast_test(){
        Logger.setContext(activity.getApplicationContext());
        Logger.setToastValue(true); //For Testing the switch
        View view = activity.findViewById(R.id.onScreenLogging);
        ((CheckBox)view).setChecked(false);
        activity.onClick(view);
        assertFalse("Toast value of logger was not false",Logger.getToastValue());
        ((CheckBox)view).setChecked(true);
        activity.onClick(view);
        assertTrue("Toast value of logger was not true", Logger.getToastValue());
    }

    @Test
    public void logger_debug_test() {
        Logger.setContext(activity.getApplicationContext());
        Logger.setDebugValue(true); //For Testing the switch
        View view = activity.findViewById(R.id.debugLogging);
        ((CheckBox)view).setChecked(false);
        activity.onClick(view);
        assertFalse("Debug value of logger was not false",Logger.getDebugValue());
        ((CheckBox)view).setChecked(true);
        activity.onClick(view);
        assertTrue("Debug value of logger was not true", Logger.getDebugValue());
    }

    /* Why test setGameOver? It's valid to assume students may want to override this method to add functionality
     *    Nothing wrong with that override; however, it still needs to set gameOver to the value given
     */
    @Test
    public void setGameOver_test(){
        activity.setGameOver(true);
        assertTrue("setGameOver has been overritten but isGameOver wasn't modified", activity.getGameOver());
        activity.setGameOver(false);
        assertFalse("setGameOver has been overritten but isGameOver wasn't modified", activity.getGameOver());
    }

    @Test
    public void test_players(){
        View view = activity.findViewById(R.id.playGameButton);
        activity.onClick(view);
        assertTrue("Game was not an instanceof LocalGame", activity.getGame() instanceof LocalGame);
        LocalGame localGame = (LocalGame) activity.getGame();
        GamePlayer[] gamePlayers = localGame.getPlayers();
        for(GamePlayer gamePlayer: gamePlayers) {
            if(gamePlayer instanceof GameHumanPlayer) {
                assertTrue("Human Player "+gamePlayer.toString()+" does not require a gui.", gamePlayer.requiresGui());
                assertTrue("Human Player "+gamePlayer.toString()+" does not support a gui.", gamePlayer.supportsGui());
                assertEquals("Activities were not equal", activity, gamePlayer.getActivity());
            }
            if(gamePlayer instanceof GameComputerPlayer) {
                assertFalse("Computer Player "+gamePlayer.toString()+" requires a gui.", gamePlayer.requiresGui());
                assertNull("Activity was not null before being set", gamePlayer.getActivity());
                gamePlayer.gameSetAsGui(activity);
                assertEquals("Activities were not equal", activity, gamePlayer.getActivity());
            }
        }
    }
}