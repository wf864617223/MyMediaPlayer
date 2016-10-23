package com.rf.hp.mymediaplayer.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

import com.rf.hp.mymediaplayer.R;

/**
 * Created by MI on 2016/10/23.
 */
public class PrgDialog {
    private ProgressDialog dialog;
    public Context context;

    public PrgDialog(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context,R.style.CustomProgressDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条的形式为圆形转动的进度条
        dialog.show();//
        dialog.setContentView(R.layout.progress_dialog);// 设置布局
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
    }


    public void closeDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
