package com.friendly.walking.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.friendly.walking.R;
import com.friendly.walking.broadcast.JWBroadCast;
import com.friendly.walking.dataSet.PetData;
import com.friendly.walking.dataSet.StrollTimeData;
import com.friendly.walking.dataSet.WalkingData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.main.DataExchangeInterface;
import com.friendly.walking.preference.PreferencePhoneShared;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.JWLog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.friendly.walking.main.DataExchangeInterface.CommandType.READ_WALKING_TIME_LIST;
import static com.github.mikephil.charting.utils.ColorTemplate.rgb;


public class WalkingChartFragment extends Fragment implements OnChartValueSelectedListener, DataExchangeInterface, SwipeRefreshLayout.OnRefreshListener {
    private static final int                    MAX_PAGE_COUNT = 3;
    private static final String                 ARG_SECTION_NUMBER = "section_number";

    private View                                mRootView;
    private PieChart                            mChart;
    //private TextView                            mNoDataView;
    private Map<String, String>                 mWalkingTimeList;
    private static ArrayList<StrollTimeData>    mWeekTimeList;
    private SwipeRefreshLayout                  mSwipeRefreshLayout;

    private LinearLayout                        mProfileView = null;
    private BroadcastReceiver                   mReceiver = null;
    private IntentFilter                        mIntentFilter = null;
    private boolean                             mIsRegisterdReceiver = false;
    private CircleImageView                     mProfileImageView = null;
    private TextView                            mProfileTextView = null;

    private Bitmap                              mProfileImage = null;
    private Bitmap                              mProfileBackground = null;
    private String                              mProfileText = null;
    private String                              mEmail = null;

    protected final String[]                    mParties = new String[] {
        "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"
    };
    protected final float[]                     mWalkingMin = new float[] {
        30, 40, 0, 50, 20, 0, 30
    };
    public static final int[]                   MATERIAL_COLORS_EX = {
        rgb("#cce1f5")
    };

    public WalkingChartFragment() {}

    public static WalkingChartFragment newInstance(int sectionNumber) {
        WalkingChartFragment fragment = new WalkingChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        JWLog.e("@@@@@@");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stroll, container, false);
        JWLog.e("@@@@@@");
        JWLog.e("","getArguments().getInt(ARG_SECTION_NUMBER) :"+getArguments().getInt(ARG_SECTION_NUMBER) );
        rootView.setBackgroundColor(Color.WHITE);

        mRootView = rootView;
        if(mWeekTimeList == null) {
            mWeekTimeList = new ArrayList<>();
        }
        initView();
        initChart();
        initReceiver();
        registerReceiverMain();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        JWLog.e("@@@@@@");
    }

    @Override
    public void onStop() {
        super.onStop();
        JWLog.e("@@@@@@");
    }


    @Override
    public void onDestroy() {
        unregisterReceiverMain();
        super.onDestroy();
    }

    private void initView() {
        mProfileView = (LinearLayout)mRootView.findViewById(R.id.profileBackgroundImageView);
        if(mProfileBackground != null) {
            mProfileView.setBackground(new BitmapDrawable(mProfileBackground));
            //mProfileView.setAlpha(0.5f);
        } else {
            mProfileView.setBackgroundResource(R.drawable.profile_bg);
            //mProfileView.setAlpha(0.5f);
        }
        mProfileImageView = (CircleImageView)mRootView.findViewById(R.id.profileImageView);
        if(mProfileImage != null) {
            mProfileImageView.setImageBitmap(mProfileImage);
        }
        mProfileTextView = (TextView)mRootView.findViewById(R.id.profileText);
        if(mProfileText == null) {
            mProfileTextView.setText("사랑하는 반려동물과 함께 산책 나가볼까요.");
        } else {
            mProfileTextView.setText(mProfileText);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout)mRootView.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright);
    }

    private void initChart() {
        mChart = (PieChart) mRootView.findViewById(R.id.pie_chart);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(0, 0, 0, 0);
        mChart.setTouchEnabled(false);
        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(false);
        mChart.setHighlightPerTapEnabled(true);

        mChart.setOnChartValueSelectedListener(this);

        setData(mWeekTimeList.size(), 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        mChart.setEntryLabelColor(Color.DKGRAY);
        mChart.setEntryLabelTextSize(10f);
    }

    private void setData(int count, float range) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        float sum = 0;

        if(count == 0) {
            entries.add(new PieEntry((float)1,
                    "없음",
                    getResources().getDrawable(R.drawable.star)));
        } else {
            for(StrollTimeData data : mWeekTimeList) {
               sum += Float.parseFloat(data.min);
            }
            for(StrollTimeData data : mWeekTimeList) {
               JWLog.e("key :"+data.day+", value :"+data.min);

               float percent = (Float.parseFloat(data.min)) * ((float)100 / sum);
               entries.add(new PieEntry((float) Float.parseFloat(data.min), data.day + "(" + data.min + "분)", getResources().getDrawable(R.drawable.star)));
           }

        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        if(sum != 0) {
            mChart.setCenterText(generateCenterSpannableText("" + (int)sum));
            mChart.setDrawEntryLabels(true);
            dataSet.setDrawValues(true);
        } else {
            mChart.setCenterText(new SpannableString("산책 기록 없음"));
            mChart.setDrawEntryLabels(false);
            dataSet.setDrawValues(false);
        }

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : MATERIAL_COLORS_EX)
            colors.add(c);

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);

