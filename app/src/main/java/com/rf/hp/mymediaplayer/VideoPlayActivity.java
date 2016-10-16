package com.rf.hp.mymediaplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView vvPlay;
    private String path;
    private Uri data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        playSetting();

    }

    private void playSetting() {

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
