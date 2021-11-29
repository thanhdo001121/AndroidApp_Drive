package com.example.firebase.map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.firebase.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;

    private static final int LOCATION_PERMISSION_CODE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGooglePlayServicesAvailable()) {
            return;
        }
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        showCurrentLocation();
    }

    private void showCurrentLocation() {
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermission();

            Log.e("Right", "right");

            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);

        if (location != null) {
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(bestProvider, AppConfig.MIN_TIME_BW_UPDATES,
                AppConfig.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                LOCATION_PERMISSION_CODE
        );
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean coarseLocationPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean fineLocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (coarseLocationPermission && fineLocationPermission) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                    showCurrentLocation();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loadNearByPlaces(double latitude, double longitude, String queryType) {
        String type = queryType;
        StringBuilder googlePlacesUrl =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlacesUrl.append("&radius=").append(AppConfig.PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=").append(type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + AppConfig.GOOGLE_BROWSER_API_KEY);

        Log.i("URL", googlePlacesUrl.toString());

        JsonObjectRequest request = new JsonObjectRequest(googlePlacesUrl.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {

                        Log.i(AppConfig.TAG, "onResponse: Result= " + result.toString());
                        parseLocationResult(result, type);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(AppConfig.TAG, "onErrorResponse: Error= " + error);
                        Log.e(AppConfig.TAG, "onErrorResponse: Error= " + error.getMessage());
                    }
                });

        AppController.getInstance().addToRequestQueue(request);
    }

    private void parseLocationResult(JSONObject result, String type) {

        String id, place_id, placeName = null, reference, icon, vicinity = null;
        double latitude, longitude;

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(AppConfig.STATUS).equalsIgnoreCase(AppConfig.OK)) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    place_id = place.getString(AppConfig.PLACE_ID);
                    if (!place.isNull(AppConfig.NAME)) {
                        placeName = place.getString(AppConfig.NAME);
                    }
                    if (!place.isNull(AppConfig.VICINITY)) {
                        vicinity = place.getString(AppConfig.VICINITY);
                    }
                    latitude = place.getJSONObject(AppConfig.GEOMETRY).getJSONObject(AppConfig.LOCATION)
                            .getDouble(AppConfig.LATITUDE);
                    longitude = place.getJSONObject(AppConfig.GEOMETRY).getJSONObject(AppConfig.LOCATION)
                            .getDouble(AppConfig.LONGITUDE);
                    reference = place.getString(AppConfig.REFERENCE);
                    icon = place.getString(AppConfig.ICON);

                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(latitude, longitude);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName + " : " + vicinity);

                    if (type == "parking") {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.parkinglot));
                    } else if (type == "atm") {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.dollar));
                    } else if (type == "gas_station") {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.gas));
                    }

                    mMap.addMarker(markerOptions);
                }

                Toast.makeText(getBaseContext(), jsonArray.length() + " places found!",
                        Toast.LENGTH_LONG).show();
            } else if (result.getString(AppConfig.STATUS).equalsIgnoreCase(AppConfig.ZERO_RESULTS)) {
                Toast.makeText(getBaseContext(), "No places found in 5KM radius!!!",
                        Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(AppConfig.TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        mMap.clear();

        loadNearByPlaces(latitude, longitude, "atm");
        loadNearByPlaces(latitude, longitude, "parking");
        loadNearByPlaces(latitude, longitude, "gas_station");
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

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, AppConfig.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(AppConfig.TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}