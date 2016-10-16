package com.rf.hp.mymediaplayer.fragment;


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rf.hp.mymediaplayer.Asunc.ScanAsyncTask;
import com.rf.hp.mymediaplayer.R;
import com.rf.hp.mymediaplayer.VideoPlayActivity;
import com.rf.hp.mymediaplayer.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.rf.hp.mymediaplayer.bean.VideoItem;

import io.vov.vitamio.provider.MediaStore;
import io.vov.vitamio.utils.FileUtils;

/**
 * Created by hp on 2016/10/11.
 */
public class LocationFragment extends Fragment {

    private ListView lvVideo;
    private TextView tvNoVideo;
    private Utils utils;
    private List<VideoItem> videoItems = new ArrayList<>();

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_location,null);
        lvVideo = (ListView) view.findViewById(R.id.lv_video);
        tvNoVideo = (TextView) view.findViewById(R.id.tv_noVideo);
        init();
        return view;
    }

    private void init() {
        utils = new Utils();
        getAllVideo();
        lvVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String path = videoItems.get(i).getPath();
                Intent intent = new Intent(getContext(), VideoPlayActivity.class);
                intent.putExtra("path",path);
                startActivity(intent);
                //Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class VideoListAdapter extends BaseAdapter{

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
                view = View.inflate(getContext(), R.layout.video_list_item, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_duration = (TextView) view.findViewById(R.id.tv_duration);

                holder.tv_size = (TextView) view.findViewById(R.id.tv_size);

                view.setTag(holder);
            }
            VideoItem videoItem = videoItems.get(position);
            holder.tv_name.setText(videoItem.getTitle());
            //holder.tv_duration.setText(utils.stringForTime(Integer.valueOf(videoItem.getDuration())));
            Long aLong = Long.parseLong(videoItem.getDuration());
            holder.tv_duration.setText(utils.longForDate(aLong));
            holder.tv_size.setText(Formatter.formatFileSize(getContext(), videoItem.getSize()));

            return view;
        }
    }
    static class ViewHolder{
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
        ScanAsyncTask ansyTask=new ScanAsyncTask();
        AsyncTask<Void, Integer, List<VideoItem>> execute = ansyTask.execute();
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
        for (int i = 0;(file != null) && (i < file.length); i++) {
            /*if(file[i].isDirectory()){
                //final File[] tempFileList = new File().listFiles();
                seeAllVideos(file[i]);
            }else*/ if(file[i].isFile() && FileUtils.isVideo(file[i])){
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
