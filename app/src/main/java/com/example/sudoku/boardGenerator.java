package com.example.sudoku;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class boardGenerator {

    public String readBoardFile(Context context, String fileName) {
        try {
            // Open the file and get an InputStream to read from it
            InputStream inputStream = context.getAssets().open(fileName);

            //read lines from InputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            // Pick a random line from the list
            Random random = new Random();
            String randomLine = lines.get(random.nextInt(lines.size()));

            // Close the InputStream and BufferedReader
            reader.close();
            inputStream.close();

            return randomLine;

           /* // Use a BufferedReader to read lines from the InputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
            //pick a line ?
            }

            // Close the InputStream and BufferedReader
            reader.close();
            inputStream.close();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
