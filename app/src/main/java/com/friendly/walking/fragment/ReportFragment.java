package com.friendly.walking.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.R;
import com.friendly.walking.activity.DayAxisValueFormatter;
import com.friendly.walking.activity.MyAxisValueFormatter;
import com.friendly.walking.activity.XYMarkerView;
import com.friendly.walking.dataSet.StrollTimeData;
import com.friendly.walking.firabaseManager.FireBaseNetworkManager;
import com.friendly.walking.main.DataExchangeInterface;
import com.friendly.walking.util.JWLog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.time.LocalDate;


import static com.friendly.walking.main.DataExchangeInterface.CommandType.READ_WALKING_TIME_LIST;

public class ReportFragment extends Fragment implements OnChartValueSelectedListener, DataExchangeInterface {

    protected BarChart                          mChart;
    protected RectF                             mOnValueSelectedRectF = new RectF();
    private View                                mView;
    private TextView                            mNoDataView;
    private Map<String, String>                 mWalkingTimeList;
    private static ArrayList<StrollTimeData>    mMonthDataList;
    private int                                 mPeekMin = 0;
    private int                                 mCurrentChart = 1;

    private Button                              mMonthButton;
    private Button                              mQuarterButton;
    private Button                              mYearButton;

    private View.OnClickListener                mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.month_button) {
                if(mCurrentChart != 1) {
                    mCurrentChart = 1;
                    updateChartData();
                }
            } else if(view.getId() == R.id.quarter_button) {
                if(mCurrentChart != 3) {
                    mCurrentChart = 3;
                    updateChartData();
                }
            } else {
                mCurrentChart = 12;

                JWLog.e("미 구현입니다.");
                JWToast.showToast("미 구현입니다.");
            }

            updateTabState(mCurrentChart);
        }
    };

    public ReportFragment() {
        // Required empty public constructor
    }

    public static ReportFragment newInstance(int sectionNumber) {
        ReportFragment fragment = new ReportFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_report, container, false);
        if(mMonthDataList == null) {
            mMonthDataList = new ArrayList<>();
        }
        mMonthButton = (Button)mView.findViewById(R.id.month_button);
        mQuarterButton = (Button)mView.findViewById(R.id.quarter_button);
        mYearButton = (Button)mView.findViewById(R.id.year_button);

        mMonthButton.setOnClickListener(mClickListener);
        mQuarterButton.setOnClickListener(mClickListener);
        mYearButton.setOnClickListener(mClickListener);

        mView.findViewById(R.id.no_data_text).setVisibility(View.VISIBLE);

        initChart();
        long cur = System.currentTimeMillis();
        JWLog.e("################ "+cur+" #################");

        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        /*
        RectF bounds = mOnValueSelectedRectF;
        mChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mChart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mChart.getLowestVisibleX() + ", high: "
                        + mChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
        */
    }

    @Override
    public void onNothingSelected() { }

    @Override
    public void functionByCommand(Object email, CommandType type) {

        if(type == READ_WALKING_TIME_LIST) {
            FireBaseNetworkManager.getInstance(getActivity()).readWalkingTimeList((String)email, new FireBaseNetworkManager.FireBaseNetworkCallback() {
                @Override
                public void onCompleted(boolean result, Object object) {
                    if(result) {
                        mWalkingTimeList = (Map<String, String>)object;
                        if(mWalkingTimeList.size() > 0) {
                            mView.findViewById(R.id.no_data_text).setVisibility(View.GONE);
                        } else {
                            mView.findViewById(R.id.no_data_text).setVisibility(View.VISIBLE);
                        }

                        updateChartData();
                        updateTabState(mCurrentChart);
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
        mMonthDataList.clear();
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_MONTH);

        JWLog.e("@@@ today :"+today);

        if(mCurrentChart == 3) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(calendar.MONTH, 0);
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");
            String currentYear = sd.format(calendar.getTime()).substring(0,4);
            String currentMonth = sd.format(calendar.getTime()).substring(4,6);

            String beforeYear = currentYear;
            String beforeMonth = currentMonth;

            if(Integer.parseInt(currentMonth) <= 2) {
                beforeYear = "" + (Integer.parseInt(currentYear) - 1);
                beforeMonth = "11";
            } else {
                beforeMonth = "" + (Integer.parseInt(currentMonth) - 1);
            }

            for (int i = 1; i <= 31; i++) {
                String day = String.format("%02d", i);
               String key = beforeYear + "-" + beforeMonth + "-" + day;
                JWLog.e("key :" + key);
                String min = mWalkingTimeList.get(key);
                JWLog.e("min :" + min);

                if (min != null) {
                    if (!min.equals("0")) {
                        mMonthDataList.add(new StrollTimeData(beforeMonth + day, min));
                    }
                }
            }

            if(Integer.parseInt(currentMonth) <= 1) {
                beforeYear = "" + (Integer.parseInt(currentYear) - 1);
                beforeMonth = "12";
            } else {
                beforeMonth = "" + (Integer.parseInt(currentMonth) - 1);
            }

            for (int i = 1; i <= 31; i++) {
                String day = String.format("%02d", i);

                String key = beforeYear + "-" + beforeMonth + "-" + day;
                JWLog.e("key :" + key);
                String min = mWalkingTimeList.get(key);
                JWLog.e("min :" + min);

                if (min != null) {
                    if (!min.equals("0")) {
                        mMonthDataList.add(new StrollTimeData(beforeMonth + day, min));
                    }
                }
            }
        }

        for(int i=1; i<=today; i++) {
            Calendar tempCal = new GregorianCalendar();
            tempCal.add(Calendar.DATE, i-today);
            String month = String.format("%02d", (tempCal.get(Calendar.MONTH) + 1));
            String day = String.format("%02d", tempCal.get(Calendar.DAY_OF_MONTH));

            String key = tempCal.get(Calendar.YEAR) + "-" + month + "-" + day;
            JWLog.e("key :"+key);
            String min = mWalkingTimeList.get(key);
            JWLog.e("min :"+min);

            if(min != null) {
                if(!min.equals("0")) {
                    mMonthDataList.add( new StrollTimeData(month + day, min));
                }
            }
        }

        for(StrollTimeData data : mMonthDataList) {
            if(mPeekMin < (int)(Float.parseFloat(data.min) - (int)Float.parseFloat(data.min))) {
                mPeekMin = (int)(Float.parseFloat(data.min) - (int)Float.parseFloat(data.min));
            }
        }

        if(mMonthDataList.size() == 0) {
            JWLog.e("no data");
            mView.findViewById(R.id.no_data_text).setVisibility(View.VISIBLE);
        } else {
            JWLog.e("exist data");
            mView.findViewById(R.id.no_data_text).setVisibility(View.GONE);
        }
        JWLog.e("mMonthDataList :"+mMonthDataList);

        initChart();
    }

    private void initChart() {
        mChart = (BarChart) mView.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(93);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        DayAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxisFormatter.setDayArray(mMonthDataList);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        //rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(getActivity(), xAxisFormatter);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        setData(mMonthDataList.size(), mPeekMin);

    }

    private void setData(int count, float range) {
//        float start = 1f;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

//        for (int i = (int) start; i < start + count + 1; i++) {
//            float mult = (range + 1);
//            float val = (float) (Math.random() * mult);
//
//            if (Math.random() * 100 < 25) {
//                yVals1.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.star)));
//            } else {
//                yVals1.add(new BarEntry(i, val));
//            }
//            JWLog.e("i :"+i+", val :"+val);
//        }

        for(int i=0; i<mMonthDataList.size(); i++) {
//            String f = mMonthDataList.get(i).min.substring(0, mMonthDataList.get(i).min.indexOf("."));
//            String min = mMonthDataList.get(i).min.substring(mMonthDataList.get(i).min.indexOf(".")+1, mMonthDataList.get(i).min.length());
//
//            JWLog.e("1 ################## "+(float)Long.parseLong(f)+ " ############");
//            JWLog.e("2 ################## "+Float.parseFloat(min)+ " ############");
//
//            String time = "" + (long)Long.parseLong(f);
//            Date day = new Date(Long.parseLong(time));
//            SimpleDateFormat sd = new SimpleDateFormat("D");
//
//            JWLog.e("@@@ "+sd.format(day));

            yVals1.add(new BarEntry(i+1, Float.parseFloat(mMonthDataList.get(i).min)));
        }

        BarDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            Calendar tempCal = new GregorianCalendar();
            int month = tempCal.get(Calendar.MONTH)+1;

            set1 = new BarDataSet(yVals1, getMonth(month));
            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            //data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }

        mChart.animateXY(1400, 1400);
    }

    private String getMonth(int mon) {
        String month[] = {"Janury", "February", "March", "April", "May", "June", "August", "September", "October", "November", "December"};
        return  month[mon-1];
    }

    private void updateTabState(int tab) {
        mMonthButton.setSelected(false);
        mQuarterButton.setSelected(false);
        mYearButton.setSelected(false);

        if(tab == 1) {
            mMonthButton.setSelected(true);
        } else if(tab == 3) {
            mQuarterButton.setSelected(true);
        } else {
            mYearButton.setSelected(true);
        }
    }
}
