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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity implements GetTrucksInterface,DevicesAdapter.ClickedPosition{

    private EditText groupName_et,contactName_et,contactPhone_et,password_et,confirm_password_et;
    private TextView devicesSpn;
    private Button   createGroupBtn;
    private DevicesAdapter devicesAdapter;
    private JSONArray devicesArray;
    private List<String> selectedDevices;
    private ImageView close_btn;
    private ProgressDialog pDialog;
    private RecyclerView devices_rv;
    private Context mContext;
    private ConnectionDetector detectCnnection;
    private SharedPreferences sharedPreferences;
    private String groupID="";
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        /*setupActionBar();*/
        mContext = this;
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        detectCnnection = new ConnectionDetector(mContext);
        groupName_et = (EditText)findViewById(R.id.group_name_et);
        devicesSpn = (TextView) findViewById(R.id.select_veh_spn);
        contactName_et = (EditText)findViewById(R.id.contact_name_et);
        contactPhone_et = (EditText)findViewById(R.id.contact_phone_et);
        password_et = (EditText)findViewById(R.id.password_et);
        confirm_password_et = (EditText)findViewById(R.id.cnf_password_et);
        createGroupBtn = (Button)findViewById(R.id.submit_btn);
        pDialog  = new ProgressDialog(this);
        devicesArray = new JSONArray();
        selectedDevices = new ArrayList<>();
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(false);

        adView = (AdView)findViewById(R.id.adView);
        //intializeBannerAd();


        if(getIntent().hasExtra(Constants.GROUP_ID)){
            groupID = getIntent().getStringExtra(Constants.GROUP_ID);
            setViews();
            createGroupBtn.setText("Update");
            setupActionBar();
        }
        devicesSpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(devicesArray.length()>0){
                    showDevices();
                }
            }
        });
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(detectCnnection.isConnectingToInternet()){
                    createGroup();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
        if(detectCnnection.isConnectingToInternet()){
            new GetDevices(CreateGroupActivity.this,sharedPreferences.getString("accountID",
                    "no accountid "),mContext,CreateGroupActivity.this).execute();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setViews(){
        groupName_et.setText(getIntent().getStringExtra(Constants.GROUP_NAME));
        contactName_et.setText(getIntent().getStringExtra(Constants.CONTACT_NAME));
        contactPhone_et.setText(getIntent().getStringExtra(Constants.CONTACT_PHONE));
        password_et.setText(getIntent().getStringExtra(Constants.PASSWORD));
        confirm_password_et.setText(getIntent().getStringExtra(Constants.PASSWORD));
    }

    private void createGroup() {
        String groupName = groupName_et.getText().toString().trim();
        String selectedDevices = devicesSpn.getText().toString().trim();
        String contactName = contactName_et.getText().toString().trim();
        String contactPhone = contactPhone_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();
        String confirmPassword = confirm_password_et.getText().toString().trim();
        if(!groupName.isEmpty() && !contactName.isEmpty() &&  !contactPhone.isEmpty() && !password.isEmpty()
                && !confirmPassword.isEmpty() && password.equals(confirmPassword)){
            new SetGroup(sharedPreferences.getString("accountID",
                    "no accountid "),groupName,contactName,contactPhone,password,selectedDevices,mContext).execute();
        }else{
            TruckApp.editTextValidation(groupName_et, groupName, getResources().getString(R.string.group_name_err));
            TruckApp.editTextValidation(contactName_et, contactName, getResources().getString(R.string.contact_name_err));
            TruckApp.editTextValidation(contactPhone_et, contactPhone, getResources().getString(R.string.contact_phone_err));
            TruckApp.editTextValidation(password_et, password, getResources().getString(R.string.password_err));
            TruckApp.editTextValidation(confirm_password_et, confirmPassword, getResources().getString(R.string.cnf_password_err));
            if(!password.isEmpty() && !confirmPassword.isEmpty() && !password.equals(confirmPassword)){
                TruckApp.editTextValidation(confirm_password_et, "", getResources().getString(R.string.password_cnf_err));
            }
        }
    }




    private void showDevices(){
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.select_devices);
        devices_rv = (RecyclerView)dialog.findViewById(R.id.devices_rv);
        devicesAdapter = new DevicesAdapter(devicesArray,CreateGroupActivity.this,selectedDevices,CreateGroupActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        devices_rv.setLayoutManager(mLayoutManager);
        devices_rv.setItemAnimator(new DefaultItemAnimator());
        devices_rv.setAdapter(devicesAdapter);
        close_btn = (ImageView) dialog.findViewById(R.id.close_iv);
        // if button is clicked, close the custom dialog
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setupActionBar() {
        if(getActionBar()!=null) {
            getActionBar().setTitle(getString(R.string.update_group));
        }else if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.update_group));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().hasExtra(Constants.GROUP_ID)){
            TruckApp.getInstance().trackScreenView("UpdateGroup Screen");
        }else{
            TruckApp.getInstance().trackScreenView("CreateGroup Screen");
        }
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void callStarted() {
        pDialog.setMessage("Fetching devices ...");
        pDialog.show();
    }

    @Override
    public void callSuccess(JSONArray trucksArray) {
        TruckApp.checkPDialog(pDialog);
        devicesArray = trucksArray;
        if(!groupID.isEmpty()) {
            StringBuilder devicesBuilder = new StringBuilder();
            for (int i = 0; i < devicesArray.length(); i++) {
                try {
                    JSONObject deviceObject = devicesArray.getJSONObject(i);
                    if (String.valueOf(deviceObject.getInt("groupID")).equals(groupID)) {
                        if (devicesBuilder.length() > 0) {
                            devicesBuilder.append(",");
                        }
                        devicesBuilder.append(deviceObject.getString("deviceID"));
                        if (!selectedDevices.contains(deviceObject.getString("deviceID"))) {
                            selectedDevices.add(deviceObject.getString("deviceID"));
                        }
                    }
                } catch (Exception e) {

                }
            }
            devicesSpn.setText(devicesBuilder.toString());
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
        if(devicesAdapter!=null && devicesArray.length()!=0){
            if(selectedDevices.contains(deviceID)){
                selectedDevices.remove(deviceID);
            }else{
                selectedDevices.add(deviceID);
            }
            devicesAdapter.swap(devicesArray,selectedDevices);
            setSelectedDevices();
        }
    }

    public void setSelectedDevices(){
        StringBuilder stringBuilder = new StringBuilder();
        if (selectedDevices.size()>0){
            for (String s:selectedDevices){
                if(stringBuilder.length()!=0){
                    stringBuilder.append(",");
                }
                stringBuilder.append(s);
            }
        }
        devicesSpn.setText(stringBuilder.toString());
    }

    public class SetGroup extends AsyncTask<Void,Void,JSONObject> {

        private String accountid,displayName,contactName,contactPhone,password,deviceID;
        private Context mContex;
        private JSONParser parser;
        public SetGroup(String accountid, String displayName, String contactName, String contactPhone, String password, String deviceID, Context mContex) {
            this.accountid = accountid;
            this.displayName = displayName;
            this.contactName = contactName;
            this.contactPhone = contactPhone;
            this.password = password;
            this.deviceID = deviceID;
            this.mContex = mContex;
            parser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(groupID.isEmpty())
                pDialog.setMessage("Creating the group ...");
            else
                pDialog.setMessage("Updating the group ...");

            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject json = null;
            try {
                String stringRequest = setUrlParameters(accountid,displayName,contactName,contactPhone,password,deviceID);
                String url="";
                if(groupID.isEmpty()){
                    url=TruckApp.setGroup;
                }else {
                    url=TruckApp.assignDevicesURL;
                }
                String res = parser.excutePost(url,stringRequest);
                json = new JSONObject(res);
            }catch(Exception e){
                Log.e("Login DoIN EX",e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            TruckApp.checkPDialog(pDialog);
            if(result!=null){
                try {
                    if (0 == result.getInt("status")) {
                        Toast.makeText(mContext,groupID.isEmpty()?"Failed to create the group":"Failed to update the group",Toast.LENGTH_LONG).show();
                    }else if (2 == result.getInt("status")) {
                        //TruckApp.logoutAction(CreateGroupActivity.this);
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
                    }else if (1 == result.getInt("status")){
                        if(groupID.isEmpty())
                            Toast.makeText(mContext,result.getString("msg"),Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(mContext,"Successfully updated the group",Toast.LENGTH_LONG).show();
                        finish();
                    }else if(-1 == result.getInt("status")){
                        Toast.makeText(mContext, "User Already Exist.Contact Phone should be unique!",Toast.LENGTH_LONG).show();
                        finish();
                    }
                }catch (Exception e){
                    Toast.makeText(mContext,groupID.isEmpty()?"Failed to create the group":"Failed to update the group",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(mContext,groupID.isEmpty()?"Failed to create the group":"Failed to update the group",Toast.LENGTH_LONG).show();
            }
        }
    }

    protected String setUrlParameters(String accountid, String displayName, String contactName, String contactPhone,
                                      String password, String deviceID){

        StringBuilder builder= new StringBuilder();
        try {
            if(groupID.length()!=0){
                builder.append("groupID=").append(URLEncoder.encode(groupID, "UTF-8")).append("&");
            }
            builder.append("accountid=").append(URLEncoder.encode(accountid, "UTF-8"));
            builder.append("&displayName=").append(URLEncoder.encode(displayName, "UTF-8"));
            builder.append("&contactName=").append(URLEncoder.encode(contactName, "UTF-8"));
            builder.append("&contactPhone=").append(URLEncoder.encode(contactPhone, "UTF-8"));
            builder.append("&password=").append(URLEncoder.encode(password, "UTF-8")).append(convertString2Array(deviceID));
           /* builder.append("&deviceID=").append(*//*URLEncoder.encode(*//*convertString2Array(deviceID)*//*, "UTF-8")*//*);
            builder.append("&deviceID=").append(*//*URLEncoder.encode(*//*convertString2Array(deviceID)*//*, "UTF-8")*//*);
*/
            builder.append("&access_token=").
                    append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public String convertString2Array(String deviceID){
        String[] deviceIDs= deviceID.split(",");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0;i<deviceIDs.length;i++){
            stringBuilder.append("&").append("deviceID[").append(i).append("]=").append(deviceIDs[i]);
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_group_menu, menu);
        return true;
    }
}
