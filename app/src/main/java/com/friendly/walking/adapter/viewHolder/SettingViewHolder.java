package com.friendly.walking.adapter.viewHolder;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.friendly.walking.R;
import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.dataSet.SettingListData;
import com.friendly.walking.util.JWLog;

/**
 * 뷰 재활용을 위한 viewHolder
 */
public class SettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface{

    public ImageButton imageButton;
    public TextView title;
    public TextView titleDesc;

    public SettingViewHolder(Context context, View itemView) {
        super(context, itemView);
        JWLog.e("", "");

        imageButton = (ImageButton) itemView.findViewById(R.id.img_button);
        title = (TextView) itemView.findViewById(R.id.textTitle);
        titleDesc = (TextView) itemView.findViewById(R.id.textDesc);
    }


    @Override
    public SettingViewHolder setLayout(Context context) {
        return new SettingViewHolder(context, mView);
    }

    @Override
    public void setDataSet(Object object) {
        if(object instanceof SettingListData) {
            SettingListData data = (SettingListData)object;

            this.title.setText(data.getTitle());
            this.titleDesc.setText(data.getTitleDesc());
            this.imageButton.setImageResource(data.getImageButtonResource());
            this.itemView.setTag(data);
        }
    }

    public void setTitleText(String title) {
        this.title.setText(title);
    }
}