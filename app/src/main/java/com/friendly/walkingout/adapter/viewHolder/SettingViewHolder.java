package com.friendly.walkingout.adapter.viewHolder;

/**
 * Created by jungjiwon on 2017. 10. 23..
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.friendly.walkingout.R;
import com.friendly.walkingout.adapter.BaseSettingDataSetInterface;
import com.friendly.walkingout.adapter.BaseSettingViewHolderInterface;
import com.friendly.walkingout.dataSet.SettingListData;
import com.friendly.walkingout.util.JWLog;

/**
 * 뷰 재활용을 위한 viewHolder
 */
public class SettingViewHolder extends BaseViewHolder implements BaseSettingViewHolderInterface{

    public ImageButton imageButton;
    public TextView title;
    public TextView titleDesc;

    public SettingViewHolder(View itemView) {
        super(itemView);
        JWLog.e("", "");

        imageButton = (ImageButton) itemView.findViewById(R.id.img_button);
        title = (TextView) itemView.findViewById(R.id.textTitle);
        titleDesc = (TextView) itemView.findViewById(R.id.textDesc);
    }


    @Override
    public SettingViewHolder setLayout() {
        return new SettingViewHolder(mView);
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
}