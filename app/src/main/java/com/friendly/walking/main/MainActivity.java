package com.friendly.walking.main;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.facebook.FacebookSdk;
import com.friendly.walking.ApplicationPool;
import com.friendly.walking.BuildConfig;
import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.R;
import com.friendly.walking.activity.BaseActivity;
import com.friendly.walking.activity.KakaoSignupActivity;
import com.friendly.walking.activity.LoginActivity;
import com.friendly.walking.activity.SignUpPetActivity;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.fragment.ReportFragment;
import com.friendly.walking.fragment.SettingFragment;
import com.friendly.walking.fragment.WalkingChartFragment;
import com.friendly.walking.fragment.WalkingShareFragment;
import com.friendly.walking.network.KakaoLoginManager;
import com.friendly.walking.permission.PermissionManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.receiver.LoginOutReceiver;
import com.friendly.walking.service.MainService;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;
import com.friendly.walking.util.JWToast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.usermgmt.response.model.UserProfile;

import static com.friendly.walking.firabaseManager.FireBaseNetworkManager.RC_GOOGLE_SIGN_IN;

//import com.friendly.walking.geofence.GeofenceManager;

public class MainActivity extends BaseActivity {

    public static final int                         PAGER_MAX_COUNT = 4;
    private SectionsPagerAdapter                    mSectionsPagerAdapter;

    private ViewPager                               mViewPager = null;

    private View                                    mChartSelected = null;
    private View                                    mShareSelected = null;
    private View                                    mReportSelected = null;
    private View                                    mSettingSelected = null;
    private View                                    mPreviousSelectedView = null;
    private View                                    mLayoutFab = null;


    private long                                    mDoublePressInterval = 2000;
    private long                                    mPreviousTouchTime = 0;
    private int                                     mCurrentTapPosition = 0;

    private MainActivity                            thisActivity;

    private BroadcastReceiver                       mReceiver = null;
    private IntentFilter                            mIntentFilter = null;
    private boolean                                 mIsRegisterdReceiver = false;

    private WalkingChartFragment                    mWalkingChartFragment;
    private WalkingShareFragment                    mWalkingShareFragment;
    private ReportFragment                          mReportFragment;
    private SettingFragment                         mSettingFragment;

    private UserData                                mUserDta;
    private DataExchangeInterface                   mCurrentFragmentInterface;

    private AdView                                  mAdView;
    private MainService                             mMainService;

    private ServiceConnection                       mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MainService.LocalBinder binder = (MainService.LocalBinder)iBinder;
            mMainService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMainService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        JWLog.e("");

        thisActivity = this;
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //GoogleApiAvailability.makeGooglePlayServicesAvailable();

        mChartSelected = findViewById(R.id.chart_page);
        mShareSelected = findViewById(R.id.share_page);
        mReportSelected = findViewById(R.id.report_page);
        mSettingSelected = findViewById(R.id.setting_page);
        mLayoutFab = findViewById(R.id.layout_fab);

        mPreviousSelectedView = mChartSelected;

        mChartSelected.setBackgroundResource(R.color.colorPrimaryLighten);

        //mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                JWLog.e("@@@","@@@ onPageSelected position : "+position);

                if(mPreviousSelectedView != null) {
                    mPreviousSelectedView.setBackgroundResource(R.color.colorTapUnselected);
                }
                mCurrentTapPosition = position;

                int selectedTapColor = R.color.colorPrimaryLighten;
                switch(position) {
                    case 0 :
                        //showProfileView(true);
                        mChartSelected.setBackgroundResource(selectedTapColor);
                        mPreviousSelectedView = mChartSelected;
                        mCurrentFragmentInterface = mWalkingChartFragment;
                        break;
                    case 1 :
                        //showProfileView(false);
                        mShareSelected.setBackgroundResource(selectedTapColor);
                        mPreviousSelectedView = mShareSelected;
                        mCurrentFragmentInterface = mWalkingShareFragment;
                        break;
                    case 2 :
                        //showProfileView(false);
                        mReportSelected.setBackgroundResource(selectedTapColor);
                        mPreviousSelectedView = mReportSelected;
                        mCurrentFragmentInterface = mReportFragment;
                        break;
                    case 3 :
                        //showProfileView(false);
                        mSettingSelected.setBackgroundResource(selectedTapColor);
                        mPreviousSelectedView = mSettingSelected;
                        mCurrentFragmentInterface = null;
                        break;

                    default:
                        break;
                }

