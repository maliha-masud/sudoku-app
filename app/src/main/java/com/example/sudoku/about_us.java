package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class about_us extends AppCompatActivity {

    //goBackHome
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ImageButton goBackBtn = findViewById(R.id.goBackButton);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to another activity
                Intent intent = new Intent(about_us.this, homeScreen.class);
                startActivity(intent);
            }
        });

        //on long click toast message
        goBackBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(about_us.this, "Go Back", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Retrieving the highest score and number of games finished
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        int highestScore = sharedPreferences.getInt("highest_score", 0);
        int gamesFinished = sharedPreferences.getInt("games_finished", 0);

        TextView score = findViewById(R.id.score);
        TextView games = findViewById(R.id.games_no);

        score.setText("High Score: " + highestScore);
        games.setText("Games Played: " + gamesFinished);
    }
}