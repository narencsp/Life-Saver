package com.example.sathish.SafeDrive_pro;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CustomerMainActivity extends Fragment
{
    Button start;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_main_customer, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("Toolbar");
        toolbar.inflateMenu(R.menu.menu_first);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.about:
                        return true;//add action
                    case R.id.contact:
                        return true;//add action
                    case R.id.faq:
                        return true;//add action
                    default:
                        return false;
                }
            }
        });

        statusCheck();

        start=(Button)rootView.findViewById(R.id.Start);

        if(!CustomerDriveStatus.status)
            start.setText("ON DRIVE");
        else
            start.setText("OFF DRIVE");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(start.getText().toString());
                if(start.getText().toString().equals("OFF DRIVE"))
                {
                    start.setText("ON DRIVE");
                    CustomerDriveStatus.setStatus(false);
                    //removeCustomerDriveStatus
                    Intent intent = new Intent(getActivity(), CustomerTabMain.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                else if(start.getText().toString().equals("ON DRIVE"))
                {
                    start.setText("OFF DRIVE");
                    CustomerDriveStatus.setStatus(true);
                    if(checkAndRequestPermissions())
                    {
                        //addCustomerDriveStatus
                        Intent intent = new Intent(getActivity(), CustomerTabMain.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                    else
                    {
                        onClick(view);
                    }
                }
                getActivity().finish();
            }
        });
        final Button updt,fav,viwLoc;
        updt = (Button)rootView.findViewById(R.id.Update);
        updt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {update(view);}});

        fav = (Button)rootView.findViewById(R.id.Favorite);
        fav.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {favorite(view);}});

        viwLoc = (Button)rootView.findViewById(R.id.VwLoc);
        viwLoc.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {viewLocation(view);}});

        return rootView;
    }

    public void update(View view)
    {
        Intent intent = new Intent(getActivity(), CustomerUpdateReg.class);
        startActivity(intent);
    }

    public void favorite(View view)
    {
        Intent intent = new Intent(getActivity(), CustomerFavorite.class);
        startActivity(intent);
    }

    public void viewLocation(View view)
    {
        Intent intent = new Intent(getActivity(), CustomerViewMap.class);
        startActivity(intent);
    }

    private  boolean checkAndRequestPermissions()
    {
        int network = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET);
        int call = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
        int phnst = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE);
        int sms = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS);
        int loc = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (network != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }
        if (call != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.CALL_PHONE);
        }
        if (phnst != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (sms != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.SEND_SMS);
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
            ActivityCompat.requestPermissions(getActivity(),listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void statusCheck()
    {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            displayLocationSettingsRequest(getActivity());
        }

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            Toast.makeText(getActivity(), "No Internet Connection ", Toast.LENGTH_LONG).show();
            //createNetErrorDialog();
        }
    }

    private void displayLocationSettingsRequest(Context context)
    {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
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
            public void onResult(LocationSettingsResult result)
            {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try
                        {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), 1);
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

    /*protected void createNetErrorDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You need internet connection for this app. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivity(i);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                getActivity().finish();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
    }*/
}
