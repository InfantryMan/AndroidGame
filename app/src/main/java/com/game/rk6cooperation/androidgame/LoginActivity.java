package com.game.rk6cooperation.androidgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.game.rk6cooperation.androidgame.Network.Api;
import com.game.rk6cooperation.androidgame.Network.AuthUserResponse;
import com.game.rk6cooperation.androidgame.Network.ListenerHandler;
import com.game.rk6cooperation.androidgame.R;

public class LoginActivity extends AppCompatActivity {

    private ListenerHandler<Api.OnAuthorizeListener> authHandler;

    private Api.OnAuthorizeListener authListener = new Api.OnAuthorizeListener() {
        @Override
        public void onSuccess(final AuthUserResponse user) {
//            setUsers(users);
//            stopProgress();
            Log.d("MYTAG", "SUCCESS" + user.getStatus() + " " + user.getUser().getNickname());
        }

        @Override
        public void onError(final Exception error) {
//            resetUsers();
//            stopProgress();
            Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("MYTAG", "ERROR" + error.getMessage());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authHandler = Api.getInstance().authorize("Timur", "1234", authListener);

    }

}
