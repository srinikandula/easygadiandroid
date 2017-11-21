package com.easygaadi.trucksmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.ERPsubVo;
import com.easygaadi.models.PartyVo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ERP_Eleemnt_SubActivity extends AppCompatActivity {

    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    LinearLayout containerLL;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private TextView freightTv,expensesTv;
    private static ArrayList<ERPsubVo> data;
    CustomAdapter partyadapter;
    String headerText="",urlLink="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expiry_truck_);
        context = ERP_Eleemnt_SubActivity.this;
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        res = getResources();
        progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
        containerLL = (LinearLayout) findViewById(R.id.container);
        detectCnnection = new ConnectionDetector(context);

        recyclerView = (RecyclerView) findViewById(R.id.quotes_rc);
        recyclerView.setHasFixedSize(true);


        freightTv = (TextView)findViewById(R.id.vexpenses_amt_tv);
        //expensesTv= (TextView)findViewById(R.id.);


        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Bundle bundle = getIntent().getExtras();
        headerText = bundle.getString("Header");
        urlLink = bundle.getString("url");
        ((TextView)findViewById(R.id.header_tv)).setText(headerText);
        System.out.println("riyaz"+urlLink);

        if (detectCnnection.isConnectingToInternet()) {
            new GetReacordList().execute();
        } else {
            Toast.makeText(context,res.getString(R.string.internet_str),Toast.LENGTH_LONG).show();
        }
    }

    private class GetReacordList extends AsyncTask<String, String, JSONObject> {

        //String uid, accountid, offset;

        public GetReacordList() {
            //this.uid = uid;
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
                String res = parser.erpExecuteGet(context, TruckApp.ERP_URL+""+urlLink);
                json = new JSONObject(res);
                System.out.println("trucks o/p"+res);
            } catch (Exception e) {
                Log.e("trucks DoIN EX",TruckApp.ERP_URL+"/"+urlLink+"==="+ e.toString());
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
                        if(urlLink.contains("party")){


                            JSONArray partArray = result.getJSONArray("trips");//"trips");
                            if(partArray.length() > 0)
                            {
                                if(result.has("totalRevenue")){
                                    JSONObject tRevenue = result.getJSONObject("totalRevenue");
                                    freightTv.setText(""+tRevenue.getInt("totalFreight"));
                                    ((TextView) findViewById(R.id.vrevenue_amt_tv)).setText(""+tRevenue.getInt("totalExpenses"));

                                    containerLL.setVisibility(View.VISIBLE);
                                }
                                data = new ArrayList<ERPsubVo>();
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    ERPsubVo voData = new ERPsubVo();
                                    if(partData.has("cost"))
                                    {
                                        voData.setExpense(""+partData.getString("cost"));
                                        voData.setPartyname("-");
                                        voData.setDate(getFormatDate(""+partData.getString("date")));
                                        voData.setTripid("-");
                                        voData.setFreight("-");
                                    }else{

                                        voData.setExpense("-");
                                        voData.setDate(getFormatDate(""+partData.getString("date")));
                                        voData.setTripid(partData.getString("tripId"));
                                        voData.setFreight(""+partData.getString("freightAmount"));
                                        JSONObject pObj = partData.getJSONObject("attrs");
                                        voData.setPartyname(pObj.getString("partyName"));
                                    }


                                    data.add(voData);
                                }

                                partyadapter = new CustomAdapter(data);
                                recyclerView.setAdapter(partyadapter);
                                recyclerView.invalidate();
                                pDialog.dismiss();
                            }else{
                                if(result.has("totalRevenue")){
                                    containerLL.setVisibility(View.VISIBLE);
                                    JSONObject tRevenue = result.getJSONObject("totalRevenue");
                                    ((TextView) findViewById(R.id.vexpenses_amt_tv)).setText(tRevenue.getInt("totalFreight"));
                                    ((TextView) findViewById(R.id.vrevenue_amt_tv)).setText(tRevenue.getInt("totalExpenses"));


                                }

                                Toast.makeText(context, "No records available",Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                            }
                        }else{
                            JSONArray partArray = result.getJSONArray("expenses");
                            if(partArray.length() > 0)
                            {
                                data = new ArrayList<ERPsubVo>();
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    ERPsubVo voData = new ERPsubVo();
                                    /*PartyVo voData = new PartyVo();
                                    voData.setId(""+partData.getString("_id"));
                                    voData.setContact(""+partData.getInt("cost"));
                                    voData.setUpdatedAt(getFormatDate(""+partData.getString("date")));
                                    JSONObject pObj = partData.getJSONObject("attrs");
                                    voData.setName(pObj.getString("expenseName"));
                                    data.add(voData);*/



                                    voData.setDate(getFormatDate(""+partData.getString("date")));
                                    JSONObject pObj = partData.getJSONObject("attrs");

                                    if((pObj.getString("expenseName")).equalsIgnoreCase("Toll")){
                                        voData.setTripid(""+partData.getInt("cost"));
                                    }else
                                    {
                                        voData.setTripid("-");
                                    }

                                    if ((pObj.getString("expenseName")).equalsIgnoreCase("Diesel")){
                                        voData.setFreight(""+partData.getInt("cost"));
                                    }else{
                                        voData.setFreight("-");
                                        }

                                    if ((pObj.getString("expenseName")).contains("Main")){
                                        voData.setExpense(""+partData.getInt("cost"));
                                    }else{
                                            voData.setExpense("-");
                                        }

                                    if ((voData.getTripid()).contains("-") && voData.getFreight().contains("-") && voData.getExpense().contains("-") ){
                                        voData.setMis(""+partData.getInt("cost"));
                                    }else{
                                            voData.setMis("-");
                                        }
                                    data.add(voData);

                                }

                                partyadapter = new CustomAdapter(data);
                                recyclerView.setAdapter(partyadapter);
                                recyclerView.invalidate();
                                pDialog.dismiss();
                            }else{
                                Toast.makeText(context, "No records available",Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
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

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder>  {

        private ArrayList<ERPsubVo> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textPartyDate,textPartyTripID,textPartyName,textPartyFAmount,textPartyEAmount,
                    textHeaderDate,textHeaderTripID,textHeaderPartyName,textHeaderFAmount,textHeaderEAmount;
            LinearLayout heaserLL;

            public MyViewHolder(View itemView) {
                super(itemView);



                this.textPartyDate = (TextView) itemView.findViewById(R.id.vdate_tv);
                this.textPartyTripID = (TextView) itemView.findViewById(R.id.vtripID_tv);
                this.textPartyName = (TextView) itemView.findViewById(R.id.vparty_tv);
                this.textPartyFAmount = (TextView) itemView.findViewById(R.id.vfreight_amt_tv);
                this.textPartyEAmount = (TextView) itemView.findViewById(R.id.vexpenceses_amt_tv);

                this.textHeaderDate = (TextView) itemView.findViewById(R.id.date_tv);
                this.textHeaderTripID = (TextView) itemView.findViewById(R.id.tripID_tv);
                this.textHeaderPartyName = (TextView) itemView.findViewById(R.id.party_tv);
                this.textHeaderFAmount = (TextView) itemView.findViewById(R.id.freight_amt_tv);
                this.textHeaderEAmount = (TextView) itemView.findViewById(R.id.expenceses_amt_tv);



                this.heaserLL = (LinearLayout) itemView.findViewById(R.id.expiry_header);
            }
        }

        public CustomAdapter(ArrayList<ERPsubVo> data) {
            this.dataSet = data;
        }

        @Override
        public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.erp_subrevenue_items, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final CustomAdapter.MyViewHolder holder, final int listPosition) {

            TextView textPartyDate = holder.textPartyDate;
            TextView textPartyTripID = holder.textPartyTripID;
            TextView textPartyName = holder.textPartyName;
            TextView textPartyFAmount = holder.textPartyFAmount;
            TextView textPartyEAmount = holder.textPartyEAmount;
            //textHeaderDate,textHeaderTripID,textHeaderPartyName,textHeaderFAmount,textHeaderEAmount
            TextView textHeaderDate = holder.textHeaderDate;
            TextView textHeaderTripID = holder.textHeaderTripID;
            TextView textHeaderPartyName = holder.textHeaderPartyName;
            TextView textHeaderFAmount = holder.textHeaderFAmount;
            TextView textHeaderEAmount = holder.textHeaderEAmount;



            if(listPosition == 0)
            {
                holder.heaserLL.setVisibility(View.VISIBLE);
                if(urlLink.contains("party")) {//trips

                }else{
                    textHeaderDate.setText("Date");
                    textHeaderTripID.setText("Diesel");
                    textHeaderPartyName.setText("Toll");
                    textHeaderFAmount.setText("Maint...");
                    textHeaderEAmount.setText("Misc....");
                }

            }
            if(urlLink.contains("party")) {//trips
                textPartyName.setText(dataSet.get(listPosition).getPartyname());
                textPartyDate.setText(dataSet.get(listPosition).getDate());
                textPartyTripID.setText(dataSet.get(listPosition).getTripid());
                textPartyFAmount.setText(dataSet.get(listPosition).getFreight());
                textPartyEAmount.setText(dataSet.get(listPosition).getExpense());
            }else {


                textPartyName.setText(dataSet.get(listPosition).getTripid());
                textPartyDate.setText(dataSet.get(listPosition).getDate());
                textPartyTripID.setText(dataSet.get(listPosition).getFreight());
                textPartyFAmount.setText(dataSet.get(listPosition).getExpense());
                textPartyEAmount.setText(dataSet.get(listPosition).getMis());
            }
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }


    }

    public String getFormatDate(String fdate){

        Date date;
        String diff = "";

        DateFormat dateFormat,formatter;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
            date = dateFormat.parse(fdate);
            formatter = new SimpleDateFormat("dd MMM yy"); //If you need time just put specific format for time like 'HH:mm:ss'
            diff = formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("err--"+e.getMessage());
        }

        return diff;
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

}
