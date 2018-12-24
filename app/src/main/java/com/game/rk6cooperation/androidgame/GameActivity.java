package com.game.rk6cooperation.androidgame;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.game.rk6cooperation.androidgame.Network.WorkerThread;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameActivity extends AppCompatActivity {

    static int moveValue = 150;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<String> availableNumbers = Arrays.asList(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "#"
    );
    private SoundPool mSoundPool;
    private AssetManager mAssetManager;
    private int mOneButSound, mTwoButSound, mThreeButSound, mFourButSound;
    private int mFiveButSound, mSixButSound, mSevenButSound,  mEightButSound;
    private int mNineButSound, mZeroButSound, m11ButSound, m12ButSound;
    private int mStreamID;

    @BindView(R.id.game_ground_container)
    LinearLayout gameGroundContainer;
    @BindView(R.id.button0)
    Button button0;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.button3)
    Button button3;
    @BindView(R.id.button4)
    Button button4;
    @BindView(R.id.button5)
    Button button5;
    @BindView(R.id.button6)
    Button button6;
    @BindView(R.id.button7)
    Button button7;
    @BindView(R.id.button8)
    Button button8;
    @BindView(R.id.button9)
    Button button9;
    @BindView(R.id.button_hashtag)
    Button buttonHashtag;
    @BindView(R.id.button_star)
    Button buttonStar;
    private Queue<TextView> numbers = new LinkedList<>();
    private WorkerThread workerThreadGenerateNumbers;
    private WorkerThread workerThreadMoveNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);

        workerThreadGenerateNumbers = new WorkerThread("WorkerGenerateNumbers");
        workerThreadMoveNumbers = new WorkerThread("WorkerMoveNumbers");

        Runnable generateNumbersTask = new Runnable() {
            @Override
            public void run() {
                generateNumbers();
            }
        };

        Runnable moveNumbersTask = new Runnable() {
            @Override
            public void run() {
                moveNumbers();
            }
        };

        workerThreadGenerateNumbers.start();
        workerThreadGenerateNumbers.prepareHandler();
        workerThreadGenerateNumbers.postTask(generateNumbersTask);

        workerThreadMoveNumbers.start();
        workerThreadMoveNumbers.prepareHandler();
        workerThreadMoveNumbers.postTask(moveNumbersTask);

//        TextView textView = new TextView(this);
//        textView.setText("Test");
//        gameGroundContainer.addView(textView);
//
//        numbers.offer(textView);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final View textViewElement = numbers.poll();
//                if (numbers.isEmpty()) {
//                    Log.d("MYTAG", "Queue is empty");
//                } else {
//                    Log.d("MYTAG", "Queue is not empty");
//                }
//                mainHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        ObjectAnimator animation = ObjectAnimator.ofFloat(textViewElement, "translationX", 150);
//                        animation.setDuration(5000);
//                        animation.start();
//                    }
//                });
//                numbers.add(textViewElement);
//            }
//        }).start();

    }

    @Override
    protected void onDestroy() {
        workerThreadGenerateNumbers.quit();
        workerThreadMoveNumbers.quit();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        workerThreadGenerateNumbers.quit();
        workerThreadMoveNumbers.quit();
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

        mZeroButSound = loadSound(Config.SOUND_ZERO);
        mOneButSound = loadSound(Config.SOUND_ONE);
        mTwoButSound = loadSound(Config.SOUND_TWO);
        mThreeButSound = loadSound(Config.SOUND_THREE);
        mFourButSound = loadSound(Config.SOUND_FOUR);
        mFiveButSound = loadSound(Config.SOUND_FIVE);
        mSixButSound = loadSound(Config.SOUND_SIX);
        mSevenButSound = loadSound(Config.SOUND_SEVEN);
        mEightButSound = loadSound(Config.SOUND_EIGHT);
        mNineButSound = loadSound(Config.SOUND_NINE);
        m11ButSound = loadSound(Config.SOUND_11);
        m12ButSound = loadSound(Config.SOUND_12);

    }


    @Override
    protected void onPause() {
        super.onPause();
        mSoundPool.release();
        mSoundPool = null;
    }


    private void generateNumbers() {
        Integer availableNumbersSize = availableNumbers.size();
        while (true) {
            int random = (int) (Math.random() * availableNumbersSize);
            String numberInString = availableNumbers.get(random);
            final TextView textView = new TextView(this);
            textView.setText(numberInString);
            numbers.add(textView);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    gameGroundContainer.addView(textView);
                }
            });
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void moveNumbers() {
        while (true) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            final TextView textViewElement = numbers.poll();
            if (numbers.isEmpty()) {
                Log.d("MYTAG", "Queue is empty.");
            } else {
                Log.d("MYTAG", "Queue is not empty. Value of polled element: " + textViewElement.getText());
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(textViewElement, "translationX", moveValue);
                    animation.setDuration(4000);
                    animation.start();
                }
            });
            numbers.add(textViewElement);
            moveValue += 10;
        }
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


    @OnClick(R.id.button0)
    void onClickZero() {
        playSound(mZeroButSound);
    }

    @OnClick(R.id.button1)
    void onClickOne() {
        playSound(mOneButSound);
    }

    @OnClick(R.id.button2)
    void onClickTwo() {
        playSound(mTwoButSound);
    }

    @OnClick(R.id.button3)
    void onClickThree() {
        playSound(mThreeButSound);
    }

    @OnClick(R.id.button4)
    void onClickFour() {
        playSound(mFourButSound);
    }

    @OnClick(R.id.button5)
    void onClickFive() {
        playSound(mFiveButSound);
    }

    @OnClick(R.id.button6)
    void onClickSix() {
        playSound(mSixButSound);
    }

    @OnClick(R.id.button7)
    void onClickSeven() {
        playSound(mSevenButSound);
    }

    @OnClick(R.id.button8)
    void onClickEight() {
        playSound(mEightButSound);
    }

    @OnClick(R.id.button9)
    void onClickNine() {
        playSound(mNineButSound);
    }

    @OnClick(R.id.button_hashtag)
    void onClick11() {
        playSound(m11ButSound);
    }

    @OnClick(R.id.button_star)
    void onClick12() {
        playSound(m12ButSound);
    }



}


