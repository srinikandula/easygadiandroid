package com.easygaadi.trucksmobileapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.easygaadi.CustomNetworkImageView;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.CustomVolleyRequestQueue;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TruckDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    @Override
    public void onStop() {
        super.onStop();

        /*// ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TruckDetails Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.easygaadi.trucksmobileapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);*/
    }

    public enum ImageType {
        RC, FITNESS, POLLUTION, NATIONALPERMIT, INSURANCE
    }


    private AdView adView;
    private final int PICTURE_TAKEN_FROM_GALLERY = 2;
    private ArrayAdapter<String> levelAdapter,positionAdapter;

    private CheckBox driverDetails_cb, maintenanceDetails_cb, renewal_cb, tryeDetails_cb;
    private EditText driverName_et, driverPhone_et, driverLicense_et, gre_km_et, ser_km_et, eng_km_et;
    private TextView onDuty_tv,
            gre_next_ser_tv, gre_last_ser_tv,
            ser_next_ser_tv, ser_last_ser_tv,
            eng_next_ser_tv, eng_last_ser_tv,
            rc_expiry_tv, fitness_expiry_tv,
            pollution_expiry_tv, national_permit_expiry_tv, insurance_expiry_tv;
    private Button rc_browse_btn, fitness_browse_btn, pollution_browse_btn, national_permit_browse_btn,
            insurance_browse_btn, submit_btn;
    private CustomNetworkImageView rc_niv, fitness_niv, pollution_niv, national_permit_niv, insurance_niv;
    private RelativeLayout driverLayout, mainteanceLayout, renewalLayout, tryes_layout;
    private LinearLayout tryesitemsLayout;
    private ImageView trye_add_btn;

    private TextView selectedTV;
    private CustomNetworkImageView selectedImageView;
    ProgressDialog pDialog;
    JSONParser parser;
    Context mContext;
    ConnectionDetector detectConnection;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;
    private String rc_path, fitness_path, pollution_path, national_permit_path, insurance_path;
    private ImageType selectedImageType;
    private ImageLoader imgLoader;
    private List<String> levelList, positionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_details);

        mContext = this;
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imgLoader = CustomVolleyRequestQueue.getInstance(mContext).getImageLoader();
        levelList = Arrays.asList(getResources().getStringArray(R.array.level_array));
        positionList = Arrays.asList(getResources().getStringArray(R.array.position_array));
        pDialog = new ProgressDialog(TruckDetailsActivity.this);
        parser = JSONParser.getInstance();
        detectConnection = new ConnectionDetector(mContext);
        driverDetails_cb = (CheckBox) findViewById(R.id.driver_details_cb);
        maintenanceDetails_cb = (CheckBox) findViewById(R.id.maintenanceDetails_cb);
        renewal_cb = (CheckBox) findViewById(R.id.renewalDetails_cb);
        tryeDetails_cb = (CheckBox) findViewById(R.id.tryesDetails_cb);
        driverName_et = (EditText) findViewById(R.id.driver_name_et);
        driverPhone_et = (EditText) findViewById(R.id.driver_mobile_et);
        driverLicense_et = (EditText) findViewById(R.id.driver_license_et);
        trye_add_btn = (ImageView) findViewById(R.id.add_tyre);
        rc_niv = (CustomNetworkImageView) findViewById(R.id.rc_niv);
        fitness_niv = (CustomNetworkImageView) findViewById(R.id.fitness_niv);
        pollution_niv = (CustomNetworkImageView) findViewById(R.id.pollution_niv);
        national_permit_niv = (CustomNetworkImageView) findViewById(R.id.national_permit_niv);
        insurance_niv = (CustomNetworkImageView) findViewById(R.id.insurance_niv);
        submit_btn = (Button) findViewById(R.id.submit_btn);

        ser_km_et = (EditText) findViewById(R.id.ser_km_et);
        gre_km_et = (EditText) findViewById(R.id.gre_km_et);
        eng_km_et = (EditText) findViewById(R.id.eng_km_et);

        rc_browse_btn = (Button) findViewById(R.id.rc_browse_btn);
        fitness_browse_btn = (Button) findViewById(R.id.fitness_browse_btn);
        pollution_browse_btn = (Button) findViewById(R.id.pollution_browse_btn);
        national_permit_browse_btn = (Button) findViewById(R.id.national_permit_browse_btn);
        insurance_browse_btn = (Button) findViewById(R.id.insurance_browse_btn);

        adView = (AdView)findViewById(R.id.adView);
        //intializeBannerAd();

        levelAdapter = new ArrayAdapter<String>(mContext,R.layout.custom_spinner_item,R.id.title_tv,getResources().getStringArray(R.array.level_array));
        positionAdapter = new ArrayAdapter<String>(mContext,R.layout.custom_spinner_item,R.id.title_tv,getResources().getStringArray(R.array.position_array));

        onDuty_tv = (TextView) findViewById(R.id.onduty_tv);

        gre_next_ser_tv = (TextView) findViewById(R.id.gre_next_ser_tv);
        gre_last_ser_tv = (TextView) findViewById(R.id.gre_last_ser_tv);
        ser_next_ser_tv = (TextView) findViewById(R.id.ser_next_ser_tv);
        ser_last_ser_tv = (TextView) findViewById(R.id.ser_last_ser_tv);
        eng_next_ser_tv = (TextView) findViewById(R.id.eng_next_ser_tv);
        eng_last_ser_tv = (TextView) findViewById(R.id.eng_last_ser_tv);

        rc_expiry_tv = (TextView) findViewById(R.id.rc_expiry_tv);
        fitness_expiry_tv = (TextView) findViewById(R.id.fitness_expiry_tv);
        pollution_expiry_tv = (TextView) findViewById(R.id.pollution_expiry_tv);
        national_permit_expiry_tv = (TextView) findViewById(R.id.national_permit_expiry_tv);
        insurance_expiry_tv = (TextView) findViewById(R.id.insuranc_expiry_tv);

        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        onDuty_tv.setOnClickListener(this);
        gre_next_ser_tv.setOnClickListener(this);
        gre_last_ser_tv.setOnClickListener(this);
        ser_next_ser_tv.setOnClickListener(this);
        ser_last_ser_tv.setOnClickListener(this);
        eng_next_ser_tv.setOnClickListener(this);
        eng_last_ser_tv.setOnClickListener(this);
        rc_expiry_tv.setOnClickListener(this);
        fitness_expiry_tv.setOnClickListener(this);
        pollution_expiry_tv.setOnClickListener(this);
        national_permit_expiry_tv.setOnClickListener(this);
        insurance_expiry_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTV = insurance_expiry_tv;
                showDatePicker();
            }
        });

        rc_browse_btn.setOnClickListener(this);
        fitness_browse_btn.setOnClickListener(this);
        pollution_browse_btn.setOnClickListener(this);
        national_permit_browse_btn.setOnClickListener(this);
        insurance_browse_btn.setOnClickListener(this);


        driverLayout = (RelativeLayout) findViewById(R.id.driver_layout);
        mainteanceLayout = (RelativeLayout) findViewById(R.id.maintenance_layout);
        renewalLayout = (RelativeLayout) findViewById(R.id.renewal_layout);
        tryes_layout = (RelativeLayout) findViewById(R.id.tyres_layout);
        tryesitemsLayout = (LinearLayout) findViewById(R.id.tryesitemsLayout);


        driverDetails_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    driverLayout.setVisibility(View.VISIBLE);
                } else {
                    driverLayout.setVisibility(View.GONE);
                }

            }
        });

        maintenanceDetails_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mainteanceLayout.setVisibility(View.VISIBLE);
                } else {
                    mainteanceLayout.setVisibility(View.GONE);
                }
            }
        });
        renewal_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    renewalLayout.setVisibility(View.VISIBLE);
                } else {
                    renewalLayout.setVisibility(View.GONE);
                }
            }
        });
        tryeDetails_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tryes_layout.setVisibility(View.VISIBLE);
                } else {
                    tryes_layout.setVisibility(View.GONE);
                }
            }
        });

        trye_add_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                addTyre();
            }
        });

        /*customDateDialog = new CustomDateTimePicker(this,
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {
                        if(selectedTV!=null){
                            selectedTV.setText(year+"-"+TruckApp.set2Digit((monthNumber+1))+"-"+TruckApp.set2Digit(calendarSelected.get(Calendar.DAY_OF_MONTH))
                                    + " " + TruckApp.set2Digit(hour24) + ":" + TruckApp.set2Digit(min)+ ":00");
                        }

                    }

                    @Override
                    public void onCancel() {

                    }
                });
        customDateDialog.set24HourFormat(true);
*/
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detectConnection.isConnectingToInternet()) {
                    setTruckDetails();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });

        setupActionBar();
        if (detectConnection.isConnectingToInternet()) {
            new GetDeviceDetails(sharedPreferences.getString("accountID",
                    "no accountid "), getIntent().getStringExtra("deviceID")).execute();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            if (selectedTV != null) {
                selectedTV.setText(year + "-" + TruckApp.set2Digit((month + 1)) + "-" + TruckApp.set2Digit(dayOfMonth));
            }
        }
    };

    private void setTruckDetails() {
        String deviceID = getIntent().getStringExtra("deviceID");
        String accountID = sharedPreferences.getString("accountID", "no accountid ");
        String nextGreasingDate = gre_next_ser_tv.getText().toString().trim();
        String lastGreasingDate = gre_last_ser_tv.getText().toString().trim();
        String greasingKm = gre_km_et.getText().toString().trim();
        String nextServicingDate = ser_next_ser_tv.getText().toString().trim();
        String lastServicingDate = ser_last_ser_tv.getText().toString().trim();
        String servicingKm = ser_km_et.getText().toString().trim();
        String eOilExpiryDate = eng_next_ser_tv.getText().toString().trim();
        String eOilReplacedDate = eng_last_ser_tv.getText().toString().trim();
        String eOilKm = eng_km_et.getText().toString().trim();
        String driverName = driverName_et.getText().toString().trim();
        String driverMobile = driverPhone_et.getText().toString().trim();
        String nPExpire = national_permit_expiry_tv.getText().toString().trim();
        String insuranceExpire = insurance_expiry_tv.getText().toString().trim();
        String fitnessExpire = fitness_expiry_tv.getText().toString().trim();
        String rcExpire = rc_expiry_tv.getText().toString().trim();
        String pollExpire = pollution_expiry_tv.getText().toString().trim();
        String driverLicenceNo = driverLicense_et.getText().toString().trim();
        String driverOnDuty = onDuty_tv.getText().toString().trim();

        HashMap<String, String> values = new HashMap<>();
        values.put("deviceID", deviceID);
        values.put("accountID", accountID);
        values.put("nextGreasingDate", nextGreasingDate);
        values.put("lastGreasingDate", lastGreasingDate);
        values.put("greasingKm", greasingKm);
        values.put("nextServicingDate", nextServicingDate);
        values.put("lastServicingDate", lastServicingDate);
        values.put("servicingKm", servicingKm);
        values.put("eOilExpiryDate", eOilExpiryDate);
        values.put("eOilReplacedDate", eOilReplacedDate);
        values.put("eOilKm", eOilKm);
        values.put("driverName", driverName);
        values.put("driverMobile", driverMobile);
        values.put("nPExpire", nPExpire);
        values.put("insuranceExpire", insuranceExpire);
        values.put("fitnessExpire", fitnessExpire);
        values.put("rcExpire", rcExpire);
        values.put("pollExpire", pollExpire);
        values.put("driverLicenceNo", driverLicenceNo);
        values.put("driverOnDuty", driverOnDuty);
        if (!getData().equalsIgnoreCase("error")) {
            new SetTruckDetails(values).execute();
        }
/*
        String fitnessDoc = fitness_path;
        String nPDoc = national_permit_path;
        String insuranceDoc = insurance_path;
        String rcDoc = rc_path;
        String pollDoc = pollution_path;*/
    }

    private void addTyre() {
        final View addView = layoutInflater.inflate(R.layout.trye_item, tryesitemsLayout, false);
        final Spinner level_spn = (Spinner) addView.findViewById(R.id.level_spn);
        final Spinner position_spn = (Spinner) addView.findViewById(R.id.position_spn);
        final TextView tyre_exp_date = (TextView) addView.findViewById(R.id.tyre_expiry_date);
        final TextView tyre_install_date = (TextView) addView.findViewById(R.id.install_date_tv);
        level_spn.setAdapter(levelAdapter);
        position_spn.setAdapter(positionAdapter);
        tyre_exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTV = tyre_exp_date;
                showDatePicker();
            }
        });
        tyre_install_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTV = tyre_install_date;
                showDatePicker();
            }
        });

        ImageView buttonRemove = (ImageView) addView.findViewById(R.id.delete_tyre);
        buttonRemove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((LinearLayout) addView.getParent()).removeView(addView);
            }
        });

        tryesitemsLayout.addView(addView);
    }

    private void setupActionBar() {
        if(getActionBar()!=null) {
            getSupportActionBar().setTitle(getString(R.string.truck_details)+" ("+getIntent().getStringExtra("deviceID")+")");
        }else if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.truck_details)+" ("+getIntent().getStringExtra("deviceID")+")");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.onduty_tv:
                selectedTV = onDuty_tv;
                showDatePicker();
                break;
            case R.id.gre_next_ser_tv:
                selectedTV = gre_next_ser_tv;
                showDatePicker();
                break;
            case R.id.gre_last_ser_tv:
                selectedTV = gre_last_ser_tv;
                showDatePicker();
                break;
            case R.id.ser_next_ser_tv:
                selectedTV = ser_next_ser_tv;
                showDatePicker();
                break;
            case R.id.ser_last_ser_tv:
                selectedTV = ser_last_ser_tv;
                showDatePicker();
                break;
            case R.id.eng_next_ser_tv:
                selectedTV = eng_next_ser_tv;
                showDatePicker();
                break;
            case R.id.eng_last_ser_tv:
                selectedTV = eng_last_ser_tv;
                showDatePicker();
                break;
            case R.id.rc_expiry_tv:
                selectedTV = rc_expiry_tv;
                showDatePicker();
                break;
            case R.id.fitness_expiry_tv:
                selectedTV = fitness_expiry_tv;
                showDatePicker();
                break;
            case R.id.pollution_expiry_tv:
                selectedTV = pollution_expiry_tv;
                showDatePicker();
                break;
            case R.id.national_permit_expiry_tv:
                selectedTV = national_permit_expiry_tv;
                showDatePicker();
                break;
            case R.id.insurance_expiry_et:
                selectedTV = insurance_expiry_tv;
                showDatePicker();
                break;
            case R.id.rc_browse_btn:
                selectedImageView = rc_niv;
                selectedImageType = ImageType.RC;
                selectImage();
                break;
            case R.id.fitness_browse_btn:
                selectedImageView = fitness_niv;
                selectedImageType = ImageType.FITNESS;
                selectImage();
                break;
            case R.id.pollution_browse_btn:
                selectedImageView = pollution_niv;
                selectedImageType = ImageType.POLLUTION;
                selectImage();
                break;
            case R.id.national_permit_browse_btn:
                selectedImageView = national_permit_niv;
                selectedImageType = ImageType.NATIONALPERMIT;
                selectImage();
                break;
            case R.id.insurance_browse_btn:
                selectedImageView = insurance_niv;
                selectedImageType = ImageType.INSURANCE;
                selectImage();
                break;
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void selectImage() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Pic"), PICTURE_TAKEN_FROM_GALLERY);
        } catch (Exception e) {
            System.out.println("Exception when calling the gallery");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Request Code:" + requestCode);
        System.out.println("Result Code:" + resultCode);

        if (requestCode == PICTURE_TAKEN_FROM_GALLERY) {
            String selectedImagePathStr = null;
            try {
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    selectedImagePathStr = TruckApp.getRealPathFromURI(selectedImageUri, mContext);
                    System.out.println("Image Path : " + selectedImagePathStr);
                    if (selectedImagePathStr == null) {
                        Toast.makeText(mContext, "Please select image",
                                Toast.LENGTH_SHORT).show();
                    } else {
                       /* selectedImageView.setImageUrl(selectedImagePathStr,
                                new ImageLoader(Volley.newRequestQueue(getApplicationContext()), new MyCache()));*/
                        selectedImageView.setLocalImageBitmap(TruckApp.getThumbnail(selectedImageUri, mContext, 100));
                    }
                }
            } catch (Exception e) {
                selectedImagePathStr = null;
                Toast.makeText(mContext, "Please select another image ", Toast.LENGTH_SHORT).show();
            }
            switch (selectedImageType) {
                case RC:
                    rc_path = selectedImagePathStr;
                    break;
                case FITNESS:
                    fitness_path = selectedImagePathStr;
                    break;
                case POLLUTION:
                    pollution_path = selectedImagePathStr;
                    break;
                case NATIONALPERMIT:
                    national_permit_path = selectedImagePathStr;
                    break;
                case INSURANCE:
                    insurance_path = selectedImagePathStr;
                    break;
            }
        }
    }

    public class GetDeviceDetails extends AsyncTask<Void, Void, JSONObject> {
        public String accountID, deviceID;
        private JSONParser jsonParser;

        public GetDeviceDetails(String accountID, String deviceID) {
            this.accountID = accountID;
            this.deviceID = deviceID;
            jsonParser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Getting the truck details...");
            pDialog.show();
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject json = null;
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("accountID=").append(URLEncoder.encode(accountID, "UTF-8"));
                builder.append("&deviceID=").append(URLEncoder.encode(deviceID, "UTF-8"));
                builder.append("&type=get");
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
                String res = parser.excutePost(TruckApp.deviceDetailsURL, builder.toString());
                json = new JSONObject(res);
            } catch (Exception e) {
                Log.e("Login DoIN EX", e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (jsonObject != null) {
                try {
                    if (jsonObject.has("status") && jsonObject.getInt("status") == 1) {
                        setData2Views(jsonObject.getJSONObject("data"));
                    }else if (jsonObject.has("status") && jsonObject.getInt("status") == 2) {
                        //TruckApp.logoutAction(TruckDetailsActivity.this);
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
                        startActivity(new Intent(mContext,
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(mContext, "Failed to fetch the truck details..", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Failed to fetch the truck details..", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(mContext, "Failed to fetch the truck details..", Toast.LENGTH_LONG).show();
            }

            TruckApp.checkPDialog(pDialog);

        }


    }

    private void setData2Views(JSONObject data) {
        try {
            if (data.has("driverName")) {
                driverName_et.setText(data.getString("driverName"));
            }
            if (data.has("driverMobile")) {
                driverPhone_et.setText(data.getString("driverMobile"));
            }
            if (data.has("driverLicenceNo")) {
                driverLicense_et.setText(data.getString("driverLicenceNo"));
            }

            if (data.has("driverOnDuty")) {
                onDuty_tv.setText(data.getString("driverOnDuty"));
            }

            if (data.has("nextGreasingDate")) {
                gre_next_ser_tv.setText(data.getString("nextGreasingDate"));
            }
            if (data.has("lastGreasingDate")) {
                gre_last_ser_tv.setText(data.getString("lastGreasingDate"));
            }
            if (data.has("greasingKm")) {
                gre_km_et.setText(data.getString("greasingKm"));
            }

            if (data.has("nextServicingDate")) {
                ser_next_ser_tv.setText(data.getString("nextServicingDate"));
            }
            if (data.has("lastServicingDate")) {
                ser_last_ser_tv.setText(data.getString("lastServicingDate"));
            }
            if (data.has("greasingKm")) {
                ser_km_et.setText(data.getString("servicingKm"));
            }

            if (data.has("eOilExpiryDate")) {
                eng_next_ser_tv.setText(data.getString("eOilExpiryDate"));
            }
            if (data.has("eOilReplacedDate")) {
                eng_last_ser_tv.setText(data.getString("eOilReplacedDate"));
            }
            if (data.has("eOilKm")) {
                eng_km_et.setText(data.getString("eOilKm"));
            }

            if (data.has("rcDoc")) {
                if (!data.getString("rcDoc").isEmpty()) {
                    //Picasso.with(mContext).load(data.getString("rcDoc")).into(rc_niv);
                    //imgLoader.DisplayImage(data.getString("rcDoc"), R.drawable.ic_launcher, rc_niv);
                    rc_niv.setImageUrl(data.getString("rcDoc"),imgLoader);
                }
            }
            if (data.has("rcExpire")) {
                rc_expiry_tv.setText(data.getString("rcExpire"));
            }

            if (data.has("fitnessDoc")) {
                if (!data.getString("fitnessDoc").isEmpty()) {
                    //imgLoader.DisplayImage(data.getString("fitnessDoc"), R.drawable.ic_launcher, fitness_niv);
                    fitness_niv.setImageUrl(data.getString("fitnessDoc"),imgLoader);
                }
            }

            if (data.has("fitnessExpire")) {
                fitness_expiry_tv.setText(data.getString("fitnessExpire"));
            }

            if (data.has("pollDoc")) {
                if (!data.getString("pollDoc").isEmpty()) {
                    //imgLoader.DisplayImage(data.getString("pollDoc"), R.drawable.ic_launcher, pollution_niv);
                    pollution_niv.setImageUrl(data.getString("pollDoc"),imgLoader);
                }
            }

            if (data.has("pollExpire")) {
                pollution_expiry_tv.setText(data.getString("pollExpire"));
            }

            if (data.has("nPDoc")) {
                if (!data.getString("nPDoc").isEmpty()) {
                    //imgLoader.DisplayImage(data.getString("nPDoc"), R.drawable.ic_launcher, national_permit_niv);
                    national_permit_niv.setImageUrl(data.getString("nPDoc"),imgLoader);
                }
            }

            if (data.has("nPExpire")) {
                national_permit_expiry_tv.setText(data.getString("nPExpire"));
            }

            if (data.has("insuranceDoc")) {
                if (!data.getString("insuranceDoc").isEmpty()) {
                    //imgLoader.DisplayImage(data.getString("insuranceDoc"), R.drawable.ic_launcher, insurance_niv);
                    insurance_niv.setImageUrl(data.getString("insuranceDoc"),imgLoader);
                }
            }

            if (data.has("insuranceExpire")) {
                insurance_expiry_tv.setText(data.getString("insuranceExpire"));
            }
            if (data.has("tyres") && data.getJSONArray("tyres").length() > 0) {
                setTyresList(data.getJSONArray("tyres"));
            }

        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

    private void setTyresList(JSONArray tyresList) throws JSONException {
        if (tyresList.length() > 0) {
            for (int i = 0; i < tyresList.length(); i++) {
                final View addView = layoutInflater.inflate(R.layout.trye_item, tryesitemsLayout, false);
                final Spinner level_spn = (Spinner) addView.findViewById(R.id.level_spn);
                final Spinner position_spn = (Spinner) addView.findViewById(R.id.position_spn);
                final TextView tyre_exp_date = (TextView) addView.findViewById(R.id.tyre_expiry_date);
                final TextView tyre_install_date = (TextView) addView.findViewById(R.id.install_date_tv);
                final EditText tyre_km_et = (EditText) addView.findViewById(R.id.tyre_km_et);
                level_spn.setAdapter(levelAdapter);
                position_spn.setAdapter(positionAdapter);
                tyre_exp_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedTV = tyre_exp_date;
                        showDatePicker();
                    }
                });
                tyre_install_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedTV = tyre_install_date;
                        showDatePicker();
                    }
                });

                ImageView buttonRemove = (ImageView) addView.findViewById(R.id.delete_tyre);
                buttonRemove.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) addView.getParent()).removeView(addView);
                    }
                });

                tryesitemsLayout.addView(addView);
                tyre_exp_date.setText((tyresList.getJSONObject(i)).getString("expiryDate"));
                tyre_install_date.setText((tyresList.getJSONObject(i)).getString("installDate"));
                tyre_km_et.setText(tyresList.getJSONObject(i).getString("km"));
                level_spn.setSelection(levelList.indexOf(tyresList.getJSONObject(i).getString("level")));
                position_spn.setSelection(positionList.indexOf(tyresList.getJSONObject(i).getString("position")));
            }
        }
    }

    protected String getData() {
        if (tryesitemsLayout != null) {
            int childcount = tryesitemsLayout.getChildCount();
            JSONArray arraySrcDes = new JSONArray();
            if (childcount > 0) {
                for (int i = 0; i < childcount; i++) {
                    View getView = tryesitemsLayout.getChildAt(i);

                    String level = ((Spinner) getView.findViewById(R.id.level_spn)).getSelectedItem().toString();
                    String position = ((Spinner) getView.findViewById(R.id.position_spn)).getSelectedItem().toString();
                    String tyre_exp_str = ((TextView) getView.findViewById(R.id.tyre_expiry_date)).getText().toString().trim();
                    String tyre_install_str = ((TextView) getView.findViewById(R.id.install_date_tv)).getText().toString().trim();
                    String tyre_km_str = ((EditText) getView.findViewById(R.id.tyre_km_et)).getText().toString();

                    if (!tyre_exp_str.isEmpty() && !tyre_install_str.isEmpty() && !tyre_km_str.isEmpty()) {
                        try {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.putOpt("level", level);
                            jsonObj.putOpt("position", position);
                            jsonObj.putOpt("expiryDate", tyre_exp_str);
                            jsonObj.putOpt("installDate", tyre_install_str);
                            jsonObj.putOpt("km", tyre_km_str);
                            arraySrcDes.put(jsonObj);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                if (arraySrcDes.length() == childcount) {
                    //  String data ="";
                   /* StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < arraySrcDes.length(); i++) {
                        try {
                            JSONObject j = arraySrcDes.getJSONObject(i);
                            stringBuilder.append("&Tyres[level][").append(i).append("]=").append(j.getString("level"));
                            stringBuilder.append("&Tyres[position][").append(i).append("]=").append(j.getString("position"));
                            stringBuilder.append("&Tyres[expiryDate][").append(i).append("]=").append(j.getString("expiryDate"));
                            stringBuilder.append("&Tyres[installDate][").append(i).append("]=").append(j.getString("installDate"));
                            stringBuilder.append("&Tyres[km][").append(i).append("]=").append(j.getString("km"));
                            //data=data+"&source["+i+"]="+j.getString("source")+"&destination["+i+"]="+j.getString("destination");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Issue in appending data", Toast.LENGTH_LONG).show();
                        }
                    }*/
                    return arraySrcDes.toString();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.tyreswrong_str), Toast.LENGTH_LONG).show();
                    return "error";
                }

            }/*else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.srcdeszero_str), Toast.LENGTH_LONG).show();
            }*/
        }
        return "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (adView != null) {
            adView.resume();
        }*/
        TruckApp.getInstance().trackScreenView("VehicleDetails Screen");
    }

    @Override
    protected void onPause() {
        /*if (adView != null) {
            adView.pause();
        }*/
        super.onPause();
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


    public class SetTruckDetails extends AsyncTask<Void, Void, JSONObject> {

        HashMap<String, String> values;
        Set<String> keys;

        public SetTruckDetails(HashMap<String, String> values) {
            this.values = values;
            keys = values.keySet();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Updating the truck details...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject jsonResponse = null;
            String urlString = TruckApp.deviceDetailsURL;
            try {
                HttpEntity resEntity;
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(urlString);
                MultipartEntity reqEntity = new MultipartEntity();
                if (rc_path != null && !rc_path.isEmpty()) {
                    File fileRC = new File(rc_path);
                    FileBody binRc = new FileBody(fileRC);
                    reqEntity.addPart("rcDoc", binRc);
                }
                if (fitness_path != null && !fitness_path.isEmpty()) {
                    File fileFitness = new File(fitness_path);
                    FileBody binFitness = new FileBody(fileFitness);
                    reqEntity.addPart("fitnessDoc", binFitness);

                }
                if (pollution_path != null && !pollution_path.isEmpty()) {
                    File filePollution = new File(pollution_path);
                    FileBody binPollution = new FileBody(filePollution);
                    reqEntity.addPart("pollDoc", binPollution);
                }
                if (national_permit_path != null && !national_permit_path.isEmpty()) {
                    File fileNationalPermit = new File(national_permit_path);
                    FileBody binNationalPermit = new FileBody(fileNationalPermit);
                    reqEntity.addPart("nPDoc", binNationalPermit);
                }
                if (insurance_path != null && !insurance_path.isEmpty()) {
                    File fileInsurance = new File(insurance_path);
                    FileBody binInsurance = new FileBody(fileInsurance);
                    reqEntity.addPart("insuranceDoc", binInsurance);
                }
                reqEntity.addPart("type", new StringBody("set"));

                for (String key : keys) {
                    reqEntity.addPart(key, new StringBody(values.get(key)));
                }
                reqEntity.addPart("access_token",new StringBody(sharedPreferences.getString("access_token","")));
                if(!getData().isEmpty()){
                    JSONArray array = new JSONArray(getData());
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObj = array.getJSONObject(i);
                        reqEntity.addPart("Tyres["+i+"][level]", new StringBody(jsonObj.getString("level")));
                        reqEntity.addPart("Tyres["+i+"][position]" , new StringBody(jsonObj.getString("position")));
                        reqEntity.addPart("Tyres["+i+"][expiryDate]", new StringBody(jsonObj.getString("expiryDate")));
                        reqEntity.addPart("Tyres["+i+"][installDate]", new StringBody(jsonObj.getString("installDate")));
                        reqEntity.addPart("Tyres["+i+"][km]", new StringBody(jsonObj.getString("km")));
                    }
                }
                //reqEntity.addPart("user", new StringBody("User"));
                post.setEntity(reqEntity);
                HttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                final String response_str = convertStreamToString(is);
                if (is != null) {
                    jsonResponse = new JSONObject(response_str);
                }
            } catch (Exception ex) {
                Log.e("Debug", "error: " + ex.getMessage(), ex);
            }
            return jsonResponse;
        }

        private String convertStreamToString(InputStream is) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append((line + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            TruckApp.checkPDialog(pDialog);
            try {
                if (jsonObject != null) {
                    if (jsonObject.has("status") && jsonObject.getInt("status") == 1) {
                        Toast.makeText(mContext, "Successfully Updated the truck details", Toast.LENGTH_SHORT).show();
                        finish();
                    }else if (jsonObject.has("status") && jsonObject.getInt("status") == 2) {
                       //TruckApp.logoutAction(TruckDetailsActivity.this);
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
                        startActivity(new Intent(mContext,
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(mContext, "Failed to update the truck details", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Failed to update the truck details", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exe) {

            }

        }
    }

}