                functionByCommand(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        MobileAds.initialize(this, "ca-app-pub-5000421881432235~3347969295");
        mAdView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                JWLog.e("");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                JWLog.e("");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                JWLog.e("");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                JWLog.e("");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                JWLog.e("");
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();


//                startActivity(new Intent(MainActivity.this, PieChartActivity.class));

//                startActivity(new Intent(MainActivity.this, BarChartActivity.class));

                if(!PermissionManager.isAcceptedLocationPermission(getApplicationContext())) {
                    PermissionManager.requestLocationPermission(MainActivity.this);
                    return;
                }
                //if(BuildConfig.IS_DEBUG) {

                    boolean isWalking = PreferencePhoneShared.getIsWalking(getApplicationContext());
                    String msg = JWBroadCast.BROAD_CAST_START_MANUAL_WALKING;
                    int transition = Geofence.GEOFENCE_TRANSITION_EXIT;
                    if (isWalking) {
                        msg = JWBroadCast.BROAD_CAST_STOP_MANUAL_WALKING;
                        transition = Geofence.GEOFENCE_TRANSITION_ENTER;
                    }

                    PreferencePhoneShared.setIsWalking(getApplicationContext(), !isWalking);
                    Intent i = new Intent(msg);
                    i.putExtra("transition", transition);
                    JWBroadCast.sendBroadcast(getApplicationContext(), i);

                    JWToast.showToast("산책 모드 "+(!isWalking ? "시작" : "중지"));
                //}
            }
        });

        if(PreferencePhoneShared.getLoginYn(thisActivity)) {
            mLayoutFab.setVisibility(View.VISIBLE);
        }


