package com.game.rk6cooperation.androidgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_start_game)
    TextView startGame;

    @BindView(R.id.btn_scoreboard)
    TextView scoreBoard;

    @BindView(R.id.btn_settings)
    TextView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_start_game)
    void onLaunchGameClick() {
        final Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_scoreboard)
    void onLaunchScoreboardClick() {
        final Intent intent = new Intent(this, ScoreboardActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_settings)
    void onLaunchSettingsClick() {
        final Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
