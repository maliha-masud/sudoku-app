package com.example.sudoku;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class congratsPage extends AppCompatActivity implements View.OnClickListener{
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats_page);

        //Retrieve the time and score values from the Intent
        Intent intent = getIntent();
        String Level = intent.getStringExtra("level");

        long elapsedTime = intent.getLongExtra("time", 0);
        //extract time
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000*60)) % 60);
        String timeString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        int score = intent.getIntExtra("score", 0);

        Button mainMenuButton = findViewById(R.id.main_menu_button);
        mainMenuButton.setOnClickListener(this);
        Button newGameButton = findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(this);

        Button goBackBtn = findViewById(R.id.main_menu_button);
        //on long click toast message
        goBackBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(congratsPage.this, "Main Menu", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        TextView timeText = findViewById(R.id.time_text);
        TextView levelText = findViewById(R.id.level_text);
        TextView scoreText = findViewById(R.id.score_text);

        levelText.setText("Level: " + Level);
        timeText.setText("Time: " + timeString);

        //factor the time taken in score
        //number of seconds calculate
        String[] timeParts = timeString.split(":");
        int minutesInSecs = Integer.parseInt(timeParts[0]) * 60;
        int secPart = Integer.parseInt(timeParts[1]);
        int totalTimeInSeconds = minutesInSecs + secPart;
        score += (int) round(13999/sqrt(totalTimeInSeconds));
        score = (int) round(score/5.0) * 5; //round to nearest 5
        scoreText.setText("Score: " + score);

        //initialize the Dialog after setContentView()
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.difficulty_menu);

        Button easyButton = dialog.findViewById(R.id.easy);
        easyButton.setOnClickListener(view -> {
            startGameScreen("Easy");
        });

        Button mediumButton = dialog.findViewById(R.id.medium);
        mediumButton.setOnClickListener(view -> {
            startGameScreen("Medium");
        });

        Button hardButton = dialog.findViewById(R.id.hard);
        hardButton.setOnClickListener(view -> {
            startGameScreen("Hard");
        });

        //store into file
        writeToFile(Level, score, timeString);
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_menu_button:
                Intent intent = new Intent(congratsPage.this, homeScreen.class);
                startActivity(intent);
                break;
            case R.id.new_game_button:
                dialog.show();
                break;
            default:
                break;
        }
    }
    private void startGameScreen(String difficulty) {
        Intent intent = new Intent(congratsPage.this, gameScreen.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }

    //store stats in file
    public void writeToFile(String level, int score, String timeString)
    {
        String filePath = "C:\\Users\\PC\\Documents\\AndroidStudio\\scores.txt";//scores.txt (file://DESKTOP-6EGM2VU/Users/PC/Documents/scores.txt)
        File file = new File(filePath);
        String data = ("Level: " + level + ", Score: " + score + ", Time: " + timeString);
        if (!file.exists()) {
            try {
                file.createNewFile();
                file.setWritable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter writer = new FileWriter(file, true); // Append to the file
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}