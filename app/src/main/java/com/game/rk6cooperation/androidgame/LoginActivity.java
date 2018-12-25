package com.game.rk6cooperation.androidgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.game.rk6cooperation.androidgame.Network.Api;
import com.game.rk6cooperation.androidgame.Network.AuthUserResponse;
import com.game.rk6cooperation.androidgame.Network.ListenerHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private final Api.OnAuthorizeListener authListener = new Api.OnAuthorizeListener() {
        @Override
        public void onSuccess(final AuthUserResponse user) {
            UserHolder.getUserHolder().setNickname(user.getUser().getNickname());
            finish();
        }

        @Override
        public void onError(final Exception error) {
            Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSessionInvalid () {
            String message = "Invalid login or password";
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };


    @BindView(R.id.login)
    EditText loginField;

    @BindView(R.id.password)
    EditText passwordField;

    @BindView(R.id.btn_submit)
    Button submitBtn;

    ListenerHandler<Api.OnAuthorizeListener> authHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

    }

    @Override
    protected void onDestroy() {
        if (authHandler != null) {
            authHandler.unregister();
        }
        super.onDestroy();
    }

    @OnClick(R.id.btn_submit)
    void onClickSubmit() {
        String login = loginField.getText().toString();
        String password = passwordField.getText().toString();

        Boolean isFieldsEmpty;
        isFieldsEmpty = (TextUtils.isEmpty(login) || TextUtils.isEmpty(password));

        if(isFieldsEmpty) {
            Toast.makeText(LoginActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
            return;
        }

        authHandler = Api.getInstance().authorize(login, password, authListener);
    }

}
