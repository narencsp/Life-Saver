package com.example.sathish.SafeDrive_pro;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.ArrayList;


/**
 * Created by Sathish on 26-02-2018.
 */

public class CustomerTabMain extends AppCompatActivity
{
    static ActionBar actionBar;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.samp_main_c);

        actionBar = getActionBar();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    return new CustomerMainActivity();

                case 1:
                    return new CustomerDriveStatus();
            }
            return null;
        }

        @Override
        public int getCount()
        {
            if(CustomerDriveStatus.status)
            {
                return 2;
            }
            else
            {
                return 1;
            }
        }
    }
}
