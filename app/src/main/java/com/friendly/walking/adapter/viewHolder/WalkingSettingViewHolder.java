package com.friendly.walking.adapter.viewHolder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.R;
import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.NotificationSettingListData;
import com.friendly.walking.dataSet.WalkingSettingListData;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.JWLog;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

public class WalkingSettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface {

    public ImageButton acceptMyLocationButton;
    public ImageButton acceptChattingButton;

    public boolean isMyLocationAccepted;
    public boolean isChattingAcepted;

    private View.OnClickListener mOnClickListener;

    public WalkingSettingViewHolder(Activity activity, View itemView) {
        super(activity, itemView);
        JWLog.e("", "");

        acceptMyLocationButton = (ImageButton) itemView.findViewById(R.id.accept_my_location);
        acceptChattingButton = (ImageButton) itemView.findViewById(R.id.accept_chatting);

        acceptMyLocationButton.setSelected(PreferencePhoneShared.getMyLocationAcceptedYn(mActivity));
        acceptChattingButton.setSelected(PreferencePhoneShared.getChattingAcceptYn(mActivity));

        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginYn = PreferencePhoneShared.getLoginYn(mActivity);
                if(loginYn) {
                    v.setSelected(!v.isSelected());

                    if(v == acceptMyLocationButton) {
                        Intent i = new Intent(JWBroadCast.BROAD_CAST_CHANGE_WALKING_MY_LOCATION_YN);
                        i.putExtra("value", v.isSelected());
                        JWBroadCast.sendBroadcast(mActivity, i);
                    } else if(v == acceptChattingButton) {
                        Intent i = new Intent(JWBroadCast.BROAD_CAST_CHANGE_WALKING_CHATTING_YN);
                        i.putExtra("value", v.isSelected());
                        JWBroadCast.sendBroadcast(mActivity, i);
                    }
                } else {
                    JWToast.showToast("로그인 후 설정 가능합니다.");
                }
            }
        };

        acceptMyLocationButton.setOnClickListener(mOnClickListener);
        acceptChattingButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public WalkingSettingViewHolder setLayout(Activity activity) {
        return new WalkingSettingViewHolder(activity, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof WalkingSettingListData) {
            WalkingSettingListData data = (WalkingSettingListData)object;

            this.isMyLocationAccepted = data.getMyLocationAcceptedState();
            this.isChattingAcepted = data.getChattingAccepted();

            acceptMyLocationButton.setSelected(isMyLocationAccepted);
            acceptChattingButton.setSelected(isChattingAcepted);

            this.itemView.setTag(data);
        }
    }
}
