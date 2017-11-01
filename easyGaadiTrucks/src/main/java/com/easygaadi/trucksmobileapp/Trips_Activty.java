package com.easygaadi.trucksmobileapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
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

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.TruckVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class Trips_Activty extends AppCompatActivity  {


    String[] payment = { "Payment Type","Cheque", "Chash"  };
    String truckID="",registrationNo="",DriverID="",paymentType="",PartyID="";
    TextView tripDate,trip_lbl,trip_frmdatelbl,trip_todatelbl,trip_trunklbl, trip_bkloadlbl,
            trip_drnmelbl, trip_pymtbl,trip_diesalamtlbl,trip_tollgateamtlbl,trip_tonnagelbl,trip_ratelbl,trip_frghtbl,trip_advbalbl,trip_balbl,trip_remrksl;
    EditText tripFromDateET,trip_loadET,tripToDateET,trip_diesalamtET,trip_tollgateamtET,trip_tonnageamtET,trip_rateET,frghtET,AdvnceET,BalnceET,erp_remarkET;
    Button formLL;
     Spinner spin,drspin,bookspin;
    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    String lookuup;
    ArrayList<TruckVo> datat,datad,datap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips__activty);

        trip_lbl =(TextView)findViewById(R.id.trip_lbl);
        trip_trunklbl=(TextView)findViewById(R.id.trip_trunklbl);
        trip_frmdatelbl=(TextView)findViewById(R.id.trip_frmdatelbl);
        trip_todatelbl=(TextView)findViewById(R.id.trip_todatelbl);

        trip_bkloadlbl=(TextView)findViewById(R.id.trip_bkloadlbl);
        trip_drnmelbl=(TextView)findViewById(R.id.trip_drnmelbl);
        trip_diesalamtlbl=(TextView)findViewById(R.id.trip_diesalamtlbl);
        trip_tonnagelbl=(TextView)findViewById(R.id.trip_tonnagelbl);
        trip_ratelbl=(TextView)findViewById(R.id.trip_ratelbl);
        trip_tollgateamtlbl=(TextView)findViewById(R.id.trip_tollgateamtlbl);
        trip_frghtbl=(TextView)findViewById(R.id.trip_frghtbl);
        trip_advbalbl=(TextView)findViewById(R.id.trip_advbalbl);
        trip_balbl=(TextView)findViewById(R.id.trip_balbl);
        trip_pymtbl=(TextView)findViewById(R.id.trip_pymtbl);
        trip_remrksl=(TextView)findViewById(R.id.trip_remrksl);
        initilizationView();


        context = Trips_Activty.this;
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        detectCnnection = new ConnectionDetector(context);

         spin = (Spinner) findViewById(R.id.spnr_trunknum);
        drspin = (Spinner) findViewById(R.id.spnr_drivername);
        bookspin = (Spinner) findViewById(R.id.spnr_book);
        datat = new ArrayList<TruckVo>();
        datad = new ArrayList<TruckVo>();
        datap = new ArrayList<TruckVo>();
        TruckVo voDatas = new TruckVo();
        voDatas.set_id("");
        voDatas.setRegistrationNo("Assigned Truck");
        datat.add(voDatas);
        TruckVo voDatasq = new TruckVo();
        voDatasq.set_id("");
        voDatasq.setRegistrationNo("Assigned Driver");




        datad.add(voDatasq);

        TruckVo voDatasp = new TruckVo();
        voDatasp.set_id("");
        voDatasp.setRegistrationNo("Assigned Party");
        datap.add(voDatasp);

        lookuup = getIntent().getStringExtra("hitupdate");
        if (detectCnnection.isConnectingToInternet()) {
            new GetBuyingTrucks("truck").execute();
        } else {
            Toast.makeText(context,res.getString(R.string.internet_str),Toast.LENGTH_LONG).show();
        }

        Spinner payspin = (Spinner) findViewById(R.id.spnr_paymnttype);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter paytypeeaa = new ArrayAdapter(this,R.layout.erp_view_spinner_item,payment);
        paytypeeaa.setDropDownViewResource(R.layout.erp_view_spinner_item);
        //Setting the ArrayAdapter data on the Spinner
        payspin.setAdapter(paytypeeaa);

        tripDate = (TextView)findViewById(R.id.trip_id);


        tripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(tripDate);
            }
        });




        AdapterView.OnItemSelectedListener countrySelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,int position, long id) {
                if(spinner.getId() == R.id.spnr_paymnttype){
                    String selected = spinner.getItemAtPosition(position).toString();
                    if(selected.equalsIgnoreCase("Payment Type"))
                    {
                        trip_pymtbl.setVisibility(View.INVISIBLE);

                    }else{
                        trip_pymtbl.setVisibility(View.VISIBLE);
                    }
                    if(position !=0)
                    {
                        paymentType=spinner.getItemAtPosition(position).toString();
                    }else {
                        paymentType = "";
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

                if(R.id.spnr_paymnttype == arg0.getId()) {
                    if (arg0.getSelectedItemPosition() != 0) {
                        trip_pymtbl.setVisibility(View.VISIBLE);
                    }
                }

            }
        };

        payspin.setOnItemSelectedListener(countrySelectedListener);
    }



    public void initilizationView(){
        //frghtET,AdvnceET,BalnceET
        //erp_frghtamt,erp_advamt,erp_balamt
        tripFromDateET = (EditText)findViewById(R.id.trip_frm_date);
        tripToDateET = (EditText)findViewById(R.id.trip_to_date);
        trip_loadET = (EditText)findViewById(R.id.trip_load);
        trip_diesalamtET = (EditText)findViewById(R.id.trip_diesalamt);
        trip_tollgateamtET = (EditText)findViewById(R.id.trip_tollgateamt);
        trip_tonnageamtET = (EditText)findViewById(R.id.trip_tonnageamt);
        trip_rateET = (EditText)findViewById(R.id.trip_rate);
        frghtET = (EditText)findViewById(R.id.erp_frghtamt);
        AdvnceET = (EditText)findViewById(R.id.erp_advamt);
        BalnceET = (EditText)findViewById(R.id.erp_balamt);
        erp_remarkET = (EditText)findViewById(R.id.erp_remark);
        chnageTextView(trip_diesalamtET);
        chnageTextView(trip_tollgateamtET);
        chnageTextView(trip_tonnageamtET);
        chnageTextView(trip_rateET);
        chnageTextView(frghtET);
        chnageTextView(AdvnceET);
        chnageTextView(BalnceET);
        chnageTextView(erp_remarkET);

        formLL = (Button)findViewById(R.id.clr_btn);
        formLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup group = (ViewGroup)findViewById(R.id.formLL);
                clearForm(group);

                if (Build.VERSION.SDK_INT >= 11) {
                    recreate();
                } else {
                    Intent intent = getIntent();
                    intent.replaceExtras(new Bundle());
                    getIntent().setAction("");
                    getIntent().setData(null);
                    getIntent().setFlags(0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);

                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });



    }


    public void AddTrip(View view)
    {
        //tripToDateET,trip_diesalamtET,trip_tollgateamtET,trip_tonnageamtET,trip_rateET,frghtET,AdvnceET,BalnceET,erp_remarkET;
        String tripDatestr =  tripDate.getText().toString().trim();
        String tripTruckID =  truckID.toString().trim();
        String tripFrom =  tripFromDateET.getText().toString().trim();
        String tripTo =  tripToDateET.getText().toString().trim();
        String trip_load =  trip_loadET.getText().toString().trim();
        String tripDriverID =  DriverID.toString().trim();
        String tripDieselAmt =  trip_diesalamtET.getText().toString().trim();
        String tripTollAmt =  trip_tollgateamtET.getText().toString().trim();
        String tripTonnageAmt =  trip_tonnageamtET.getText().toString().trim();
        String tripRateAmt =  trip_rateET.getText().toString().trim();
        String tripfrghtAmt =  frghtET.getText().toString().trim();
        String tripAdvnceAmt =  AdvnceET.getText().toString().trim();
        String tripBalnceAmt =  BalnceET.getText().toString().trim();
        String triperp_remark =  erp_remarkET.getText().toString().trim();
        String trippaymentType =  paymentType.toString().trim();
        String tripPartBook =  PartyID.toString().trim();

        if(tripDatestr.contains("-")){
            if(tripTruckID.length()>0){
                if(tripFrom.length()>0){
                    if(tripTo.length()>0){
                        if(trip_load.length()>0){
                            if(tripDriverID.length()>0){
                                if(tripDieselAmt.length()>0){
                                    if(tripTollAmt.length()>0){
                                        if(tripTonnageAmt.length()>0){
                                            if(tripRateAmt.length()>0){
                                                if(tripfrghtAmt.length()>0){
                                                    if(tripAdvnceAmt.length()>0){
                                                        if(tripBalnceAmt.length()>0){
                                                            if(trippaymentType.length()>0){
                                                                if (detectCnnection.isConnectingToInternet()) {
                                                                    new AddTrip( tripDatestr ,tripTruckID,tripFrom ,tripTo ,trip_load ,tripDriverID,tripDieselAmt,tripTollAmt ,tripTonnageAmt,tripRateAmt,tripfrghtAmt ,tripAdvnceAmt,
                                                                            tripBalnceAmt,triperp_remark,trippaymentType,tripPartBook).execute();
                                                                } else {
                                                                    Toast.makeText(context,res.getString(R.string.internet_str),Toast.LENGTH_LONG).show();
                                                                }
                                                            }else
                                                            {
                                                                Toast.makeText(context, "Please Select Payment Type ", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }else
                                                        {
                                                            Toast.makeText(context, "Please Enter Balance Amount ", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }else
                                                    {
                                                        Toast.makeText(context, "Please Enter Advnce Amount ", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else
                                                {
                                                    Toast.makeText(context, "Please Enter Frieght Amount ", Toast.LENGTH_SHORT).show();
                                                }

                                            }else
                                            {
                                                Toast.makeText(context, "Please Enter Rate ", Toast.LENGTH_SHORT).show();
                                            }

                                        }else
                                        {
                                            Toast.makeText(context, "Please Enter Tonnage ", Toast.LENGTH_SHORT).show();
                                        }

                                    }else
                                    {
                                        Toast.makeText(context, "Please Enter Toll Amount", Toast.LENGTH_SHORT).show();
                                    }
                                }else
                                {
                                    Toast.makeText(context, "Please Enter Diesel Amount", Toast.LENGTH_SHORT).show();
                                }
                            }else
                            {
                                Toast.makeText(context, "Please Select Driver for Truck", Toast.LENGTH_SHORT).show();
                            }

                        }else
                        {
                            Toast.makeText(context, "Please Enter Load for Truck", Toast.LENGTH_SHORT).show();
                        }

                    }else
                    {
                        Toast.makeText(context, "Please Enter Destination Location", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Toast.makeText(context, "Please Enter Source Location", Toast.LENGTH_SHORT).show();
                }
            }else
            {
                Toast.makeText(context, "Please Select Truck Number", Toast.LENGTH_SHORT).show();
            }
        }else
        {
            Toast.makeText(context, "Please Enter Date", Toast.LENGTH_SHORT).show();
        }




    }

    public void chnageTextView(final EditText etview){

        etview.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if(R.id.trip_diesalamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_diesalamtlbl.setVisibility(View.VISIBLE);
                        slideUp(trip_diesalamtlbl);
                    }else{
                        trip_diesalamtlbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.trip_tollgateamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_tollgateamtlbl.setVisibility(View.VISIBLE);
                        slideUp(trip_tollgateamtlbl);
                    }else{
                        trip_tollgateamtlbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.trip_tonnageamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_tonnagelbl.setVisibility(View.VISIBLE);
                        slideUp(trip_tonnagelbl);
                    }else{
                        trip_tonnagelbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.trip_rate == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_ratelbl.setVisibility(View.VISIBLE);
                        slideUp(trip_ratelbl);
                    }else{
                        trip_ratelbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.erp_frghtamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_frghtbl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_frghtbl)));
                    }else{
                        trip_frghtbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.erp_advamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_advbalbl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_advbalbl)));
                    }else{
                        trip_advbalbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.erp_balamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_balbl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_balbl)));
                    }else{
                        trip_balbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.erp_remark == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_remrksl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_remrksl)));
                    }else{
                        trip_remrksl.setVisibility(View.INVISIBLE);
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


    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(Trips_Activty.this,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                int tempmonth = view.getMonth()+1;
                String temp = ""+tempmonth;
                if(temp.length()>2){
                    temp ="0"+temp;
                }
                if(Tview.getId() == R.id.trip_id){
                    tripDate.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(tripDate.getText().toString().length()>0){
                        trip_lbl.setVisibility(View.VISIBLE);
                        slideUp(trip_lbl);
                    }else {
                        trip_lbl.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }, year, month, day);


        dpd.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    if(Tview.getId() == R.id.trip_id){
                        if(tripDate.getText().toString().length()>0){
                            trip_lbl.setVisibility(View.VISIBLE);
                        }else {
                            trip_lbl.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });

        dpd.show();


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




    private void clearForm(ViewGroup group)
    {

        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }else if (view instanceof TextView) {
                ((TextView)view).setText("");
            }else if (view instanceof Spinner) {
                ((Spinner)view).setSelection(0);
            }


            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }
    }

    private class GetBuyingTrucks extends AsyncTask<String, String, JSONObject> {

       String type;

        public GetBuyingTrucks(String type) {
            this.type = type;
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
                String res ="";
                if(type.equalsIgnoreCase("truck"))
                {
                    res = parser.erpExecuteGet(context,TruckApp.truckListURL);
                    Log.e("truckListURL",res.toString());
                }else if(type.equalsIgnoreCase("drivers")){
                    res = parser.erpExecuteGet(context,TruckApp.driverListURL);
                    Log.e("driverListURL",res.toString());
                }else if(type.equalsIgnoreCase("parties")){
                    res = parser.erpExecuteGet(context,TruckApp.payListURL);
                    Log.e("payListURL",res.toString());
                }
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
                        if(this.type.equalsIgnoreCase("drivers")){
                            JSONArray partArray = result.getJSONArray("drivers");
                            if(partArray.length() > 0)
                            {
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    TruckVo voData = new TruckVo();
                                    voData.set_id(partData.getString("_id"));
                                    voData.setRegistrationNo(""+partData.getString("fullName"));
                                    datad.add(voData);
                                }
                                SpinnerCustomAdapters customAdapter=new SpinnerCustomAdapters(getApplicationContext(),datad);
                                drspin.setAdapter(customAdapter);
                                pDialog.dismiss();
                                new GetBuyingTrucks("parties").execute();
                            }else{
                                pDialog.dismiss();

                            }
                        }else if(this.type.equalsIgnoreCase("truck")){
                            JSONArray partArray = result.getJSONArray("trucks");
                            if(partArray.length() > 0)
                            {
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    TruckVo voData = new TruckVo();
                                    voData.set_id(partData.getString("_id"));
                                    voData.setRegistrationNo(""+partData.getString("registrationNo"));

                                    datat.add(voData);
                                }
                                pDialog.dismiss();
                            }else{
                                pDialog.dismiss();
                            }
                            new GetBuyingTrucks("drivers").execute();
                            SpinnerCustomAdapter customAdapter=new SpinnerCustomAdapter(getApplicationContext(),datat);
                            spin.setAdapter(customAdapter);
                        }else if(this.type.equalsIgnoreCase("parties")){

                            JSONArray partArray = result.getJSONArray("parties");
                            if(partArray.length() > 0)
                            {
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    TruckVo voData = new TruckVo();
                                    voData.set_id(partData.getString("_id"));
                                    voData.setRegistrationNo(""+partData.getString("name"));

                                    datap.add(voData);
                                }
                                pDialog.dismiss();
                            }else{
                                pDialog.dismiss();
                            }

                            PartySpinnerCustomAdapters customAdapter=new PartySpinnerCustomAdapters(getApplicationContext(),datap);
                            bookspin.setAdapter(customAdapter);
                        }

                    }



                } catch (Exception e) {
                    System.out.println("ex in truck leads" + e.toString());
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

            if(i == 0){
                truckID = "";
                registrationNo = "";
            }else{
                truckID  = book.get_id();
                registrationNo = book.getRegistrationNo();
            }
            return bookRow;
        }
    }


    public class SpinnerCustomAdapters extends BaseAdapter {
        Context mcontext;
        ArrayList<TruckVo> dataset;
        LayoutInflater inflter;

        public SpinnerCustomAdapters(Context applicationContext, ArrayList<TruckVo> dataset) {
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

            if(i == 0){
                DriverID = "";
            }else{
                DriverID  = book.get_id();
            }
            return bookRow;
        }
    }


    public class PartySpinnerCustomAdapters extends BaseAdapter {
        Context mcontext;
        ArrayList<TruckVo> dataset;
        LayoutInflater inflter;

        public PartySpinnerCustomAdapters(Context applicationContext, ArrayList<TruckVo> dataset) {
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

            if(i == 0){
                PartyID = "";
            }else{
                PartyID  = book.get_id();
            }
            return bookRow;
        }
    }

    private class AddTrip extends AsyncTask<String, String, JSONObject> {
        String tripDatestr ,tripTruckID,tripFrom ,tripTo ,trip_load ,tripDriverID,tripDieselAmt,tripTollAmt ,tripTonnageAmt,tripRateAmt,tripfrghtAmt ,tripAdvnceAmt,
                tripBalnceAmt,triperp_remark,trippaymentType,tripPartBook;

        public AddTrip(String tripDatestr ,String tripTruckID,String tripFrom ,String tripTo ,String trip_load ,String tripDriverID,String tripDieselAmt,String tripTollAmt ,String tripTonnageAmt,String
                tripRateAmt,String tripfrghtAmt ,String tripAdvnceAmt,
                       String tripBalnceAmt,String triperp_remark,String  trippaymentType,String tripPartBook) {
            this.tripDatestr = tripDatestr;
            this.tripTruckID =  tripTruckID;
            this.tripFrom =  tripFrom;
            this.tripTo = tripTo;
            this.trip_load =  trip_load;
            this.tripDriverID =tripDriverID ;
            this.tripDieselAmt = tripDieselAmt;
            this.tripTollAmt = tripTollAmt;
            this.tripTonnageAmt = tripTonnageAmt;
            this.tripRateAmt = tripRateAmt ;
            this.tripfrghtAmt = tripfrghtAmt;
            this.tripAdvnceAmt = tripAdvnceAmt;
            this.tripBalnceAmt = tripBalnceAmt;
            this.triperp_remark = triperp_remark;
            this.trippaymentType = trippaymentType;
            this.tripPartBook = tripPartBook;

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
                //truckreg, truckType,truck_model, truck_tonnage, truckfitness,truckInsura,truckPermit,truckPoll,trucktax
                try {
                    post_dict.put("date" , tripDatestr);
                    post_dict.put("driver",DriverID);
                    post_dict.put("registrationNo",tripTruckID);
                    post_dict.put("freightAmount", Integer.parseInt(tripfrghtAmt));
                    post_dict.put("bookedFor", PartyID);
                    post_dict.put("advance", Integer.parseInt(tripAdvnceAmt));
                    post_dict.put("bookLoad", Integer.parseInt(trip_load));
                    post_dict.put("dieselAmount", Integer.parseInt(tripDieselAmt));
                    post_dict.put("tollgateAmount", Integer.parseInt(tripTollAmt));
                    post_dict.put("from", tripFrom);
                    post_dict.put("to", tripTo);
                    post_dict.put("tonnage", Integer.parseInt(tripTonnageAmt));
                    post_dict.put("rate", Integer.parseInt(tripRateAmt));
                    post_dict.put("balance", Integer.parseInt(tripBalnceAmt));
                    post_dict.put("paymentType", trippaymentType);
                    post_dict.put("remarks", triperp_remark);
                    post_dict.put("bookedFor", PartyID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("" + String.valueOf(post_dict));
                String result = parser.easyyExcutePost(context,TruckApp.tripsListURL+"/addTrip ",String.valueOf(post_dict));
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

                        Toast.makeText(context,"fail",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, ""+"Trips Added", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent();
                        intent.putExtra("addItem","");
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

}

