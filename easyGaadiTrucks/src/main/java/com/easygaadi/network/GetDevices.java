package com.easygaadi.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.interfaces.GetTrucksInterface;
import com.easygaadi.trucksmobileapp.LoginActivity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TruckApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by admin on 04-03-2017.
 */
public class GetDevices extends AsyncTask<Void,Void,JSONObject> {

    private GetTrucksInterface getTrucksInterface;
    private String accountid;
    private Activity mContex;
    private JSONParser parser;
    private SharedPreferences sharedPreferences;

    public GetDevices(GetTrucksInterface getTrucksInterface, String accountid, Context mContex, Activity activity) {
        this.getTrucksInterface = getTrucksInterface;
        this.accountid = accountid;
        this.mContex = activity;
        parser = JSONParser.getInstance();
        sharedPreferences = mContex.getApplicationContext().getSharedPreferences(
                mContex.getResources().getString(R.string.app_name), mContex.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(getTrucksInterface!=null){
            getTrucksInterface.callStarted();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        JSONObject json = null;
        try {
            StringBuilder builder= new StringBuilder();
            builder.append("accountid=").append(URLEncoder.encode(accountid, "UTF-8"));
            builder.append("&access_token=").
                    append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
            String res = parser.excutePost(TruckApp.getDevicesURL,builder.toString());
            json = new JSONObject(res);
        }catch(Exception e){
            Log.e("Login DoIN EX",e.toString());
        }
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        if(getTrucksInterface!=null){
            if(result!=null){
                try {
                    if (0 == result.getInt("status")) {
                        getTrucksInterface.callFailure("No devices found.");
                    }else if(2==result.getInt("status")){
                        //TruckApp.logoutAction(mContex);
                        getTrucksInterface.callFailure("Access Token Failed");
                       /* SharedPreferences sharedPreferences = mContex.getSharedPreferences(
                                mContex.getResources().getString(R.string.app_name), mContex.MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        mContex.startActivity(new Intent(mContex,
                                LoginActivity.class));
                        mContex.finish();*/
                    }else if (1 == result.getInt("status")){
                        getTrucksInterface.callSuccess(result.getJSONArray("data"));
                    }
                }catch (Exception e){
                    getTrucksInterface.callFailure("Failed to get devices");
                }
            }else{
                getTrucksInterface.callFailure("Failed to get devices");
            }
        }

    }
}
