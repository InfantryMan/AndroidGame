package com.game.rk6cooperation.androidgame;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import butterknife.OnClick;

public class ScoreboardActivity extends AppCompatActivity {

    @BindView(R.id.progress)
    ProgressBar progress;

    public static final String EMPTY_LIST = "Leaderboard is empty";
    @BindView(R.id.table)
    TableLayout tableLayout;
    @BindView(R.id.controls)
    LinearLayout controls;
    @BindView(R.id.title_scoreboard)
    TextView title;
    @BindView(R.id.button_next)
    Button button_next;
    @BindView(R.id.button_prev)
    Button button_prev;

    private ListenerHandler<Api.OnUsersListGetListener> userHandler;
    @BindView(R.id.notification)
    TextView notification;

    Integer pages_count = 0;
    Integer current_page = 1;
    Integer on_page = 10;

    private Api.OnUsersListGetListener usersListListener = new Api.OnUsersListGetListener() {
        @Override
        public void onSuccess(final ScoreboardUsers users) {
            setUsers(users);
            stopProgress();
        }

        @Override
        public void onError(final Exception error) {
            resetUsers();
            stopProgress();
            Toast.makeText(ScoreboardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("MYTAG", "ERROR" + error.getMessage());
        }
    };

    private TextView createScoreboardTextView(String text) {
        TextView textView = new TextView(new ContextThemeWrapper(this, R.style.ScoreboardFont));
        textView.setText(text);
        return textView;
    }

    private void setUsers(final ScoreboardUsers users) {
        List<ScoreboardUser> usersList = users.getUsers();
        pages_count = users.getPages();

        if (usersList.isEmpty()) {
            notification.setText(EMPTY_LIST);
            notification.setVisibility(View.VISIBLE);
            return;
        }

        tableLayout.setStretchAllColumns(true);
        tableLayout.setShrinkAllColumns(true);

        TableRow rowTitle = new TableRow(this);
        rowTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView title_place = createScoreboardTextView(getString(R.string.scoreboard_place));
        TextView title_nickname = createScoreboardTextView(getString(R.string.scoreboard_nickname));
        TextView title_score = createScoreboardTextView(getString(R.string.scoreboard_score));

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = 1;
        rowTitle.addView(title_place, params);
        TableRow.LayoutParams params4 = new TableRow.LayoutParams();
        params4.span = 4;
        rowTitle.addView(title_nickname, params4);
        TableRow.LayoutParams params3 = new TableRow.LayoutParams();
        params3.span = 2;
        rowTitle.addView(title_score, params3);
        tableLayout.addView(rowTitle);

        for(ScoreboardUser su: usersList) {
            TableRow rowUser = new TableRow(this);
            rowUser.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView user_place = createScoreboardTextView(su.getPlace().toString());
            TextView user_nickname = createScoreboardTextView(su.getNickname());
            TextView user_score = createScoreboardTextView(su.getScore().toString());

            rowUser.addView(user_place, params);
            rowUser.addView(user_nickname, params4);
            rowUser.addView(user_score, params3);
            tableLayout.addView(rowUser);
        }

        if (current_page >= pages_count) {
            button_next.setEnabled(false);
        } else {
            button_next.setEnabled(true);
        }
        if (current_page <= 1) {
            button_prev.setEnabled(false);
        } else {
            button_prev.setEnabled(true);
        }

        stopProgress();
    }

    private void resetUsers() {
        this.toggleContent(false);
        tableLayout.removeAllViews();
    }


    private void toggleContent(boolean show) {
        int to_show = show ? View.VISIBLE : View.INVISIBLE;
        controls.setVisibility(to_show);
        tableLayout.setVisibility(to_show);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        ButterKnife.bind(this);

        startProgress();
        userHandler = Api.getInstance().getUsersList(this.current_page, this.on_page, usersListListener);
    }

    private void startProgress() {
        this.toggleContent(false);
        progress.setVisibility(View.VISIBLE);
    }

    private void stopProgress() {
        progress.setVisibility(View.INVISIBLE);
        this.toggleContent(true);
    }

    @OnClick(R.id.button_next)
    public void next_page() {
        this.resetUsers();
        this.startProgress();
        if (current_page + 1 > pages_count) {
            return;
        }
        userHandler = Api.getInstance().getUsersList(++this.current_page, this.on_page, usersListListener);
    }

    @OnClick(R.id.button_prev)
    public void prev_page() {
        this.resetUsers();
        this.startProgress();
        if (current_page - 1 < 1) {
            return;
        }
        userHandler = Api.getInstance().getUsersList(--this.current_page, this.on_page, usersListListener);
    }
}
