package com.example.sathish.SafeDrive_pro;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Sathish on 18-11-2016.
 */

public class CustomerHelpAlarm extends FragmentActivity implements View.OnClickListener
{

    Button btn;
    int i=0, second = 30;
    MediaPlayer eMediaplayer;
    Vibrator vibrate;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        //display on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //alarm
        eMediaplayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        eMediaplayer.start();

        //vibrate
        vibrate = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 500};
        vibrate.vibrate(pattern, 0);

        setContentView(R.layout.alarm_layout);

        btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener( this );

        new Thread( new Runnable()
        {
            public void run()
            {
                while( i < second )
                {
                    try
                    {
                        handle.sendMessage( handle.obtainMessage());
                        Thread.sleep(1000);
                    }
                    catch(Throwable t)
                    {
                        t.printStackTrace();
                    }
                }
            }

            Handler handle = new Handler()
            {
                public void handleMessage( Message msg)
                {
                    btn.setText( "Cancel alarm "+ second );
                    System.out.println("Cancel alarm "+ second);
                    second--;
                }
            };
        }).start();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                eMediaplayer.release();
                vibrate.cancel();
                if(second==0&&CustomerDriveStatus.status)
                {
                    Intent intent = new Intent(CustomerHelpAlarm.this, CustomerSendMessage.class);
                    startActivity(intent);
                    btn.setText("Notification sent");
                }
                finish();
            }
        }, 30000);
    }

    @Override
    public void onClick(View v)
    {
        eMediaplayer.release();
        vibrate.cancel();
        CustomerDriveStatus.setStatus(false);
        second=-1;
        finish();
    }
}