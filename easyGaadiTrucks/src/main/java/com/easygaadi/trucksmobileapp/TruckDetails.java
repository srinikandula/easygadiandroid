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
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.TruckVo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
                        JSONArray partArray = result.getJSONArray("trucks");

                        pDialog.show();
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

}
