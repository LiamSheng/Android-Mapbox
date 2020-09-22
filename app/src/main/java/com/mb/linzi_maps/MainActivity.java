package com.mb.linzi_maps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap map;
    private LocationServices locationServices;

    private static final int PERMISSIONS_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final IconFactory iconFactory = IconFactory.getInstance(this);


        // get access token.
        MapboxAccountManager.start(this, getString(R.string.access_token));
        setContentView(R.layout.activity_main);

        // MapView found or created by new MapView.
        mapView = (MapView) findViewById(R.id.mapview);

        locationServices = LocationServices.getLocationServices(MainActivity.this);

        // what ..always see savedInstanceState.
        mapView.onCreate(savedInstanceState);

        // Add a MapboxMap
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                Icon icon = iconFactory.fromResource(R.mipmap.point);

                MarkerOptions marker1 = new MarkerOptions()
                        .position(new LatLng(49.900219, -97.141663))
                        .title("Red River College - The Roblin Centre")
                        .snippet("Welcome!!!")
                        .icon(icon);
                MarkerOptions marker2 = new MarkerOptions()
                        .position(new LatLng(35.160970, -111.672890))
                        .title("Trail")
                        .snippet("Few people will pass by...")
                        .icon(icon);
                MarkerOptions marker3 = new MarkerOptions()
                        .position(new LatLng(41.234430, -111.992620))
                        .title("River")
                        .snippet("The river cut off the road")
                        .icon(icon);
                List<MarkerOptions> markerList = new ArrayList<>();
                markerList.add(marker1);
                markerList.add(marker2);
                markerList.add(marker3);

                mapboxMap.addMarkers(markerList);


                //drawPolygon(mapboxMap);
                map = mapboxMap;
            }
        });


        final int layerNumber = 3;
        final String[] styles = new String[layerNumber];
        styles[0] = Style.DARK;
        styles[1] = Style.SATELLITE;
        styles[2] = Style.MAPBOX_STREETS;
        final int[] layerIndex = {0};

        Button btn = (Button) findViewById(R.id.Layers);
        btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                // View v means this view Object Button!!
                final int lengthShort = Toast.LENGTH_SHORT;
                Toast.makeText(MainActivity.this, "Switching style", lengthShort).show();
                if (layerIndex[0] < layerNumber) {
                    mapView.setStyleUrl(styles[layerIndex[0]]);
                    layerIndex[0]++;

                } else {
                    layerIndex[0] = 0;
                }
            }
        });

    }

    // LatLng is basic Point Geometry of Mapbox
    private void fly2Point(MapboxMap map, LatLng point, int zoom) {
        CameraPosition position = new CameraPosition.Builder()
                .target(point)
                .zoom(zoom)
                .bearing(45)
                .tilt(30)
                .build(); // Creates a CameraPosition from the builder

        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), 4000);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void toggleGps(boolean enableGps) {
        if (enableGps) {
            // Check if user has granted location permission
            if (!locationServices.areLocationPermissionsGranted()) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_LOCATION);
                Toast.makeText(MainActivity.this, "location not permitted..", Toast.LENGTH_SHORT).show();
            } else {
                enableLocation(true);
            }
        } else {
            enableLocation(false);
        }
    }

    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            final Location lastLocation = locationServices.getLastLocation();
            if (lastLocation != null) {
                LatLng latLng = new LatLng(49.900219, -97.141663);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng), 15));
                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 10));

                Toast.makeText(MainActivity.this, "Lng:" + map.getCameraPosition().target.getLongitude() +
                        ", Lat:" + map.getCameraPosition().target.getLatitude(), Toast.LENGTH_LONG).show();
            }

            locationServices.addLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        LatLng latLng = new LatLng(49.900219, -97.141663);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng), 15));
                        locationServices.removeLocationListener(this);
                    }
                }
            });
//            fButton.setImageResource(R.drawable.ic_disable_24dp);
        } else {
//            fButton.setImageResource(R.drawable.ic_my_location_black_24dp);
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation(true);
            }
        }
    }

    //to inflate the xml menu file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    //to handle events
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        boolean returnVal = false;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Log.d("JG", "refresh menu item");
                returnVal = true;
                break;
            case R.id.action_settings:
                Log.d("JG", "settings menu item");
                returnVal = true;
                if (map != null) {
                    toggleGps(!map.isMyLocationEnabled());
                }
                break;
            case R.id.location1:
                fly2Point(map, new LatLng(49.900219, -97.141663), 18);
                break;
            case R.id.location2:
                fly2Point(map, new LatLng(35.160970, -111.672890), 18);
                break;
            case R.id.location3:
                fly2Point(map, new LatLng(41.234430, -111.992620), 18);
                break;
        }
        return returnVal;
    }
}
