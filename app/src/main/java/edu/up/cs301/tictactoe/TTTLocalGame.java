package edu.up.cs301.tictactoe;

import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.tictactoe.infoMessage.TTTState;
import edu.up.cs301.tictactoe.tttActionMessage.TTTMoveAction;

/**
 * The TTTLocalGame class for a simple tic-tac-toe game.  Defines and enforces
 * the game rules; handles interactions with players.
 * 
 * @author Steven R. Vegdahl
 * @author Eric Imperio
 * @version January 2020
 */

public class TTTLocalGame extends LocalGame {
	//Tag for logging
	private static final String TAG = "TTTLocalGame";

	// the marks for player 0 and player 1, respectively
	private final static char[] mark = {'X','O'};

	// the number of moves that have been played so far, used to
	// determine whether the game is over
	protected int moveCount;

	/**
	 * Constructor for the TTTLocalGame.
	 */
	public TTTLocalGame() {

		// perform superclass initialization
		super();

		// create a new, unfilled-in TTTState object
		super.state = new TTTState();
	}

	/**
	 * Constructor for the TTTLocalGame with loaded tttState
	 * @param tttState
	 */
	public TTTLocalGame(TTTState tttState){
		super();
		super.state = new TTTState(tttState);
	}

	/**
	 *  This is where you should initialize anything specific to the
	 *  number of players.  For example you may need to init your
	 *  game state or part of it.  Loading data could also happen here.
	 *
	 * 	 @param players
	 */
	@Override
	public void start(GamePlayer[] players)
	{
		super.start(players);
	}

	/**
	 * Check if the game is over. It is over, return a string that tells
	 * who the winner(s), if any, are. If the game is not over, return null;
	 * 
	 * @return
	 * 		a message that tells who has won the game, or null if the
	 * 		game is not over
	 */
	@Override
	protected String checkIfGameOver() {

		// the idea is that we simultaneously look at a row, column and
		// a diagonal, using the variables 'rowToken', 'colToken' and
		// 'diagToken'; we do this three times so that we get all three
		// rows, all three columns, and both diagonals.  (The way the
		// math works out, one of the diagonal tests tests the middle
		// column.)  The respective variables get set to ' ' unless
		// all characters in the line that have currently been seen are
		// identical; in this case the variable contains that character

		// the character that will eventually contain an 'X' or 'O' if we
		// find a winner
		char resultChar = ' ';

		TTTState state = (TTTState) super.state;

		// to all three lines in the current group
		for (int i = 0; i < 3; i++) {
			// get the initial character in each line
			char rowToken = state.getPiece(i,0);
			char colToken = state.getPiece(0,i);;
			char diagToken = state.getPiece(0,i);
			// determine the direction that the diagonal moves
			int diagDelta = 1-i;
			// look for matches for each of the three positions in each
			// of the current lines; set the corresponding variable to ' '
			// if a mismatch is found
			for (int j = 1; j < 3; j++) {
				if (state.getPiece(i,j) != rowToken) rowToken = ' ';
				if (state.getPiece(j,i) != colToken) colToken = ' ';
				if (state.getPiece(j, i+(diagDelta*j)) != diagToken) diagToken = ' ';
			}

			////////////////////////////////////////////////////////////
			// At this point, if any of our three variables is non-blank
			// then we have found a winner.
			////////////////////////////////////////////////////////////

			// if we find a winner, indicate such by setting 'resultChar'
			// to the winning mark.
			if (rowToken != ' ') resultChar = rowToken;
			else if (colToken != ' ') resultChar = colToken;
			else if (diagToken != ' ') resultChar = diagToken;
		}

		// if resultChar is blank, we found no winner, so return null,
		// unless the board is filled up. In that case, it's a cat's game.
		if (resultChar == ' ') {
			if  (moveCount >= 9) {
				// no winner, but all 9 spots have been filled
				return "It's a cat's game.";
			}
			else {
				return null; // no winner, but game not over
			}
		}

		// if we get here, then we've found a winner, so return the 0/1
		// value that corresponds to that mark; then return a message
		int gameWinner = resultChar == mark[0] ? 0 : 1;
		return playerNames[gameWinner]+" is the winner.";
	}

	/**
	 * Notify the given player that its state has changed. This should involve sending
	 * a GameInfo object to the player. If the game is not a perfect-information game
	 * this method should remove any information from the game that the player is not
	 * allowed to know.
	 * 
	 * @param p
	 * 			the player to notify
	 */
	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {
		// make a copy of the state, and send it to the player
		p.sendInfo(new TTTState(((TTTState) state)));

	}

	/**
	 * Tell whether the given player is allowed to make a move at the
	 * present point in the game. 
	 * 
	 * @param playerIdx
	 * 		the player's player-number (ID)
	 * @return
	 * 		true iff the player is allowed to move
	 */
	protected boolean canMove(int playerIdx) {
		return playerIdx == ((TTTState)state).getWhoseMove();
	}

	/**
	 * Makes a move on behalf of a player.
	 * 
	 * @param action
	 * 			The move that the player has sent to the game
	 * @return
	 * 			Tells whether the move was a legal one.
	 */
	@Override
	protected boolean makeMove(GameAction action) {

		// get the row and column position of the player's move
		TTTMoveAction tm = (TTTMoveAction) action;
		TTTState state = (TTTState) super.state;

		int row = tm.getRow();
		int col = tm.getCol();

		// get the 0/1 id of our player
		int playerId = getPlayerIdx(tm.getPlayer());

		// if that space is not blank, indicate an illegal move
		if (state.getPiece(row, col) != ' ') {
			return false;
		}

		// get the 0/1 id of the player whose move it is
		int whoseMove = state.getWhoseMove();

		// place the player's piece on the selected square
		state.setPiece(row, col, mark[playerId]);

		// make it the other player's turn
		state.setWhoseMove(1 - whoseMove);

		// bump the move count
		moveCount++;

		// return true, indicating the it was a legal move
		return true;
	}

	//TESTING

	public int whoWon(){
		String gameOver = checkIfGameOver();
		if(gameOver == null || gameOver.equals("It's a cat's game.")) return -1;
		if(gameOver.equals(playerNames[0]+" is the winner.")) return 0;
		return 1;
	}
}
