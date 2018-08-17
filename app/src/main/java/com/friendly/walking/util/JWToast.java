package com.friendly.walking.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.R;


public class JWToast extends View {
    private Context             mContext = null;
    private static Toast        mToast = null;
    private TextView            text;


    public static void showToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            JWToast toast = new JWToast(ApplicationPool.getGlobalApplicationContext());
            toast.setToast(msg);
        }
    }

    public static void showToastLong(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            JWToast toast = new JWToast(ApplicationPool.getGlobalApplicationContext());
            toast.setToastLong(msg);
        }
    }

    public static void showToast(int res) {
        if(res != 0) {
            JWToast toast = new JWToast(ApplicationPool.getGlobalApplicationContext());
            toast.setToast(res);
        }
    }

    public static void showToastAndLog(String tag, String msg) {
        if (!TextUtils.isEmpty(msg)) {
            JWLog.d(tag, msg);
            JWToast toast = new JWToast(ApplicationPool.getGlobalApplicationContext());
            toast.setToast(msg);
        }
    }

    public JWToast(Context context) {
        super(context);
        this.mContext = context;
    }

    public void setToast(String toastMsg) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_view, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        text = (TextView) layout.findViewById(R.id.toast_text);

        if (mToast == null) {
            mToast = new Toast(mContext);
            mToast.setGravity(Gravity.BOTTOM, 0, 0);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }

        text.setText(toastMsg);
        mToast.setView(layout);
        mToast.show();
    }

    public void setToastLong(String toastMsg) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_view, (ViewGroup) findViewById(R.id.custom_toast_layout_id));
        text = (TextView) layout.findViewById(R.id.toast_text);

        if (mToast == null) {
            mToast = new Toast(mContext);
            mToast.setGravity(Gravity.BOTTOM, 0, 0);
            mToast.setDuration(Toast.LENGTH_LONG);
        }

        text.setText(toastMsg);
        mToast.setView(layout);
        mToast.show();
    }

    public void setToast(int res) {
        String text = mContext.getText(res).toString();
        setToast(text);
    }

}
