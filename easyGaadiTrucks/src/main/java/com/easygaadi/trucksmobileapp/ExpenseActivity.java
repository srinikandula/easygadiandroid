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
import android.widget.BaseAdapter;
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

public class ExpenseActivity extends AppCompatActivity {
    TextView maintDatelblTV,maintnce_dateTV,maintnce_trunknum_lbl,maintnce_lbl,maintnce_cost_lbl,
            maintnce_are_lbl;
    String truckID = "",expensesID = "";
    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    ArrayList<TruckVo> datat,dataE;
    Spinner spinTruck,spinExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);


        context = ExpenseActivity.this;
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        datat = new ArrayList<TruckVo>();
        dataE = new ArrayList<TruckVo>();
        detectCnnection = new ConnectionDetector(context);

        spinTruck = (Spinner) findViewById(R.id.spnr_trunknum);
        spinExpense = (Spinner) findViewById(R.id.spnr_expense);

        maintnce_dateTV = (TextView)findViewById(R.id.maintnce_date);
        maintDatelblTV = (TextView)findViewById(R.id.maintnce_date_lbl);
        maintnce_trunknum_lbl = (TextView)findViewById(R.id.maintnce_trunknum_lbl);
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

        TruckVo voDatas = new TruckVo();
        voDatas.set_id("");
        voDatas.setRegistrationNo("Assigned Truck");

        datat.add(voDatas);

        TruckVo voDatae = new TruckVo();
        voDatae.set_id("");
        voDatae.setRegistrationNo("Select Expenses");
        dataE.add(voDatae);





        if (detectCnnection.isConnectingToInternet()) {
            new GetAllTrucks("").execute();
        } else {
            Toast.makeText(context,
                    res.getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }
    }

    EditText maintnce_PTypeET,maintnce_rTypeET,maintnce_costET,maintnce_shedET,maintnce_areET;
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
            if(maintenanceTrucknum.trim().length()>0){
                if(true){
                    if(true){
                        if(maintenancCost.trim().length()>0){
                            if(true){
                                if(maintenancArea.trim().length()>0){
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

        DatePickerDialog dpd = new DatePickerDialog(ExpenseActivity  .this,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //(view).setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                if(Tview.getId() == R.id.maintnce_date){
                    maintnce_dateTV.setText(view.getYear()+"-"+view.getMonth()+"-"+view.getDayOfMonth());
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

    private class GetAllTrucks extends AsyncTask<String, String, JSONObject> {

        String type;

        public GetAllTrucks(String type) {
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
                if(type.equalsIgnoreCase("trucks"))
                {
                     res = parser.erpExecuteGet(context,TruckApp.truckListURL);
                    Log.e("truckListURL",res.toString());
                }else if(type.equalsIgnoreCase("expenses")){
                    res = parser.erpExecuteGet(context,TruckApp.ExpensesURL+"/getAllExpense");
                    Log.e("ExpensesURL",res.toString());
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
                        if(this.type.equalsIgnoreCase("trucks")) {
                            JSONArray partArray = result.getJSONArray("trucks");
                            if (partArray.length() > 0) {
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    TruckVo voData = new TruckVo();
                                    voData.set_id(partData.getString("_id"));
                                    voData.setRegistrationNo("" + partData.getString("registrationNo"));

                                    datat.add(voData);
                                }
                                SpinnerCustomAdapter customAdapter=new SpinnerCustomAdapter(getApplicationContext(),datat,this.type);
                                spinTruck.setAdapter(customAdapter);
                                pDialog.dismiss();
                                new GetAllTrucks("expenses").execute();
                            }
                        }else if(this.type.equalsIgnoreCase("expenses")){
                            JSONArray partArray = result.getJSONArray("maintanenceCosts");
                            if (partArray.length() > 0) {
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    TruckVo voData = new TruckVo();
                                    voData.set_id(partData.getString("_id"));
                                    voData.setRegistrationNo("" + partData.getString("expenseType"));
                                    JSONObject attributes = partData.getJSONObject("attrs");
                                    if(attributes.has("expenseName"))
                                    {
                                        voData.setTruckType(attributes.getString("expenseName"));
                                    }
                                    dataE.add(voData);
                                }
                                SpinnerCustomAdapter customAdapter=new SpinnerCustomAdapter(getApplicationContext(),dataE,this.type);
                                spinExpense.setAdapter(customAdapter);
                                pDialog.dismiss();

                            }
                        }
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

            }else if(this.Type.equalsIgnoreCase("expenses")){

                if(i == 0){
                    expensesID = "";
                }else{
                    expensesID  = book.get_id();
                }
            }


            return bookRow;
        }
    }


    private class AddMaintenance extends AsyncTask<String, String, JSONObject> {
        String maintenanceDate, maintenanceTrucknum,maintenanceRepair, maintenancPay, maintenancCost,maintenancShed,maintenancArea;

        public AddMaintenance(String maintenanceDate, String maintenanceTrucknum,String maintenancCost,String maintenancArea) {
            this.maintenanceDate = maintenanceDate;
            this.maintenanceTrucknum=maintenanceTrucknum;
            this.maintenanceRepair= maintenanceRepair;
            this.maintenancPay= maintenancPay;
            this.maintenancCost =maintenancCost;
            this.maintenancShed=maintenancShed;
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
                JSONObject salary = new JSONObject();

                try {
                    post_dict.put("description" , maintenanceRepair);
                    post_dict.put("vehicleNumber",truckID);
                    post_dict.put("cost", maintenancCost);
                    post_dict.put("date", maintenanceDate);
                    post_dict.put("location", maintenancArea);
                    post_dict.put("expenseType",maintenancPay);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("" + String.valueOf(post_dict));
                String result="";

                result = parser.easyyExcutePost(context, TruckApp.maintenanceListURL+"/"+"addMaintenance", String.valueOf(post_dict));
                System.out.println("AddMaintenance-->" +result);
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

}
