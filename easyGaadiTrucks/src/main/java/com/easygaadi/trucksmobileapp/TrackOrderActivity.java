package com.easygaadi.trucksmobileapp;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
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

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@TargetApi(23)
public class TrackOrderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};
    GoogleMap googleMap;
    SharedPreferences sharedPreferences;
    private ConnectionDetector detectCnnection;
    JSONParser parser;
    ProgressDialog pDialog;
    HashMap<String, String> markersMap;
    int locationCount = 0;
    Context context;
    TextView truckReg_tv, distanceTra_tv, timetra_tv, idle_tv, avgSpeed_tv;
    ScrollView bottomLayout;
    ImageView closeInfo_iv;

    private static final String[] INITIAL_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final String[] LOCATION_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int INITIAL_REQUEST = 1337;
    private static final int LOCATION_REQUEST = INITIAL_REQUEST + 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracktruck);

        context = this;
        detectCnnection = new ConnectionDetector(context);
        parser = JSONParser.getInstance();
        markersMap = new HashMap<String, String>();
        truckReg_tv = (TextView) findViewById(R.id.truckreg_tv);
        distanceTra_tv = (TextView) findViewById(R.id.distravelled_tv);
        timetra_tv = (TextView) findViewById(R.id.timetravelled_tv);
        idle_tv = (TextView) findViewById(R.id.idletime_tv);
        avgSpeed_tv = (TextView) findViewById(R.id.averagespeed_tv);
        bottomLayout = (ScrollView) findViewById(R.id.bottomlayout);
        closeInfo_iv = (ImageView) findViewById(R.id.closeinfo_tv);
        sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

        bottomLayout.setVisibility(View.GONE);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


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

        closeInfo_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                bottomLayout.setVisibility(View.GONE);
            }
        });

    }

    private void googleFunctionality() {
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_main.xml
            initializeMap();

        }

    }

    private void initializeMap() {
        if (googleMap == null) {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            fm.getMapAsync(this);
        }
    }

    public void trackTruck() {
        if (detectCnnection.isConnectingToInternet()) {
            try {
                if (getIntent().hasExtra("regno")) {
                    if (googleMap != null) {
                        googleMap.clear();
                        new TrackTruck(sharedPreferences.getString("uid", ""), getIntent().getStringExtra("oid")).execute();
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
            truckReg_tv.setText(": " + getIntent().getStringExtra("regno"));
            distanceTra_tv.setText(": " + successObj.getString("distanceTravelled"));
            timetra_tv.setText(": " + successObj.getString("timeTravelled"));
            idle_tv.setText(": " + successObj.getString("odokm"));
            avgSpeed_tv.setText(": " + successObj.getString("averageSpeed"));
            successObj = null;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ;

    }

    private void drawMarker(LatLng point, String title, JSONObject locJSON, boolean first, boolean direction) {
        try {
            JSONObject data = new JSONObject(locJSON.toString());
            // Creating an instance of MarkerOptions
            MarkerOptions markerOptions = new MarkerOptions();
            // Setting latitude and longitude for the marker
            markerOptions.position(point);

            if (!direction && first) {

                StringBuilder builder = new StringBuilder();
                builder.append("Speed  :").append(title).append("\n ").append("\nAddress :").append(getCompleteAddressString(point.latitude, point.longitude));
                markerOptions.title(builder.toString());
                data.put("direction", false);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.from_icon));
            } else if (!direction && !first) {
                StringBuilder builder = new StringBuilder();
                builder.append("Speed  :").append(title).append("\n ").append("\nAddress :").append(getCompleteAddressString(point.latitude, point.longitude));
                markerOptions.title(builder.toString());
                data.put("direction", false);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.to_icon));
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("Speed  :").append(title);
                markerOptions.title(builder.toString());
                int heading;
                data.put("direction", true);
                heading = Integer.parseInt(data.getString("heading"));
                if (heading >= 0 && heading < 25) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h0));
                } else if (heading >= 25 && heading < 70) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h1));
                } else if (heading >= 70 && heading < 110) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h2));
                } else if (heading >= 110 && heading < 160) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h3));
                } else if (heading >= 160 && heading < 200) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h4));
                } else if (heading >= 200 && heading < 240) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h5));
                } else if (heading >= 240 && heading < 290) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h6));
                } else if (heading >= 290 && heading < 330) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h7));
                } else if (heading >= 330 && heading < 390) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h0));
                } else if (heading >= 390 && heading < 420) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h1));
                } else if (heading >= 420 && heading < 450) {
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.h2));
                } else if (heading >= 450 && heading < 500) {
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
                Log.w("My Current address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onMapReady(GoogleMap mGoogleMap) {
        this.googleMap = mGoogleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // Setting a custom info window adapter for the google map
        googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

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


                    TextView currentloc_tv = (TextView) v.findViewById(R.id.currentloc_tv);
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
                    odometer_tv.setVisibility(View.GONE);
                    //odometer_tv.setText(Html.fromHtml("<b>Odometer : </b>"+jObj.getString("odometer")));
                    speed_tv.setText(Html.fromHtml("<b>Speed : </b>" + jObj.getString("speed")));
                    regno_tv.setText(Html.fromHtml("<b>Reg.No : </b>" + getIntent().getStringExtra("regno")));
                    time_tv.setText(Html.fromHtml("<b>Date : </b>" + TruckApp.secToDate(jObj.getInt("time_in_secs"), context)));
                    geo_latlon_tv.setText(Html.fromHtml("<b>GPS : </b>" + f.format(jObj.getDouble("latitude")) + "/" + f.format(jObj.getDouble("longitude"))));
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

        String uid, oid;

        public TrackTruck(String uid, String oid) {
            this.uid = uid;
            this.oid = oid;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Tracking order...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                String urlParameters =
                        "uid=" + URLEncoder.encode(uid, "UTF-8") +
                                "&oid=" + URLEncoder.encode(oid, "UTF-8");
                String res = parser.excutePost(TruckApp.trackOrderURL, urlParameters);
                //System.out.println("URL:"+TruckApp.trackOrderURL+ urlParameters);
                json = new JSONObject(res);
                //System.out.println("DoIN obj"+res);
            } catch (Exception e) {
                Log.e("Login DoIN EX", e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                try {
                    System.out.println("result obj" + result.toString());
                    if (0 == result.getInt("status")) {
                        TruckApp.checkPDialog(pDialog);
                        Toast.makeText(context, "No records found", Toast.LENGTH_LONG).show();
                    } else if (1 == result.getInt("status")) {
                        JSONObject successObj = result.getJSONObject("success");
                        setTruckDetails(successObj);
                        System.out.println("success obj" + successObj.toString());
                        JSONArray truckArray = successObj.getJSONArray("points");
                        System.out.println("truckArrayS obj" + truckArray.toString());
                        if (truckArray.length() > 0) {
                            /*locateTrucks(truckArray);*/
                            drawRoute(truckArray);
                        } else {
                            TruckApp.checkPDialog(pDialog);
                            Toast.makeText(context, "No truck found", Toast.LENGTH_LONG).show();
                        }

                    }
                } catch (Exception e) {
                    TruckApp.checkPDialog(pDialog);
                    System.out.println("ex in get leads" + e.toString());
                }
                TruckApp.checkPDialog(pDialog);
            } else {
                Toast.makeText(context, getResources().getString(R.string.exceptionmsg), Toast.LENGTH_LONG).show();
                TruckApp.checkPDialog(pDialog);
            }
        }
    }


    public void drawRoute(JSONArray jsonArray) {
        if (googleMap != null) {
            markersMap.clear();
            /*String zoom = "17";*/
            googleMap.clear();
            try {

                if (jsonArray.length() > 0) {
                    ArrayList<LatLng> points = null;
                    PolylineOptions polyLineOptions = null;
                    System.out.println("i");
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();

                    com.google.android.gms.maps.model.LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();

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
                    try {
                        JSONObject jObj = jsonArray.getJSONObject(0);
                        LatLng latlng1 = new LatLng(jObj.getDouble("latitude"), jObj.getDouble("longitude"));
                        drawMarker(latlng1, jObj.getString("speed"), jObj, false, false);
                        jObj = null;
                        if (jsonArray.length() > 2) {
                            JSONObject jObjLast = jsonArray.getJSONObject(jsonArray.length() - 1);
                            LatLng latlng2 = new LatLng(jObjLast.getDouble("latitude"), jObjLast.getDouble("longitude"));
                            drawMarker(latlng2, jObjLast.getString("speed"), jObjLast, true, false);
                            jObjLast = null;
                            // Moving CameraPosition to last clicked position
                            if (jsonArray.length() > 50) {

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    if (i != 0 && i != jsonArray.length() - 1 && i % 50 == 0) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        LatLng latlngObj = new LatLng(jsonObject.getDouble("latitude"), jsonObject.getDouble("longitude"));
                                        drawMarker(latlngObj, jsonObject.getString("speed"), jsonObject, false, true);
                                        jsonObject = null;
                                        latlngObj = null;
                                    }
                                }

                            }
                        }

                        LatLngBounds bounds = boundsBuilder.build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 10);
                        googleMap.moveCamera(cameraUpdate);

						/*// Setting the zoom level in the map on last position is clicked
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat("12")));*/

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
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                trackTruck();
                break;
            case R.id.action_info:
                bottomLayout.setVisibility(View.VISIBLE);
                break;
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.change_map:
                if (googleMap != null) {
                    showMapTypeSelectorDialog();
                } else {
                    Toast.makeText(context, "Can't change now, Try Later", Toast.LENGTH_SHORT).show();
                }
                break;
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

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //initializeMap();
        TruckApp.getInstance().trackScreenView("TrackOrder Screen");
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


}
