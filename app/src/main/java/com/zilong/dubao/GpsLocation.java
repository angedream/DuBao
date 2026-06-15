package com.zilong.dubao;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class GpsLocation {
    Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    String TAG="GpsLocation";
    GpsLocation(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // 更新UI，处理位置数据
                Toast.makeText(context, "Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "Provider " + provider + " status changed to " + status);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                Log.d(TAG, "Provider " + provider + " enabled");
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.d(TAG, "Provider " + provider + " disabled");
            }
        };


    }





    public void startLocationUpdates() {
        try {
            @SuppressLint("MissingPermission") Location lc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isNetworkEnabled) {
                Log.d(TAG, "0000000000000000000000");


                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6000, 10, locationListener);
            }
            if (isGPSEnabled) {
                Log.d(TAG, "1111111111111111111111");
//                Toast.makeText(context, "00000000000000000000000000000", Toast.LENGTH_LONG).show();

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10, locationListener);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}


