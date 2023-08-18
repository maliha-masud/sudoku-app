package com.example.sudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
    }
}