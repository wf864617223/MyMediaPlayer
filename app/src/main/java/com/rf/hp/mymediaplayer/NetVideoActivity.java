package com.rf.hp.mymediaplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class NetVideoActivity extends AppCompatActivity {

    private WebView wvNetVideo;
    private ProgressBar pbLine;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_video);
        context = NetVideoActivity.this;
        getSupportActionBar().hide();
        wvNetVideo = (WebView) findViewById(R.id.wv_netVideo);
        pbLine = (ProgressBar) findViewById(R.id.pb_line);
        init();
    }

    private void init() {
        //Bundle extras = getIntent().getExtras();
        Intent intent = getIntent();
        String uri = intent.getStringExtra("uri");
        //Toast.makeText(context, uri, Toast.LENGTH_SHORT).show();
        wvNetVideo.loadUrl(uri);
        WebSettings settings = wvNetVideo.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        wvNetVideo.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                wvNetVideo.loadUrl(url);
                return true;
            }
        });
        wvNetVideo.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress == 100){
                    pbLine.setVisibility(View.GONE);
                }else{
                    if(View.INVISIBLE == pbLine.getVisibility()){
                        pbLine.setVisibility(View.VISIBLE);
                    }
                    pbLine.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if(wvNetVideo.canGoBack()) {
                wvNetVideo.goBack();//返回上一页面
                return true;
            }
            else {
                finish();//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
