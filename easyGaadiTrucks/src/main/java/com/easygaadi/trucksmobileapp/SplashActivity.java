package com.easygaadi.trucksmobileapp;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.trucksmobileapp.gcm.Config;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.gcm.GoogleCloudMessaging;



public class SplashActivity extends Activity {

    Context context;
    SharedPreferences preferences;

    private ConnectionDetector detectCnnection;

    String regId;

    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";

    static final String TAG = "Register Activity";
    GoogleCloudMessaging gcm;
    JSONParser parser;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MobileAds.initialize(this, Constants.APP_ID);

        context         = this;
        preferences     = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        detectCnnection = new ConnectionDetector(context);
        parser          = JSONParser.getInstance();
        adView          = (AdView)findViewById(R.id.adView);

        setTimer();
        //intializeAd();//comment by riyaz on 2-10-2017
    }

    private void intializeAd() {
        if(detectCnnection.isConnectingToInternet()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    setTimer();
                }
            });
        }else{
            Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    initialization();
                }catch (Exception ex){
                    Log.e(SplashActivity.class.getName(),ex.toString());
                }
            }
        },5000);
    }

    public void initialization(){
        String id = preferences.getString("uid", "NAN");

        if(detectCnnection.isConnectingToInternet()){
            try{
                if(id.equalsIgnoreCase("NAN")){
                    Log.e("F", "NO ACCOUNT ID");
                    if (TextUtils.isEmpty(regId)) {
                        regId = registerGCM();
                        Log.d("RegisterActivity", "GCM RegId: " + regId);
                    }
                }else{
                    Log.e("F", "ACCOUNT ID P");
                    //new GetActiveStatus(id,preferences.getString(SplashActivity.REG_ID, "")).execute();
                    nextIntent();
                }
            }catch(Exception e){
                System.out.println("Ex inn on resume:"+e.toString());
            }

        }else{
            Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
            finish();
        }
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
      /*  if (adView != null)
        { adView.destroy(); }*/
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        TruckApp.getInstance().trackScreenView("Splash Screen");
       /* if (adView != null) {
            adView.resume();
        }*/
    }

    private class GetActiveStatus extends AsyncTask<String, String, JSONObject>{
        String id_admin,deviceid;


        public GetActiveStatus(String id_admin, String deviceid) {
            super();
            this.id_admin = id_admin;
            this.deviceid = deviceid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject json = null;
            try {
                String res =parser.excutePost(TruckApp.checkActiveStatusURL+"?uid="+id_admin+"&deviceid="+deviceid,"");
                json  = new JSONObject(res);
                System.out.println("active:"+res);
            }catch(Exception e){
                Log.e("GetCustomersTypes EX",e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(result!=null){
                try {
                    if(1 == result.getInt("status")){
                        Log.e("F", "ACCOUNT ID ACTIVE");
                        try {
                            if (detectCnnection.isConnectingToInternet()) {
                                if (TextUtils.isEmpty(regId)) {
                                    regId = registerGCM();
                                    Log.d("F", "GCM RegId: " + regId);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("GetCustomersTypes",e.toString());
                            e.printStackTrace();
                        }
                    }else if(0 == result.getInt("status")){
                        Toast.makeText(getApplicationContext(),"Your not active.Please contact admin" , Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Please try again" , Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Some problem please try again" , Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    public void nextIntent(){
        if(1 == preferences.getInt("login", 0)){
            startActivity(new Intent(context, HomeScreenActivity.class));
        }else{
            startActivity(new Intent(context, LoginActivity.class));
        }
        finish();
    }

    public String registerGCM() {

        gcm   = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);
        } else {
            Log.d("RegisterActivity",
                    "registerGCM - successfully registered with GCM server - regId: "
                            + regId);
			/*Toast.makeText(getApplicationContext(),
					"RegId already available. RegId: " + regId,
					Toast.LENGTH_LONG).show();*/
            nextIntent();
        }
        return regId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                    storeRegistrationId(context, regId);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;}
        }.execute();
    }


    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            Log.d("RegisterActivity",
                    "I never expected this! Going down, going down!" + e);
            throw new RuntimeException(e);
        }
    }
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putInt(APP_VERSION, appVersion);
        editor.putInt("login", 0);
        editor.commit();
        nextIntent();
    }

}
