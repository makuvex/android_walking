package com.friendly.walking.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.friendly.walking.R;
import com.friendly.walking.views.CircleAnimIndicator;


import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;

public class StrollFragment extends Fragment {
    private static final int    MAX_PAGE_COUNT = 3;
    private static final String ARG_SECTION_NUMBER = "section_number";

    private CircleAnimIndicator circleAnimIndicator;
    private CircleProgressView circleProgressView;

    public StrollFragment() {}

    public static StrollFragment newInstance(int sectionNumber) {
        StrollFragment fragment = new StrollFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stroll, container, false);

        circleAnimIndicator = (CircleAnimIndicator)rootView.findViewById(R.id.circleAnimIndicator);
        circleProgressView = (CircleProgressView)rootView.findViewById(R.id.circle_progress);

        initIndicaotor();
        Log.e("","getArguments().getInt(ARG_SECTION_NUMBER) :"+getArguments().getInt(ARG_SECTION_NUMBER) );
        if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            rootView.setBackgroundColor(Color.WHITE);
        } else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
            rootView.setBackgroundColor(Color.YELLOW);
        } else {
            rootView.setBackgroundColor(Color.GREEN);
        }
        return rootView;
    }

    private void initIndicaotor(){

        //원사이의 간격
        circleAnimIndicator.setItemMargin(15);
        //애니메이션 속도
        circleAnimIndicator.setAnimDuration(300);
        //indecator 생성
        circleAnimIndicator.createDotPanel(MAX_PAGE_COUNT, R.drawable.dot_icon_2 , R.drawable.dot_icon_1);

        circleAnimIndicator.selectDot(0);

        circleProgressView.setBarColor(getResources().getColor(R.color.primary), getResources().getColor(R.color.accent));
        circleProgressView.setRimColor(getResources().getColor(R.color.primary_light));

        circleProgressView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                circleProgressView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                circleProgressView.setUnitVisible(true);
                                break;
                            case SPINNING:
                                circleProgressView.setTextMode(TextMode.TEXT); // show text while spinning
                                circleProgressView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );


        circleProgressView.setMaxValue(100);
        circleProgressView.setValueAnimated(50, 1500);

    }

}