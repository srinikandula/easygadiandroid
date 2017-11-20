package com.easygaadi.trucksmobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Party_Activity extends AppCompatActivity {

    private static final String TAG_RESULT = "predictions";
    JSONObject json;
    ArrayList<String> names;
    ArrayAdapter<String> adapter;
    private String browserKey = "AIzaSyAqh4el3Bd1lafjwQMQ7w6hJ_OVZWEst-0";
    private String API_KEY = "AIzaSyCd0ifbJjaQoMN66oY3Vyjh43r2gH9kPxA";
    private LinearLayout operatRoutesLayout;
    String url;
    EditText partNameET,partCitryET,partLaneET,party_mobileET,party_mailET;
    TextView party_name_TV,party_mobile_TV,party_mail_TV,party_city_TV,pary_lne_TV;
    private Button addMore_btn;
    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    private LayoutInflater layoutInflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_);
        layoutInflater =(LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initilizationView();
        context =  Party_Activity.this;
        parser = JSONParser.getInstance();
        res = getResources();
        operatRoutesLayout  = (LinearLayout)findViewById(R.id.operatingroutes);
        addMore_btn         = (Button)findViewById(R.id.addmore);
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        context = getApplicationContext();
        detectCnnection = new ConnectionDetector(context);
        party_name_TV = (TextView)findViewById(R.id.party_name_lbl);
        party_city_TV = (TextView)findViewById(R.id.party_city_lbl);
        pary_lne_TV = (TextView)findViewById(R.id.pary_lne_lbl);
        party_mobile_TV = (TextView)findViewById(R.id.party_mobile_lbl);
        party_mail_TV = (TextView)findViewById(R.id.party_mail_lbl);
        names               = new ArrayList<String>();

        addMore_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                addSrcDest();
            }
        });
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
        String partyLane = getData();//partLaneET.getText().toString().trim();

        if(partyName.length()>0){
            if(partyMob.length() == 10){
                if(partyName.length()>0){
                    if(isValidEmail(partyMail)){
                        if(partyCity.length()>=3){
                            if (detectCnnection.isConnectingToInternet()) {
                                new AddParty(partyName, partyMob,partyMail, partyCity, partyLane).execute();
                            } else {
                                Toast.makeText(context,res.getString(R.string.internet_str),Toast.LENGTH_LONG).show();
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
                JSONArray post_dicts= new JSONArray();

                try {
                    post_dict.put("name" , partyName);
                    post_dict.put("contact",partyMob);
                    post_dict.put("email", partyMail);
                    post_dict.put("city", partyCity);
                    post_dict.put("tripLanes", new JSONArray(partyOptlane));

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


    private void addSrcDest() {
        final View addView = layoutInflater.inflate(R.layout.erp_operatingrouteitem,operatRoutesLayout,false);

        final AutoCompleteTextView source_trip_et       = (AutoCompleteTextView)addView.findViewById(R.id.source_triplane_et);
        final AutoCompleteTextView source_et       = (AutoCompleteTextView)addView.findViewById(R.id.source_et);
        final AutoCompleteTextView destination_et  = (AutoCompleteTextView)addView.findViewById(R.id.destination_et);
        ImageView buttonRemove = (ImageView) addView.findViewById(R.id.delete_imgbtn);
        source_trip_et.requestFocus();

        source_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (s.toString().length() <= 3 && detectCnnection.isConnectingToInternet()) {
                    names.clear();
                    updateList(s.toString(),source_et);
                }else{
                    if(!detectCnnection.isConnectingToInternet())
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
                if (s.toString().length() <= 3 && detectCnnection.isConnectingToInternet()) {
                    names.clear();
                    updateList(s.toString(), destination_et);
                } else {
                    if(!detectCnnection.isConnectingToInternet())
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

                        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, names) {
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

    protected String getData() {
        if(operatRoutesLayout!=null){
            int childcount = operatRoutesLayout.getChildCount();
            JSONArray arraySrcDes= new JSONArray();
            if(childcount>0){
                for(int i=0;i<childcount;i++){
                    View v= operatRoutesLayout.getChildAt(i);
                    String triplane_str =((AutoCompleteTextView)v.findViewById(R.id.source_triplane_et)).getText().toString().trim();
                    String src_str =((AutoCompleteTextView)v.findViewById(R.id.source_et)).getText().toString().trim();
                    String des_str =((AutoCompleteTextView)v.findViewById(R.id.destination_et)).getText().toString().trim();
                    System.out.println("Value @ "+i+" src :"+src_str+" des :"+des_str);
                    //if (src_str.length()!=0 && des_str.length()!=0) {
                        try {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.putOpt("name", triplane_str);
                            jsonObj.putOpt("from", src_str);
                            jsonObj.putOpt("to", des_str);
                            arraySrcDes.put(jsonObj);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    //}
                }
                return arraySrcDes.toString();
               /* if (arraySrcDes.length() == childcount) {
                    String data ="";
                    for (int i = 0; i < arraySrcDes.length(); i++) {
                        try {
                            JSONObject j = arraySrcDes.getJSONObject(i);
                            data=data+"&name["+i+"]="+j.getString("name")+"&from["+i+"]="+j.getString("from")+"&to["+i+"]="+j.getString("to");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Issue in appending data", Toast.LENGTH_LONG).show();
                        }
                    }
                    return data+"-->"+arraySrcDes;
                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.srcdeswrong_str), Toast.LENGTH_LONG).show();
                }*/

            }else {
                //Toast.makeText(getApplicationContext(), getResources().getString(R.string.srcdeszero_str), Toast.LENGTH_LONG).show();
                return "";
            }
        }
        return "";
    }

}