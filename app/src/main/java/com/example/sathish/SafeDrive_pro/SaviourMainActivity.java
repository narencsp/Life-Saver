package com.example.sathish.SafeDrive_pro;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Sathish on 18-02-2017.
 */

public class SaviourMainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    private GoogleMap map=null;
    LatLng sorLoc;
    public static String id=null;
    public static int avil=0;
    Button status;
    GoogleApiClient mGoogleApiClient;
    private Marker source_marker=null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        checkAndRequestPermissions();
        statusCheck();
        setContentView(R.layout.activity_main_saviour);

        SaviourDatabaseHelper myDb = new SaviourDatabaseHelper(this);
        Cursor res = myDb.getAllData();

        if (res.getCount() != 0)
        {
            while (res.moveToNext())
            {
                id=res.getString(4);
            }
        }

        status =(Button) findViewById(R.id.status);
        status.setText("Savior Mode On");
        SaviourUpdateLocation.locat=null;
        avil=0;

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(status.getText().toString());
                if(status.getText().toString().equals("Savior Mode Off"))
                {
                    status.setText("Savior Mode On");
                    SaviourUpdateLocation.locat=null;
                    avil=0;
                }
                else if(status.getText().toString().equals("Savior Mode On"))
                {
                    status.setText("Savior Mode Off");
                    avil=1;
                }
            }
        });

        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        if (map == null)
        {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap retMap)
    {
        map = retMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try
        {
            map.setMyLocationEnabled(true);
        }
        catch (SecurityException e)
        {
            Log.w("SecurityException", e);
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch (SecurityException e)
        {
            Log.w("SecurityException", e);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@Nullable ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        sorLoc = new LatLng(latitude, longitude);

        if(source_marker!=null)
        {
            source_marker.remove();
        }
        else
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sorLoc,16));

        source_marker = map.addMarker(new MarkerOptions().position(sorLoc).title("Source"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sorLoc));

        new SaviourUpdateLocation(id, latitude, longitude, avil).execute();

        if(SaviourUpdateLocation.locat!=null)
        {
            mGoogleApiClient.disconnect();
            status.setText("Savior Mode On");
            avil=0;
            Intent intent = new Intent(SaviourMainActivity.this, SaviourMapsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy()
    {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    public void update(View view)
    {
        Intent intent = new Intent(SaviourMainActivity.this, SaviourUpdateReg.class);
        startActivity(intent);
    }

    boolean checkAndRequestPermissions()
    {
        List<String> listPermissionsNeeded = new ArrayList<>();
        int gser = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_GSERVICES);
        int network = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (gser != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_GSERVICES);
        }
        if (network != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }
        if (loc2 != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }

    public void statusCheck()
    {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            displayLocationSettingsRequest(this);
        }

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null)
        {
            /*if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                Toast.makeText(this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                Toast.makeText(this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            }*/
        }
        else
        {
            Toast.makeText(this, "No Internet Connection ", Toast.LENGTH_LONG).show();
            //createNetErrorDialog();
        }
    }

    private void displayLocationSettingsRequest(Context context)
    {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@Nullable LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                switch (status.getStatusCode())
                {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try
                        {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(SaviourMainActivity.this, 1);
                        }
                        catch (IntentSender.SendIntentException e)
                        {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
}