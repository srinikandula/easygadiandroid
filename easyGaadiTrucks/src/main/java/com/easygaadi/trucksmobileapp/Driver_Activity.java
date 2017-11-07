package com.easygaadi.trucksmobileapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.erp.PartList;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.PartyVo;
import com.easygaadi.models.TruckVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Driver_Activity extends AppCompatActivity {

    EditText driverFnameET,drivermobET,driverlicnumET,driverSalET;
    String truckAssigned="",truckID="";
    TextView trip_fnamelbl,maintnce_trunknum_lbl,driver_moblbl,driver_licnumberlbl,trip_dojlbl,trip_liclbl,drvr_sal,drvr_doj,drvr_lic_date;

     Spinner spin;
    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    ArrayList<TruckVo> data;
    String lookuup="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_);
        initilizationView();
        context = Driver_Activity.this;
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        data = new ArrayList<TruckVo>();
        detectCnnection = new ConnectionDetector(context);
        trip_fnamelbl = (TextView)findViewById(R.id.trip_fnamelbl);
        driver_moblbl = (TextView)findViewById(R.id.driver_moblbl);
        driver_licnumberlbl = (TextView)findViewById(R.id.driver_licnumberlbl);
        maintnce_trunknum_lbl = (TextView)findViewById(R.id.maintnce_trunknum_lbl);
        trip_dojlbl = (TextView)findViewById(R.id.trip_dojlbl);
        trip_liclbl = (TextView)findViewById(R.id.trip_liclbl);
        drvr_sal = (TextView)findViewById(R.id.drvr_sal);
        drvr_doj = (TextView)findViewById(R.id.drvr_doj);
        drvr_lic_date = (TextView)findViewById(R.id.drvr_lic_date);
        drvr_doj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(drvr_doj);
            }
        });
        drvr_lic_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(drvr_lic_date);
            }
        });


        spin = (Spinner) findViewById(R.id.spnr_trunknum);
        //Creating the ArrayAdapter instance having the country list

        //Setting the ArrayAdapter data on the Spinner



       /* AdapterView.OnItemSelectedListener countrySelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,int position, long id) {
                if(spinner.getId() == R.id.spnr_trunknum){
                    //truckAssigned = spinner.getItemAtPosition(position).toString();
                    TruckVo truckvo = (TruckVo) spinner.getItemAtPosition(position);
                    truckAssigned = truckvo.getRegistrationNo();
                    if(truckAssigned.equalsIgnoreCase("Assigned Truck"))
                    {
                        maintnce_trunknum_lbl.setVisibility(View.INVISIBLE);
                    }else{
                        maintnce_trunknum_lbl.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

                if(spin.getId() == arg0.getId()) {
                    if (arg0.getSelectedItemPosition() != 0) {
                        maintnce_trunknum_lbl.setVisibility(View.VISIBLE);
                    }
                }

            }
        };
        spin.setOnItemSelectedListener(countrySelectedListener);*/
        lookuup = getIntent().getStringExtra("hitupdate");
        if (detectCnnection.isConnectingToInternet()) {
            new GetBuyingTrucks().execute();
        } else {
            Toast.makeText(context,
                    res.getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }

    }

    public void initilizationView() {
        //driverFnameET,drivermobET,driverSalET
        //erp_frghtamt,erp_advamt,erp_balamt
        driverFnameET = (EditText) findViewById(R.id.driver_fname);
        drivermobET = (EditText) findViewById(R.id.driver_mob);
        driverlicnumET = (EditText) findViewById(R.id.driver_licnumber);
        driverSalET = (EditText) findViewById(R.id.dvr_sal);
        chnageTextView(driverFnameET);
        chnageTextView(drivermobET);
        chnageTextView(driverlicnumET);
        chnageTextView(driverSalET);
    }




    public void chnageTextView(final EditText etview){

        etview.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if(R.id.driver_fname == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_fnamelbl.setVisibility(View.VISIBLE);
                        slideUp(trip_fnamelbl);
                    }else{
                        trip_fnamelbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.driver_mob == etview.getId()) {
                    if (string.trim().length() != 0) {
                        driver_moblbl.setVisibility(View.VISIBLE);
                        slideUp(driver_moblbl);
                    }else{
                        driver_moblbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.driver_licnumber == etview.getId()) {
                    if (string.trim().length() != 0) {
                        driver_licnumberlbl.setVisibility(View.VISIBLE);
                        slideUp(driver_licnumberlbl);
                    }else{
                        driver_licnumberlbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.dvr_sal == etview.getId()) {
                    if (string.trim().length() != 0) {
                        drvr_sal.setVisibility(View.VISIBLE);
                        slideUp(drvr_sal);
                    }else{
                        drvr_sal.setVisibility(View.INVISIBLE);
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

    public void submit(View view){
        //startActivity(new Intent(Driver_Activity.this,Party_Activity.class));

        String driverName = driverFnameET.getText().toString().trim();
        String drivertrunl = truckAssigned;
        String drivermobile = drivermobET.getText().toString().trim();
        String driverLicNum = driverlicnumET.getText().toString().trim();
        String driverDOJ = drvr_doj.getText().toString().trim();
        String driverlicval = drvr_lic_date.getText().toString().trim();
        String driversal = driverSalET.getText().toString().trim();

        if(driverName.length()>0){
            if(spin.getSelectedItemPosition() > 0){
                if(drivermobile.length() == 10){
                    if(driverLicNum.length() > 0){
                        if(driverDOJ.contains("-")) {
                            if(driverlicval.contains("-")) {
                                if(driversal.length() > 1) {
                                    if (detectCnnection.isConnectingToInternet()) {
                                        new AddDriver(driverName, drivertrunl,drivermobile, driverLicNum, driverDOJ,driverlicval,driversal).execute();
                                    } else {
                                        Toast.makeText(context,res.getString(R.string.internet_str),Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(context, "Please Enter Salary of Driver ", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(context, "Please Select License expiry Date ", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(context, "Please Select Date of Joining", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(context, "Please Enter License Number", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Please Enter Party Name", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "Please Asigned Truck", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Please Enter Party Name", Toast.LENGTH_SHORT).show();
        }

    }



    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(Driver_Activity.this,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //(view).setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                if(Tview.getId() == R.id.drvr_doj){

                    drvr_doj.setText(view.getYear()+"-"+view.getMonth()+"-"+view.getDayOfMonth());
                    if(drvr_doj.getText().toString().length()>0){
                        trip_dojlbl.setVisibility(View.VISIBLE);
                        slideUp(trip_dojlbl);
                    }else {
                        trip_dojlbl.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.drvr_lic_date)
                {
                    drvr_lic_date.setText(view.getYear()+"-"+view.getMonth()+"-"+view.getDayOfMonth());
                    if(drvr_lic_date.getText().toString().length()>0){
                        trip_liclbl.setVisibility(View.VISIBLE);
                        slideUp(trip_liclbl);
                    }else {
                        trip_liclbl.setVisibility(View.INVISIBLE);

                    }

                }
            }
        }, year, month, day);


        dpd.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    if(Tview.getId() == R.id.drvr_doj){
                        if(drvr_doj.getText().toString().length()>0){
                            trip_dojlbl.setVisibility(View.VISIBLE);
                        }else {
                            trip_dojlbl.setVisibility(View.INVISIBLE);
                        }
                    }else if(Tview.getId() == R.id.drvr_lic_date)
                    {
                        if(drvr_lic_date.getText().toString().length()>0){
                            trip_liclbl.setVisibility(View.VISIBLE);
                        }else {
                            trip_liclbl.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        dpd.show();
    }

    private class AddDriver extends AsyncTask<String, String, JSONObject> {
        String driverName, drivertrunl,drivermobile, driverLicNum, driverDOJ,driverlicval,driversal;

        public AddDriver(String driverName,String  drivertrunl,String  drivermobile,String  driverLicNum,String  driverDOJ,String  driverlicval,String driversal) {
            this.driverName = driverName;
            this.drivertrunl = drivertrunl;
            this.drivermobile = drivermobile;
            this.driverLicNum = driverLicNum;
            this.driverDOJ = driverDOJ;
            this.driverlicval = driverlicval;
            this.driversal = driversal;
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
                JSONObject salary = new JSONObject();
                    salary.put("value",driversal);

                try {
                    post_dict.put("fullName" , driverName);
                    post_dict.put("truckId",truckID);
                    post_dict.put("mobile", drivermobile);
                    post_dict.put("licenseNumber", driverLicNum);
                    post_dict.put("joiningDate", driverDOJ);
                    post_dict.put("licenseValidity", driverlicval);
                    post_dict.put("salary",Integer.parseInt(driversal));
                    post_dict.put("_id",lookuup);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("" + String.valueOf(post_dict));
                String result="";
                if(lookuup.length()> 0) {
                    result = parser.ERPexcutePut(context, TruckApp.driverListURL, String.valueOf(post_dict));
                    System.out.println("edit Driver Details" );
                }else
                {
                     result = parser.easyyExcutePost(context, TruckApp.driverListURL, String.valueOf(post_dict));
                    System.out.println("Add Driver Details");
                }

                res = new JSONObject(result);

            } catch (Exception e) {
                Log.e("Login DoIN EX", e.toString());
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
                        Toast.makeText(context, "Successfully added", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent();
                        if(lookuup.length()> 0) {
                            intent.putExtra("addItem",s.getJSONObject("driver").toString());
                            intent.putExtra("updated","update");
                            setResult(123,intent);
                            finish();
                        }else{
                            intent.putExtra("updated","add");
                            intent.putExtra("addItem",s.getJSONObject("driver").toString());
                            setResult(123,intent);
                            finish();
                        }


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


    private class GetBuyingTrucks extends AsyncTask<String, String, JSONObject> {

        //String uid, accountid, offset;

        public GetBuyingTrucks() {
            //this.uid = uid;
            //this.accountid = accountid;
            //this.offset = String.valueOf(offset);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Fetching Trucks Please..");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                String res = parser.erpExecuteGet(context,TruckApp.truckListURL);
                Log.e("paylist",res.toString());
                json = new JSONObject(res);

            } catch (Exception e) {
                Log.e("Login DoIN EX", e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {

                try {

                    TruckVo voDatas = new TruckVo();
                    voDatas.set_id("");
                    voDatas.setRegistrationNo("Assigned Truck");

                    data.add(voDatas);

                    if (!result.getBoolean("status")) {
                        Toast.makeText(context, "No records available",Toast.LENGTH_LONG).show();
                    }else
                    {
                        JSONArray partArray = result.getJSONArray("trucks");
                        if(partArray.length() > 0)
                        {
                            for (int i = 0; i < partArray.length(); i++) {
                                JSONObject partData = partArray.getJSONObject(i);
                                TruckVo voData = new TruckVo();
                                voData.set_id(partData.getString("_id"));
                                voData.setRegistrationNo(""+partData.getString("registrationNo"));

                                data.add(voData);
                            }
                            pDialog.dismiss();
                        }
                    }
                    SpinnerCustomAdapter customAdapter=new SpinnerCustomAdapter(getApplicationContext(),data);
                    spin.setAdapter(customAdapter);

                    if(lookuup.length()> 0)
                    {

                        new GetFreshTrucks().execute();
                    }


                } catch (Exception e) {
                    System.out.println("ex in get leads" + e.toString());
                }

            } else {
                pDialog.dismiss();
                Toast.makeText(context,
                        getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    public class SpinnerCustomAdapter extends BaseAdapter {
        Context mcontext;
        ArrayList<TruckVo> dataset;
        LayoutInflater inflter;

        public SpinnerCustomAdapter(Context applicationContext, ArrayList<TruckVo> dataset) {
            this.mcontext = applicationContext;
            this. dataset = dataset;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return dataset.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflator=LayoutInflater.from(mcontext);
            View bookRow=inflator.inflate(R.layout.erp_spinner_item, viewGroup, false);
            TextView names = (TextView) bookRow.findViewById(R.id.text1);
            TruckVo book= dataset.get(i);
            names.setText(book.getRegistrationNo());
            truckID  = book.get_id();
            return bookRow;
        }
    }


    private class GetFreshTrucks extends AsyncTask<String, String, JSONObject> {

        //String uid, accountid, offset;

        public GetFreshTrucks() {
            //this.uid = uid;
            //this.accountid = accountid;
            //this.offset = String.valueOf(offset);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Fetching Trucks Please..");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                String res = parser.erpExecuteGet(context,TruckApp.driverFreshURL+""+lookuup);
                Log.e("paylist",res.toString());
                json = new JSONObject(res);

            } catch (Exception e) {
                Log.e("Login DoIN EX", e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {

                try {
                    if (!result.getBoolean("status")) {
                        Toast.makeText(context, "No records available",Toast.LENGTH_LONG).show();
                    }else
                    {

                        JSONObject partData = result.getJSONObject("driver");
                        // driverFnameET,drivermobET,driverlicnumET,driverSalET

                        driverFnameET.setText(partData.getString("fullName"));
                        drivermobET.setText(partData.getString("mobile"));
                        driverlicnumET.setText(partData.getString("licenseNumber"));
                        driverSalET.setText("");
                        //drvr_doj.setText(getDate(partData.getString("licenseNumber")));
                        drvr_doj.setText("");
                        drvr_lic_date.setText(getDate(partData.getString("licenseValidity")));


                        for (int i = 0; i < data.size(); i++) {
                            TruckVo vo = data.get(i);
                            System.out.println(vo.get_id()+"riyaz"+partData.getString("truckId"));
                            if(vo.get_id().contentEquals(partData.getString("truckId"))){
                                spin.setSelection(i);
                                break;
                            }
                        }
                        pDialog.dismiss();

                    }


                } catch (Exception e) {
                    System.out.println("ex GetFreshTrucks get leads" + e.toString());
                }

            } else {
                pDialog.dismiss();
                Toast.makeText(context,
                        getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    private String getDate(String fdate)
    {
        Date date;
        String diff = "";
        System.out.println("getDate--"+"getDate"+fdate);
        DateFormat dateFormat,formatter;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
            date = dateFormat.parse(fdate);
            formatter = new SimpleDateFormat("yyyy-MM-dd"); //If you need time just put specific format for time like 'HH:mm:ss'
            diff = formatter.format(date);


        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("err--"+e.getMessage());
        }
        return diff;
    }

}
