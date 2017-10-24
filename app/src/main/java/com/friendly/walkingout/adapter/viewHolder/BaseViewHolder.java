package com.friendly.walkingout.adapter.viewHolder;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.friendly.walkingout.R;
import com.friendly.walkingout.adapter.BaseSettingViewHolderInterface;
import com.friendly.walkingout.dataSet.SettingListData;
import com.friendly.walkingout.util.JWLog;

/**
 * 뷰 재활용을 위한 viewHolder
 */
public abstract class BaseViewHolder extends RecyclerView.ViewHolder{

    protected View mView;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

     public abstract  BaseViewHolder setLayout();
}