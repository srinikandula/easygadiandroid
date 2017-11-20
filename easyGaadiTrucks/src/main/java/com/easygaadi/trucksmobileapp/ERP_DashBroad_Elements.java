package com.easygaadi.trucksmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.ExpiryDateVo;
import com.easygaadi.models.PartyVo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ERP_DashBroad_Elements extends AppCompatActivity {

    Context context;
    Resources res;
    JSONParser parser;
    private ConnectionDetector detectCnnection;
    FrameLayout progressFrame;
    ProgressDialog pDialog;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private TextView headerTv;
    private static ArrayList<PartyVo> data;
    CustomAdapter partyadapter;
    String headerText="",urlLink="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expiry_truck_);
        context = ERP_DashBroad_Elements.this;
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

        Bundle bundle = getIntent().getExtras();
        headerText = bundle.getString("Header");
        urlLink = bundle.getString("url");
        ((TextView)findViewById(R.id.header_tv)).setText(headerText);


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

                        if(urlLink.contains("trips")){
                            JSONArray partArray = result.getJSONArray("revenue");
                            if(partArray.length() > 0)
                            {
                                data = new ArrayList<PartyVo>();
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    PartyVo voData = new PartyVo();
                                    voData.setId(""+partData.getString("registrationNo"));
                                    voData.setContact(""+partData.getInt("totalFreight"));
                                    voData.setCity(""+partData.getInt("totalExpense"));
                                    voData.setCreatedAt(""+partData.getInt("totalRevenue"));
                                    JSONObject pObj = partData.getJSONObject("attrs");
                                    voData.setName(pObj.getString("truckName"));
                                    data.add(voData);
                                }

                                partyadapter = new CustomAdapter(data,context);
                                recyclerView.setAdapter(partyadapter);
                                recyclerView.invalidate();
                                pDialog.dismiss();
                            }else{
                                Toast.makeText(context, "No records available",Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                            }
                        }else{
                            JSONArray partArray = result.getJSONArray("expenses");
                            if(partArray.length() > 0)
                            {
                                data = new ArrayList<PartyVo>();
                                for (int i = 0; i < partArray.length(); i++) {


                                    JSONObject partData = partArray.getJSONObject(i);

                                    JSONArray innerArr = partData.getJSONArray("exps");

                                    PartyVo voData = new PartyVo();
                                    JSONObject innerOBj = innerArr.getJSONObject(0);

                                    voData.setId(""+partData.getString("id"));
                                    voData.setName(""+partData.getString("regNumber"));
                                    voData.setContact(""+innerOBj.getInt("dieselExpense"));
                                    voData.setCity(""+innerOBj.getInt("tollExpense"));
                                    voData.setCreatedAt(""+innerOBj.getInt("misc"));
                                    data.add(voData);
                                }

                                partyadapter = new CustomAdapter(data,context);
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

        private ArrayList<PartyVo> dataSet;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textPartyVName,textPartyFAmount,textPartyEAmount,textPartyRAmount,textHeaderVName,textHeaderFAmount,textHeaderEAmount,textHeaderRAmount;
            LinearLayout heaserLL,containerLL;

            public MyViewHolder(View itemView) {
                super(itemView);

                //this.textHeaderName = (TextView) itemView.findViewById(R.id.vehicle_tv);
                //this.textHeaderAmount = (TextView) itemView.findViewById(R.id.exp_on_tv);
                this.textPartyVName = (TextView) itemView.findViewById(R.id.vehiclenum_tv);
                this.textPartyFAmount = (TextView) itemView.findViewById(R.id.vfreight_amt_tv);
                this.textPartyEAmount = (TextView) itemView.findViewById(R.id.vexpenses_amt_tv);
                this.textPartyRAmount = (TextView) itemView.findViewById(R.id.vrevenue_amt_tv);

                this.textHeaderVName = (TextView) itemView.findViewById(R.id.vehicle_tv);
                this.textHeaderFAmount = (TextView) itemView.findViewById(R.id.freight_amt_tv);
                this.textHeaderEAmount = (TextView) itemView.findViewById(R.id.expenceses_amt_tv);
                this.textHeaderRAmount = (TextView) itemView.findViewById(R.id.revene_amt_tv);


                this.heaserLL = (LinearLayout) itemView.findViewById(R.id.expiry_header);
                this.containerLL = (LinearLayout) itemView.findViewById(R.id.container);
            }
        }

        public CustomAdapter(ArrayList<PartyVo> data,Context context) {
            this.dataSet = data;
            this.context = context;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.erp_revenue_items, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final CustomAdapter.MyViewHolder holder, final int listPosition) {

            TextView textPartyName = holder.textPartyVName;
            TextView textPartyFAmount = holder.textPartyFAmount;
            TextView textPartyEAmount = holder.textPartyEAmount;
            TextView textPartyRAmount = holder.textPartyRAmount;
            //textHeaderVName,textHeaderFAmount,textHeaderEAmount,textHeaderRAmount
            TextView textHeaderVName = holder.textHeaderVName;
            TextView textHeaderFAmount = holder.textHeaderFAmount;
            TextView textHeaderEAmount = holder.textHeaderEAmount;
            TextView textHeaderRAmount = holder.textHeaderRAmount;
            if(listPosition == 0)
            {
                holder.heaserLL.setVisibility(View.VISIBLE);

                if(urlLink.contains("trips")) {
                    //textHeaderName.setText("Party Name");
                    //textHeaderAmount.setText("Amount");
                }else{
                    //textHeaderVName,textHeaderFAmount,textHeaderEAmount,textHeaderRAmount
                    textHeaderVName.setText("V.No");
                    textHeaderFAmount.setText("Diesel");
                    textHeaderEAmount.setText("Toll");
                    textHeaderEAmount.setText("Maitenance");
                    textHeaderRAmount.setText("misce...");
                }
            }

            SpannableString content = new SpannableString(dataSet.get(listPosition).getName());
            content.setSpan(new UnderlineSpan(), 0, (dataSet.get(listPosition).getName()).length(), 0);
            textPartyName.setText(content);
            textPartyFAmount.setText(dataSet.get(listPosition).getContact());
            textPartyEAmount.setText(dataSet.get(listPosition).getCity());
            textPartyRAmount.setText(dataSet.get(listPosition).getCreatedAt());

            holder.containerLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ERP_Eleemnt_SubActivity.class);

                    if(urlLink.contains("trips")) {
                        intent.putExtra("Header","Revenue -"+ dataSet.get(listPosition).getName());
                        intent.putExtra("url", "party/vehiclePayments" + "/" + dataSet.get(listPosition).getId());
                       // intent.putExtra("url", "trips/find/tripsByParty" + "/" + dataSet.get(listPosition).getId());
                    }else{
                        intent.putExtra("Header"," Expenses for "+ dataSet.get(listPosition).getName());
                        intent.putExtra("url", "expense/vehicleExpense" + "/" + dataSet.get(listPosition).getId());
                    }

                    startActivity(intent);
                }
            });


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
            formatter = new SimpleDateFormat("dd-MM-yyyy"); //If you need time just put specific format for time like 'HH:mm:ss'
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
