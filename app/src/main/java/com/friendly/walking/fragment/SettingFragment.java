package com.friendly.walking.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.friendly.walking.adapter.SettingRecyclerAdapter;
import com.friendly.walking.adapter.baseInterface.BaseSettingDataSetInterface;
import com.friendly.walking.dataSet.LocationSettingListData;
import com.friendly.walking.dataSet.LoginSettingListData;
import com.friendly.walking.dataSet.NotificationSettingListData;
import com.friendly.walking.dataSet.VersionInfoSettingListData;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.views.DividerItemDecoration;
import com.friendly.walking.R;
import com.friendly.walking.dataSet.PermissionSettingListData;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {

    private static String[] contentTitle = {"로그인 정보", "알림 설정", "권한 설정", "위치 서비스", "생활패턴 인식", "버전 정보"};
    private static String[] contentTitleDesc = {"로그인 정보", "알림 받기", "", "위치 서비스", "생활패턴 인식", "버전 정보"};

    private RecyclerView    mRecyclerView = null;
    private Context         mContext;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initLayout(view);
        initData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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

        String loginText = PreferencePhoneShared.getLoginID(mContext);
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

        mRecyclerView.setAdapter(new SettingRecyclerAdapter(getActivity(), list));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}
