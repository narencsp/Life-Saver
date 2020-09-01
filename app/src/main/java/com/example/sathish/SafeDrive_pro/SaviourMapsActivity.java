
package com.example.sathish.SafeDrive_pro;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sathish on 23-11-2016.
 */

public class SaviourMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    private GoogleMap map=null;
    private GoogleApiClient mGoogleApiClient;
    LatLng sorLoc,desLoc;
    private Marker source_marker=null,destination_marker=null;
    Polyline line=null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
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

        //new SaviourUpdateLocation(SaviourMainActivity.id, latitude, longitude, SaviourMainActivity.avil).execute();

        Log.d("-->", SaviourUpdateLocation.locat);
        String res[] = SaviourUpdateLocation.locat.split(",");
        //String cid=res[0];
        Double clat = Double.parseDouble(res[1]);
        Double clog = Double.parseDouble(res[2]);

        desLoc = new LatLng(clat, clog);

        // Getting URL to the Google Directions API
        String url = getUrl(sorLoc, desLoc);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);

        //move map camera
        //map.moveCamera(CameraUpdateFactory.newLatLng(sorLoc));
    }

    @Override
    protected void onDestroy()
    {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    public void quit(View view)
    {
        SaviourUpdateLocation.locat=null;
        finish();
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        return ("https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters);
    }



    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try
            {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            }
            catch (Exception e)
            {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }

        private String downloadUrl(String strUrl) throws IOException
        {
            String data = "";
            InputStream iStream;
            HttpURLConnection urlConnection;
            try
            {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data);
                br.close();

            }
            catch (Exception e)
            {
                Log.d("Exception", e.toString());
            }
            return data;
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
    {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try
            {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask",routes.toString());

            }
            catch (Exception e)
            {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result)
        {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            try {
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.destination_marker_icon);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 30, 40, false);

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null)
            {
                if(line!=null && source_marker!=null && destination_marker!=null)
                {
                    source_marker.remove();
                    line.remove();
                    destination_marker.remove();
                }
                source_marker = map.addMarker(new MarkerOptions().position(sorLoc).title("Source"));//.icon(BitmapDescriptorFactory.fromResource(R.drawable.source_marker_icon)));
                line = map.addPolyline(lineOptions);
                destination_marker=map.addMarker(new MarkerOptions().position(desLoc).title("Destination").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }
        }
    }

    private class DataParser
    {

        /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
        List<List<HashMap<String,String>>> parse(JSONObject jObject)
        {
            List<List<HashMap<String, String>>> routes = new ArrayList<>() ;
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /* Traversing all routes */
                for(int i=0;i<jRoutes.length();i++){
                    jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<>();

                    /* Traversing all legs */
                    for(int j=0;j<jLegs.length();j++){
                        jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                        /* Traversing all steps */
                        for(int k=0;k<jSteps.length();k++){
                            String polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            /* Traversing all points */
                            for(int l=0;l<list.size();l++){
                                HashMap<String, String> hm = new HashMap<>();
                                hm.put("lat", Double.toString((list.get(l)).latitude) );
                                hm.put("lng", Double.toString((list.get(l)).longitude) );
                                path.add(hm);
                            }
                        }
                        routes.add(path);
                    }
                }

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return routes;
        }


        /**
         * Method to decode polyline points
         * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         * */
        private List<LatLng> decodePoly(String encoded)
        {

            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len)
            {
                int b, shift = 0, result = 0;
                do
                {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do
                {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }
    }
}