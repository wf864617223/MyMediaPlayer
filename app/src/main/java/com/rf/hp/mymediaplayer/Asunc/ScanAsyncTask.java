package com.rf.hp.mymediaplayer.Asunc;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;

import io.vov.vitamio.utils.FileUtils;

/**
 * Created by hp on 2016/10/11.
 */
public class ScanAsyncTask extends AsyncTask<Void,File,Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        eachAllMedias(Environment.getExternalStorageDirectory());
        return null;
    }

    @Override
    protected void onProgressUpdate(File... values) {
        super.onProgressUpdate(values);
    }

    /** 遍历所有文件夹，查找出视频文件 */
    public void eachAllMedias(File f) {
        if (f != null && f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File file : f.listFiles()) {
                    if (file.isDirectory()) {
                        eachAllMedias(file);
                    } else if (file.exists() && file.canRead() && FileUtils.isVideoOrAudio(file)) {
                        publishProgress(file);
                    }
                }
            }
        }
    }
}
