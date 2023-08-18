package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.view.MenuItem;

public class homeScreen extends AppCompatActivity implements View.OnClickListener{
    private Dialog dialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //Initialize the Dialog after setContentView()
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.difficulty_menu);

        Button howToPlayBtn = findViewById(R.id.howToPlayBtn);
        howToPlayBtn.setOnClickListener(this);

        //on long click toast message
        howToPlayBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(homeScreen.this, "How to Play", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

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

        Button newGameButton = findViewById(R.id.newGameBtn);
        newGameButton.setOnClickListener(this);

        Button aboutUs = findViewById(R.id.aboutUsBtn);
        aboutUs.setOnClickListener(this);

        //on long click toast message
        aboutUs.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(homeScreen.this, "About Us", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.howToPlayBtn:
                Intent intent = new Intent(homeScreen.this, howToPlayPage.class);
                startActivity(intent);
                break;
            case R.id.newGameBtn:
                dialog.show();
                break;
            case R.id.aboutUsBtn:
                Intent intent2 = new Intent(homeScreen.this, about_us.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    private void startGameScreen(String difficulty) {
        Intent intent = new Intent(homeScreen.this, gameScreen.class);
        intent.putExtra("difficulty", difficulty);
        startActivity(intent);
    }
}