//        List<Integer> textColorList = new ArrayList<>();
//        textColorList.addAll(new ArrayList<Integer>(Arrays.asList(VORDIPLOM_DARK_COLORS)));
//        textColorList.addAll(new ArrayList<Integer>(Arrays.asList(JOYFUL_DARK_COLORS)));

        data.setValueTextColor(Color.DKGRAY);

        //data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private SpannableString generateCenterSpannableText(String min) {
        String title = "요일 별 산책 결과\n\n전체 "+min+"분";

        SpannableString s = new SpannableString(title);
        s.setSpan(new RelativeSizeSpan(1.3f), 0, title.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), "요일 별 산책 결과".length(), title.length(), 0);
        s.setSpan(new RelativeSizeSpan(.8f), "요일 별 산책 결과".length(), title.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), "요일 별 산책 결과".length(), title.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.rgb(92, 177, 124)), "요일 별 산책 결과".length(), s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        JWLog.e("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        JWLog.e("PieChart", "nothing selected");
    }

    @Override
    public void functionByCommand(String email, CommandType type) {

        if(type == READ_WALKING_TIME_LIST) {
            mEmail = email;
            FireBaseNetworkManager.getInstance(getActivity()).readWalkingTimeList(email, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    if(result) {
                        mWalkingTimeList = (Map<String, String>)object;
                        updateChartData();
                    }
                }
            });
        }
    }

    private void updateChartData() {
        if(mWalkingTimeList == null) {
            JWLog.e("mWalkingTimeList is empty");
            return;
        }
        mWeekTimeList.clear();
        Calendar cal = Calendar.getInstance();
        int nWeek = cal.get(Calendar.DAY_OF_WEEK);

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd", Locale.KOREA );
        String today = formatter.format(date);

        JWLog.e("nWeek :"+nWeek);

        // 2, 3, 4, 5, 6, 7, 1      -> 월, 화, ..., 일
        if(nWeek == 1) {
            nWeek = 8;
        }

        for (int i=2; i<=nWeek; i++) {
            Calendar tempCal = new GregorianCalendar();
            tempCal.add(Calendar.DATE, nWeek - i > 0 ? -(nWeek - i) : (nWeek - i));
            String month = String.format("%02d", (tempCal.get(Calendar.MONTH) + 1));
            String day = String.format("%02d", tempCal.get(Calendar.DAY_OF_MONTH));

            String key = tempCal.get(Calendar.YEAR) + "-" + month + "-" + day;
            JWLog.e("key :"+key);
            String min = mWalkingTimeList.get(key);
            JWLog.e("min :"+min);

            if(min != null) {
                //min = min.substring(min.indexOf(".")+1, min.length());
                if(!min.equals("0")) {
                    mWeekTimeList.add( new StrollTimeData(getDayOfWeek(i), min));
                }
            }
        }


        JWLog.e("mWeekTimeList :"+mWeekTimeList);
        float sum = 0;
        for(StrollTimeData data : mWeekTimeList) {
            sum += Float.parseFloat(data.min);
        }

        if(sum > 0) {
            initChart();
        }
    }

    private String getDayOfWeek(int nWeek) {
        String weekEnd[] = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};
        return  weekEnd[nWeek-2];
    }

    private void initReceiver() {
        mIsRegisterdReceiver = false;
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_UPDATE_SETTING_UI);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_FACEBOOK_LOGIN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_GOOGLE_LOGIN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_KAKAO_LOGIN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_LOGOUT);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_EMAIL_LOGIN);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_REFRESH_USER_DATA);
        mIntentFilter.addAction(JWBroadCast.BROAD_CAST_UPDATE_PROFILE);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                JWLog.e("", "action :" + intent.getAction());

                if(JWBroadCast.BROAD_CAST_LOGOUT.equals(intent.getAction())) {
                    mProfileText = null;
                    mProfileImage = null;
                    mProfileBackground = null;
                    mWeekTimeList.clear();
                    setData(mWeekTimeList.size(), 100);

                    mProfileTextView.setText("사랑하는 반려동물과 함께 산책 나가볼까요.");
                    mProfileImageView.setImageResource(R.drawable.default_profile);
                    mProfileView.setBackgroundResource(R.drawable.profile_bg);
                } else if(JWBroadCast.BROAD_CAST_FACEBOOK_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_GOOGLE_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_KAKAO_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_EMAIL_LOGIN.equals(intent.getAction())
                        || JWBroadCast.BROAD_CAST_REFRESH_USER_DATA.equals(intent.getAction())) {

                    String email = intent.getStringExtra("email");
                    readPetProfileImage(email);
                } else if(JWBroadCast.BROAD_CAST_UPDATE_PROFILE.equals(intent.getAction())) {
                    String userNickName = PreferencePhoneShared.getNickName(getApplicationContext());
                    String petName = PreferencePhoneShared.getPetName(getApplicationContext());
                    if(!petName.isEmpty()) {
                        mProfileText = userNickName+"님 "+"우리 " + petName + "와 함께 산책을 해볼까요!";
                    } else {
                        mProfileText = userNickName+"님 "+" 사랑하는 반려동물과 함께 산책 나가볼까요.";
                    }

                    mProfileTextView.setText(mProfileText);
                }
            }
        };
    }

    private void readPetProfileImage(String email) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Uri uri = Uri.parse(path.getAbsolutePath() + "/" + email +"_pet_profile.jpg");
        JWLog.e("","uri :"+uri.toString());

        FireBaseNetworkManager.getInstance(getApplicationContext()).downloadProfileImage(uri, new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                if(result) {
                    mProfileImage = (Bitmap)object;
                    mProfileBackground = CommonUtil.blurRenderScript(getApplicationContext(), mProfileImage, 25);//second parametre is radius

                    mProfileImageView.setImageBitmap(mProfileImage);
                    mProfileView.setBackground(new BitmapDrawable(mProfileBackground));
                }
            }
        });
    }

    private void registerReceiverMain() {
        if(mIsRegisterdReceiver != true) {
            getActivity().registerReceiver(mReceiver, mIntentFilter);
            mIsRegisterdReceiver = true;
        }
    }

    private void unregisterReceiverMain() {
        if(mIsRegisterdReceiver == true) {
            getActivity().unregisterReceiver(mReceiver);
            mIsRegisterdReceiver = false;
        }
    }

    @Override
    public void onRefresh() {
        JWLog.e("@@@@@@@@@@@@@@@ onRefresh @@@@@@@@@@@@@@@");
        FireBaseNetworkManager.getInstance(getActivity()).refreshWalkingTimeList(mEmail, new FireBaseNetworkManager.FireBaseNetworkCallback() {
            @Override
            public void onCompleted(boolean result, Object object) {
                if(result) {
                    mWalkingTimeList = (Map<String, String>)object;
                    updateChartData();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}