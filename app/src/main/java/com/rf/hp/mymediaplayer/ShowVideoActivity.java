package com.rf.hp.mymediaplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rf.hp.mymediaplayer.Asunc.ScanAsyncTask;
import com.rf.hp.mymediaplayer.bean.VideoItem;
import com.rf.hp.mymediaplayer.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import io.vov.vitamio.utils.FileUtils;

public class ShowVideoActivity extends AppCompatActivity {

    private ListView lvVideo;
    private TextView tvNoVideo;
    private Utils utils;
    private ArrayList<VideoItem> videoItems = new ArrayList<>();
    Context context;
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            if(videoItems != null&&videoItems.size()>0){
                lvVideo.setAdapter(new VideoListAdapter());
            }else{
                tvNoVideo.setVisibility(View.VISIBLE);
            }

        };
    };
    private File path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_location);
        getSupportActionBar().hide();
        context = ShowVideoActivity.this;
        lvVideo = (ListView) findViewById(R.id.lv_video);
        tvNoVideo = (TextView) findViewById(R.id.tv_noVideo);
        init();
    }


    private void init() {
        utils = new Utils();
        //getAllVideo();
        videoItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra("videolist");
        lvVideo.setAdapter(new VideoListAdapter());
        String s = videoItems.toString();
        System.out.print("videolitems==>"+s);
        lvVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = videoItems.get(i).getPath();
                Intent intent = new Intent(context, VideoPlayActivity.class);
                //intent.putExtra("path",path);
                Bundle extras = new Bundle();
                extras.putSerializable("videolist", videoItems);
                extras.putString("path",path);
                extras.putInt("position",i);
                intent.putExtras(extras);
                startActivity(intent);
                //Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class VideoListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return videoItems.size();
        }

        @Override
        public Object getItem(int i) {
            return videoItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View converView, ViewGroup viewGroup) {
            View view;
            ViewHolder holder;
            if(converView != null){
                view = converView;
                holder = (ViewHolder)view.getTag();
            }else{
                view = View.inflate(context, R.layout.video_list_item, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_duration = (TextView) view.findViewById(R.id.tv_duration);
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_size = (TextView) view.findViewById(R.id.tv_size);

                view.setTag(holder);
            }
            VideoItem videoItem = videoItems.get(position);
            holder.tv_name.setText(videoItem.getTitle());
            //holder.tv_duration.setText(utils.stringForTime(Integer.valueOf(videoItem.getDuration())));
            Long aLong = Long.parseLong(videoItem.getDuration());
            String path = videoItem.getPath();
            //mediaretriever
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            try {
                mediaMetadataRetriever.setDataSource(path);
                Bitmap frameAtTime = mediaMetadataRetriever.getFrameAtTime(4000);
                holder.iv_icon.setImageBitmap(frameAtTime);
            } catch (Exception e) {
                e.printStackTrace();
                holder.iv_icon.setImageResource(R.drawable.icon);
            }

            holder.tv_duration.setText(utils.longForDate(aLong));
            holder.tv_size.setText(Formatter.formatFileSize(context, videoItem.getSize()));

            return view;
        }
    }
    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }
    private void getAllVideo() {
        /*new Thread(){
            public void run(){
                path = Environment.getExternalStorageDirectory();
                seeAllVideos(path);
                handler.sendEmptyMessage(0);
            }
        }.start();*/
        ScanAsyncTask ansyTask = new ScanAsyncTask();
        AsyncTask<Void, Integer, ArrayList<VideoItem>> execute = ansyTask.execute();
        try {
            videoItems = execute.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(0);
    }

    private void seeAllVideos(final File dir) {
        final File[] file = dir.listFiles();
        for (int i = 0; (file != null) && (i < file.length); i++) {
            /*if(file[i].isDirectory()){
                //final File[] tempFileList = new File().listFiles();
                seeAllVideos(file[i]);
            }else*/
            if (file[i].isFile() && FileUtils.isVideo(file[i])) {
                VideoItem item = new VideoItem();
                int size = 0;
                item.setData("54321");
                item.setDuration("12345");
                item.setTitle(file[i].getName());
                try {
                    FileInputStream fileInputStream = new FileInputStream(file[i]);
                    size = fileInputStream.available(); //这就是文件大小
                } catch (Exception e) {
                    e.printStackTrace();
                }
                item.setSize(size);
                videoItems.add(item);
            }
        }
        //
    }
}