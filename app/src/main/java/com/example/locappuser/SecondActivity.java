package com.example.locappuser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SecondActivity extends AppCompatActivity implements OnMapReadyCallback {

    SharedPreferences pref;
    TextView locationTextView;
    private GoogleMap mMap;
    private RadioGroup mapTypeRBG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setComponents();
        setLocationLabel();
        setMap();
        setMapTypeRadioButtonGroup();

    }

    private void setMapTypeRadioButtonGroup() {
        mapTypeRBG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.normalMapRB){
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                else if(i==R.id.urbanTypeMapRB){
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });
    }

    private void setLocationLabel() {
        String longtitude,lattitude;
        if((longtitude=pref.getString("lattitude",null))!=null && (lattitude=pref.getString("longtitude",null))!=null){
            locationTextView.setText(longtitude +" "+ lattitude);
        }
        else
            Log.i("DEBUG","An error occurred while writing the location. Shared Preferences are empty.");
    }

    private void setMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setComponents() {
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        locationTextView=findViewById(R.id.locationTextView);
        mapTypeRBG=findViewById(R.id.mapTypeRBG);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.parseDouble(pref.getString("longtitude",null)),Double.parseDouble(pref.getString("lattitude",null)) );
        mMap.addMarker(new MarkerOptions().position(sydney).title("Öğrenci Burada"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
