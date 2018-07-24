package com.example.arrow.sigstrength;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import static android.location.LocationManager.NETWORK_PROVIDER;


public class LocationService implements ActivityCompat.OnRequestPermissionsResultCallback {
    private LocationManager locationManager;
    private String provider;
    private Context context;
    private List<String> providers;
    private Location location;

    public LocationService(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        providers = locationManager.getProviders(true);
//        Log.d("providers", String.valueOf(providers));
//        if (providers.contains(LocationManager.GPS_PROVIDER)) {
//            //是否为GPS位置控制器
//            Log.d("provider1","gps") ;
//            provider = LocationManager.GPS_PROVIDER;
//        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
//            //是否为网络位置控制器
//            Log.d("provider2","network") ;
//            provider = LocationManager.NETWORK_PROVIDER;
//
//        } else {
//            Toast.makeText(context, "请检查网络或GPS是否打开", Toast.LENGTH_LONG).show();
//        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
        provider = NETWORK_PROVIDER;
        location = locationManager.getLastKnownLocation(provider);

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void startRequestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
        locationManager.requestLocationUpdates(provider, 5000, 3, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setLocation(location);
                return;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101: {
                // 授权被允许
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("-------->", "授权请求被允许");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.e("-------->", "授权请求被拒绝");
                }
                return;
            }
        }
    }
}
