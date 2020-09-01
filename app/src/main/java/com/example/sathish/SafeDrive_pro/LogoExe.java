package com.example.sathish.SafeDrive_pro;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;

/**
 * Created by Sathish on 15-11-2016.
 */

public class LogoExe extends Activity
{
    Thread splashTread;
    static String isFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.logo_layout);

        splashTread = new Thread() {
            @Override
            public void run() {
                try
                {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500)
                    {
                        sleep(100);
                        waited += 100;
                    }
                }
                catch (InterruptedException e)
                {
                    Log.w("InterruptedException", e);
                }
                finally
                {

                    SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(LogoExe.this);

                    isFirstTime = app_preferences.getString("isFirstTime","yes");
                    Intent intent;
                    switch(isFirstTime)
                    {
                        case "consumer":
                            //customer app open directly
                            intent = new Intent(LogoExe.this, CustomerTabMain.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            break;
                        case "saviour":
                            //saviour app open directly
                            intent = new Intent(LogoExe.this, SaviourMainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            break;
                        default:
                            //implement your first time logic
                            intent = new Intent(LogoExe.this, FirstActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            break;
                    }
                    LogoExe.this.finish();
                }
            }
        };
        splashTread.start();
    }
}
