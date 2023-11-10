package edu.up.cs301.stadiumcheckers;

import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.GameConfig;
import edu.up.cs301.game.GameFramework.gameConfiguration.GamePlayerType;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.GameFramework.utilities.Logger;
import edu.up.cs301.game.GameFramework.utilities.Saving;
import edu.up.cs301.game.R;
import edu.up.cs301.stadiumcheckers.infoMessage.SCState;
import edu.up.cs301.stadiumcheckers.players.SCHumanPlayer;

/**
 * Stadium Checkers
 *
 * @author Jaden Barker
 * @author James Pham
 * @author Luis Perez
 * @author Mohammad Surur
 * @author Dylan Sprigg
 */
public class SCMainActivity extends GameMainActivity {
    //Tag for logging
    private static final String TAG = "SCMainActivity";
    public static final int PORT_NUMBER = 5213;

    @Override
    public GameConfig createDefaultConfig() {

        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // Main player
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new SCHumanPlayer(name, R.layout.sc_human_player);
            }
        });

        // Create a game configuration class for stadium checkers
        GameConfig defaultConfig = new GameConfig(playerTypes, 2, 2,
                "Stadium Checkers", PORT_NUMBER);

        // Add the default players
        defaultConfig.addPlayer("Human", 0);

        // Set the initial information for the remote player
        // defaultConfig.setRemoteData("Remote Player", "", 1);

        //done!
        return defaultConfig;

    }//createDefaultConfig


    /**
     * createLocalGame
     * <p>
     * Creates a new game that runs on the server tablet,
     *
     * @param gameState the gameState for this game or null for a new game
     * @return a new, game-specific instance of a sub-class of the LocalGame
     * class.
     */
    @Override
    public LocalGame createLocalGame(GameState gameState) {
        if (gameState == null)
            return new SCLocalGame();
        return new SCLocalGame((SCState) gameState);
    }

    /**
     * saveGame, adds this games prepend to the filename
     *
     * @param gameName Desired save name
     * @return String representation of the save
     */
    @Override
    public GameState saveGame(String gameName) {
        return super.saveGame(getGameString(gameName));
    }

    /**
     * loadGame, adds this games prepend to the desire file to open and creates the game specific state
     *
     * @param gameName The file to open
     * @return The loaded GameState
     */
    @Override
    public GameState loadGame(String gameName) {
        String appName = getGameString(gameName);
        super.loadGame(appName);
        Logger.log(TAG, "Loading: " + gameName);
        return (GameState) new SCState((SCState) Saving.readFromFile(appName, this.getApplicationContext()));
    }
}
