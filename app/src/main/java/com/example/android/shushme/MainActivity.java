package com.example.android.shushme;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Constants
    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 111;

    // Member variables
    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    /**
     * Called when the activity is starting
     *
     * @param savedInstanceState The Bundle that contains the data supplied in onSaveInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        // COMPLETED (4) Create a GoogleApiClient with the LocationServices API and GEO_DATA_API
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google Api Client Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Api Client Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Google Api Client Connection Failed");
    }

    // COMPLETED (5) Override onConnected, onConnectionSuspended and onConnectionFailed for GoogleApiClient
    // COMPLETED (7) Override onResume and inside it initialize the location permissions checkbox
    // COMPLETED (8) Implement onLocationPermissionClicked to handle the CheckBox click event
    // COMPLETED (9) Implement the Add Place Button click event to show  a toast message with the permission status


    @Override
    protected void onResume() {
        super.onResume();

        CheckBox permissionCheckBox = findViewById(R.id.permission_checkbox);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionCheckBox.setChecked(true);
            permissionCheckBox.setEnabled(false);
        } else {
            permissionCheckBox.setChecked(false);
        }

    }

    public void onLocationPermissionClicked(View view) {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

    }

    public void onAddLocationClicked(View view) {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, getString(R.string.need_permission), Toast.LENGTH_SHORT)
                    .show();
        }

    }
}
