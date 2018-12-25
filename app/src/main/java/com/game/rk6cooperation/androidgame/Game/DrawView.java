package com.game.rk6cooperation.androidgame.Game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import com.game.rk6cooperation.androidgame.R;

import java.util.List;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

    private DrawThread drawThread;
    private final List<RunningNumber> runningNumberList;
    private final int rowsNumber;

    public DrawView(Context context, List<RunningNumber> runningNumberList, int rowsNumber) {
        super(context);

        this.runningNumberList = runningNumberList;
        this.rowsNumber = rowsNumber;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread = new DrawThread(getHolder(), runningNumberList, rowsNumber);
        drawThread.setRunning(true);
        drawThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    class DrawThread extends Thread {

        private boolean running = false;
        private final SurfaceHolder surfaceHolder;
        private final Paint p = new Paint();
        private final List<RunningNumber> runningNumberList;
        private final int rowsNumber;
        private final int spSize = getResources().getDimensionPixelSize(R.dimen.number_game_text_size);
        private final int textHalf;

        DrawThread(SurfaceHolder surfaceHolder, List<RunningNumber> runningNumberList, int rowsNumber) {
            this.surfaceHolder = surfaceHolder;
            this.runningNumberList = runningNumberList;
            this.rowsNumber = rowsNumber;

            p.setColor(Color.RED);
            p.setTextSize(spSize);
            textHalf = (int) (p.descent() + p.ascent()) / 2;

        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            Log.d("MYTAG", "Privet");
            while (running) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas == null)
                        continue;
                    Log.d("MYTAG", "Hello");
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    double rowHeight = canvas.getHeight() / this.rowsNumber;
                    for (RunningNumber rn: this.runningNumberList) {
                        int yPos = (int) (rowHeight * rn.getIndexOfRow() + (canvas.getHeight() / this.rowsNumber / 2) - this.textHalf);
                        canvas.drawText(rn.getValueInString(), (int) rn.getxCoord(), yPos, p);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

}
