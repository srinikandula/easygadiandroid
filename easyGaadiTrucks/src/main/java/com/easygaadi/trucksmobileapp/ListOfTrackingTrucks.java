package com.easygaadi.trucksmobileapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.ExceptionHandler;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ListOfTrackingTrucks extends AppCompatActivity {

    Context context;
    SharedPreferences sharedPreferences;
    private ConnectionDetector detectConnection;
    JSONParser parser;
    ProgressDialog pDialog;
    ListView trucksLV;
    Dialog updateTruckDialog;
    Dialog createTripDialog;
    JSONArray trucksArray, allTrucksArray;
    TrackingTrucksAdapter adapter;
    HashMap<Integer, String> addressMap;
    SimpleDateFormat sdf;
    String currentDateandTime;
    String curenttime, aa;
    int egAccountid;
    long diff;
    long Objsec, Curent;
    long timeStamp = 86400000;
    boolean destset = false;
    Toolbar toolbar;
    TextView insurance_expiry_tv, fitness_expiry_tv, national_permit_tv, updia_tit_tv, trip_dlg_title_tv, trip_dest_tv;
    EditText rc_no_et, insurance_amount_et, trip_start_pt_et;
    AutoCompleteTextView trip_end_pt_et;
    CheckBox national_permit_cb;
    LinearLayout natpermit_layout;
    ImageView close_upd_truck_iv, close_create_trip_iv;
    Button update_btn, create_trip_btn;
    DatePickerDialog dateDialog;
    String groupId = "", groupName = "";
    EditText searchTruck;
    Context mContext;
    private InterstitialAd mInterstitialAd;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_trackingtruckslist);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        adView = (AdView)findViewById(R.id.adView);
        setSupportActionBar(toolbar);
        mContext = this;
        //intializeBannerAd();
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
        searchTruck = (EditText) findViewById(R.id.search_truck);
        searchTruck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterTrucks(searchTruck.getText().toString().trim().toLowerCase());
            }
        });
        detectConnection = new ConnectionDetector(context);
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

        trucksLV = (ListView) findViewById(R.id.tratrucks_lv);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(ListOfTrackingTrucks.this));


        fetchTrucks(0);

        //intializeGoogleAd();
        //intializeBannerAd();
        sdf = new SimpleDateFormat("dd-mm-yy HH:mm");
        currentDateandTime = sdf.format(new Date());

        updateTruckDialog = new Dialog(ListOfTrackingTrucks.this,
                android.R.style.Theme_Dialog);
        updateTruckDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateTruckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        updateTruckDialog.setContentView(R.layout.updatetruckinfo_dia);

        createTripDialog = new Dialog(ListOfTrackingTrucks.this, android.R.style.Theme_Dialog);
        createTripDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        createTripDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        createTripDialog.setContentView(R.layout.dialog_create_trip);

        insurance_amount_et = (EditText) updateTruckDialog.findViewById(R.id.ins_amount_et);
        rc_no_et = (EditText) updateTruckDialog.findViewById(R.id.rc_no_et);
        insurance_expiry_tv = (TextView) updateTruckDialog.findViewById(R.id.insurance_expiry_et);
        national_permit_tv = (TextView) updateTruckDialog.findViewById(R.id.national_permit_et);
        fitness_expiry_tv = (TextView) updateTruckDialog.findViewById(R.id.fitness_expiry_et);
        updia_tit_tv = (TextView) updateTruckDialog.findViewById(R.id.dialogtitle_tv);

        national_permit_cb = (CheckBox) updateTruckDialog.findViewById(R.id.np_cb);
        natpermit_layout = (LinearLayout) updateTruckDialog.findViewById(R.id.np_layout);
        close_upd_truck_iv = (ImageView) updateTruckDialog.findViewById(R.id.close_iv);
        update_btn = (Button) updateTruckDialog.findViewById(R.id.update_btn);

        trip_dlg_title_tv = (TextView) createTripDialog.findViewById(R.id.create_trip_dialogtitle_tv);
        trip_start_pt_et = (EditText) createTripDialog.findViewById(R.id.create_trip_start_et);
        trip_end_pt_et = (AutoCompleteTextView) createTripDialog.findViewById(R.id.create_trip_end_et);
        create_trip_btn = (Button) createTripDialog.findViewById(R.id.create_trip_btn);
        close_create_trip_iv = (ImageView) createTripDialog.findViewById(R.id.close_create_trip_dlg_iv);
        trip_dest_tv = (TextView) createTripDialog.findViewById(R.id.create_trip_end_tv);
        TruckApp.setMandatory(trip_dest_tv, trip_dest_tv.getText().toString());
        close_upd_truck_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (updateTruckDialog != null && updateTruckDialog.isShowing()) {
                    updateTruckDialog.dismiss();
                }
            }
        });

        close_create_trip_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (createTripDialog != null && createTripDialog.isShowing()) {
                    createTripDialog.dismiss();
                }
            }
        });

        egAccountid = sharedPreferences.getInt("egAccount", -1);

        trucksLV.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                try {
                    Intent next_in = new Intent(context,SearchDialogActivity.class);
                    next_in.putExtra("json", trucksArray.getJSONObject(arg2).toString());
                    startActivity(next_in);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
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

    private void intializeBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void filterTrucks(String searchQuery) {
        trucksArray = new JSONArray();
        try {
            for (int i = 0; i < allTrucksArray.length(); i++) {
                JSONObject jObj = allTrucksArray.getJSONObject(i);
                boolean isFiltered = false;
                String address = jObj.getString("address");
                String truckNumber = jObj.getString("truck_no");
                if (truckNumber != null && !truckNumber.equals("")) {
                    if (truckNumber.toLowerCase().contains(searchQuery)) {
                        isFiltered = true;
                        System.out.println("isFiltered1" + ":" + truckNumber + ":" + searchQuery + ":" + true);
                    }
                }
                if (!isFiltered) {
                    if (address != null && !address.equals("")) {
                        if (address.toLowerCase().contains(searchQuery)) {
                            isFiltered = true;
                            System.out.println("isFiltered2" + ":" + address + ":" + searchQuery + ":" + true);
                        }
                    }
                }
                if (isFiltered) {
                    trucksArray.put(jObj);
                }
            }
            if (adapter != null) {
                adapter.setTrucks(trucksArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
    }

    public void fetchTrucks(int offset) {
        if (detectConnection.isConnectingToInternet()) {
            if (0 == offset) {
                trucksArray = new JSONArray();
                allTrucksArray = new JSONArray();
                addressMap = new HashMap<Integer, String>();
                adapter = null;
            }
            try {
                new GetTrackingTrucks(sharedPreferences.getString("accountID", ""), groupId, groupName).execute();
            } catch (Exception e) {
                System.out.println("Ex inn on resume:" + e.toString());
            }
        } else {
            Toast.makeText(context,
                    getResources().getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }
    }

    private class GetTrackingTrucks extends AsyncTask<String, String, JSONObject> {

        String uid, groupId, groupName;

        public GetTrackingTrucks(String uid, String groupId, String groupName) {
            this.uid = uid;
            this.groupId = groupId;
            this.groupName = groupName;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Fetching trucks locations...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("account_id=").append(URLEncoder.encode(uid, "UTF-8"))
                        .append(builder.append("&gid=").append(URLEncoder.encode(groupId, "UTF-8")))
                        .append(builder.append("&group=").append(URLEncoder.encode(groupName, "UTF-8")))
                        .append(builder.append("&access_token=")).append(sharedPreferences.getString("access_token",""));
                String res = parser.excutePost(TruckApp.TRACK_ALL_VEHICLES_URL, builder.toString());
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
            if (result != null) {
                try {
                    if (0 == result.getInt("status")) {
                       Toast.makeText(context, "No records found",Toast.LENGTH_LONG).show();
                    } else if (1 == result.getInt("status")) {
                        trucksArray = result.getJSONArray("success");
                        allTrucksArray = result.getJSONArray("success");
                        if (trucksArray.length() == 0) {
                            Toast.makeText(context, "No records available",Toast.LENGTH_LONG).show();
                        } else {
                            adapter = new TrackingTrucksAdapter(trucksArray, ListOfTrackingTrucks.this);
                            trucksLV.setAdapter(adapter);
                        }
                    }else if(2== result.getInt("status")){
                        //TruckApp.logoutAction(ListOfTrackingTrucks.this);
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name), MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(Constants.FUEL_USERNAME_KEY,"").commit();
                        editor.putString(Constants.FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(Constants.FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(Constants.TOLL_USERNAME_KEY,"").commit();
                        editor.putString(Constants.TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(Constants.TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                    System.out.println("ex in get leads" + e.toString());
                }
                TruckApp.checkPDialog(pDialog);
            } else {
                TruckApp.checkPDialog(pDialog);
                Toast.makeText(context,getResources().getString(R.string.exceptionmsg), Toast.LENGTH_LONG).show();
            }

        }
    }

    public class TrackingTrucksAdapter extends BaseAdapter {
        Activity activity;
        LayoutInflater inflater;
        JSONArray trucks;

        public void setTrucks(JSONArray trucks) {
            this.trucks = trucks;
            notifyDataSetChanged();
        }

        public TrackingTrucksAdapter(JSONArray loads, Activity activity) {
            this.trucks = loads;
            this.activity = activity;
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public class ViewHolder {
            TextView truckreg_tv, place_tv, speed_tv, meter_tv, contact_tv,lastupdate_tv,message_tv;
            LinearLayout CantactLL, GPS_Color_LL;
            CheckBox lfl_cb;//looking for load checkbox
            ImageView edit_img, trip_img;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return trucks.length();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            try {
                return trucks.get(arg0);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewholder = null;
            if (convertView == null) {
                viewholder = new ViewHolder();
                convertView = inflater.inflate(R.layout.trackingtruck_item,parent, false);
                viewholder.truckreg_tv = (TextView) convertView.findViewById(R.id.truckRegNo_tv);
                viewholder.place_tv = (TextView) convertView.findViewById(R.id.place_tv);
                viewholder.speed_tv = (TextView) convertView.findViewById(R.id.speed_tv);
                viewholder.meter_tv = (TextView) convertView.findViewById(R.id.meter_tv);
                viewholder.message_tv = (TextView) convertView.findViewById(R.id.tv_message);

                viewholder.contact_tv = (TextView) convertView.findViewById(R.id.tv_contact);

                viewholder.lastupdate_tv = (TextView) convertView.findViewById(R.id.tv_lastupadate);

                viewholder.edit_img = (ImageView) convertView.findViewById(R.id.edit_img);

                viewholder.trip_img = (ImageView) convertView.findViewById(R.id.trip_img);

                viewholder.GPS_Color_LL = (LinearLayout) convertView.findViewById(R.id.LL_GpsColr);
                viewholder.lfl_cb = (CheckBox) convertView.findViewById(R.id.lookingforload_cb);
                // viewholder.CantactLL =
                // (LinearLayout)findViewById(R.id.cantactLL);
                convertView.setTag(viewholder);
            } else {
                viewholder = (ViewHolder) convertView.getTag();
            }
            setData(position, viewholder);
            return convertView;
        }

        private void setData(int position, ViewHolder viewholder) {
            try {
                JSONObject jObj = trucks.getJSONObject(position);
                viewholder.place_tv.setText(jObj.getString("address").equals("") ? "Location Unavailable" : jObj.getString("address"));
                viewholder.truckreg_tv.setText(jObj.getString("truck_no"));
                viewholder.speed_tv.setText(jObj.getString("speed"));
                viewholder.meter_tv.setText(jObj.getString("odometer"));
                viewholder.lfl_cb.setTag(position + ";" + jObj.toString());
                //viewholder.edit_img.setTag(position + ";" + jObj.toString());
                viewholder.edit_img.setTag(jObj.getString("deviceID"));
                viewholder.trip_img.setTag(position + ";" + jObj.toString());
                if (egAccountid == 1) {
                    viewholder.contact_tv.setText(jObj.getString("contact"));
                    viewholder.contact_tv.setVisibility(View.VISIBLE);
                }else{
                    viewholder.contact_tv.setVisibility(View.GONE);
                }

                if(jObj.has("momentMsg") && !jObj.getString("momentMsg").isEmpty()){
                    viewholder.message_tv.setVisibility(View.VISIBLE);
                    viewholder.message_tv.setText(jObj.getString("momentMsg"));
                }else{
                    viewholder.message_tv.setVisibility(View.GONE);
                }
                if(jObj.getString("vehicleType")!=null && jObj.getString("vehicleType").equalsIgnoreCase("CR")){
                    viewholder.truckreg_tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tracar_icon, 0, 0);
                }else{
                    viewholder.truckreg_tv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tratruck_icon, 0, 0);
                }
                if (jObj.getString("vehicleType") != null && jObj.getString("vehicleType").equals("TK")) {
                    viewholder.lfl_cb.setVisibility(View.VISIBLE);
                    if (jObj.getString("lookingForLoad") != null && jObj.getString("lookingForLoad").equals("0")) {
                        viewholder.lfl_cb.setChecked(false);
                    } else {
                        viewholder.lfl_cb.setChecked(true);
                    }
                } else {
                    viewholder.lfl_cb.setVisibility(View.GONE);
                }

                viewholder.lastupdate_tv.setText(jObj.getString("date_time"));
                String Objtime = viewholder.lastupdate_tv.getText().toString();

                try {

                    Date date1 = sdf.parse(Objtime);
                    Objsec = date1.getTime();
                    String ObjDate = String.valueOf(Objsec);

                    Date date2 = sdf.parse(currentDateandTime);
                    Curent = date2.getTime();
                    String CurentDate = String.valueOf(Curent);


                } catch (Exception e) {
                    // TODO: handle exception
                }
                if(jObj.has("momentStatus")){
                    if(jObj.getString("momentStatus").equalsIgnoreCase("Running")){
                        viewholder.GPS_Color_LL.setBackgroundColor(Color.parseColor("#456D0B"));
                    }else if(jObj.getString("momentStatus").equalsIgnoreCase("Stop")){
                        viewholder.GPS_Color_LL.setBackgroundColor(Color.parseColor("#FF7F00"));
                    }else if(jObj.getString("momentStatus").equalsIgnoreCase("Long Stop")){
                        viewholder.GPS_Color_LL.setBackgroundColor(Color.parseColor("#FF0000"));
                    }else if(jObj.getString("momentStatus").equalsIgnoreCase("Damage")){
                        viewholder.GPS_Color_LL.setBackgroundColor(Color.parseColor("#A31919"));
                    }
                } else {
                    viewholder.GPS_Color_LL.setBackgroundColor(Color.parseColor("#456D0B"));
                    viewholder.place_tv.setBackgroundColor(Color.parseColor("#456D0B"));
                }
                /*if (jObj.has("damage") && jObj.getString("damage") != null && jObj.getString("damage").equals("1")) {
                    viewholder.GPS_Color_LL.setBackgroundColor(Color.parseColor("#A31919"));
                } else if (jObj.has("speed") && jObj.getString("speed").trim().equals("0 Km/Hr")) {
                    viewholder.GPS_Color_LL.setBackgroundColor(Color.parseColor("#FF7F00"));
                } else {
                    viewholder.GPS_Color_LL.setBackgroundColor(Color.parseColor("#456D0B"));
                }*/

                viewholder.lfl_cb.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        CheckBox cb = (CheckBox) v;
                        String[] array = (cb.getTag().toString()).split(";");
                        if (cb.isChecked()) {
                            new LookingForLoad(Integer.parseInt(array[0]), 1, array[1]).execute();
                        } else {
                            new LookingForLoad(Integer.parseInt(array[0]), 0, array[1]).execute();
                        }
                    }
                });
                boolean ins_time=false,fit_time=false, np_time= false;
                if(jObj.has("insuranceExpire")){
                     ins_time = TruckApp.checkTime(jObj.getString("insuranceExpire") + " 00:00:00");
                }

                if(jObj.has("fitnessExpire")){
                     fit_time = TruckApp.checkTime(jObj.getString("fitnessExpire") + " 00:00:00");
                }

                if(jObj.has("NPExpire")){
                     np_time = TruckApp.checkTime(jObj.getString("NPExpire") + " 00:00:00");
                }

                /*if (ins_time && fit_time && np_time) {
                    viewholder.edit_img.setImageResource(R.drawable.ic_edit_blue);
                } else {
                    viewholder.edit_img.setImageResource(R.drawable.ic_edit_r);
                }*/

                viewholder.edit_img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(mContext,"Cliecke edit button,",Toast.LENGTH_LONG).show();
                        Intent editTruck = new Intent(mContext,TruckDetailsActivity.class);
                        editTruck.putExtra("deviceID",view.getTag().toString());
                        startActivity(editTruck);
                        /*updateTruckDialog(Integer.parseInt(array[0]), array[1]);*/
                    }
                });

                viewholder.trip_img.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String[] array = (v.getTag().toString()).split(";");
                        trip_end_pt_et.setText("");
                        createTripDialog(Integer.parseInt(array[0]), array[1]);
                    }

                });

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }



        protected void createTripDialog(final int parseInt, String string) {

            try {
                final JSONObject jsonObj = new JSONObject(string);
                //trip_dlg_title_tv.setText("Create Trip "+jsonObj.getString("deviceID"));
                TruckApp.spanTextview(trip_dlg_title_tv, "Create Trip ", jsonObj.getString("deviceID"));
                trip_start_pt_et.setText(jsonObj.getString("address"));
                trip_start_pt_et.setEnabled(false);
                createTripDialog.show();
                trip_end_pt_et.setAdapter(new GooglePlaceAutoCompleteAdapter(context, R.layout.list_text_item, "India", false));
                trip_end_pt_et.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        destset = true;

                    }
                });
                trip_end_pt_et.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (destset) destset = false;
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub

                    }
                });
                create_trip_btn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (detectConnection.isConnectingToInternet()) {

                            String trip_start_pt = trip_start_pt_et.getText().toString();
                            String trip_end_pt = trip_end_pt_et.getText().toString();
                            String dev_id = "";
                            try {
                                dev_id = jsonObj.getString("deviceID");
                            } catch (JSONException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            if (dev_id.length() != 0 && trip_start_pt.length() != 0 && trip_end_pt.length() != 0 && destset) {
                                try {
                                    new CreateTrip(dev_id, trip_start_pt, trip_end_pt).execute();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } else {
                                TruckApp.editTextValidation(trip_start_pt_et, trip_start_pt, "enter source");
                                TruckApp.editTextValidation(trip_end_pt_et, trip_end_pt, "enter destinatio");
                                TruckApp.autocompleteValidation(trip_end_pt_et, destset, "select destination from dropdown");
                            }
                        } else {
                            Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        protected void updateTruckDialog(final int parseInt, String string) {
            updateTruckDialog.show();
            try {
                final JSONObject jsonObj = new JSONObject(string);
                updia_tit_tv.setText(jsonObj.getString("deviceID") + " Truck Info");
                insurance_expiry_tv.setText(jsonObj.getString("insuranceExpire"));
                fitness_expiry_tv.setText(jsonObj.getString("fitnessExpire"));
                national_permit_tv.setText(jsonObj.getString("NPExpire"));
                boolean ins_time = TruckApp.checkTime(jsonObj.getString("insuranceExpire") + " 00:00:00");
                boolean fit_time = TruckApp.checkTime(jsonObj.getString("fitnessExpire") + " 00:00:00");
                boolean np_time = TruckApp.checkTime(jsonObj.getString("NPExpire") + " 00:00:00");

                if (!ins_time) {
                    insurance_expiry_tv.setError("");
                } else {
                    insurance_expiry_tv.setError(null);
                }
                if (!fit_time) {
                    fitness_expiry_tv.setError("");
                } else {
                    fitness_expiry_tv.setError(null);
                }
                if (!np_time) {
                    national_permit_tv.setError("");
                } else {
                    national_permit_tv.setError(null);
                }
                if (jsonObj.getString("NPAvailable") != null && jsonObj.getString("NPAvailable").equalsIgnoreCase("0")) {
                    natpermit_layout.setVisibility(View.GONE);
                    ;
                } else {
                    natpermit_layout.setVisibility(View.VISIBLE);
                    ;
                }

                rc_no_et.setText(jsonObj.getString("rcNo"));
                insurance_amount_et.setText(jsonObj.getString("insuranceAmount"));
                insurance_expiry_tv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Calendar c = Calendar.getInstance();
                        dateDialog = new DatePickerDialog(context, mInsDateLst, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        dateDialog.show();
                    }
                });
                fitness_expiry_tv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Calendar c = Calendar.getInstance();
                        dateDialog = new DatePickerDialog(context, mFitDateLst, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        dateDialog.show();
                    }
                });

                national_permit_tv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Calendar c = Calendar.getInstance();

                        dateDialog = new DatePickerDialog(context, mNPDateLst, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        dateDialog.show();
                    }
                });

                national_permit_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                        if (arg1) {
                            natpermit_layout.setVisibility(View.VISIBLE);
                        } else {
                            natpermit_layout.setVisibility(View.GONE);
                        }

                    }
                });

                update_btn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (detectConnection.isConnectingToInternet()) {
                            String ins_exp = insurance_expiry_tv.getText().toString();
                            String fit_exp = fitness_expiry_tv.getText().toString();
                            String np_exp = national_permit_tv.getText().toString();
                            String ins_amt = insurance_amount_et.getText().toString();
                            String rc_no = rc_no_et.getText().toString().trim();
                            if (rc_no.length() != 0) {
                                try {
                                    new UpdateTruckInfo(parseInt, ins_exp, fit_exp, np_exp, ins_amt, rc_no, national_permit_cb.isChecked(), jsonObj).execute();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } else {
                                TruckApp.editTextValidation(
                                        rc_no_et,
                                        rc_no,
                                        getResources().getString(
                                                R.string.rc_no_error));
                            }
                        } else {
                            Toast.makeText(context,
                                    getResources().getString(R.string.internet_str),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }

        private class CreateTrip extends AsyncTask<String, String, JSONObject> {

            String dev_id, start_pt, end_pt;

            CreateTrip(String dev_id, String start_pt, String end_pt) {
                this.dev_id = dev_id;
                this.start_pt = start_pt;
                this.end_pt = end_pt;
                System.out.println("----------------------");
                System.out.println(dev_id);
                System.out.println(start_pt);
                System.out.println(end_pt);
                System.out.println("----------------------");
            }


            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                pDialog.setMessage("Creating Trip...");
                pDialog.show();


            }

            @Override
            protected JSONObject doInBackground(String... params) {

                JSONObject json = null;
                try {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Trip[deviceID]=").append(URLEncoder.encode(dev_id, "UTF-8"));
                    builder.append("&Trip[accountID]=").append(URLEncoder.encode(sharedPreferences.getString("accountID",""), "UTF-8"));
                    builder.append("&Trip[source]=").append(URLEncoder.encode(start_pt, "UTF-8"));
                    builder.append("&Trip[destination]=").append(URLEncoder.encode(end_pt, "UTF-8"));

                    System.out.println("Params" + builder.toString());
                    String res = parser.excutePost(TruckApp.createTripURL, builder.toString());
                    /*String res = parser.executeGet(TruckApp.lookingforloadURL+"?"+
                            builder.toString());
					 */

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

                if (result != null) {
                    try {
                        TruckApp.checkPDialog(pDialog);
                        if (0 == result.getInt("status")) {
                            Toast.makeText(context, getResources().getString(R.string.exceptionmsg), Toast.LENGTH_LONG).show();
                        } else if (1 == result.getInt("status")) {
                            Toast.makeText(context, "Trip created", Toast.LENGTH_LONG).show();
                            if (createTripDialog != null && createTripDialog.isShowing()) {
                                createTripDialog.dismiss();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("ex in create trip" + e.toString());
                    }

                } else {
                    TruckApp.checkPDialog(pDialog);
                    Toast.makeText(context, getResources().getString(R.string.exceptionmsg), Toast.LENGTH_LONG).show();
                }
            }

        }

        private class UpdateTruckInfo extends AsyncTask<String, String, JSONObject> {

            int position;
            String ins_exp, fit_exp, np_exp, ins_amt, rc_no;
            boolean npavaliable;
            JSONObject jsonObj;

            UpdateTruckInfo(int position, String ins_exp, String fit_exp, String np_exp, String ins_amt, String rc_no, boolean npavaliable, JSONObject jsonObj) {
                this.position = position;
                this.ins_exp = ins_exp;
                this.fit_exp = fit_exp;
                this.np_exp = np_exp;
                this.ins_amt = ins_amt;
                this.rc_no = rc_no;
                this.npavaliable = npavaliable;
                this.jsonObj = jsonObj;
            }


            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                pDialog.setMessage("Looking for load...");
                pDialog.show();

            }

            @Override
            protected JSONObject doInBackground(String... params) {

                JSONObject json = null;
                try {
                    StringBuilder builder = new StringBuilder();
                    builder.append("deviceid=").append(URLEncoder.encode(jsonObj.getString("deviceID"), "UTF-8"));
                    builder.append("&accountid=").append(URLEncoder.encode(sharedPreferences.getString("accountID",""), "UTF-8"));
                    builder.append("&fitnessExpire=").append(URLEncoder.encode(fit_exp, "UTF-8"));
                    builder.append("&insuranceExpire=").append(URLEncoder.encode(ins_exp, "UTF-8"));
                    builder.append("&rcNo=").append(URLEncoder.encode(rc_no, "UTF-8"));
                    builder.append("&NPAvailable=").append(URLEncoder.encode((npavaliable ? "1" : "0"), "UTF-8"));
                    builder.append("&NPExpire=").append(URLEncoder.encode(np_exp, "UTF-8"));
                    builder.append("&insuranceAmount=").append(URLEncoder.encode(ins_amt, "UTF-8"));

                    System.out.println("Params" + builder.toString());
                    String res = parser.excutePost(TruckApp.updateDeviceInfoURL, builder.toString());
                    /*String res = parser.executeGet(TruckApp.lookingforloadURL+"?"+
                            builder.toString());
					 */

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
                if (result != null) {
                    try {
                        TruckApp.checkPDialog(pDialog);
                        if (0 == result.getInt("status")) {
                            Toast.makeText(context,
                                    getResources().getString(R.string.exceptionmsg),
                                    Toast.LENGTH_LONG).show();
                        } else if (1 == result.getInt("status")) {
                            Toast.makeText(context,getResources().getString(R.string.truckinfoupdate),Toast.LENGTH_LONG).show();
                            jsonObj.put("insuranceExpire", ins_exp);
                            jsonObj.put("fitnessExpire", fit_exp);
                            jsonObj.put("NPExpire", np_exp);
                            jsonObj.put("NPAvailable", npavaliable);
                            jsonObj.put("insuranceAmount", ins_amt);
                            jsonObj.put("rcNo", rc_no);
                            trucksArray.put(position, jsonObj);
                            notifyDataSetChanged();
                            if (updateTruckDialog != null && updateTruckDialog.isShowing()) {
                                updateTruckDialog.dismiss();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("ex in get leads" + e.toString());
                    }

                } else {
                    TruckApp.checkPDialog(pDialog);
                    Toast.makeText(context,
                            getResources().getString(R.string.exceptionmsg),
                            Toast.LENGTH_LONG).show();
                }
            }

        }

        // Register  DatePickerDialog listener
        public DatePickerDialog.OnDateSetListener mInsDateLst =
                new DatePickerDialog.OnDateSetListener() {
                    // the callback received when the user "sets" the Date in the DatePickerDialog
                    @Override
                    public void onDateSet(DatePicker view, int yearSelected,
                                          int monthOfYear, int dayOfMonth) {
                        String date = ((new StringBuilder().append(yearSelected).append("-").append(set2Digit(monthOfYear + 1)).append("-")
                                .append(set2Digit(dayOfMonth))).toString());
                        System.out.println("Date:" + date);
                        insurance_expiry_tv.setText(date);
                    }
                };

        // Register  DatePickerDialog listener
        DatePickerDialog.OnDateSetListener mFitDateLst =
                new DatePickerDialog.OnDateSetListener() {
                    // the callback received when the user "sets" the Date in the DatePickerDialog
                    @Override
                    public void onDateSet(DatePicker view, int yearSelected,
                                          int monthOfYear, int dayOfMonth) {
                        String date = ((new StringBuilder().append(yearSelected).append("-").append(set2Digit(monthOfYear + 1)).append("-")
                                .append(set2Digit(dayOfMonth))).toString());
                        System.out.println("Date:" + date);
                        fitness_expiry_tv.setText(date);
                    }
                };

        // Register  DatePickerDialog listener
        DatePickerDialog.OnDateSetListener mNPDateLst =
                new DatePickerDialog.OnDateSetListener() {
                    // the callback received when the user "sets" the Date in the DatePickerDialog
                    @Override
                    public void onDateSet(DatePicker view, int yearSelected,
                                          int monthOfYear, int dayOfMonth) {
                        String date = ((new StringBuilder().append(yearSelected).append("-").append(set2Digit(monthOfYear + 1)).append("-")
                                .append(set2Digit(dayOfMonth))).toString());
                        System.out.println("Date:" + date);
                        national_permit_tv.setText(date);
                    }
                };


        String set2Digit(int val) {
            String no = String.valueOf(val);
            if (1 == no.length()) {
                no = "0" + no;
            }
            return no;
        }

        private class LookingForLoad extends AsyncTask<String, String, JSONObject> {

            int position, status;
            JSONObject jsonobj;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog.setMessage("Looking for load...");
                pDialog.show();
            }

            public LookingForLoad(int position, int status, String jsonStr) {
                super();
                this.position = position;
                this.status = status;
                try {
                    this.jsonobj = new JSONObject(jsonStr);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            @Override
            protected JSONObject doInBackground(String... params) {
                JSONObject json = null;
                try {
                    StringBuilder builder = new StringBuilder();
                    builder.append("uid=").append(URLEncoder.encode(sharedPreferences.getString("uid",""), "UTF-8"));
                    builder.append("&cname=").append(URLEncoder.encode(sharedPreferences.getString("contactName",""), "UTF-8"));
                    builder.append("&mobile=").append(URLEncoder.encode(sharedPreferences.getString("phone",""), "UTF-8"));
                    builder.append("&deviceid=").append(URLEncoder.encode(jsonobj.getString("deviceID"), "UTF-8"));
                    builder.append("&lookingforload=").append( URLEncoder.encode(String.valueOf(status), "UTF-8"));
                    builder.append("&address=").append(URLEncoder.encode(jsonobj.getString("address"), "UTF-8"));
                    builder.append("&vehicleModel=").append( URLEncoder.encode(jsonobj.getString("vehicleModel"), "UTF-8"));
                    builder.append("&accountid=").append(URLEncoder.encode(sharedPreferences.getString("accountID",""), "UTF-8"));
                    System.out.println("builder:" + builder.toString());
                    String res = parser.executeGet(TruckApp.lookingforloadURL + "?" +
                            builder.toString());
                    System.out.println("looking for load res:" + res);
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
                if (result != null) {
                    try {
                        TruckApp.checkPDialog(pDialog);
                        if (0 == result.getInt("status")) {
                            Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
                        } else if (1 == result.getInt("status")) {
                            if (1 == status) {
                                Toast.makeText(context, result.getString("msg"),Toast.LENGTH_LONG).show();
                                jsonobj.put("lookingForLoad", "1");
                            } else if (0 == status) {
                                jsonobj.put("lookingForLoad", "0");
                            }
                            trucksArray.put(position, jsonobj);
                            notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        System.out.println("ex in get leads" + e.toString());
                    }

                } else {
                    TruckApp.checkPDialog(pDialog);
                    Toast.makeText(context,getResources().getString(R.string.exceptionmsg), Toast.LENGTH_LONG).show();
                }

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.truckslocation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_rewards:
                startActivity(new Intent(context, LoyaltyPointsActivity.class));
                break;
            case R.id.action_trips:
                startActivity(new Intent(context, TripsActivity.class));
                break;
            case R.id.action_refresh:
               // intializeGoogleAd();
                fetchTrucks(0);
                break;
            case R.id.action_map:
                Intent intent = new Intent(context, LocateTrucksActivity.class);
                intent.putExtra(Constants.GROUP_ID, groupId);
                intent.putExtra(Constants.GROUP_NAME, groupName);
                startActivity(intent);
                break;
        /*case R.id.action_report:
            reportproblem();
			break;*/
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
       /* if (adView != null)
        { adView.destroy(); }*/
        sharedPreferences = null;
        detectConnection = null;
        parser = null;
        pDialog = null;
        trucksLV = null;
        trucksArray = null;
        adapter = null;
        super.onDestroy();
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

        sendIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"gdinesh029@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");

        startActivity(Intent.createChooser(sendIntent, "Title:"));
        deleteFile("stack.trace");
    }


    @Override
    protected void onResume() {
        super.onResume();
        TruckApp.getInstance().trackScreenView("GPSVehicleListing Screen");
        /*if (adView != null) {
            adView.resume();
        }*/
    }

    @Override
    protected void onPause() {
        /*if (adView != null) {
            adView.pause();
        }*/
        super.onPause();
    }
}
