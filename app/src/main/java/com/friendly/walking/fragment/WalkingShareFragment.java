package com.friendly.walking.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.friendly.walking.ApplicationPool;
import com.friendly.walking.dataSet.StrollTimeData;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.main.DataExchangeInterface;
import com.friendly.walking.main.MainActivity;
import com.friendly.walking.service.MainService;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.friendly.walking.main.DataExchangeInterface.CommandType.READ_LOCATION_INFO;
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
    private LatLng                                  mLocation;


    //private DemoUtils demoUtils = new DemoUtils();


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

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_RESPONSE_LOCATION);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JWLog.e("","action :"+intent.getAction());

                if(JWBroadCast.BROAD_CAST_RESPONSE_LOCATION.equals(intent.getAction())) {
                    ApplicationPool pool = (ApplicationPool) mContext.getApplicationContext();
                    mLocation = (LatLng) pool.getExtra(MainService.KEY_LOCATION_DATA, intent);
                    JWLog.e("mLocation "+mLocation);
                    JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR));

                    if(mAdapter != null) {
                        mAdapter.updateLocation(mLocation);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
        registerReceiverMain();
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
        unregisterReceiverMain();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onRefresh() {
        JWLog.e("@@@@@@@@@@@@@@@ onRefresh @@@@@@@@@@@@@@@");
        if(!PreferencePhoneShared.getLoginYn(getActivity())) {
            JWToast.showToast(R.string.need_login);
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR));
        JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_REQUEST_LOCATION));

        requestCurrentWalkingList();
    }

    private void requestCurrentWalkingList() {
        if (!GoogleMapActivity.checkLocationServicesStatus(getActivity())) {
            JWLog.e("startLocationUpdates : call showDialogForLocationServiceSetting");
            GoogleMapActivity.showDialogForLocationServiceSetting(getActivity(), "위치 정보를 활성화 시키면 나와의 거리가 표시됩니다. 위치 설정을 수정 하실래요?");
        }
        FireBaseNetworkManager.getInstance(getActivity()).readCurrentWalkingList(new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR));
                JWLog.e("result :"+result+", object :"+object);
                mCurrentWalkingList = (ArrayList<WalkingData>) object;
                if(mCurrentWalkingList == null || mCurrentWalkingList.size() == 0) {
                    mNoPersonView.setVisibility(View.VISIBLE);
                } else {
                    mNoPersonView.setVisibility(View.GONE);
                }
                if(result) {
                    updateWalkingList(mCurrentWalkingList.size());
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void registerReceiverMain() {
        if(mIsRegisterdReceiver != true) {
            mContext.registerReceiver(mReceiver, mIntentFilter);
            mIsRegisterdReceiver = true;
        }
    }

    private void unregisterReceiverMain() {
        if(mIsRegisterdReceiver == true) {
            mContext.unregisterReceiver(mReceiver);
            mIsRegisterdReceiver = false;
        }
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
/*
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
*/
    }

    private void updateWalkingList(int count) {
        //demoUtils.currentOffset = 0;

        List<WalkingShareItem> items = new ArrayList<>();
        String key = PreferencePhoneShared.getUserUid(mContext);
        String paddedKey = key.substring(0, 16);
        String decEmail = null;
        try {
            decEmail = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginID(mContext), paddedKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < count; i++) {
            int colSpan = Math.random() < 0.2f ? 2 : 1;
            // Swap the next 2 lines to have items with variable
            // column/row span.
            //int rowSpan = Math.random() < 0.2f ? 2 : 1;
            int rowSpan = colSpan;
            WalkingShareItem item = new WalkingShareItem(colSpan, rowSpan, i, mCurrentWalkingList.get(i).mem_nickname, mCurrentWalkingList.get(i).mem_email);
            item.setLocation(mCurrentWalkingList.get(i).lat, mCurrentWalkingList.get(i).lot);


            if(decEmail != null && !item.getEmail().equals(decEmail)) {
                items.add(item);
            }
        }

        if(items.size() == 0) {
            mNoPersonView.setVisibility(View.VISIBLE);
        } else {
            mNoPersonView.setVisibility(View.GONE);
            mAdapter.setItems(items);
            if (mLocation != null) {
                mAdapter.updateLocation(mLocation);
            }
        }
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
        JWLog.e("item "+item);

        if(PermissionManager.isAcceptedLocationPermission(mContext)) {
            Intent intent = new Intent(mContext, GoogleMapActivity.class);
            double lat = item.getLocation().getLatitude();
            double lot = item.getLocation().getLongitude();

            if(lat == 0 || lot == 0) {
                JWToast.showToastLong("위치 정보가 없어 맵에 표시 할 수 없습니다.");
                return;
            }
            intent.putExtra("lat", String.format("%s",lat));
            intent.putExtra("lot", String.format("%s",lot));
            intent.putExtra("title", "산책 위치");
            intent.putExtra("user", item.getNickName());

            startActivity(intent);
        } else {
            PermissionManager.requestLocationPermission(getActivity());
        }
    }

    @Override
    public void functionByCommand(Object obj, CommandType type) {

        if(PermissionManager.isAcceptedLocationPermission(getActivity())) {
            if(type == READ_WALKING_TIME_LIST) {
                requestCurrentWalkingList();
            }
            if(type == READ_LOCATION_INFO) {
                JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR));
                JWBroadCast.sendBroadcast(mContext, new Intent(JWBroadCast.BROAD_CAST_REQUEST_LOCATION));
            }
        } else {
            if(type == READ_LOCATION_INFO) {
                CommonUtil.alertDialogShow(getActivity(), "위치", "권한이 허용되지 않았습니다.\n주위에 산책 중인 사람들을 보려면 권한이 필요합니다. 허용 하시겠습니까?", new CommonUtil.CompleteCallback() {
                    @Override
                    public void onCompleted(boolean result, Object object) {
                        if (result) {
                            PermissionManager.requestLocationPermission(getActivity());
                        }
                    }
                });
            }
        }

    }
}
