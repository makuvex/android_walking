package com.friendly.walking.adapter.viewHolder;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 뷰 재활용을 위한 viewHolder
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder{

    protected View                      mView;
    protected static Activity mActivity;

    public BaseViewHolder(Activity activity, View itemView) {
        super(itemView);
        mView = itemView;
        mActivity = activity;
    }

     public abstract  BaseViewHolder setLayout(Activity activity);
}