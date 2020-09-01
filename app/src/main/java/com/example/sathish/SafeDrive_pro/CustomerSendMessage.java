package com.example.sathish.SafeDrive_pro;

import android.app.Activity;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sathish on 13-11-2016.
 */

public class CustomerSendMessage extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    protected String message,contact1,contact2,contact3,name,age,blood,adrno,id;
    protected double longitude,latitude;
    protected String address, city, state, country, postalCode, location;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(!CustomerDriveStatus.status)
        {
            finish();
        }
        else
        {
            CustomerDatabaseHelper myDb = new CustomerDatabaseHelper(this);
            Cursor res = myDb.getAllData();
            try
            {
                if (res.getCount() == 0)
                {
                    Toast.makeText(CustomerSendMessage.this, " No Data Found ", Toast.LENGTH_LONG).show();
                    finish();
                }

                while (res.moveToNext())
                {
                    name=res.getString(0)+" "+res.getString(1);
                    age=res.getString(2);
                    blood=res.getString(3);
                    id=res.getString(6);
                    adrno=res.getString(8);
                    contact1 = res.getString(9);
                    contact2 = res.getString(10);
                    contact3 = res.getString(11);

                    message="Name: "+name+"\nDOB:" +age+"\nBlood group: "+blood+"\nAadhar No.: "+adrno+"\n";
                }
            }
            catch (Exception e)
            {
                Log.w("Exception: ", e);
            }

            if (mGoogleApiClient == null)
            {
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint)
    {

        try
        {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null)
            {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }
            else
            {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
            }

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Thread.sleep(5000);

            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            location = this.address + ", " + this.city + ", " + this.state + ", " + this.country + "- " + this.postalCode;

            message +="Acciden location: "+location+"\n\nlat,lon: ("+latitude+","+longitude+")";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            sendSMS();
        }
        catch (SecurityException e)
        {
            Log.w("SecurityException", e);
        }
        catch (IOException e)
        {
            Log.w("IOException", e);
        }
        catch (Exception e)
        {
            Log.w("Exception", e);
        }
        finish();
    }

    public void sendSMS()
    {
        try
        {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage("+91"+contact1, null, parts, null, null);
            smsManager.sendMultipartTextMessage("+91"+contact2, null, parts, null, null);
            smsManager.sendMultipartTextMessage("+91"+contact3, null, parts, null, null);
            Toast.makeText(this, "message sent to \n" +contact1+"\n"+contact2+"\n"+contact3, Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Message faild, please later", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        new CustomerUpdateLocationOnline(id,latitude, longitude).execute();
        CustomerDriveStatus.setStatus(false);
    }
    protected void onStart()
    {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
}
