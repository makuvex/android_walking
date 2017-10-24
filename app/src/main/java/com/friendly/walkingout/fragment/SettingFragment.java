package com.friendly.walkingout.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.friendly.walkingout.R;
import com.friendly.walkingout.adapter.BaseSettingDataSetInterface;
import com.friendly.walkingout.adapter.BaseSettingViewHolderInterface;
import com.friendly.walkingout.adapter.SettingRecyclerAdapter;
import com.friendly.walkingout.dataSet.LoginSettingListData;
import com.friendly.walkingout.dataSet.SettingListData;
import com.friendly.walkingout.views.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {

    private static String[] contentTitle = {"로그인 정보", "알림 설정", "권한 설정", "위치 서비스", "생활패턴 인식", "버전 정보"};
    private static String[] contentTitleDesc = {"로그인 정보", "알림 받기", "", "위치 서비스", "생활패턴 인식", "버전 정보"};

    private RecyclerView mRecyclerView = null;

    public SettingFragment() {
    }

    public static SettingFragment newInstance(int sectionNumber) {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        LoginSettingListData data = new LoginSettingListData("makuvex7@gmail", true);
        list.add(data);

        SettingListData album = new SettingListData("타이틀 ", "설명2 ", R.mipmap.stroll);
        list.add(album);


//        for (int i =0; i<20; i ++){
//            SettingListData album = new SettingListData("타이틀 " + i, "설명 " + i, R.mipmap.stroll);
//            albumList.add(album);
//        }

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mRecyclerView.setAdapter(new SettingRecyclerAdapter(list));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

}
