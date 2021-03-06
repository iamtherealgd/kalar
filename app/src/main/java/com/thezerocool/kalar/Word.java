package com.thezerocool.kalar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Gagandeep Rangi on 4/19/2016.
 */
public class Word extends Fragment implements View.OnClickListener {
    //For Scores:
    SharedPreferences scores;
    final static String WORD_HIGH = "Word High Score";

    ImageButton back;
    TextView score, ques, gameMode;
    Button choice1, choice2, choice3, choice4;
    static ProgressBar progressBar;
    Dialog dialog;

    String[] questions = {"blue", "red", "green", "yellow"};
    int[] colors = {android.graphics.Color.BLUE,
            android.graphics.Color.GREEN,
            android.graphics.Color.RED,
            android.graphics.Color.YELLOW};
    static int index;
    static int colorIndex;
    static int scoreValue, highScoreValue;
    static int scoreQuotient;
    static boolean progressFlag = true;
    static Thread t;
    static int x = 1000;
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_color, viewGroup, false);

        MainActivity.last = 10;
        handler = new Handler(Looper.getMainLooper());

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        back = (ImageButton) v.findViewById(R.id.backButton);
        score = (TextView) v.findViewById(R.id.score);
        ques = (TextView) v.findViewById(R.id.ques);
        gameMode = (TextView) v.findViewById(R.id.gameMode);
        choice1 = (Button) v.findViewById(R.id.choice1);
        choice2 = (Button) v.findViewById(R.id.choice2);
        choice3 = (Button) v.findViewById(R.id.choice3);
        choice4 = (Button) v.findViewById(R.id.choice4);

        gameMode.setText("Word");
        progressBar.setMax(1000);

        back.setOnClickListener(this);
        choice1.setOnClickListener(this);
        choice2.setOnClickListener(this);
        choice3.setOnClickListener(this);
        choice4.setOnClickListener(this);

        choice1.setText("blue");
        choice1.setBackgroundColor(android.graphics.Color.BLUE);
        choice2.setText("green");
        choice2.setBackgroundColor(android.graphics.Color.GREEN);
        choice3.setText("red");
        choice3.setBackgroundColor(android.graphics.Color.RED);
        choice4.setText("yellow");
        choice4.setBackgroundColor(android.graphics.Color.YELLOW);

        scores = getActivity().getSharedPreferences("Scores", Context.MODE_PRIVATE);
//        scores = getActivity().getPreferences(Context.MODE_PRIVATE);

        if (savedInstanceState != null) {
            scoreValue = savedInstanceState.getInt("score");
        } else {
            scoreValue = 0;
        }
        score.setText("" + scoreValue);

        Random r = new Random();
        index = r.nextInt(4);
        colorIndex = r.nextInt(4);
        ques.setText(questions[index]);
        ques.setTextColor(colors[colorIndex]);

        progressFlag = true;
        x = 1000;
        updateProgressBar();

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("score", scoreValue);
        progressFlag = false;
    }

    private void updateProgressBar() {

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressFlag) {
                    if (x == 0) {
                        progressFlag = false;
                        gameOver();
                    }

                    progressBar.setProgress(x);
                    updateScore(x);
                    x--;
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        });
        t.start();

    }

    public void nextQuestion() {
        progressFlag = true;
        x = 1000;
        scoreValue += scoreQuotient;
        score.setText("" + scoreValue);

        Random r = new Random();
        index = r.nextInt(4);
        colorIndex = r.nextInt(4);
        ques.setText(questions[index]);
        ques.setTextColor(colors[colorIndex]);

    }

    public void updateScore(int temp) {
        scoreQuotient = temp / 100;
    }

    public void gameOver() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    int temp = scores.getInt(WORD_HIGH, 0);
                    highScoreValue = temp;
                    if (scoreValue >= temp) {
                        SharedPreferences.Editor editor = scores.edit();
                        editor.putInt(WORD_HIGH, Word.scoreValue);
                        highScoreValue = scoreValue;
                        editor.commit();
                    }
                    DialogGameOver dialog = new DialogGameOver();
                    dialog.show(getFragmentManager(), "show");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Fragment f;
        FragmentTransaction ft;

        progressFlag = false;
        switch (v.getId()) {
            case R.id.backButton:
                getFragmentManager().popBackStack();
                f = new Front();
                ft = getFragmentManager().
                        beginTransaction();
                ft.replace(R.id.fragmentContainer, f);

                ft.commit();

                break;
            case R.id.choice1:
                if (questions[index].equals(choice1.getText().toString())) {
                    nextQuestion();
                } else {
                    gameOver();
                }
                break;
            case R.id.choice2:
                if (questions[index].equals(choice2.getText().toString())) {
                    nextQuestion();
                } else {
                    gameOver();
                }
                break;
            case R.id.choice3:
                if (questions[index].equals(choice3.getText().toString())) {
                    nextQuestion();
                } else {
                    gameOver();
                }
                break;
            case R.id.choice4:
                if (questions[index].equals(choice4.getText().toString())) {
                    nextQuestion();
                } else {
                    gameOver();
                }
                break;
        }

    }
}
