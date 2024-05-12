package com.example.pazapa;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    CountDownTimer timer;

    NumberPicker runPicker;
    NumberPicker walkPicker;
    Button startButton;
    Button stopButton;

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

        startButton = findViewById(R.id.start);
        startButton.setOnClickListener(this::start);

        stopButton = findViewById(R.id.stop);
        stopButton.setOnClickListener(this::stop);
    }

    private void stop(View v) {
        this.runStatus = RunStatus.WAITING;
        this.resetChronometerTime();
        this.chronometer.stop();
    }

    private void start(View v) {
        this.runStatus = RunStatus.WAITING;
        this.setChronometerTime(0, 5);
        this.chronometer.start();
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
        this.runStatus = RunStatus.RUNNING;
        this.setChronometerTime(runPicker.getValue(), 0);
        this.playAudio("run.mp3");
        this.chronometer.start();
    }

    private void walk() {
        this.runStatus = RunStatus.WALKING;
        this.setChronometerTime(walkPicker.getValue(), 0);
        this.playAudio("walk.mp3");
        this.chronometer.start();
    }

    private void playAudio(String fileName) {
        try {
            AssetFileDescriptor afd = getAssets().openFd(fileName);
            player.reset();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupNumPicker(NumberPicker np, int maxValue) {
        np.setMinValue(1);
        np.setMaxValue(maxValue);
        String[] available_run_values = new String[maxValue];
        for (int i = 0; i < maxValue; i++) {
            available_run_values[i] = String.format("%d", i + 1);
        }
        np.setDisplayedValues(available_run_values);
    }

    private void setChronometerTime(int minutes, int seconds) {
        long ms = minutes * 60000L + seconds * 1000L;
        long base = SystemClock.elapsedRealtime() + ms;
        chronometer.setBase(base);

        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(ms, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                countdownDone();
            }
        }.start();
    }

    private void resetChronometerTime() {
        long base = SystemClock.elapsedRealtime();
        chronometer.setBase(base);
        if (timer != null) {
            timer.cancel();
        }
    }
}
