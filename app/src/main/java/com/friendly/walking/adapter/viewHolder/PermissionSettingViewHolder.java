package com.friendly.walking.adapter.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.friendly.walking.R;
import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.dataSet.NotificationSettingListData;
import com.friendly.walking.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class PermissionSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton moreButton;

    private View.OnClickListener mOnClickListener;

    public PermissionSettingViewHolder(Context context, View itemView) {
        super(context, itemView);
        JWLog.e("", "");

        moreButton = (ImageButton) itemView.findViewById(R.id.permission_more);

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JWLog.e("","moreButton");
            }
        };

        moreButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public PermissionSettingViewHolder setLayout(Context context) {
        return new PermissionSettingViewHolder(context, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof NotificationSettingListData) {

        }
    }
}
