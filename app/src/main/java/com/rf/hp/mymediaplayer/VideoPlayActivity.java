package com.rf.hp.mymediaplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView vvPlay;
    private String path;
    private Uri data;
    private LinearLayout ll_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);
        getSupportActionBar().hide();
        vvPlay = (VideoView) findViewById(R.id.vv_play);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        data = getIntent().getData();
        if(path==null){
            startSDplay();
        }else{
            startPlay();
        }
        initView();
        playSetting();

    }

    private void initView() {
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
    }

    private void playSetting() {
        vvPlay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ll_loading.setVisibility(View.GONE);
            }
        });
    }

    private void startSDplay() {
        vvPlay.setVideoURI(data);
        vvPlay.setMediaController(new MediaController(this));
        vvPlay.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH,0);
        vvPlay.requestFocus();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void startPlay() {
        vvPlay.setVideoPath(path);
        vvPlay.setVideoURI(Uri.parse(path));
        vvPlay.setMediaController(new MediaController(this));
        vvPlay.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH,0);
        vvPlay.requestFocus();
    }
}
