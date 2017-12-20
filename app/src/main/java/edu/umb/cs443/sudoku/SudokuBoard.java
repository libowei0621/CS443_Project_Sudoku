package edu.umb.cs443.sudoku;

import java.util.HashSet;
import java.util.LinkedList;
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
        fullBoard = generator();

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

        while(count < 17)
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
     * Clean the value for current position
     */
    public void cleanCurrValue(){
        if(currX == 10 || currY == 10)
            return;
        currBoard[currX][currY] = 0;
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
     * Set the current board
     */
    public void setCurrBoard(int[][] currBoard) {
        this.currBoard = currBoard;
        currX = currY = 10;
    }

    /**
     * Set the default board
     */
    public void setDefaultBoard(int[][] defaultBoard) {
        this.defaultBoard = defaultBoard;
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
            for(int j = 0; j < 9; j++)
                if(currBoard[i][j] == 0)
                    return false;

        return true;
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

    /***************************************************************************************
     *
     *                            Soduku Board generation algorithm
     *
     *      Because of time limitation, I only implement a algorithm to generate a valid sudoku
     *      board. It is not a good sudoku board because it may have more than one solution.
     *      I learn this algorithm online:
     *
     *      1. Fill the first row with nine different numbers.
     *      2. Fill the second row which is a shift of the first line by three slots.
     *      3. Fill the third row which is a shift of the second line by three slots.
     *      4. Fill the fourth row which is a shift of the third by one slot.
     *
     *          line 1: 8 9 3  2 7 6  4 5 1
     *          line 2: 2 7 6  4 5 1  8 9 3 (shift 3)
     *          line 3: 4 5 1  8 9 3  2 7 6 (shift 3)
     *
     *          line 4: 5 1 8  9 3 2  7 6 4 (shift 1)
     *          line 5: 9 3 2  7 6 4  5 1 8 (shift 3)
     *          line 6: 7 6 4  5 1 8  9 3 2 (shift 3)
     *
     *          line 7: 6 4 5  1 8 9  3 2 7 (shift 1)
     *          line 8: 1 8 9  3 2 7  6 4 5 (shift 3)
     *          line 9: 3 2 7  6 4 5  1 8 9 (shift 3)
     ***************************************************************************************/

    private int[][] generator(){
        int[][] result = new int[9][9];
        LinkedList<Integer> nums = new LinkedList<>();
        Random ran = new Random();

        for(int i = 0; i < 9; i++)
            nums.add(i+1);

        for(int i = 0; i < 9; i++)
        {
            int index = ran.nextInt(nums.size());
            result[0][i] = nums.remove(index);
        }

        for(int i = 1; i < 9; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                if(i == 3 || i == 6)
                {
                    int shift = (j + 1) % 9;
                    result[i][j] = result[i-1][shift];
                }
                else
                {
                    int shift = (j + 3) % 9;
                    result[i][j] = result[i-1][shift];
                }
            }
        }

        return result;
    }
}
