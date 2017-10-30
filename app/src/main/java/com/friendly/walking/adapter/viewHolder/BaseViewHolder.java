package com.friendly.walking.adapter.viewHolder;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 뷰 재활용을 위한 viewHolder
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder{

    protected View mView;
    protected static Context mContext;

    public BaseViewHolder(Context context, View itemView) {
        super(itemView);
        mView = itemView;
        mContext = context;
    }

     public abstract  BaseViewHolder setLayout(Context context);
}