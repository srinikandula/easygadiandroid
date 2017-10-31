package com.easygaadi.trucksmobileapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReportActivity extends AppCompatActivity {
    private RecyclerView report_rv;
    private Context mContext;
    private ConnectionDetector detectCnnection;
    private SharedPreferences sharedPreferences;
    private JSONArray reportsArray;
    private ReportAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Button download_report;
    private String fromDate,toDate,type;
    private LinearLayout topLayout1,topLayout;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mContext = this;
        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        detectCnnection = new ConnectionDetector(mContext);
        reportsArray = new JSONArray();
        topLayout1 = (LinearLayout)findViewById(R.id.title_layout1);
        topLayout = (LinearLayout)findViewById(R.id.title_layout);
        download_report = (Button)findViewById(R.id.download_report);
        report_rv = (RecyclerView)findViewById(R.id.report_rv);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        adView = (AdView)findViewById(R.id.adView);
        intializeBannerAd();

        if(getIntent().hasExtra("data")){
            try {
                fromDate = getIntent().getStringExtra("fromDate");
                toDate = getIntent().getStringExtra("toDate");
                if(getIntent().hasExtra("cardNo")){
                    type = "Toll";
                    topLayout.setVisibility(View.VISIBLE);
                    setAdapter(getIntent().getStringExtra("data"),"Toll");
                    setupActionBar(getIntent().getStringExtra("cardNo"));
                }else{
                    type = "Fuel";
                    topLayout1.setVisibility(View.VISIBLE);
                    setAdapter(getIntent().getStringExtra("data"),"Fuel");
                    setupActionBar("Fuel Reports");
                }

            }catch (Exception ex){

            }
        }
        download_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download_report();
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        TruckApp.getInstance().trackScreenView("Toll/Fuel Report Screen");
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (adView != null)
        { adView.destroy(); }
        super.onDestroy();
    }


    private void intializeBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    private void download_report() {
        if(reportsArray!=null && reportsArray.length()>0){
            StringBuilder builder = new StringBuilder();
            try{
                for (int i =0;i<reportsArray.length();i++){
                    JSONObject report = reportsArray.getJSONObject(i);
                    if(type.equalsIgnoreCase("Toll")) {
                        builder.append("Location:").append(report.getString("location")).append("     ").
                                append("Amount:").append(report.getString("amount")).append("     ")
                                .append("time:").append(report.getString("time")).append("\n\n");
                    }else {
                        builder.append("Card:").append(report.getString("card_no")).append("     ").
                                append("Usage(Rs.):").append(report.getString("usage")).append("\n\n");
                    }
                }
            }catch (Exception e){

            }
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, "");
            email.putExtra(Intent.EXTRA_SUBJECT, "EasyGaadi "+type+" Report from "+fromDate
                    +" to "+toDate);
            email.putExtra(Intent.EXTRA_TEXT, builder.toString());

            //need this to prompts email client only
            email.setType("message/rfc822");

            startActivity(Intent.createChooser(email, "Choose an Email client :"));
        }else{
            Toast.makeText(mContext,"No reports to download",Toast.LENGTH_LONG).show();
        }
    }

    private void setAdapter(String data,String type) throws Exception{
        reportsArray = new JSONArray(data);
        adapter = new ReportAdapter(reportsArray,type);
        report_rv.setLayoutManager(mLayoutManager);
        new SimpleDividerItemDecoration(getResources());
        report_rv.setAdapter(adapter);
    }

    private void setupActionBar(String cardNo) {
        SpannableString s = new SpannableString(cardNo);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, getString(R.string.distance_report).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(getActionBar()!=null) {
            getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_clr)));
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setHomeButtonEnabled(false);
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setTitle(s);
        }else if(getSupportActionBar()!=null){
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_clr)));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(s);
        }
    }


    public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder>{

        private JSONArray reportsArray;
        private String type;

        public ReportAdapter(JSONArray reportsArray,String type) {
            this.reportsArray = reportsArray;
            this.type = type;
        }


        @Override
        public ReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = null;
            if(type.equalsIgnoreCase("Toll")){
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item,parent,false);
            }else{
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fuel_report_item,parent,false);
            }
            return new ReportViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(ReportViewHolder holder, int position) {
            try{
                JSONObject reportData = reportsArray.getJSONObject(position);
                if(type.equalsIgnoreCase("Toll")) {
                    holder.location_tv.setText(reportData.getString("location"));
                    holder.amount_tv.setText(reportData.getString("amount"));
                    holder.date_tv.setText(reportData.getString("time"));
                }else {
                    holder.card_no_tv.setText(reportData.getString("card_no"));
                    holder.usage_tv.setText(reportData.getString("usage"));
                }
            }catch (Exception e){

            }
        }

        @Override
        public int getItemCount() {
            return reportsArray.length();
        }

        public class ReportViewHolder extends  RecyclerView.ViewHolder{
            public TextView location_tv,amount_tv,date_tv,card_no_tv,usage_tv;

            public ReportViewHolder(View itemView) {
                super(itemView);
                if(type.equalsIgnoreCase("Toll")){
                    location_tv = (TextView)itemView.findViewById(R.id.location_tv);
                    amount_tv = (TextView)itemView.findViewById(R.id.amount_tv);
                    date_tv = (TextView)itemView.findViewById(R.id.date_tv);
                }else{
                    card_no_tv = (TextView)itemView.findViewById(R.id.card_tv);
                    usage_tv = (TextView)itemView.findViewById(R.id.usage_tv);
                }
            }
        }

    }
}
