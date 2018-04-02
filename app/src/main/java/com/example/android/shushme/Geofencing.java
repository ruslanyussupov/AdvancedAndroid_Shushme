package com.example.android.shushme;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Geofencing implements ResultCallback {

    private static final String LOG_TAG = Geofencing.class.getSimpleName();
    private static final long EXP_DURATION = TimeUnit.DAYS.toMillis(1L);
    private static final float RADIUS = 5f;

    private GoogleApiClient mClient;
    private Context mContext;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    public Geofencing(Context context, GoogleApiClient client) {
        this.mClient = client;
        this.mContext = context;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();
    }

    public void registerAllGeofences() {
        if (mClient == null || !mClient.isConnected() ||
                mGeofenceList == null || mGeofenceList.isEmpty()) {
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(mClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()).setResultCallback(this);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public void unregisterAllGeofences() {
        if (mClient == null || !mClient.isConnected()) {
            return;
        }

        try {
            LocationServices.GeofencingApi.removeGeofences(mClient, getGeofencePendingIntent())
                    .setResultCallback(this);
        } catch (SecurityException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public void updateGeofencesList(PlaceBuffer places) {

        if (places == null || places.getCount() == 0) {
            return;
        }

        mGeofenceList = new ArrayList<>(places.getCount());
        for (Place place : places) {
            String placeId = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLng = place.getLatLng().longitude;

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeId)
                    .setExpirationDuration(EXP_DURATION)
                    .setCircularRegion(placeLat, placeLng, RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();

            mGeofenceList.add(geofence);
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(mGeofenceList)
                .build();
    }

    public PendingIntent getGeofencePendingIntent() {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;

    }

    @Override
    public void onResult(@NonNull Result result) {

    }
}
