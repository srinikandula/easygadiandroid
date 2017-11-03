package com.easygaadi.trucksmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TripsDetails_Activty extends AppCompatActivity {

    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    String lookuup,forActivty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_details__activty);
        context = TripsDetails_Activty.this;
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        detectCnnection = new ConnectionDetector(context);

        lookuup = getIntent().getStringExtra("hitupdate");
        forActivty = getIntent().getStringExtra("call");
        if (detectCnnection.isConnectingToInternet()) {
            new GetBuyingTrucks().execute();
        } else {
            Toast.makeText(context,
                    res.getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void callback(View view){
        finish();
    }


    private class GetBuyingTrucks extends AsyncTask<String, String, JSONObject> {

        //String uid, accountid, offset;

        public GetBuyingTrucks() {
            //this.uid = uid;
            //this.accountid = accountid;
            //this.offset = String.valueOf(offset);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Fetching Trip Details Please..");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                Log.i("trip id",lookuup);
                String res = parser.erpExecuteGet(context,TruckApp.tripsListURL+"/"+lookuup);
                Log.e("tripsListURL",res.toString());
                json = new JSONObject(res);

            } catch (Exception e) {
                Log.e("tripsListURL DoIN EX", e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {

                try {
                    if (!result.getBoolean("status")) {
                        Toast.makeText(context, ""+"unable to fetch records",Toast.LENGTH_LONG).show();
                        pDialog.show();
                        finish();
                    }else
                    {
                        JSONObject tripObj = result.getJSONObject("trip");



                        pDialog.dismiss();
                    }



                } catch (Exception e) {
                    System.out.println("ex in get leads" + e.toString());
                    pDialog.show();
                }

            } else {
                pDialog.dismiss();
                Toast.makeText(context,
                        getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    public String getFormatDate(String fdate){

        Date date;
        String diff = "";

        DateFormat dateFormat,formatter;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
            date = dateFormat.parse(fdate);
            formatter = new SimpleDateFormat("yyyy-MM-dd"); //If you need time just put specific format for time like 'HH:mm:ss'
            diff = formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("err--"+e.getMessage());
        }

        return diff;
    }
}
