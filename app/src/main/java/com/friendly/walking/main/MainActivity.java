package com.friendly.walking.main;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.LinearLayout;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.TextView;
import android.widget.Toast;

import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.activity.KakaoSignupActivity;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.PetData;
import com.friendly.walking.dataSet.UserData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.fragment.ReportFragment;
import com.friendly.walking.fragment.StrollFragment;
import com.friendly.walking.fragment.StrollMapFragment;
import com.friendly.walking.R;
import com.friendly.walking.activity.BaseActivity;
import com.friendly.walking.fragment.SettingFragment;
//import com.friendly.walking.geofence.GeofenceManager;
import com.friendly.walking.network.KakaoLoginManager;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.service.MainService;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.usermgmt.response.model.UserProfile;


import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.friendly.walking.firabaseManager.FireBaseNetworkManager.RC_GOOGLE_SIGN_IN;

public class MainActivity extends BaseActivity {

    public static final int                         PAGER_MAX_COUNT = 4;
    private SectionsPagerAdapter                    mSectionsPagerAdapter;

    private ViewPager                               mViewPager = null;

    private View                                    mStrollSelected = null;
    private View                                    mMapSelected = null;
    private View                                    mReportSelected = null;
    private View                                    mSettingSelected = null;
    private View                                    mPreviousSelectedView = null;
    private CircleImageView                         mProfileImageView = null;
    private TextView                                mProfileText = null;

    private LinearLayout                            mProfileView = null;

    private long                                    mDoublePressInterval = 2000;
    private long                                    mPreviousTouchTime = 0;

    private MainActivity                            thisActivity;

    private BroadcastReceiver                       mReceiver = null;
    private IntentFilter                            mIntentFilter = null;
    private boolean                                 mIsRegisterdReceiver = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivity = this;
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        mStrollSelected = findViewById(R.id.stroll_page);
        mMapSelected = findViewById(R.id.map_page);
        mReportSelected = findViewById(R.id.report_page);
        mSettingSelected = findViewById(R.id.setting_page);
        mProfileImageView = (CircleImageView)findViewById(R.id.profileImageView);
        mProfileText = (TextView)findViewById(R.id.profileText);
        mProfileText.setText("누구의 산책에 온거 축하");

        mPreviousSelectedView = mStrollSelected;

        mStrollSelected.setBackgroundResource(R.color.colorTapSelected);
        mProfileView = (LinearLayout)findViewById(R.id.profileBackgroundImageView);
        //mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProfileView.setBackgroundResource(R.drawable.profile_bg);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                Log.e("@@@","@@@ onPageSelected position : "+position);

                if(mPreviousSelectedView != null) {
                    mPreviousSelectedView.setBackgroundResource(R.color.colorTapUnselected);
                }

