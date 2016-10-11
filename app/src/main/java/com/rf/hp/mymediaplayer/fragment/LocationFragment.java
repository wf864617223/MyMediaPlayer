package com.rf.hp.mymediaplayer.fragment;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rf.hp.mymediaplayer.R;
import com.rf.hp.mymediaplayer.utils.Utils;

import java.io.File;
import java.util.ArrayList;

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
    private ArrayList<VideoItem> videoItems = new ArrayList<>();

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
        findAllVideo();
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
            //holder.tv_size.setText(Formatter.formatFileSize(getContext(), videoItem.getSize()));

            return view;
        }
    }
    static class ViewHolder{
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
    }
    public void findAllVideo() {
        path = Environment.getExternalStorageDirectory();
        getAllVideo(path);

    }

    private void getAllVideo(final File roots) {
        new Thread(){
            public void run(){
                final File[] file = roots.listFiles();
                for (int i = 0;(file != null) && (i < file.length); i++) {
                    if(file[i].isDirectory()){
                        //final File[] tempFileList = new File().listFiles();
                        getAllVideo(file[i]);
                    }else if(file[i].isFile() && FileUtils.isVideo(file[i])){
                        VideoItem item = new VideoItem();
                        String size = MediaStore.Video.Media.SIZE;
                        item.setSize(123454657);
                        item.setData("54321");
                        item.setDuration("12345");
                        item.setTitle(file[i].getName());
                        videoItems.add(item);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }
}
