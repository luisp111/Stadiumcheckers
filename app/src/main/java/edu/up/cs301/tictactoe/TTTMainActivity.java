package edu.up.cs301.tictactoe;

import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.utilities.Logger;
import edu.up.cs301.game.GameFramework.utilities.Saving;
import edu.up.cs301.game.R;
import edu.up.cs301.game.GameFramework.gameConfiguration.GameConfig;
import edu.up.cs301.game.GameFramework.gameConfiguration.GamePlayerType;
import edu.up.cs301.tictactoe.infoMessage.TTTState;
import edu.up.cs301.tictactoe.players.TTTComputerPlayer1;
import edu.up.cs301.tictactoe.players.TTTComputerPlayer2;
import edu.up.cs301.tictactoe.players.TTTHumanPlayer1;
import edu.up.cs301.tictactoe.players.TTTHumanPlayer2;

/**
 * this is the primary activity for Counter game
 * 
 * @author Steven R. Vegdahl
 * @author Eric Imperio
 * @version July 2020
 */
public class TTTMainActivity extends GameMainActivity {
	//Tag for logging
	private static final String TAG = "TTTMainActivity";
	public static final int PORT_NUMBER = 5213;

	/**
	 * a tic-tac-toe game is for two players. The default is human vs. computer
	 */
	@Override
	public GameConfig createDefaultConfig() {

		// Define the allowed player types
		ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();
		
		// yellow-on-blue GUI
		playerTypes.add(new GamePlayerType("Local Human Player (blue-yellow)") {
			public GamePlayer createPlayer(String name) {
				return new TTTHumanPlayer1(name, R.layout.ttt_human_player1);
			}
		});
		
		// red-on-yellow GUI
		playerTypes.add(new GamePlayerType("Local Human Player (yellow-red)") {
			public GamePlayer createPlayer(String name) {
				return new TTTHumanPlayer1(name, R.layout.ttt_human_player1_flipped);
			}
		});

		// note that most games don't require a second human player class
		playerTypes.add(new GamePlayerType("Local Human Player (game of 33)") {
			public GamePlayer createPlayer(String name) {
				return new TTTHumanPlayer2(name);
			}
		});
		
		// dumb computer player
		playerTypes.add(new GamePlayerType("Computer Player (dumb)") {
			public GamePlayer createPlayer(String name) {
				return new TTTComputerPlayer1(name);
			}
		});
		
		// smarter computer player
		playerTypes.add(new GamePlayerType("Computer Player (smart)") {
			public GamePlayer createPlayer(String name) {
				return new TTTComputerPlayer2(name);
			}
		});

		// Create a game configuration class for Tic-tac-toe
		GameConfig defaultConfig = new GameConfig(playerTypes, 2,2, "Tic-Tac-Toe", PORT_NUMBER);

		// Add the default players
		defaultConfig.addPlayer("Human", 0); // yellow-on-blue GUI
		defaultConfig.addPlayer("Computer", 3); // dumb computer player

		// Set the initial information for the remote player
		defaultConfig.setRemoteData("Remote Player", "", 1); // red-on-yellow GUI
		
		//done!
		return defaultConfig;
		
	}//createDefaultConfig


	/**
	 * createLocalGame
	 * 
	 * Creates a new game that runs on the server tablet,
	 * @param gameState
	 * 				the gameState for this game or null for a new game
	 * 
	 * @return a new, game-specific instance of a sub-class of the LocalGame
	 *         class.
	 */
	@Override
	public LocalGame createLocalGame(GameState gameState){
		if(gameState == null)
			return new TTTLocalGame();
		return new TTTLocalGame((TTTState) gameState);
	}

	/**
	 * saveGame, adds this games prepend to the filename
	 *
	 * @param gameName
	 * 				Desired save name
	 * @return String representation of the save
	 */
	@Override
	public GameState saveGame(String gameName) {
		return super.saveGame(getGameString(gameName));
	}

	/**
	 * loadGame, adds this games prepend to the desire file to open and creates the game specific state
	 * @param gameName
	 * 				The file to open
	 * @return The loaded GameState
	 */
	@Override
	public GameState loadGame(String gameName){
		String appName = getGameString(gameName);
		super.loadGame(appName);
		Logger.log(TAG, "Loading: " + gameName);
		return (GameState) new TTTState((TTTState) Saving.readFromFile(appName, this.getApplicationContext()));
	}

}
