package com.example.sathish.SafeDrive_pro;

/**
 * Created by Sathish on 10-12-2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class CustomerDriveStatus extends Fragment implements SensorEventListener
{
    public static Boolean status=false;

    float gravity[]=new float[3];
    float linear_acceleration[]=new float[3];
    float mAccelCurrent,mAccelLast;
    int flag=0,apCount=0,t=1;

    //Button btn;
    TextView txt;

    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;

    SensorManager sensorManager;
    GraphView graph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.graph_layout, container, false);

        super.onCreate(savedInstanceState);

        checkAndRequestPermissions();

        /*btn = (Button)rootView.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {quit(view);}});*/

        txt = (TextView)rootView.findViewById(R.id.change);

        graph = (GraphView) rootView.findViewById(R.id.graph);
        series = new LineGraphSeries<>();
        Viewport viewport = graph.getViewport();
        viewport.setXAxisBoundsManual(true);
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(30);

        sensorManager =(SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(CustomerDriveStatus.this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        return rootView;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        final float alpha = (float) 0.8;
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        check();
    }

    @Override
    public void onDestroy()
    {
        sensorManager.unregisterListener(this);
        super.onDestroy();
        getActivity().finish();
    }

    public void check()
    {
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt(((linear_acceleration[0]*linear_acceleration[0])+(linear_acceleration[1]*linear_acceleration[1])+(linear_acceleration[2]*linear_acceleration[2])));
        txt.setText("Motion:" + mAccelCurrent);

        float delta = mAccelCurrent - mAccelLast;

        addEntry();

        graph.addSeries(series);

        System.out.println("\n"+delta+" "+ flag);

        if(delta > 6||delta < -6)
        {
            flag++;
            t=1;
        }
        else if (t>2)
        {
            t=1;
            flag=0;
        }
        else
            t++;

        if(flag>5)
        {
            flag=0;
            Intent intent = new Intent(getActivity(), CustomerHelpAlarm.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            onDestroy();
        }
    }

    public static void setStatus(Boolean stus)
    {
        System.out.println("Set Status "+stus);
        status=stus;
    }

    private void addEntry()
    {
        apCount++;
        series.appendData(new DataPoint(lastX++,mAccelCurrent), true, 5);
        if(apCount==30)
        {
            series = new LineGraphSeries<>();
            apCount=0;
        }
    }

    /*public void quit(View view)
    {
        CustomerDriveStatus.setStatus(false);
        onDestroy();
    }*/

    public boolean checkAndRequestPermissions()
    {
        List<String> listPermissionsNeeded = new ArrayList<>();

        int sen = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.BODY_SENSORS);
        int vib = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.VIBRATE);
        int aud = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.MODIFY_AUDIO_SETTINGS);

        if (sen != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.BODY_SENSORS);
        }
        if (vib != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.VIBRATE);
        }
        if (aud != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(android.Manifest.permission.MODIFY_AUDIO_SETTINGS);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(getActivity(),listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),1);
            return false;
        }
        return true;
    }
}
