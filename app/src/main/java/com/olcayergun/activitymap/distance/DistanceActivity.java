package com.olcayergun.activitymap.distance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.olcayergun.activitymap.PermissionUtils;
import com.olcayergun.activitymap.R;

import java.util.ArrayList;

public class DistanceActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private Marker centerMarker = null;
    private final ArrayList<LatLng> coordinates = new ArrayList<>();
    private final ArrayList<Marker> markers = new ArrayList<>();

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    private GoogleMap map;

    private EditText editTextNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.distanceMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        editTextNumber = (EditText) findViewById(R.id.editTextNumber);

        Button btnShow = (Button) findViewById(R.id.btnShow);
        if (btnShow != null) {
            btnShow.setOnClickListener((View.OnClickListener) (it -> {
                if (centerMarker == null) {
                    alertDialog("Please long touch on the map to mark a center, then enter a distance.");
                    return;
                }

                int distance = 0;
                try {
                    String s = editTextNumber.getText().toString();
                    distance = Integer.parseInt(s);
                } catch (Exception e) {
                    alertDialog("Please enter a distance as an integer number.");
                    return;
                }
                if (distance == 0) {
                    alertDialog("Please enter a distance as an integer number.");
                    return;
                }

                deleteMarkers();
                Double dis = 0.0;
                distance = 1000 * distance;
                markers.clear();
                for (LatLng coordinate : coordinates) {
                    LatLng centerLatLng = centerMarker.getPosition();
                    dis = SphericalUtil.computeDistanceBetween(centerLatLng, coordinate);
                    if (dis <= distance) {
                        Marker marker = map.addMarker(new MarkerOptions().position(coordinate)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                        markers.add(marker);
                    }
                }

                if (markers.size() == 0) {
                    alertDialog("No point is found with the distance: " + distance);
                    return;
                }
            }));
        }

        Button btnDelete = (Button) findViewById(R.id.btnDelete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener((View.OnClickListener) (it -> {
                deleteMarkers();
            }));
        }
        initCoordinates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnMapLongClickListener(this);
        enableMyLocation();
        alertDialog("Please long touch on the map to mark a center, then enter a distance.");


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(41.00946077896473, 29.013244501220022))
                .zoom(15).build();
        //Zoom in and animate the camera.
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (null != centerMarker) {
            centerMarker.remove();
        }
        centerMarker = map.addMarker(new MarkerOptions().position(latLng));
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_LONG).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        // Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    // [START maps_check_location_permission_result]
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            // [END_EXCLUDE]
        }
    }
    // [END maps_check_location_permission_result]

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    private void deleteMarkers() {
        for (Marker marker : markers) {
            marker.remove();
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void alertDialog(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setTitle("Activityies in Map");
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void initCoordinates() {
        coordinates.add(new LatLng(41.021238137495224, 29.00845671838434));
        coordinates.add(new LatLng(41.016739833742264, 29.03830875310864));
        coordinates.add(new LatLng(41.008126085964705, 29.028266562765264));
        coordinates.add(new LatLng(40.99666093061958, 29.028266562765264));
        coordinates.add(new LatLng(41.00838516247413, 29.013761176713732));
        coordinates.add(new LatLng(41.01097587154255, 29.01856769516868));
        coordinates.add(new LatLng(41.008514700346836, 29.028438224138664));
        coordinates.add(new LatLng(41.0061829796749, 29.0086971661987));
        coordinates.add(new LatLng(40.99957598970225, 29.020541800962675));
        coordinates.add(new LatLng(41.007543160094116, 29.01444782220729));
        coordinates.add(new LatLng(40.99892820993588, 29.04096950439621));
        coordinates.add(new LatLng(41.01078157189585, 29.019683494095723));
        coordinates.add(new LatLng(41.00715453998212, 29.0181385417352));
        coordinates.add(new LatLng(41.01395506103946, 29.01298870053347));
        coordinates.add(new LatLng(41.00385117649354, 29.020970954396155));
        coordinates.add(new LatLng(41.013372186738394, 29.035733832507777));
        coordinates.add(new LatLng(41.01181783005684, 29.020284308902585));
        coordinates.add(new LatLng(41.01648079008484, 29.019855155469116));
        coordinates.add(new LatLng(41.00203749475401, 29.05032504924601));
        coordinates.add(new LatLng(41.012335953025364, 29.041999472636547));
        coordinates.add(new LatLng(40.99555965252742, 29.03358806534039));
    }
}