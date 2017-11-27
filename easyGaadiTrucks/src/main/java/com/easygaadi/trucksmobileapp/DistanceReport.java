package com.easygaadi.trucksmobileapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.CustomDateTimePicker;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

public class DistanceReport extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog pDialog;
    private RecyclerView devices_rv;
    private Context mContext;
    private ConnectionDetector detectCnnection;
    private SharedPreferences sharedPreferences;
    private CustomDateTimePicker customDialogFrom,customDialogTo;
    private TextView fromdate_tv,todate_tv;
    private Button generate_btn,download_report;
    private JSONArray reportsArray;
    private DistanceReportAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String fromDateStr,toDateStr;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_report);
        mContext = this;
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        detectCnnection = new ConnectionDetector(mContext);
        reportsArray = new JSONArray();
        adapter = new DistanceReportAdapter(reportsArray);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        pDialog  = new ProgressDialog(this);
        pDialog.setCancelable(false);
        devices_rv = (RecyclerView)findViewById(R.id.distance_report_rv);
        fromdate_tv = (TextView)findViewById(R.id.fromdate_et);
        todate_tv   = (TextView)findViewById(R.id.todate_et);
        generate_btn = (Button)findViewById(R.id.generate_report_tbn);
        download_report = (Button)findViewById(R.id.download_report);
        adView          = (AdView)findViewById(R.id.adView);
        setupActionBar();
        customDialogFrom = new CustomDateTimePicker(this,
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {

                        fromdate_tv.setText(year+"-"+TruckApp.set2Digit((monthNumber+1))+"-"+TruckApp.set2Digit(calendarSelected.get(Calendar.DAY_OF_MONTH))
                                + " " + TruckApp.set2Digit(hour24) + ":" + TruckApp.set2Digit(min)+ ":00");
                    }

                    @Override
                    public void onCancel() {
                    }
                });



        customDialogTo = new CustomDateTimePicker(this,
                new CustomDateTimePicker.ICustomDateTimeListener() {
                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {
                        todate_tv.setText(year+"-"+TruckApp.set2Digit((monthNumber+1))+"-"+TruckApp.set2Digit(calendarSelected.get(Calendar.DAY_OF_MONTH))
                                + " " + TruckApp.set2Digit(hour24) + ":" + TruckApp.set2Digit(min)+ ":00");
                    }

                    @Override
                    public void onCancel() {
                    }
                });
        //intializeAd();

        /**
         * Pass Directly current time format it will return AM and PM if you set
         * false
         */
        customDialogFrom.set24HourFormat(true);
        customDialogTo.set24HourFormat(true);
        /**
         * Pass Directly current data and time to show when it pop up
         */
        customDialogFrom.setDate(Calendar.getInstance());
        customDialogTo.setDate(Calendar.getInstance());

        fromdate_tv.setOnClickListener(this);
        todate_tv.setOnClickListener(this);
        generate_btn.setOnClickListener(this);
        download_report.setOnClickListener(this);
        setAdapter();
    }

    private void intializeAd() {
        if(detectCnnection.isConnectingToInternet()) {
            //AdRequest adRequest = new AdRequest.Builder().build();
            //adView.loadAd(adRequest);
        }else{
            //adView.setVisibility(View.GONE);
        }
    }




    private void setupActionBar() {
        SpannableString s = new SpannableString(getString(R.string.distance_report));
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, getString(R.string.distance_report).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    private void setAdapter() {
        devices_rv.setLayoutManager(mLayoutManager);
        new SimpleDividerItemDecoration(getResources());
        devices_rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fromdate_et:
                customDialogFrom.showDialog();
                break;
            case R.id.todate_et:
                customDialogTo.showDialog();
                break;
            case R.id.generate_report_tbn:
                generateReport();
                break;
            case R.id.download_report:
                download_report();
                break;
            default:
                break;
        }
    }

    private void download_report() {
        if(reportsArray!=null && reportsArray.length()>0){
            StringBuilder builder = new StringBuilder();
            try{
                for (int i =0;i<reportsArray.length();i++){
                    JSONObject report = reportsArray.getJSONObject(i);
                    builder.append("Start Location:").append(report.getString("startLocation")).append("     ").
                            append("End Location:").append(report.getString("endLocation")).append("     ").
                            append("Vehicle No:").append(report.getString("deviceID")).append("     ")
                            .append("Distance:").append(report.getString("km")).append(" K" +
                            "m").append("\n\n");
                }
            }catch (Exception e){

            }
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, "");
            email.putExtra(Intent.EXTRA_SUBJECT, "EasyGaadi Distance Report from "+fromDateStr.replace("%20"," ")+" to "+toDateStr.replace("%20"," "));
            email.putExtra(Intent.EXTRA_TEXT, builder.toString());

//need this to prompts email client only
            email.setType("message/rfc822");

            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }else{
            Toast.makeText(mContext,"No reports to download",Toast.LENGTH_LONG).show();
        }
    }

    private void generateReport() {
        if(detectCnnection.isConnectingToInternet()){
            String fromStr = fromdate_tv.getText().toString().trim();
            String toStr   = todate_tv.getText().toString().trim();
            if(!fromStr.isEmpty() && !toStr.isEmpty()){
                String fromDate = fromStr.replaceAll(" ", "%20");
                String toDate =   toStr.replace(" ", "%20");
                new GenerateReport(sharedPreferences.getString("accountID",
                        "no accountid "),fromDate,toDate).execute();
            }else{
                String error = "Please select ";
                if(fromStr.isEmpty()){
                    error = error + "\nFrom Date";
                }
                if(toStr.isEmpty()){
                    error = error + "\nTo Date";
                }
                Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();

            }
        }else {
            Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TruckApp.getInstance().trackScreenView("DistanceReport Screen");
        /*if (adView != null) {
            adView.resume();
        }*/
    }

    @Override
    protected void onDestroy() {
        /*if (adView != null)
        { adView.destroy(); }*/
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        /*if (adView != null) {
            adView.pause();
        }*/
        super.onPause();
    }

    public class GenerateReport extends AsyncTask<Void,Void,JSONObject>{
        String fromDate,toDate,accountID;
        JSONParser parser;
        public GenerateReport(String accountID,String fromDate, String toDate) {
            this.accountID =accountID;
            this.fromDate = fromDate;
            this.toDate = toDate;
            fromDateStr = fromDate;
            toDateStr   = toDate;
            parser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Fetching the reports");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject responseJSON = null;
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("accountID=").append(URLEncoder.encode(accountID, "UTF-8"));
                builder.append("&from=").append(fromDate);
                builder.append("&to=").append(toDate);
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
                String response = parser.excutePost(TruckApp.distanceReportURL,builder.toString());
                responseJSON = new JSONObject(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseJSON;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            TruckApp.checkPDialog(pDialog);
            if(jsonObject!=null){
                try{
                    if(jsonObject.has("status") && jsonObject.getInt("status")==1){
                        if(jsonObject.has("data")){
                            adapter.swap(jsonObject.getJSONArray("data"));
                            reportsArray = jsonObject.getJSONArray("data");
                        }else{
                            Toast.makeText(mContext,"No records found for reports..",Toast.LENGTH_LONG).show();
                        }
                    }if(jsonObject.has("status") && jsonObject.getInt("status")==2){
                        //TruckApp.logoutAction(DistanceReport.this);
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
                        Toast.makeText(mContext,"Failed to fetch the reports..",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(mContext,"Failed to fetch the reports..",Toast.LENGTH_LONG).show();

                }
            }else {
                Toast.makeText(mContext,"Failed to fetch the reports..",Toast.LENGTH_LONG).show();
            }
        }
    }

    public class DistanceReportAdapter extends RecyclerView.Adapter<DistanceReportAdapter.DistanceReportHolder>{
        JSONArray reportsArray;

        public DistanceReportAdapter(JSONArray reportsArray) {
            this.reportsArray = reportsArray;
        }

        @Override
        public DistanceReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.distance_report_item,parent,false);
            return new DistanceReportHolder(itemView);
        }

        @Override
        public void onBindViewHolder(DistanceReportHolder holder, int position) {
            try{
                JSONObject distanceReport = reportsArray.getJSONObject(position);
                holder.start_location_tv.setText(distanceReport.getString("startLocation"));
                holder.end_location_tv.setText(distanceReport.getString("endLocation"));
                holder.device_name.setText(distanceReport.getString("deviceID"));
                holder.distance_tv.setText(distanceReport.getString("km")+" kms");
            }catch (Exception e){

            }
        }

        @Override
        public int getItemCount() {
            return reportsArray.length();
        }

        public class DistanceReportHolder extends RecyclerView.ViewHolder{
            public TextView device_name,distance_tv,start_location_tv,end_location_tv;
            public DistanceReportHolder(View itemView) {
                super(itemView);
                device_name = (TextView)itemView.findViewById(R.id.device_tv);
                distance_tv = (TextView)itemView.findViewById(R.id.distance_tv);
                start_location_tv = (TextView)itemView.findViewById(R.id.start_point_tv);
                end_location_tv = (TextView)itemView.findViewById(R.id.end_point_tv);
            }
        }

        public void swap(JSONArray reportArray){
            reportsArray = reportArray;
            notifyDataSetChanged();
        }
    }
}
