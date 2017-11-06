package com.easygaadi.trucksmobileapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.Date;

public class TruckDetails extends AppCompatActivity {

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
        setContentView(R.layout.activity_truck_details2);
        context = TruckDetails.this;
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        detectCnnection = new ConnectionDetector(context);

        lookuup = getIntent().getStringExtra("hitupdate");
        forActivty = getIntent().getStringExtra("call");
        if (detectCnnection.isConnectingToInternet()) {
            new GetTrucksDetails().execute();
        } else {
            Toast.makeText(context,
                    res.getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void callback(View view){
        if(view.getId() == R.id.truck_res_ton){
            Intent intent = new Intent(TruckDetails.this, Trunck_Activity.class);
            intent.putExtra("hitupdate", lookuup);
            startActivity(intent);
        }else{
            finish();
        }

    }


    private class GetTrucksDetails extends AsyncTask<String, String, JSONObject> {

        //String uid, accountid, offset;

        public GetTrucksDetails() {
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
                //Log.e("truckDetails",res.toString());
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
                        //((TextView)findViewById(R.id.truck_reg_lbl)).setCompoundDrawables(partArray.getString("registrationNo"));

                        ((EditText)findViewById(R.id.truck_type)).setText( ":  "+partArray.getString("truckType"));
                        ((TextView)findViewById(R.id.fitnessExpiry)).setText(":  "+getFormatDate(partArray.getString("fitnessExpiry")));
                        ((TextView)findViewById(R.id.truck_ins)).setText(":  "+getFormatDate(partArray.getString("insuranceExpiry")));
                        ((TextView)findViewById(R.id.truck_pollexpiry)).setText(": " +getFormatDate(partArray.getString("pollutionExpiry")));
                        ((TextView)findViewById(R.id.truck_permit)).setText(": "+getFormatDate(partArray.getString("permitExpiry")));
                        ((TextView)findViewById(R.id.truck_res_ton)).setText("XXT"+ "   "+""+partArray.getString("modelAndYear"));

                        pDialog.dismiss();
                    }



                } catch (Exception e) {
                    System.out.println("ex in get leads" + e.toString());
                    pDialog.show();
                }

            } else {
                pDialog.dismiss();
                Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
            }
        }
    }


    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(TruckDetails.this,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //(view).setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                int tempmonth = view.getMonth()+1;
                String temp = ""+tempmonth;
                if(temp.length()>2){
                    temp ="0"+temp;
                }
                /*if(Tview.getId() == R.id.truck_duetax){

                    truck_duetaxTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(truck_duetaxTV.getText().toString().length()>0){
                        truck_duetaxlbltv.setVisibility(View.VISIBLE);
                        slideUp(truck_duetaxlbltv);
                    }else {
                        truck_duetaxlbltv.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_fexpire){
                    truck_fexpireTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(truck_fexpireTV.getText().toString().length()>0){
                        truck_fexpirelbltv.setVisibility(View.VISIBLE);
                        slideUp(truck_fexpirelbltv);
                    }else {
                        truck_fexpirelbltv.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_insexpire){
                    truck_insexpireTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(truck_insexpireTV.getText().toString().length()>0){
                        truck_insexpirelbltv.setVisibility(View.VISIBLE);
                        slideUp(truck_insexpirelbltv);
                    }else {
                        truck_insexpirelbltv.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_perexpire){
                    truck_perexpireTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(truck_perexpireTV.getText().toString().length()>0){
                        truck_perexpirelblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_perexpirelblTV);
                    }else {
                        truck_perexpirelblTV.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_pollexpire){
                    truck_pollexpireTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(truck_pollexpireTV.getText().toString().length()>0){
                        truck_pollexpirelblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_pollexpirelblTV);
                    }else {
                        truck_pollexpirelblTV.setVisibility(View.INVISIBLE);
                    }
                }*/
            }
        }, year, month, day);

        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    if(Tview.getId() == R.id.truck_model){
                        /*if(truck_modelTV.getText().toString().length()>0){
                            truck_modellblTV.setVisibility(View.VISIBLE);
                        }else {
                            truck_modellblTV.setVisibility(View.INVISIBLE);
                        }*/
                    }
                }
            }
        });

        dpd.show();
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
