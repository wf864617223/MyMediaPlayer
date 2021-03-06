package com.rf.hp.mymediaplayer.Asunc;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.rf.hp.mymediaplayer.bean.VideoItem;
import com.rf.hp.mymediaplayer.view.PrgDialog;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.FileUtils;

/**
 * Created by hp on 2016/10/11.
 */
public class ScanAsyncTask extends AsyncTask <Void,Integer,ArrayList<VideoItem>>{
    private ArrayList<VideoItem> videoInfos=new ArrayList<VideoItem>();
   // private PrgDialog prgDialog;
   // Context context;
   // private OnTaskListener listener;

    /*public interface OnTaskListener{
        void onSuccess();
        void onFilde();
    }
    public ScanAsyncTask(Context context,OnTaskListener onTaskListener){
        this.context = context;
        this.listener = onTaskListener;
    }*/
    @Override
    protected ArrayList<VideoItem> doInBackground(Void... params) {

        videoInfos=getVideoFile(videoInfos, Environment.getExternalStorageDirectory());
        videoInfos=filterVideo(videoInfos);
        Log.i("tga","最后的大小"+videoInfos.size());

        return videoInfos;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //prgDialog = new PrgDialog(context);
    }

    @Override
    protected void onPostExecute(ArrayList<VideoItem> videoInfos) {
        super.onPostExecute(videoInfos);

    }

    /**
     * 获取视频文件
     * @param list
     * @param file
     * @return
     */
    private ArrayList<VideoItem> getVideoFile(final ArrayList<VideoItem> list, File file) {

        file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {

                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4")
                            || name.equalsIgnoreCase(".3gp")
                            || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".ts")
                            || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".mov")
                            || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".avi")
                            || name.equalsIgnoreCase(".m3u8")
                            || name.equalsIgnoreCase(".3gpp")
                            || name.equalsIgnoreCase(".3gpp2")
                            || name.equalsIgnoreCase(".mkv")
                            || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".divx")
                            || name.equalsIgnoreCase(".f4v")
                            || name.equalsIgnoreCase(".rm")
                            || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".ram")
                            || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".v8")
                            || name.equalsIgnoreCase(".swf")
                            || name.equalsIgnoreCase(".m2v")
                            || name.equalsIgnoreCase(".asx")
                            || name.equalsIgnoreCase(".ra")
                            || name.equalsIgnoreCase(".ndivx")
                            || name.equalsIgnoreCase(".xvid")) {
                        VideoItem video = new VideoItem();
                        file.getUsableSpace();
                        video.setTitle(file.getName());
                        video.setPath(file.getAbsolutePath());
                        String res = "";
                        int size = 0;
                        long lastModified = 0;
                        FileInputStream fileInputStream = null;
                        try {
                            fileInputStream = new FileInputStream(file);
                            size = fileInputStream.available(); //这就是文件大小
                            //res = EncodingUtils.getString(buffer, "UTF-8");
                            lastModified = file.lastModified();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        video.setDuration(lastModified+"");
                        video.setSize(size);
                        Log.i("tga","name"+video.getPath());
                        list.add(video);

                        return true;
                    }
                    //判断是不是目录
                } else if (file.isDirectory()) {
                    getVideoFile(list, file);
                }
                return false;
            }
        });

        return list;
    }

    /**10M=10485760 b,小于10m的过滤掉
     * 过滤视频文件
     * @param videoInfos
     * @return
     */
    private ArrayList<VideoItem> filterVideo(ArrayList<VideoItem> videoInfos){
        ArrayList<VideoItem> newVideos=new ArrayList<VideoItem>();
        for(VideoItem videoInfo:videoInfos){
            File f=new File(videoInfo.getPath());
            if(f.exists()&&f.isFile()&&f.length()>10485760){
                newVideos.add(videoInfo);
                Log.i("TGA","文件大小"+f.length());
            }else {
                Log.i("TGA","文件太小或者不存在");
            }
        }
        return newVideos;
    }
}