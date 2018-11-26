package com.game.rk6cooperation.androidgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    @BindView(R.id.btn_login_menu)
    TextView login;

    @BindView(R.id.btn_register_menu)
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    @OnClick(R.id.btn_login_menu)
    void onLaunchLoginClick() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_register_menu)
    void onLaunchRegisterClick() {
        final Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

}
