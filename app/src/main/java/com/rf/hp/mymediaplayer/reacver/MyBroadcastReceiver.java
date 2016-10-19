package com.rf.hp.mymediaplayer.reacver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hp on 2016/10/19.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private int level;

    public MyBroadcastReceiver(int level) {
        this.level = level;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        level = intent.getIntExtra("level", 0);
    }
}
