package com.game.rk6cooperation.androidgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.game.rk6cooperation.androidgame.Network.Api;
import com.game.rk6cooperation.androidgame.Network.ListenerHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private final Api.OnCheckAuthListener checkAuthListener = new Api.OnCheckAuthListener() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onSessionInvalid() {
            UserHolder.getUserHolder().deleteCookie();

            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putString(Config.KEY_SESSION, null);
            editor.apply();

            setVisibilityUnauthorized();
        }

        @Override
        public void onError(final Exception error) {
        }
    };

    private Api.OnLogoutListener logoutListener = new Api.OnLogoutListener() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onError(final Exception error) {
        }
    };

    private void setVisibilityAuthorized() {
        login.setVisibility(View.INVISIBLE);
        register.setVisibility(View.INVISIBLE);
        logout.setVisibility(View.VISIBLE);
        nickname.setText(UserHolder.getUserHolder().getNickname());
        nickname.setVisibility(View.VISIBLE);
    }

    private void setVisibilityUnauthorized() {
        login.setVisibility(View.VISIBLE);
        register.setVisibility(View.VISIBLE);
        nickname.setVisibility(View.INVISIBLE);
        logout.setVisibility(View.INVISIBLE);
    }

    @BindView(R.id.view_nickname)
    TextView nickname;

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

    @BindView(R.id.btn_logout)
    TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setVisibilityUnauthorized();

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String session = prefs.getString(Config.KEY_SESSION, null);
        String nick = prefs.getString(Config.KEY_NICKNAME, null);


        if(session != null) {
            UserHolder.getUserHolder().setCookie(session);
            UserHolder.getUserHolder().setNickname(nick);
        }

        ListenerHandler<Api.OnCheckAuthListener> checkAuthHandler = Api.getInstance().checkAuth(checkAuthListener);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(UserHolder.getUserHolder().isCookieExist()) {
            setVisibilityAuthorized();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(Config.KEY_SESSION, UserHolder.getUserHolder().getCookie());
        editor.putString(Config.KEY_NICKNAME, UserHolder.getUserHolder().getNickname());
        editor.apply();
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

    @OnClick(R.id.btn_logout)
    void onLogoutClick() {
        ListenerHandler<Api.OnLogoutListener> checkLogoutHandler = Api.getInstance().logout(logoutListener);
        UserHolder.getUserHolder().deleteCookie();
        setVisibilityUnauthorized();
    }

}
