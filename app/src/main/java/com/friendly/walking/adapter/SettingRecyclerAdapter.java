package com.friendly.walking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendly.walking.adapter.baseInterface.BaseSettingDataSetInterface;
import com.friendly.walking.adapter.baseInterface.BaseSettingViewHolderInterface;
import com.friendly.walking.adapter.viewHolder.LocationSettingViewHolder;
import com.friendly.walking.adapter.viewHolder.LoginSettingViewHolder;
import com.friendly.walking.adapter.viewHolder.NotificationSettingViewHolder;
import com.friendly.walking.adapter.viewHolder.PermissionSettingViewHolder;
import com.friendly.walking.adapter.viewHolder.VersionInfoSettingViewHolder;
import com.friendly.walking.dataSet.LocationSettingListData;
import com.friendly.walking.dataSet.LoginSettingListData;
import com.friendly.walking.util.JWLog;
import com.friendly.walking.R;

import java.util.HashMap;
import java.util.List;


public class SettingRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int INDEX_DATA_LOGIN                    = 0;
    public static final int INDEX_DATA_NOTIFICATION             = 1;
    public static final int INDEX_DATA_PERMISSION               = 2;
    public static final int INDEX_DATA_LOCATION                 = 3;
    public static final int INDEX_DATA_VERSION                  = 4;

    private HashMap<Integer, Integer> layoutMap;
    private HashMap<Integer, Class<?>> holderMap;
    private List<BaseSettingDataSetInterface> dataList;
    private int itemLayout;
    private Context mContext;

    /**
     * 생성자
     * @param items
     */
    public SettingRecyclerAdapter(Context context, List<BaseSettingDataSetInterface> items){

        mContext = context;
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
        layoutMap.put(2, R.layout.permission_setting_row);
        layoutMap.put(3, R.layout.location_setting_row);
        layoutMap.put(4, R.layout.version_info_setting_row);

//        holderMap.put(0, LoginSettingViewHolder.class);
//        holderMap.put(1, SettingViewHolder.class);
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
        if(viewType == INDEX_DATA_LOGIN) {
            return new LoginSettingViewHolder(mContext, view).setLayout(mContext);
        } else if(viewType == INDEX_DATA_NOTIFICATION) {
            return new NotificationSettingViewHolder(mContext, view).setLayout(mContext);
        } else if(viewType == INDEX_DATA_PERMISSION) {
            return new PermissionSettingViewHolder(mContext, view).setLayout(mContext);
        } else if(viewType == INDEX_DATA_LOCATION) {
            return new LocationSettingViewHolder(mContext, view).setLayout(mContext);
        } else if(viewType == INDEX_DATA_VERSION) {
            return new VersionInfoSettingViewHolder(mContext, view).setLayout(mContext);
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

    public void setDataWithIndex(int index, Object object) {
        if(index == INDEX_DATA_LOGIN) {
            LoginSettingListData data = (LoginSettingListData)dataList.get(index);
            data.setDataSet(object);
        }
    }

}

