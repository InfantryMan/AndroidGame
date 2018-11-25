package com.game.rk6cooperation.androidgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.game.rk6cooperation.androidgame.Network.Api;
import com.game.rk6cooperation.androidgame.Network.ListenerHandler;
import com.game.rk6cooperation.androidgame.Network.ScoreboardUser;
import com.game.rk6cooperation.androidgame.Network.ScoreboardUsers;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScoreboardActivity extends AppCompatActivity {

    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.container)
    LinearLayout userContainer;

    private ListenerHandler<Api.OnUsersListGetListener> userHandler;

    private Api.OnUsersListGetListener usersListListener = new Api.OnUsersListGetListener() {
        @Override
        public void onSuccess(final ScoreboardUsers users) {
            setUsers(users);
            stopProgress();
            Log.d("MYTAG", "SUCCESS" + users.getUsers().get(0).getNickname());
        }

        @Override
        public void onError(final Exception error) {
            resetUsers();
            stopProgress();
            Toast.makeText(ScoreboardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("MYTAG", "ERROR" + error.getMessage());
        }
    };

    private void setUsers(final ScoreboardUsers users) {
        List<ScoreboardUser> usersList = users.getUsers();
        for (ScoreboardUser user : usersList
                ) {
            Log.d("MYTAG", user.getNickname());
        }
        stopProgress();

//        if (!TextUtils.isEmpty(user.getNick())) {
//            userName.setText(String.format("%s %s %s",
//                    user.getFirstName(), user.getNick(), user.getLastName()));
//        } else {
//            userName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
//        }
//        gender.setText(String.valueOf(user.getGender()));
//
//        avatar.setImageDrawable(null);
//        if (!TextUtils.isEmpty(user.getAvatar())) {
//            Picasso.get().load(user.getAvatar()).into(avatar);
//        }
    }

    private void resetUsers() {


//        userName.setText("");
//        gender.setText("");
//        avatar.setImageDrawable(null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        ButterKnife.bind(this);

        startProgress();
        userHandler = Api.getInstance().getUsersList(0, usersListListener);


    }

    private void startProgress() {
        progress.setVisibility(View.VISIBLE);
        userContainer.setVisibility(View.INVISIBLE);
    }

    private void stopProgress() {
        progress.setVisibility(View.INVISIBLE);
        userContainer.setVisibility(View.VISIBLE);
    }


}
