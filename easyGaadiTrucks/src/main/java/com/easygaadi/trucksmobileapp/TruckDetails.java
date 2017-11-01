package com.easygaadi.trucksmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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
import com.easygaadi.models.TruckVo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TruckDetails extends AppCompatActivity {

    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    String lookuup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_details2);
        context = TruckDetails.this;
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        detectCnnection = new ConnectionDetector(context);

        lookuup = getIntent().getStringExtra("hitupdate");
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
            pDialog.setMessage("Fetching Trucks Please..");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                Log.e("truck id",lookuup);
                String res = parser.erpExecuteGet(context,TruckApp.truckListURL+"/"+lookuup);
                Log.e("truckDetails",res.toString());
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
                    if (!result.getBoolean("status")) {
                        Toast.makeText(context, ""+result.getString("message"),Toast.LENGTH_LONG).show();
                        pDialog.show();
                        finish();
                    }else
                    {
                        JSONObject partArray = result.getJSONObject("truck");
                        ((TextView)findViewById(R.id.truck_reg_lbl)).setText(partArray.getString("registrationNo"));
                        ((TextView)findViewById(R.id.truck_type)).setText(getFormatDate(partArray.getString("truckType")));
                        ((TextView)findViewById(R.id.fitnessExpiry)).setText(getFormatDate(partArray.getString("fitnessExpiry")));
                        ((TextView)findViewById(R.id.truck_ins)).setText(getFormatDate(partArray.getString("insuranceExpiry")));
                        ((TextView)findViewById(R.id.truck_pollexpiry)).setText(getFormatDate(partArray.getString("pollutionExpiry")));


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
