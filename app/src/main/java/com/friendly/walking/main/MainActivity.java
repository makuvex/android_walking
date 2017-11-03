package com.friendly.walking.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.LoginSettingListData;
import com.friendly.walking.fragment.ReportFragment;
import com.friendly.walking.fragment.StrollFragment;
import com.friendly.walking.fragment.StrollMapFragment;
import com.friendly.walking.R;
import com.friendly.walking.activity.BaseActivity;
import com.friendly.walking.fragment.SettingFragment;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.Crypto;
import com.friendly.walking.util.JWLog;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {

    public static final int                         PAGER_MAX_COUNT = 4;
    private SectionsPagerAdapter                    mSectionsPagerAdapter;

    private ViewPager                               mViewPager = null;

    private View                                    mStrollSelected = null;
    private View                                    mMapSelected = null;
    private View                                    mReportSelected = null;
    private View                                    mSettingSelected = null;
    private View                                    mPreviousSelectedView = null;


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

        mPreviousSelectedView = mStrollSelected;

        mStrollSelected.setBackgroundResource(R.color.colorTapSelected);
        mProfileView = (LinearLayout)findViewById(R.id.profileBackgroundImageView);
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

        CircleImageView imageview = (CircleImageView)findViewById(R.id.profileImageView);

        BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Bitmap blurred = CommonUtil.blurRenderScript(this, bitmap, 25);//second parametre is radius
        //imageview.setImageBitmap(blurred);
        mProfileView.setBackground(new BitmapDrawable(blurred));


        initReceiver();
        registerReceiverMain();

        PreferencePhoneShared.setLoginYn(this, false);
        doLogin();
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
        registerReceiverMain();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiverMain();
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

    private void doLogin() {
        try {
            String key = PreferencePhoneShared.getUserUid(this);
            boolean autoLogin = PreferencePhoneShared.getAutoLoginYn(this);

            if(TextUtils.isEmpty(key) || !autoLogin) {
                return;
            }

            setProgressBar(View.VISIBLE);

            JWLog.e("","uid :" + key);
            String decEmail = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginID(this), key));
            String decPassword = CommonUtil.urlDecoding(Crypto.decryptAES(PreferencePhoneShared.getLoginPassword(this), key));

            Intent intent = new Intent(JWBroadCast.BROAD_CAST_LOGIN);
            intent.putExtra("email", decEmail);
            intent.putExtra("password", decPassword);
            //intent.putExtra("autoLogin", autoLogin);

            JWBroadCast.sendBroadcast(getApplicationContext(), intent);
        } catch(Exception e) {
            e.printStackTrace();
            setProgressBar(View.INVISIBLE);
        }
    }

    private void initReceiver() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JWLog.e("","action :"+intent.getAction());

                if(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI.equals(intent.getAction())) {
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
}
