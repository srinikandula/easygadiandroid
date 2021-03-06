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
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.erp.CommonERP;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
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

public class PaymentsActivity extends AppCompatActivity {
    TextView maintDatelblTV,maintnce_dateTV,maintnce_lbl,maintnce_cost_lbl,maintnce_are_lbl;
    String truckID = "",tripID = "",partyID = "";
    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    ArrayList<TruckVo> dataP;
    Spinner spinParties;
    String lookuup="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);


        context = PaymentsActivity.this;
        parser = JSONParser.getInstance();
        pDialog = CommonERP.createProgressDialog(context);//new ProgressDialog(context);
        pDialog.setCancelable(true);
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);

        dataP = new ArrayList<TruckVo>();
        detectCnnection = new ConnectionDetector(context);

        spinParties = (Spinner) findViewById(R.id.spnr_party);
        maintnce_dateTV = (TextView)findViewById(R.id.maintnce_date);
        maintDatelblTV = (TextView)findViewById(R.id.maintnce_date_lbl);
        maintnce_lbl = (TextView)findViewById(R.id.maintnce_rType_lbl);

        maintnce_cost_lbl = (TextView)findViewById(R.id.maintnce_cost_lbl);
        maintnce_are_lbl = (TextView)findViewById(R.id.maintnce_are_lbl);
        initilizationView();
        maintnce_dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(maintnce_dateTV);
            }
        });



        TruckVo voDatap = new TruckVo();
        voDatap.set_id("");
        voDatap.setRegistrationNo("Select Party");
        dataP.add(voDatap);
        lookuup = getIntent().getStringExtra("hitupdate");
        if (detectCnnection.isConnectingToInternet()) {
            new GetAllParty("party").execute();
        } else {
            Toast.makeText(context,res.getString(R.string.internet_str),Toast.LENGTH_LONG).show();
        }
    }

    EditText maintnce_costET,maintnce_areET;
    public void initilizationView() {
        //frghtET,AdvnceET,BalnceET
        //erp_frghtamt,erp_advamt,erp_balamt

        maintnce_costET = (EditText) findViewById(R.id.maintnce_cost);
        maintnce_areET = (EditText) findViewById(R.id.maintnce_are);

        chnageTextView(maintnce_costET);
        chnageTextView(maintnce_areET);
    }


    public void chnageTextView(final EditText etview){

        etview.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if(R.id.maintnce_PType == etview.getId()) {
                    if (string.trim().length() != 0) {
                        maintnce_lbl.setVisibility(View.VISIBLE);
                        slideUp(maintnce_lbl);
                    }else{
                        maintnce_lbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.maintnce_cost == etview.getId()) {
                    if (string.trim().length() != 0) {
                        maintnce_cost_lbl.setVisibility(View.VISIBLE);
                        slideUp(maintnce_cost_lbl);
                    }else{
                        maintnce_cost_lbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.maintnce_are == etview.getId()) {
                    if (string.trim().length() != 0) {
                        maintnce_are_lbl.setVisibility(View.VISIBLE);
                        slideUp(maintnce_are_lbl);
                    }else{
                        maintnce_are_lbl.setVisibility(View.INVISIBLE);
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

    public void callTruckAct(View view){
        String maintenanceDate = maintnce_dateTV.getText().toString().trim();
        String maintenanceTrucknum = truckID;

        String maintenancCost = maintnce_costET.getText().toString().trim();
        String maintenancArea = maintnce_areET.getText().toString().trim();

        if(maintenanceDate.contains("-")){
            if(true){
                if(true){
                    if(true){
                        if(maintenancCost.trim().length()>0){
                            if(true){
                                if(true){
                                    if (detectCnnection.isConnectingToInternet()) {
                                        new AddMaintenance(maintenanceDate, maintenanceTrucknum,maintenancCost,maintenancArea).execute();
                                    } else {
                                        Toast.makeText(context,res.getString(R.string.internet_str),Toast.LENGTH_LONG).show();
                                    }

                                }else{
                                    Toast.makeText(context, "Please Enter Area Name", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(context, "Please Enter Shed Name", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(context, "Please Enter Cost For Repair", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, "Please Enter Payment Type", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Please Enter Repair Description", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "Please Asign Truck", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Please Select Date", Toast.LENGTH_SHORT).show();
        }

    }
    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(PaymentsActivity.this,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //(view).setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());

                int motnh = view.getMonth() + 1;
                if(Tview.getId() == R.id.maintnce_date){
                    maintnce_dateTV.setText(view.getYear()+"-"+motnh+"-"+view.getDayOfMonth());
                    if(maintnce_dateTV.getText().toString().length()>0){
                        maintDatelblTV.setVisibility(View.VISIBLE);
                        slideUp(maintDatelblTV);
                    }else {
                        maintDatelblTV.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }, year, month, day);


        dpd.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    if(Tview.getId() == R.id.trip_id){
                        if(maintnce_dateTV.getText().toString().length()>0){
                            maintnce_dateTV.setVisibility(View.VISIBLE);
                        }else {
                            maintnce_dateTV.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
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

    private class GetAllParty extends AsyncTask<String, String, JSONObject> {

        String type;

        public GetAllParty(String type) {
            this.type = type;
            //this.accountid = accountid;
            //this.offset = String.valueOf(offset);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                String res ="";
                 if(type.equalsIgnoreCase("party")){
                    res = parser.erpExecuteGet(context,TruckApp.paryListURL);
                    Log.e("ExpensesURL",res.toString());
                }
                System.out.print("type"+res);
                json = new JSONObject(res);
            } catch (Exception e) {
                Log.e(type+"-- DoIN EX", e.toString());
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
                        if(this.type.equalsIgnoreCase("party")){
                            JSONArray partArray = result.getJSONArray("parties");
                            if (partArray.length() > 0) {
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    TruckVo voData = new TruckVo();
                                    voData.set_id(partData.getString("_id"));
                                    voData.setRegistrationNo("" + partData.getString("name"));
                                    dataP.add(voData);
                                }
                            }
                            SpinnerCustomAdapter customAdapter=new SpinnerCustomAdapter(getApplicationContext(),dataP,this.type);
                            spinParties.setAdapter(customAdapter);
                            pDialog.dismiss();

                            if(lookuup.length()> 0)
                            {
                                new GetFreshParty().execute();
                            }
                         }
                    }
                } catch (Exception e) {
                    System.out.println("ex in get leads" + e.toString());
                }

            } else {
                pDialog.dismiss();
                Toast.makeText(context,getResources().getString(R.string.exceptionmsg),Toast.LENGTH_LONG).show();
            }
        }
    }


    public class SpinnerCustomAdapter extends BaseAdapter {
        Context mcontext;
        ArrayList<TruckVo> dataset;
        LayoutInflater inflter;
        String Type;

        public SpinnerCustomAdapter(Context applicationContext, ArrayList<TruckVo> dataset,String Type) {
            this.mcontext = applicationContext;
            this. dataset = dataset;
            inflter = (LayoutInflater.from(applicationContext));
            this.Type = Type;
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
            //truckID  = book.get_id();


            if(this.Type.equalsIgnoreCase("trucks"))
            {
                if(i == 0){
                    truckID = "";
                }else{
                    truckID  = book.get_id();
                }
            }else if(this.Type.equalsIgnoreCase("trips")){
                if(i == 0){
                    tripID = "";
                }else{
                    tripID  = book.get_id();
                }
            }else if(this.Type.equalsIgnoreCase("party")){
                if(i == 0){
                    partyID = "";
                }else{
                    partyID  = book.get_id();
                }
            }


            return bookRow;
        }
    }


    private class AddMaintenance extends AsyncTask<String, String, JSONObject> {
        String maintenanceDate, maintenanceTrucknum, maintenancCost,maintenancArea;

        public AddMaintenance(String maintenanceDate,String maintenanceTrucknum,String maintenancCost,String maintenancArea) {
            this.maintenanceDate = maintenanceDate;
            this.maintenanceTrucknum=maintenanceTrucknum;
            this.maintenancCost =maintenancCost;
            this.maintenancArea=maintenancArea;
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

                try {
                    if(lookuup.length()> 0) {
                        post_dict.put("_id", lookuup);
                    }
                    post_dict.put("partyId",partyID);
                    post_dict.put("amount", maintenancCost);
                    post_dict.put("date", maintenanceDate);
                    post_dict.put("description", maintenancArea);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("payments" + String.valueOf(post_dict));
                String result="";
                if(lookuup.length()> 0) {
                    result = parser.ERPexcutePut(context, TruckApp.PaymentURL+"/updatePayments", String.valueOf(post_dict));
                    System.out.println("edit PArty Details" );
                }else {
                    result = parser.easyyExcutePost(context, TruckApp.PaymentURL + "/" + "addPayments", String.valueOf(post_dict));
                    System.out.println("addPayments-->" + result);
                }
                res = new JSONObject(result);

            } catch (Exception e) {
                Log.e("addExpense DoIN EX", e.toString());
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
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        if(lookuup.length()> 0) {
                            Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Successfully added", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent=new Intent();
                        intent.putExtra("updated","add");
                        intent.putExtra("addItem","");//s.getJSONObject("driver").toString()
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


    private class GetFreshParty extends AsyncTask<String, String, JSONObject> {
        public GetFreshParty() {}
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject json = null;
            try {
                String res = parser.erpExecuteGet(context,TruckApp.PaymentRecordURL+lookuup);
                json = new JSONObject(res);
            } catch (Exception e) {
                Log.e("paymentsDetails DoIN EX", e.toString());
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

                        JSONObject partData = result.getJSONObject("paymentsDetails");

                        maintnce_dateTV.setText(getDate(partData.getString("date")));
                        maintnce_areET.setText(partData.getString("description"));
                        maintnce_costET.setText(""+partData.getInt("amount"));
                        if(maintnce_dateTV.getText().toString().length() >0){
                            maintDatelblTV.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < dataP.size(); i++) {
                            TruckVo vo = dataP.get(i);
                            System.out.println(vo.get_id()+"riyaz"+partData.getString("partyId"));
                            if(vo.get_id().contentEquals(partData.getString("partyId"))){
                                spinParties.setSelection(i);
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
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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

