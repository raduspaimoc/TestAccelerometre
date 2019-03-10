package com.example.mapa;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity
implements SeekBar.OnSeekBarChangeListener, OnMapReadyCallback, GoogleMap.OnGroundOverlayClickListener {

    //private GoogleMap mMap;
    private static final int TRANSPARENCY_MAX = 100;

    private static final LatLng NEWARK = new LatLng(40.714086, -74.228697);

    private static final LatLng NEAR_NEWARK =
            new LatLng(NEWARK.latitude - 0.001, NEWARK.longitude - 0.025);

    private static final LatLng POLIVALENT = new LatLng(41.6082387,0.6212267); //41.6112161,0.6177565,16
    private static final LatLng NEAR_POLIVALENT = new LatLng(POLIVALENT.latitude - 0.001, POLIVALENT.longitude - 0.025);
    private static final LatLng RECTORAT = new LatLng(41.614592, 0.6197898); //41.6112161,0.6177565,16
    private static final LatLng MEDICINA = new LatLng(41.6193732,0.613702);

    private final List<BitmapDescriptor> mImages = new ArrayList<BitmapDescriptor>();

    private GroundOverlay mGroundOverlay;

    private GroundOverlay mGroundOverlayRotated;

    private SeekBar mTransparencyBar;

    private int mCurrentEntry = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);


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
    public void onMapReady(GoogleMap map) {

        map.setOnGroundOverlayClickListener(this);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(POLIVALENT, 11));

        mImages.clear();
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.capont1));
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.capont2));
        mImages.add(BitmapDescriptorFactory.fromResource(R.drawable.capont3));

        map.addMarker(new MarkerOptions()
        .position(POLIVALENT)
        .title("POLIVALENT")
        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.capont1))
        .infoWindowAnchor(0.5f, 0.5f));

        //41.6938394,0.5949571
        map.addMarker(new MarkerOptions()
                .position(RECTORAT)
                .title("Rectorat")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.capont1))
                .infoWindowAnchor(0.5f, 0.5f));

        map.addMarker(new MarkerOptions()
                .position(MEDICINA)
                .title("MEDICINA")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.capont1))
                .infoWindowAnchor(0.5f, 0.5f));

        Polyline line = map.addPolyline(new PolylineOptions()
                .add(MEDICINA, POLIVALENT)
                .width(5)
                .color(Color.GREEN));

        Polyline line1 = map.addPolyline(new PolylineOptions()
                .add(RECTORAT, MEDICINA)
                .width(5)
                .color(Color.GREEN));

        Polyline line2 = map.addPolyline(new PolylineOptions()
                .add(POLIVALENT, RECTORAT)
                .width(5)
                .color(Color.GREEN));


        // Add a small, rotated overlay that is clickable by default
        // (set by the initial state of the checkbox.)
        mGroundOverlayRotated = map.addGroundOverlay(new GroundOverlayOptions()
                .image(mImages.get(1)).anchor(0, 1)
                .position(NEAR_POLIVALENT, 4300f, 3025f)
                .bearing(30)
                .clickable(((CheckBox) findViewById(R.id.toggleClickability)).isChecked()));

        // Add a large overlay at Newark on top of the smaller overlay.
        mGroundOverlay = map.addGroundOverlay(new GroundOverlayOptions()
                .image(mImages.get(mCurrentEntry)).anchor(0, 1)
                .position(POLIVALENT, 8600f, 6500f));

        mTransparencyBar.setOnSeekBarChangeListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        map.setContentDescription("Google Map with ground overlay. Custom");

    }

    /** SeekBarChangeListener */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (mGroundOverlay != null) {
            mGroundOverlay.setTransparency((float) progress / (float) TRANSPARENCY_MAX);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar){}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar){}

    /** GoogleMap.OnGroundOverlayClickListener */
    /**
     * Toggles the visibility between 100% and 50% when a {@link GroundOverlay} is clicked.
     */
    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {
        // Toggle transparency value between 0.0f and 0.5f. Initial default value is 0.0f.
        mGroundOverlayRotated.setTransparency(0.5f - mGroundOverlayRotated.getTransparency());
    }

    /**
     * Toggles the clickability of the smaller, rotated overlay based on the state of the View that
     * triggered this call.
     * This callback is defined on the CheckBox in the layout for this Activity.
     */
    public void toggleClickability(View view) {
        if (mGroundOverlayRotated != null) {
            mGroundOverlayRotated.setClickable(((CheckBox) view).isChecked());
        }
    }

    public void switchImage(View view) {
        mCurrentEntry = (mCurrentEntry + 1) % mImages.size();
        mGroundOverlay.setImage(mImages.get(mCurrentEntry));
    }

}
