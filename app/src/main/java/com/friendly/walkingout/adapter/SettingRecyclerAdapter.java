package com.friendly.walkingout.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendly.walkingout.R;
import com.friendly.walkingout.adapter.viewHolder.BaseViewHolder;
import com.friendly.walkingout.adapter.viewHolder.LoginSettingViewHolder;
import com.friendly.walkingout.adapter.viewHolder.NotificationSettingViewHolder;
import com.friendly.walkingout.adapter.viewHolder.SettingViewHolder;
import com.friendly.walkingout.util.JWLog;

import java.util.HashMap;
import java.util.List;


public class SettingRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private HashMap<Integer, Integer> layoutMap;
    private HashMap<Integer, Class<?>> holderMap;
    private List<BaseSettingDataSetInterface> dataList;
    private int itemLayout;

    /**
     * 생성자
     * @param items
     */
    public SettingRecyclerAdapter(List<BaseSettingDataSetInterface> items){

        this.dataList = items;
        if(layoutMap == null) {
            layoutMap = new HashMap<Integer, Integer>();
        }
        if(holderMap == null) {
            holderMap = new HashMap<Integer, Class<?>>();
        }

        layoutMap.put(0, R.layout.login_setting_row);
        //layoutMap.put(1, R.layout.setting_recycle_row);
        layoutMap.put(1, R.layout.notification_setting_row);

        holderMap.put(0, LoginSettingViewHolder.class);
        holderMap.put(1, SettingViewHolder.class);
    }

    /**
     * 레이아웃을 만들어서 Holer에 저장
     * @param viewGroup
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        JWLog.e("","");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutMap.get(viewType), viewGroup,false);
        if(viewType == 0) {
            return new LoginSettingViewHolder(view).setLayout();
        } else if(viewType == 1) {
            return new NotificationSettingViewHolder(view).setLayout();
        } else {
            return null;
        }
    }

    /**
     * listView getView 를 대체
     * 넘겨 받은 데이터를 화면에 출력하는 역할
     *
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        JWLog.e("","");

        if(viewHolder instanceof BaseSettingViewHolderInterface) {
            BaseSettingViewHolderInterface holder = (BaseSettingViewHolderInterface)viewHolder;

            BaseSettingDataSetInterface item = dataList.get(position);
            holder.setDataSet(item.getDataSet());
        }

    }

    @Override
    public int getItemCount() {
        JWLog.e("","");
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}

