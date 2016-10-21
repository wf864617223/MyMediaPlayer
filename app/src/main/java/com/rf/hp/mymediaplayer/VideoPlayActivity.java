package com.rf.hp.mymediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
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


import com.rf.hp.mymediaplayer.bean.VideoItem;
import com.rf.hp.mymediaplayer.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;

import io.vov.vitamio.MediaPlayer;
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
    private Button btnVoice;

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
    //是否静音
    private boolean isMute = false;
    //
    private static final int PROGRESS = 1;
    //
    private static final int HIDEPROGRESSCTRL = 2;
    private BroadcastReceiver receiver;
    private boolean isPlaying = false;
    private int level;
    private Utils utils;
    Context context;
    private AudioManager am;
    private ArrayList<VideoItem> videoItems = new ArrayList<>();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PROGRESS:
                    int currentPosition = (int)vvPlay.getCurrentPosition();;
                    //tvAllTime.setText(utils.stringForTime(currentPosition));
                    Log.i("VideoPlay","==currentPosition==>"+currentPosition);
                    System.out.println("==currentPosition==>"+currentPosition);
                    tvNowTime.setText(utils.stringForTime(currentPosition));
                    sbVideo.setProgress(currentPosition);
                    tvSysTime.setText(utils.getSystemTime());
                    setBattey();
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
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_play);
        context = VideoPlayActivity.this;
        getSupportActionBar().hide();
        initView();
        getData();
        playSetting();
        initData();
        setListener();
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        path = bundle.getString("path");
        videoItems = (ArrayList<VideoItem>) bundle.getSerializable("videolist");
        position = bundle.getInt("position", 0);
        data = getIntent().getData();
        if (path == null) {
            startSDplay();
        } else {
            startPlay();
        }
    }

    private void setListener() {
        btnPlay.setOnClickListener(mOnClickListener);
        btnPre.setOnClickListener(mOnClickListener);
        btnNext.setOnClickListener(mOnClickListener);
        btnVoice.setOnClickListener(mOnClickListener);
        vvPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
        vvPlay.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(VideoPlayActivity.this, "该视频可能已经损害，无法播放！", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            removeDelayedHideCtrlPlayer();
            sendDelayedHideCtrlPlayer();
            switch (view.getId()){
                case R.id.btn_play_pause:
                    startOrPause();
                    break;
                case R.id.btn_play_pre:
                    playPreVideo();
                    break;
                case R.id.btn_play_next:
                    playNextVideo();
                    break;
                case R.id.btn_voice:
                    updateVolume(currentVolume);
                    break;
                default:
                    break;
            }
        }
    };

    private void playNextVideo() {

        if(videoItems!=null||videoItems.size()>0){
            position++;
            if(position<videoItems.size()){
                path = videoItems.get(position).getPath();
                startPlay();
            }else {
                position = videoItems.size()-1;
                Toast.makeText(VideoPlayActivity.this, "这是最后一个视频", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void playPreVideo() {
        if(videoItems!=null||videoItems.size()>0){
            position--;
            if(position>=0){
                path =  videoItems.get(position).getPath();
                startPlay();
            }else{
                position = 0;
                Toast.makeText(VideoPlayActivity.this, "这是第一个视频", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateVolume(int volume) {
        isMute = !isMute;
        if(isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
        }else{
            am.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);
            sbVoice.setProgress(volume);
        }
        currentVolume = volume;
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
        receiver = new MyBroadcastReceiver();
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
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            level = intent.getIntExtra("level", 0);
        }

    }

    /**
     * 初始化ID
     */
    private void initView() {
        vvPlay = (VideoView) findViewById(R.id.vv_play);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        View ivLayout = findViewById(R.id.ic_layout);
        btnPlay = (Button) findViewById(R.id.btn_play_pause);
        btnPre = (Button) findViewById(R.id.btn_play_pre);
        btnNext = (Button) findViewById(R.id.btn_play_next);
        tvNowTime = (TextView) findViewById(R.id.tv_now_time);
        tvAllTime = (TextView) findViewById(R.id.tv_alltime);
        sbVoice = (SeekBar) findViewById(R.id.sb_voice);
        sbVideo = (SeekBar) findViewById(R.id.sb_video);
        tvvideoTitle = (TextView) findViewById(R.id.tv_video_title);
        tvSysTime = (TextView) findViewById(R.id.tv_sysTime);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        llCtrlPlayer = (LinearLayout) findViewById(R.id.ll_ctrl_player);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        utils = new Utils();
    }

    /**
     * 电池电量图标设置
     */
    private void setBattey(){
        if(level<=10){
            ivBattery.setImageResource(R.drawable.battery_10);
        }else if(level<=20){
            ivBattery.setImageResource(R.drawable.battery_20);
        }else if(level<=50){
            ivBattery.setImageResource(R.drawable.battery_50);
        }else if(level<=80){
            ivBattery.setImageResource(R.drawable.battery_80);
        }else if(level<=100){
            ivBattery.setImageResource(R.drawable.battery_100);
        }
    }
    private void playSetting() {
        vvPlay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ll_loading.setVisibility(View.GONE);
                isPlaying = true;
                int duration = (int)vvPlay.getDuration();

                tvAllTime.setText(utils.stringForTime(duration));
                sbVideo.setMax(duration);
                hideCtrl();
                handler.sendEmptyMessage(PROGRESS);
            }
        });
    }

    /**
     * 当从SD卡进来时调用这个方法
     */
    private void startSDplay() {
        vvPlay.setVideoURI(data);

        //vvPlay.setMediaController(new MediaController(this));
        vvPlay.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        vvPlay.requestFocus();
        String path = data.getPath();
        String[] split =  path.split("/");
        String s = split[split.length - 1];
        tvvideoTitle.setText(s);
        //Toast.makeText(VideoPlayActivity.this, "data==>"+ this.data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * 从主页面进来调用这个方法
     */
    private void startPlay() {
        vvPlay.setVideoPath(path);
        vvPlay.setVideoURI(Uri.parse(path));
        //vvPlay.setMediaController(new MediaController(this));
        vvPlay.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        vvPlay.requestFocus();
        String[] split = path.split("/");
        String s = split[split.length - 1];
        tvvideoTitle.setText(s);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                removeDelayedHideCtrlPlayer();
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                sendDelayedHideCtrlPlayer();
                break;
        }
        return true;

    }

    private void startOrPause(){
        if(isPlaying){
            vvPlay.pause();
            btnPlay.setBackgroundResource(R.drawable.btn_play_selector);
        }else{
            vvPlay.start();
            btnPlay.setBackgroundResource(R.drawable.btn_pause_selector);
        }
        isPlaying = !isPlaying;
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
