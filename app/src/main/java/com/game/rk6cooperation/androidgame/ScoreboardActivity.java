package com.game.rk6cooperation.androidgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

    public static final String EMPTY_LIST = "Leaderboard is empty";
    @BindView(R.id.table)
    TableLayout tableLayout;
    @BindView(R.id.title_scoreboard)
    TextView title;

    private ListenerHandler<Api.OnUsersListGetListener> userHandler;
    @BindView(R.id.notification)
    TextView notification;

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

        if (usersList.isEmpty()) {
            notification.setText(EMPTY_LIST);
            notification.setVisibility(View.VISIBLE);
            return;
        }

        tableLayout.setStretchAllColumns(true);
        tableLayout.setShrinkAllColumns(true);

        TableRow tableRow = new TableRow(this);

        stopProgress();
    }

    private void resetUsers() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        ButterKnife.bind(this);

        startProgress();
        userHandler = Api.getInstance().getUsersList(1, 10, usersListListener);


    }

    private void startProgress() {
        progress.setVisibility(View.VISIBLE);
//        userContainer.setVisibility(View.INVISIBLE);
    }

    private void stopProgress() {
        progress.setVisibility(View.INVISIBLE);
//        userContainer.setVisibility(View.VISIBLE);
    }


}
