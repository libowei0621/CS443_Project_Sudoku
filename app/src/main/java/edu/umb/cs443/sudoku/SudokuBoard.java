package edu.umb.cs443.sudoku;

import java.util.Random;

/**
 * Created by Bowei on 2017/11/26.
 */

public class SudokuBoard {

    private static SudokuBoard board = null;

    private int[][] fullBoard;
    private int[][] defaultBoard;
    private int[][] currBoard;

    // current position
    private int currX;
    private int currY;

    /*
     * Singleton constructor
     */
    private SudokuBoard(){
        fullBoard = new int[][]{
                {4,3,8,7,9,6,2,1,5},
                {6,5,9,1,3,2,4,7,8},
                {2,7,1,4,5,8,6,9,3},
                {8,4,5,2,1,9,3,6,7},
                {7,1,3,5,6,4,8,2,9},
                {9,2,6,8,7,3,1,5,4},
                {1,9,4,3,2,5,7,8,6},
                {3,6,2,9,8,7,5,4,1},
                {5,8,7,6,4,1,9,3,2}
        };

        initDefaultBoard();
        initCurrBoard();
    }

    /**
     * Get SudokuBoard instance
     *
     * @return SudokuBoard instance
     */
    public static SudokuBoard getBoard(){
        if(board == null)
            board = new SudokuBoard();

        return board;
    }

    /*
     * init default board
     */
    private void initDefaultBoard(){
        defaultBoard = new int[9][9];

        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                defaultBoard[i][j] = 0;

        Random rand = new Random();
        int count = 0;

        while(count < 10)
        {
            int x = rand.nextInt(9);
            int y = rand.nextInt(9);

            if(defaultBoard[x][y] != 0)
                continue;

            defaultBoard[x][y] = fullBoard[x][y];
            count++;
        }
    }

    /*
     * init current board and current position
     */
    private void initCurrBoard(){
        currBoard = new int[9][9];
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                currBoard[i][j] = defaultBoard[i][j];

        currX = currY = 10;
    }

    /**
     * Get current x position
     *
     * @return current x position
     */
    public int getCurrX() {
        return currX;
    }

    /**
     * Get current y position
     *
     * @return current y position
     */
    public int getCurrY() {
        return currY;
    }

    /**
     * Set current position
     *
     * @param x current x position value
     * @param y current y position value
     */
    public void setCurrPosition(int x, int y){
        currX = x;
        currY = y;
    }

    /**
     * Set value for current position
     *
     * @param value the number for current box
     */
    public void setCurrValue(int value){
        currBoard[currX][currY] = value;
    }

    /**
     * To check is the input box available for user to change
     *
     * @param x x position
     * @param y y position
     * @return true for available, otherwise unavailable.
     */
    public boolean isClickable(int x, int y){
        return defaultBoard[x][y] == 0 ? true : false;
    }

    /**
     * Get the board with only default numbers
     *
     * @return default board
     */
    public int[][] getDefaultBoard() {
        return defaultBoard;
    }

    /**
     * Get the current board
     *
     * @return current board
     */
    public int[][] getCurrBoard() {
        return currBoard;
    }
}
