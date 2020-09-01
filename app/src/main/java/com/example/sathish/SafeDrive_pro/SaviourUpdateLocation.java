package com.example.sathish.SafeDrive_pro;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Sathish on 11-02-2017.
 */

class SaviourUpdateLocation extends AsyncTask
{
    private String id;
    private Double lat, log;
    private int avil;
    static String locat=null;

    //flag 0 means get and 1 means post.(By default it is get.)
    SaviourUpdateLocation(String id, Double lat, Double log, int avil)
    {
        this.id=id;
        this.lat=lat;
        this.log=log;
        this.avil=avil;
    }

    @Override
    protected String doInBackground(Object[] arg0)
    {
        try
        {
            String link="http://cdans.000webhostapp.com/updateLocation.php";
            String data  = URLEncoder.encode("id", "UTF-8") + "=" +
                    URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("lat", "UTF-8") + "=" +
                    URLEncoder.encode(""+lat, "UTF-8");
            data += "&" + URLEncoder.encode("log", "UTF-8") + "=" +
                    URLEncoder.encode(""+log, "UTF-8");
            data += "&" + URLEncoder.encode("avil", "UTF-8") + "=" +
                    URLEncoder.encode(""+avil, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            Log.d("-->",link+" "+data);

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
            }

            Log.d("-->",sb.toString());

            if(sb.toString().contains("*"))
            {
                locat=sb.toString();
                locat=locat.substring(locat.indexOf("(") + 1, locat.indexOf(")"));
                SaviourMainActivity.avil=0;
                return locat;
            }
            return sb.toString();
        } catch(Exception e){
            return ("Exception: " + e.getMessage());
        }
    }
}