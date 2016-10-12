package com.rf.hp.mymediaplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rf.hp.mymediaplayer.NetVideoActivity;
import com.rf.hp.mymediaplayer.R;

/**
 * Created by hp on 2016/10/11.
 */
public class NetWorkFrgment extends Fragment {

    Context context;
    private ListView lvNetVideo;
    private int[] piclist = {R.mipmap.logo_iqiyi,R.mipmap.logo_letv,R.mipmap.logo_pptv,R.mipmap.logo_qq,
    R.mipmap.logo_sina,R.mipmap.logo_sohu,R.mipmap.logo_tudou,R.mipmap.logo_youku};
    private String[] nameList = {"爱奇艺","乐视","PPTV","腾讯视频","新浪视频","搜狐视频","土豆网","优酷"};
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_network,null);
        context = getContext();
        lvNetVideo = (ListView) view.findViewById(R.id.lv_netVideo);
        lvNetVideo.setAdapter(new NetWorkVideoAdapter());
        lvNetVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, NetVideoActivity.class);
                switch (i){
                    case 0:
                        Log.i("TAG","aiqiyi");
                        //Toast.makeText(context, "aiqiyi", Toast.LENGTH_SHORT).show();
                        intent.putExtra("uri","http://www..com");
                        break;
                    case 1:
                       // Toast.makeText(context, "leshi", Toast.LENGTH_SHORT).show();
                        intent.putExtra("uri","http://www..com");
                        break;
                    case 2:
                        intent.putExtra("uri","http://www..com");
                        break;
                    case 3:
                        intent.putExtra("uri","http://www..com");
                        break;
                    case 4:
                        intent.putExtra("uri","http://www..com");
                        break;
                    case 5:
                        intent.putExtra("uri","http://www..com");
                        break;
                    case 6:
                        intent.putExtra("uri","http://www..com");
                        break;
                    case 7:
                        intent.putExtra("uri","http://www.youku.com");
                        break;
                    default:
                        //Toast.makeText(context, "qita", Toast.LENGTH_SHORT).show();
                        break;
                }
                startActivity(intent);
            }
        });
        return view;
    }
    private class NetWorkVideoAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return piclist.length;
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
            ViewHolder viewHolder;
            if(convertView != null){
                viewHolder = (ViewHolder) convertView.getTag();
            }else{
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.viewholder_netlist,null);
                viewHolder = new ViewHolder();
                viewHolder.ivNet = (ImageView) convertView.findViewById(R.id.lv_net);
                viewHolder.tvNetName = (TextView) convertView.findViewById(R.id.tv_netName);
            }
            viewHolder.ivNet.setImageResource(piclist[position]);
            viewHolder.tvNetName.setText(nameList[position]);
            return convertView;
        }
    }
    static class ViewHolder{
        ImageView ivNet;
        TextView tvNetName;
    }
}
