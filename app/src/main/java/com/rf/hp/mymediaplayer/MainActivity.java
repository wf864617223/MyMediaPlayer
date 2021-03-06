package com.rf.hp.mymediaplayer;

import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rf.hp.mymediaplayer.fragment.LocationFragment;
import com.rf.hp.mymediaplayer.fragment.NetWorkFrgment;
import com.rf.hp.mymediaplayer.utils.MarsQuestion;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private FrameLayout flVideoList;
    private RadioButton rbLocation;
    private RadioButton rbNetWork;
    private RadioGroup rgVideo;
    private LocationFragment locationFragment;
    private NetWorkFrgment netWorkFrgment;
    private FragmentTransaction transaction;
    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        flVideoList = (FrameLayout) findViewById(R.id.fl_videolist);
        rgVideo = (RadioGroup) findViewById(R.id.rg_myVideo);
        rbLocation = (RadioButton) findViewById(R.id.btn_modelocation);
        rbNetWork = (RadioButton) findViewById(R.id.btn_modelNetWork);
        mkAppDirs();
        init();
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK&&event.getAction() == KeyEvent.ACTION_DOWN){
            if(System.currentTimeMillis() - exitTime >2000){
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime =  System.currentTimeMillis();
            }else{
                finish();
            }
        }
        return true;
    }

    private void mkAppDirs() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            System.out.println("SD卡不可用");
            Toast.makeText(MainActivity.this, "SD卡不可用", Toast.LENGTH_SHORT).show();
        }else{
            MarsQuestion.verifyStoragePermissions(MainActivity.this);
        }
    }
    private void init() {

        transaction = getSupportFragmentManager().beginTransaction().
                setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        locationFragment = new LocationFragment();
        netWorkFrgment = new NetWorkFrgment();
        transaction.add(R.id.fl_videolist,locationFragment);
        transaction.add(R.id.fl_videolist,netWorkFrgment);
        transaction.commit();
        transaction.hide(locationFragment);
        transaction.hide(netWorkFrgment);
        rbLocation.setChecked(true);
        if(rbLocation.isChecked()){
            transaction = getSupportFragmentManager().beginTransaction();
            //.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            transaction.show(locationFragment);
            transaction.hide(netWorkFrgment);
            transaction.commit();
        }
        rgVideo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.btn_modelocation:
                        transaction = getSupportFragmentManager().beginTransaction().
                                setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                        transaction.hide(netWorkFrgment);
                        transaction.show(locationFragment);

                        transaction.commit();
                        break;
                    case R.id.btn_modelNetWork:
                        transaction = getSupportFragmentManager().beginTransaction().
                                setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                        transaction.hide(locationFragment);
                        transaction.show(netWorkFrgment);
                        transaction.commit();
                        break;
                }
            }
        });
    }
}
