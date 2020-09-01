package com.example.sathish.SafeDrive_pro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by Sathish on 15-11-2016.
 */

public class CustomerCallCheck extends BroadcastReceiver
{
    protected String message="Please call back later.The subscriber is on drive...";
    protected String number;
    Cursor res;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")&& CustomerDriveStatus.status)
        {
            CustomerDatabaseHelper db=new CustomerDatabaseHelper(context);
            res=db.getAllFav();
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String buffer="";
            while (res.moveToNext())
            {
                buffer+="+91"+ res.getString(1)+ " ";
            }

            if(!buffer.contains(""+number))
            {
                blockCall(context);
                sendSMS(context);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void blockCall(Context context)
    {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        try
        {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void sendSMS(Context context)
    {
        try
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            Toast.makeText(context, "Message Sent to "+number, Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}