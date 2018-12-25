package com.game.rk6cooperation.androidgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.game.rk6cooperation.androidgame.Network.Api;
import com.game.rk6cooperation.androidgame.Network.ListenerHandler;
import com.game.rk6cooperation.androidgame.Network.RegUserResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    private final Api.OnRegisterListener regListener = new Api.OnRegisterListener() {
        @Override
        public void onSuccess(final RegUserResponse user) {
            UserHolder.getUserHolder().setNickname(user.getUser().getNickname());
            finish();
        }

        @Override
        public void onError(final Exception error) {
            Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRegisterInvalid() {
            String message = "Login already used";
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    @BindView(R.id.login)
    EditText loginField;

    @BindView(R.id.password)
    EditText passwordField;

    @BindView(R.id.repeat_password)
    EditText passwordRepeatField;

    @BindView(R.id.btn_submit)
    Button submitBtn;

    ListenerHandler<Api.OnRegisterListener> regHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

    }

    @Override
    protected void onDestroy() {
        if (regHandler != null) {
            regHandler.unregister();
        }
        super.onDestroy();
    }

    @OnClick(R.id.btn_submit)
    void onClickSubmit() {
        String login = loginField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordRepeat = passwordRepeatField.getText().toString();

        Boolean isFieldsEmpty;
        isFieldsEmpty = (TextUtils.isEmpty(login) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordRepeat));

        if(isFieldsEmpty) {
            Toast.makeText(RegisterActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(passwordRepeat)) {
            Toast.makeText(RegisterActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        regHandler = Api.getInstance().register(login, password, regListener);
    }
}
