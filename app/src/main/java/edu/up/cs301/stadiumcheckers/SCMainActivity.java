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
import edu.up.cs301.stadiumcheckers.players.SCDumbComputerPlayer;
import edu.up.cs301.stadiumcheckers.players.SCHumanPlayer;
import edu.up.cs301.stadiumcheckers.players.SCMastermindComputerPlayer;
import edu.up.cs301.stadiumcheckers.players.SCSmartComputerPlayer;

/**
 * Stadium Checkers
 * <p>
 * BETA RELEASE:
 * The rotation action still has some rare cases where the selected marble will
 * rotate in some unexpected way. It's rarer now, but still happens.
 * <p>
 * The ability to reset your marble manually isn't implemented yet, mainly due to the fact that
 * this isn't a very impactful feature and it'd take a pretty significant amount of time to
 * get working right. The game still feels perfectly fine without it.
 * <p>
 * Animations aren't present, as trying to get them in would take a *huge* amount of effort
 * due to the complexity of the game's singular action.
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

        // random selector ai
        playerTypes.add(new GamePlayerType("Random Randy") {
            public GamePlayer createPlayer(String name) {
                return new SCDumbComputerPlayer(name, 0);
            }
        });

        // lowest first ai
        playerTypes.add(new GamePlayerType("Downward Dale") {
            public GamePlayer createPlayer(String name) {
                return new SCDumbComputerPlayer(name, 1);
            }
        });

        // highest first ai
        playerTypes.add(new GamePlayerType("Flat Fenris") {
            public GamePlayer createPlayer(String name) {
                return new SCDumbComputerPlayer(name, 2);
            }
        });

        // smart ai
        playerTypes.add(new GamePlayerType("Smart Alec") {
            public GamePlayer createPlayer(String name) {
                return new SCSmartComputerPlayer(name);
            }
        });

        // exhaustive-search ai
        playerTypes.add(new GamePlayerType("Mastermind Michael") {
            public GamePlayer createPlayer(String name) {
                return new SCMastermindComputerPlayer(name);
            }
        });

        // Create a game configuration class for stadium checkers
        GameConfig defaultConfig = new GameConfig(playerTypes, 1, 4,
                "Stadium Checkers", PORT_NUMBER);

        // Add the default players
        defaultConfig.addPlayer("Human", 0);
        defaultConfig.addPlayer("Random Randy", 1);
        defaultConfig.addPlayer("Downward Dale", 2);
        defaultConfig.addPlayer("Flat Fenris", 3);

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
