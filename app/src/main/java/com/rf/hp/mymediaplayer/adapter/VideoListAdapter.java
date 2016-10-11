package com.rf.hp.mymediaplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rf.hp.mymediaplayer.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hp on 2016/10/11.
 */
public class VideoListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<File> vList;

    public VideoListAdapter(Context context, ArrayList<File> vList) {
        this.context = context;
        this.vList = vList;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        /*final File f = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.viewholder_vlist, null);
        }
        ((TextView) convertView.findViewById(R.id.title)).setText(f.getName());
        return convertView;*/
        return null;
    }
}
