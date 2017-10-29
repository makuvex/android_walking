package com.friendly.walkingout.main;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.Toast;

import com.friendly.walkingout.R;
import com.friendly.walkingout.activity.BaseActivity;
import com.friendly.walkingout.fragment.ReportFragment;
import com.friendly.walkingout.fragment.SettingFragment;
import com.friendly.walkingout.fragment.StrollFragment;
import com.friendly.walkingout.fragment.StrollMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import de.hdodenhof.circleimageview.CircleImageView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends BaseActivity {

    public static final int PAGER_MAX_COUNT = 4;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager   mViewPager = null;

    private View        mStrollSelected = null;
    private View        mMapSelected = null;
    private View        mReportSelected = null;
    private View        mSettingSelected = null;
    private View        mPreviousSelectedView = null;

    private LinearLayout mProfileView = null;

    private long        mDoublePressInterval = 2000;
    private long        mPreviousTouchTime = 0;

    private MainActivity    thisActivity;


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

        Bitmap blurred = blurRenderScript(bitmap, 25);//second parametre is radius
        //imageview.setImageBitmap(blurred);
        mProfileView.setBackground(new BitmapDrawable(blurred));
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

    @SuppressLint("NewApi")
    private Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(this.getApplicationContext());

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
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
}
