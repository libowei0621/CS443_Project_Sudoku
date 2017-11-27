package edu.umb.cs443.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Bowei on 2017/11/10.
 */

public class SudokuView extends View {

    private int screenWidth;        //Screen's width
    private int boxWidth;           //Each box's width

    private int[] falseChoice;      //List of number that cannot pick
                                    //for current box

    private Paint linePaint;        //Lines for drawing game map;
    private Paint defaultBox;       //Gray Box
    private Paint clickBos;         //Box that on current select
    private Paint defaultNum;       //Number that map gives to user
    private Paint userNum;          //Number that user pick to the map
    private Paint pickNum;          //Number that user can pick for current box

    private float textX;            //X position for text in box
    private float textY;            //BaseLine offset for text in box

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

        falseChoice = new int[9];

        for(int i = 0; i < 9; i++)
        {
            falseChoice[i] = i;
        }

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

        //Black number represent default numbers
        defaultNum = new Paint();
        defaultNum.setColor(Color.BLACK);
        defaultNum.setTextSize(boxWidth * 0.65f);
        defaultNum.setTextAlign(Paint.Align.CENTER);
        defaultNum.setAntiAlias(true);

        //Red number represent user input
        userNum = new Paint();
        userNum.setColor(Color.RED);
        userNum.setTextSize(boxWidth * 0.65f);
        userNum.setTextAlign(Paint.Align.CENTER);
        userNum.setAntiAlias(true);

        //Bigger number represent choices
        pickNum = new Paint();
        pickNum.setColor(Color.BLACK);
        pickNum.setTextSize(boxWidth * 0.65f + 15);
        pickNum.setTextAlign(Paint.Align.CENTER);
        pickNum.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(screenWidth, screenWidth + boxWidth + 20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawMap(canvas);

        //TODO draw current map numbers

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
        int[][] board = SudokuBoard.getBoard().getDefaultBoard();

        for(int i = 0; i < 9; i++){
            for(int j = 0; j < 9; j++){
                if(board[i][j] != 0){
                    canvas.drawText(Integer.toString(board[i][j] + 1), boxWidth * i + 20 + textX,
                            boxWidth * j + 20 + boxWidth - textY, defaultNum);
                }
            }
        }
    }

    private void drawChoiceNums(Canvas canvas) {

        //offset for choice's position
        float startY = screenWidth + 10;

        //TODO: draw numbers base on current box
        for(int i = 0; i < 9; i++)
        {
            canvas.drawText(Integer.toString(i+1), i * boxWidth + textX+ 20,
                    startY + (boxWidth - textY), pickNum);
        }
    }

}
