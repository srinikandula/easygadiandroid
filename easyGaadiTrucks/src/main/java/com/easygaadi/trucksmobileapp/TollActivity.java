package com.easygaadi.trucksmobileapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.os.Bundle;
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

public class TollActivity extends RootActivity implements TrucksAsyncInterface {

    private Dialog card2cardRechargeDialog;
    private List<String> cardsList;
    private Dialog cugRechargeDialog,reportDialog;
    private TextView cug_balance_tv;
    private ConnectionDetector detectCnnection;
    private Dialog generalSettingsDialog;
    private TextView loyalitypoints_tv;
    private Context mContext;
    private ProgressDialog pDialog;
    private Button rechargeBtn;
    private Button redeemBtn;
    private SharedPreferences sharedPreferences;
    private TollAdapter tollAdapter;
    private JSONArray tollData;
    private RecyclerView toll_rv;
    private CustomDateTimePicker customDialogFrom,customDialogTo;
    private EditText fromdate_tv,todate_tv;
    private InterstitialAd mInterstitialAd;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toll_card);
        this.mContext = this;
        this.detectCnnection = new ConnectionDetector(this.mContext);
        this.sharedPreferences = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        this.pDialog = new ProgressDialog(this);
        this.pDialog.setCancelable(false);
        this.tollData = new JSONArray();
        this.cardsList = new ArrayList();
        this.tollAdapter = new TollAdapter(this.tollData);
        this.adView = (AdView)findViewById(R.id.adView);
        this.cug_balance_tv = ((TextView)findViewById(R.id.cug_balance_tv));
        this.loyalitypoints_tv = ((TextView)findViewById(R.id.loyalitypoints_tv));
        this.toll_rv = ((RecyclerView)findViewById(R.id.toll_rv));
        this.redeemBtn = ((Button)findViewById(R.id.redeem_btn));
        this.rechargeBtn = ((Button)findViewById(R.id.rechargeBtn));
        this.rechargeBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                if (detectCnnection.isConnectingToInternet())
                {
                    startActivity(new Intent(mContext, WebActivity.class));
                }else{
                    Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
                }
            }
        });
        setAdapter();
        //intializeGoogleAd();
        //intializeBannerAd();
        if (this.detectCnnection.isConnectingToInternet())
        {
            new GetCardDetails().execute();
        }else{
            Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
        }

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

    public void setAdapter()
    {
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.toll_rv.setLayoutManager(localLinearLayoutManager);
        this.toll_rv.setItemAnimator(new DefaultItemAnimator());
        this.toll_rv.setAdapter(this.tollAdapter);
    }

    private void intializeGoogleAd() {

        mInterstitialAd = new InterstitialAd(TollActivity.this);
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

    private void intializeBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


    public class GetCardDetails
            extends AsyncTask<Void, Void, JSONObject>
    {
        public GetCardDetails() {}

        protected JSONObject doInBackground(Void... paramVarArgs)
        {
            JSONObject result = null;
            try
            {
                String urlParams = setUrlParameters();
                result = new JSONObject(JSONParser.getInstance().excutePost(TruckApp.tollCardSettingsURL, urlParams));
                return result;
            }
            catch (Exception exception)
            {
                Log.e("Login DoIN EX", exception.toString());
            }
            return null;
        }

        protected void onPostExecute(JSONObject result)
        {
            super.onPostExecute(result);
            TruckApp.checkPDialog(pDialog);
            if (result != null)
            {
                try
                {
                    if ((result.has("status")) && (result.getInt("status") == 1))
                    {
                        if (result.has("data"))
                        {
                            sharedPreferences.edit().
                                    putString(TOLL_USERNAME_KEY, encrypt(result.getJSONObject("data").getString("card_username"))).commit();
                            sharedPreferences.edit().
                                    putString(TOLL_PASSWORD_KEY, encrypt(result.getJSONObject("data").getString("card_password"))).commit();
                            sharedPreferences.edit().
                                    putString(TOLL_CUSTOMERID_KEY,encrypt(result.getJSONObject("data").getString("customerID"))).commit();
                            loyalitypoints_tv.setText(getString(R.string.loyality_str) + "\n" + result.getJSONObject("data").getString("loyalty_points"));
                            cug_balance_tv.setText(getString(R.string.balance_str) + "\n" + result.getJSONObject("data").getString("cug_balance"));
                            redeemBtn.setVisibility(View.VISIBLE);
                            rechargeBtn.setVisibility(View.VISIBLE);
                            new GetCardsList().execute();
                        }else{
                            Toast.makeText(mContext, "No card details found", Toast.LENGTH_SHORT).show();
                        }
                    }else if (result.has("status") && result.getInt("status") == 2) {
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
                        Toast.makeText(mContext, "No card details found", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception exception)
                {
                    Toast.makeText(mContext, "Failed to get the card details", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(mContext, "Failed to get the card details", Toast.LENGTH_SHORT).show();
            }
        }

        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog.setMessage("Getting the balance details...");
            pDialog.show();
        }

        protected String setUrlParameters()
        {
            Object localObject = new StringBuilder();
            try
            {
                ((StringBuilder)localObject).append("uid=").append(URLEncoder.encode(sharedPreferences.getString("uid", ""), "UTF-8"));
                ((StringBuilder)localObject).append("&access_token=").append(URLEncoder.encode(sharedPreferences.getString("access_token", ""), "UTF-8"));
                ((StringBuilder)localObject).append("&accountID=").append(URLEncoder.encode(sharedPreferences.getString("accountID", "no account id"), "UTF-8"));
                ((StringBuilder)localObject).append("&type=get");
                localObject = ((StringBuilder)localObject).toString();
                return (String)localObject;
            }
            catch (UnsupportedEncodingException localUnsupportedEncodingException)
            {
                localUnsupportedEncodingException.printStackTrace();
            }
            return null;
        }
    }

    public class GetCardsList  extends AsyncTask<Void, Void, JSONObject>
    {
        public GetCardsList() {}

        protected JSONObject doInBackground(Void... paramVarArgs)
        {
            JSONObject resJSON = null;
            try
            {
                String urlParams = setUrlParameters();
                resJSON = new JSONObject(JSONParser.getInstance().excutePost(TruckApp.tollCardgetCardsURL, urlParams));
                return resJSON;
            }
            catch (Exception exc)
            {
                Log.e("Login DoIN EX", exc.toString());
            }
            return null;
        }

        protected void onPostExecute(JSONObject jsonObject)
        {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject != null) {
                    if (jsonObject.has("status") && jsonObject.getInt("status") == 1) {
                        if(jsonObject.has("data")){
                            JSONArray tollData = jsonObject.getJSONArray("data");
                            tollAdapter.swapData(tollData);
                            for (int i=0;i<tollData.length();i++){
                                JSONObject localJSONObject = tollData.getJSONObject(i);
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
            TruckApp.checkPDialog(pDialog);
        }

        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog.setMessage("Getting the cards list...");
            pDialog.show();
        }

        protected String setUrlParameters()
        {
            Object localObject = new StringBuilder();
            try
            {
                ((StringBuilder)localObject).append("uid=").append(URLEncoder.encode(sharedPreferences.getString("uid", ""), "UTF-8"));
                ((StringBuilder)localObject).append("&access_token=").append(URLEncoder.encode(sharedPreferences.getString("access_token", ""), "UTF-8"));
                ((StringBuilder)localObject).append("&accountID=").append(URLEncoder.encode(sharedPreferences.getString("accountID", "no account id"), "UTF-8"));
                ((StringBuilder)localObject).append("&card_username=").append(URLEncoder.encode(decrypt(sharedPreferences.getString(TOLL_USERNAME_KEY, "NoUsername")), "UTF-8"));
                ((StringBuilder)localObject).append("&card_password=").append(URLEncoder.encode(decrypt(sharedPreferences.getString(TOLL_PASSWORD_KEY, "NoPassword")), "UTF-8"));
                ((StringBuilder)localObject).append("&type=get");
                localObject = ((StringBuilder)localObject).toString();
                return (String)localObject;
            }
            catch (UnsupportedEncodingException localUnsupportedEncodingException)
            {
                localUnsupportedEncodingException.printStackTrace();
            }
            return null;
        }
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private class TollAdapter
            extends RecyclerView.Adapter<TollAdapter.TollViewHolder> {
        JSONArray tollData;

        public TollAdapter(JSONArray paramJSONArray) {
            this.tollData = paramJSONArray;
        }

        public int getItemCount() {
            return this.tollData.length();
        }

        public void onBindViewHolder(TollViewHolder paramTollViewHolder, int paramInt) {
            try {
                JSONObject localJSONObject = this.tollData.getJSONObject(paramInt);
                paramTollViewHolder.card_no.setText(localJSONObject.getString("card_no"));
                paramTollViewHolder.vehicle_no.setText(localJSONObject.getString("vehicle_no"));
                paramTollViewHolder.monthlySaleBalance.setText(localJSONObject.getString("balance"));
                paramTollViewHolder.status_tv.setText(localJSONObject.getString("status"));
                paramTollViewHolder.reportBtn.setTag(paramInt + "~" + localJSONObject);
                paramTollViewHolder.cardTransferBtn.setTag(paramInt + "~" + localJSONObject);
                paramTollViewHolder.cugTransferBtn.setTag(paramInt + "~" + localJSONObject);
                paramTollViewHolder.cugTransferBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramAnonymousView) {
                        String[] strArray = paramAnonymousView.getTag().toString().split("~");
                        try {
                            setCUGRechargeDialog(new JSONObject(strArray[1]), Integer.parseInt(strArray[0]));
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
                paramTollViewHolder.cardTransferBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        String[] strArray = view.getTag().toString().split("~");
                        try {
                            setCard2CardDialog(new JSONObject(strArray[1]), Integer.parseInt(strArray[0]));
                        } catch (Exception paramAnonymousView) {
                            paramAnonymousView.printStackTrace();
                        }
                    }
                });
                paramTollViewHolder.reportBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        String[] strArray = view.getTag().toString().split("~");
                        try {
                            setReportDialog(new JSONObject(strArray[1]), Integer.parseInt(strArray[0]));
                        } catch (Exception paramAnonymousView) {
                            paramAnonymousView.printStackTrace();
                        }
                    }
                });
            } catch (Exception exe) {
                Log.e("Error", exe.toString());
            }
        }

        public TollViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
            return new TollViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.toll_card_item, paramViewGroup, false));
        }

        public void swapData(JSONArray paramJSONArray) {
            this.tollData = paramJSONArray;
            notifyDataSetChanged();
        }

        public class TollViewHolder
                extends RecyclerView.ViewHolder {
            Button cardTransferBtn;
            TextView card_no;
            Button cugTransferBtn;
            EditText monthlySaleBalance;
            Button reportBtn;
            EditText status_tv;
            TextView vehicle_no;

            public TollViewHolder(View paramView) {
                super(paramView);
                this.monthlySaleBalance = ((EditText) paramView.findViewById(R.id.card_balance_tv));
                this.card_no = ((TextView) paramView.findViewById(R.id.card_no_tv));
                this.vehicle_no = ((TextView) paramView.findViewById(R.id.device_tv));
                this.status_tv = ((EditText) paramView.findViewById(R.id.status_tv));
                this.cugTransferBtn = ((Button) paramView.findViewById(R.id.cugTransferBtn));
                this.cardTransferBtn = ((Button) paramView.findViewById(R.id.cardTransferBtn));
                this.reportBtn = ((Button) paramView.findViewById(R.id.reportBtn));
            }
        }
    }

    private void setReportDialog(final JSONObject jsonObject, int i) throws Exception{
        reportDialog = null;
        reportDialog = new Dialog(TollActivity.this, android.R.style.Theme_Dialog);
        reportDialog.requestWindowFeature(1);
        reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        reportDialog.setContentView(R.layout.cug_report_dialog);
        ImageView cardDiaCls = (ImageView)reportDialog.findViewById(R.id.recharge_close);
        final Button go_btn = (Button)reportDialog.findViewById(R.id.go_btn);
        fromdate_tv = (EditText) reportDialog.findViewById(R.id.fromDate_et);
        todate_tv = (EditText) reportDialog.findViewById(R.id.toDate_et);
        fromdate_tv.setClickable(true);
        todate_tv.setClickable(true);
        go_btn.setTag(jsonObject.getString("card_no"));
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
                String cardNo = go_btn.getTag().toString();
                if(fromDate!=null && !fromDate.isEmpty() && toDate!=null && !toDate.isEmpty()){
                    new GetReport(fromDate,toDate,cardNo).execute();
                }else{
                    TruckApp.editTextValidation(fromdate_tv,fromDate,"Please Enter From Date");
                    TruckApp.editTextValidation(todate_tv,toDate,"Please Enter To Date");
                }
            }
        });


    }

    private void setCard2CardDialog(JSONObject jsonObject, int i) throws Exception {
        card2cardRechargeDialog = null;
        card2cardRechargeDialog = new Dialog(TollActivity.this, android.R.style.Theme_Dialog);
        card2cardRechargeDialog.requestWindowFeature(1);
        card2cardRechargeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        card2cardRechargeDialog.setContentView(R.layout.card_dialog);
        ImageView cardDiaCls = (ImageView)card2cardRechargeDialog.findViewById(R.id.recharge_close);
        Button submit_btn = (Button)card2cardRechargeDialog.findViewById(R.id.recharge_btn);
        final EditText from_card_et = (EditText) card2cardRechargeDialog.findViewById(R.id.balance_tv);
        final Spinner toCard_spn = (Spinner) card2cardRechargeDialog.findViewById(R.id.cardsSpn);
        toCard_spn.setAdapter(new ArrayAdapter<String>(mContext,R.layout.custom_spinner_item,R.id.title_tv,cardsList));
        final EditText amount_et = (EditText) card2cardRechargeDialog.findViewById(R.id.recharge_amount_tv);
        from_card_et.setText(jsonObject.getString("card_no"));
        amount_et.setText("");
        amount_et.setError(null);
        card2cardRechargeDialog.show();
        cardDiaCls.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                card2cardRechargeDialog.dismiss();
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

    private void setCUGRechargeDialog(JSONObject jsonObject, int i) throws Exception {
        this.cugRechargeDialog = null;
        this.cugRechargeDialog = new Dialog(TollActivity.this, android.R.style.Theme_Dialog);
        this.cugRechargeDialog.requestWindowFeature(1);
        this.cugRechargeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.cugRechargeDialog.setContentView(R.layout.cug_recharge_dialog);
        ImageView setCardLimitCls = (ImageView)cugRechargeDialog.findViewById(R.id.recharge_close);
        Button recharge_btn = (Button)cugRechargeDialog.findViewById(R.id.recharge_btn);
        ((TextView) cugRechargeDialog.findViewById(R.id.dialogtitle_tv)).
                setText(getResources().getString(R.string.cug_transfer));
        final EditText balance_et = (EditText) cugRechargeDialog.findViewById(R.id.balance_tv);
        balance_et.setHint(getResources().getString(R.string.cug_balance_str));
        final EditText card_no_et = (EditText) cugRechargeDialog.findViewById(R.id.card_no_tv);
        final EditText amount_et = (EditText) cugRechargeDialog.findViewById(R.id.recharge_amount_tv);
        balance_et.setText(jsonObject.getString("balance"));
        card_no_et.setText(jsonObject.getString("card_no"));
        amount_et.setText("");
        amount_et.setError(null);
        this.cugRechargeDialog.show();
        setCardLimitCls.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                cugRechargeDialog.dismiss();
            }
        });
        recharge_btn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                String cardNo = card_no_et.getText().toString().trim();
                String amount = amount_et.getText().toString().trim();
                if (!amount.isEmpty())
                {
                    new CUGCardTransfer(cardNo, amount).execute(new Void[0]);
                }else {
                    TruckApp.editTextValidation(amount_et, amount, "Enter amount");
                }
            }
        });

    }

    public class GetReport
            extends AsyncTask<Void, Void, JSONObject>
    {
        public String from_date,to_date,card_no;

        public GetReport(String from_date, String to_date, String card_no) {
            this.from_date = from_date;
            this.to_date = to_date;
            this.card_no = card_no;
        }

        protected JSONObject doInBackground(Void... paramVarArgs)
        {
            JSONObject resultJSON = null;
            try
            {
                String urlParams = setUrlParameters(from_date,to_date,card_no);
                resultJSON = new JSONObject(JSONParser.getInstance().excutePost(TruckApp.tollCardUsageReportURL, urlParams));
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
                            reportIntent.putExtra("cardNo",card_no);
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

        protected String setUrlParameters(String from_date, String to_date, String card_no)
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
                localStringBuilder.append("&card_no=").append(URLEncoder.encode(card_no, "UTF-8"));
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
                resultJSON = new JSONObject(JSONParser.getInstance().excutePost(TruckApp.tollCard2CardTransferURL, urlParams));
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
                        TruckApp.checkDialog(card2cardRechargeDialog);
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
                localStringBuilder.append("&card_username=").append(URLEncoder.encode(decrypt(sharedPreferences.getString(TOLL_USERNAME_KEY, "NoUsername")), "UTF-8"));
                localStringBuilder.append("&card_password=").append(URLEncoder.encode(decrypt(sharedPreferences.getString(TOLL_PASSWORD_KEY, "NoPassword")), "UTF-8"));
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


    public class CUGCardTransfer
            extends AsyncTask<Void, Void, JSONObject>
    {
        public String amount;
        public String to_card_no;

        public CUGCardTransfer(String paramString1, String paramString2)
        {
            this.to_card_no = paramString1;
            this.amount = paramString2;
        }

        protected JSONObject doInBackground(Void... paramVarArgs)
        {
            JSONObject resultJSON = null;
            try
            {
                String urlParams = setUrlParameters(this.to_card_no, this.amount);
                resultJSON = new JSONObject(JSONParser.getInstance().excutePost(TruckApp.tollCardCUGTransferURL, urlParams));
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
                        TruckApp.checkDialog(cugRechargeDialog);
                        Toast.makeText(mContext, "Successfully transfer", Toast.LENGTH_LONG).show();
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
            pDialog.setMessage("Transfering amount from CUG to card");
            pDialog.show();
        }

        protected String setUrlParameters(String paramString1, String paramString2)
        {
            StringBuilder localStringBuilder = new StringBuilder();
            try
            {
                localStringBuilder.append("uid=").append(URLEncoder.encode(sharedPreferences.getString("uid", ""), "UTF-8"));
                localStringBuilder.append("&access_token=").append(URLEncoder.encode(sharedPreferences.getString("access_token", ""), "UTF-8"));
                localStringBuilder.append("&accountID=").append(URLEncoder.encode(sharedPreferences.getString("accountID", "no account id"), "UTF-8"));
                localStringBuilder.append("&card_username=").append(URLEncoder.encode(decrypt(sharedPreferences.getString(TOLL_USERNAME_KEY, "NoUsername")), "UTF-8"));
                localStringBuilder.append("&card_password=").append(URLEncoder.encode(decrypt(sharedPreferences.getString(TOLL_PASSWORD_KEY, "NoPassword")), "UTF-8"));
                localStringBuilder.append("&to_card_no=").append(URLEncoder.encode(paramString1, "UTF-8"));
                localStringBuilder.append("&amount=").append(URLEncoder.encode(paramString2, "UTF-8"));
                paramString1 = localStringBuilder.toString();
                return paramString1;
            }
            catch (UnsupportedEncodingException exc)
            {
                exc.printStackTrace();
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
                showSettingsDialog("Toll Gate");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSettingsDialog(final String title) {
        generalSettingsDialog = new Dialog(TollActivity.this,
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
        TruckApp.getInstance().trackScreenView("TollGate Screen");
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
}
