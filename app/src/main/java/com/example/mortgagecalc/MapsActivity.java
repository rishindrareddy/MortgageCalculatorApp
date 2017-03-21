package com.example.mortgagecalc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DatabaseHelper dh = new DatabaseHelper(this);
        LatLng point;
        List<Marker> markers = new ArrayList<Marker>();
        int padding = 0; // offset from edges of the map in pixels
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        if(mMap != null){

            if (marker != null) {
                marker.remove();
            }

            Cursor cursor = dh.getAllData();
            if (cursor.moveToFirst()){
                do{
                    String data = cursor.getString(2) + " " + cursor.getString(3)+ " "+ cursor.getString(4)+" "+ cursor.getString(5);
                    point = getLocationFromAddress(data);
                    System.out.println("LatLong: "+ point);

                    MarkerOptions options = new MarkerOptions()
                            .title(cursor.getString(3) +" " +cursor.getString(4))
                            .position(point);

                    marker = mMap.addMarker(options);
                    markers.add(marker);

                } while(cursor.moveToNext());

                for (Marker m : markers) {
                    builder.include(m.getPosition());
                }
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
                mMap.animateCamera(cu);
            }

        }

    }


    public LatLng getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            System.out.println("address: "+ address);
            if (address.size() == 0) {
                return null;
            }
            Address location= address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude() , location.getLongitude());
        }
        catch (IOException ex) {

            ex.printStackTrace();
        }
        return p1;
    }
    }


