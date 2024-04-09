package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.Locale;
import java.util.Random;

public class gameScreen extends AppCompatActivity {
    private Dialog dialog, dialog2;
    private TextView timeTextView;
    private CountDownTimer timer;
    private long startTime; private long endTime; private long elapsedTime = 0;
    private int mistakes = 0;
    int finalScore = 0;

    private Button selectedButton = null;

    // Saving the highest score and number of games finished
    // SharedPreferences initialization moved to onCreate
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int highestScore;
    private int gamesFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        // Initialize SharedPreferences inside onCreate
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        highestScore = sharedPreferences.getInt("highest_score", 0);
        gamesFinished = sharedPreferences.getInt("games_finished", 0);

        //initialize the Dialogs
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.lost_page);

        dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout.pause_pop_up);

        //button to go back to home screen
        ImageButton goBackBtn = findViewById(R.id.goBackButton);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create an Intent to navigate to another activity
                Intent intent = new Intent(gameScreen.this, homeScreen.class);

                //start the new activity
                startActivity(intent);
            }
        });

        Button okBtn = dialog.findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to another activity
                Intent intent = new Intent(gameScreen.this, homeScreen.class);

                // Start the new activity
                startActivity(intent);
            }
        });

        Button undoBtn = findViewById(R.id.UndoBtn);
        Button hintBtn = findViewById(R.id.hintBtn);
        ImageButton pauseBtn = findViewById(R.id.pauseButton);

        //display time
        timeTextView = findViewById(R.id.time_text);
        startTime = System.currentTimeMillis();
        timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedTime / 1000) % 60;
                int minutes = (int) ((elapsedTime / (1000*60)) % 60);
                String timeString = String.format(Locale.getDefault(), "Time: %02d:%02d", minutes, seconds);
                endTime = System.currentTimeMillis() - startTime;
                timeTextView.setText(timeString);
            }

            @Override
            public void onFinish() {
                // not used
            }
        };

        //if game being continued
        Intent intent = getIntent();
        if (intent.hasExtra("time")) {

        }

        TextView mistakesTextView = findViewById(R.id.mistakes_textview);
        TextView scoreTextView = findViewById(R.id.score_text);

        // ----------------------- display screen - initial functions -----------------------
        timer.start();

        String difficulty = getIntent().getStringExtra("difficulty");
        TextView levelText = findViewById(R.id.level_text);
        levelText.setText("Level:  " + difficulty);

        boardGenerator b = new boardGenerator();
        String boardString = b.readBoardFile(this, "boards.txt");
        //make board into array
        int[]board = new int[82];
        for (int i = 1; i <= 81; i++) {
            board[i] = Character.getNumericValue(boardString.charAt(i-1));
        }

        Button[] buttons = new Button[82];
        //populate the array with the buttons in the grid
        for (int i = 1; i <= 81; i++) {
            String buttonIdString = "btn" + i;
            int buttonId = getResources().getIdentifier(buttonIdString, "id", getPackageName());
            buttons[i] = findViewById(buttonId);
        }

        for(int i = 1; i <= 81; i++)
        {
            int num = board[i];
            buttons[i].setText(Integer.toString(num));
        }

        //store the original board
        int answers[] = new int[82];
        for (int i = 1; i <= 81; i++){
            answers[i] = Integer.parseInt(buttons[i].getText().toString());
        }

        //to fill the board with numbers in random positions
        int[] randomArray = new int[82];
        Random rand = new Random();
        for (int i = 1; i <= 81; i++) {
            randomArray[i] = i;
        }
        for (int i = 1; i < randomArray.length; i++) {
            int j = rand.nextInt(i) + 1;
            int temp = randomArray[i];
            randomArray[i] = randomArray[j];
            randomArray[j] = temp;
        }

        //display the number of filled cells according to level
        for (int i = 1; i <= 81; i++) {
            if (difficulty.equals("Easy") && i <= 30) {
                buttons[randomArray[i]].setTextColor(Color.WHITE);
                buttons[randomArray[i]].setEnabled(true);
                mistakesTextView.setText("Mistakes: 0/5");
            } else if (difficulty.equals("Medium") && i <= 41) {
                buttons[randomArray[i]].setTextColor(Color.WHITE);
                buttons[randomArray[i]].setEnabled(true);
                mistakesTextView.setText("Mistakes: 0/4");
            } else if (difficulty.equals("Hard") && i <= 50) {
                buttons[randomArray[i]].setTextColor(Color.WHITE);
                buttons[randomArray[i]].setEnabled(true);
                mistakesTextView.setText("Mistakes: 0/3");
            } else {
                //if the text color is not white, disable the button
                if (buttons[randomArray[i]].getCurrentTextColor() != Color.WHITE) {
                    buttons[randomArray[i]].setTextColor(Color.BLACK);
                    buttons[randomArray[i]].setEnabled(false);
                }
            }
        }

        // ----------------------- end of display initial screen -----------------------

        //change color of selected button (only if it is not pre-filled)
        //implements undo for wrongly filled cells, and hint for empty cells (only once)
        for (int i = 1; i <= 81; i++) {
            Button button = buttons[i];
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //only add listener to "empty" cells or wrongly-filled cells
                        if (button.isEnabled()){
                            //if a different cell was already selected, unselect it
                            if (selectedButton != null && selectedButton != button && selectedButton.getCurrentTextColor() != Color.RED) {
                                selectedButton.setBackgroundColor(Color.WHITE);
                                selectedButton.setTextColor(Color.WHITE);
                            }
                            else if (selectedButton != null && selectedButton.getCurrentTextColor() == Color.RED)
                            {
                                selectedButton.setBackgroundColor(Color.WHITE);
                                selectedButton.setTextColor(Color.RED);
                            }
                            //select the new cell
                            selectedButton = (Button) view;
                            selectedButton.setBackgroundColor(Color.parseColor("#ADD8E6"));
                            if (selectedButton != null && selectedButton.getCurrentTextColor() != Color.RED) {
                                selectedButton.setTextColor(Color.parseColor("#ADD8E6"));
                                //if hint clicked
                                hintBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        selectedButton.setBackgroundColor(Color.WHITE);
                                        String buttonId = getResources().getResourceEntryName(selectedButton.getId());
                                        int index = Integer.parseInt(buttonId.substring(3));
                                        int answer = answers[index];
                                        selectedButton.setText(Integer.toString(answer));
                                        selectedButton.setTextColor(Color.BLACK);
                                        selectedButton.setEnabled(false);
                                        selectedButton = null;
                                        //hint button cannot be used again
                                        hintBtn.setPaintFlags(hintBtn.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                        hintBtn.setEnabled(false);
                                    }
                                });
                            }
                            else {
                                selectedButton.setTextColor(Color.RED);
                                //if undo clicked
                                undoBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        selectedButton.setBackgroundColor(Color.parseColor("#ADD8E6"));
                                        selectedButton.setTextColor(Color.parseColor("#ADD8E6"));
                                    }
                                });
                            }
                    }
                }
            });
        }

        //add the clicked number button to the selected grid cell
        for (int i = 1; i <= 9; i++) {
            String buttonIdString = "click" + i;
            int buttonId = getResources().getIdentifier(buttonIdString, "id", getPackageName());
            Button numberButton = findViewById(buttonId);
            numberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedButton != null) {
                        String buttonId = getResources().getResourceEntryName(selectedButton.getId());
                        int index = Integer.parseInt(buttonId.substring(3));
                        int number = Integer.parseInt(((Button) view).getText().toString());
                        int answer = answers[index];
                        selectedButton.setText(Integer.toString(number));
                        if (answer == number){ //if user entered correct number
                            selectedButton.setBackgroundColor(Color.WHITE);
                            selectedButton.setTextColor(Color.BLUE);
                            selectedButton.setEnabled(false);

                            //update score according to level of difficulty
                            if (difficulty.equals("Easy"))
                                finalScore += 50;
                            else if (difficulty.equals("Medium"))
                                finalScore += 80;
                            else if (difficulty.equals("Hard"))
                                finalScore += 110;
                            scoreTextView.setText("Score: " + finalScore);
                        }
                        else {
                            selectedButton.setBackgroundColor(Color.WHITE);
                            selectedButton.setTextColor(Color.RED);
                            boolean endGame = false;
                            //update number of mistakes on the screen
                            if (difficulty.equals("Easy")){
                                mistakesTextView.setText("Mistakes: " + ++mistakes + "/5");
                                if (mistakes == 5) endGame = true;
                            }
                            else if (difficulty.equals("Medium")){
                                mistakesTextView.setText("Mistakes: " + ++mistakes + "/4");
                                if (mistakes == 4) endGame = true;
                            }
                            else if (difficulty.equals("Hard")){
                                mistakesTextView.setText("Mistakes: " + ++mistakes + "/3");
                                if (mistakes == 3) endGame = true;
                            }
                            if(endGame){
                                timer.cancel(); //stop the timer
                                dialog.show(); //go to lost game page
                            }
                        }
                        //clear the selected button and its background color
                        selectedButton = null;
                        if(checkComplete(buttons, answers)) {
                            timer.cancel();

                            if (finalScore > highestScore) {
                                editor.putInt("highest_score", finalScore);
                                editor.apply();
                            }
                            gamesFinished++;
                            editor.putInt("games_finished", gamesFinished);
                            editor.apply();

                            //create intent
                            Intent intent = new Intent(gameScreen.this, congratsPage.class);
                            intent.putExtra("level", difficulty);
                            intent.putExtra("time", endTime);
                            intent.putExtra("score", finalScore);
                            //start the new activity
                            startActivity(intent);
                        }
                    }
                }
            });
        }

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //record elapsed time
                elapsedTime = System.currentTimeMillis() - startTime;
                timer.cancel();
                //get references to the buttons in the popup
                Button continueBtn = dialog2.findViewById(R.id.continueBtn);
                Button goToHomeBtn = dialog2.findViewById(R.id.goToHomeBtn);

                //continue button clicked
                continueBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                        //new start time based on the elapsed time before pausing
                        startTime = System.currentTimeMillis() - elapsedTime;
                        timer.start();
                    }
                });

                //go to home button clicked
                goToHomeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(gameScreen.this, homeScreen.class);
                        startActivity(intent);
                    }
                });

                //show the dialog
                dialog2.show();
            }
        });
    }

    protected boolean checkComplete(Button buttons[], int answers[])
    {
        for (int i = 1; i <= 81; i++) {
            if (buttons[i].isEnabled()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }
}
