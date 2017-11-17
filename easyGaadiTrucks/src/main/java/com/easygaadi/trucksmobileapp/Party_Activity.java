package com.easygaadi.trucksmobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

public class Party_Activity extends AppCompatActivity {

    EditText partNameET,partCitryET,partLaneET,party_mobileET,party_mailET;
    TextView party_name_TV,party_mobile_TV,party_mail_TV,party_city_TV,pary_lne_TV;
    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_);
        initilizationView();
        context =  Party_Activity.this;
        parser = JSONParser.getInstance();
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        context = getApplicationContext();
        detectCnnection = new ConnectionDetector(context);
        party_name_TV = (TextView)findViewById(R.id.party_name_lbl);
        party_city_TV = (TextView)findViewById(R.id.party_city_lbl);
        pary_lne_TV = (TextView)findViewById(R.id.pary_lne_lbl);
        party_mobile_TV = (TextView)findViewById(R.id.party_mobile_lbl);
        party_mail_TV = (TextView)findViewById(R.id.party_mail_lbl);
    }

    public void initilizationView() {
        //driverFnameET,drivermobET,driverSalET
        //erp_frghtamt,erp_advamt,erp_balamt
        partNameET = (EditText) findViewById(R.id.party_name);
        party_mobileET = (EditText) findViewById(R.id.party_mobile);
        party_mailET = (EditText) findViewById(R.id.party_mail);
        partCitryET = (EditText) findViewById(R.id.party_city);
        partLaneET = (EditText) findViewById(R.id.pary_lane);
        chnageTextView(partNameET);
        chnageTextView(party_mobileET);
        chnageTextView(party_mailET);
        chnageTextView(partCitryET);
        chnageTextView(partLaneET);
    }


    public void chnageTextView(final EditText etview){

        etview.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if(R.id.party_name == etview.getId()) {
                    if (string.trim().length() != 0) {
                        party_name_TV.setVisibility(View.VISIBLE);
                        slideUp(party_name_TV);
                    }else{
                        party_name_TV.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.party_mobile == etview.getId()) {
                    if (string.trim().length() != 0) {
                        party_mobile_TV.setVisibility(View.VISIBLE);
                        slideUp(party_mobile_TV);
                    }else{
                        party_city_TV.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.party_mail == etview.getId()) {
                    if (string.trim().length() != 0) {
                        party_mail_TV.setVisibility(View.VISIBLE);
                        slideUp(party_mail_TV);
                    }else{
                        party_city_TV.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.party_city == etview.getId()) {
                    if (string.trim().length() != 0) {
                        party_city_TV.setVisibility(View.VISIBLE);
                        slideUp(party_city_TV);
                    }else{
                        party_city_TV.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.pary_lane == etview.getId()) {
                    if (string.trim().length() != 0) {
                        pary_lne_TV.setVisibility(View.VISIBLE);
                        slideUp(pary_lne_TV);
                    }else{
                        pary_lne_TV.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }




    public void callMainAct(View view){
       // startActivity(new Intent(Party_Activity.this,Maintenance_Activity.class));
        //partNameET,party_mobileET,party_mailET,partCitryET,partLaneET,,
        String partyName = partNameET.getText().toString().trim();
        String partyMob = party_mobileET.getText().toString().trim();
        String partyMail = party_mailET.getText().toString().trim();
        String partyCity = partCitryET.getText().toString().trim();
        String partyLane = partLaneET.getText().toString().trim();

        if(partyName.length()>0){
            if(partyMob.length() == 10){
                if(partyName.length()>0){
                    if(isValidEmail(partyMail)){
                        if(partyCity.length()>=3){

                            if (detectCnnection.isConnectingToInternet()) {
                                new AddParty(partyName, partyMob,partyMail, partyCity, partyLane).execute();
                            } else {
                                Toast.makeText(context,
                                        res.getString(R.string.internet_str),
                                        Toast.LENGTH_LONG).show();
                            }
                        }else
                        {
                            Toast.makeText(Party_Activity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                        }
                    }else
                    {
                        Toast.makeText(Party_Activity.this, "Please Enter Mail ID", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Toast.makeText(Party_Activity.this, "Please Enter Party Name", Toast.LENGTH_SHORT).show();
                }

            }else
            {
                Toast.makeText(Party_Activity.this, "Please Enter 10 digits Mobile Number", Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(Party_Activity.this, "Please Enter Party Name", Toast.LENGTH_SHORT).show();
        }

    }

    public final static boolean isValidEmail(CharSequence target)
    {
        if (TextUtils.isEmpty(target))
        {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }


    private class AddParty extends AsyncTask<String, String, JSONObject> {
        String partyName, partyMob, partyMail,partyCity,partyOptlane;

        public AddParty(String partyName, String partyMob,String partyMail,String partyCity,String partyOptlane) {
            this.partyName = partyName;
            this.partyMob = partyMob;
            this.partyMail = partyMail;
            this.partyCity = partyCity;
            this.partyOptlane = partyOptlane;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressFrame.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject res = null;
            try {

                JSONObject post_dict = new JSONObject();

                try {
                    post_dict.put("name" , partyName);
                    post_dict.put("contact",partyMob);
                    post_dict.put("email", partyMail);
                    post_dict.put("city", partyCity);
                    post_dict.put("operatingLane", partyOptlane);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("" + String.valueOf(post_dict));
                String result = parser.easyyExcutePost(context,TruckApp.addPayURL+"/addParty",String.valueOf(post_dict));
                res = new JSONObject(result);

            } catch (Exception e) {
                Log.e("addPayURL DoIN EX", e.toString());
                res = null;
            }
            return res;
        }
        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
           // login_btn.setEnabled(true);
            progressFrame.setVisibility(View.GONE);
            Log.v("response","res"+s.toString());
            if (s != null) {

                try {
                    //JSONObject js = new JSONObject(s);
                    if (!s.getBoolean("status")) {

                        Toast.makeText(context, "fail",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, ""+"Party Added", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent();
                        intent.putExtra("addItem",s.getJSONObject("party").toString());
                        setResult(123,intent);
                        finish();

                    }
                } catch (JSONException e) {
                    System.out.println("Exception while extracting the response:"+ e.toString());
                }
            } else {
                Toast.makeText(context, res.getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    public void callback(View view){
        Intent intent=new Intent();
        intent.putExtra("addItem","");
        setResult(124,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("addItem","");
        setResult(124,intent);
        finish();
        super.onBackPressed();
        // Do extra stuff here
    }

}