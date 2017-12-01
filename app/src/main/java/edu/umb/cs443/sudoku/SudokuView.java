package edu.umb.cs443.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Set;

/**
 * Created by Bowei on 2017/11/10.
 */

public class SudokuView extends View {

    private int screenWidth;        //Screen's width
    private int boxWidth;           //Each box's width

    SudokuBoard board;              //Game board object

    Set<Integer> faultNums;         //Set of numbers which cannot be pick for
                                    //current position

    private Paint linePaint;        //Lines for drawing game map;
    private Paint defaultBox;       //Gray Box
    private Paint clickBos;         //Box that on current select
    private Paint defaultNum;       //Number that map gives to user
    private Paint userNum;          //Number that user pick to the map
    private Paint pickNum;          //Number that user can pick for current box

    private float textX;            //X position for text in box
    private float textY;            //BaseLine offset for text in box

    private int counter;            //For button click count;

    public SudokuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SudokuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SudokuView(Context context) {
        super(context);
        init();
    }

    private void init() {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        boxWidth = (screenWidth - 40) / 9;

        textX = boxWidth / 2;
        textY = boxWidth / 4;

        board = SudokuBoard.getBoard();

        initPaint();

        invalidate();
    }

    private void initPaint() {
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3f);

        defaultBox = new Paint();
        defaultBox.setColor(Color.GRAY);
        defaultBox.setStyle(Paint.Style.FILL);

        clickBos = new Paint();
        clickBos.setColor(Color.GREEN);
        clickBos.setStyle(Paint.Style.FILL);

        //Dark Gray number represent default numbers
        defaultNum = new Paint();
        defaultNum.setColor(Color.DKGRAY);
        defaultNum.setTextSize(boxWidth * 0.65f);
        defaultNum.setTextAlign(Paint.Align.CENTER);
        defaultNum.setAntiAlias(true);

        //Black number represent user input
        userNum = new Paint();
        userNum.setColor(Color.BLACK);
        userNum.setTextSize(boxWidth * 0.65f);
        userNum.setTextAlign(Paint.Align.CENTER);
        userNum.setAntiAlias(true);

        //Bigger number represent choices
        pickNum = new Paint();
        pickNum.setColor(Color.BLACK);
        pickNum.setTextSize(boxWidth * 0.65f + 15);
        pickNum.setTextAlign(Paint.Align.CENTER);
        pickNum.setAntiAlias(true);

        counter = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(screenWidth, screenWidth + boxWidth + 20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMap(canvas);

        drawCurrentData(canvas);

        drawChoiceNums(canvas);
    }

    private void drawMap(Canvas canvas) {

        // draw box
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int x = i / 3;
                int y = j / 3;

                //drawing four gray big boxes
                if(x == 1 && y != 1)
                {
                    canvas.drawRect(boxWidth * j + 20, 20 + boxWidth * i,
                            boxWidth * j + 20 + boxWidth, 20 + boxWidth
                                    * i + boxWidth, defaultBox);
                }
                else if(y == 1 && x != 1)
                {
                    canvas.drawRect(boxWidth * j + 20, 20 + boxWidth * i,
                            boxWidth * j + 20 + boxWidth, 20 + boxWidth
                                    * i + boxWidth, defaultBox);
                }

            }
        }

        // draw lines
        for (int i = 0; i < 10; i++) {
            canvas.drawLine(20, boxWidth * i + 20, 9 * boxWidth + 20,
                    boxWidth * i + 20, linePaint);
            canvas.drawLine(boxWidth * i + 20, 20, boxWidth * i
                    + 20, 9 * boxWidth + 20, linePaint);
        }

        // draw init numbers
        int[][] defaultBoard = board.getDefaultBoard();

        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(defaultBoard[i][j] != 0){
                    canvas.drawText(Integer.toString(defaultBoard[i][j]), boxWidth * i + 20 + textX,
                            boxWidth * j + 20 + boxWidth - textY, defaultNum);
                }
            }
        }
    }

    private void drawChoiceNums(Canvas canvas) {

        //offset for choice's position
        float startY = screenWidth + 10;

        //update fault numbers set
        faultNums = board.getCurrFaultNums();

        for(int i = 0; i < 9; i++)
        {
            if(!faultNums.contains(i+1))
                canvas.drawText(Integer.toString(i + 1), i * boxWidth + textX+ 20,
                        startY + (boxWidth - textY), pickNum);
        }
    }

    private void drawCurrentData(Canvas canvas){

        // draw current position
        canvas.drawRect(boxWidth * board.getCurrX() + 20, boxWidth * board.getCurrY() + 20,
                boxWidth * board.getCurrX() + 20 + boxWidth, boxWidth * board.getCurrY()
                        + 20 + boxWidth, clickBos);

        // draw user data
        int[][] defaultMap = board.getDefaultBoard();
        int[][] currentMap = board.getCurrBoard();

        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                if(defaultMap[i][j] == 0 && currentMap[i][j] != 0)
                    canvas.drawText(Integer.toString(currentMap[i][j]), boxWidth * i + 20 + textX,
                            boxWidth * j + 20 + boxWidth - textY, userNum);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // reset the cheat counter
        counter = 0;

        // if event is not click, ignore it
        if(event.getAction() != MotionEvent.ACTION_DOWN)
            return super.onTouchEvent(event);

        // if click position out of game board, ignore it
        if(event.getX() < 20 || event.getY() < 20 || event.getX() > screenWidth - 20)
            return super.onTouchEvent(event);

        // calculate click position
        int x = (int)((event.getX() - 20) / boxWidth);
        int y = (int)((event.getY() - 20) / boxWidth);

        // if click on the game board
        if(event.getY() < screenWidth - 20) {
            if(board.isClickable(x, y))
                board.setCurrPosition(x, y);
        } else { // if click on the choices
            if(x < 9 && !faultNums.contains(x + 1))
                board.setCurrValue(x + 1);
        }

        // redraw the view
        invalidate();

        // if game is done, show the finish dialog
        if(board.isFinish())
            ((MainActivity) getContext()).showDialog();

        return true;
    }

    // if user click restart button three times continuously,
    // complete the whole map.
    public void cheat(){
        counter++;

        if(counter > 2)
        {
            board.completeCurrMap();
            counter = 0;
        }

        invalidate();
    }

    // reset current board to default board
    public void cleanBoard(){
        board.setToDefault();
        invalidate();
    }

    // create a new game board for new game
    public void newStart(){
        board = board.newGame();
        invalidate();
    }
}
