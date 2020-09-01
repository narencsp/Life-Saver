package com.example.sathish.SafeDrive_pro;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sathish on 14-11-2016.
 */

public class CustomerViewMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        checkAndRequestPermissions();
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
        //remove previous current location Marker
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        //Toast.makeText(this, "Latitude:"+latitude+"\nLongitude: "+longitude, Toast.LENGTH_LONG).show();

        LatLng loc = new LatLng(latitude, longitude);
        if (marker != null)
        {
            marker.remove();
        }
        else
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,16));

        marker = map.addMarker(new MarkerOptions().position(loc).title(latitude+" , "+longitude));
        map.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }

    @Override
    protected void onDestroy()
    {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    public void quit(View view)
    {
        finish();
    }

    private  boolean checkAndRequestPermissions()
    {
        int network = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        int loc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

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
}