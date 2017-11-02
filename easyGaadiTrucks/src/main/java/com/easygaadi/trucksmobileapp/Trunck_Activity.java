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

public class Trunck_Activity extends AppCompatActivity {

    String truckID = "";
    TextView truck_type_lbl,truck_modelTV,truck_modellblTV,truck_duetaxTV,truck_tonnagelblTV,truck_duetaxlbltv,
            truck_fexpireTV, truck_fexpirelbltv,truck_insexpirelbltv,truck_insexpireTV,
            truck_perexpirelblTV,truck_perexpireTV,truck_pollexpirelblTV,truck_pollexpireTV,
            truck_reg_lblTV,truck_type_lblTV;
    EditText truck_regET,truck_typeET,truck_modelET,truck_tonnageET;
    Spinner spin;
    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    ArrayList<TruckVo> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trunck_);
        context =  Trunck_Activity.this;
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);

        parser = JSONParser.getInstance();
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        context = getApplicationContext();
        detectCnnection = new ConnectionDetector(context);
        truck_type_lbl = (TextView)findViewById(R.id.truck_type_lbl);
        truck_modelTV = (TextView)findViewById(R.id.truck_model);
        truck_modellblTV = (TextView)findViewById(R.id.truck_modellbl);
        truck_tonnagelblTV = (TextView)findViewById(R.id.truck_tonnagelbl);
        truck_duetaxTV = (TextView)findViewById(R.id.truck_duetax);
        truck_duetaxlbltv = (TextView)findViewById(R.id.truck_duetaxlbl);
        truck_fexpireTV = (TextView)findViewById(R.id.truck_fexpire);
        truck_fexpirelbltv = (TextView)findViewById(R.id.truck_fexpirelbl);
        truck_insexpirelbltv = (TextView)findViewById(R.id.truck_insexpirelbl);
        truck_insexpireTV = (TextView)findViewById(R.id.truck_insexpire);
        truck_perexpireTV = (TextView)findViewById(R.id.truck_perexpire);
        truck_perexpirelblTV = (TextView)findViewById(R.id.truck_perexpirelbl);
        truck_pollexpirelblTV = (TextView)findViewById(R.id.truck_pollexpirelbl);
        truck_pollexpireTV = (TextView)findViewById(R.id.truck_pollexpire);
        truck_reg_lblTV = (TextView)findViewById(R.id.truck_reg_lbl);
        truck_type_lblTV = (TextView)findViewById(R.id.truck_type_lbl);


        truck_regET = (EditText)findViewById(R.id.truck_reg);
        truck_typeET = (EditText)findViewById(R.id.truck_type);
        truck_modelET = (EditText)findViewById(R.id.truck_model);
        truck_tonnageET = (EditText)findViewById(R.id.truck_tonnage);

        chnageTextView(truck_regET);
        chnageTextView(truck_typeET);
        chnageTextView(truck_modelET);
        chnageTextView(truck_tonnageET);

        ListenerDate(truck_modelTV);
        ListenerDate(truck_duetaxTV);
        ListenerDate(truck_fexpireTV);
        ListenerDate(truck_insexpireTV);
        ListenerDate(truck_perexpireTV);
        ListenerDate(truck_pollexpireTV);
        initilizaSpinner();

    }

    private void initilizaSpinner()
    {
        spin = (Spinner) findViewById(R.id.spnr_driver_fname);
        data = new ArrayList<TruckVo>();
        if (detectCnnection.isConnectingToInternet()) {
            new GetBuyingTrucks().execute();
        } else {
            Toast.makeText(context,
                    res.getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }
    }



    public void chnageTextView(final EditText etview){

        etview.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if(R.id.truck_reg == etview.getId()) {
                    if (string.trim().length() != 0) {
                        truck_reg_lblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_reg_lblTV);
                    }else{
                        truck_reg_lblTV.setVisibility(View.INVISIBLE);
                    }
                } else if(R.id.truck_type == etview.getId()) {
                    if (string.trim().length() != 0) {
                        truck_type_lblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_type_lblTV);
                    }else{
                        truck_type_lblTV.setVisibility(View.INVISIBLE);
                    }
                } else if(R.id.truck_model == etview.getId()) {
                    if (string.trim().length() != 0) {
                        truck_modellblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_modellblTV);
                    }else{
                        truck_modellblTV.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.truck_tonnage == etview.getId()) {
                    if (string.trim().length() != 0) {
                        truck_tonnagelblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_tonnagelblTV);
                    }else{
                        truck_tonnagelblTV.setVisibility(View.INVISIBLE);
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



    public void ListenerDate(final TextView dateTV)
    {
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(dateTV);
            }
        });
    }

    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(Trunck_Activity.this,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //(view).setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                int tempmonth = view.getMonth()+1;
                String temp = ""+tempmonth;
                if(temp.length()>2){
                    temp ="0"+temp;
                }
                 if(Tview.getId() == R.id.truck_duetax){

                    truck_duetaxTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(truck_duetaxTV.getText().toString().length()>0){
                        truck_duetaxlbltv.setVisibility(View.VISIBLE);
                        slideUp(truck_duetaxlbltv);
                    }else {
                        truck_duetaxlbltv.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_fexpire){
                    truck_fexpireTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(truck_fexpireTV.getText().toString().length()>0){
                        truck_fexpirelbltv.setVisibility(View.VISIBLE);
                        slideUp(truck_fexpirelbltv);
                    }else {
                        truck_fexpirelbltv.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_insexpire){
                    truck_insexpireTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(truck_insexpireTV.getText().toString().length()>0){
                        truck_insexpirelbltv.setVisibility(View.VISIBLE);
                        slideUp(truck_insexpirelbltv);
                    }else {
                        truck_insexpirelbltv.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_perexpire){
                    truck_perexpireTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(truck_perexpireTV.getText().toString().length()>0){
                        truck_perexpirelblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_perexpirelblTV);
                    }else {
                        truck_perexpirelblTV.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_pollexpire){
                    truck_pollexpireTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(truck_pollexpireTV.getText().toString().length()>0){
                        truck_pollexpirelblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_pollexpirelblTV);
                    }else {
                        truck_pollexpirelblTV.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }, year, month, day);

        dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dpd.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    if(Tview.getId() == R.id.truck_model){
                        if(truck_modelTV.getText().toString().length()>0){
                            truck_modellblTV.setVisibility(View.VISIBLE);
                        }else {
                            truck_modellblTV.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });

        dpd.show();
    }

    public void callMainAct(View view) {
        // startActivity(new Intent(Party_Activity.this,Maintenance_Activity.class));
        //partNameET,party_mobileET,party_mailET,partCitryET,partLaneET,,
        String truckreg = truck_regET.getText().toString().trim();
        String truckType = truck_typeET.getText().toString().trim();
        String truck_model = truck_modelET.getText().toString().trim();
        String truck_tonnage = truck_tonnageET.getText().toString().trim();
        String truckfitness = truck_fexpireTV.getText().toString().trim();
        String truckInsura = truck_insexpireTV.getText().toString().trim();
        String truckPermit = truck_perexpireTV.getText().toString().trim();
        String truckPoll= truck_pollexpireTV.getText().toString().trim();
        String trucktax= truck_duetaxTV.getText().toString().trim();;

        if(truckreg.length()>0){
            if(truckType.length()>0){
                if(truck_model.length()>0){
                    if(truck_tonnage.length()>0){
                        if(truckfitness.contains("-")){
                            if(truckInsura.contains("-")){
                                if(truckPermit.contains("-")){
                                    if(truckPoll.contains("-")){
                                        if(trucktax.contains("-")){

                                            if (detectCnnection.isConnectingToInternet()) {
                                                new AddTruck(truckreg, truckType,truck_model, truck_tonnage, truckfitness,truckInsura,truckPermit,truckPoll,trucktax).execute();
                                            } else {
                                                Toast.makeText(context,res.getString(R.string.internet_str),Toast.LENGTH_LONG).show();
                                            }
                                        }else{
                                            Toast.makeText(context, "Please Select Due Tax date ", Toast.LENGTH_SHORT).show();
                                        }

                                    }else{
                                        Toast.makeText(context, "Please Select Pollution date ", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(context, "Please Select Permit date ", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(context, "Please Select Insurance date ", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(context, "Please Select Fitness date ", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(context, "Please Enter Truck tonnage", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Please Enter Truck_model", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "Please Enter tTruck type", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Please Registration num", Toast.LENGTH_SHORT).show();
        }



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
            pDialog.setMessage("Fetching Driver Name Please..");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                String res = parser.erpExecuteGet(context,TruckApp.driverListURL+"/1");
                System.out.println("paylist"+res.toString());
                json = new JSONObject(res);

            } catch (Exception e) {
                System.out.println("Login DoIN EX"+ e.toString());
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
                    voDatas.setRegistrationNo("Assigned Driver");

                    data.add(voDatas);

                    if (!result.getBoolean("status")) {
                        Toast.makeText(context, "No records available",Toast.LENGTH_LONG).show();
                    }else
                    {
                        JSONArray partArray = result.getJSONArray("drivers");
                        if(partArray.length() > 0)
                        {
                            for (int i = 0; i < partArray.length(); i++) {
                                JSONObject partData = partArray.getJSONObject(i);
                                TruckVo voData = new TruckVo();
                                voData.set_id(partData.getString("_id"));
                                voData.setRegistrationNo(""+partData.getString("fullName"));

                                data.add(voData);
                            }
                            pDialog.dismiss();
                        }
                    }
                    SpinnerCustomAdapter customAdapter=new SpinnerCustomAdapter(getApplicationContext(),data);
                    spin.setAdapter(customAdapter);

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
            }else{
                truckID  = book.get_id();
            }
            return bookRow;
        }
    }

    private class AddTruck extends AsyncTask<String, String, JSONObject> {
        String truckreg, truckType,truck_model, truck_tonnage, truckfitness,truckInsura,truckPermit,truckPoll,trucktax;

        public AddTruck(String truckreg, String truckType,String truck_model,String truck_tonnage,String truckfitness,String truckInsura,String truckPermit,String truckPoll,String trucktax) {
            this.truckreg = truckreg;
            this.truckType = truckType;
            this.truck_model = truck_model;
            this.truck_tonnage = truck_tonnage;
            this.truckfitness = truckfitness;
            this.truckInsura = truckInsura;
            this.truckPermit = truckPermit;
            this.truckPoll = truckPoll;
            this.trucktax = trucktax;
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
                    post_dict.put("registrationNo" , truckreg);
                    post_dict.put("truckType",truckType);
                    post_dict.put("modelAndYear", truck_model);
                    post_dict.put("tonnage", truck_tonnage);
                    post_dict.put("fitnessExpiry", truckfitness);
                    post_dict.put("insuranceExpiry", truckInsura);
                    post_dict.put("permitExpiry", truckPermit);
                    post_dict.put("pollutionExpiry", truckPoll);
                    post_dict.put("taxDueDate", trucktax);
                    post_dict.put("driverId", truckID);
                    post_dict.put("tripLane", "");
                    post_dict.put("tripExpenses", 50);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("" + String.valueOf(post_dict));
                String result = parser.easyyExcutePost(context,TruckApp.truckListURL,String.valueOf(post_dict));
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

                        Toast.makeText(context, "Fail to Added",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, ""+"Truck Added", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent();
                        intent.putExtra("addItem",s.getJSONObject("truck").toString());
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
