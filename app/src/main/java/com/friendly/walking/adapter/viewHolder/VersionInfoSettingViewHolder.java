package com.friendly.walking.adapter.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.R;
import com.friendly.walking.dataSet.VersionInfoSettingListData;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class VersionInfoSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public TextView currentVersion;
    public TextView latestVersion;
    public Button updateButton;

    public VersionInfoSettingViewHolder(Activity activity, View itemView) {
        super(activity, itemView);
        JWLog.e("", "");

        currentVersion = (TextView) itemView.findViewById(R.id.current_version);
        latestVersion = (TextView) itemView.findViewById(R.id.latest_version);
        updateButton = (Button) itemView.findViewById(R.id.update_button);


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JWLog.e("업데이트");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" +  mActivity.getPackageName()));
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public VersionInfoSettingViewHolder setLayout(Activity activity) {
        return new VersionInfoSettingViewHolder(activity, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof VersionInfoSettingListData) {
            VersionInfoSettingListData data = (VersionInfoSettingListData)object;

            this.currentVersion.setText("v"+data.getCurrentVersion());
            this.latestVersion.setText("v"+data.getLatestVersion());
        }
    }
}
