package com.example.runfractions;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_RUN_MINUTES = 15;
    private static final int MAX_WALK_MINUTES = 5;

    RunStatus runStatus;

    Chronometer chronometer;
    NumberPicker runPicker;
    NumberPicker walkPicker;
    Button startButton;

    MediaPlayer player = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        runPicker = findViewById(R.id.run_picker);
        walkPicker = findViewById(R.id.walk_picker);

        setupNumPicker(runPicker, MAX_RUN_MINUTES);
        setupNumPicker(walkPicker, MAX_WALK_MINUTES);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setCountDown(true);
        chronometer.setOnChronometerTickListener(t -> {
            if (SystemClock.elapsedRealtime() - t.getBase() > 0) {
                countdownDone();
            }
        });

        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(this::start);
    }

    private void start(View v) {
        runStatus = RunStatus.WAITING;
        setChronometerTime(0, 5);
        chronometer.start();
    }

    private void countdownDone() {

        if (runStatus == RunStatus.WAITING) {
            run();
        } else if (runStatus == RunStatus.RUNNING) {
            walk();
        } else if (runStatus == RunStatus.WALKING) {
            run();
        }
    }

    private void run() {
        runStatus = RunStatus.RUNNING;
        setChronometerTime(runPicker.getValue(), 0);
        playAudio("run.mp3");
        chronometer.start();
    }

    private void walk() {
        runStatus = RunStatus.WALKING;
        setChronometerTime(walkPicker.getValue(), 0);
        playAudio("walk.mp3");
        chronometer.start();
    }

    private void playAudio(String fileName) {
        try {
            AssetFileDescriptor afd = getAssets().openFd(fileName);
            player.reset();
            player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupNumPicker(NumberPicker np, int maxValue) {
        np.setMinValue(1);
        np.setMaxValue(maxValue);
        String[] availables_run_values = new String[maxValue];
        for (int i = 0; i < maxValue; i++) {
            availables_run_values[i] = String.format("%d", i + 1);
        }
        np.setDisplayedValues(availables_run_values);
    }

    private void setChronometerTime(int minutes, int seconds) {
        long base = SystemClock.elapsedRealtime() + (minutes * 60000 + seconds * 1000);
        chronometer.setBase(base);
    }
}
