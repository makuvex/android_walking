package com.friendly.walking.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import com.friendly.walking.util.JWLog;

import static android.support.v4.content.ContextCompat.checkSelfPermission;


public class PermissionManager {

	public static final int 			LOCATION_PERMISSION_REQUEST_CODE = 100;
	public static final int 			STORAGE_PERMISSION_REQUEST_CODE = 101;
	public static final int 			CAMERA_PERMISSION_REQUEST_CODE = 102;

	public static String				mLastDeniedPermission = null;

	public static boolean isAcceptedLocationPermission(Context context) {
		return isAcceptedPermission(context, "android.permission.ACCESS_FINE_LOCATION");
	}

	public static boolean isAcceptedStoragePermission(Context context) {
		return isAcceptedPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") &&
				isAcceptedPermission(context, "android.permission.READ_EXTERNAL_STORAGE");
	}

	public static boolean isAcceptedCameraPermission(Context context) {
		return isAcceptedPermission(context, "android.permission.CAMERA");
	}

	public static boolean isAcceptedPermission(Context context, String permission) {
		boolean checkPermission = true;
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {
			return true;
		}
		if(checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
			JWLog.d("", "" + permission + " denied");
			checkPermission = false;
		}

		return checkPermission;
	}

	public static void requestLocationPermission(Activity activity) {
		requestPermission(activity, new String[] {"android.permission.ACCESS_FINE_LOCATION"}, LOCATION_PERMISSION_REQUEST_CODE);
	}

	public static void requestCameraPermission(Activity activity) {
		requestPermission(activity,
				new String[] {"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"},
				CAMERA_PERMISSION_REQUEST_CODE);
	}

	public static void requestStoragePermission(Activity activity) {
		requestPermission(activity,
				new String[] {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"},
				STORAGE_PERMISSION_REQUEST_CODE);
	}

	public static void requestPermission(final Activity activity, String[] permissions, int requestCode) {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {
			return ;
		}
		ActivityCompat.requestPermissions(activity, permissions, requestCode);
	}

	public static void goPermissionSettingMenu(final Activity context) {
		if (context == null) {
			return;
		}
		final Intent i = new Intent();
		i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		i.addCategory(Intent.CATEGORY_DEFAULT);
		i.setData(Uri.parse("package:" + context.getPackageName()));
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		context.startActivity(i);
	}
}
