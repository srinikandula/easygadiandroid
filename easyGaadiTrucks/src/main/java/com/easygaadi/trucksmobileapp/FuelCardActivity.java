package com.easygaadi.trucksmobileapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.CustomDateTimePicker;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.interfaces.TrucksAsyncInterface;
import com.easygaadi.network.SaveCardSettings;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FuelCardActivity extends RootActivity implements TrucksAsyncInterface{

    private List<String> cardsList;
    private Dialog card2cardTransferDialog,reportDialog;
    private TextView balance_tv,loyalitypoints_tv;
    private Button redeemBtn,rechargeBtn,reportBtn;
    private RecyclerView fuel_rv;
    private SharedPreferences sharedPreferences;
    private ConnectionDetector detectCnnection;
    private ProgressDialog pDialog;
    private Dialog setCardLimitDialog,rechargeDialog,ccmsTransferDialog,generalSettingsDialog;
    private Context mContext;
    private FuelAdapter fuelAdapter;
    private JSONArray fuelData;
    private CustomDateTimePicker customDialogFrom,customDialogTo;
    private EditText fromdate_tv,todate_tv;
    private ArrayAdapter<String> limitTypeAdapter;
    private InterstitialAd mInterstitialAd;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel_card);
        mContext = this;
        detectCnnection = new ConnectionDetector(mContext);
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        fuelData = new JSONArray();
        cardsList = new ArrayList<>();
        fuelAdapter = new FuelAdapter(fuelData);
        balance_tv = (TextView)findViewById(R.id.ccms_balance_tv);
        loyalitypoints_tv = (TextView)findViewById(R.id.loyalitypoints_tv);
        redeemBtn = (Button)findViewById(R.id.redeem_btn);
        rechargeBtn = (Button)findViewById(R.id.rechargeBtn);
        reportBtn = (Button)findViewById(R.id.reportBtn);
        adView = (AdView)findViewById(R.id.adView);
        //intializeBannerAd();
        redeemBtn.setVisibility(View.GONE);
        rechargeBtn.setVisibility(View.GONE);
        reportBtn.setVisibility(View.INVISIBLE);
        fuel_rv = (RecyclerView)findViewById(R.id.fuel_rv);
        limitTypeAdapter = new ArrayAdapter<String>(mContext,R.layout.custom_spinner_item,R.id.title_tv,getResources().getStringArray(R.array.limit_type_array));

        setAdapter();
        //intializeGoogleAd();
        if(detectCnnection.isConnectingToInternet()) {
            new GetCardDetails().execute();
        }else{
            Toast.makeText(mContext,getResources().getString(R.string.internet_str),Toast.LENGTH_LONG).show();
        }


        rechargeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRechargeDialog();
            }
        });


        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setReportDialog();
            }
        });

        customDialogFrom = new CustomDateTimePicker(this,
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {
                        if(fromdate_tv!=null)
                            fromdate_tv.setText(year+"-"+TruckApp.set2Digit((monthNumber+1))+"-"+TruckApp.set2Digit(calendarSelected.get(Calendar.DAY_OF_MONTH))
                                    + " " + TruckApp.set2Digit(hour24) + ":" + TruckApp.set2Digit(min)+ ":00");
                    }

                    @Override
                    public void onCancel() {

                    }
                });



        customDialogTo = new CustomDateTimePicker(this,
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {
                        if(todate_tv!=null)
                            todate_tv.setText(year+"-"+TruckApp.set2Digit((monthNumber+1))+"-"+TruckApp.set2Digit(calendarSelected.get(Calendar.DAY_OF_MONTH))
                                    + " " + TruckApp.set2Digit(hour24) + ":" + TruckApp.set2Digit(min)+ ":00");
                    }

                    @Override
                    public void onCancel() {

                    }
                });

        /**
         * Pass Directly current time format it will return AM and PM if you set
         * false
         */
        customDialogFrom.set24HourFormat(true);
        customDialogTo.set24HourFormat(true);
        /**
         * Pass Directly current data and time to show when it pop up
         */
        customDialogFrom.setDate(Calendar.getInstance());
        customDialogTo.setDate(Calendar.getInstance());


    }

    public void setAdapter(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        fuel_rv.setLayoutManager(mLayoutManager);
        fuel_rv.setItemAnimator(new DefaultItemAnimator());
        fuel_rv.setAdapter(fuelAdapter);
    }


    private void intializeGoogleAd() {
        mInterstitialAd = new InterstitialAd(FuelCardActivity.this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.eg_interstital));

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
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
        /*if (adView != null)
        { adView.destroy(); }*/
        super.onDestroy();
    }


    private void intializeBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }



    @Override
    public void callStarted() {
        pDialog.setMessage("Updating the details...");
        pDialog.show();
    }

    @Override
    public void saveSettingsCompleted(JSONObject jsonObject, String cardType, String card_username, String card_password, String customerID) {
        TruckApp.checkPDialog(pDialog);
        try {
            if (jsonObject != null) {
                if (jsonObject.has("status") && jsonObject.getInt("status") == 1) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(cardType.equalsIgnoreCase("Fuel Card")){
                        editor.putString(FUEL_USERNAME_KEY,encrypt(card_username));
                        editor.putString(FUEL_PASSWORD_KEY,encrypt(card_password));
                        editor.putString(FUEL_CUSTOMERID_KEY,encrypt(customerID));
                    }else {
                        editor.putString(TOLL_USERNAME_KEY,encrypt(card_username));
                        editor.putString(TOLL_PASSWORD_KEY,encrypt(card_password));
                        editor.putString(TOLL_CUSTOMERID_KEY,encrypt(customerID));
                    }
                    editor.commit();
                    TruckApp.checkDialog(generalSettingsDialog);
                    Toast.makeText(mContext, "Successfully updated the settings", Toast.LENGTH_SHORT).show();
                }else if (jsonObject.has("status") && jsonObject.getInt("status") == 2) {
                    SharedPreferences sharedPreferences = getSharedPreferences(
                            getResources().getString(R.string.app_name),MODE_PRIVATE);
                    SharedPreferences.Editor
                            editor = sharedPreferences.edit();
                    editor.putInt("login", 0).commit();
                    editor.putString("accountID", "").commit();
                    editor.putString(FUEL_USERNAME_KEY,"").commit();
                    editor.putString(FUEL_PASSWORD_KEY,"").commit();
                    editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                    editor.putString(TOLL_USERNAME_KEY,"").commit();
                    editor.putString(TOLL_PASSWORD_KEY,"").commit();
                    editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                    startActivity(new Intent(mContext,
                            LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(mContext, "Failed to save the settings", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(mContext, "Failed to save the settings", Toast.LENGTH_SHORT).show();

            }
        }catch (Exception exe) {
            Toast.makeText(mContext, "Failed to save the settings", Toast.LENGTH_SHORT).show();
        }
    }


    public class GetCardDetails extends AsyncTask<Void,Void,JSONObject> {

        public GetCardDetails() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Getting the balance details...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject json = null;
            try {
                String stringRequest = setUrlParameters();
                String res = JSONParser.getInstance().excutePost(TruckApp.dcardSettingsURL,stringRequest);
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
            try {
                if (jsonObject != null) {
                    if (jsonObject.has("status") && jsonObject.getInt("status") == 1) {
                        if(jsonObject.has("data")){
                            sharedPreferences.edit().putString(FUEL_USERNAME_KEY,encrypt(jsonObject.getJSONObject("data").getString("card_username"))).commit();
                            sharedPreferences.edit().putString(FUEL_PASSWORD_KEY,encrypt(jsonObject.getJSONObject("data").getString("card_password"))).commit();
                            sharedPreferences.edit().putString(FUEL_CUSTOMERID_KEY,encrypt(jsonObject.getJSONObject("data").getString("customerID"))).commit();
                            StringBuilder spannableStringBuilder = new StringBuilder();
                            spannableStringBuilder.append("<u style='color:SkyBlue;'>").
                                    append(getString(R.string.loyality_str)).append("</u><br>")
                                    .append(jsonObject.getJSONObject("data").getString("loyalty_points"));
                            loyalitypoints_tv.setText(getString(R.string.loyality_str)+"\n"+
                                    jsonObject.getJSONObject("data").getString("loyalty_points"));
                            spannableStringBuilder.setLength(0);
                            spannableStringBuilder.append("<u style='color:SkyBlue;'>").
                                    append(getString(R.string.balance_str)).append("</u><br>").
                                    append(jsonObject.getJSONObject("data").getString("ccms_balance"));
                            balance_tv.setText(getString(R.string.balance_str)+"\n"+
                                    jsonObject.getJSONObject("data").getString("ccms_balance"));
                            redeemBtn.setVisibility(View.VISIBLE);
                            rechargeBtn.setVisibility(View.VISIBLE);
                            reportBtn.setVisibility(View.VISIBLE);
                            new GetCardsList().execute();
                        }else{
                            Toast.makeText(mContext, "No card details found", Toast.LENGTH_SHORT).show();
                        }
                    }else if (jsonObject.has("status") && jsonObject.getInt("status") == 2) {
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name),MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(FUEL_USERNAME_KEY,"").commit();
                        editor.putString(FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(TOLL_USERNAME_KEY,"").commit();
                        editor.putString(TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(mContext,
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(mContext, "Failed to get the card details", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(mContext, "Failed to get the card details", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception exe) {
                Toast.makeText(mContext, "Failed to get the card details", Toast.LENGTH_SHORT).show();
            }

        }

        protected String setUrlParameters(){
            StringBuilder builder= new StringBuilder();
            try {
                builder.append("uid=").
                        append(URLEncoder.encode(sharedPreferences.getString("uid",""), "UTF-8"));
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
                builder.append("&accountID=").
                        append(URLEncoder.encode(sharedPreferences.getString("accountID","no account id"), "UTF-8"));
                builder.append("&type=get");
                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }


    public class GetCardsList extends AsyncTask<Void,Void,JSONObject> {

        public GetCardsList() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Getting the cards list...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject json = null;
            try {
                String stringRequest = setUrlParameters();
                String res = JSONParser.getInstance().excutePost(TruckApp.dCardgetCardsURL,stringRequest);
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
            try {
                if (jsonObject != null) {
                    if (jsonObject.has("status") && jsonObject.getInt("status") == 1) {
                        if(jsonObject.has("data")){
                            JSONArray fuelData = jsonObject.getJSONArray("data");
                            fuelAdapter.swapData(fuelData);
                            for (int i=0;i<fuelData.length();i++){
                                JSONObject localJSONObject = fuelData.getJSONObject(i);
                                if (!cardsList.contains(localJSONObject.getString("card_no"))) {
                                    cardsList.add(localJSONObject.getString("card_no"));
                                }
                            }
                        }else {
                            Toast.makeText(mContext, "Failed to get the card list", Toast.LENGTH_SHORT).show();
                        }
                    }else if (jsonObject.has("status") && jsonObject.getInt("status") == 2) {
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name),MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(FUEL_USERNAME_KEY,"").commit();
                        editor.putString(FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(TOLL_USERNAME_KEY,"").commit();
                        editor.putString(TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(mContext,
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(mContext, "Failed to get the card list", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(mContext, "Failed to get the card list", Toast.LENGTH_SHORT).show();

                }
            }catch (Exception exe) {
                Toast.makeText(mContext, "Failed to get the card list", Toast.LENGTH_SHORT).show();
            }
        }

        private void intializeGoogleAd() {
            mInterstitialAd = new InterstitialAd(FuelCardActivity.this);

            // set the ad unit ID
            mInterstitialAd.setAdUnitId(getString(R.string.eg_interstital));

            AdRequest adRequest = new AdRequest.Builder()
                    .build();

            // Load ads into Interstitial Ads
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });

        }

        private void showInterstitial() {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }

        protected String setUrlParameters(){
            StringBuilder builder= new StringBuilder();
            try {
                builder.append("uid=").

                        append(URLEncoder.encode(sharedPreferences.getString("uid",""), "UTF-8"));
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
                builder.append("&accountID=").

                        append(URLEncoder.encode(sharedPreferences.getString("accountID","no account id"), "UTF-8"));
                builder.append("&card_username=").
                        append(URLEncoder.encode(decrypt(sharedPreferences.getString(FUEL_USERNAME_KEY,"NoUsername")), "UTF-8"));
                builder.append("&card_password=").
                        append(URLEncoder.encode(decrypt(sharedPreferences.getString(FUEL_PASSWORD_KEY,"NoPassword")), "UTF-8"));


                builder.append("&type=get");
                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }


    private void setCard2CardDialog(JSONObject jsonObject, int i) throws Exception {
        card2cardTransferDialog = null;
        card2cardTransferDialog = new Dialog(FuelCardActivity.this, android.R.style.Theme_Dialog);
        card2cardTransferDialog.requestWindowFeature(1);
        card2cardTransferDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        card2cardTransferDialog.setContentView(R.layout.card_dialog);
        ImageView cardDiaCls = (ImageView)card2cardTransferDialog.findViewById(R.id.recharge_close);
        Button submit_btn = (Button)card2cardTransferDialog.findViewById(R.id.recharge_btn);
        final EditText from_card_et = (EditText) card2cardTransferDialog.findViewById(R.id.balance_tv);
        final Spinner toCard_spn = (Spinner) card2cardTransferDialog.findViewById(R.id.cardsSpn);
        toCard_spn.setAdapter(new ArrayAdapter<String>(mContext,R.layout.custom_spinner_item,R.id.title_tv,cardsList));
        final EditText amount_et = (EditText) card2cardTransferDialog.findViewById(R.id.recharge_amount_tv);
        from_card_et.setText(jsonObject.getString("card_no"));
        amount_et.setText("");
        amount_et.setError(null);


        card2cardTransferDialog.show();
        cardDiaCls.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                card2cardTransferDialog.dismiss();
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                String fromCard = from_card_et.getText().toString().trim();
                String toCard = toCard_spn.getSelectedItem().toString().trim();
                String amount = amount_et.getText().toString().trim();
                if (!amount.isEmpty() && !fromCard.equalsIgnoreCase(toCard))
                {
                    new Card2CardTransfer(fromCard,toCard, amount).execute(new Void[0]);
                }else {
                    if(fromCard.equalsIgnoreCase(toCard)){
                        Toast.makeText(mContext,"Please select a different card as from and to card cann't be same",Toast.LENGTH_SHORT).show();
                    }
                    TruckApp.editTextValidation(amount_et, amount, "Enter amount");
                }
            }
        });
    }


    private void setReportDialog(){
        reportDialog = null;
        reportDialog = new Dialog(FuelCardActivity.this, android.R.style.Theme_Dialog);
        reportDialog.requestWindowFeature(1);
        reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        reportDialog.setContentView(R.layout.cug_report_dialog);
        ImageView cardDiaCls = (ImageView)reportDialog.findViewById(R.id.recharge_close);
        final Button go_btn = (Button)reportDialog.findViewById(R.id.go_btn);
        fromdate_tv = (EditText) reportDialog.findViewById(R.id.fromDate_et);
        todate_tv = (EditText) reportDialog.findViewById(R.id.toDate_et);
        fromdate_tv.setClickable(true);
        todate_tv.setClickable(true);
        fromdate_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialogFrom.showDialog();
            }
        });
        todate_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialogTo.showDialog();
            }
        });
        reportDialog.show();
        cardDiaCls.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                reportDialog.dismiss();
            }
        });
        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fromDate = fromdate_tv.getText().toString().trim();
                String toDate = todate_tv.getText().toString().trim();
                if(fromDate!=null && !fromDate.isEmpty() && toDate!=null && !toDate.isEmpty()){
                    new GetReport(fromDate,toDate).execute();
                }else{
                    TruckApp.editTextValidation(fromdate_tv,fromDate,"Please Enter From Date");
                    TruckApp.editTextValidation(todate_tv,toDate,"Please Enter To Date");
                }
            }
        });
    }

    private void setCCMSTransferDialog(JSONObject jsonObject, final int position) throws Exception {
        ccmsTransferDialog = null;
        ccmsTransferDialog = new Dialog(FuelCardActivity.this,
                android.R.style.Theme_Dialog);
        ccmsTransferDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ccmsTransferDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        ccmsTransferDialog.setContentView(R.layout.cug_recharge_dialog);
        final ImageView setCardLimitCls = (ImageView) ccmsTransferDialog
                .findViewById(R.id.recharge_close);
        Button rechargeBtn = (Button) ccmsTransferDialog
                .findViewById(R.id.recharge_btn);
        ((TextView) ccmsTransferDialog.findViewById(R.id.dialogtitle_tv)).
                setText(getResources().getString(R.string.ccms_transfer));
        final EditText balance_et = (EditText) ccmsTransferDialog.findViewById(R.id.balance_tv);
        balance_et.setHint(getResources().getString(R.string.ccms_balance_str));
        final EditText card_no_et = (EditText) ccmsTransferDialog.findViewById(R.id.card_no_tv);
        final EditText amount_et = (EditText) ccmsTransferDialog.findViewById(R.id.recharge_amount_tv);
        balance_et.setText(jsonObject.getString("MonthlySaleBalance"));
        card_no_et.setText(jsonObject.getString("card_no"));
        amount_et.setText("");
        ccmsTransferDialog.show();
        setCardLimitCls.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ccmsTransferDialog.dismiss();
            }
        });

        rechargeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String cardNo = card_no_et.getText().toString().trim();
                String amount = amount_et.getText().toString().trim();
                if (!amount.isEmpty()) {
                    new CUGCardTransfer(cardNo,amount).execute();
                } else {
                    TruckApp.editTextValidation(amount_et, amount, "Enter amount");
                }
            }
        });
    }


    public class GetReport
            extends AsyncTask<Void, Void, JSONObject>
    {
        public String from_date,to_date;

        public GetReport(String from_date, String to_date) {
            this.from_date = from_date;
            this.to_date = to_date;
        }

        protected JSONObject doInBackground(Void... paramVarArgs)
        {
            JSONObject resultJSON = null;
            try
            {
                String urlParams = setUrlParameters(from_date,to_date);
                resultJSON = new JSONObject(JSONParser.getInstance().excutePost(TruckApp.dCardUsageReportURL, urlParams));
                return resultJSON;
            }
            catch (Exception exc)
            {
                Log.e("Login DoIN EX", exc.toString());
            }
            return null;
        }

        protected void onPostExecute(JSONObject paramJSONObject)
        {
            super.onPostExecute(paramJSONObject);
            TruckApp.checkPDialog(pDialog);
            if (paramJSONObject != null)
            {
                try
                {
                    if ((paramJSONObject.has("status")) && (paramJSONObject.getInt("status") == 1))
                    {
                        TruckApp.checkDialog(reportDialog);
                        if(paramJSONObject.has("data")){
                            Intent reportIntent = new Intent(mContext,ReportActivity.class);
                            reportIntent.putExtra("fromDate",from_date);
                            reportIntent.putExtra("toDate",to_date);
                            reportIntent.putExtra("data",paramJSONObject.getJSONArray("data").toString());
                            startActivity(reportIntent);
                        }else{
                            Toast.makeText(mContext, "Failed to get report", Toast.LENGTH_SHORT).show();
                        }

                    }else if ((paramJSONObject.has("status")) && (paramJSONObject.getInt("status") == 2)) {
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name),MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(FUEL_USERNAME_KEY,"").commit();
                        editor.putString(FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(TOLL_USERNAME_KEY,"").commit();
                        editor.putString(TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(mContext,
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(mContext, "Failed to get report", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception exe) {
                    Toast.makeText(mContext, "Failed to get report", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(mContext, "Failed to get report", Toast.LENGTH_SHORT).show();
            }
        }

        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog.setMessage("Getting the card report");
            pDialog.show();
        }

        protected String setUrlParameters(String from_date, String to_date)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            try
            {
                localStringBuilder.append("uid=").append(URLEncoder.encode(sharedPreferences.getString("uid", ""), "UTF-8"));
                localStringBuilder.append("&access_token=").append(URLEncoder.encode(sharedPreferences.getString("access_token", ""), "UTF-8"));
                localStringBuilder.append("&accountID=").append(URLEncoder.encode(sharedPreferences.getString("accountID", "no account id"), "UTF-8"));
                localStringBuilder.append("&card_username=").append(URLEncoder.encode(decrypt(sharedPreferences.getString(TOLL_USERNAME_KEY, "NoUsername")), "UTF-8"));
                localStringBuilder.append("&card_password=").append(URLEncoder.encode(decrypt(sharedPreferences.getString(TOLL_PASSWORD_KEY, "NoPassword")), "UTF-8"));
                localStringBuilder.append("&from_date=").append(from_date.replaceAll(" ", "%20"));
                localStringBuilder.append("&to_date=").append(to_date.replaceAll(" ", "%20"));
                return localStringBuilder.toString();
            }
            catch (UnsupportedEncodingException exc)
            {
                exc.printStackTrace();
            }
            return null;
        }
    }



    public class Card2CardTransfer
            extends AsyncTask<Void, Void, JSONObject>
    {
        public String from_card_no,to_card_no,amount;

        public Card2CardTransfer(String from_card_no, String to_card_no,String amount)
        {
            this.from_card_no = from_card_no;
            this.to_card_no = to_card_no;
            this.amount = amount;
        }

        protected JSONObject doInBackground(Void... paramVarArgs)
        {
            JSONObject resultJSON = null;
            try
            {
                String urlParams = setUrlParameters(this.from_card_no,this.to_card_no, this.amount);
                resultJSON = new JSONObject(JSONParser.getInstance().excutePost(TruckApp.dCardToCardTransferURL, urlParams));
                return resultJSON;
            }
            catch (Exception exc)
            {
                Log.e("Login DoIN EX", exc.toString());
            }
            return null;
        }

        protected void onPostExecute(JSONObject paramJSONObject)
        {
            super.onPostExecute(paramJSONObject);
            TruckApp.checkPDialog(pDialog);
            if (paramJSONObject != null)
            {
                try
                {
                    if ((paramJSONObject.has("status")) && (paramJSONObject.getInt("status") == 1))
                    {
                        TruckApp.checkDialog(card2cardTransferDialog);
                        Toast.makeText(mContext, "Successfully transferred", Toast.LENGTH_LONG).show();
                    }else if ((paramJSONObject.has("status")) && (paramJSONObject.getInt("status") == 2)) {
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name),MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(FUEL_USERNAME_KEY,"").commit();
                        editor.putString(FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(TOLL_USERNAME_KEY,"").commit();
                        editor.putString(TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(mContext,
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(mContext, "Failed to transfer", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception exe) {
                    Toast.makeText(mContext, "Failed to transfer", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(mContext, "Failed to transfer", Toast.LENGTH_SHORT).show();
            }
        }

        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog.setMessage("Transfering amount from card to card");
            pDialog.show();
        }

        protected String setUrlParameters(String from_card_no,String to_card_no, String amount)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            try
            {
                localStringBuilder.append("uid=").append(URLEncoder.encode(sharedPreferences.getString("uid", ""), "UTF-8"));
                localStringBuilder.append("&access_token=").append(URLEncoder.encode(sharedPreferences.getString("access_token", ""), "UTF-8"));
                localStringBuilder.append("&accountID=").append(URLEncoder.encode(sharedPreferences.getString("accountID", "no account id"), "UTF-8"));
                localStringBuilder.append("&card_username=").append(URLEncoder.encode(decrypt(sharedPreferences.getString(FUEL_USERNAME_KEY, "NoUsername")), "UTF-8"));
                localStringBuilder.append("&card_password=").append(URLEncoder.encode(decrypt(sharedPreferences.getString(FUEL_PASSWORD_KEY, "NoPassword")), "UTF-8"));
                localStringBuilder.append("&from_card_no=").append(URLEncoder.encode(from_card_no, "UTF-8"));
                localStringBuilder.append("&to_card_no=").append(URLEncoder.encode(to_card_no, "UTF-8"));
                localStringBuilder.append("&amount=").append(URLEncoder.encode(amount, "UTF-8"));
                return localStringBuilder.toString();
            }
            catch (UnsupportedEncodingException exc)
            {
                exc.printStackTrace();
            }
            return null;
        }
    }


    public class CUGCardTransfer extends AsyncTask<Void,Void,JSONObject>{

        public String to_card_no,amount;

        public CUGCardTransfer(String to_card_no, String amount) {
            this.to_card_no = to_card_no;
            this.amount = amount;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Transfering amount from CUG to card");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject json = null;
            try {
                String stringRequest = setUrlParameters(to_card_no,amount);
                String res = JSONParser.getInstance().excutePost(TruckApp.dCardCCMSToCardTransferURL,stringRequest);
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
            try {
                if (jsonObject != null) {
                    if (jsonObject.has("status") && jsonObject.getInt("status") == 1) {
                        TruckApp.checkDialog(ccmsTransferDialog);
                        Toast.makeText(mContext, "Successfully transfer", Toast.LENGTH_SHORT).show();
                    }else if (jsonObject.has("status") && jsonObject.getInt("status") == 2) {
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name),MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(FUEL_USERNAME_KEY,"").commit();
                        editor.putString(FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(TOLL_USERNAME_KEY,"").commit();
                        editor.putString(TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(mContext,
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(mContext, "Failed to transfer", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(mContext, "Failed to transfer", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception exe) {
                Toast.makeText(mContext, "Failed to transfer", Toast.LENGTH_SHORT).show();
            }
        }

        protected String setUrlParameters(String to_card_no,String amount){
            StringBuilder builder= new StringBuilder();
            try {
                builder.append("uid=").
                        append(URLEncoder.encode(sharedPreferences.getString("uid",""), "UTF-8"));
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
                builder.append("&accountID=").
                        append(URLEncoder.encode(sharedPreferences.getString("accountID","no account id"), "UTF-8"));
                builder.append("&card_username=").
                        append(URLEncoder.encode(decrypt(sharedPreferences.getString(TOLL_USERNAME_KEY,"NoUsername")), "UTF-8"));
                builder.append("&card_password=").
                        append(URLEncoder.encode(decrypt(sharedPreferences.getString(TOLL_PASSWORD_KEY,"NoPassword")), "UTF-8"));
                builder.append("&to_card_no=").
                        append(URLEncoder.encode(to_card_no, "UTF-8"));
                builder.append("&amount=").
                        append(URLEncoder.encode(amount, "UTF-8"));
                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }


    private class FuelAdapter extends RecyclerView.Adapter<FuelAdapter.FuelViewHolder>{

        JSONArray tollData;

        public FuelAdapter(JSONArray tollData) {
            this.tollData = tollData;
        }

        public void swapData(JSONArray swapData){
            tollData = swapData;
            notifyDataSetChanged();
        }

        @Override
        public FuelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fuel_card_item,parent,false);
            return new FuelViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FuelViewHolder holder, int position) {
            try{
                JSONObject jsonObject = tollData.getJSONObject(position);
                holder.card_no.setText(jsonObject.getString("card_no"));
                holder.vehicle_no.setText(jsonObject.getString("vehicle_no"));
                holder.monthlySaleBalance.setText(jsonObject.getString("MonthlySaleBalance"));
                holder.expiry_on.setText(jsonObject.getString("expiry_on"));
                holder.status_tv.setText(jsonObject.getString("status"));
                holder.monthlySaleLimit.setText(jsonObject.getString("MonthlySaleLimit"));
                holder.dailySaleLimit.setText(jsonObject.getString("DailySaleLimit"));
                holder.ccmsLimit.setText(jsonObject.getString("CCMSLimit"));
                holder.setLimitBtn.setTag(position+"~"+jsonObject);
                holder.cardTransferBtn.setTag(position+"~"+jsonObject);
                holder.ccmsTransferBtn.setTag(position+"~"+jsonObject);
                holder.setLimitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] tagValues = view.getTag().toString().split("~");
                        try {
                            setCardLimitDialog(new JSONObject(tagValues[1]),
                                    Integer.parseInt(tagValues[0]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                holder.ccmsTransferBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] tagValues = view.getTag().toString().split("~");
                        try {
                            setCCMSTransferDialog(new JSONObject(tagValues[1]),
                                    Integer.parseInt(tagValues[0]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                holder.cardTransferBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] tagValues = view.getTag().toString().split("~");
                        try {
                            setCard2CardDialog(new JSONObject(tagValues[1]),
                                    Integer.parseInt(tagValues[0]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }catch (Exception e){
                Log.e("Error",e.toString());
            }
        }

        @Override
        public int getItemCount() {
            return tollData.length();
        }

        public class FuelViewHolder extends RecyclerView.ViewHolder{

            EditText monthlySaleBalance,expiry_on,status_tv
                    ,ccmsLimit,dailySaleLimit,monthlySaleLimit;
            TextView card_no,vehicle_no;
            Button ccmsTransferBtn,cardTransferBtn,setLimitBtn;
            public FuelViewHolder(View itemView) {
                super(itemView);
                monthlySaleLimit = (EditText)itemView.findViewById(R.id.mslimit_tv);
                dailySaleLimit = (EditText)itemView.findViewById(R.id.dslimit_tv);
                ccmsLimit = (EditText)itemView.findViewById(R.id.ccmslimit_tv);
                monthlySaleBalance = (EditText)itemView.findViewById(R.id.card_balance_tv);
                card_no = (TextView)itemView.findViewById(R.id.card_no_tv);
                vehicle_no = (TextView)itemView.findViewById(R.id.device_tv);
                expiry_on = (EditText)itemView.findViewById(R.id.exp_date);
                status_tv = (EditText)itemView.findViewById(R.id.status_tv);
                ccmsTransferBtn = (Button) itemView.findViewById(R.id.ccmsTransferBtn);
                cardTransferBtn = (Button) itemView.findViewById(R.id.cardTransferBtn);
                setLimitBtn = (Button) itemView.findViewById(R.id.setLimitBtn);
            }
        }
    }

    private void setCardLimitDialog(JSONObject jsonObject, final int position) throws Exception {
        setCardLimitDialog = null;
        setCardLimitDialog = new Dialog(FuelCardActivity.this,
                android.R.style.Theme_Dialog);
        setCardLimitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCardLimitDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        setCardLimitDialog.setContentView(R.layout.card_limit_dialog);
        final ImageView setCardLimitCls = (ImageView) setCardLimitDialog
                .findViewById(R.id.card_limit_close);
        Button saveCardLimitBtn = (Button) setCardLimitDialog
                .findViewById(R.id.card_limit_save);
        final EditText card_no_et = (EditText) setCardLimitDialog.findViewById(R.id.card_no_tv);
        EditText vehicle_no_et = (EditText) setCardLimitDialog.findViewById(R.id.vehicle_no_tv);
        final EditText amount_et = (EditText) setCardLimitDialog.findViewById(R.id.amount_tv);
        final Spinner limitTypeSpn = (Spinner) setCardLimitDialog.findViewById(R.id.limitTypeSpn);
        limitTypeSpn.setAdapter(limitTypeAdapter);
        card_no_et.setText(jsonObject.getString("card_no"));
        vehicle_no_et.setText(jsonObject.getString("vehicle_no"));
        amount_et.setText("");
        limitTypeSpn.setSelection(0);
        setCardLimitDialog.show();
        setCardLimitCls.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setCardLimitDialog.dismiss();
            }
        });

        saveCardLimitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String cardNo = card_no_et.getText().toString().trim();
                String amount = amount_et.getText().toString().trim();
                String limitType = limitTypeSpn.getSelectedItem().toString();
                if (!amount.isEmpty()) {
                    new SetCardLimit(cardNo,amount,limitType,position).execute();
                } else {
                    TruckApp.editTextValidation(amount_et, amount, "Enter amount");
                }
            }
        });
    }


    private void setRechargeDialog(){
        rechargeDialog = null;
        rechargeDialog = new Dialog(FuelCardActivity.this,
                android.R.style.Theme_Dialog);
        rechargeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        rechargeDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        rechargeDialog.setContentView(R.layout.recharge_dialog);
        final ImageView setCardLimitCls = (ImageView) rechargeDialog
                .findViewById(R.id.recharge_close);
        Button rechargeBtn = (Button) rechargeDialog
                .findViewById(R.id.recharge_btn);
        final EditText customerID_et = (EditText) rechargeDialog.findViewById(R.id.customer_id_tv);
        final EditText amount_et = (EditText) rechargeDialog.findViewById(R.id.recharge_amount_tv);
        customerID_et.setText(decrypt(sharedPreferences.getString(FUEL_CUSTOMERID_KEY,"NoCustomerID")));
        amount_et.setText("");
        rechargeDialog.show();
        setCardLimitCls.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                rechargeDialog.dismiss();
            }
        });

        rechargeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String customerID = customerID_et.getText().toString().trim();
                String amount = amount_et.getText().toString().trim();
                if (!amount.isEmpty()) {
                    // new SetCardLimit(cardNo,amount,limitType,position).execute();
                } else {
                    TruckApp.editTextValidation(amount_et, amount, "Enter amount");
                }
            }
        });
    }


    public class SetCardLimit extends AsyncTask<String, String, JSONObject> {

        String card_no, limitValue, limitType;
        int position;

        public SetCardLimit(String card_no, String limitValue, String limitType,int position) {
            this.card_no = card_no;
            this.limitType = limitType;
            this.limitValue = limitValue;
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Updating the card limit");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject json = null;
            try {
                String stringRequest = setUrlParameters(card_no,limitValue,limitType);
                String res = JSONParser.getInstance().excutePost(TruckApp.dCardSetCardLimit,stringRequest);
                json = new JSONObject(res);
            }catch(Exception e){
                Log.e("Login DoIN EX",e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            // TODO Auto-generated method stub
            super.onPostExecute(jsonObject);
            TruckApp.checkPDialog(pDialog);
            try {
                if (jsonObject != null) {
                    if (jsonObject.has("status") && jsonObject.getInt("status") == 1) {
                        Toast.makeText(mContext, "Successfully updated the card limit", Toast.LENGTH_SHORT).show();
                        TruckApp.checkDialog(setCardLimitDialog);
                       /* JSONObject jsonObject1 = fuelData.getJSONObject(position);
                        if(limitType.equalsIgnoreCase("CCMSMonthlyLimit")){
                            jsonObject1.put("",limitValue);
                        }else if(limitType.equalsIgnoreCase("CCMSDailyLimit")){
                            jsonObject1.put("",limitValue);
                        }else if(limitType.equalsIgnoreCase("")){
                            jsonObject1.put("",limitValue);
                        }*/
                    }else if (jsonObject.has("status") && jsonObject.getInt("status") == 2) {
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name),MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(FUEL_USERNAME_KEY,"").commit();
                        editor.putString(FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(TOLL_USERNAME_KEY,"").commit();
                        editor.putString(TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(mContext,
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(mContext, "Failed to update the card limit", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(mContext, "Failed to update the card limit", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception exe) {
                Toast.makeText(mContext, "Failed to update the card limit", Toast.LENGTH_SHORT).show();
            }
        }

        protected String setUrlParameters(String card_no, String limitValue, String limitType){
            StringBuilder builder= new StringBuilder();
            try {
                builder.append("uid=").
                        append(URLEncoder.encode(sharedPreferences.getString("uid",""), "UTF-8"));
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
                builder.append("&accountID=").
                        append(URLEncoder.encode(sharedPreferences.getString("accountID","no account id"), "UTF-8"));
                builder.append("&card_username=").
                        append(URLEncoder.encode(decrypt(sharedPreferences.getString(FUEL_USERNAME_KEY,"NoUsername")), "UTF-8"));
                builder.append("&card_password=").
                        append(URLEncoder.encode(decrypt(sharedPreferences.getString(FUEL_PASSWORD_KEY,"NoPassword")), "UTF-8"));
                builder.append("&card_no=").append(URLEncoder.encode(card_no, "UTF-8"));
                builder.append("&limitValue=").append(URLEncoder.encode(limitValue, "UTF-8"));
                builder.append("&limitType=").append(URLEncoder.encode(limitType, "UTF-8"));
                builder.append("&type=get");
                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fuel_card_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showSettingsDialog("Fuel Card");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog(final String title) {
        generalSettingsDialog = new Dialog(FuelCardActivity.this,
                android.R.style.Theme_Dialog);
        generalSettingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        generalSettingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        generalSettingsDialog.setContentView(R.layout.general_settings);
        TextView titleTv = (TextView)generalSettingsDialog.
                findViewById(R.id.dialogtitle_tv);
        titleTv.setText(getString(R.string.settings)+"("+title+")");
        final ImageView settingsClose = (ImageView)generalSettingsDialog
                .findViewById(R.id.close_iv);
        final EditText  usernameEt = (EditText) generalSettingsDialog
                .findViewById(R.id.uname_et);
        final EditText  passwordEt = (EditText) generalSettingsDialog
                .findViewById(R.id.pwd_et);
        final EditText  customerIDEt = (EditText) generalSettingsDialog
                .findViewById(R.id.customerid_et);
        Button saveBtn = (Button)generalSettingsDialog.findViewById(R.id.save_btn);
        saveBtn.setText(getString(R.string.update));
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings(usernameEt,passwordEt,customerIDEt,title);
            }
        });
        settingsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generalSettingsDialog.dismiss();
            }
        });
        usernameEt.setText("");
        passwordEt.setText("");
        customerIDEt.setText("");
        generalSettingsDialog.show();
        usernameEt.setText(decrypt(sharedPreferences.getString(FUEL_USERNAME_KEY,"NoUsername")));
        passwordEt.setText(decrypt(sharedPreferences.getString(FUEL_PASSWORD_KEY,"NoUsername")));
        customerIDEt.setText(decrypt(sharedPreferences.getString(FUEL_CUSTOMERID_KEY,"NoUsername")));

    }

    private void saveSettings(EditText usernameEt, EditText passwordEt, EditText customerIDEt,String title) {
        if(detectCnnection.isConnectingToInternet()) {
            String username = usernameEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();
            String customerID = customerIDEt.getText().toString().trim();
            if (!username.isEmpty() && !password.isEmpty() && !customerID.isEmpty()) {
                new SaveCardSettings(this,mContext,title, username, password, customerID).execute();
            } else {
                TruckApp.editTextValidation(usernameEt, username, getString(R.string.username_error));
                TruckApp.editTextValidation(passwordEt, password, getString(R.string.password_err));
                TruckApp.editTextValidation(customerIDEt, customerID, getString(R.string.customerid_error));
            }
        }else{
            Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        TruckApp.getInstance().trackScreenView("FuelCard Screen");
        if (adView != null) {
            adView.resume();
        }

    }
}
