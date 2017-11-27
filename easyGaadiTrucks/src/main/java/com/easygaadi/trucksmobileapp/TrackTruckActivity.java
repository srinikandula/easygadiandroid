package com.easygaadi.trucksmobileapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.ExceptionHandler;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TrackTruckActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};
    GoogleMap googleMap;
    SharedPreferences sharedPreferences;
    private ConnectionDetector detectCnnection;
    JSONParser parser;
    ProgressDialog pDialog;
    HashMap<String, String> markersMap;
    int locationCount = 0, speedLimit = 0;
    Context context;
    TextView truckReg_tv, src_tv, dest_tv, start_loc_tv, curr_loc_tv, stop_loc_tv, distanceTra_tv, timetra_tv, idle_tv, avgSpeed_tv, topSpeed_tv, empty_tv, confirm_dlg_title_tv, confirm_dlg_src_tv, confirm_dlg_dest_tv, confirm_dlg_date_tv, confirm_tv, start_date_tv, stop_date_tv;
    Button confirm_btn, cancel_btn;
    ScrollView bottomLayout;
    LinearLayout stopsLayout, dist_ll;
    LinearLayout start_loc_ll, curr_loc_ll, stop_loc_ll, start_date_ll, stop_date_ll;
    RelativeLayout src_dest_ll;
    ImageView closeInfo_iv, closeStops_iv,playVideo;
    ListView stopsLV;
    SupportMapFragment fm;
    Dialog confirmDialog;
    boolean isTrip = false;
    Menu mMenu;
    boolean stopped = false;
    String startLocation, stopLocation, currentLocation;
    int startTime, stopTime;
    double end_pt_lat, end_pt_lng;
    private InterstitialAd mInterstitialAd;

    private static final String[] INITIAL_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final String[] LOCATION_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;
    Toolbar toolbar;

    private JSONArray trackPoints;


    private AdView adView;


    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracktruck);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        detectCnnection = new ConnectionDetector(context);
        parser = JSONParser.getInstance();
        markersMap = new HashMap<String, String>();
        truckReg_tv = (TextView) findViewById(R.id.truckreg_tv);
        src_tv = (TextView) findViewById(R.id.trip_summary_src_tv);
        dest_tv = (TextView) findViewById(R.id.trip_summary_dest_tv);
        start_loc_tv = (TextView) findViewById(R.id.trip_summary_start_loc_tv);
        curr_loc_tv = (TextView) findViewById(R.id.trip_summary_curr_loc_tv);
        stop_loc_tv = (TextView) findViewById(R.id.trip_summary_stop_loc_tv);
        start_date_tv = (TextView) findViewById(R.id.trip_summary_start_date_tv);
        stop_date_tv = (TextView) findViewById(R.id.trip_summary_stop_date_tv);
        distanceTra_tv = (TextView) findViewById(R.id.distravelled_tv);
        timetra_tv = (TextView) findViewById(R.id.timetravelled_tv);
        idle_tv = (TextView) findViewById(R.id.idletime_tv);
        avgSpeed_tv = (TextView) findViewById(R.id.averagespeed_tv);
        topSpeed_tv = (TextView) findViewById(R.id.topspeedtv);
        bottomLayout = (ScrollView) findViewById(R.id.bottomlayout);
        closeInfo_iv = (ImageView) findViewById(R.id.closeinfo_tv);
        playVideo = (ImageView) findViewById(R.id.play_iv);
        src_dest_ll = (RelativeLayout) findViewById(R.id.trip_summary_src_dest_ll);
        start_loc_ll = (LinearLayout) findViewById(R.id.trip_summary_start_loc_ll);
        curr_loc_ll = (LinearLayout) findViewById(R.id.trip_summary_curr_loc_ll);
        stop_loc_ll = (LinearLayout) findViewById(R.id.trip_summary_stop_loc_ll);
        start_date_ll = (LinearLayout) findViewById(R.id.trip_summary_start_date_ll);
        stop_date_ll = (LinearLayout) findViewById(R.id.trip_summary_stop_date_ll);
        dist_ll = (LinearLayout) findViewById(R.id.trip_summary_dist_ll);
        stopsLayout = (LinearLayout) findViewById(R.id.stopslayout);
        closeStops_iv = (ImageView) findViewById(R.id.closestops_tv);
        stopsLV = (ListView) findViewById(R.id.stopsLV);
        empty_tv = (TextView) findViewById(R.id.empty_pts_tv);

        adView = (AdView)findViewById(R.id.adView);
       // intializeBannerAd();


        if(getIntent().hasExtra("vehicleType")){
            playVideo.setVisibility(View.VISIBLE);
        }else{
            playVideo.setVisibility(View.GONE);
        }

        //intializeGoogleAd();


        playVideo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trackPoints != null){
                    Intent playIntent = new Intent(context, VideoPlayback.class);
                    playIntent.putExtra("points",trackPoints.toString());
                    playIntent.putExtra("vehicleType",getIntent().getStringExtra("vehicleType"));
                    startActivity(playIntent);
                }
            }
        });

        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);


        bottomLayout.setVisibility(View.GONE);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        confirmDialog = new Dialog(context, android.R.style.Theme_Dialog);
        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        confirmDialog.setContentView(R.layout.dialog_confirm);

        confirm_dlg_title_tv = (TextView) confirmDialog.findViewById(R.id.confirm_dlg_title_tv);
        confirm_btn = (Button) confirmDialog.findViewById(R.id.confirm_dlg_confirm_btn);
        cancel_btn = (Button) confirmDialog.findViewById(R.id.confirm_dlg_cancel_btn);
        confirm_tv = (TextView) confirmDialog.findViewById(R.id.confirm_dlg_main_tv);
        confirm_dlg_src_tv = (TextView) confirmDialog.findViewById(R.id.confirm_dlg_src_tv);
        confirm_dlg_dest_tv = (TextView) confirmDialog.findViewById(R.id.confirm_dlg_dest_tv);
        confirm_dlg_date_tv = (TextView) confirmDialog.findViewById(R.id.confirm_dlg_date_created_tv);

        ActionBar ab = getSupportActionBar();
        if (getIntent().hasExtra("tripId")) {
            if (getIntent().getStringExtra("tripId") != null) {

                //ab.setTitle("Trip "+ getIntent().getStringExtra("truckNo"));

                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append("Trip ");
                int start = builder.length();
                builder.append(getIntent().getStringExtra("truckNo"));
                int end = builder.length();

                builder.setSpan(new ForegroundColorSpan(Color.DKGRAY), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                ab.setTitle(builder);
                isTrip = true;
                invalidateOptionsMenu();
            }
        }
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(TrackTruckActivity.this));


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && !canAccessLocation()) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        } else {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                System.out.println("Less version than 23");
                googleFunctionality();
            } else {
                if (canAccessLocation()) {
                    googleFunctionality();
                } else {
                    System.out.println("Nothing supporting location permissions");
                }
            }
        }
        cancel_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (confirmDialog != null && confirmDialog.isShowing()) {
                    confirmDialog.dismiss();
                }
            }
        });
        closeInfo_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                bottomLayout.setVisibility(View.GONE);
            }
        });
        closeStops_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                stopsLayout.setVisibility(View.GONE);
            }
        });

    }


    public void googleFunctionality() {
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available
            initializeMap();
        }
    }

    private void initializeMap() {
        System.out.println("On Map Initialize");
        if (googleMap == null) {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            fm.getMapAsync(this);
        }
    }

    private static String timeConversion(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return TruckApp.set2Digit(hours) + ":" + TruckApp.set2Digit(minutes) + ":" + TruckApp.set2Digit(seconds);
    }

    public void trackTruck() {
        if (detectCnnection.isConnectingToInternet()) {
            try {
                if (getIntent().hasExtra("regno")) {
                    if (googleMap != null) {
                        googleMap.clear();
                        new TrackTruck(getIntent().getStringExtra("regno"), getIntent().getStringExtra("urlParams")).execute();
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.googlemap_str), Toast.LENGTH_LONG).show();
                    }
                } else if (getIntent().hasExtra("tripId")) {
                    if (googleMap != null) {
                        googleMap.clear();
                        System.out.println(getIntent().getStringExtra("tripId") + " : " + getIntent().getBooleanExtra("istrip", false));
                        new TrackTruck(getIntent().getStringExtra("tripId"), getIntent().getBooleanExtra("istrip", false)).execute();
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.googlemap_str), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                System.out.println("Ex inn on resume:" + e.toString());
            }
        } else {
            Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();

        }
    }

    private void setTruckDetails(JSONObject successObj) {

        try {

            if (!isTrip) {
                src_dest_ll.setVisibility(View.GONE);
                start_date_ll.setVisibility(View.GONE);
                truckReg_tv.setText(": " + getIntent().getStringExtra("regno"));
                distanceTra_tv.setText(": " + successObj.getString("distanceTravelled"));
                timetra_tv.setText(": " + successObj.getString("timeTravelled"));
                idle_tv.setText(": " + successObj.getString("odokm"));
                avgSpeed_tv.setText(": " + successObj.getString("averageSpeed"));
                topSpeed_tv.setText(": " + successObj.getString("topSpeed") + " Km/Hr");
            } else {
                truckReg_tv.setText(": " + successObj.getString("deviceID"));
                //distanceTra_tv.setText(": "+successObj.getString("distance"));
                src_dest_ll.setVisibility(View.VISIBLE);
                start_date_ll.setVisibility(View.VISIBLE);
                src_tv.setText(successObj.getString("source"));
                dest_tv.setText(successObj.getString("destination"));
                start_loc_tv.setText(": " + successObj.getString("startLoc"));
                String date_start = DateFormat.getDateTimeInstance().format(new Date((long) successObj.getInt("startPointTime") * 1000));
                start_date_tv.setText(": " + date_start);
                dist_ll.setVisibility(View.GONE);
                src_dest_ll.setVisibility(View.VISIBLE);
                start_loc_ll.setVisibility(View.VISIBLE);
                if (successObj.getString("endPointTime").equals("0")) {
                    curr_loc_tv.setText(": " + successObj.getString("currentLoc"));
                    curr_loc_ll.setVisibility(View.VISIBLE);
                    stop_loc_ll.setVisibility(View.GONE);
                    stop_date_ll.setVisibility(View.GONE);
                } else {
                    stop_loc_tv.setText(": " + successObj.getString("endLoc"));
                    String date_stop = DateFormat.getDateTimeInstance().format(new Date((long) successObj.getInt("endPointTime") * 1000));
                    stop_date_tv.setText(": " + date_stop);
                    stop_loc_ll.setVisibility(View.VISIBLE);
                    stop_date_ll.setVisibility(View.VISIBLE);
                    curr_loc_ll.setVisibility(View.GONE);
                }
                timetra_tv.setText(": " + successObj.getString("hoursTravelled") + " Hrs");
                idle_tv.setText(": " + successObj.getString("odo") + " Km");
                avgSpeed_tv.setText(": " + successObj.getString("avgSpeed") + " Km/Hr");
                topSpeed_tv.setText(": " + successObj.getString("topSpeed"));
            }
            successObj = null;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ;

    }

    private void hideIfEmpty(TextView tv, String str) {
        if (str != "") tv.setText(str);
        else tv.setVisibility(View.GONE);
    }

    private void drawMarker(LatLng point, String title, JSONObject locJSON, boolean first, boolean direction, boolean stops) {

        try {
            JSONObject data = new JSONObject(locJSON.toString());
            // Creating an instance of MarkerOptions
            MarkerOptions markerOptions = new MarkerOptions();
            // Setting latitude and longitude for the marker
            markerOptions.position(point);

            if (!direction && first && !stops) {
                //stop
                StringBuilder builder = new StringBuilder();
                builder.append("Speed  :").append(title).append("\n ").append("\nAddress :").append(getCompleteAddressString(point.latitude, point.longitude));
                markerOptions.title(builder.toString());
                data.put("direction", false);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_flag));
            } else if (!direction && !first && !stops) {
                //start
                StringBuilder builder = new StringBuilder();
                builder.append("Speed  :").append(title).append("\n ").append("\nAddress :").append(getCompleteAddressString(point.latitude, point.longitude));
                markerOptions.title(builder.toString());
                data.put("direction", false);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_flag));
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("Speed  :").append(title);
                markerOptions.title(builder.toString());
                int heading, speed;
                if (!stops) {
                    speed = data.getInt("speedValue");
                    heading = Integer.parseInt(data.getString("heading"));
                } else {
                    speed = 0;
                    heading = 0;
                }
                data.put("direction", true);

                if (speed == 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_red));
                } else if (speed >= speedLimit) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.over_r));
                    System.out.println("overSpeed");
                } else if (heading >= 0 && heading < 25 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h0));
                } else if (heading >= 25 && heading < 70 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h1));
                } else if (heading >= 70 && heading < 110 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h2));
                } else if (heading >= 110 && heading < 160 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h3));
                } else if (heading >= 160 && heading < 200 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h4));
                } else if (heading >= 200 && heading < 240 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h5));
                } else if (heading >= 240 && heading < 290 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h6));
                } else if (heading >= 290 && heading < 330 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h7));
                } else if (heading >= 330 && heading < 390 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h0));
                } else if (heading >= 390 && heading < 420 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h1));
                } else if (heading >= 420 && heading < 450 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h2));
                } else if (heading >= 450 && heading < 500 && speed != 0) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h3));
                } else {
                    System.out.println("not found" + heading);
                }
            }
            // Adding marker on the Google Map
            Marker marker = googleMap.addMarker(markerOptions);
            markersMap.put(marker.getId(), data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void intializeGoogleAd() {
        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.eg_interstital));

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
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

    @Override
    public void onMapReady(GoogleMap mGoogleMap) {
        this.googleMap = mGoogleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        System.out.println("On Map Ready");
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // Setting a custom info window adapter for the google map
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.marker_item, null);

                try {
                    // Getting view from the layout file info_window_layout

                    String data = markersMap.get(arg0.getId());
                    JSONObject jObj = new JSONObject(data);

                    TextView startloc_tv = (TextView) v.findViewById(R.id.marker_trip_start_loc_tv);
                    TextView currentloc_tv = (TextView) v.findViewById(R.id.currentloc_tv);
                    TextView endloc_tv = (TextView) v.findViewById(R.id.marker_trip_end_loc_tv);

                    TextView startdate_tv = (TextView) v.findViewById(R.id.start_date_tv);
                    TextView stopdate_tv = (TextView) v.findViewById(R.id.stop_date_tv);

                    TextView speed_tv = (TextView) v.findViewById(R.id.speed_tv);

                    TextView geo_latlon_tv = (TextView) v.findViewById(R.id.geo_latlon);
                    TextView regno_tv = (TextView) v.findViewById(R.id.regno_tv);

                    TextView time_tv = (TextView) v.findViewById(R.id.time_tv);

                    TextView address_tv = (TextView) v.findViewById(R.id.address_tv);
                    TextView odometer_tv = (TextView) v.findViewById(R.id.odometer_tv);

                    ((TextView) v.findViewById(R.id.track_tv)).setVisibility(View.GONE);
                    if (!jObj.getBoolean("direction")) {
                        System.out.println("NOT HEADING");
                        String add_str = getCompleteAddressString(jObj.getDouble("latitude"), jObj.getDouble("longitude"));
                        currentloc_tv.setText(Html.fromHtml("<b>Current Loc : </b>" + (add_str.split(",")[0])));
                        address_tv.setText(Html.fromHtml("<b>Address : </b>" + add_str));
                    } else {
                        System.out.println("HEADING");
                        currentloc_tv.setVisibility(View.GONE);
                        address_tv.setVisibility(View.GONE);
                    }

                    DecimalFormat f = new DecimalFormat("##.0000");
                    if (jObj.getString("speed").equalsIgnoreCase("0km/hr")) {
                        odometer_tv.setVisibility(View.VISIBLE);
                        odometer_tv.setText(Html.fromHtml("<b>StopTime :</b> " + TrackTruckActivity.timeConversion(jObj.getInt("stopTime"))));
                    } else {
                        odometer_tv.setVisibility(View.GONE);
                    }
                    speed_tv.setText(Html.fromHtml("<b>Speed : </b>" + jObj.getString("speed")));
                    regno_tv.setText(Html.fromHtml("<b>Reg.No : </b>" + getIntent().getStringExtra("regno")));
                    time_tv.setText(Html.fromHtml("<b>Date : </b>" + TruckApp.secToDate(jObj.getInt("time_in_secs"), context)));
                    geo_latlon_tv.setText(Html.fromHtml("<b>GPS : </b>" + f.format(jObj.getDouble("latitude")) + "/" + f.format(jObj.getDouble("longitude"))));

                    if (getIntent().hasExtra("tripId")) {

                        System.out.println("creating info");
                        regno_tv.setVisibility(View.GONE);
                        startdate_tv.setVisibility(View.VISIBLE);
                        startloc_tv.setVisibility(View.VISIBLE);
                        System.out.println("startLoc : " + startLocation);
                        startloc_tv.setText(Html.fromHtml("<b>Start Location : </b>" + startLocation));
                        Date startDate = new Date((long) startTime * 1000);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String start_date = sdf.format(startDate);
                        System.out.println("startDate : " + start_date);
                        startdate_tv.setText(Html.fromHtml("<b>Start Date : </b>" + start_date));
                        if (stopTime != 0) {
                            currentloc_tv.setVisibility(View.GONE);
                            endloc_tv.setVisibility(View.VISIBLE);
                            stopdate_tv.setVisibility(View.VISIBLE);
                            Date stopDate = new Date((long) stopTime * 1000);
                            String stop_date = sdf.format(stopDate);
                            stopdate_tv.setText(Html.fromHtml("<b>Stop Date : </b>" + stop_date));
                            String end_pt_loc = getCompleteAddressString(end_pt_lat, end_pt_lng);
                            endloc_tv.setText(Html.fromHtml("<b>Stop Location : </b>" + end_pt_loc));
                            //////show stop location////////////
                        } else {
                            /////show current location/////////
                            currentloc_tv.setVisibility(View.VISIBLE);
                            String add_str = getCompleteAddressString(jObj.getDouble("latitude"), jObj.getDouble("longitude"));
                            currentloc_tv.setText(Html.fromHtml("<b>Current Loc : </b>" + (add_str.split(",")[0])));

                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Returning the view containing InfoWindow contents
                return v;

            }
        });
        trackTruck();
    }


    private class TrackTruck extends AsyncTask<String, String, JSONObject> {
        boolean trip = false;
        String truckregno, urlParams, tripId;

        public TrackTruck(String truckregno, String urlparams) {
            this.truckregno = truckregno;
            this.urlParams = urlparams;
        }

        public TrackTruck(String tripId, boolean istrip) {
            this.tripId = tripId;
            this.trip = istrip;
            isTrip = istrip;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Tracking truck data...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                String url, res;

                if (!trip) {
                    url = TruckApp.TRACK_TRUCK_URL+"?truckregno="
                            + truckregno + "&account_id=" + sharedPreferences.getString("accountID",
                            "no accountid ") + "&" + urlParams+"&access_token="+sharedPreferences.getString("access_token","");
                    res = parser.executeGet(url);
                    json = new JSONObject(res);
                } else {
                    System.out.println("trip:- " + trip + "\ttripId: " + tripId);
                    url = TruckApp.getTripSummaryURL;
                    StringBuilder builder = new StringBuilder();
                    builder.append("Trip[id]=").append(URLEncoder.encode(tripId, "UTF-8"));
                    builder.append(builder.append("&access_token=")).append(sharedPreferences.getString("access_token",""));
                    //String urlParams = "Trip[id]=" + URLEncoder.encode(tripId, "UTF-8");
                    res = parser.excutePost(url, builder.toString());
                    System.out.println("res:- " + res);
                    json = new JSONObject(res);
                }
                System.out.println("URL:" + url);
                //String res = parser.executeGet(url);
                //json = new JSONObject(res);
                System.out.println("TTURL:" + url + "\nRESULT:" + res);
            } catch (Exception e) {
                Log.e("Login DoIN EX", e.toString());
            }
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            System.out.println("trip: " + trip);
            if (result != null) {
                try {
                    if (!trip) {
                        System.out.println("result obj" + result.toString());
                        if (0 == result.getInt("status")) {
                            TruckApp.checkPDialog(pDialog);
                            Toast.makeText(context, "No records found", Toast.LENGTH_LONG).show();
                        }else if (2 == result.getInt("status")) {
                            //TruckApp.logoutAction(TrackTruckActivity.this);
                            SharedPreferences sharedPreferences = getSharedPreferences(
                                    getResources().getString(R.string.app_name), MODE_PRIVATE);
                            SharedPreferences.Editor
                                    editor = sharedPreferences.edit();
                            editor.putInt("login", 0).commit();
                            editor.putString("accountID", "").commit();
                            startActivity(new Intent(context,
                                    LoginActivity.class));
                            finish();
                        } else if (1 == result.getInt("status")) {
                            playVideo.setVisibility(View.VISIBLE);
                            JSONObject successObj = result.getJSONObject("success");
                            setTruckDetails(successObj);
                            System.out.println("success obj" + successObj.toString());
                            speedLimit = successObj.getString("speedLimit") != null ? successObj.getInt("speedLimit") : 0;
                            JSONArray truckArray = successObj.getJSONArray("points");
                            trackPoints = truckArray;
                            JSONArray stopsArray = successObj.getJSONArray("stops");
                            System.out.println("truckArrayS obj" + truckArray.toString());
                            if (truckArray.length() > 0) {
                                if (googleMap != null) {
                                    markersMap.clear();
                                    googleMap.clear();
                                    drawRoute(truckArray, false);
                                    if (stopsArray.length() > 0) {
                                        drawRoute(stopsArray, true);
                                        stopsLV.setAdapter(new StopsAdapter(TrackTruckActivity.this, stopsArray));
                                    }
                                }
                            } else {
                                TruckApp.checkPDialog(pDialog);
                                Toast.makeText(context, "No truck found", Toast.LENGTH_LONG).show();
                            }

                        }
                    } else {
                        System.out.println("result obj" + result.toString());
                        if (0 == result.getInt("status")) {
                            TruckApp.checkPDialog(pDialog);
                            Toast.makeText(context, "No records found", Toast.LENGTH_LONG).show();
                        } else if (1 == result.getInt("status")) {
                            JSONObject successObj = result.getJSONObject("data");
                            ///////////////////////////////////////////////////////////
                            setTruckDetails(successObj);
                            System.out.println("success obj" + successObj.toString());
                            if (successObj.has("speedLimit") && successObj.getString("speedLimit") != null && successObj.getString("speedLimit") != "")
                                speedLimit = successObj.getInt("speedLimit");
                            JSONArray tripArray =null,stopsArray= null;
                            if(successObj.has("points"))
                                tripArray = successObj.getJSONArray("points") != null ? successObj.getJSONArray("points") : null;
                            if(successObj.has("stops"))
                                stopsArray = successObj.getJSONArray("stops") != null ? successObj.getJSONArray("stops") : null;
                            startLocation = successObj.getString("startLoc");
                            startTime = successObj.getInt("startPointTime");
                            stopTime = successObj.getInt("endPointTime");
                            end_pt_lat = successObj.getDouble("endPointLat");
                            end_pt_lng = successObj.getDouble("endPointLng");

                            //System.out.println("tripArrayS obj"+tripArray.toString());

                            if (tripArray!=null && tripArray.length() > 0) {
                                if (googleMap != null) {
                                    markersMap.clear();
                                    googleMap.clear();
                                    drawRoute(tripArray, false);
                                    if (stopsArray!=null && stopsArray.length() > 0) {
                                        drawRoute(stopsArray, true);
                                        stopsLV.setAdapter(new StopsAdapter(TrackTruckActivity.this, stopsArray));
                                    }
                                }
                            } else {
                                System.out.println("hide map1");
                                TruckApp.checkPDialog(pDialog);
                                Toast.makeText(context, "No tracking data", Toast.LENGTH_LONG).show();
                                getSupportFragmentManager().beginTransaction();
                                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.hide(fm);
                                ft.commit();
                                empty_tv.setVisibility(View.VISIBLE);
                                bottomLayout.setVisibility(View.VISIBLE);
                                System.out.println("hide map2");
                            }

                        }
                    }
                } catch (Exception e) {
                    TruckApp.checkPDialog(pDialog);
                    System.out.println("ex in get TRIP" + e.toString());
                    System.out.println("hide map1");
                    TruckApp.checkPDialog(pDialog);
                    Toast.makeText(context, "No tracking data", Toast.LENGTH_LONG).show();
                    getSupportFragmentManager().beginTransaction();
                    android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.hide(fm);
                    ft.commit();
                    empty_tv.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.VISIBLE);
                    System.out.println("hide map2");
                }
                TruckApp.checkPDialog(pDialog);
            } else {
                Toast.makeText(context, getResources().getString(R.string.exceptionmsg), Toast.LENGTH_LONG).show();
                TruckApp.checkPDialog(pDialog);
            }
        }
    }

    public void drawRoute(JSONArray jsonArray, boolean stops) {
        if (googleMap != null) {
            try {
                if (jsonArray.length() > 0 && !stops) {
                    ArrayList<LatLng> points = null;
                    PolylineOptions polyLineOptions = null;
                    System.out.println("i");
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();

                    LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();

                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject point;
                        try {
                            point = jsonArray.getJSONObject(j);
                            double lat = point.getDouble("latitude");
                            double lng = point.getDouble("longitude");

                            LatLng position = new LatLng(lat, lng);
                            points.add(position);
                            boundsBuilder.include(position);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(9);
                    polyLineOptions.color(R.color.overlaycolor);

                    googleMap.addPolyline(polyLineOptions);

                    LatLngBounds bounds = boundsBuilder.build();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 10);
                    googleMap.moveCamera(cameraUpdate);
                }
                if (jsonArray.length() > 0) {
                    try {
                        JSONObject jObj = jsonArray.getJSONObject(0);
                        LatLng latlng1 = new LatLng(jObj.getDouble("latitude"), jObj.getDouble("longitude"));
                        if (!stops)
                            drawMarker(latlng1, jObj.getString("speed"), jObj, false, false, false);
                        else {
                            jObj.put("speed", "0Km/hr");
                            drawMarker(latlng1, "0 Km/Hr", jObj, false, false, true);
                        }

                        jObj = null;
                        if (jsonArray.length() > 2) {
                            JSONObject jObjLast = jsonArray.getJSONObject(jsonArray.length() - 1);
                            LatLng latlng2 = new LatLng(jObjLast.getDouble("latitude"), jObjLast.getDouble("longitude"));
                            int multipleValue = 1;
                            if (!stops) {
                                //multipleValue = 1;
                                drawMarker(latlng2, jObjLast.getString("speed"), jObjLast, true, false, false);
                            } else {
                                //multipleValue = 1;
                                jObjLast.put("speed", "0Km/hr");
                                drawMarker(latlng2, "0 Km/Hr", jObjLast, false, false, true);
                            }

                            jObjLast = null;

                            // Moving CameraPosition to last clicked position
                            if (jsonArray.length() > multipleValue) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    if (i != 0 && i != jsonArray.length() - 1 && i % multipleValue == 0) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        LatLng latlngObj = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));

                                        if (!stops) {
                                                drawMarker(latlngObj, jsonObject.getString("speed"), jsonObject, false, true, false);
                                        } else {
                                            jsonObject.put("speed", "0Km/hr");
                                            drawMarker(latlngObj, "0 Km/Hr", jsonObject, false, false, true);
                                        }
                                        jsonObject = null;
                                        latlngObj = null;
                                    }
                                }

                            }
                        }

                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            TruckApp.checkPDialog(pDialog);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tracktruck_menu, menu);
        mMenu = menu;
        MenuItem item = (MenuItem) menu.findItem(R.id.action_stop_trip);
        if (getIntent().hasExtra("tripId") && getIntent().getStringExtra("endTime").equals("0") && !stopped) {
            System.out.println("track menu created");
            item.setVisible(true);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        googleMap = null;
        sharedPreferences = null;
        detectCnnection = null;
        parser = null;
        pDialog = null;
        markersMap = null;
        /*if (adView != null)
        { adView.destroy(); }*/
        super.onDestroy();
    }

    private class StopTrip extends AsyncTask<String, String, JSONObject> {
        String tripID, accID, devID, startTime;

        public StopTrip(String id_trip, String acc_id, String dev_id, String start_time) {
            this.tripID = id_trip;
            this.accID = acc_id;
            this.devID = dev_id;
            this.startTime = start_time;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject json = null;

            try {
                String url = TruckApp.stopTripURL;
                String urlParams = "Trip[id]=" + URLEncoder.encode(tripID, "UTF-8") + "&Trip[accountID]="
                        + URLEncoder.encode(accID, "UTF-8") + "&Trip[deviceID]=" + URLEncoder.encode(devID, "UTF-8") + "&Trip[startPointTime]=" + URLEncoder.encode(startTime, "UTF-8");
                String res = parser.excutePost(url, urlParams);
                json = new JSONObject(res);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return json;
        }

        @Override
        public void onPostExecute(JSONObject result) {
            if (confirmDialog != null && confirmDialog.isShowing()) confirmDialog.dismiss();
            if (result != null) {
                try {
                    if (result.getInt("status") == 0) {
                        Toast.makeText(context, "Error stopping trip", Toast.LENGTH_SHORT);
                    } else if (result.getInt("status") == 1) {
                        stopped = true;
                        invalidateOptionsMenu();
                        Toast.makeText(context, "Trip stopped", Toast.LENGTH_SHORT);
                        trackTruck();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, getResources().getString(R.string.exceptionmsg), Toast.LENGTH_LONG).show();
            }

        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stop_trip:
                confirmDialog(getIntent().getStringExtra("json"));
                break;
            case R.id.action_refresh:
                trackTruck();
                break;
            case R.id.action_info:
                bottomLayout.setVisibility(View.VISIBLE);
                stopsLayout.setVisibility(View.GONE);
                break;
            case R.id.action_stops:
                bottomLayout.setVisibility(View.GONE);
                stopsLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.change_map:
                if (googleMap != null) {
                    showMapTypeSelectorDialog();
                } else {
                    Toast.makeText(context, "Can't change now, Try Later", Toast.LENGTH_SHORT).show();
                }
                break;
            /*case R.id.action_report:
            reportproblem();
			break;*/
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMapTypeSelectorDialog() {
        // Prepare the dialog by setting up a Builder.
        final String fDialogTitle = "Select Map Type";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);

        // Find the current map type to pre-check the item representing the current state.
        int checkItem = googleMap.getMapType() - 1;

        // Add an OnClickListener to the dialog, so that the selection will be handled.
        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        // Locally create a finalised object.

                        // Perform an action depending on which item was selected.
                        switch (item) {
                            case 1:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );

        // Build the dialog and show it.
        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }

    public class StopsAdapter extends BaseAdapter {
        Activity activity;
        LayoutInflater inflater;
        JSONArray stopsArray;

        public StopsAdapter(Activity activity, JSONArray stopsArray) {
            super();
            this.activity = activity;
            this.stopsArray = stopsArray;
            this.inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return stopsArray.length();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            try {
                return stopsArray.get(position);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        public class ViewHolder {
            TextView date_tv, address_tv;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewholder = null;
            if (convertView == null) {
                viewholder = new ViewHolder();
                convertView = inflater.inflate(R.layout.truckstop_item,
                        parent, false);
                viewholder.date_tv = (TextView) convertView
                        .findViewById(R.id.datetimetv);
                viewholder.address_tv = (TextView) convertView
                        .findViewById(R.id.addresstv);

                convertView.setTag(viewholder);
            } else {
                viewholder = (ViewHolder) convertView.getTag();
            }
            setData(position, viewholder);
            return convertView;

        }

        private void setData(int position, ViewHolder viewholder) {
            try {
                JSONObject jObj = stopsArray.getJSONObject(position);
                String[] dateArray = ((TruckApp.secToDate(jObj.getInt("time_in_secs"), context)).trim()).split(" ");
                viewholder.address_tv.setText(Html.fromHtml("<b>" + getCompleteAddressString(jObj.getDouble("latitude"), jObj.getDouble("longitude")) + "</b><br>" + TrackTruckActivity.timeConversion(jObj.getInt("stopTime"))));
                viewholder.date_tv.setText(Html.fromHtml(dateArray[0] + "<br>" + dateArray[1]));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


    }

    public void reportproblem() {
        String trace = null, line;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getApplicationContext().openFileInput("stack.trace")));
            while ((line = reader.readLine()) != null) {
                trace += line + "\n";
            }
        } catch (FileNotFoundException fnfe) {
            // ...
        } catch (IOException ioe) {
            // ...
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = "Error report";
        String body =
                "Mail this to gdinesh029@gmail.com: " +
                        "\n\n" +
                        trace +
                        "\n\n";

        sendIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{"gdinesh029@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");

        startActivity(
                Intent.createChooser(sendIntent, "Title:"));
        deleteFile("stack.trace");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //initializeMap();
        TruckApp.getInstance().trackScreenView("TrackVehicle/Trip Screen");
        /*if (adView != null) {
            adView.resume();
        }*/

    }


    @Override
    protected void onPause() {
       /* if (adView != null) {
            adView.pause();
        }*/
        super.onPause();
    }



    private void intializeBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }



    private boolean canAccessLocation() {
        return (hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION));
    }

    @TargetApi(23)
    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, perm));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case LOCATION_REQUEST:
                if (canAccessLocation()) {
                    googleFunctionality();
                } else {
                    Toast.makeText(this, "Can't access location", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        if (stopped) intent.putExtra("refresh", true);
        setResult(200, intent);
        super.finish();
    }

    protected void confirmDialog(String string) {
        try {
            final JSONObject jsonObj = new JSONObject(string);
            String dateCreated = DateFormat.getDateTimeInstance().format(new Date((long) jsonObj.getInt("startPointTime") * 1000));
            confirm_dlg_title_tv.setText("Stop Trip " + jsonObj.getString("deviceID"));
            //confirm_tv.setText("Stop this trip created on :"+dateCreated.substring(0,dateCreated.indexOf(' '))+"?");
            confirm_btn.setText("Stop");
            confirm_dlg_date_tv.setText("Date Created: " + dateCreated);
            confirm_dlg_src_tv.setText(jsonObj.getString("source"));
            confirm_dlg_dest_tv.setText(jsonObj.getString("destination"));
            confirmDialog.show();
            confirm_btn.setOnClickListener(new OnClickListener() {
                String id_trip = jsonObj.getString("id");
                String id_acc = jsonObj.getString("accountID");
                String id_dev = jsonObj.getString("deviceID");
                String time_start = jsonObj.getString("startPointTime");

                @Override
                public void onClick(View v) {
                    if (detectCnnection.isConnectingToInternet()) {
                        System.out.println("-----------------STOP---------------");
                        new StopTrip(id_trip, id_acc, id_dev, time_start).execute();
                    } else
                        Toast.makeText(context, R.string.internet_str, Toast.LENGTH_SHORT).show();

                }

            });

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
