package com.friendly.walking.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.friendly.walking.util.JWToast;

import com.friendly.walking.BuildConfig;
import com.friendly.walking.R;
import com.friendly.walking.util.JWLog;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


/**
 * Created by jungjiwon on 2017. 12. 13..
 */

public class GeofenceManager implements OnCompleteListener<Void> {

    private static GeofenceManager                      mSelf;
    private Context                                     mContext;

    private GeofencingClient                            mGeofencingClient;
    private ArrayList<Geofence>                         mGeofenceList;
    private PendingIntent                               mGeofencePendingIntent;
    private PendingGeofenceTask                         mPendingGeofenceTask = PendingGeofenceTask.NONE;

    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    public interface GeofenceManagerCallback {
        public void onCompleted(boolean result, Object object);
    }

    public static GeofenceManager getInstance(AppCompatActivity activity) {
        if(mSelf == null) {
            mSelf = new GeofenceManager(activity);
        }
        mSelf.mContext = activity;

        return mSelf;
    }

    public static GeofenceManager getInstance(Context context) {
        if(mSelf == null) {
            mSelf = new GeofenceManager(context);
        }
        mSelf.mContext = context;

        return mSelf;
    }

    private GeofenceManager(Context context) {
        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
        mGeofencingClient = LocationServices.getGeofencingClient(context);
        //populateGeofenceList();

    }

    public void populateGeofenceList(String address, String lat, String lot) {

        for(Geofence geo : mGeofenceList) {
            if(geo.getRequestId().equalsIgnoreCase(address)) {
                JWLog.e("동일한 주소로 지오펜스가 이미 등록되어 있습니다.");
                //Toast.makeText(mContext, "동일한 주소로 지오펜스가 이미 등록되어 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(address)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        Double.parseDouble(lat),
                        Double.parseDouble(lot),
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Constants.GEOFENCE_NEVER_EXPIRE)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build());

        JWLog.e("populateGeofenceList:"+address);

    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressWarnings("MissingPermission")
    public void addGeofences() {
        JWLog.e("addGeofences");
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    /**
     * Removes geofences. This method should be called after the user has granted the location
     * permission.
     */
    public void removeGeofences() {
        JWLog.e("removeGeofences");
        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Runs when the result of calling {@link #addGeofences()} and/or {@link #removeGeofences()}
     * is available.
     * @param task the resulting Task, containing either a result or error.
     */
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
            if(BuildConfig.IS_DEBUG) {
                int messageId = getGeofencesAdded() ? R.string.geofences_added : R.string.geofences_removed;
                if (messageId == R.string.geofences_added) {
                    JWToast.showToast(mContext.getString(messageId));
                }
            }
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(mContext, task.getException());
            JWLog.e(errorMessage);
        }
    }

    /**
     * Returns true if geofences were added, otherwise false.
     */
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

    /**
     * Stores whether geofences were added ore removed in {@link SharedPreferences};
     *
     * @param added Whether geofences were added or removed.
     */
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }




}