//        CircleImageView imageview = (CircleImageView)findViewById(R.id.profileImageView);
//
//        BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
//        Bitmap bitmap = drawable.getBitmap();
//
//        Bitmap blurred = CommonUtil.blurRenderScript(this, bitmap, 25);//second parametre is radius
//        //imageview.setImageBitmap(blurred);
//        mProfileView.setBackground(new BitmapDrawable(blurred));

        initReceiver();
        registerReceiverMain();

        Intent intent = new Intent(MainActivity.this, MainService.class);
        //bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);

        PreferencePhoneShared.setLoginYn(this, false);
        if(!doLogin()) {
            JWBroadCast.sendBroadcast(MainActivity.this, new Intent(JWBroadCast.BROAD_CAST_REMOVE_GEOFENCE));
        }

        String fcmToken = PreferencePhoneShared.getFCMToken(this);
        JWLog.e("FCM TOKEN :"+fcmToken);


    }

    @Override
    public void onBackPressed() {
        if(mCurrentTapPosition != 0) {
            mViewPager.setCurrentItem(0, true);
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (0 >= mPreviousTouchTime || mDoublePressInterval < (currentTime - mPreviousTouchTime)) {
            JWToast.showToast("'뒤로' 버튼을 한번 더 누르시면 종료 됩니다.");
            mPreviousTouchTime = currentTime;
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //registerReceiverMain();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregisterReceiverMain();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiverMain();
//        if(mMainService != null) {
//            unbindService(mServiceConnection);
//        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int currentPosition = 0;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            JWLog.e("","@@@ getItem position :"+position);
            currentPosition = position;
            if(position == 0) {
                if(mWalkingChartFragment == null) {
                    mWalkingChartFragment = WalkingChartFragment.newInstance(position);
                }
                mCurrentFragmentInterface = mWalkingChartFragment;
                return mWalkingChartFragment;
            } else if(position == 1) {
                if(mWalkingShareFragment == null) {
                    mWalkingShareFragment = WalkingShareFragment.newInstance(position);
                }
                //mWalkingShareFragment.selectedThisFragment();
                return mWalkingShareFragment;
            } else if(position == 2) {
                if(mReportFragment == null) {
                    mReportFragment = ReportFragment.newInstance(position);
                }
                return mReportFragment;
            } else if(position == 3) {
                if(mSettingFragment == null) {
                    mSettingFragment = SettingFragment.newInstance(position);
                }
                return mSettingFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        public int getCurrentPosition() {
            return currentPosition;
        }
    }

    public void tapClicked(View button) {
        if(button.getId() == R.id.stroll) {
            mViewPager.setCurrentItem(0, true);
        } else if(button.getId() == R.id.map) {
            mViewPager.setCurrentItem(1, true);
        } else if(button.getId() == R.id.report) {
            mViewPager.setCurrentItem(2, true);
        } else if(button.getId() == R.id.setting) {
            mViewPager.setCurrentItem(3, true);
        }
    }

//    public void showProfileView(boolean show) {
//        mProfileView.setVisibility(show ? View.VISIBLE : View.GONE);
//    }

    private boolean doLogin() {

        try {
            boolean autoLogin = PreferencePhoneShared.getAutoLoginYn(this);
            if(!autoLogin) {

                return false;
            }
            int autoLoginType = PreferencePhoneShared.getAutoLoginType(this);

            if(autoLoginType == GlobalConstantID.LOGIN_TYPE_EMAIL) {
                String key = PreferencePhoneShared.getUserUid(this);
                String paddedKey = key.substring(0, 16);

                if (TextUtils.isEmpty(key) || !autoLogin) {
                    return false;
                }

                JWLog.e("", "uid :" + paddedKey);
                String decEmail = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginID(this), paddedKey));
                String decPassword = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginPassword(this), paddedKey));

                JWLog.e("email :"+decEmail+", password :"+decPassword);
                if(TextUtils.isEmpty(decEmail) || TextUtils.isEmpty(decPassword)) {
                    return false;
                }

                setProgressBar(View.VISIBLE);
                Intent intent = new Intent();
                intent.putExtra("email", decEmail);
                intent.putExtra("password", decPassword);
                intent.setAction(JWBroadCast.BROAD_CAST_LOGIN);
                intent.setPackage(getPackageName());
                //intent.setComponent(new ComponentName(getApplicationContext(), LoginOutReceiver.class));
                //intent.setComponent(new ComponentName(getApplicationContext(), SettingFragment.class));

                //intent.putExtra("autoLogin", autoLogin);

                JWBroadCast.sendBroadcast(getApplicationContext(), intent);
            } else if(autoLoginType == GlobalConstantID.LOGIN_TYPE_GOOGLE) {
                FireBaseNetworkManager.getInstance(this).googleSignIn(this);
            } else if(autoLoginType == GlobalConstantID.LOGIN_TYPE_FACEBOOK) {
                JWLog.e("FirebaseAuth current User "+FirebaseAuth.getInstance().getCurrentUser());

                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    //String loginId = PreferencePhoneShared.getLoginID(thisActivity);
                    String key = PreferencePhoneShared.getUserUid(thisActivity);
                    String paddedKey = key.substring(0, 16);
                    JWLog.e("","uid :" + key);
                    String decEmail = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginID(thisActivity), paddedKey));

                    PreferencePhoneShared.setLoginYn(getApplicationContext(), true);
                    FireBaseNetworkManager.getInstance(thisActivity).facebookUpdateUI(decEmail);
                }
                /*
                FireBaseNetworkManager.getInstance(thisActivity).initFaceBookCallbackManager(new FireBaseNetworkManager.FireBaseNetworkCallback() {

                    @Override
                    public void onCompleted(boolean result, Object object) {
                        setProgressBar(View.INVISIBLE);
                        JWLog.e("result "+result);
                        if (result) {
                            if (object instanceof Task<?>) {
                                try {
                                    final Task<AuthResult> task = (Task<AuthResult>) object;

                                    JWLog.e("", "email :" + task.getResult().getUser().getEmail() + ", uid :" + task.getResult().getUser().getUid());
                                    JWLog.e("", "" + task.getResult().getUser().getDisplayName() + ", " + task.getResult().getUser().getPhoneNumber() + ", " + task.getResult().getUser().getDisplayName());

                                    String id = task.getResult().getUser().getEmail();
                                    if(TextUtils.isEmpty(id)) {
                                        id = task.getResult().getUser().getDisplayName();
                                    }
                                    final String displayName = id;

                                    JWLog.e("displayName :"+displayName+", email :"+task.getResult().getUser().getEmail());


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
*/
            } else if(autoLoginType == GlobalConstantID.LOGIN_TYPE_KAKAO) {
                if(KakaoLoginManager.getInstance(this).hasKakaoLoginSession()) {
                    KakaoLoginManager.getInstance(this).requestMe(new KakaoLoginManager.KakaoLoginManagerCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            if(result) {
                                final UserProfile profile = (UserProfile)object;
                                FireBaseNetworkManager.getInstance(MainActivity.this).findUserEmail(profile.getEmail(), new FireBaseNetworkManager.FireBaseNetworkCallback() {
                                    @Override
                                    public void onCompleted(boolean result, Object object) {
                                        if(!result) {
                                            KakaoSignupActivity.startSignUpPet(MainActivity.this, (UserProfile)object);
                                        } else {
                                            PreferencePhoneShared.setLoginYn(getApplicationContext(), true);
                                            Intent intent = new Intent(JWBroadCast.BROAD_CAST_KAKAO_LOGIN);
                                            intent.putExtra("email", profile.getEmail());

                                            JWBroadCast.sendBroadcast(MainActivity.this, intent);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            setProgressBar(View.INVISIBLE);
        }
        return false;
    }

    private void initReceiver() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_FACEBOOK_LOGIN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_GOOGLE_LOGIN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_KAKAO_LOGIN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_LOGOUT);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_EMAIL_LOGIN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_REFRESH_USER_DATA);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                JWLog.e("","action :"+intent.getAction());

                if(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI.equals(intent.getAction())) {
                    setProgressBar(View.INVISIBLE);
                } else if(JWBroadCast.BROAD_CAST_FACEBOOK_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_GOOGLE_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_KAKAO_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_EMAIL_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_REFRESH_USER_DATA.equals(intent.getAction())) {

                    JWLog.e("@@@ intent.getAction() :"+intent.getAction());
                    /*
                    if(!JWBroadCast.BROAD_CAST_REFRESH_USER_DATA.equals(intent.getAction()) && mVisibleState == View.VISIBLE) {
                        JWLog.e("이미 처리됨");
                        return;
                    }
                    */
                    final String email = intent.getStringExtra("email");
                    setProgressBar(View.VISIBLE);

                    mLayoutFab.setVisibility(View.VISIBLE);
                    FirebaseMessaging.getInstance().subscribeToTopic("news");
                    //readPetProfileImage(email);

                    FireBaseNetworkManager.getInstance(MainActivity.this).readUserData(email, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            JWLog.e("", "result :"+result);
                            setProgressBar(View.INVISIBLE);
                            if(!result) {
                                JWLog.e("email "+email);
                                return;
                            }

                            UserData userData = (UserData) object;
                            mUserDta = userData;

                            //JWLog.e("userData :"+userData);

                            if(!TextUtils.isEmpty(userData.mem_email)) {
                                FireBaseNetworkManager.getInstance(MainActivity.this).updateLastLoginTime(userData.uid, null);
                                FireBaseNetworkManager.getInstance(MainActivity.this).updateAutoLoginCheck(userData.uid, PreferencePhoneShared.getAutoLoginYn(MainActivity.this), null);
                            }

                            PreferencePhoneShared.setNickName(getApplicationContext(), userData.mem_nickname);
                            PreferencePhoneShared.setAutoLoginYn(getApplicationContext(), userData.mem_auto_login);
                            PreferencePhoneShared.setNotificationYn(getApplicationContext(), userData.mem_notification_yn);
                            PreferencePhoneShared.setGeoNotificationYn(getApplicationContext(), userData.mem_geo_notification_yn);
                            PreferencePhoneShared.setLocationYn(getApplicationContext(), userData.mem_location_yn);
                            PreferencePhoneShared.setWalkingCoin(getApplicationContext(), userData.walking_coin);
                            PreferencePhoneShared.setMyLocationAcceptedYn(getApplicationContext(), userData.mem_walking_my_location_yn);
                            PreferencePhoneShared.setChattingAcceptYn(getApplicationContext(), userData.mem_walking_chatting_yn);

                            PreferencePhoneShared.setStartStrollTime(getApplicationContext(), userData.mem_alarm_time.get("start"));
                            PreferencePhoneShared.setEndStrollTime(getApplicationContext(), userData.mem_alarm_time.get("end"));
                            PreferencePhoneShared.setAutoStrollMode(getApplicationContext(), userData.mem_auto_stroll_mode);

                            if(userData.pet_list.size() > 0) {
                                PreferencePhoneShared.setPetName(getApplicationContext(), userData.pet_list.get(0).petName);
                            } else {
                                PreferencePhoneShared.setPetName(getApplicationContext(), "");
                            }

                            if(PermissionManager.isAcceptedLocationPermission(MainActivity.this)) {
                                if(userData.mem_auto_stroll_mode) {
                                    if (userData != null && userData.mem_address != null) {
                                        String address = userData.mem_address.get("address");
                                        String lat = userData.mem_address.get("lat");
                                        String lot = userData.mem_address.get("lot");

                                        JWLog.e("address : " + address + ", lat :" + lat + ", lot :" + lot);
                                        if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lot)) {
                                            Intent intent = new Intent(JWBroadCast.BROAD_CAST_ADD_GEOFENCE);
                                            intent.putExtra("address", address);
                                            intent.putExtra("lat", lat);
                                            intent.putExtra("lot", lot);

                                            JWBroadCast.sendBroadcast(MainActivity.this, intent);
                                        } else {
                                            JWBroadCast.sendBroadcast(MainActivity.this, new Intent(JWBroadCast.BROAD_CAST_REMOVE_GEOFENCE));
                                        }
                                    }
                                } else {
                                    JWLog.e("자동 산책 모드가 아닙니다.");
                                    //JWToast.showToast("자동 산책 모드가 아닙니다.");
                                    JWBroadCast.sendBroadcast(MainActivity.this, new Intent(JWBroadCast.BROAD_CAST_REMOVE_GEOFENCE));
                                }
                            } else {
                                /*
                                JWLog.e("### intent.getAction() :"+intent.getAction());
                                CommonUtil.alertDialogShow(MainActivity.this, "위치", "권한이 허용되지 않았습니다.\n자동 산책모드 사용을 위해 권한을 허용 하시겠습니까?", new CommonUtil.CompleteCallback() {
                                    @Override
                                    public void onCompleted(boolean result, Object object) {
                                        if(result) {
                                            PermissionManager.requestLocationPermission(MainActivity.this);
                                        }
                                    }
                                });
                                */
                            }

                            if(userData != null) {
                                Intent intent = new Intent(JWBroadCast.BROAD_CAST_UPDATE_PROFILE);
                                intent.putExtra("email", email);
                                intent.putExtra("autoLogin", userData.mem_auto_login);
                                JWBroadCast.sendBroadcast(getApplicationContext(), intent);

//                                JWBroadCast.sendBroadcast(getApplicationContext(), new Intent(JWBroadCast.BROAD_CAST_UPDATE_PROFILE));

//                                PetData petData = userData.pet_list.get(0);

//                                mProfileText.setText(userData.mem_nickname+"님 "+"우리" + petData.petName + "와 함께 산책을 해볼까요!");
                            }

                            functionByCommand(mCurrentTapPosition);
                        }
                    });
                } else if(JWBroadCast.BROAD_CAST_LOGOUT.equals(intent.getAction())) {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
                    mUserDta = null;
                    mLayoutFab.setVisibility(View.GONE);
                } else if(JWBroadCast.BROAD_CAST_SHOW_PROGRESS_BAR.equals(intent.getAction())) {
                    JWLog.e("");
                    setProgressBar(View.VISIBLE);
                } else if(JWBroadCast.BROAD_CAST_HIDE_PROGRESS_BAR.equals(intent.getAction())) {
                    JWLog.e("");
                    setProgressBar(View.INVISIBLE);
                }
            }
        };
    }

    private void registerReceiverMain() {
        if(mIsRegisterdReceiver != true) {
            registerReceiver(mReceiver, mIntentFilter);
            mIsRegisterdReceiver = true;
        }
    }

    private void unregisterReceiverMain() {
        if(mIsRegisterdReceiver == true) {
            unregisterReceiver(mReceiver);
            mIsRegisterdReceiver = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int autoLoginType = PreferencePhoneShared.getAutoLoginType(this);

        JWLog.e("", "requestCode" + requestCode+", resultCode "+resultCode);
        if ( requestCode == RC_GOOGLE_SIGN_IN ) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if ( result.isSuccess() ) {
                JWLog.e("", "Google Login succeed." + result.getStatus());
                GoogleSignInAccount account = result.getSignInAccount();

                JWLog.e("",""+account.getId()+", "+account.getEmail()+", "+account.getDisplayName()+", "+account.getServerAuthCode());

                setProgressBar(View.VISIBLE);
                FireBaseNetworkManager.getInstance(this).firebaseAuthWithGoogle(account,new  FireBaseNetworkManager.FireBaseNetworkCallback() {
                    @Override
                    public void onCompleted(boolean result, Object object) {
                        setProgressBar(View.INVISIBLE);
                        if(object instanceof FirebaseUser) {
                            try {
                                FirebaseUser user = (FirebaseUser) object;

                                PreferencePhoneShared.setLoginYn(getApplicationContext(), true);

                                Intent intent = new Intent(JWBroadCast.BROAD_CAST_GOOGLE_LOGIN);
                                intent.putExtra("email", user.getEmail());
                                intent.putExtra("autoLogin", PreferencePhoneShared.getAutoLoginYn(getApplicationContext()));
                                JWBroadCast.sendBroadcast(getApplicationContext(), intent);

                                //updateUI(user.getEmail());




                                //readPetProfileImage(user.getEmail());

//                                FireBaseNetworkManager.getInstance(MainActivity.this).readUserData(user.getEmail(), new FireBaseNetworkManager.FireBaseNetworkCallback() {
//                                    @Override
//                                    public void onCompleted(boolean result, Object object) {
//                                        JWLog.e("", "result :"+result);
//                                        UserData userData = (UserData) object;
//                                        if(userData != null) {
//                                            PetData petData = userData.pet_list.get(0);
//                                            mProfileText.setText("우리" + petData.petName + "와 함께 산책을 해볼까요!");
//                                        }
//                                    }
//                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                JWLog.e("", "Google Login Failed." + result.getStatus());
            }
        } else if(autoLoginType == GlobalConstantID.LOGIN_TYPE_FACEBOOK) {
            FireBaseNetworkManager.getInstance(this).getFacebookCallback().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        JWLog.e("onRequestPermissionsResult ");
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        if(requestCode == PermissionManager.LOCATION_PERMISSION_REQUEST_CODE && PermissionManager.isAcceptedLocationPermission(this)) {
            if(mCurrentTapPosition == 1) {
                functionByCommand(mCurrentTapPosition);
            }
        }
    }

    private void functionByCommand(int index) {
        if(!PreferencePhoneShared.getLoginYn(thisActivity)) {
            JWToast.showToast(R.string.need_login);
            return;
        }
        if(mUserDta != null) {
            if (mCurrentFragmentInterface != null && mCurrentFragmentInterface instanceof DataExchangeInterface) {
                mCurrentFragmentInterface.functionByCommand(mUserDta.mem_email, DataExchangeInterface.CommandType.READ_WALKING_TIME_LIST);
                if(index == 1) {
                    mCurrentFragmentInterface.functionByCommand(null, DataExchangeInterface.CommandType.READ_LOCATION_INFO);
                }
            }
        }
    }

}
