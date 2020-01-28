package edu.up.cs301.tictactoe;

import edu.up.cs301.game.GameFramework.infoMessage.GameState;

import static edu.up.cs301.game.GameFramework.utilities.Saving.SEPARATOR;
import static edu.up.cs301.game.GameFramework.utilities.Saving.ARRAY_SEPARATOR;
import static edu.up.cs301.game.GameFramework.utilities.Saving.SECOND_ARRAY_SEPARATOR;


/**
 * Contains the state of a Tic-Tac-Toe game.  Sent by the game when
 * a player wants to enquire about the state of the game.  (E.g., to display
 * it, or to help figure out its next move.)
 * 
 * @author Steven R. Vegdahl
 * @authoer Eric Imperio
 * @version July 2020
 */
public class TTTState extends GameState {
    //Tag for logging
    private static final String TAG = "TTTState";
	private static final long serialVersionUID = 7552321013488624386L;

    ///////////////////////////////////////////////////
    // ************** instance variables ************
    ///////////////////////////////////////////////////
	
	// the 3x3 array of char that represents the X's and O's on the board
    private char[][] board;
    
    // an int that tells whose move it is
    private int playerToMove;

    /**
     * Constructor for objects of class TTTState
     */
    public TTTState()
    {
        // initialize the state to be a brand new game
        board = new char[3][3];
        for (int i = 0; i < 3; i++) {
        	for (int j = 0; j < 3; j++) {
        		board[i][j] = ' ';
        	}
        }
        
        // make it player 0's move
        playerToMove = 0;
    }// constructor
    
    /**
     * Copy constructor for class TTTState
     *  
     * @param original
     * 		the TTTState object that we want to clong
     */
    public TTTState(TTTState original)
    {
    	// create a new 3x3 array, and copy the values from
    	// the original
    	board = new char[3][3];
    	for (int i = 0; i < 3; i++) {
    		for (int j = 0; j < 3; j++) {
    			board[i][j] = original.board[i][j];
    		}
    	}
    	
    	// copy the player-to-move information
        playerToMove = original.playerToMove;
    }

    /**
     * From String constructor for class TTTState
     * @param str
     *      a string representation of a previously saved TTTState
     */
    public TTTState(String str){
        String[] variables = str.split(SEPARATOR, -1);
        playerToMove = Integer.parseInt(variables[0]);
        String[] rows = variables[1].split(ARRAY_SEPARATOR, -1);
        board = new char[3][3];
        for(int i = 0 ; i < 3; i++){
            String[] values = rows[i].split(SECOND_ARRAY_SEPARATOR, -1);
            for(int j = 0; j < 3; j++){
                board[i][j] = values[j].toCharArray()[0];
            }
        }
    }

    /**
     * Find out which piece is on a square
     * 
     * @param row
	 *		the row being queried
     * @param col
     * 		the column being queried
     * @return
     * 		the piece at the given square; ' ' if no piece there;
     * 		'?' if it is an illegal square
     */
    public char getPiece(int row, int col) {
        // if we're out of bounds or anything, return '?';
        if (board == null || row < 0 || col < 0) return '?';
        if (row >= board.length || col >= board[row].length) return '?';

        // return the character that is in the proper position
        return board[row][col];
    }

    /**
     * Sets a piece on a square
     * 
     * @param row
     * 		the row being queried
     * @param
     * 		col the column being queried
     * @param
     * 		piece the piece to place
     */
    public void setPiece(int row, int col, char piece) {
        // if we're out of bounds or anything, return;
        if (board == null || row < 0 || col < 0) return;
        if (row >= board.length || col >= board[row].length) return;

        // return the character that is in the proper position
        board[row][col] = piece;
    }
    
    /**
     * Tells whose move it is.
     * 
     * @return the index (0 or 1) of the player whose move it is.
     */
    public int getWhoseMove() {
        return playerToMove;
    }
    
    /**
     * set whose move it is
     * @param id
     * 		the player we want to set as to whose move it is
     */
    public void setWhoseMove(int id) {
    	playerToMove = id;
    }

    /**
     *  equals, Compares the string representation of this gameState with object for equality
     * @param object
     *          The object to be compared with
     * @return true if equal or false if not equal
     */
    @Override
    public boolean equals(Object object){
        return this.toString().equals(object.toString());
    }

    /**
     * toString, makes a string representation of this instance
     * @return String representation of this instance
     */
    @Override
    public String toString(){
        String ret_val = this.playerToMove + SEPARATOR;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ret_val += this.board[i][j] + SECOND_ARRAY_SEPARATOR;
            }
            ret_val = ret_val.substring(0, ret_val.length()-3) + ARRAY_SEPARATOR;
        }
        ret_val = ret_val.substring(0,ret_val.length()-3);

        return ret_val;
    }
}
