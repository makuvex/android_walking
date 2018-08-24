package com.friendly.walking.activity;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.GlobalConstantID;
import com.friendly.walking.R;
import com.friendly.walking.util.CommonUtil;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.friendly.walking.activity.GoogleMapActivity.ViewMapType.TYPE_SETTING_LOCATION;
import static com.friendly.walking.activity.GoogleMapActivity.ViewMapType.TYPE_VIEW_LOCATION;

/**
 * Created by jungjiwon on 2017. 10. 25..
 */

public class GoogleMapActivity extends BaseActivity implements View.OnClickListener,
                                                                OnMapReadyCallback,
                                                                LocationListener,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener {

    public enum ViewMapType {
        TYPE_SETTING_LOCATION,
        TYPE_VIEW_LOCATION,
    };


    private static final int                    GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int                    UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int                    FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private EditText                            mAddressText;
    private ImageButton                         mFindButton;
    private ImageButton                         mDoneButton;

    private String                              mAddress;
    private double                              mLat;
    private double                              mLot;
    private Geocoder                            mGeocoder;
    private Location                            mCurrentLocation;
    private GoogleMap                           mGoogleMap;
    private String                              mGoogleMapAddressLine;
    private String                              mNickName;
    private FloatingActionButton                mFloatingButton;
    private ViewMapType                         mViewMapType;

    private boolean                             mMoveMapByUser = true;
    private boolean                             mMoveMapByAPI = true;
    private boolean                             mRequestingLocationUpdates = false;
    private Marker                              currentMarker = null;
    private GoogleApiClient                     mGoogleApiClient = null;
    LocationRequest                             locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                                                    .setInterval(UPDATE_INTERVAL_MS)
                                                                    .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    @Override
    public void onCreate(Bundle bundle) {
        JWLog.e("","");
        super.onCreate(bundle);

        setContentView(R.layout.activity_google_map);

//        Intent intent = getIntent();
//        if(intent != null) {
//            mAddress = intent.getStringExtra(GlobalConstantID.HOME_ADDRESS);
//        }
        mViewMapType = TYPE_SETTING_LOCATION;
        mAddressText = (EditText)findViewById(R.id.address);
        mFindButton = (ImageButton)findViewById(R.id.find);
        mDoneButton = (ImageButton)findViewById(R.id.confirm);
        mFloatingButton = (FloatingActionButton)findViewById(R.id.fab);
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JWLog.e("@@@");
            }
        });

        mGoogleMapAddressLine = getIntent().getStringExtra("address");
        mNickName = getIntent().getStringExtra("user");
        String title = getIntent().getStringExtra("title");

        if(title != null) {
            TextView textView = (TextView)findViewById(R.id.title);
            textView.setText(title);

            findViewById(R.id.address_layout).setVisibility(View.GONE);
            mViewMapType = TYPE_VIEW_LOCATION;

            // 추후 구현 예정
            //mFloatingButton.setVisibility(View.VISIBLE);
        }
        try {
            mLat = Double.parseDouble(getIntent().getStringExtra("lat"));
            mLot = Double.parseDouble(getIntent().getStringExtra("lot"));

            if(mGoogleMapAddressLine == null) {
                mGoogleMapAddressLine = getCurrentAddress(new LatLng(mLat, mLot));
            }

            JWLog.e("mGoogleMapAddressLine "+mGoogleMapAddressLine+ ", lat : "+mLat+", mLot :"+mLot);
            if(mLot != 0 && mLat != 0) {
                mRequestingLocationUpdates = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAddressText.setText(mGoogleMapAddressLine);
        mAddressText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    searchAddress(v);
                }
                return true;
            }
        });

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mFindButton.setOnClickListener(this);
        mDoneButton.setOnClickListener(this);
        mGeocoder = new Geocoder(this);
    }

    @Override
    public void onStart() {
        JWLog.e("","");
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){
            JWLog.e("", "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    public void onResume() {
        JWLog.e("","");
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            if (!mRequestingLocationUpdates) {
                if(findViewById(R.id.address_layout).getVisibility() == View.VISIBLE) {
                    startLocationUpdates();
                }
            }
        }
    }

    @Override
    public void onStop() {
        JWLog.e("","");
        if (mRequestingLocationUpdates) {
            JWLog.e("", "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if (mGoogleApiClient != null &&  mGoogleApiClient.isConnected()) {
            JWLog.e("", "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        JWLog.e("","");
        if (mRequestingLocationUpdates) {
            JWLog.e("", "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if (mGoogleApiClient != null &&  mGoogleApiClient.isConnected()) {
            JWLog.e("", "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        JWLog.e("","");
        if(view == mFindButton) {
            searchAddress(view);
        } else if(view == mDoneButton) {
            Bundle bundle = new Bundle();
            Intent intent = new Intent();

            bundle.putString("address", mGoogleMapAddressLine);
            bundle.putString("lat", ""+mCurrentLocation.getLatitude());
            bundle.putString("lot", ""+mCurrentLocation.getLongitude());

            intent.putExtras(bundle);
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        JWLog.e("","");

        mGoogleMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {
                JWLog.e( "", "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                mRequestingLocationUpdates = false;
                startLocationUpdates();

                if(mCurrentLocation != null) {
                    String markerTitle = getCurrentAddress(mCurrentLocation);
                    String markerSnippet = "위도:" + String.valueOf(mCurrentLocation.getLatitude())
                            + " 경도:" + String.valueOf(mCurrentLocation.getLongitude());

                    //현재 위치에 마커 생성하고 이동
                    mAddressText.setText(markerTitle);
                    mGoogleMapAddressLine = markerTitle;

                    setCurrentLocation(mCurrentLocation, markerTitle, markerSnippet);
                }
                return true;
            }
        });

        if(mViewMapType == TYPE_SETTING_LOCATION) {
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    JWLog.e("", "onMapClick :");
                    stopLocationUpdates();

                    String markerTitle = getCurrentAddress(latLng);
                    String markerSnippet = "위도:" + String.valueOf(latLng.latitude) + " 경도:" + String.valueOf(latLng.longitude);

                    mCurrentLocation = new Location(getPackageName());
                    mCurrentLocation.setLatitude(latLng.latitude);
                    mCurrentLocation.setLongitude(latLng.longitude);

                    mAddressText.setText(markerTitle);
                    mGoogleMapAddressLine = markerTitle;
                    setCurrentLocation(latLng, markerTitle, markerSnippet);
                }
            });
        }

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if (mMoveMapByUser == true && mRequestingLocationUpdates){
                    JWLog.e("", "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        JWLog.e("","");
        String markerTitle = getCurrentAddress(location);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude()) + " 경도:" + String.valueOf(location.getLongitude());

        setCurrentLocation(location, markerTitle, markerSnippet);

        mCurrentLocation = location;
        stopLocationUpdates();
    }

    public void setCurrentLocation(LatLng latLng, String markerTitle, String markerSnippet) {
        JWLog.e("","");

        mMoveMapByUser = false;
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = latLng;

        //구글맵의 디폴트 현재 위치는 파란색 동그라미로 표시
        //마커를 원하는 이미지로 변경하여 현재 위치 표시하도록 수정해야함.
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        mAddressText.setText(getCurrentAddress(latLng));
        mGoogleMapAddressLine = getCurrentAddress(latLng);

        if ( mMoveMapByAPI ) {
            JWLog.e( "", "setCurrentLocation :  mGoogleMap moveCamera " + latLng.latitude + " " + latLng.longitude ) ;

            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.animateCamera(cameraUpdate, 500, null);
        }
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        JWLog.e("","");

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        setCurrentLocation(currentLatLng, markerTitle, markerSnippet);
    }

    private void stopLocationUpdates() {
        JWLog.e("","stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    public String getCurrentAddress(LatLng latLng) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            JWToast.showToast("지오코더 서비스 사용불가");
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            JWToast.showToast("잘못된 GPS 좌표");
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            JWToast.showToast("주소 미발견");
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    public String getCurrentAddress(Location location) {
        return getCurrentAddress(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public void setDefaultLocation() {

        mMoveMapByUser = false;

        //디폴트 위치, Seoul
        mLat = mLat == 0 ? 37.56 : mLat;
        mLot = mLot == 0 ? 126.97 : mLot;

        LatLng DEFAULT_LOCATION = new LatLng(mLat, mLot);
        String markerTitle = mNickName == null ? "위치정보 가져올 수 없음" : mNickName;
        //String markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        //markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        if(mNickName != null) {
            CircleOptions circle = new CircleOptions().center(DEFAULT_LOCATION) //원점
                    .radius(300)      //반지름 단위 : m
                    .strokeWidth(0f)  //선너비 0f : 선없음
                    .fillColor(Color.parseColor("#66BEEEFF")); //배경색

            mGoogleMap.addCircle(circle);
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void startLocationUpdates() {
        JWLog.e("","");
        if (!checkLocationServicesStatus()) {
            JWLog.e("", "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {
            JWLog.e("", "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        JWLog.e("","");
        if ( mRequestingLocationUpdates == false ) {
            JWLog.e("", "onConnected : 퍼미션 가지고 있음");
            JWLog.e("", "onConnected : call startLocationUpdates");
            startLocationUpdates();
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            JWLog.e("", "onConnected : call startLocationUpdates");
            //startLocationUpdates();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        JWLog.e("","");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        JWLog.e("","");
    }

    private void searchAddress(View view) {
        JWLog.e("","");
        CommonUtil.hideKeyboard(this, view);

        mAddress = mAddressText.getText().toString();

        LatLng homeLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        List<Address> list = null;

        try {
            list = mGeocoder.getFromLocationName( mAddress, 3);
        } catch (Exception e) {
            e.printStackTrace();
            JWLog.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
            JWToast.showToast("입력한 주소를 구글맵에서 변환할 수 없습니다.");
        }

        if (list != null) {
            if (list.size() == 0) {
                JWLog.e("", "해당되는 주소 정보는 없습니다");
                JWToast.showToast("입력한 주소를 구글맵에서 변환할 수 없습니다.");
            } else {
                JWLog.e("", list.get(0).toString());

                homeLatLng = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
                mGoogleMapAddressLine = list.get(0).getAddressLine(0);
            }
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(homeLatLng);
        markerOptions.title("집 주소");
        markerOptions.snippet(mGoogleMapAddressLine);
        mGoogleMap.addMarker(markerOptions);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, 15));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 500, null);
    }
}
