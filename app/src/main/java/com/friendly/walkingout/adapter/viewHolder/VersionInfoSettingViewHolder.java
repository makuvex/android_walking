package com.friendly.walkingout.adapter.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.friendly.walkingout.R;
import com.friendly.walkingout.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walkingout.dataSet.NotificationSettingListData;
import com.friendly.walkingout.dataSet.VersionInfoSettingListData;
import com.friendly.walkingout.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class VersionInfoSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public TextView currentVersion;
    public TextView latestVersion;

    public VersionInfoSettingViewHolder(Context context, View itemView) {
        super(context, itemView);
        JWLog.e("", "");

        currentVersion = (TextView) itemView.findViewById(R.id.current_version);
        latestVersion = (TextView) itemView.findViewById(R.id.latest_version);
    }

    @Override
    public VersionInfoSettingViewHolder setLayout(Context context) {
        return new VersionInfoSettingViewHolder(context, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof VersionInfoSettingListData) {
            VersionInfoSettingListData data = (VersionInfoSettingListData)object;

            this.currentVersion.setText(data.getCurrentVersion());
            this.latestVersion.setText(data.getLatestVersion());
        }
    }
}
