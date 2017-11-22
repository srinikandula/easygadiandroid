package com.easygaadi.trucksmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {



    private static final String TAG_RESULT = "predictions";
    JSONObject json;


    private AdView adView;
    ArrayList<String> names;
    ArrayAdapter<String> adapter;
    private String browserKey = "AIzaSyAqh4el3Bd1lafjwQMQ7w6hJ_OVZWEst-0";
    private String API_KEY = "AIzaSyCd0ifbJjaQoMN66oY3Vyjh43r2gH9kPxA";
    private LinearLayout operatRoutesLayout;
    private Button addMore_btn,submit_btn;
    private EditText min_stop_minutes_et,over_speed_et,customer_email_et,stop_alert_minutes;
    private Spinner route_notification_spn,email_daily_spn;
    ProgressDialog pDialog;
    JSONParser parser;
    Context mContext;
    String url;
    ConnectionDetector detectConnection;
    private SharedPreferences sharedPreferences;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mContext             = this;
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        layoutInflater =(LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pDialog             = new ProgressDialog(this);
        parser              = JSONParser.getInstance();
        detectConnection    = new ConnectionDetector(mContext);
        addMore_btn         = (Button)findViewById(R.id.addmore);
        submit_btn         = (Button)findViewById(R.id.submit_btn);
        operatRoutesLayout  = (LinearLayout)findViewById(R.id.operatingroutes);
        min_stop_minutes_et = (EditText)findViewById(R.id.min_stop_et);
        over_speed_et = (EditText)findViewById(R.id.over_speed_et);
        customer_email_et = (EditText)findViewById(R.id.customer_email_et);
        stop_alert_minutes = (EditText)findViewById(R.id.stop_alert_time_et);
        route_notification_spn = (Spinner) findViewById(R.id.notification_spn);
        email_daily_spn = (Spinner) findViewById(R.id.email_daily_report_spn);
        adView = (AdView)findViewById(R.id.adView);
        //intializeBannerAd();
        names               = new ArrayList<String>();
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(detectConnection.isConnectingToInternet()){
                    setSettings();
                }else {
                    Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
                }
            }
        });

        setupActionBar();
        addMore_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                addSrcDest();
            }
        });

        if(detectConnection.isConnectingToInternet()){
            new GetSettings(sharedPreferences.getString("accountID",
                    "no accountid ")).execute();
        }else {
            Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
       /* if (adView != null) {
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


    private void addSrcDest() {
        final View addView = layoutInflater.inflate(R.layout.operatingrouteitem,operatRoutesLayout,false);

        final AutoCompleteTextView source_et       = (AutoCompleteTextView)addView.findViewById(R.id.source_et);
        final AutoCompleteTextView destination_et  = (AutoCompleteTextView)addView.findViewById(R.id.destination_et);
        ImageView buttonRemove = (ImageView) addView.findViewById(R.id.delete_imgbtn);
        source_et.requestFocus();

        source_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (s.toString().length() <= 3 && detectConnection.isConnectingToInternet()) {
                    names.clear();
                    updateList(s.toString(),source_et);
                }else{
                    if(!detectConnection.isConnectingToInternet())
                        System.out.println("NO NET");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) { }


        });
        destination_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (s.toString().length() <= 3 && detectConnection.isConnectingToInternet()) {
                    names.clear();
                    updateList(s.toString(), destination_et);
                } else {
                    if(!detectConnection.isConnectingToInternet())
                        System.out.println("NO NET");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((LinearLayout) addView.getParent()).removeView(addView);
            }
        });

        operatRoutesLayout.addView(addView);
    }

    private void setSettings() {
        String emailDailSummaryReport= (email_daily_spn.getSelectedItemPosition()==0)?"1":"0";
        String routeNotificationCount = route_notification_spn.getSelectedItem().toString();
        String stopAlertTime = stop_alert_minutes.getText().toString().trim();
        String overSpeedLimit = over_speed_et.getText().toString().trim();
        String contactEmail = customer_email_et.getText().toString().trim();
        String stopDurationLimit = min_stop_minutes_et.getText().toString().trim();
        String data = getData();
        if(!stopAlertTime.isEmpty() && !overSpeedLimit.isEmpty()&& !stopDurationLimit.isEmpty()){
            if(emailDailSummaryReport.equalsIgnoreCase("0") || (emailDailSummaryReport.equalsIgnoreCase("1") && !contactEmail.isEmpty() && TruckApp.emailValidator(contactEmail))){
                new SetSettings(data,sharedPreferences.getString("accountID",
                        "no accountid "),emailDailSummaryReport,routeNotificationCount,
                        stopAlertTime,overSpeedLimit,contactEmail,stopDurationLimit).execute();
            }else {
                if(contactEmail.isEmpty()){
                    TruckApp.editTextValidation(customer_email_et,contactEmail,getString(R.string.email_error));
                }else if(!TruckApp.emailValidator(contactEmail)){
                    TruckApp.editTextValidation(customer_email_et,"",getString(R.string.emailvalid_error));
                }
            }

        }else{
            TruckApp.editTextValidation(stop_alert_minutes,stopAlertTime,getString(R.string.stop_alert_time_err));
            TruckApp.editTextValidation(over_speed_et,overSpeedLimit,getString(R.string.over_speed_limit_err));
            TruckApp.editTextValidation(min_stop_minutes_et,stopDurationLimit,getString(R.string.min_stop_dur_err));

        }
    }

    public void updateList(String place,final AutoCompleteTextView auto_tv) {
        try{
            String input = "";
            try {
                input = "input=" + URLEncoder.encode(place, "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            String output = "json";
            String parameter = input + "&types=geocode&sensor=true&key="
                    + browserKey + "&components=country:in";

            url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key="+browserKey+
                    "&components=country:in&input="+ URLEncoder.encode(input, "utf-8");
            String placesApi = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key="+API_KEY+"&components=country:in&types=(cities)&"+input;

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, placesApi,
                    null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try {

                        JSONArray ja = response.getJSONArray(TAG_RESULT);

                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject c = ja.getJSONObject(i);
                            String description = c.getString("description");
                            Log.d("description", description);
                            String trimCountryName = ", India";
                            if(description.lastIndexOf(trimCountryName)==(description.length()-trimCountryName.length())){
                                description = description.substring(0, (description.lastIndexOf(trimCountryName)));
                            }
                            names.add(description);
                        }

                        adapter = new ArrayAdapter<String>(
                                getApplicationContext(),
                                android.R.layout.simple_list_item_1, names) {
                            @Override
                            public View getView(int position,View convertView, ViewGroup parent) {
                                View view = super.getView(position,convertView, parent);
                                TextView text = (TextView) view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };
                        auto_tv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            TruckApp.getInstance().addToRequestQueue(jsonObjReq, "jreq");

        }catch (Exception e){

        }
    }


    private void setupActionBar() {
        SpannableString s = new SpannableString(getString(R.string.settings));
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, getString(R.string.settings).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(getActionBar()!=null) {
            getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_clr)));
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setHomeButtonEnabled(false);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setTitle(s);
        }else if(getSupportActionBar()!=null){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_clr)));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(s);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected String getData() {
        if(operatRoutesLayout!=null){
            int childcount = operatRoutesLayout.getChildCount();
            JSONArray arraySrcDes= new JSONArray();
            if(childcount>0){
                for(int i=0;i<childcount;i++){
                    View v= operatRoutesLayout.getChildAt(i);
                    String src_str =((AutoCompleteTextView)v.findViewById(R.id.source_et)).getText().toString().trim();
                    String des_str =((AutoCompleteTextView)v.findViewById(R.id.destination_et)).getText().toString().trim();
                    System.out.println("Value @ "+i+" src :"+src_str+" des :"+des_str);
                    if (src_str.length()!=0 && des_str.length()!=0) {
                        try {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.putOpt("source", src_str);
                            jsonObj.putOpt("destination", des_str);
                            arraySrcDes.put(jsonObj);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

                if (arraySrcDes.length() == childcount) {
                    String data ="";
                    for (int i = 0; i < arraySrcDes.length(); i++) {
                        try {
                            JSONObject j = arraySrcDes.getJSONObject(i);
                            data=data+"&source["+i+"]="+j.getString("source")+"&destination["+i+"]="+j.getString("destination");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Issue in appending data", Toast.LENGTH_LONG).show();
                        }
                    }
                    return data;
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.srcdeswrong_str), Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.srcdeszero_str), Toast.LENGTH_LONG).show();
            }
        }
        return "";
    }


    private class SetSettings extends AsyncTask<Void,Void,JSONObject>{

        String accountid;
        String emailDailSummaryReport;
        String routeNotificationCount;
        String stopAlertTime;
        String overSpeedLimit;
        String contactEmail;
        String stopDurationLimit;
        String data;
        JSONParser jsonParser;
        public SetSettings(String data,String accountid, String emailDailSummaryReport, String routeNotificationCount, String stopAlertTime, String overSpeedLimit, String contactEmail, String stopDurationLimit) {
            this.accountid = accountid;
            this.emailDailSummaryReport = emailDailSummaryReport;
            this.routeNotificationCount = routeNotificationCount;
            this.stopAlertTime = stopAlertTime;
            this.overSpeedLimit = overSpeedLimit;
            this.contactEmail = contactEmail;
            this.stopDurationLimit = stopDurationLimit;
            this.data = data;
            jsonParser = JSONParser.getInstance();
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Updating settings");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject jsonRes = null;
            try {
                String urlParams = setUrlParams(accountid, emailDailSummaryReport, routeNotificationCount,
                        stopAlertTime, overSpeedLimit, contactEmail, stopDurationLimit, data);
                String response = jsonParser.excutePost(TruckApp.settingsURL, urlParams);
                jsonRes = new JSONObject(response);
            }catch (Exception e){
                Log.e("Settings the settings",e.toString());
            }
            return jsonRes;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            TruckApp.checkPDialog(pDialog);
            try{
                if(jsonObject!=null){
                    if(jsonObject.has("status") && jsonObject.getInt("status")==1){
                        Toast.makeText(mContext,"Successfully updated settings",Toast.LENGTH_LONG).show();
                    }else if(jsonObject.has("status") && jsonObject.getInt("status")==2){
                       //TruckApp.logoutAction(SettingsActivity.this);
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
                    }else{
                        Toast.makeText(mContext,"Failed to update settings",Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(mContext,"Failed to update settings",Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Toast.makeText(mContext,"Failed to update settings",Toast.LENGTH_LONG).show();
            }
        }

        public String setUrlParams(String accountid, String emailDailSummaryReport, String routeNotificationCount,
                                   String stopAlertTime, String overSpeedLimit, String contactEmail,
                                   String stopDurationLimit, String data){

            StringBuilder builder= new StringBuilder();
            try {
                builder.append("accountid=").append(URLEncoder.encode(accountid, "UTF-8"));
                builder.append("&emailDailSummaryReport=").append(URLEncoder.encode(emailDailSummaryReport, "UTF-8"));
                builder.append("&routeNotificationCount=").append(URLEncoder.encode(routeNotificationCount, "UTF-8"));
                builder.append("&stopAlertTime=").append(URLEncoder.encode(stopAlertTime, "UTF-8"));
                builder.append("&overSpeedLimit=").append(URLEncoder.encode(overSpeedLimit, "UTF-8"));
                builder.append("&contactEmail=").append(URLEncoder.encode(contactEmail, "UTF-8"));
                builder.append("&stopDurationLimit=").append(URLEncoder.encode(stopDurationLimit, "UTF-8"));
                builder.append("&type=").append(URLEncoder.encode("set", "UTF-8")).append(data);
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }


    private class GetSettings extends AsyncTask<Void,Void,JSONObject>{

        String accountID;
        JSONParser parser;

        public GetSettings(String accountID) {
            this.accountID = accountID;
            parser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Getting the settings");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                String urlParameters =
                        "accountid=" + URLEncoder.encode(accountID, "UTF-8")+"&type="+URLEncoder.encode("get", "UTF-8")
                                +"&access_token="+URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8");
                String response = parser.excutePost(TruckApp.settingsURL,urlParameters);
                return new JSONObject(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            TruckApp.checkPDialog(pDialog);
            if(jsonObject!=null){
                try {
                    if(jsonObject.has("status") && jsonObject.getInt("status") == 1){
                        if(jsonObject.has("settings")){
                            JSONObject settingsObject = jsonObject.getJSONObject("settings");
                            setTheViews(settingsObject);
                        }

                        if(jsonObject.has("routes")){
                            JSONArray routesArray = jsonObject.getJSONArray("routes");
                            setSrcDesList(routesArray);
                        }
                    }else if(jsonObject.has("status") && jsonObject.getInt("status") == 2){
                        //TruckApp.logoutAction(SettingsActivity.this);
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
                    }else {
                        Toast.makeText(mContext, getResources().getString(R.string.settings_err), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(mContext, getResources().getString(R.string.settings_err), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setTheViews(JSONObject settingsObject) {
        try{
            min_stop_minutes_et.setText(settingsObject.getString("stopDurationLimit"));
            over_speed_et.setText(settingsObject.getString("overSpeedLimit"));
            customer_email_et.setText(settingsObject.getString("contactEmail"));
            stop_alert_minutes.setText(settingsObject.getString("stopAlertTime"));
            route_notification_spn.setSelection(getPosition(settingsObject.getString("routeNotificationCount"),
                    getResources().getStringArray(R.array.route_notification_interval)));
            if(settingsObject.getString("emailDailSummaryReport").equals("0")){
                email_daily_spn.setSelection(1);
            }else{
                email_daily_spn.setSelection(0);
            }
        }catch (Exception ex){

        }
    }

    private void setSrcDesList(JSONArray optDesArray) throws JSONException {
        if (optDesArray.length()>0) {
            for (int i = 0; i < optDesArray.length(); i++) {
                final View addView = layoutInflater.inflate(R.layout.operatingrouteitem, operatRoutesLayout, false);

                final AutoCompleteTextView source_et = (AutoCompleteTextView) addView.findViewById(R.id.source_et);
                final AutoCompleteTextView destination_et = (AutoCompleteTextView) addView.findViewById(R.id.destination_et);
                ImageView buttonRemove = (ImageView) addView.findViewById(R.id.delete_imgbtn);
                source_et.requestFocus();

                source_et.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                        // TODO Auto-generated method stub
                        if (s.toString().length() <= 3 && detectConnection.isConnectingToInternet()) {
                            names.clear();
                            updateList(s.toString(), source_et);
                        } else {
                            if(!detectConnection.isConnectingToInternet())
                                System.out.println("NO NET");
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                    }


                });
                destination_et.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                        // TODO Auto-generated method stub
                        if (s.toString().length() <= 3 && detectConnection.isConnectingToInternet()) {
                            names.clear();
                            updateList(s.toString(), destination_et);
                        } else {
                            if(!detectConnection.isConnectingToInternet())
                                System.out.println("NO NET");
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                    }
                });

                buttonRemove.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((LinearLayout) addView.getParent()).removeView(addView);
                    }
                });

                operatRoutesLayout.addView(addView);
                source_et.setText((optDesArray.getJSONObject(i)).getString("source"));
                destination_et.setText((optDesArray.getJSONObject(i)).getString("destination"));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TruckApp.getInstance().trackScreenView("Settings Screen");
        /*if (adView != null) {
            adView.resume();
        }*/

    }

    public int getPosition(String s, String[] array){
        List<String> list = Arrays.asList(array);
        return list.indexOf(s);
    }
}
