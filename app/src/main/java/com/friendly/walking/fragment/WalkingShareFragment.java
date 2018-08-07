package com.friendly.walking.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.friendly.walking.main.DataExchangeInterface;
import com.friendly.walking.util.JWToast;

import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.friendly.walking.R;
import com.friendly.walking.activity.GoogleMapActivity;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.WalkingData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.permission.PermissionManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.DefaultListAdapter;
import com.friendly.walking.util.DemoAdapter;
import com.friendly.walking.util.DemoItem;
import com.friendly.walking.util.DemoUtils;
import com.friendly.walking.util.JWLog;
import com.friendly.walking.util.WalkingShareItem;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.friendly.walking.main.DataExchangeInterface.CommandType.READ_WALKING_TIME_LIST;


public class WalkingShareFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, DataExchangeInterface {

    private Context                                 mContext;


    private BroadcastReceiver                       mReceiver = null;
    private IntentFilter                            mIntentFilter = null;
    private boolean                                 mIsRegisterdReceiver = false;
    private ArrayList<WalkingData>                  mCurrentWalkingList = null;

    private AsymmetricGridView                      mGridView;
    private DefaultListAdapter                      mAdapter;
    private TextView                                mNoPersonView;
    private SwipeRefreshLayout                      mSwipeRefreshLayout;

    private DemoUtils demoUtils = new DemoUtils();


    public WalkingShareFragment() {
    }

    public static WalkingShareFragment newInstance(int sectionNumber) {
        WalkingShareFragment fragment = new WalkingShareFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mCurrentWalkingList = new ArrayList<>();

        /*
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_LOGOUT);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_CHANGE_NOTIFICATION_YN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_CHANGE_GEO_NOTIFICATION_YN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_CHANGE_LOCATION_YN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_REFRESH_USER_DATA);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JWLog.e("","action :"+intent.getAction());

                if(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_REFRESH_USER_DATA.equals(intent.getAction())) {

                    String email = intent.getStringExtra("email");
                    boolean autoLogin = intent.getBooleanExtra("autoLogin", false);

                    if(TextUtils.isEmpty(email)) {
                        email = getString(R.string.login_guide);
                        autoLogin = false;
                    }
                    mAdapter.setDataWithIndex(0, new LoginSettingListData(email, PreferencePhoneShared.getNickName(mContext), autoLogin, PreferencePhoneShared.getWalkingCoin(mContext)));
                    mAdapter.notifyDataSetChanged();
                } else if(JWBroadCast.BROAD_CAST_LOGOUT.equals(intent.getAction())) {
                    mAdapter.setDataWithIndex(0, new LoginSettingListData(getString(R.string.login_guide), "", false, 0));
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
                    FireBaseNetworkManager.getInstance(getActivity()).updateLocationYn(uid, result, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR));
                            if(result) {
                                PreferencePhoneShared.setLocationYn(mContext, result);
                            }
                        }
                    });

                } else if(JWBroadCast.BROAD_CAST_CHANGE_WALKING_CHATTING_YN.equals(intent.getAction())) {
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

                }
            }
        };

        mContext.registerReceiver(mReceiver, mIntentFilter);
        mIsRegisterdReceiver = true;
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        initLayout(view);

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

    @Override
    public void onRefresh() {
        JWLog.e("@@@@@@@@@@@@@@@ onRefresh @@@@@@@@@@@@@@@");

        FireBaseNetworkManager.getInstance(getActivity()).readCurrentWalkingList(new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR));
                JWLog.e("result :"+result+", object :"+object);
                mCurrentWalkingList = (ArrayList<WalkingData>) object;

                if(result) {
                    updateWalkingList(mCurrentWalkingList.size());
                }
                if(mCurrentWalkingList == null || mCurrentWalkingList.size() == 0) {
                    mNoPersonView.setVisibility(View.VISIBLE);
                } else {
                    mNoPersonView.setVisibility(View.GONE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initLayout(View view) {
        mGridView = (AsymmetricGridView) view.findViewById(R.id.gridview);
        mNoPersonView = (TextView)view.findViewById(R.id.no_person);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);

        mAdapter = new DefaultListAdapter(getActivity());

        mGridView.setRequestedColumnCount(3);
        mGridView.setRequestedHorizontalSpacing(Utils.dpToPx(mContext, 3));
        mGridView.setAdapter(getNewAdapter());
        mGridView.setAllowReordering(true);

        //mGridView.setDebugging(true);

        mGridView.setOnItemClickListener(this);

        JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR));

        FireBaseNetworkManager.getInstance(getActivity()).readCurrentWalkingList(new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR));
                JWLog.e("result :"+result+", object :"+object);
                mCurrentWalkingList = (ArrayList<WalkingData>) object;

                if(result) {
                    updateWalkingList(mCurrentWalkingList.size());
                }
                if(mCurrentWalkingList == null || mCurrentWalkingList.size() == 0) {
                    mNoPersonView.setVisibility(View.VISIBLE);
                } else {
                    mNoPersonView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateWalkingList(int count) {
        demoUtils.currentOffset = 0;

        List<WalkingShareItem> items = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int colSpan = Math.random() < 0.2f ? 2 : 1;
            // Swap the next 2 lines to have items with variable
            // column/row span.
            //int rowSpan = Math.random() < 0.2f ? 2 : 1;
            int rowSpan = colSpan;
            WalkingShareItem item = new WalkingShareItem(colSpan, rowSpan, i, mCurrentWalkingList.get(i).mem_nickname, mCurrentWalkingList.get(i).mem_email);
            item.setLocation(mCurrentWalkingList.get(i).lat, mCurrentWalkingList.get(i).lot);

            items.add(item);
        }


        mAdapter.setItems(items);
    }

    private AsymmetricGridViewAdapter getNewAdapter() {
        return new AsymmetricGridViewAdapter(mContext, mGridView, mAdapter);
    }

    private void setNumColumns(int numColumns) {
        mGridView.setRequestedColumnCount(numColumns);
        mGridView.determineColumns();
        mGridView.setAdapter(getNewAdapter());
    }

    private void setColumnWidth(int columnWidth) {
        mGridView.setRequestedColumnWidth(Utils.dpToPx(mContext, columnWidth));
        mGridView.determineColumns();
        mGridView.setAdapter(getNewAdapter());
    }

    @Override
    public void onItemClick(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
        JWToast.showToast("Item " + position + " clicked");


        WalkingShareItem item = mAdapter.getItem(position);
        if(PermissionManager.isAcceptedLocationPermission(mContext)) {
            Intent intent = new Intent(mContext, GoogleMapActivity.class);
            intent.putExtra("lat", String.format("%s",item.getLocation().getLatitude()));
            intent.putExtra("lot", String.format("%s",item.getLocation().getLongitude()));
            intent.putExtra("title", "산책 위치");
            intent.putExtra("user", item.getNickName());

            startActivity(intent);
        } else {
            PermissionManager.requestLocationPermission(getActivity());
        }
    }

    @Override
    public void functionByCommand(String email, CommandType type) {
        if(type == READ_WALKING_TIME_LIST) {
            onRefresh();
        }
    }
}
