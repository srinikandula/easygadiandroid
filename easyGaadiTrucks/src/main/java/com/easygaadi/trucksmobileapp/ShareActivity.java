package com.easygaadi.trucksmobileapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.easygaadi.adapter.DevicesAdapter;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.interfaces.GetTrucksInterface;
import com.easygaadi.network.GetDevices;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShareActivity extends AppCompatActivity implements GetTrucksInterface, DevicesAdapter.ClickedPosition {


    private static final String TAG_RESULT = "predictions";
    JSONObject json;
    String url = "";

    ArrayList<String> names;
    ArrayAdapter<String> adapter;
    private String browserKey = "AIzaSyAqh4el3Bd1lafjwQMQ7w6hJ_OVZWEst-0";
    private String API_KEY = "AIzaSyCd0ifbJjaQoMN66oY3Vyjh43r2gH9kPxA";

    private AdView adView;

    private AutoCompleteTextView source_et,destination_et;
    private Button createShare_btn;
    private Spinner options_spinner;
    private RecyclerView share_rv;
    private SharedPreferences sharedPreferences;
    private ConnectionDetector detectCnnection;
    private ProgressDialog pDialog;
    private Context mContext;
    private JSONArray devicesArray,shareArray;
    private String selectedId;
    private ShareAdapter shareAdapter;
    private TextView deleteDiaMsg_tv;
    private Button delete_dia_btn,cancel_del_dia_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mContext = this;
        names               = new ArrayList<String>();
        detectCnnection = new ConnectionDetector(mContext);
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        source_et = (AutoCompleteTextView)findViewById(R.id.source_et);
        destination_et = (AutoCompleteTextView)findViewById(R.id.dest_et);
        createShare_btn = (Button) findViewById(R.id.create_share_btn);
        options_spinner = (Spinner) findViewById(R.id.options_spn);
        share_rv = (RecyclerView) findViewById(R.id.share_rv);
        adView = (AdView)findViewById(R.id.adView);
        //intializeBannerAd();
        shareArray = new JSONArray();
        devicesArray = new JSONArray();

        shareAdapter = new ShareAdapter(shareArray);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        setAdapter();
        source_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (s.toString().length() >0 && detectCnnection.isConnectingToInternet()) {
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
                if (s.toString().length() > 0 && detectCnnection.isConnectingToInternet()) {
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

        createShare_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(detectCnnection.isConnectingToInternet()){
                    createShare();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
        if(detectCnnection.isConnectingToInternet()){
            new GetShares(sharedPreferences.getString("accountID",
                    "no accountid ")).execute();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TruckApp.getInstance().trackScreenView("ShareVehicle Screen");
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

    @Override
    protected void onDestroy() {
       /* if (adView != null)
        { adView.destroy(); }*/
        super.onDestroy();
    }


    private void intializeBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void createShare() {
        String source = source_et.getText().toString().trim();
        String destination = destination_et.getText().toString().trim();
        String selectedID = options_spinner.getSelectedItem().toString().trim();
        if(!source.isEmpty() && !destination.isEmpty() && selectedID!=null && !selectedID.isEmpty()){
            new CreateShare(sharedPreferences.getString("accountID",
                    "no accountid "),selectedID,source,destination).execute();
        }else{
            String errMsg= "";
            if(source.isEmpty()){
                errMsg = errMsg +"Source is Empty\n";
            }
            if(destination.isEmpty()){
                errMsg = errMsg +"Destination is Empty\n";
            }
            if(selectedID!=null && selectedID.isEmpty()){
                errMsg = errMsg +"Select vehicle\n";
            }
            Toast.makeText(mContext,errMsg,Toast.LENGTH_LONG).show();
        }
    }

    public void setAdapter(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        share_rv.setLayoutManager(mLayoutManager);
        share_rv.setItemAnimator(new DefaultItemAnimator());
        share_rv.setAdapter(shareAdapter);
    }


    public void updateList(String place,final AutoCompleteTextView auto_tv) {
        String input = "";

        try {
            input = "input=" + URLEncoder.encode(place, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        String output = "json";
        String parameter = input + "&types=geocode&sensor=true&key="
                + browserKey;

        url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
                + output + "?" + parameter;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONArray ja = response.getJSONArray(TAG_RESULT);

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject c = ja.getJSONObject(i);
                        String description = c.getString("description");
                        Log.d("description", description);
                        names.add(description);
                    }

                    adapter = new ArrayAdapter<String>(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1, names) {
                        @Override
                        public View getView(int position,
                                            View convertView, ViewGroup parent) {
                            View view = super.getView(position,
                                    convertView, parent);
                            TextView text = (TextView) view
                                    .findViewById(android.R.id.text1);
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
    }


    /*public void updateList(String place,final AutoCompleteTextView auto_tv) {
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
            String placesApi = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key="+API_KEY+"&components=country:in&types=(cities)"+input;

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
                            public View getView(int position,
                                                View convertView, ViewGroup parent) {
                                View view = super.getView(position,
                                        convertView, parent);
                                TextView text = (TextView) view
                                        .findViewById(android.R.id.text1);
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
    }*/

    @Override
    public void callStarted() {
        pDialog.setMessage("Fetching devices ...");
        pDialog.show();
    }

    @Override
    public void callSuccess(JSONArray trucksArray) {
        TruckApp.checkPDialog(pDialog);
        if(trucksArray!=null && trucksArray.length() != 0){
            devicesArray = trucksArray;
            List<String> devicesIds = new ArrayList<>();
            try {
                for (int i=0 ;i<trucksArray.length();i++){
                    devicesIds.add(trucksArray.getJSONObject(i).getString("deviceID"));
                }
                options_spinner.setAdapter(new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,devicesIds));
                /*if(detectCnnection.isConnectingToInternet()){
                    new GetShares(sharedPreferences.getString("accountID",
                            "no accountid ")).execute();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
                    finish();
                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void callFailure(String message) {
        TruckApp.checkPDialog(pDialog);
        if(message.equalsIgnoreCase("Access Token Failed")){
            SharedPreferences sharedPreferences = getSharedPreferences(
                    getResources().getString(R.string.app_name), MODE_PRIVATE);
            SharedPreferences.Editor
                    editor = sharedPreferences.edit();
            editor.putInt("login", 0).commit();
            editor.putString("accountID", "").commit();
            startActivity(new Intent(mContext,
                    LoginActivity.class));
            finish();
        }else {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void clickedPositon(String deviceID) {

    }


    private class CreateShare extends AsyncTask<Void,Void,JSONObject>{

        private String deviceID,source,destination,accountID;
        private JSONParser jsonParser;

        public CreateShare(String accountID,String deviceID, String source, String destination) {
            this.accountID = accountID;
            this.deviceID = deviceID;
            this.source = source;
            this.destination = destination;
            jsonParser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Creating the share item");
            pDialog.show();
        }

        /*set,accountID,access_token*/
        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject jsonObject = null;
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("accountID=").append(URLEncoder.encode(accountID, "UTF-8"));
                builder.append("&source=").append(URLEncoder.encode(source, "UTF-8"));
                builder.append("&destination=").append(URLEncoder.encode(destination, "UTF-8"));
                builder.append("&deviceID=").append(URLEncoder.encode(deviceID, "UTF-8"));
                builder.append("&type=set");
                builder.append("&access_token=").
                        append(sharedPreferences.getString("access_token",""));
                String res = jsonParser.excutePost(TruckApp.SHARE_URL,builder.toString());
                jsonObject = new JSONObject(res);
                if(jsonObject.has("status") && jsonObject.getInt("status")==1){
                    builder.setLength(0);
                    builder.append("accountID=").append(URLEncoder.encode(accountID, "UTF-8"));
                    builder.append("&type=get");
                    builder.append("&access_token=").
                            append(sharedPreferences.getString("access_token",""));
                    String result = jsonParser.excutePost(TruckApp.SHARE_URL,builder.toString());
                    JSONObject resultObject = new JSONObject(result);
                    if(resultObject.has("status") && resultObject.getInt("status")==1){
                        shareArray = resultObject.getJSONArray("data");
                    }

                }
            }catch (Exception e){
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if(jsonObject.has("status")){
                try{
                    if(jsonObject.getInt("status")==1){
                        Toast.makeText(mContext,"Successfully added the item",Toast.LENGTH_SHORT).show();
                        shareAdapter.swap(shareArray);
                    }else if(jsonObject.getInt("status")==0){
                        Toast.makeText(mContext,"Failed to add the item",Toast.LENGTH_SHORT).show();
                    }else if(jsonObject.getInt("status")==2){
                        //TruckApp.logoutAction(ShareActivity.this);
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
                    }
                }catch (Exception e) {
                    Toast.makeText(mContext,"Failed to add the item",Toast.LENGTH_SHORT).show();
                }
            }
            TruckApp.checkPDialog(pDialog);
        }
    }

    private class GetShares extends AsyncTask<Void,Void,JSONObject>{

        String accountID;
        JSONParser jsonParser;

        public GetShares(String accountID) {
            this.accountID = accountID;
            jsonParser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Fetching vehilces...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject jsonObject = null;
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("accountID=").append(URLEncoder.encode(accountID, "UTF-8"));
                builder.append("&type=get");
                builder.append("&access_token=").
                        append(sharedPreferences.getString("access_token",""));
                String res = jsonParser.excutePost(TruckApp.SHARE_URL,builder.toString());
                jsonObject = new JSONObject(res);
            }catch (Exception e){
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            TruckApp.checkPDialog(pDialog);
            if(jsonObject.has("status")){
                try{
                    if(jsonObject.getInt("status")==1){
                        if(jsonObject.has("data")){
                            shareArray = jsonObject.getJSONArray("data");
                            shareAdapter.swap(shareArray);
                        }
                    }else if(jsonObject.getInt("status")==0){
                        Toast.makeText(mContext,"No records found",Toast.LENGTH_SHORT).show();
                    }else if(jsonObject.getInt("status")==2){
                        //TruckApp.logoutAction(ShareActivity.this);
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
                    }
                }catch (Exception e){
                    Toast.makeText(mContext,"Failed to fetch the vehicles",Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(mContext,"Failed to fetch the vehicles",Toast.LENGTH_SHORT).show();
            }
            if(detectCnnection.isConnectingToInternet()){
                new GetDevices(ShareActivity.this,sharedPreferences.getString("accountID",
                        "no accountid "),mContext,ShareActivity.this).execute();
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    private class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ShareViewHolder>{

        private  JSONArray shareArray;

        public ShareAdapter(JSONArray shareArray) {
            this.shareArray = shareArray;
        }

        @Override
        public ShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_item,parent,false);
            return new ShareViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ShareViewHolder holder, int position) {
            try{
                //link , msg
                JSONObject shareObj = shareArray.getJSONObject(position);
                holder.src_tv.setText(shareObj.getString("source"));
                holder.des_tv.setText(shareObj.getString("destination"));
                holder.vehicle_tv.setText(shareObj.getString("deviceID"));
                holder.date_tv.setText(shareObj.getString("dateCreated"));
                holder.share_iv.setTag(shareObj.toString());
                holder.delete_iv.setTag(shareObj.toString());
                holder.share_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareVehicle(view.getTag().toString());
                    }
                });
                holder.delete_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(mContext,,Toast.LENGTH_SHORT).show();
                        confirmDialog(view.getTag().toString());
                    }
                });
            }catch (Exception e){

            }
        }

        @Override
        public int getItemCount() {
            return shareArray.length();
        }

        public void swap(JSONArray shareArray){
            this.shareArray = shareArray;
            notifyDataSetChanged();
        }

        public class ShareViewHolder extends RecyclerView.ViewHolder{

            public TextView src_tv,des_tv,vehicle_tv,date_tv;
            public ImageView share_iv,delete_iv;
            public ShareViewHolder(View itemView) {
                super(itemView);
                src_tv = (TextView)itemView.findViewById(R.id.src_tv);
                des_tv = (TextView)itemView.findViewById(R.id.des_tv);
                vehicle_tv = (TextView)itemView.findViewById(R.id.veh_tv);
                date_tv = (TextView)itemView.findViewById(R.id.date_tv);
                share_iv = (ImageView)itemView.findViewById(R.id.share_iv);
                delete_iv = (ImageView)itemView.findViewById(R.id.delete_iv);
            }
        }

    }


    protected void confirmDialog(String string) {
        try {
            final JSONObject jsonObj = new JSONObject(string);
            final Dialog confirmDialog = new Dialog(mContext, android.R.style.Theme_Dialog);
            confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            confirmDialog.setContentView(R.layout.delete_share);
            TextView message_tv = (TextView) confirmDialog.findViewById(R.id.message_tv);
            Button confirm_btn = (Button) confirmDialog.findViewById(R.id.confirm_dlg_confirm_btn);
            Button cancel_btn = (Button) confirmDialog.findViewById(R.id.confirm_dlg_cancel_btn);
            message_tv.setText("Do you want to delete the record with vehicle "+jsonObj.getString("deviceID"));
            confirm_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detectCnnection.isConnectingToInternet()) {
                        try {
                            new DeleteShareItem(jsonObj.getString("id")).execute();
                            confirmDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                        Toast.makeText(mContext, R.string.internet_str, Toast.LENGTH_SHORT).show();

                }

            });

            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDialog.dismiss();
                }
            });
            confirmDialog.show();


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private class DeleteShareItem extends AsyncTask<Void,Void,JSONObject>{

        String id;
        JSONParser jsonParser;
        public DeleteShareItem(String id) {
            this.id = id;
            jsonParser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Deleting .....");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject jsonObject = null;
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("accountID=").append(URLEncoder.encode(sharedPreferences.getString("accountID",
                        "no accountid ") , "UTF-8"));
                builder.append("&type=delete");
                builder.append("&id=").append(URLEncoder.encode(id,"UTF-8"));
                builder.append("&access_token=").
                        append(sharedPreferences.getString("access_token",""));
                String res = jsonParser.excutePost(TruckApp.SHARE_URL,builder.toString());
                jsonObject = new JSONObject(res);
            }catch (Exception e){
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if(jsonObject.has("status")){
                try{
                    if(jsonObject.getInt("status")==1){
                        Toast.makeText(mContext,"Successfully removed the item",Toast.LENGTH_SHORT).show();
                        JSONArray newDevicesArray = new JSONArray();
                        for (int i=0 ;i<shareArray.length() ;i++){
                            JSONObject jsonObject1 = shareArray.getJSONObject(i);
                            if(!jsonObject1.getString("id").equalsIgnoreCase(id)){
                                newDevicesArray.put(jsonObject1);
                            }
                        }
                        shareArray = newDevicesArray;
                        shareAdapter.swap(shareArray);
                    }else if(jsonObject.getInt("status")==0){
                        Toast.makeText(mContext,"Failed to remove the item",Toast.LENGTH_SHORT).show();
                    }else if(jsonObject.getInt("status")==2){
                        //TruckApp.logoutAction(ShareActivity.this);
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
                    }
                }catch (Exception e) {
                    Toast.makeText(mContext,"Failed to remove the item",Toast.LENGTH_SHORT).show();
                }
            }
            TruckApp.checkPDialog(pDialog);
        }
    }

    private void shareVehicle(String data) {
        try{
            JSONObject shareObj = new JSONObject(data);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(shareObj.getString("link")).append("\n\n");
            builder.append(shareObj.getString("msg")).append("\n");
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_SUBJECT, "Share vehicle details of "+shareObj.getString("deviceID"));
            email.putExtra(Intent.EXTRA_TEXT, builder.toString());
            email.setType("text/plain");
            startActivity(Intent.createChooser(email, "Share"));
        }catch (Exception e){

        }
    }
}

