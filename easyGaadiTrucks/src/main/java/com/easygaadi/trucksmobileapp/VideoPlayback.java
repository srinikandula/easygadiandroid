package com.easygaadi.trucksmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VideoPlayback extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private JSONArray trackPoints;
    private Context context;
    private String vehicleType="";
    private LatLngBounds.Builder plotBoundsBuilder;
    private List<LatLng> plotPoints;
    private List<JSONObject> plotData;
    private Marker plotMarker;
    private ProgressDialog progressDialog;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_playback);
        context = this;
        plotBoundsBuilder = LatLngBounds.builder();
        plotPoints = new ArrayList<>();
        plotData = new ArrayList<>();

        adView = (AdView)findViewById(R.id.adView);
        //intializeBannerAd();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        progressDialog = new ProgressDialog(VideoPlayback.this);
        progressDialog.setMessage("Intializing the route");
        progressDialog.show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        TruckApp.getInstance().trackScreenView("TrackVehiclePlayback Screen");
       /* if (adView != null) {
            adView.resume();
        }*/
    }



    @Override
    protected void onDestroy() {
        /*if (adView != null)
        { adView.destroy(); }*/
        super.onDestroy();
    }


    private void intializeBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
      /*  if (adView != null) {
            adView.pause();
        }*/
        TruckApp.checkPDialog(progressDialog);
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
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                try {
                    TruckApp.checkPDialog(progressDialog);
                    drawRoute();
                }catch (Exception e){
                    Log.e("exception",e.toString());
                }
            }
        });
    }

    private void drawRoute() throws Exception{
        if(getIntent().hasExtra("points")) {
            JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("points"));
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;
            points = new ArrayList<LatLng>();
            polyLineOptions = new PolylineOptions();
            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();

            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject point;
                try {

                    point = jsonArray.getJSONObject(j);
                    double lat = point.getDouble("latitude");
                    double lng = point.getDouble("longitude");
                    plotData.add(point);
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                    boundsBuilder.include(position);
                    if(j==0){
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(position);
                        StringBuilder builder = new StringBuilder();
                        builder.append("Speed  :").append(point.getString("speed")).append("\n ").append("\nAddress :").append(getCompleteAddressString(position.latitude, position.longitude));
                        markerOptions.title(builder.toString());
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_flag));
                        googleMap.addMarker(markerOptions);
                    }else if(j == jsonArray.length()-1){
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(position);
                        StringBuilder builder = new StringBuilder();
                        builder.append("Speed  :").append(point.getString("speed")).append("\n ").append("\nAddress :").append(getCompleteAddressString(position.latitude, position.longitude));
                        markerOptions.title(builder.toString());
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_flag));
                        googleMap.addMarker(markerOptions);
                    }
                } catch (JSONException e) {
                    Log.e("JSONEXE",e.toString()) ;
                }
            }

            polyLineOptions.addAll(points);
            polyLineOptions.width(14);
            polyLineOptions.color(R.color.overlaycolor);

            googleMap.addPolyline(polyLineOptions);

            LatLngBounds bounds = boundsBuilder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 12);
            googleMap.moveCamera(cameraUpdate);
            googleMap.animateCamera(cameraUpdate);
            plotRoute();
        }
    }


    private void plotRoute() {
        if(getIntent().hasExtra("points")){
            try {
                trackPoints = new JSONArray(getIntent().getStringExtra("points"));
                vehicleType = getIntent().getStringExtra("vehicleType");
                int drawable_id = (vehicleType.equalsIgnoreCase("CR")) ? (R.drawable.car_running): (R.drawable.truck_running);
                LatLng latLng = null;
                for (int i=0;i<trackPoints.length();i++){
                    try {
                        JSONObject trackPoint = trackPoints.getJSONObject(i);
                        latLng = new LatLng(trackPoint.getDouble("latitude"), trackPoint.getDouble("longitude"));
                        plotPoints.add(latLng);
                    }catch(Exception e){
                        Log.e("Error",e.toString());
                    }
                }
                setAnimation(googleMap, BitmapFactory.decodeResource(context.getResources(),
                        drawable_id));
            }catch (Exception e){
                Log.e("Error",e.toString());
            }
        }
    }

    private  void setAnimation(GoogleMap myMap, final Bitmap bitmap) {
        if(plotMarker==null) {
            plotMarker = myMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    .position(plotPoints.get(0))
                    .anchor(0.5f,0.5f)
                    .flat(true));
        }
        //LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //builder.include(new LatLng(plotPoints.get(0).latitude, plotPoints.get(0).longitude));
        /*builder.include(new LatLng(plotPoints.get(directionPoint.size()-1)
                .latitude, plotPoints.get(plotPoints.size()-1).longitude));*/
        //LatLngBounds bounds = plotBoundsBuilder.build();
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 12);
        //googleMap.moveCamera(cameraUpdate);

        animateMarker(myMap, plotMarker, false);
    }

    private void animateMarker(final GoogleMap myMap, final Marker marker,  final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = myMap.getProjection();
        final long duration = 450*(plotPoints.size()+10);

        final Interpolator interpolator = new LinearInterpolator();
        Log.d("TotalPosition:",plotPoints.size()+"");
        handler.post(new Runnable() {
            int i = 0;
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                if (i < plotPoints.size()) {
                    if(plotPoints.size()-1==i){
                        finish();
                    }
                    Log.d("IFPosition:",i+"");
                    //play_btn.setVisibility(View.GONE);
                    marker.setPosition(plotPoints.get(i));
                    try {
                        marker.setRotation(Float.parseFloat(plotData.get(i).getString("heading")));
                        int drawable_id;
                        if (plotData.get(i).getInt("speedValue") == 0) {
                            drawable_id = (vehicleType.equalsIgnoreCase("CR"))?R.drawable.car_stop_small:R.drawable.truck_stop_small;
                        } else {
                            drawable_id = (vehicleType.equalsIgnoreCase("CR"))?R.drawable.car_running_small:R.drawable.truck_running_small;
                        }
                        marker.setIcon(BitmapDescriptorFactory.fromResource(drawable_id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.d("ElsePosition:",i+"");
                    marker.setVisible(false);
                    //play_btn.setVisibility(View.VISIBLE);
                }
                i++;
                if (t < 1.0 && i <= plotPoints.size()) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 450);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My loction address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My loction address", "Canont get Address!");
        }
        return strAdd;
    }
}
