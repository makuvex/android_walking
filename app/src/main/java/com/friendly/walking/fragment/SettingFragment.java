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
import com.friendly.walking.dataSet.WalkingSettingListData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.friendly.walking.views.DividerItemDecoration;
import com.friendly.walking.R;
import com.friendly.walking.dataSet.PermissionSettingListData;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

import static com.friendly.walking.adapter.SettingRecyclerAdapter.INDEX_DATA_LOGIN;
import static com.friendly.walking.adapter.SettingRecyclerAdapter.INDEX_DATA_NOTIFICATION;
import static com.friendly.walking.adapter.SettingRecyclerAdapter.INDEX_DATA_VERSION;

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
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_UPDATE_PROFILE);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_LOGIN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_LOGOUT);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_WITHDRAW);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_CHANGE_NOTIFICATION_YN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_CHANGE_GEO_NOTIFICATION_YN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_CHANGE_LOCATION_YN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_REFRESH_USER_DATA);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_CHANGE_WALKING_MY_LOCATION_YN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_CHANGE_WALKING_CHATTING_YN);


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JWLog.e("","action :"+intent.getAction());

                if(JWBroadCast.BROAD_CAST_UPDATE_PROFILE.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_REFRESH_USER_DATA.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_LOGIN.equals(intent.getAction())) {

                    String email = intent.getStringExtra("email");
                    boolean autoLogin = intent.getBooleanExtra("autoLogin", false);

                    if(TextUtils.isEmpty(email)) {
                        email = getString(R.string.login_guide);
                        autoLogin = false;
                    }
                    JWLog.e("autoLogin "+autoLogin+", getWalkingCoin "+PreferencePhoneShared.getWalkingCoin(mContext));
                    JWLog.e("getNotificationYn "+PreferencePhoneShared.getNotificationYn(mContext)+", getGeoNotificationYn "+PreferencePhoneShared.getGeoNotificationYn(mContext));
                    JWLog.e("getLocationYn "+PreferencePhoneShared.getLocationYn(mContext));

                    mAdapter.setDataWithIndex(SettingRecyclerAdapter.INDEX_DATA_LOGIN, new LoginSettingListData(email, PreferencePhoneShared.getNickName(mContext), autoLogin, PreferencePhoneShared.getWalkingCoin(mContext)));
                    mAdapter.setDataWithIndex(SettingRecyclerAdapter.INDEX_DATA_NOTIFICATION, new NotificationSettingListData(PreferencePhoneShared.getNotificationYn(mContext), PreferencePhoneShared.getGeoNotificationYn(mContext)));
                    mAdapter.setDataWithIndex(SettingRecyclerAdapter.INDEX_DATA_LOCATION, new LocationSettingListData(PreferencePhoneShared.getLocationYn(mContext), false));
                    mAdapter.setDataWithIndex(SettingRecyclerAdapter.INDEX_DATA_WALKING, new WalkingSettingListData(PreferencePhoneShared.getMyLocationAcceptedYn(mContext), PreferencePhoneShared.getChattingAcceptYn(mContext)));

                    mAdapter.notifyDataSetChanged();
                } else if(JWBroadCast.BROAD_CAST_LOGOUT.equals(intent.getAction()) || JWBroadCast.BROAD_CAST_WITHDRAW.equals(intent.getAction())) {

                    mAdapter.setDataWithIndex(SettingRecyclerAdapter.INDEX_DATA_LOGIN, new LoginSettingListData(getString(R.string.login_guide), "", false, 0));
                    mAdapter.setDataWithIndex(SettingRecyclerAdapter.INDEX_DATA_NOTIFICATION, new NotificationSettingListData(false, false));
                    mAdapter.setDataWithIndex(SettingRecyclerAdapter.INDEX_DATA_LOCATION, new LocationSettingListData(false, false));
                    mAdapter.setDataWithIndex(SettingRecyclerAdapter.INDEX_DATA_WALKING, new WalkingSettingListData(false, false));

                    mAdapter.notifyDataSetChanged();
                } else if(JWBroadCast.BROAD_CAST_CHANGE_NOTIFICATION_YN.equals(intent.getAction())) {
                    JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR));

                    String uid = PreferencePhoneShared.getUserUid(mContext);
                    final boolean result = intent.getBooleanExtra("value", false);
                    FireBaseNetworkManager.getInstance(getActivity()).updateNotificationYn(uid, result, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR));
                            if(result) {
                                PreferencePhoneShared.setNotificationYn(mContext, result);
                            }
                        }
                    });
                    if(result) {
                        FirebaseMessaging.getInstance().subscribeToTopic("news");
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
                    }

                } else if(JWBroadCast.BROAD_CAST_CHANGE_GEO_NOTIFICATION_YN.equals(intent.getAction())) {
                    JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR));

                    String uid = PreferencePhoneShared.getUserUid(mContext);
                    final boolean result = intent.getBooleanExtra("value", false);
                    FireBaseNetworkManager.getInstance(getActivity()).updateGeoNotificationYn(uid, result, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR));
                            if(result) {
                                PreferencePhoneShared.setGeoNotificationYn(mContext, result);
                            }
                        }
                    });

                } else if(JWBroadCast.BROAD_CAST_CHANGE_LOCATION_YN.equals(intent.getAction())) {
                    JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR));

                    String uid = PreferencePhoneShared.getUserUid(mContext);
                    final boolean result = intent.getBooleanExtra("value", false);
                    FireBaseNetworkManager.getInstance(getActivity()).updateLocationYn(uid, result, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR));
                            if(result) {
                                PreferencePhoneShared.setLocationYn(mContext, result);
                            }
                        }
                    });

                } else if(JWBroadCast.BROAD_CAST_CHANGE_WALKING_MY_LOCATION_YN.equals(intent.getAction())) {
                    JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR));

                    String uid = PreferencePhoneShared.getUserUid(mContext);
                    final boolean result = intent.getBooleanExtra("value", false);
                    FireBaseNetworkManager.getInstance(getActivity()).updateWalkingMyLocationYn(uid, result, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR));
                            if(result) {
                                PreferencePhoneShared.setMyLocationAcceptedYn(mContext, result);
                            }
                        }
                    });

                } else if(JWBroadCast.BROAD_CAST_CHANGE_WALKING_CHATTING_YN.equals(intent.getAction())) {
                    JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR));

                    String uid = PreferencePhoneShared.getUserUid(mContext);
                    final boolean result = intent.getBooleanExtra("value", false);
                    FireBaseNetworkManager.getInstance(getActivity()).updateWalkingChattingYn(uid, result, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR));
                            if(result) {
                                PreferencePhoneShared.setChattingAcceptYn(mContext, result);
                            }
                        }
                    });

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
        JWLog.e("");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        JWLog.e("");
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

        if(PreferencePhoneShared.getLoginYn(mContext)) {
            list.add(new LoginSettingListData(loginText, PreferencePhoneShared.getNickName(mContext), PreferencePhoneShared.getAutoLoginYn(getActivity()), PreferencePhoneShared.getWalkingCoin(mContext)));
            list.add(new NotificationSettingListData(PreferencePhoneShared.getNotificationYn(getActivity()), PreferencePhoneShared.getGeoNotificationYn(getActivity())));
            list.add(new PermissionSettingListData());
            list.add(new LocationSettingListData(PreferencePhoneShared.getLocationYn(getActivity()), false));
            list.add(new VersionInfoSettingListData("", ""));
            list.add(new WalkingSettingListData(PreferencePhoneShared.getMyLocationAcceptedYn(mContext), PreferencePhoneShared.getChattingAcceptYn(mContext)));
        } else {
            list.add(new LoginSettingListData(loginText, "", false, 0));
            list.add(new NotificationSettingListData(false, false));
            list.add(new PermissionSettingListData());
            list.add(new LocationSettingListData(false, false));
            list.add(new VersionInfoSettingListData("", ""));
            list.add(new WalkingSettingListData(false, false));
        }

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new SettingRecyclerAdapter(getActivity(), list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        FireBaseNetworkManager.getInstance(getActivity()).readVersionInfo(new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                if(result && object != null) {
                    PreferencePhoneShared.setVersionInfo(mContext, (String)object);

                    String latestVersion = PreferencePhoneShared.getVersionInfo(mContext);
                    String currentVersion = CommonUtil.getAppVersion(mContext);
                    JWLog.e("currentVersion :"+currentVersion+", latestVersion :"+latestVersion);

                    mAdapter.setDataWithIndex(INDEX_DATA_VERSION, new VersionInfoSettingListData(currentVersion, latestVersion));
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}
