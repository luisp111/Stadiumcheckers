package edu.up.cs301;

import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

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

/* @author Eric Imperio
 * @version Spring 2020
 * This is used to verify the functionality of the Framework is intact
 */
@RunWith(RobolectricTestRunner.class)
public class FrameworkTests {

    //Look for these TODOs in the document
    //TODO: Change to your game Main Activity
    public TTTMainActivity activity;

    @Before
    public void setup() throws Exception {
        //TODO: Change to your game Main Activity
        activity = Robolectric.buildActivity(TTTMainActivity.class).create().resume().get();
    }

    //This verifies the default config has the same port number as the activity
    //It also verifies the test_gameConfig returns true
    @Test
    public void test_createDefaultConfig(){
        GameConfig gameConfig = activity.createDefaultConfig();
        Pair<Boolean, String> pair = gameConfig.test_gameConfig();
        assertTrue(pair.second , pair.first);
        assertEquals("Port Num Mismatch: GameConfig=" +gameConfig.getPortNum() + " != Activity=" + activity.PORT_NUMBER, gameConfig.getPortNum(), activity.PORT_NUMBER);
    }

    //Verifies a local Game can be created
    @Test
    public void test_createLocalGame(){
        //TODO: Change to your game State
        LocalGame localGame = activity.createLocalGame(new TTTState());
        assertTrue("GameState was null", localGame.getGameState() != null);
    }

    //Verifies the configuration can be saved and loaded
    @Test
    public void test_saveConfig(){
        GameConfig gameConfig = activity.createDefaultConfig();
        gameConfig.removePlayer(1);
        gameConfig.addPlayer("Test", 0);
        String fileName = "savedTestConfig0000.dat";
        assertTrue("Failed to Save GameConfig", gameConfig.saveConfig(fileName, activity));
        GameConfig restoreConfig = activity.createDefaultConfig();
        assertTrue("Failed to Load GameConfig",restoreConfig.restoreSavedConfig(fileName, activity));
        assertEquals("Did not properly reload GameConfig", restoreConfig, gameConfig);
    }

    //"Clicks" the save Config Button to verify that works
    @Test
    public void test_saveConfigButton(){
        GameConfig gameConfig = activity.getConfig();
        //TODO: This add and remove player might need to be modified based on default player count and number of types
        ((EditText) activity.tableRows.get(1).findViewById(R.id.playerNameEditText)).setText("Test");
        ((Spinner) activity.tableRows.get(1).findViewById(R.id.playerTypeSpinner)).setSelection(4);
        gameConfig.removePlayer(1);
        gameConfig.addPlayer("Test",4);
        View view = activity.findViewById(R.id.saveConfigButton);
        activity.onClick(view);
        //TODO: Change to your game Main Activity
        TTTMainActivity reloadActivity = Robolectric.buildActivity(TTTMainActivity.class).create().resume().get();
        assertTrue("Failed to Load GameConfig",reloadActivity != null);
        GameConfig restoreConfig = reloadActivity.getConfig();
        assertEquals("Did not properly reload GameConfig", restoreConfig, gameConfig);
    }

    //NOTICE: Tests the toString conversions of saving
    //Tests that your game can be saved and loaded
    @Test
    public void test_save_game() {
        //Make a game state here
        //TODO: Change to your game State
        TTTState tttState = new TTTState();
        tttState.setPiece(0,0,'X');
        tttState.setPiece(1,1, 'O');
        tttState.setPiece(0,2,'X');

        //Call save game (In Main)
        Saving.writeToFile(tttState, R.string.app_name + "_test", activity);

        //Test by remaking the state
        //TODO: Change to your game State
        TTTState newtttState = new TTTState((TTTState) Saving.readFromFile(R.string.app_name + "_test", activity));

        //Check the game states are the same
        assertTrue(tttState.equals(newtttState));
    }

    //Tests players can be added to the max and removed to the min
    @Test
    public void test_add_delete_player() {
        int before = activity.tableRows.size();
        int maxPlayers = activity.getConfig().getMaxPlayers();
        int minPlayers = activity.getConfig().getMinPlayers();
        View view = activity.findViewById(R.id.addPlayerButton);
        for(int i = before; i < maxPlayers; i++) {
            activity.onClick(view);
            assertEquals("Couldn't add player #" + before,before+=1,activity.tableRows.size());
        }
        assertEquals("Couldn't add players to the max player size",before, maxPlayers);
        activity.onClick(view);
        assertEquals("Was able to add above the max player count",maxPlayers, activity.tableRows.size());
        //view = activity.findViewById(R.id.delPlayerButton);
        for(int i = before; i > minPlayers; i--) {
            activity.onClick(activity.tableRows.get(i-1).findViewById(R.id.delPlayerButton));
            assertEquals("Couldn't remove player #" + before, before-=1, activity.tableRows.size());
        }
        activity.onClick(activity.tableRows.get(minPlayers-1).findViewById(R.id.delPlayerButton));
        assertEquals("Was able to remove below the minimum player count",minPlayers,activity.tableRows.size());
    }

    //Verifies a game can be started
    @Test
    public void test_startGame(){
        View view = activity.findViewById(R.id.playGameButton);
        activity.onClick(view);
        assertFalse("Game was null", activity.isGameNull());
    }

    //Verifies Toast can be turned on and off
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

    // Verifies Debug mode can be turned on and off
    @Test
    public void test_logger_debug() {
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
    //Verifies setGameOver still sets the Game as over
    @Test
    public void test_setGameOver(){
        activity.setGameOver(true);
        assertTrue("setGameOver has been overritten but isGameOver wasn't modified", activity.getGameOver());
        activity.setGameOver(false);
        assertFalse("setGameOver has been overritten but isGameOver wasn't modified", activity.getGameOver());
    }

    //Verifies that human players support and require a GUI while Computer Players don't
    //Tests if activities are equal
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
