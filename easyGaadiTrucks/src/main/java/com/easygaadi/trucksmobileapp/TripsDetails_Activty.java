package com.easygaadi.trucksmobileapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.erp.TripList;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.TripVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TripsDetails_Activty extends AppCompatActivity {

    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    String lookuup,forActivty;
    TextView trip_feightamt,trip_advanceamt;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<TripVo> data;
    CustomAdapter partyadapter;
    private EditText fromdate_tv,amount_tv;
    private Dialog reportDialog;
    String paymentType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_details__activty);
        initializeViews();
        context = TripsDetails_Activty.this;
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        detectCnnection = new ConnectionDetector(context);
        recyclerView = (RecyclerView) findViewById(R.id.quotes_rc);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        lookuup = getIntent().getStringExtra("hitupdate");
        forActivty = getIntent().getStringExtra("call");
        if (detectCnnection.isConnectingToInternet()) {
            new GetTtripDetails().execute();
        } else {
            Toast.makeText(context,
                    res.getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }

    }

    public void AddPayment(View view)
    {
        if(view.getId() == R.id.payment_type)
        {
            setReportDialog(lookuup);
        }
    }

    public void callback(View view){
        Intent intent=new Intent();
        intent.putExtra("addItem","");
        setResult(123,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("addItem","");
        setResult(123,intent);
        finish();
        super.onBackPressed();
        // Do extra stuff here
    }


    private void setReportDialog(final String tripID){
        String[] payment = { "Payment Type","Cheque", "Chash"  };

        reportDialog = null;
        reportDialog = new Dialog(context, android.R.style.Theme_Dialog);
        reportDialog.requestWindowFeature(1);
        reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        reportDialog.setContentView(R.layout.erp_trips_addbal_dialog);
        ImageView cardDiaCls = (ImageView)reportDialog.findViewById(R.id.recharge_close);
        final Button go_btn = (Button)reportDialog.findViewById(R.id.go_btn);
        fromdate_tv = (EditText) reportDialog.findViewById(R.id.Date_et);
        amount_tv = (EditText) reportDialog.findViewById(R.id.amount_tv);
        fromdate_tv.setClickable(true);
        fromdate_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(fromdate_tv);
            }
        });

        //Spinner trip_pymtbl = (Spinner) reportDialog.findViewById(R.id.spnr_paymnttype);
        Spinner payspin = (Spinner) reportDialog.findViewById(R.id.spnr_paymnttype);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter paytypeeaa = new ArrayAdapter(context,R.layout.erp_view_spinner_item,payment);
        paytypeeaa.setDropDownViewResource(R.layout.erp_view_spinner_item);
        //Setting the ArrayAdapter data on the Spinner
        payspin.setAdapter(paytypeeaa);


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
                String amount = amount_tv.getText().toString().trim();
                String paymentsType = paymentType;
                if(fromDate.contains("-")){
                    if(amount.length() > 0){
                        if(Integer.parseInt(amount) > 0){
                            if(paymentsType.length() > 0){
                                if (detectCnnection.isConnectingToInternet()) {
                                    new AddPayment(tripID,fromDate,amount,paymentsType).execute();
                                } else {
                                    Toast.makeText(context,context.getResources().getString(R.string.internet_str),Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(context,"Please Select Payment Type",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(context,"Please Enter VAlid Amount ",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context,"Please Enter Amount",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,"Please Select Date",Toast.LENGTH_SHORT).show();
                }
            }
        });


        AdapterView.OnItemSelectedListener countrySelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,int position, long id) {
                if(spinner.getId() == R.id.spnr_paymnttype){
                    String selected = spinner.getItemAtPosition(position).toString();
                    if(selected.equalsIgnoreCase("Payment Type"))
                    {
                        //trip_pymtbl.setVisibility(View.INVISIBLE);

                    }else{
                        //trip_pymtbl.setVisibility(View.VISIBLE);
                    }
                    if(position !=0)
                    {
                        paymentType = spinner.getItemAtPosition(position).toString();
                    }else {
                        paymentType = "";
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        };

        payspin.setOnItemSelectedListener(countrySelectedListener);

    }



    private void initializeViews()
    {
        trip_feightamt  = (TextView)findViewById(R.id.trip_feightamt);
        trip_advanceamt = (TextView)findViewById(R.id.trip_advanceamt);
    }


    private class GetTtripDetails extends AsyncTask<String, String, JSONObject> {

        //String uid, accountid, offset;

        public GetTtripDetails() {
            //this.uid = uid;
            //this.accountid = accountid;
            //this.offset = String.valueOf(offset);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Fetching Trip Details Please wait..");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                Log.i("trip id",lookuup);
                String res = parser.erpExecuteGet(context,TruckApp.tripsListURL+"/"+lookuup);
                Log.e("tripsListURL",res.toString());
                json = new JSONObject(res);

            } catch (Exception e) {
                Log.e("tripsListURL DoIN EX", e.toString());
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
                        Toast.makeText(context, ""+"unable to fetch records",Toast.LENGTH_LONG).show();
                        pDialog.show();
                        finish();
                    }else
                    {
                        JSONObject tripObj = result.getJSONObject("trip");
                        ((TextView)findViewById(R.id.trip_id)).setText(tripObj.getString("tripId"));
                        ((TextView)findViewById(R.id.tripsdur)).setText(getFormatDate(tripObj.getString("updatedAt")));
                        ((TextView)findViewById(R.id.srcloc_tv)).setText(tripObj.getString("from"));
                        ((TextView)findViewById(R.id.destloc_tv)).setText(tripObj.getString("to"));
                        ((TextView)findViewById(R.id.diesel_tv)).setText("Diesel "+ ""+ tripObj.getInt("dieselAmount"));
                        ((TextView)findViewById(R.id.toll_tv)).setText("Toll "+ ""+ tripObj.getInt("tollgateAmount"));
                        trip_feightamt.setText(""+tripObj.getInt("freightAmount"));
                        trip_advanceamt.setText(""+tripObj.getInt("advance"));
                        ((TextView)findViewById(R.id.trip_bal)).setText( ""+tripObj.getInt("balance"));
                        if(tripObj.getInt("balance") >0){
                            ((LinearLayout)findViewById(R.id.paymntLL)).setVisibility(View.VISIBLE);
                        }

                        if(tripObj.has("attrs")) {
                            JSONObject attrsObj = tripObj.getJSONObject("attrs");
                            ((TextView) findViewById(R.id.truckRegNo_tv)).setText(attrsObj.getString("truckName"));
                            ((TextView) findViewById(R.id.tripsnaam)).setText(attrsObj.getString("partyName"));
                        }


                        JSONArray partArray = tripObj.getJSONArray("paymentHistory");
                        if(partArray.length() > 0)
                        {
                            data = new ArrayList<TripVo>();
                            for (int i = 0; i < partArray.length(); i++) {
                                JSONObject partData = partArray.getJSONObject(i);

                                TripVo voData = new TripVo();
                                voData.setPaymentType(partData.getString("paymentType"));
                                voData.setBalance(""+partData.getInt("amount"));
                                voData.setUpdatedAt(""+partData.getString("paymentDate"));

                                if(partData.has("attrs")) {
                                    JSONObject attrsObj = tripObj.getJSONObject("attrs");
                                    voData.setCreatedBy(attrsObj.getString("createdByName"));
                                }
                                data.add(voData);

                            }
                            partyadapter = new CustomAdapter(data);
                            recyclerView.setAdapter(partyadapter);
                        }
                        pDialog.dismiss();
                    }
                } catch (Exception e) {
                    System.out.println("paymentHistory" + e.toString());
                    pDialog.show();
                }

            } else {
                pDialog.dismiss();
                Toast.makeText(context,
                        getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    private class AddPayment extends AsyncTask<String, String, JSONObject> {
        String tripId, paymentDate,amount, paymentType;

        public AddPayment(String tripId,String  paymentDate,String  amount,String  paymentType) {
            this.tripId = tripId;
            this.paymentDate = paymentDate;
            this.amount = amount;
            this.paymentType = paymentType;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject res = null;
            try {

                JSONObject post_dict = new JSONObject();
                JSONObject salary = new JSONObject();

                try {
                    post_dict.put("tripId",tripId);
                    post_dict.put("paymentDate", paymentDate);
                    post_dict.put("paymentType", paymentType);
                    post_dict.put("amount", amount);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("" + String.valueOf(post_dict));
                String result="";
                result = parser.ERPexcutePut(context, TruckApp.PaymentURL, String.valueOf(post_dict));
                System.out.println("PaymentURL Trip"+result );
                res = new JSONObject(result);

            } catch (Exception e) {
                Log.e("PaymentURL DoIN EX", e.toString());
                res = null;
            }
            return res;
        }
        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            // login_btn.setEnabled(true);
            pDialog.dismiss();
            Log.v("PaymentURL","res"+s.toString());
            if (s != null) {

                try {
                    //JSONObject js = new JSONObject(s);
                    if (!s.getBoolean("status")) {
                        Toast.makeText(context, "fail",Toast.LENGTH_LONG).show();
                    } else {
                        reportDialog.dismiss();
                        Toast.makeText(context, "Successfully Amount Addedd", Toast.LENGTH_SHORT).show();
                        if (detectCnnection.isConnectingToInternet()) {
                            new GetTtripDetails().execute();
                        }else{
                            Toast.makeText(context,
                                    getResources().getString(R.string.internet_str),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                } catch (JSONException e) {
                    System.out.println("Exception while extracting the response:"+ e.toString());
                }
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }



    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>  {

        private ArrayList<TripVo> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textViewName,textViewAmount,textViewlastupadate,textViewPayment;
            LinearLayout headerLL;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.textViewName = (TextView) itemView.findViewById(R.id.addedby_tv);
                this.textViewAmount = (TextView) itemView.findViewById(R.id.amount_tv);
                this.textViewlastupadate = (TextView) itemView.findViewById(R.id.createDate_tv);
                this.textViewPayment = (TextView) itemView.findViewById(R.id.paymentType_tv);
                this.headerLL = (LinearLayout)itemView.findViewById(R.id.headerLL);

            }
        }

        public CustomAdapter(ArrayList<TripVo> data) {
            this.dataSet = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_payment_catitem_layout, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView textViewName = holder.textViewName;
            TextView textViewAmount = holder.textViewAmount;
            TextView textViewlastupadate = holder.textViewlastupadate;
            TextView textViewPayment = holder.textViewPayment;
            LinearLayout headerLL = holder.headerLL;

            if(listPosition == 0)
            {
                headerLL.setVisibility(View.VISIBLE);
            }

            textViewName.setText(""+dataSet.get(listPosition).getCreatedBy());
            textViewAmount.setText(""+dataSet.get(listPosition).getBalance());
            textViewlastupadate.setText(""+getFormatDate(dataSet.get(listPosition).getUpdatedAt()));
            textViewPayment.setText(""+dataSet.get(listPosition).getPaymentType());

        }



        @Override
        public int getItemCount() {
            return dataSet.size();
        }


    }


    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(context,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                int tempmonth = view.getMonth()+1;
                String temp = ""+tempmonth;
                if(temp.length()>2){
                    temp ="0"+temp;
                }
                if(Tview.getId() == R.id.Date_et){
                    fromdate_tv.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());

                }
            }
        }, year, month, day);

        dpd.show();


    }

    public String getFormatDate(String fdate){

        Date date;
        String diff = "";

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
