package com.easygaadi.trucksmobileapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.erp.CommonERP;
import com.easygaadi.erp.DriverList;
import com.easygaadi.erp.TripList;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.DriverVo;
import com.easygaadi.models.ReportVo;
import com.easygaadi.models.TripVo;
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
import java.util.concurrent.TimeUnit;

public class ERP_Report extends AppCompatActivity {

    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    CustomAdapter partyadapter;
    TextView trip_frm_dateTV,trip_to_dateTV,trip_frm_datelbl,trip_to_datelbl,trip_trucklbl,trip_drnmelbl;
    Spinner spnrTrucknum,spnrDrivername;

    ArrayList<TruckVo> datat,datad;
    String TruckID="",registrationNo="",DriverID="";
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<ReportVo> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_erp__report);

        context = ERP_Report.this;
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        detectCnnection = new ConnectionDetector(context);
        initializeView();

        spnrTrucknum = (Spinner) findViewById(R.id.spnr_trucknum);
        spnrDrivername = (Spinner) findViewById(R.id.spnr_driver);
        datat = new ArrayList<TruckVo>();
        datad = new ArrayList<TruckVo>();

        TruckVo voDatas = new TruckVo();
        voDatas.set_id("");
        voDatas.setRegistrationNo("Assigned Truck");
        datat.add(voDatas);
        TruckVo voDatasq = new TruckVo();
        voDatasq.set_id("");
        voDatasq.setRegistrationNo("Assigned Driver");
        datad.add(voDatasq);
        if (detectCnnection.isConnectingToInternet()) {
            new GetBuyingTrucks("truck").execute();
        } else {
            Toast.makeText(context,res.getString(R.string.internet_str),Toast.LENGTH_LONG).show();
        }

        AdapterView.OnItemSelectedListener countrySelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,int position, long id) {

                 if(spinner.getId() == R.id.spnr_trucknum){
                    if(position ==0)
                    {
                        trip_trucklbl.setVisibility(View.INVISIBLE);

                    }else{
                        trip_trucklbl.setVisibility(View.VISIBLE);

                       // updateDriverSpinner(position);
                    }
                }else  if(spinner.getId() == R.id.spnr_driver){
                    if(position ==0)
                    {
                        trip_drnmelbl.setVisibility(View.INVISIBLE);

                    }else{
                        trip_drnmelbl.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {


            }
        };

        spnrTrucknum.setOnItemSelectedListener(countrySelectedListener);
        spnrDrivername.setOnItemSelectedListener(countrySelectedListener);
    }

    private void initializeView() {

        trip_frm_dateTV = (TextView)findViewById(R.id.trip_frm_date);
        trip_to_dateTV = (TextView)findViewById(R.id.trip_to_date);
        trip_to_datelbl = (TextView)findViewById(R.id.trip_to_datelbl);
        trip_frm_datelbl = (TextView)findViewById(R.id.trip_frm_datelbl);
        trip_trucklbl = (TextView)findViewById(R.id.trip_trucklbl);
        trip_drnmelbl = (TextView)findViewById(R.id.trip_drnmelbl);
        trip_frm_dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(trip_frm_dateTV);
            }
        });

        trip_to_dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(trip_to_dateTV);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.trip_list_rc);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void callback(View view){

        finish();
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
        // Do extra stuff here
    }

    public void GenerateReport(View v){
        //truckID="",registrationNo="",DriverID=""
        String fromDate = trip_frm_dateTV.getText().toString().trim();
        String toDate = trip_to_dateTV.getText().toString().trim();
        String truckID = TruckID;
        String driverID = DriverID;


        if (detectCnnection.isConnectingToInternet()) {
            Log.i("generate",fromDate+""+toDate+""+truckID+""+driverID);
            new FindTripReports(fromDate,toDate,truckID,driverID).execute();
        } else {
            Toast.makeText(context,res.getString(R.string.internet_str),Toast.LENGTH_LONG).show();
        }
    }



    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ERP_Report.this,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                int tempmonth = view.getMonth()+1;
                String temp = ""+tempmonth;
                if(temp.length()>2){
                    temp ="0"+temp;
                }
                if(Tview.getId() == R.id.trip_frm_date){
                    trip_frm_dateTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(trip_frm_dateTV.getText().toString().length()>0){
                        trip_frm_datelbl.setVisibility(View.VISIBLE);
                        //slideUp(trip_lbl);
                    }else {
                        trip_frm_datelbl.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.trip_to_date){
                    trip_to_dateTV.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());
                    if(trip_to_dateTV.getText().toString().length()>0){
                        trip_to_datelbl.setVisibility(View.VISIBLE);
                        //slideUp(trip_lbl);
                    }else {
                        trip_to_datelbl.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }, year, month, day);


        dpd.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {

                }
            }
        });

        dpd.show();

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
                    res = parser.erpExecuteGet(context,TruckApp.driverListURL+"/account/drivers");
                    Log.e("driverListURL",res.toString());
                }
                json = new JSONObject(res);

            } catch (Exception e) {
                Log.e("type EX", e.toString());
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
                                SpinnerCustomAdapter customAdapter = new SpinnerCustomAdapter(getApplicationContext(),datad,"drivers");
                                spnrDrivername.setAdapter(customAdapter);
                                pDialog.dismiss();
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

                                    if(partData.has("driverId")) {
                                        voData.setDriverID("" + partData.getString("driverId"));//driverId
                                    }else{
                                        voData.setDriverID("" );//driverId
                                    }
                                    datat.add(voData);
                                }
                                pDialog.dismiss();
                            }else{
                                pDialog.dismiss();
                            }
                            new GetBuyingTrucks("drivers").execute();
                            SpinnerCustomAdapter customAdapter=new SpinnerCustomAdapter(getApplicationContext(),datat,"truck");
                            spnrTrucknum.setAdapter(customAdapter);
                        }

                    }
                } catch (Exception e) {
                    System.out.println(this.type+"type truck leads" + e.toString());
                }

            } else {
                pDialog.dismiss();
                Toast.makeText(context,
                        getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    private class FindTripReports extends AsyncTask<String, String, JSONObject> {
        String tripDatefrom,tripDateto,tripTruckID,tripDriverID;

        public FindTripReports(String tripDatefrom ,String tripDateto,String tripTruckID ,String tripDriverID ) {
            this.tripDatefrom = tripDatefrom;
            this.tripDateto =  tripDateto;
            this.tripTruckID =  tripTruckID;
            this.tripDriverID = tripDriverID;

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
                    post_dict.put("fromDate" , tripDatefrom);
                    post_dict.put("toDate",tripDateto);
                    post_dict.put("registrationNo",tripTruckID);
                    post_dict.put("driver", tripDriverID);


                } catch (JSONException e) {
                    Log.i("TripReports_post_dic", e.toString());
                    e.printStackTrace();
                }
                System.out.println("post_dict" + String.valueOf(post_dict));
                String result = parser.easyyExcutePost(context,TruckApp.tripsListURL+"/report ",String.valueOf(post_dict));
                res = new JSONObject(result);

            } catch (Exception e) {
                Log.i("triprepots DoIN EX",""+ e.toString());
                res = null;
            }
            return res;
        }
        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            // login_btn.setEnabled(true);
            progressFrame.setVisibility(View.GONE);
            //Log.v("reports","res"+s.toString());
            if (s != null) {

                try {
                    //JSONObject js = new JSONObject(s);
                    if (!s.getBoolean("status")) {

                        Toast.makeText(context,"fail",Toast.LENGTH_LONG).show();
                    } else {

                        JSONArray reportArray = s.getJSONArray("tripsReport");
                        System.out.print("trips lenght"+reportArray.length());
                        if(reportArray.length() > 0) {

                            data = new ArrayList<ReportVo>();
                            for (int i = 0; i < reportArray.length(); i++) {
                                JSONObject partData = reportArray.getJSONObject(i);


                                ReportVo report = new ReportVo();
                                JSONObject tripObj = partData.getJSONObject("trips");

                                if(tripObj.length() > 0) {
                                    report.setRegistrationNo(tripObj.getString("registrationNo"));

                                    if (tripObj.has("tripId")){
                                        report.setTripID(tripObj.getString("tripId"));
                                    }else{
                                        report.setTripID("");
                                    }

                                    if (tripObj.has("date")) {
                                        report.setDate(CommonERP.getDate(tripObj.getString("date")));
                                    }else{
                                        report.setDate("");
                                    }

                                    if (tripObj.has("driverName")){
                                        report.setDriverName(tripObj.getString("driverName"));
                                    }else{
                                        report.setDriverName("");
                                    }
                                    if (tripObj.has("bookedFor")){
                                        report.setBookedFor(tripObj.getString("bookedFor"));
                                    }else{
                                        report.setBookedFor("");
                                    }

                                    if (tripObj.has("from")){
                                        report.setFrom(tripObj.getString("from"));
                                    }else{
                                        report.setFrom("");
                                    }

                                    if (tripObj.has("to")){
                                        report.setTo(tripObj.getString("to"));
                                    }else{
                                        report.setTo("");
                                    }

                                    if (tripObj.has("mobile")){
                                        report.setMobile(tripObj.getString("mobile"));
                                    }else{
                                        report.setMobile("");
                                    }
                                }else{

                                }

                                JSONObject paymentsObj = partData.getJSONObject("payments");
                                if(paymentsObj.length() > 0) {
                                    if (paymentsObj.has("freightAmount")){
                                        report.setFreightAmount("" + paymentsObj.getInt("freightAmount"));
                                    }else{
                                        report.setFreightAmount("");
                                    }

                                    if (paymentsObj.has("advance")){
                                        report.setAdvance("" + paymentsObj.getInt("advance"));
                                    }else{
                                        report.setAdvance("");
                                    }

                                    if (paymentsObj.has("balance")){
                                        report.setBalance("" + paymentsObj.getInt("balance"));
                                    }else{
                                        report.setBalance("");
                                    }
                                }else{
                                    report.setFreightAmount("");
                                    report.setAdvance("");
                                    report.setBalance("");
                                }
                                data.add(report);
                            }

                            Toast.makeText(context,"partyadapter lenght"+data.size(),Toast.LENGTH_LONG).show();
                            partyadapter = new CustomAdapter(data);
                            recyclerView.setAdapter(partyadapter);
                            pDialog.dismiss();
                        }else{
                            Toast.makeText(context,"No records found",Toast.LENGTH_LONG).show();
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


    public class SpinnerCustomAdapter extends BaseAdapter {
        Context mcontext;
        ArrayList<TruckVo> dataset;
        LayoutInflater inflter;
        String type = "";

        public SpinnerCustomAdapter(Context applicationContext, ArrayList<TruckVo> dataset,String type) {
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflator=LayoutInflater.from(mcontext);
            View bookRow=inflator.inflate(R.layout.erp_spinner_item, viewGroup, false);
            TextView names = (TextView) bookRow.findViewById(R.id.text1);
            TruckVo book= dataset.get(i);
            names.setText(book.getRegistrationNo());

            if(this.type.equalsIgnoreCase("truck")) {
                if (i == 0) {
                    TruckID = "";
                    registrationNo = "";
                } else {
                    TruckID = book.get_id();
                    registrationNo = book.getRegistrationNo();
                }

            }else if(this.type.equalsIgnoreCase("drivers")){
                if(i == 0){
                    DriverID = "";
                }else{
                    DriverID  = book.get_id();
                }
            }
            return bookRow;
        }
    }




    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>  {

        private ArrayList<ReportVo> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textViewTruckRegNum,textViewTripId,textViewTripDate,textViewPartyName,textViewDriverName,textViewTripLoc,
                    textViewDriverContact,textViewFreight,textViewAdvAmt,textViewBalAmt;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.textViewTruckRegNum = (TextView) itemView.findViewById(R.id.truckId_tv);
                this.textViewTripId = (TextView) itemView.findViewById(R.id.tv_tripId);
                this.textViewTripDate = (TextView) itemView.findViewById(R.id.triperdate_tv);
                this.textViewPartyName = (TextView) itemView.findViewById(R.id.partyname_tv);
                this.textViewDriverName = (TextView) itemView.findViewById(R.id.driverName_tv);
                this.textViewTripLoc = (TextView) itemView.findViewById(R.id.sdloc_tv);
                this.textViewDriverContact = (TextView) itemView.findViewById(R.id.drivecontact_tv);
                this.textViewFreight= (TextView) itemView.findViewById(R.id.freightamt_tv);
                this.textViewAdvAmt= (TextView) itemView.findViewById(R.id.advamt_tv);
                this.textViewBalAmt= (TextView) itemView.findViewById(R.id.balanceamt_tv);
            }
        }



        public CustomAdapter(ArrayList<ReportVo> data) {
            this.dataSet = data;
        }

        @Override
        public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reportscatitem_layout, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }



        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView textViewTruckRegNum = holder.textViewTruckRegNum;
            TextView textViewTripId = holder.textViewTripId;
            TextView textViewTripDate = holder.textViewTripDate;
            TextView textViewPartyName = holder.textViewPartyName;
            TextView textViewDriverName = holder.textViewDriverName;
            TextView textViewTripLoc = holder.textViewTripLoc;
            TextView textViewDriverContact = holder.textViewDriverContact;
            TextView textViewFreight = holder.textViewFreight;
            TextView textViewAdvamt = holder.textViewAdvAmt;
            TextView textViewBalamt = holder.textViewBalAmt;


            textViewTruckRegNum.setText(dataSet.get(listPosition).getRegistrationNo());

            if(((dataSet.get(listPosition).getTripID()).toString().trim()).length() > 0)
                textViewTripId.setText(dataSet.get(listPosition).getTripID());

            if((dataSet.get(listPosition).getDate()).length() > 0)
                textViewTripDate.setText(dataSet.get(listPosition).getDate());

            if((dataSet.get(listPosition).getBookedFor()).length() > 0)
                textViewPartyName.setText(dataSet.get(listPosition).getBookedFor());


            if((dataSet.get(listPosition).getDriverName()).length() > 0)
                textViewDriverName.setText(dataSet.get(listPosition).getDriverName());


            textViewTripLoc.setText(dataSet.get(listPosition).getFrom() +"  -  "+dataSet.get(listPosition).getTo());

            if((dataSet.get(listPosition).getMobile()).length() > 0)
                textViewDriverContact.setText(dataSet.get(listPosition).getMobile() );

            if((dataSet.get(listPosition).getFreightAmount()).length() > 0)
                textViewFreight.setText(dataSet.get(listPosition).getFreightAmount());

            if((dataSet.get(listPosition).getAdvance()).length() > 0)
                textViewAdvamt.setText(dataSet.get(listPosition).getAdvance());


            if((dataSet.get(listPosition).getBalance()).length() > 0)
                textViewBalamt.setText(dataSet.get(listPosition).getBalance());


        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }


    }
}
