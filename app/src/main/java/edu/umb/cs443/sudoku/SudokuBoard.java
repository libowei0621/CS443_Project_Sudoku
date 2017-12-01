package edu.umb.cs443.sudoku;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
        if(x < 0 || y < 0 || x > 9 || y > 9)
            return;
        currX = x;
        currY = y;
    }

    /**
     * Set value for current position
     *
     * @param value the number for current box
     */
    public void setCurrValue(int value){
        if(currX == 10 || currY == 10)
            return;
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

    /**
     *  Get the set of numbers which cannot be pick for current position
     *
     * @return set of fault numbers
     */
    public Set<Integer> getCurrFaultNums(){
        Set<Integer> faultNums = new HashSet<>();

        if(currX == 10 || currY == 10)
            return faultNums;

        for(int i = 0; i < 9; i++) {
            if (currBoard[currX][i] != 0)
                faultNums.add(currBoard[currX][i]);

            if (currBoard[i][currY] != 0)
                faultNums.add(currBoard[i][currY]);
        }

        int x = currX/3 * 3;
        int y = currY/3 * 3;

        for(int i = x; i < x + 3; i++)
            for(int j = y; j < y + 3; j++)
                if(currBoard[i][j] != 0)
                    faultNums.add(currBoard[i][j]);

        return faultNums;
    }

    /**
     * Check the game is finish or not.
     * @return true if game is done, otherwise false;
     */
    public boolean isFinish(){
        for(int i = 0; i < 9; i++)
        {
            HashSet<Integer> row = new HashSet<>();
            HashSet<Integer> col = new HashSet<>();
            HashSet<Integer> box = new HashSet<>();

            for(int j = 0; j < 9; j++)
            {
                if(currBoard[i][j] == 0 || !row.add(currBoard[i][j]))
                    return false;

                if(currBoard[j][i] == 0 || !col.add(currBoard[j][i]))
                    return false;

                int RowIndex = 3*(i/3);
                int ColIndex = 3*(i%3);

                if(currBoard[RowIndex + j/3][ColIndex + j%3] == 0 ||
                        !box.add(currBoard[RowIndex + j/3][ColIndex + j%3]))
                    return false;
            }
        }

        return true;
    }

    /**
     * Helper function for cheating. It will solve the whole game except one box
     */
    public void completeCurrMap(){
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                currBoard[i][j] = fullBoard[i][j];

        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                if(defaultBoard[i][j] == 0)
                {
                    currBoard[i][j] = 0;
                    currX = i;
                    currY = j;
                    return;
                }
    }

    /**
     * Set current board to default board. Used for restart purpose
     */
    public void setToDefault(){
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                currBoard[i][j] = defaultBoard[i][j];

        currX = currY = 10;
    }

    /**
     * Reset the board instance. Used for new game purpose
     * @return new game board
     */
    public SudokuBoard newGame(){
        board = null;
        return getBoard();
    }
}
