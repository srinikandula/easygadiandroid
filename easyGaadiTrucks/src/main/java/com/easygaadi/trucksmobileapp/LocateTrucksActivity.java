package com.easygaadi.trucksmobileapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class LocateTrucksActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Road Map", "Hybrid", "Satellite", "Terrain"};

    GoogleMap googleMap;
    SharedPreferences sharedPreferences;
    int locationCount = 0;
    private ConnectionDetector detectCnnection;
    Context context;
    JSONParser parser;
    ProgressDialog pDialog;
    Editor editor;
    ArrayList<String> regNoList = new ArrayList<String>();
    int egAccountid;
    AutoCompleteTextView actvRegNo;
    HashMap<String, String> markersMap;
    HashMap<String, Integer> regNoMap = new HashMap<String, Integer>();
    JSONArray trkArray;
    String groupName = "", groupId = "";
    String vehicleType="";
    Toolbar toolbar;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locatetrucks);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        adView = (AdView)findViewById(R.id.adView);
        intializeBannerAd();

        try {
            groupId = getIntent().getStringExtra(Constants.GROUP_ID);
            groupName = getIntent().getStringExtra(Constants.GROUP_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (groupName != null && !groupName.equals("") && getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(groupName);
        }
        context = this;
        detectCnnection = new ConnectionDetector(context);
        parser = JSONParser.getInstance();
        markersMap = new HashMap<String, String>();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        egAccountid = sharedPreferences.getInt("egAccount", -1);

        actvRegNo = (AutoCompleteTextView) findViewById(R.id.search_truck_no_actv);

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            // not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
                    requestCode);
            dialog.show();

        } else { // Google Play Services are available
            // Getting reference to the SupportMapFragment of activity_main.xml
            initializeMap();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initializeMap();
        TruckApp.getInstance().trackScreenView("GPSMap Screen");
        if (adView != null) {
            adView.resume();
        }

    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }



    private void intializeBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }



    private void initializeMap() {
        if (googleMap == null) {
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            fm.getMapAsync(this);
        }
    }

    public void locateTrucks() {
        if (detectCnnection.isConnectingToInternet()) {
            try {
                System.out.println("AccId:"
                        + sharedPreferences.getString("accountID",
                        "no accountid "));
                new TrackAllVehicles(sharedPreferences.getString("accountID",
                        ""), groupId, groupName).execute();
            } catch (Exception e) {
                System.out.println("Ex inn on resume:" + e.toString());
            }
        } else {
            Toast.makeText(context,
                    getResources().getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void drawMarker(LatLng point, String title, String data,
                            String deviceId, int speed, String momentStatus,float heading) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

       // IconGenerator ig = new IconGenerator(this);
        // Setting latitude and longitude for the marker
        markerOptions.position(point);
        if (title.trim().length() == 0) {
            markerOptions.title("No Truck Reg. No\n" + deviceId);
        } else {
            markerOptions.title(title);
        }
        markerOptions.rotation(heading);
        int drawable_id;

        if(momentStatus.equalsIgnoreCase(getResources().getString(R.string.running_str))){
            drawable_id = (vehicleType.equalsIgnoreCase("CR"))?R.drawable.car_running:R.drawable.truck_running;
            markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(drawable_id));
        } else if(momentStatus.equalsIgnoreCase(getResources().getString(R.string.damage_str))){
            drawable_id = (vehicleType.equalsIgnoreCase("CR"))?R.drawable.car_damage:R.drawable.truck_damage;
            markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(drawable_id));
        } else if(momentStatus.equalsIgnoreCase(getResources().getString(R.string.long_stop_str))){
            drawable_id = (vehicleType.equalsIgnoreCase("CR"))?R.drawable.car_long_stop:R.drawable.truck_long_stop;
            markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(drawable_id));
        } else if(momentStatus.equalsIgnoreCase(getResources().getString(R.string.stop_str))){
            drawable_id = (vehicleType.equalsIgnoreCase("CR"))?R.drawable.car_stop:R.drawable.truck_stop;
            markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(drawable_id));
        }
        /*if (speed == 0) {
            markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.stop_red));
        } else {
            markerOptions.icon(BitmapDescriptorFactory
                    .fromResource(R.drawable.from_icon));
        }*/

		/*if(speed == 0) { 
            ig.setStyle(IconGenerator.STYLE_RED);
		} else ig.setStyle(IconGenerator.STYLE_GREEN);
		markerOptions.icon(BitmapDescriptorFactory.fromBitmap(ig.makeIcon(deviceId))).anchor(0.5f, 0.7f);*/
        // Adding marker on the Google Map
        Marker marker = googleMap.addMarker(markerOptions);
        if (title.trim().length() != 0) {
            markersMap.put(marker.getId(), data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.locate_truck_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_refresh:
                locateTrucks();
                break;
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
    public void onMapReady(GoogleMap mGoogleMap) {
        System.out.println("onMapReady");
        this.googleMap = mGoogleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (markersMap.containsKey(marker.getId())) {
                    View v = getLayoutInflater().inflate(
                            R.layout.marker_item, null);

                    try {
                        // Getting view from the layout file
                        // info_window_layout

                        String data = markersMap.get(marker.getId());
                        final JSONObject jObj = new JSONObject(data);

                        String add_str = TruckApp.getCompleteAddressString(
                                context, jObj.getDouble("latitude"),
                                jObj.getDouble("longitude"));
                        TextView currentloc_tv = (TextView) v
                                .findViewById(R.id.currentloc_tv);

                        TextView speed_tv = (TextView) v
                                .findViewById(R.id.speed_tv);
                        TextView odometer_tv = (TextView) v
                                .findViewById(R.id.odometer_tv);
                        TextView regno_tv = (TextView) v
                                .findViewById(R.id.regno_tv);
                        TextView time_tv = (TextView) v
                                .findViewById(R.id.time_tv);
                        TextView geo_latlon_tv = (TextView) v
                                .findViewById(R.id.geo_latlon);
                        TextView address_tv = (TextView) v
                                .findViewById(R.id.address_tv);
                        TextView contact_tv = (TextView) v
                                .findViewById(R.id.contact_tv);

							/*
                             * TextView track_tv = (TextView)
							 * v.findViewById(R.id.track_tv);
							 */
                        DecimalFormat f = new DecimalFormat("##.0000");
                        currentloc_tv.setText(Html
                                .fromHtml("<b>Current Loc : </b>"
                                        + (add_str.split(",")[0])));
                        speed_tv.setText(Html.fromHtml("<b>Speed : </b>"
                                + jObj.getString("speed")));
                        odometer_tv.setText(Html
                                .fromHtml("<b>Odometer : </b>"
                                        + jObj.getString("odometer")));
                        regno_tv.setText(Html.fromHtml("<b>Reg.No : </b>"
                                + jObj.getString("truck_no")));
                        /*time_tv.setText(Html.fromHtml("<b>Date : </b>"
                                + TruckApp.secToDate(
                                jObj.getInt("time_in_secs"),
                                context)));date_time*/
                        time_tv.setText(Html.fromHtml("<b>Date : </b>"
                                +  jObj.getString("date_time")));
                        geo_latlon_tv.setText(Html.fromHtml("<b>GPS : </b>"
                                + f.format(jObj.getDouble("latitude"))
                                + "/"
                                + f.format(jObj.getDouble("longitude"))));
                        address_tv.setText(Html
                                .fromHtml("<b>Address : </b>" + add_str));
                        if (egAccountid == 1) {

                            contact_tv.setText(Html
                                    .fromHtml("<b>Contact : </b>"
                                            + jObj.getString("contact")));
                            contact_tv.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Returning the view containing InfoWindow contents
                    return v;
                } else {
                    Toast.makeText(context, "No truck no found",
                            Toast.LENGTH_LONG).show();
                }
                return null;
            }
        });
        locateTrucks();
    }

    private class TrackAllVehicles extends
            AsyncTask<String, String, JSONObject> {

        String account_id, groupId, groupName;

        public TrackAllVehicles(String account_id, String groupId, String groupName) {
            this.account_id = account_id;
            this.groupId = groupId;
            this.groupName = groupName;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Fetching trucks data...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("account_id=").append(URLEncoder.encode(account_id, "UTF-8"))
                        .append(builder.append("&gid=").append(URLEncoder.encode(groupId, "UTF-8")))
                        .append(builder.append("&group=").append(URLEncoder.encode(groupName, "UTF-8")))
                        .append(builder.append("&access_token=")).append(sharedPreferences.getString("access_token",""));

                String res = parser.excutePost(TruckApp.TRACK_ALL_VEHICLES_URL,
                        builder.toString());
                System.out.println("LTA:" + res);
                json = new JSONObject(res);
            } catch (Exception e) {
                Log.e("Login DoIN EX", e.toString());
            }
            return json;

        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            TruckApp.checkPDialog(pDialog);
            if (result != null) {
                try {
                    if (0 == result.getInt("status")) {
                        Toast.makeText(
                                context,
                                getResources().getString(
                                        R.string.Deviceserrormsg),
                                Toast.LENGTH_LONG).show();
                    } else if (1 == result.getInt("status")) {
                        JSONArray truckArray = result.getJSONArray("success");
                        if (truckArray.length() > 0) {
                            trkArray = truckArray;
                            locateTrucks(result.getJSONArray("success"));
                        } else {
                            Toast.makeText(context, "No truck found",
                                    Toast.LENGTH_LONG).show();
                        }
                    }else if(2==result.getInt("status")){
                        //TruckApp.logoutAction(LocateTrucksActivity.this);
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name), MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(Constants.FUEL_USERNAME_KEY,"").commit();
                        editor.putString(Constants.FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(Constants.FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(Constants.TOLL_USERNAME_KEY,"").commit();
                        editor.putString(Constants.TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(Constants.TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(context,
                                LoginActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                    System.out.println("ex in get leads" + e.toString());
                }
            } else {
                Toast.makeText(context,
                        getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void locateTrucks(JSONArray jsonArray) {
        if (googleMap != null) {
            markersMap.clear();

            googleMap.clear();
            try {
                com.google.android.gms.maps.model.LatLngBounds.Builder boundsBuilder = LatLngBounds
                        .builder();
                LatLng loc = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    regNoMap.put(obj.getString("truck_no"), i);
                    regNoList.add(obj.getString("truck_no"));


                    loc = new LatLng(Double.valueOf(obj.getString("latitude")),
                            Double.valueOf(obj.getString("longitude")));
                    boundsBuilder.include(loc);
                    vehicleType = obj.getString("vehicleType");
                    drawMarker(loc, obj.getString("truck_no"), obj.toString(),
                            obj.getString("deviceID"), obj.getInt("speedValue"),obj.getString("momentStatus"),Float.parseFloat(obj.getString("heading")));
                }

                //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, regNoList);
                final TrucksAutoCompleteAdapter adapter = new TrucksAutoCompleteAdapter(context, R.layout.list_text_item, regNoList);
                actvRegNo.setThreshold(1);
                actvRegNo.setAdapter(adapter);
                actvRegNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(LocateTrucksActivity.this, adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
                        int pos = regNoMap.get(adapter.getItem(position).toString());
                        zoomTruck(pos);
                    }

                });
                LatLngBounds bounds = boundsBuilder.build();
                CameraUpdate cameraUpdate = CameraUpdateFactory
                        .newLatLngBounds(bounds, 10);
                // Moving CameraPosition to last clicked position
                googleMap.moveCamera(cameraUpdate);

                // Setting the zoom level in the map on last position is clicked
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float
                        .parseFloat("6")));
                googleMap
                        .setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

                            @Override
                            public void onInfoWindowClick(Marker arg0) {
                                if (markersMap.containsKey(arg0.getId())) {
                                    try {
                                        // JSONObject jObj = new
                                        // JSONObject(markersMap.get(arg0.getId()));
                                        try {
                                            Intent next_in = new Intent(
                                                    context,
                                                    SearchDialogActivity.class);
                                            next_in.putExtra("json", markersMap
                                                    .get(arg0.getId()));
                                            startActivity(next_in);
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                /*
                                 * Intent in = new
								 * Intent(getApplicationContext
								 * (),TrackTruckActivity.class);
								 * in.putExtra
								 * ("regno",jObj.getString("truck_no"));
								 * in.putExtra("urlParams","days=1");
								 * startActivity(in);
								 */
                                    } catch (Exception e) {
                                        System.out.println("issue:"
                                                + e.toString());
                                    }
                                } else {
                                    Toast.makeText(context,
                                            "No truck no found",
                                            Toast.LENGTH_LONG).show();
                                }

                            }
                        });

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void zoomTruck(int pos) {
        JSONObject obj = null;
        try {
            obj = trkArray.getJSONObject(pos);
            LatLng loc1 = new LatLng(Double.valueOf(obj.getString("latitude")), Double.valueOf(obj.getString("longitude")));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc1, 10));
            // Zoom in, animating the camera.
            googleMap.animateCamera(CameraUpdateFactory.zoomIn());

            // Setting the zoom level in the map on last position is clicked
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat("15")));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        googleMap = null;
        parser = null;
        pDialog = null;
        markersMap = null;
        sharedPreferences = null;
        editor = null;
        if (adView != null)
        { adView.destroy(); }
        super.onDestroy();
    }

}
