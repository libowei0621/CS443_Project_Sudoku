package edu.umb.cs443.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;


public class MainActivity extends Activity {

    private SudokuView sudokuView;
    private AlertDialog finishDialog;               //dialog for the end of game

    private String saveName = "save.data";          //save file's name
    private String[] saveTitle;                     //Names for each save slot
    private String[] saveData;                      //save data for each save slot
    private int currSave = 0;                       //indicate which slot user choose

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sudokuView = (SudokuView) findViewById(R.id.board);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
        .setTitle("You solve the game.")
        .setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sudokuView.newStart();
            }
        })
        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                writeSaves();
                System.exit(1);
            }
        });
        finishDialog = builder.create();
    }


    @Override
    protected void onResume() {
        readSaves();
        super.onResume();
    }

    @Override
    protected void onPause() {
        writeSaves();
        super.onPause();
    }

    public void restartButton(View view){
        sudokuView.cleanBoard();
        sudokuView.cheat();
    }

    public void showFinishDialog(){
        finishDialog.show();
    }

    public void clean(View view){
        SudokuBoard.getBoard().cleanCurrValue();
        sudokuView.invalidate();
    }

    /***************************************************************************************
     *
     *                              Save and Load Part
     *
     ***************************************************************************************/
    //read data from local file
    private void readSaves(){
        File saveFile = new File(getFilesDir(), saveName);
        saveTitle = new String[5];
        saveData = new String[5];

        if(!saveFile.exists())
        {
            for(int i = 0; i < 5; i++) {
                saveTitle[i] = "Empty";
                saveData[i] = "0";
            }
        }
        else
        {
            FileInputStream fi = null;
            String result = "";

            try {
                fi = openFileInput(saveName);
                byte[] data = new byte[fi.available()];
                while((fi.read(data)) != -1)
                {
                    result = new String(data);
                }

                saveData = result.split(Pattern.quote("|"));

                for(int i = 0; i < 5; i++) {
                    if (saveData[i].length() == 1)
                        saveTitle[i] = "Empty";
                    else
                        saveTitle[i] = "Save " + (i + 1);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fi != null)
                        fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //write save data to the local file
    private void writeSaves(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++) {
            sb.append(saveData[i]);
            sb.append("|");
        }
        sb.append(saveData[4]);

        try{
            FileOutputStream fo = openFileOutput(saveName, MODE_PRIVATE);
            fo.write(sb.toString().getBytes());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //show the save&load dialog
    public void showSaveDialog(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setTitle("Save & Load")
                .setSingleChoiceItems(saveTitle, currSave, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currSave = i;
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder sb = new StringBuilder();
                        int[][] board = SudokuBoard.getBoard().getCurrBoard();
                        int[][] defaultBoard = SudokuBoard.getBoard().getDefaultBoard();

                        //save current board data
                        for(int x = 0; x < 9; x++)
                            for(int y = 0; y < 9; y++)
                                sb.append(board[x][y]);

                        sb.append("-");

                        //save current default number board data
                        for(int x = 0; x < 9; x++)
                            for(int y = 0; y < 9; y++)
                                sb.append(defaultBoard[x][y]);

                        saveData[currSave] = sb.toString();
                        saveTitle[currSave] = "Save " + (currSave + 1);

                        Toast.makeText(MainActivity.this,
                                "Save Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Load", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(saveTitle[currSave].equals("Empty")) {
                            Toast.makeText(MainActivity.this,
                                    "Empty slot", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int count = 0;
                        int[][] newBoard = new int[9][9];
                        int[][] newDefaultBoard = new int[9][9];

                        String[] data = saveData[currSave].split(Pattern.quote("-"));

                        //read data
                        for(int x = 0; x < 9; x++) {
                            for (int y = 0; y < 9; y++) {
                                int currNum = 0;

                                //board data
                                currNum = data[0].charAt(count) - '0';
                                newBoard[x][y] = currNum;

                                //default number data
                                currNum = data[1].charAt(count) - '0';
                                newDefaultBoard[x][y] = currNum;

                                count++;
                            }
                        }

                        SudokuBoard.getBoard().setCurrBoard(newBoard);
                        SudokuBoard.getBoard().setDefaultBoard(newDefaultBoard);

                        sudokuView.invalidate();
                        Toast.makeText(MainActivity.this,
                                "Load Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
}
