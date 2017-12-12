package com.friendly.walking.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.friendly.walking.adapter.SettingRecyclerAdapter;
import com.friendly.walking.adapter.baseInterface.BaseSettingDataSetInterface;
import com.friendly.walking.adapter.viewHolder.SettingViewHolder;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.LocationSettingListData;
import com.friendly.walking.dataSet.LoginSettingListData;
import com.friendly.walking.dataSet.NotificationSettingListData;
import com.friendly.walking.dataSet.VersionInfoSettingListData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.friendly.walking.views.DividerItemDecoration;
import com.friendly.walking.R;
import com.friendly.walking.dataSet.PermissionSettingListData;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {

    private static String[] contentTitle = {"로그인 정보", "알림 설정", "권한 설정", "위치 서비스", "생활패턴 인식", "버전 정보"};
    private static String[] contentTitleDesc = {"로그인 정보", "알림 받기", "", "위치 서비스", "생활패턴 인식", "버전 정보"};

    private RecyclerView                            mRecyclerView = null;
    private SettingRecyclerAdapter                  mAdapter = null;
    private Context                                 mContext;

    private BroadcastReceiver                       mReceiver = null;
    private IntentFilter                            mIntentFilter = null;
    private boolean                                 mIsRegisterdReceiver = false;

    public SettingFragment() {
    }

    public static SettingFragment newInstance(int sectionNumber) {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_LOGOUT);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JWLog.e("","action :"+intent.getAction());

                if(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI.equals(intent.getAction())) {
                    String email = intent.getStringExtra("email");
                    boolean autoLogin = intent.getBooleanExtra("autoLogin", false);

                    if(TextUtils.isEmpty(email)) {
                        email = getString(R.string.login_guide);
                        autoLogin = false;
                    }
                    mAdapter.setDataWithIndex(0, new LoginSettingListData(email, autoLogin));
                    mAdapter.notifyDataSetChanged();
                } else if(JWBroadCast.BROAD_CAST_LOGOUT.equals(intent.getAction())) {
                    mAdapter.setDataWithIndex(0, new LoginSettingListData(getString(R.string.login_guide), false));
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        mContext.registerReceiver(mReceiver, mIntentFilter);
        mIsRegisterdReceiver = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initLayout(view);
        initData();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mIsRegisterdReceiver == true) {
            mContext.unregisterReceiver(mReceiver);
            mIsRegisterdReceiver = false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initLayout(View view) {
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
    }

    private void initData() {

        List<BaseSettingDataSetInterface> list = new ArrayList<BaseSettingDataSetInterface>();
        String loginText = "";

        try {
            String key = PreferencePhoneShared.getUserUid(getActivity());
            String paddedKey = key.substring(0, 16);
            JWLog.e("","uid :" + key);
            String decEmail = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginID(getActivity()), paddedKey));
            loginText = decEmail;
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(!PreferencePhoneShared.getLoginYn(mContext)) {
            loginText = getString(R.string.login_guide);
        }

        list.add(new LoginSettingListData(loginText, true));
        list.add(new NotificationSettingListData(false, true));
        list.add(new PermissionSettingListData());
        list.add(new LocationSettingListData(false, true));
        list.add(new VersionInfoSettingListData("v1.0.0", "v1.0.1"));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new SettingRecyclerAdapter(getActivity(), list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

}
