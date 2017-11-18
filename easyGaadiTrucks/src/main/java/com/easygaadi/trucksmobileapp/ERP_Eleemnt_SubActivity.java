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
        context = ERP_Eleemnt_SubActivity.this;
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
                            JSONArray partArray = result.getJSONArray("trips");
                            if(partArray.length() > 0)
                            {
                                data = new ArrayList<PartyVo>();
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    PartyVo voData = new PartyVo();
                                    voData.setId(""+partData.getString("_id"));
                                    voData.setContact(""+partData.getInt("freightAmount"));
                                    voData.setUpdatedAt(getFormatDate(""+partData.getString("date")));
                                    JSONObject pObj = partData.getJSONObject("attrs");
                                    voData.setName(pObj.getString("truckName"));
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
                        }else{
                            JSONArray partArray = result.getJSONArray("expenses");
                            if(partArray.length() > 0)
                            {
                                data = new ArrayList<PartyVo>();
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    PartyVo voData = new PartyVo();
                                    voData.setId(""+partData.getString("_id"));
                                    voData.setContact(""+partData.getInt("cost"));
                                    voData.setUpdatedAt(getFormatDate(""+partData.getString("date")));
                                    JSONObject pObj = partData.getJSONObject("attrs");
                                    voData.setName(pObj.getString("expenseName"));
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

        private ArrayList<PartyVo> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textPartyName,textPartyVechile,textPartyAmount,textHeaderName,textHeaderAmount,textHeaderVechile;
            LinearLayout heaserLL;

            public MyViewHolder(View itemView) {
                super(itemView);

                this.textHeaderName = (TextView) itemView.findViewById(R.id.vehicle_tv);
                this.textHeaderAmount = (TextView) itemView.findViewById(R.id.exp_on_tv);
                this.textHeaderVechile = (TextView) itemView.findViewById(R.id.amount_tv);

                this.textPartyName = (TextView) itemView.findViewById(R.id.vehiclenum_tv);
                this.textPartyVechile = (TextView) itemView.findViewById(R.id.vexp_on_tv);
                this.textPartyAmount = (TextView) itemView.findViewById(R.id.vamunot_tv);

                this.heaserLL = (LinearLayout) itemView.findViewById(R.id.expiry_header);
            }
        }

        public CustomAdapter(ArrayList<PartyVo> data) {
            this.dataSet = data;
        }

        @Override
        public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expiry_sub_items, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final CustomAdapter.MyViewHolder holder, final int listPosition) {

            TextView textPartyName = holder.textPartyName;
            TextView textPartyVechile = holder.textPartyVechile;
            TextView textPartyAmount = holder.textPartyAmount;

            TextView textHeaderName = holder.textHeaderName;
            TextView textHeaderVechile = holder.textHeaderVechile;
            TextView textHeaderAmount = holder.textHeaderAmount;
            if(listPosition == 0)
            {
                holder.heaserLL.setVisibility(View.VISIBLE);
                if(urlLink.contains("trips")) {
                    textHeaderName.setText("Date");
                    textHeaderVechile.setText("Amount");
                    textHeaderAmount.setText("V.No");
                }else{
                    textHeaderName.setText("Date");
                    textHeaderVechile.setText("Amount");
                    textHeaderAmount.setText("Type");
                }

            }

            textPartyName.setText(dataSet.get(listPosition).getUpdatedAt());
            textPartyAmount.setText(dataSet.get(listPosition).getContact());
            textPartyVechile.setText(dataSet.get(listPosition).getName());
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
