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
import android.view.Display;
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
import com.rf.hp.mymediaplayer.view.VerticalSeekBar;

import java.io.Serializable;
import java.util.ArrayList;

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
    private VerticalSeekBar sbVoice;
    private SeekBar sbVideo;
    private TextView tvvideoTitle;
    private TextView tvSysTime;
    private ImageView ivBattery;
    private LinearLayout llCtrlPlayer;
    private Button btnVoice;
    private VerticalSeekBar sbLight;

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
    private static final int PLAY_VIDEO_PRE = 3;
    private static final int PLAY_VIDEO_NEXT = 4;
    private BroadcastReceiver receiver;
    private boolean isPlaying = false;
    private int level;
    private Utils utils;
    Context context;
    private AudioManager am;
    private float startY;
    private float TouchRang;
    private int mVol;
    private ArrayList<VideoItem> videoItems = new ArrayList<>();
    private int maxVolume;

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
                case PLAY_VIDEO_NEXT:
                    //Toast.makeText(VideoPlayActivity.this, "next", Toast.LENGTH_SHORT).show();
                    playNextVideo();
                    break;
                case PLAY_VIDEO_PRE:
                    playPreVideo();
                    break;
                default:
                    break;
            }
        }
    };
    private int position;
    private float mLight;

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
        data = getIntent().getData();
        if (path == null) {
            startSDplay();
            btnPre.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
        } else {
            videoItems = (ArrayList<VideoItem>) bundle.getSerializable("videolist");
            String s = videoItems.toString();
            Log.i("VideoPlayActivity","==videoItems==>"+s);
            position = bundle.getInt("position", 0);
            startPlay(path);
        }
    }

    private void setListener() {
        btnPlay.setOnClickListener(mOnClickListener);
        btnPre.setOnClickListener(mOnClickListener);
        btnNext.setOnClickListener(mOnClickListener);
        btnVoice.setOnClickListener(mOnClickListener);

        sbVoice.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar VerticalBar, int progress, boolean fromUser) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = progress;

            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar VerticalBar) {
                removeDelayedHideCtrlPlayer();
            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar VerticalBar) {
                sendDelayedHideCtrlPlayer();

            }
        });
        /*sbVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateVolume(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                removeDelayedHideCtrlPlayer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendDelayedHideCtrlPlayer();
            }
        });*/
        sbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    vvPlay.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                removeDelayedHideCtrlPlayer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendDelayedHideCtrlPlayer();
            }
        });
        sbLight.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar seekbar, int progress, boolean fromUser) {
                if(fromUser){
                    setScreenBrightness(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {
                removeDelayedHideCtrlPlayer();
            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {
                sendDelayedHideCtrlPlayer();
            }
        });
        vvPlay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextVideo();
            }
        });
        vvPlay.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(VideoPlayActivity.this, "该视频可能已经损坏，无法播放！", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    //设置屏幕亮度的函数
    private void setScreenBrightness(float num){
        WindowManager.LayoutParams layoutParams=super.getWindow().getAttributes();
        layoutParams.screenBrightness=num/10;//设置屏幕的亮度
        super.getWindow().setAttributes(layoutParams);
    }
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            removeDelayedHideCtrlPlayer();
            sendDelayedHideCtrlPlayer();
            Message message = handler.obtainMessage();
            switch (view.getId()){
                case R.id.btn_play_pause:
                    startOrPause();
                    break;
                case R.id.btn_play_pre:
                    /*message.what = PLAY_VIDEO_PRE;
                    handler.sendMessage(message);*/
                    playPreVideo();
                    break;
                case R.id.btn_play_next:
                   /* message.what = PLAY_VIDEO_NEXT;
                    handler.sendMessage(message);*/
                    playNextVideo();
                    break;
                case R.id.btn_voice:
                    isMute = !isMute;
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
                //vvPlay.stopPlayback();
                String s = videoItems.get(position).getPath();
                Log.i("VideoPlayActivity","===path==>"+s);
                //startPlay(s);
                vvPlay.setVideoPath(s);
                //vvPlay.setVideoURI(Uri.parse(s));
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
                //vvPlay.stopPlayback();
                String s = videoItems.get(position).getPath();
                videoItems.get(position).getData();
                Log.i("VideoPlayActivity","===path==>"+s);
                //startPlay(s);
                vvPlay.setVideoPath(s);
                //vvPlay.setVideoURI(Uri.parse(s));
            }else{
                position = 0;
                Toast.makeText(VideoPlayActivity.this, "这是第一个视频", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void updateVolume(int volume) {

        int i = 0;
        if(isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            sbVoice.setProgress(0);
        }else{
            i++;
            Log.i("updateVoice","===volume==>"+volume+"==currentVolume==>"+currentVolume+"===i=>"+i);
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
                float oldX =e1.getX();
                float oldY = e2.getY();
                Display defaultDisplay = getWindowManager().getDefaultDisplay();
                int width = defaultDisplay.getWidth();
                int height = defaultDisplay.getHeight();
                long currentPosition = vvPlay.getCurrentPosition();
                int halfWidth = screenWidth / 2;
                if(Math.abs(distanceX)>=Math.abs(distanceY)){
                    if(Math.abs(distanceX)>Math.abs(distanceY)){
                        //进行快进、快退操作
                        //Toast.makeText(VideoPlayActivity.this, "快进、快退操作", Toast.LENGTH_SHORT).show();
                        onVideoSpeed(distanceX);
                    }
                }else if(Math.abs(oldX)<=halfWidth){
                    Toast.makeText(VideoPlayActivity.this, "音量操作", Toast.LENGTH_SHORT).show();
                    onVolumeSilde(e2);

                }else if(oldX>halfWidth){
                    //Toast.makeText(VideoPlayActivity.this, "亮度操作", Toast.LENGTH_SHORT).show();
                    WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                    int screenBrightness = (int)layoutParams.screenBrightness;
                    if(oldY>distanceY&&Math.abs(screenBrightness)==1){
                        Toast.makeText(VideoPlayActivity.this, "已经是最亮", Toast.LENGTH_SHORT).show();
                    }
                    onLightSilde(e2);
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

    }

    private void onLightSilde(MotionEvent event) {

        float endY = event.getY();
        float distanceY = startY - endY;
        float date1 = distanceY/TouchRang;
        float lights = distanceY/TouchRang*1;
        float Ylight = Math.min(Math.max(lights+mLight,0),1);
        if(date1!=0){
            setScreenBrightness(Ylight*10);
            sbLight.setProgress((int) (Ylight*10));
        }
    }


    private void onVolumeSilde(MotionEvent event) {
        float endY = event.getY();
        float distanceY = startY - endY;
        float date1 = distanceY/TouchRang;

        float volume = distanceY/TouchRang*maxVolume;

        float volemeS = Math.min(Math.max(volume+mVol, 0), maxVolume);
        if(date1!=0){
            updateVolume((int)volemeS);
        }
    }

    private void onVideoSpeed(float distanceX) {
        int currentPosition = (int)vvPlay.getCurrentPosition();
        int duration = (int)vvPlay.getDuration();
        if(distanceX < 0){
            currentPosition += duration/1000;
            vvPlay.seekTo(currentPosition);
            setProgress(currentPosition);
        }else if(distanceX > 0){
            currentPosition -=duration/10;
            vvPlay.seekTo(currentPosition);
            setProgress(currentPosition);
        }
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
        sbVoice = (VerticalSeekBar) findViewById(R.id.sb_voice);
        sbVideo = (SeekBar) findViewById(R.id.sb_video);
        tvvideoTitle = (TextView) findViewById(R.id.tv_video_title);
        tvSysTime = (TextView) findViewById(R.id.tv_sysTime);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        llCtrlPlayer = (LinearLayout) findViewById(R.id.ll_ctrl_player);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        sbLight = (VerticalSeekBar) findViewById(R.id.sb_light);
        utils = new Utils();
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sbVoice.setProgress(currentVolume);
        sbVoice.setMax(maxVolume);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        float screenBrightness = layoutParams.screenBrightness;
        System.out.println("===light==>"+screenBrightness);
        float abs = Math.abs(screenBrightness);
        System.out.println("===light==>"+abs);
        sbLight.setMax(10);
        sbLight.setProgress((int) (abs*10));
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
    private void startPlay(String paths) {
        vvPlay.setVideoPath(paths);
        vvPlay.setVideoURI(Uri.parse(paths));
        //vvPlay.setMediaController(new MediaController(this));
        vvPlay.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH, 0);
        vvPlay.requestFocus();
        //vvPlay.start();
        String[] split = paths.split("/");
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
                removeDelayedHideCtrlPlayer();
                startY = event.getY();
                TouchRang = Math.min(screenHeight, screenWidth);
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                mLight = layoutParams.screenBrightness;
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
