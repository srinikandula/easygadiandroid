package com.easygaadi.trucksmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    private AdView adView;
    private ProgressDialog pDialog;
    private RecyclerView notification_rv;
    private Context mContext;
    private ConnectionDetector detectCnnection;
    private SharedPreferences sharedPreferences;
    private NotificationsAdapter notificationsAdapter;
    private JSONArray notificationsArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        mContext = this;
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        notificationsArray = new JSONArray();
        detectCnnection = new ConnectionDetector(mContext);
        notification_rv = (RecyclerView)findViewById(R.id.notifications_rv);
        notificationsAdapter = new NotificationsAdapter(notificationsArray);
        adView = (AdView)findViewById(R.id.adView);
       // intializeBannerAd();
        pDialog  = new ProgressDialog(this);
        pDialog.setCancelable(false);
        setAdapter();
        if(detectCnnection.isConnectingToInternet()){
            new GetNotifications(sharedPreferences.getString("accountID",
                    "no accountid ")).execute();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        TruckApp.getInstance().trackScreenView("Notifications Screen");
        /*if (adView != null) {
            adView.resume();
        }*/
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


    public void setAdapter(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        notification_rv.setLayoutManager(mLayoutManager);
        notification_rv.setItemAnimator(new DefaultItemAnimator());
        notification_rv.setAdapter(notificationsAdapter);
    }

    public class GetNotifications extends AsyncTask<Void,Void,JSONObject>{
        String accountID;
        JSONParser jsonParser;
        public GetNotifications(String accountID) {
            this.accountID = accountID;
            jsonParser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Fetching notifications ...");

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject json = null;
            try {
                StringBuilder builder= new StringBuilder();
                builder.append("accountID=").append(URLEncoder.encode(accountID, "UTF-8"));
                builder.append("&uid=").
                        append(URLEncoder.encode(sharedPreferences.getString("uid", ""), "UTF-8"));
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));

                String res = jsonParser.excutePost(TruckApp.NOTIFICATIONS_URL,builder.toString());
                json = new JSONObject(res);
            }catch(Exception e){
                Log.e("Login DoIN EX",e.toString());
            }
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            TruckApp.checkPDialog(pDialog);
            if(jsonObject.has("status")){
                try{
                    if(jsonObject.getInt("status") == 1){
                        notificationsArray = jsonObject.getJSONArray("data");
                        notificationsAdapter.swap(notificationsArray);
                    }else if(jsonObject.getInt("status") == 0){
                        Toast.makeText(mContext,"No notifications",Toast.LENGTH_LONG).show();
                    }else if(jsonObject.getInt("status") == 2){
                        //TruckApp.logoutAction(NotificationsActivity.this);
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
                        Toast.makeText(mContext,"Failed to fetch notifications",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(mContext,"Failed to fetch notifications",Toast.LENGTH_LONG).show();
                }

            }
        }



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>{

        JSONArray notisArray;

        public NotificationsAdapter(JSONArray notisArray) {
            this.notisArray = notisArray;
        }

        @Override
        public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item,parent,false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NotificationViewHolder holder, int position) {
            try{
                JSONObject notiObj = notisArray.getJSONObject(position);
                holder.message_tv.setText(notiObj.getString("info"));
                holder.date_tv.setText(notiObj.getString("date"));
            }catch (Exception e){

            }
        }

        @Override
        public int getItemCount() {
            return notisArray.length();
        }

        public void swap(JSONArray notiArray){
            this.notisArray = notiArray;
            notifyDataSetChanged();
        }
        public class NotificationViewHolder extends RecyclerView.ViewHolder{

            public TextView message_tv,date_tv;

            public NotificationViewHolder(View itemView) {
                super(itemView);
                message_tv = (TextView)itemView.findViewById(R.id.noti_msg);
                date_tv = (TextView)itemView.findViewById(R.id.noti_date_tv);
            }
        }

    }

}
