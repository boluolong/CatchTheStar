package com.example.wangtianlong.catchtheball;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class main extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView box;
    private ImageView orange;
    private ImageView purple;
    private ImageView white;

    // Size
    private int frameHeight;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;


    // Position
    private int boxY;
    private int orangeX;
    private int orangeY;
    private int purpleX;
    private int purpleY;
    private int whiteX;
    private int whiteY;

    //Speed
    private int boxSpeed;
    private int orangeSpeed;
    private int purpleSpeed;
    private int whiteSpeed;


    //Score
    private int score = 0;

    // Initialisation Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    // Status Check
    private boolean action_flg = false;
    private boolean start_flg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = new SoundPlayer(this);

        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);
        box = (ImageView) findViewById(R.id.blueStar);
        orange = (ImageView) findViewById(R.id.orangeStar);
        purple = (ImageView) findViewById(R.id.purpleStar);
        white = (ImageView) findViewById(R.id.whiteStar);

        //Get screen size
        WindowManager wn = getWindowManager();
        Display disp = wn.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        // Now
        // Nexus5 width : 1080  height : 1776
        // Speed box : 20  orange : 12   purple 20   white : 16
        boxSpeed = Math.round(screenHeight / 60F); //1776/60 = 29.6 = > 30
        orangeSpeed = Math.round(screenWidth / 60F); //1080/60 = 18
        purpleSpeed = Math.round(screenWidth / 36F); //1080/36 = 30
        whiteSpeed = Math.round(screenWidth / 45F); //1080/45 = 24


        Log.v("SPEED_BOX",boxSpeed+"");
        Log.v("SPEED_ORANGE",orangeSpeed+"");
        Log.v("SPEED_PURPLE",purpleSpeed+"");
        Log.v("SPEED_WHITE",whiteSpeed+"");


        //Move to our of the screen
        orange.setX(-80);
        orange.setY(-80);
        purple.setX(-80);
        purple.setY(-80);
        white.setX(-80);
        white.setY(-80);

        scoreLabel.setText("Score : " + score);

    }

    public void changePos() {

        hitCheck();

        //Orange
        orangeX -= orangeSpeed;
        if (orangeX < 0) {
            orangeX = screenWidth + 20;
            orangeY = (int) Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setY(orangeY);
        orange.setX(orangeX);

        //White
        whiteX -= whiteSpeed;
        if (whiteX < 0) {
            whiteX = screenWidth + 20;
            whiteY = (int) Math.floor(Math.random() * (frameHeight - white.getHeight()));
        }
        white.setY(whiteY);
        white.setX(whiteX);

        //purple
        purpleX -= purpleSpeed;
        if (purpleX < 0) {
            purpleX = screenWidth + 20;
            purpleY = (int) Math.floor(Math.random() * (frameHeight - purple.getHeight()));
        }
        purple.setY(purpleY);
        purple.setX(purpleX);

        //Move Box
        if (action_flg == true) {
            // Touching
            boxY -= boxSpeed;
        } else {
            // Releasing
            boxY += boxSpeed;
        }

        //Check box position
        if (boxY < 0) {
            boxY = 0;
        }

        if (boxY > frameHeight - boxSize) {
            boxY = frameHeight - boxSize;
        }

        box.setY(boxY);
        scoreLabel.setText("Score  : " + score);
    }

    public void hitCheck() {

        // If the center of the ball is in the box, it counts as a hit

        //orange
        int orangeCenterX = orangeX + orange.getWidth() / 2;
        int orangeCenterY = orangeY + orange.getHeight() / 2;

        // 0 <= orangeCenterX <= boxWidth
        // boxY <= orangeCenterY <= boxY + boxHeight
        if (0 <= orangeCenterX && orangeCenterX <= boxSize &&
                boxY <= orangeCenterY && orangeCenterY <= boxY + boxSize) {
            score += 10;
            orangeX = -10;
            sound.playHitSound();
        }

        //purple
        int purpleCenterX = purpleX + purple.getWidth() / 2;
        int purpleCenterY = purpleY + purple.getHeight() / 2;

        // 0 <= purpleCenterX <= boxWidth
        // boxY <= purpleCenterY <= boxY + boxHeight
        if (0 <= purpleCenterX && purpleCenterX <= boxSize &&
                boxY <= purpleCenterY && purpleCenterY <= boxY + boxSize) {
            score += 30;
            purpleX = -10;
            sound.playHitSound();
        }

        //white
        int whiteCenterX = whiteX + white.getWidth() / 2;
        int whiteCenterY = whiteY + white.getHeight() / 2;

        // 0 <= whiteCenterX <= boxWidth
        // boxY <= whiteCenterY <= boxY + boxHeight
        if (0 <= whiteCenterX && whiteCenterX <= boxSize &&
                boxY <= whiteCenterY && whiteCenterY <= boxY + boxSize) {
            //Stop timer
            timer.cancel();
            timer = null;

            sound.playOverSound();

            // shower result
            Intent intent = new Intent(getApplicationContext(), result.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }

    }

    public boolean onTouchEvent(MotionEvent me) {
        if (start_flg == false) {

            start_flg = true;

            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            boxY = (int) box.getY();

            boxSize = box.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);

        } else {
            if (me.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;
            } else if (me.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }


        box.setY(boxY);

        return true;
    }

    //DisableReturn
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }
}
