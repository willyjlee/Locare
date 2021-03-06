package william_lee.labs.fun.LocationServer;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private MapFragment mapf;
    private Intent starterIntent;

    private boolean mReady;
    private GoogleMap myMap;
    private LatLng latlng;

    private View v;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        starterIntent = getIntent();
        mapf = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_container, mapf);
        fragmentTransaction.commit();

        mapf.getMapAsync(this);

        mReady = false;

        v = findViewById(R.id.map_container);
        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i("HI", "onGlobalLayout");
                if(mReady && myMap!=null && latlng!=null) {
                    myMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(latlng, latlng), 10));
                    mReady=false;
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //33.85215713 -117.74921909
        //LatLng latlng = new LatLng(33.85215713, -117.74921909);
        Log.i("HI", "onMapReady");

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        Intent i = starterIntent;
        Log.i("tag", "LATITUDE FOUND IS: "+i.getDoubleExtra("lat", 0));
        Log.i("tag", "LONGITUDE FOUND IS: "+i.getDoubleExtra("long", 0));

        latlng = new LatLng(i.getDoubleExtra("lat", 0), i.getDoubleExtra("long", 0));
        googleMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title(i.getStringExtra("username")).snippet(i.getStringExtra("geoad")).snippet(i.getStringExtra("date")+" "+i.getStringExtra("time")));

//        if(ready) {
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(latlng, latlng), 10));
//        }
        mReady = true;
        myMap = googleMap;

    }
}
