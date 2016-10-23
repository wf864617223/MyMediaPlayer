package com.rf.hp.mymediaplayer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.rf.hp.mymediaplayer.Asunc.ScanAsyncTask;
import com.rf.hp.mymediaplayer.bean.VideoItem;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SplashActivity extends AppCompatActivity {

    private ArrayList<VideoItem> videoItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //getSupportActionBar().hide();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent Intent = new Intent(SplashActivity.this,ShowVideoActivity.class);
                finish();
                startActivity(Intent);
            }
        }, 2000);*/
        //getAllVideo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //getAllVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllVideo();
    }

    private void getAllVideo() {
        ScanAsyncTask ansyTask = new ScanAsyncTask();
        AsyncTask<Void, Integer, ArrayList<VideoItem>> execute = ansyTask.execute();
        try {
            videoItems = execute.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(SplashActivity.this,ShowVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("videolist", videoItems);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


}
