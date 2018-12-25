package com.game.rk6cooperation.androidgame;

import android.annotation.TargetApi;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.game.rk6cooperation.androidgame.Game.RunningNumber;
import com.game.rk6cooperation.androidgame.Game.DrawView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity {

    private static final List<String> availableNumbers = Arrays.asList(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#"
    );
    private SoundPool mSoundPool;
    private AssetManager mAssetManager;
    private List<Integer> sounds = null;
    private int mStreamID;


    @BindView(R.id.game_ground_container)
    LinearLayout gameGround;
    
    @BindView(R.id.table_layout)
    TableLayout keyboardLayout;

    // Количество строк с числами в игре
    private final List<RunningNumber> runningNumberList = new ArrayList<>();
    private List<Button> buttons = new ArrayList<>();

    private void initList() {
        for (int i = 0; i < Constants.ROWS_NUMBER; i++) {
            int indexOfMas = (int) (Math.random() * availableNumbers.size());
            double velocity = Constants.VELOCITY;
            RunningNumber runningNumber = new RunningNumber(availableNumbers.get(indexOfMas), i, velocity);
            runningNumberList.add(runningNumber);
        }
        DrawView drawView = new DrawView(this, runningNumberList, Constants.ROWS_NUMBER);
        gameGround.addView(drawView);

    }

    private void generateKeyboard() {
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
        layoutParams.width = TableRow.LayoutParams.MATCH_PARENT;
        layoutParams.height = TableRow.LayoutParams.MATCH_PARENT;
        ContextThemeWrapper newContext = new ContextThemeWrapper(this, R.style.GameButton);

        for (int i = 0; i < Constants.KEYBOARD_ROW_NUMBER; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(layoutParams);
            for (int j = 0; j < Constants.KEYBOARD_COL_NUMBER; j++) {
                Button button = new Button(newContext);
                int buttonIndex = i * Constants.KEYBOARD_COL_NUMBER + j;
                button.setText(availableNumbers.get(buttonIndex + 1));
                button.setTag(buttonIndex + 1);
                tableRow.addView(button);
                buttons.add(button);
            }
            keyboardLayout.addView(tableRow);
        }
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(layoutParams);
        Button buttonStar = new Button(newContext);
        buttonStar.setText(availableNumbers.get(10));
        buttonStar.setTag(10);
        buttons.add(buttonStar);
        tableRow.addView(buttonStar);

        Button button0 = new Button(newContext);
        button0.setText(availableNumbers.get(0));
        button0.setTag(0);
        buttons.add(0, button0);
        tableRow.addView(button0);

        Button buttonHash = new Button(newContext);
        buttonHash.setText(availableNumbers.get(11));
        buttonHash.setTag(11);
        buttons.add(buttonHash);
        tableRow.addView(buttonHash);
        keyboardLayout.addView(tableRow);

        bindKeyboard();
    }

    private void bindKeyboard() {
        for (Button bn: buttons) {
            bn.setOnClickListener(onGameButtonClick);
        }
    }

    View.OnClickListener onGameButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            Integer id = (int) button.getTag();
            if (sounds != null) {
                playSound(sounds.get(id));
            }
            pushMessage(0, id);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        initList();
        isFinished = false;
        generateKeyboard();

        gameGround.post(new Runnable() {
            @Override
            public void run() {
                gameThread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        isFinished = true;
        handler.removeCallbacks(gameMechanicRunnable);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            createOldSoundPool();
        } else {
            createNewSoundPool();
        }

        mAssetManager = getAssets();

        sounds = Arrays.asList(
            loadSound(Constants.SOUND_ZERO),
            loadSound(Constants.SOUND_ONE),
            loadSound(Constants.SOUND_TWO),
            loadSound(Constants.SOUND_THREE),
            loadSound(Constants.SOUND_FOUR),
            loadSound(Constants.SOUND_FIVE),
            loadSound(Constants.SOUND_SIX),
            loadSound(Constants.SOUND_SEVEN),
            loadSound(Constants.SOUND_EIGHT),
            loadSound(Constants.SOUND_NINE),
            loadSound(Constants.SOUND_11),
            loadSound(Constants.SOUND_12)
        );
    }


    @Override
    protected void onPause() {
        super.onPause();
        mSoundPool.release();
        mSoundPool = null;
        this.sounds = null;
    }

    private Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    boolean isFinished = false;

    void pushMessage(@SuppressWarnings("SameParameterValue") int what, int arg1) {
        Message msg = new Message();
        msg.what = what;
        msg.arg1 = arg1;
        messageQueue.add(msg);
    }

    Runnable gameMechanicRunnable = new Runnable() {

        private void handleClickNumber(Message msg) {
            String valueInString = availableNumbers.get(msg.arg1);
            for (RunningNumber rn : runningNumberList) {
                if (rn.getValueInString().equals(valueInString)) {
                    int indexOfMas = (int) (Math.random() * availableNumbers.size());
                    rn.setValueInString(availableNumbers.get(indexOfMas));
                    rn.setxCoord(0.0);
                    rn.setVelocity(rn.getVelocity() + Constants.NITRO);
                    return;
                }
            }
        }

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            int gameWidth = gameGround.getWidth();

            long currentTime = System.currentTimeMillis();
            while (!isFinished) {
                while (!messageQueue.isEmpty()) {
                    Message msg = messageQueue.poll();
                    if (msg != null) {
                        switch (msg.what) {
                            case 0:
                                this.handleClickNumber(msg);
                                break;
                            default:
                                break;
                        }
                    }
                }

                long newCurrentTime = System.currentTimeMillis();
                for (RunningNumber rn: runningNumberList) {
                    if (rn.getxCoord() >= gameWidth) {
                        handler.sendEmptyMessage(0);
                    }
                    long dt = newCurrentTime - currentTime;
                    rn.move(rn.getVelocity() * dt);
                }
                currentTime = newCurrentTime;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    Thread gameThread = new Thread(gameMechanicRunnable);

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Log.d("MYTAG", "KUTAG");
                handleLose();
            }
        }
    };

    private void handleLose() {
        this.isFinished = true;
        Log.d("MYTAG2", "handleLose");

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    private void createOldSoundPool() {
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
    }

    private int playSound(int sound) {
        if (sound > 0) {
            mStreamID = mSoundPool.play(sound, 1, 1, 1, 0, 1);
        }
        return mStreamID;
    }

    private int loadSound(String fileName) {
        AssetFileDescriptor afd;
        try {
            afd = mAssetManager.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Can't load: " + fileName,
                    Toast.LENGTH_SHORT).show();
            return -1;
        }
        return mSoundPool.load(afd, 1);
    }

}