                int selectedTapColor = R.color.colorTapSelected;
                switch(position) {
                    case 0 :
                        showProfileView(true);
                        mStrollSelected.setBackgroundResource(selectedTapColor);
                        mPreviousSelectedView = mStrollSelected;
                        break;
                    case 1 :
                        showProfileView(false);
                        mMapSelected.setBackgroundResource(selectedTapColor);
                        mPreviousSelectedView = mMapSelected;
                        break;
                    case 2 :
                        showProfileView(false);
                        mReportSelected.setBackgroundResource(selectedTapColor);
                        mPreviousSelectedView = mReportSelected;
                        break;
                    case 3 :
                        showProfileView(false);
                        mSettingSelected.setBackgroundResource(selectedTapColor);
                        mPreviousSelectedView = mSettingSelected;
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        startService(intent);

        PreferencePhoneShared.setLoginYn(this, false);
        if(!doLogin()) {
            JWBroadCast.sendBroadcast(MainActivity.this, new Intent(JWBroadCast.BROAD_CAST_REMOVE_GEOFENCE));
        }
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (0 >= mPreviousTouchTime || mDoublePressInterval < (currentTime - mPreviousTouchTime)) {
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료 됩니다.", Toast.LENGTH_SHORT).show();
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
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int currentPosition = 0;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("","@@@ getItem position :"+position);
            currentPosition = position;
            if(position == 0) {
                return StrollFragment.newInstance(position + 1);
            } else if(position == 1) {
                StrollMapFragment fragment = StrollMapFragment.newInstance(position + 1);
                fragment.selectedThisFragment();
                return fragment;
            } else if(position == 2) {
                return ReportFragment.newInstance(position + 1);
            } else if(position == 3) {
                return SettingFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
            }
            return null;
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

    public void showProfileView(boolean show) {
        mProfileView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

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
                Intent intent = new Intent(JWBroadCast.BROAD_CAST_LOGIN);
                intent.putExtra("email", decEmail);
                intent.putExtra("password", decPassword);
                //intent.putExtra("autoLogin", autoLogin);

                JWBroadCast.sendBroadcast(getApplicationContext(), intent);
            } else if(autoLoginType == GlobalConstantID.LOGIN_TYPE_GOOGLE) {
                FireBaseNetworkManager.getInstance(this).googleSignIn(this);
            } else if(autoLoginType == GlobalConstantID.LOGIN_TYPE_FACEBOOK) {

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


        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JWLog.e("","action :"+intent.getAction());

                if(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI.equals(intent.getAction())) {
                    setProgressBar(View.INVISIBLE);
                } else if(JWBroadCast.BROAD_CAST_FACEBOOK_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_GOOGLE_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_KAKAO_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_EMAIL_LOGIN.equals(intent.getAction())) {

                    String email = intent.getStringExtra("email");
                    setProgressBar(View.VISIBLE);

                    readPetProfileImage(email);

                    FireBaseNetworkManager.getInstance(MainActivity.this).readUserData(email, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                        @Override
                        public void onCompleted(boolean result, Object object) {
                            JWLog.e("", "result :"+result);

                            setProgressBar(View.INVISIBLE);
                            UserData userData = (UserData) object;

                            JWLog.e("userData :"+userData);
                            if(!TextUtils.isEmpty(userData.mem_email)) {
                                FireBaseNetworkManager.getInstance(MainActivity.this).updateLastLoginTime(userData.uid, null);
                                FireBaseNetworkManager.getInstance(MainActivity.this).updateAutoLoginCheck(userData.uid, PreferencePhoneShared.getAutoLoginYn(MainActivity.this), null);
                            }

                            if(userData != null) {
                                PetData petData = userData.pet_list.get(0);
                                mProfileText.setText("우리" + petData.petName + "와 함께 산책을 해볼까요!");
                            }

                            PreferencePhoneShared.setNotificationYn(getApplicationContext(), userData.mem_notification_yn);
                            PreferencePhoneShared.setGeoNotificationYn(getApplicationContext(), userData.mem_geo_notification_yn);
                            PreferencePhoneShared.setLocationYn(getApplicationContext(), userData.mem_location_yn);

                            PreferencePhoneShared.setStartStrollTime(getApplicationContext(), userData.mem_alarm_time.get("start"));
                            PreferencePhoneShared.setEndStrollTime(getApplicationContext(), userData.mem_alarm_time.get("end"));
                            PreferencePhoneShared.setAutoStrollMode(getApplicationContext(), userData.mem_auto_stroll_mode);

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
                                Toast.makeText(MainActivity.this, "자동 산책 모드가 아닙니다.", Toast.LENGTH_SHORT).show();
                                JWBroadCast.sendBroadcast(MainActivity.this, new Intent(JWBroadCast.BROAD_CAST_REMOVE_GEOFENCE));
                            }
                            if(userData != null) {
                                PetData petData = userData.pet_list.get(0);
                                mProfileText.setText("우리" + petData.petName + "와 함께 산책을 해볼까요!");
                            }
                        }
                    });
                } else if(JWBroadCast.BROAD_CAST_LOGOUT.equals(intent.getAction())) {
                    mProfileText.setText("누구의 산책에 온거 축하");
                    mProfileImageView.setImageResource(R.drawable.default_profile);
                    mProfileView.setBackgroundResource(R.drawable.profile_bg);
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
                                updateUI(user.getEmail());
                                readPetProfileImage(user.getEmail());

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
        }
    }

    private void readPetProfileImage(String email) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Uri uri = Uri.parse(path.getAbsolutePath() + "/" + email +"_pet_profile.jpg");
        JWLog.e("","uri :"+uri.toString());

        FireBaseNetworkManager.getInstance(this).downloadProfileImage(uri, new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                if(result) {
                    Bitmap bitmap = (Bitmap)object;
                    Bitmap blurred = CommonUtil.blurRenderScript(getApplicationContext(), bitmap, 25);//second parametre is radius

                    mProfileImageView.setImageBitmap(bitmap);
                    mProfileView.setBackground(new BitmapDrawable(blurred));
                }
            }
        });
    }


}
