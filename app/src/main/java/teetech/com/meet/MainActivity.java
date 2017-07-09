package teetech.com.meet;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener,OnMapReadyCallback
{
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;


    protected TextView title;
    protected TextView meetingDetails,time,date,venue;

    //private double lan=3.280167,lat=6.581773;
    private double lan=0.0,lat=0.0;

    private GoogleMap map;

    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        meetingDetails = (TextView)findViewById(R.id.meeting_details);
        time = (TextView)findViewById(R.id.time);
        date = (TextView)findViewById(R.id.date);
        venue = (TextView)findViewById(R.id.venue);


        title = (TextView) findViewById((R.id.meeting_title));



        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        else
        {
            mGoogleApiClient.connect();
            Toast.makeText(this, "API not null", Toast.LENGTH_LONG).show();
        }
    }

    protected void onStart()
    {
        try
        {
            SharedPreferences meetingData = getApplicationContext().getSharedPreferences("meeting_data", 0);

            lat = Double.parseDouble(meetingData.getString("Lat",null));
            lan = Double.parseDouble(meetingData.getString("Lon",null));

            title.setText(meetingData.getString("title",null));
            date.setText(meetingData.getString("date",null));
            venue.setText(meetingData.getString("venue",null));
            time.setText(meetingData.getString("time",null));
            meetingDetails.setText(meetingData.getString("details",null));

        }
        catch (Exception e)
        {
            Log.e("SharedPref",e.getMessage());
        }
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop()
    {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle)
    {

        registerRequestUpdate(this);
        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //
            //ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },LocationServices.MY_PERMISSION_ACCESS_COARSE_LOCATION );
            Toast.makeText(this, "First enable LOCATION ACCESS in settings.", Toast.LENGTH_LONG).show();
            return;

        }*/

        try
        {
            /*mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            lat = mLocation.getLatitude();
            lan = mLocation.getLongitude();*/

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        catch (SecurityException e)
        {
            //dialogGPS(this.getApplication());
            Log.e("GPS-Error",e.getMessage());
        }


    }

    @Override
    public void onConnectionSuspended(int i)
    {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Toast.makeText(this, "connection failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        lat = location.getLatitude();
        lan = location.getLongitude();

       /* mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel,lat));
        mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel,lan));

        mLatitudeText.invalidate();
        mLongitudeText.invalidate();*/

       // Toast.makeText(this, "new Location Fix", Toast.LENGTH_LONG).show();
    }

    public void registerRequestUpdate(final LocationListener listener)
    {

        mLocationRequest = LocationRequest.create();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationRequest.setInterval(1000); // every second

        new Handler().postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
                    Toast.makeText(MainActivity.this, "Requesting Location Service", Toast.LENGTH_SHORT).show();
                }
                catch (SecurityException e)
                {
                    Log.e("security error",e.getMessage());
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Log.e("Exception---",e.getMessage());
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    if (!mGoogleApiClient.isConnected())
                    {

                        mGoogleApiClient.connect();

                    }

                    registerRequestUpdate(listener);

                }
            }
        }, 1000);
    }

    @Override
    public void onMapReady(GoogleMap map)
    {


        try
        {
           /* map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

            map.addMarker(new MarkerOptions()
                    .title("Sydney")
                    .snippet("The most populous city in Australia.")
                    .position(sydney));*/

            map.setMyLocationEnabled(true);
            LatLng sydney = new LatLng(lat, lan);
            map.addMarker(new MarkerOptions().position(sydney).title("Meeting Venue"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16 ));
            map.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                @Override
                public void onCircleClick(Circle circle)
                {
                    circle.setFillColor(Color.GREEN);
                }
            });
            map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
        }
        catch (SecurityException e)
        {
            Log.e("map-exception",e.getMessage());
        }

    }
}