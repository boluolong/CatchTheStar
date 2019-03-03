package com.example.wangtianlong.catchtheball;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPlayer {

    private static SoundPool soundPool;
    private static int hitSound;
    private static int overSound;

    public SoundPlayer(Context context) {


        // SountPool(int maxStreams, int streamType, int srcQuality)
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

        hitSound = soundPool.load(context, R.raw.hit, 1);
        overSound = soundPool.load(context, R.raw.over, 1);

    }

    public void playHitSound() {
        // play(int soundIN, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f);

    }

    public void playOverSound() {
        // play(int soundIN, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(overSound, 1.0f, 1.0f, 1, 0, 1.0f);

    }


}
