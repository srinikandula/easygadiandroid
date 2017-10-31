package com.easygaadi.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.interfaces.TrucksAsyncInterface;
import com.easygaadi.trucksmobileapp.LoginActivity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TruckApp;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by admin on 27-04-2017.
 */
public class SaveCardSettings extends AsyncTask<Void,Void,JSONObject> {
    private String cardType,card_username,card_password,customerID;
    private TrucksAsyncInterface trucksAsyncInterface;
    private Context context;
    public SaveCardSettings(TrucksAsyncInterface trucksAsyncInterface, Context context, String cardType, String card_username,
                            String card_password, String customerID) {
        this.cardType = cardType;
        this.card_username = card_username;
        this.card_password = card_password;
        this.customerID = customerID;
        this.trucksAsyncInterface = trucksAsyncInterface;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        /*pDialog.setMessage("Saving the details...");
        pDialog.show();*/
        trucksAsyncInterface.callStarted();
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        JSONObject json = null;
        try {
            String stringRequest = setUrlParameters(card_username,card_password,customerID);
            String url =  cardType.equalsIgnoreCase("Fuel Card")? TruckApp.dcardSettingsURL:TruckApp.tollCardSettingsURL;
            String res = JSONParser.getInstance().excutePost(url,stringRequest);
            json = new JSONObject(res);
        }catch(Exception e){
            Log.e("Login DoIN EX",e.toString());
        }
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        trucksAsyncInterface.saveSettingsCompleted(jsonObject, cardType, card_username, card_password, customerID);
    }


    protected String setUrlParameters(String card_username,
                                      String card_password, String customerID){
        StringBuilder builder= new StringBuilder();
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    context.getResources().getString(R.string.app_name), context.MODE_PRIVATE);
            builder.append("uid=").
                    append(URLEncoder.encode(sharedPreferences.getString("uid",""), "UTF-8"));
            builder.append("&access_token=").
                    append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
            builder.append("&accountID=").
                    append(URLEncoder.encode(sharedPreferences.getString("accountID","no account id"), "UTF-8"));
            builder.append("&card_username=").append(URLEncoder.encode(card_username, "UTF-8"));
            builder.append("&card_password=").append(URLEncoder.encode(card_password, "UTF-8"));
            builder.append("&customerID=").append(URLEncoder.encode(customerID, "UTF-8"));
            builder.append("&type=set");
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
