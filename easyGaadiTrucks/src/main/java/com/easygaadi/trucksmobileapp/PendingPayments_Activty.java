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
import com.easygaadi.models.PartyVo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PendingPayments_Activty extends AppCompatActivity {

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
        setContentView(R.layout.activity_pending_payments__activty);
        context = PendingPayments_Activty.this;
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
                System.out.println(urlLink+"trucks o/p"+res);
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
                        JSONObject totalObj = result.getJSONObject("grossAmounts");
                        ((TextView) findViewById(R.id.vfreight_amt_tv)).setText(""+totalObj.getInt("grossFreight"));

                        ((TextView) findViewById(R.id.vpaid_amt_tv)).setText(""+totalObj.getInt("grossExpenses"));

                        ((TextView) findViewById(R.id.vdue_amt_tv)).setText(""+totalObj.getInt("grossDue"));


                            JSONArray partArray = result.getJSONArray("parties");
                            if(partArray.length() > 0)
                            {
                                data = new ArrayList<PartyVo>();
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    PartyVo voData = new PartyVo();
                                    voData.setId(""+partData.getString("id"));
                                    if(partData.has("totalFright")){
                                        voData.setContact(""+partData.getInt("totalFright"));
                                    }else{
                                        voData.setContact("0");
                                    }
                                    if(partData.has("totalPayment")){
                                        voData.setAccountId(""+partData.getInt("totalPayment"));
                                    }else{
                                        voData.setAccountId("0");
                                    }

                                    JSONObject pObj = partData.getJSONObject("attrs");
                                    voData.setName(pObj.getString("partyName"));
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
                } catch (Exception e) {
                    System.out.println("ex in get leads" + e.toString());
                    pDialog.dismiss();
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

            TextView textPartyName,textPartyAmount,textPartypaidAmount,textPartyDueAmount;
            LinearLayout heaserLL,containerLL;

            public MyViewHolder(View itemView) {
                super(itemView);

                this.textPartyName = (TextView) itemView.findViewById(R.id.partyname_tv);
                this.textPartyAmount = (TextView) itemView.findViewById(R.id.partyamt_on_tv);
                this.textPartypaidAmount = (TextView) itemView.findViewById(R.id.partydue_on_tv);
                this.textPartyDueAmount = (TextView) itemView.findViewById(R.id.vdue_on_tv);

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_pay_items, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final CustomAdapter.MyViewHolder holder, final int listPosition) {

            TextView textPartyName = holder.textPartyName;
            TextView textPartyAmount = holder.textPartyAmount;
            TextView textPartypaidAmount = holder.textPartypaidAmount;
            TextView textPartyDueAmount = holder.textPartyDueAmount;


            if(listPosition == 0)
            {
                //holder.heaserLL.setVisibility(View.VISIBLE);
            }

            SpannableString content = new SpannableString(dataSet.get(listPosition).getName());
            content.setSpan(new UnderlineSpan(), 0, (dataSet.get(listPosition).getName()).length(), 0);
            textPartyName.setText(content);
            textPartyAmount.setText(dataSet.get(listPosition).getContact());
            textPartypaidAmount.setText(dataSet.get(listPosition).getAccountId());

            textPartyDueAmount.setText(""+(Integer.parseInt(dataSet.get(listPosition).getContact())-Integer.parseInt(dataSet.get(listPosition).getAccountId())));


            holder.containerLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PendingSub_Activity.class);
                    intent.putExtra("Header"," Payments of "+ dataSet.get(listPosition).getName());
                    intent.putExtra("url", "party/tripsPayments" + "/" + dataSet.get(listPosition).getId());
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
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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
