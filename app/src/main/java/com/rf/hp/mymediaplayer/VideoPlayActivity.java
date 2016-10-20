package com.rf.hp.mymediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rf.hp.mymediaplayer.reacver.MyBroadcastReceiver;
import com.rf.hp.mymediaplayer.utils.Utils;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView vvPlay;
    private String path;
    private Uri data;
    private LinearLayout ll_loading;
    private Button btnPlay;
    private Button btnPre;
    private Button btnNext;
    private TextView tvNowTime;
    private TextView tvAllTime;
    private SeekBar sbVoice;
    private SeekBar sbVideo;
    private TextView tvvideoTitle;
    private TextView tvSysTime;
    private ImageView ivBattery;
    private LinearLayout llCtrlPlayer;

    private GestureDetector detector;

    private boolean isDestroyed =  false;
    //是否展示控制界面
    private boolean isShowControl = false;
    //屏幕的高
    private int screenHeight;
    //屏幕的宽
    private int screenWidth;
    private WindowManager wm;
    //当前音量
    private int currentVolume;
    //最大音量
    private int maxVolume;
    //是否静音
    private boolean isMute = false;
    //
    private static final int PROGRESS = 1;
    //
    private static final int HIDEPROGRESSCTRL = 2;
    private BroadcastReceiver receiver;
    private int level;
    private Utils utils;
    Context context;
    MediaPlayer mp;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PROGRESS:
                    long currentPosition = vvPlay.getCurrentPosition();
                    int duration = (int) vvPlay.getDuration();
                    //tvAllTime.setText(utils.stringForTime(duration));
                    Log.i("VideoPlay","==currentPosition==>"+currentPosition);
                    System.out.println("==currentPosition==>"+currentPosition);
                    tvNowTime.setText(utils.stringForTime(duration));
                    sbVideo.setProgress(duration);
                    tvSysTime.setText(utils.getSystemTime());
                    if(!isDestroyed){
                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS,1000);
                    }
                    break;
                case HIDEPROGRESSCTRL:
                    hideCtrl();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);
        context = VideoPlayActivity.this;
        getSupportActionBar().hide();
        mp = new MediaPlayer(context);
        initView();
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        data = getIntent().getData();
        if (path == null) {
            startSDplay();
        } else {
            startPlay();
        }
        playSetting();
        initData();
    }

    private void initData() {
        isDestroyed = false;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        //获取屏幕宽
        screenWidth = wm.getDefaultDisplay().getWidth();
        //获取屏幕高
        screenHeight = wm.getDefaultDisplay().getHeight();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        receiver = new MyBroadcastReceiver(level);
        registerReceiver(receiver,intentFilter);
        detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                //Toast.makeText(VideoPlayActivity.this, "onDoubleTop", Toast.LENGTH_SHORT).show();
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                Toast.makeText(VideoPlayActivity.this, "onLongPress", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //Toast.makeText(VideoPlayActivity.this, "onSingleTapUp", Toast.LENGTH_SHORT).show();
                return super.onSingleTapUp(e);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //Toast.makeText(VideoPlayActivity.this, "onSingleTapConfirmed", Toast.LENGTH_SHORT).show();
                if(isShowControl){
                    removeDelayedHideCtrlPlayer();
                    hideCtrl();
                }else{
                    sendDelayedHideCtrlPlayer();
                    showCtrl();
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //Toast.makeText(VideoPlayActivity.this, "onScroll", Toast.LENGTH_SHORT).show();
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    private void initView() {
        vvPlay = (VideoView) findViewById(R.id.vv_play);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        View ivLayout = findViewById(R.id.ic_layout);
        btnPlay = (Button) ivLayout.findViewById(R.id.btn_play_pause);
        btnPre = (Button) ivLayout.findViewById(R.id.btn_play_pre);
        btnNext = (Button) ivLayout.findViewById(R.id.btn_play_next);
        tvNowTime = (TextView) ivLayout.findViewById(R.id.tv_now_time);
        tvAllTime = (TextView) ivLayout.findViewById(R.id.tv_alltime);
        sbVoice = (SeekBar) ivLayout.findViewById(R.id.sb_voice);
        sbVideo = (SeekBar) ivLayout.findViewById(R.id.sb_video);
        tvvideoTitle = (TextView) ivLayout.findViewById(R.id.tv_video_title);
        tvSysTime = (TextView) ivLayout.findViewById(R.id.tv_sysTime);
        ivBattery = (ImageView) ivLayout.findViewById(R.id.iv_battery);
        llCtrlPlayer = (LinearLayout) findViewById(R.id.ll_ctrl_player);

    }

    private void playSetting() {
        vvPlay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ll_loading.setVisibility(View.GONE);

                //sbVideo.setMax(duration);
                hideCtrl();
                handler.sendEmptyMessage(PROGRESS);
            }
        });
    }

    private void startSDplay() {
        vvPlay.setVideoURI(data);
        //vvPlay.setMediaController(new MediaController(this));
        vvPlay.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        vvPlay.requestFocus();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void startPlay() {
        vvPlay.setVideoPath(path);
        vvPlay.setVideoURI(Uri.parse(path));
        //vvPlay.setMediaController(new MediaController(this));
        vvPlay.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        vvPlay.requestFocus();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        detector.onTouchEvent(event);
        return true;

    }

    private void removeDelayedHideCtrlPlayer(){
        handler.removeMessages(HIDEPROGRESSCTRL);
    }
    private void hideCtrl(){
        llCtrlPlayer.setVisibility(View.GONE);
        isShowControl = false;
    }
    private boolean sendDelayedHideCtrlPlayer(){
        return handler.sendEmptyMessageDelayed(HIDEPROGRESSCTRL,2000);
    }
    private void showCtrl(){
        llCtrlPlayer.setVisibility(View.VISIBLE);
        isShowControl = true;
    }
    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        unregisterReceiver(receiver);
        receiver = null;
    }
}
