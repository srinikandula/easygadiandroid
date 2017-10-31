package com.easygaadi.trucksmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

public class DashboardActivity extends AppCompatActivity {

    private CheckBox servicing_cb,e_oil_cb,gresing_cb,tyres_cb,national_permit_cb,rc_cb,pollution_cb,fitness_cb,insurance_cb;
    private RelativeLayout servicing_lay,gresing_lay,e_oil_lay,tyres_lay,national_permit_lay,rc_lay,pollution_lay,fitness_lay,insurance_lay;
    private RecyclerView servicing_rv,gresing_rv,e_oil_rv,tyres_rv,national_permit_rv,rc_rv,pollution_rv,fitness_rv,insurance_rv;
    Context mContext;
    private ProgressDialog pDialog;
    private ConnectionDetector detectCnnection;
    private SharedPreferences sharedPreferences;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mContext = this;

        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        detectCnnection = new ConnectionDetector(mContext);
        pDialog  = new ProgressDialog(this);
        pDialog.setCancelable(false);

        servicing_cb = (CheckBox)findViewById(R.id.servicing_expiry_cb);
        gresing_cb = (CheckBox)findViewById(R.id.greasing_expiry_cb);
        e_oil_cb = (CheckBox)findViewById(R.id.e_oil_expiry_cb);
        tyres_cb = (CheckBox)findViewById(R.id.tyres_expiry_cb);
        national_permit_cb = (CheckBox)findViewById(R.id.nat_permit_expiry_cb);
        rc_cb = (CheckBox)findViewById(R.id.rc_expiry_cb);
        pollution_cb = (CheckBox)findViewById(R.id.pollution_expiry_cb);
        fitness_cb = (CheckBox)findViewById(R.id.fitness_expiry_cb);
        insurance_cb = (CheckBox)findViewById(R.id.insurance_expiry_cb);

        gresing_rv = (RecyclerView) findViewById(R.id.gresing_expiry_rc);
        servicing_rv = (RecyclerView) findViewById(R.id.servicing_expiry_rc);
        e_oil_rv = (RecyclerView)findViewById(R.id.e_oil_expiry_rc);
        tyres_rv = (RecyclerView)findViewById(R.id.tyres_expiry_rc);
        national_permit_rv = (RecyclerView)findViewById(R.id.nat_permit_expiry_rc);
        rc_rv = (RecyclerView)findViewById(R.id.rc_expiry_rc);
        pollution_rv = (RecyclerView)findViewById(R.id.pollution_expiry_rc);
        fitness_rv = (RecyclerView)findViewById(R.id.fitness_expiry_rc);
        insurance_rv = (RecyclerView)findViewById(R.id.insurance_expiry_rc);

        gresing_lay = (RelativeLayout) findViewById(R.id.gresing_expiry_lay);
        servicing_lay = (RelativeLayout) findViewById(R.id.servicing_expiry_lay);
        e_oil_lay = (RelativeLayout)findViewById(R.id.e_oil_expiry_lay);
        tyres_lay = (RelativeLayout)findViewById(R.id.tyres_expiry_lay);
        national_permit_lay = (RelativeLayout)findViewById(R.id.nat_permit_expiry_lay);
        rc_lay = (RelativeLayout)findViewById(R.id.rc_expiry_lay);
        pollution_lay= (RelativeLayout)findViewById(R.id.pollution_expiry_lay);
        fitness_lay = (RelativeLayout)findViewById(R.id.fitness_expiry_lay);
        insurance_lay = (RelativeLayout)findViewById(R.id.insurance_expiry_lay);

        adView = (AdView)findViewById(R.id.adView);
        //intializeBannerAd();

        servicing_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    servicing_lay.setVisibility(View.VISIBLE);
                } else {
                    servicing_lay.setVisibility(View.GONE);
                }

            }
        });

        e_oil_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    e_oil_lay.setVisibility(View.VISIBLE);
                } else {
                    e_oil_lay.setVisibility(View.GONE);
                }

            }
        });

        gresing_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    gresing_lay.setVisibility(View.VISIBLE);
                } else {
                    gresing_lay.setVisibility(View.GONE);
                }

            }
        });

        tyres_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tyres_lay.setVisibility(View.VISIBLE);
                } else {
                    tyres_lay.setVisibility(View.GONE);
                }

            }
        });

        national_permit_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    national_permit_lay.setVisibility(View.VISIBLE);
                } else {
                    national_permit_lay.setVisibility(View.GONE);
                }

            }
        });

        rc_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rc_lay.setVisibility(View.VISIBLE);
                } else {
                    rc_lay.setVisibility(View.GONE);
                }

            }
        });

        pollution_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    pollution_lay.setVisibility(View.VISIBLE);
                } else {
                    pollution_lay.setVisibility(View.GONE);
                }

            }
        });

        fitness_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    fitness_lay.setVisibility(View.VISIBLE);
                } else {
                    fitness_lay.setVisibility(View.GONE);
                }

            }
        });

        insurance_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    insurance_lay.setVisibility(View.VISIBLE);
                } else {
                    insurance_lay.setVisibility(View.GONE);
                }

            }
        });
        if(detectCnnection.isConnectingToInternet()){
            new GetDashboard(sharedPreferences.getString("accountID",
                    "no accountid "),mContext).execute();
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
            finish();
        }
    }



    private void intializeBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    private class ServicingAdapter extends RecyclerView.Adapter<ServicingAdapter.ServicingViewHolder>{

        private JSONArray list;

        public ServicingAdapter(JSONArray list) {
            this.list = list;
        }

        @Override
        public ServicingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.servicing_expiry_item, parent, false);
            return new ServicingViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ServicingViewHolder holder, int position) {
            try{
                JSONObject itemJson= list.getJSONObject(position);
                if(itemJson.has("deviceID")){
                    holder.vehicle_no.setText(itemJson.getString("deviceID"));
                    holder.vehicle_no.setTag(itemJson.getString("deviceID"));
                }
                if(itemJson.has("serviceOn")){
                    holder.service_on.setText(itemJson.getString("serviceOn"));
                }
                if(itemJson.has("expiryOn")){
                    holder.service_on.setText(itemJson.getString("expiryOn"));
                }
                if(itemJson.has("currentKm")){
                    holder.current_km.setText(String.valueOf(itemJson.getLong("currentKm")));
                }
                if(itemJson.has("expiryKm")){
                    holder.exp_km.setText(String.valueOf(itemJson.getLong("expiryKm")));
                }
                int textColor;
                if(itemJson.has("expired") && itemJson.getInt("expired")==1){
                    holder.title_lay.setBackgroundResource(R.drawable.title_bg_high);
                    textColor = getResources().getColor(android.R.color.white);
                }else {
                    holder.title_lay.setBackgroundResource(R.drawable.title_bg);
                    textColor = getResources().getColor(android.R.color.black);
                }
                holder.vehicle_no.setTextColor(textColor);
                holder.service_on.setTextColor(textColor);
                holder.current_km.setTextColor(textColor);
                holder.exp_km.setTextColor(textColor);

                holder.vehicle_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent editTruck = new Intent(mContext,TruckDetailsActivity.class);
                        editTruck.putExtra("deviceID",view.getTag().toString());
                        startActivity(editTruck);
                    }
                });
            }catch (Exception e){

            }
        }

        @Override
        public int getItemCount() {
            return list.length();
        }

        public class ServicingViewHolder extends RecyclerView.ViewHolder{

            public TextView vehicle_no,service_on,current_km,exp_km;
            public LinearLayout title_lay;
            public ServicingViewHolder(View itemView) {
                super(itemView);
                vehicle_no=(TextView)itemView.findViewById(R.id.vehicle_tv);
                service_on=(TextView)itemView.findViewById(R.id.service_on_tv);
                current_km=(TextView)itemView.findViewById(R.id.current_km_tv);
                exp_km=(TextView)itemView.findViewById(R.id.exp_km_tv);
                title_lay = (LinearLayout)itemView.findViewById(R.id.title_servicing);
            }
        }
    }

    private class TyreAdapter extends RecyclerView.Adapter<TyreAdapter.TyreViewHolder>{

        private JSONArray list;

        public TyreAdapter(JSONArray list) {
            this.list = list;
        }


        @Override
        public TyreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tyre_expiry_item, parent, false);
            return new TyreViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TyreViewHolder holder, int position) {
            try{
                JSONObject itemJson= list.getJSONObject(position);
                if(itemJson.has("deviceID")){
                    holder.vehicle_no.setText(itemJson.getString("deviceID"));
                    holder.vehicle_no.setTag(itemJson.getString("deviceID"));
                }
                /*if(itemJson.has("serviceOn")){
                    holder.exp_on.setText(itemJson.getString("serviceOn"));
                }*/
                if(itemJson.has("level")){
                    holder.level.setText(itemJson.getString("level"));
                }
                if(itemJson.has("position")){
                    holder.position.setText(itemJson.getString("position"));
                }
                if(itemJson.has("expiryOn")){
                    holder.exp_on.setText(itemJson.getString("expiryOn"));
                }

                if(itemJson.has("currentKm")){
                    holder.current_km.setText(String.valueOf(itemJson.getLong("currentKm")));
                }
                if(itemJson.has("expiryKm")){
                    holder.exp_km.setText(String.valueOf(itemJson.getLong("expiryKm")));
                }
                int textColor;
                if(itemJson.has("expired") && itemJson.getInt("expired")==1){
                    holder.title_lay.setBackgroundResource(R.drawable.title_bg_high);
                    textColor = getResources().getColor(android.R.color.white);
                }else {
                    holder.title_lay.setBackgroundResource(R.drawable.title_bg);
                    textColor = getResources().getColor(android.R.color.black);
                }
                holder.vehicle_no.setTextColor(textColor);
                holder.exp_on.setTextColor(textColor);
                holder.position.setTextColor(textColor);
                holder.level.setTextColor(textColor);
                holder.current_km.setTextColor(textColor);
                holder.exp_km.setTextColor(textColor);

                holder.vehicle_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent editTruck = new Intent(mContext,TruckDetailsActivity.class);
                        editTruck.putExtra("deviceID",view.getTag().toString());
                        startActivity(editTruck);
                    }
                });
            }catch (Exception e){

            }
        }

        @Override
        public int getItemCount() {
            return list.length();
        }

        public class TyreViewHolder extends RecyclerView.ViewHolder{

            public TextView vehicle_no,level,position,exp_on,current_km,exp_km;
            public LinearLayout title_lay;
            public TyreViewHolder(View itemView) {
                super(itemView);
                vehicle_no=(TextView)itemView.findViewById(R.id.vehicle_tv);
                level=(TextView)itemView.findViewById(R.id.level_tv);
                position=(TextView)itemView.findViewById(R.id.position_tv);
                exp_on=(TextView)itemView.findViewById(R.id.exp_on_tv);
                current_km=(TextView)itemView.findViewById(R.id.current_km_tv);
                exp_km=(TextView)itemView.findViewById(R.id.exp_km_tv);
                title_lay = (LinearLayout)itemView.findViewById(R.id.title_tyres);
            }
        }
    }



    private class RCAdapter extends RecyclerView.Adapter<RCAdapter.RCViewHolder>{

        private JSONArray list;

        public RCAdapter(JSONArray list) {
            this.list = list;
        }


        @Override
        public RCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rc_expiry_item, parent, false);
            return new RCViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RCViewHolder holder, int position) {
            try{
                JSONObject itemJson= list.getJSONObject(position);
                if(itemJson.has("deviceID")){
                    holder.vehicle_no.setText(itemJson.getString("deviceID"));
                    holder.vehicle_no.setTag(itemJson.getString("deviceID"));
                }
                if(itemJson.has("expiryOn")){
                    holder.exp_on_tv.setText(itemJson.getString("expiryOn"));
                }
                int textColor;
                if(itemJson.has("expired") && itemJson.getInt("expired")==1){
                    holder.title_lay.setBackgroundResource(R.drawable.title_bg_high);
                    textColor = getResources().getColor(android.R.color.white);
                }else {
                    holder.title_lay.setBackgroundResource(R.drawable.title_bg);
                    textColor = getResources().getColor(android.R.color.black);
                }
                holder.vehicle_no.setTextColor(textColor);
                holder.exp_on_tv.setTextColor(textColor);

                holder.vehicle_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent editTruck = new Intent(mContext,TruckDetailsActivity.class);
                        editTruck.putExtra("deviceID",view.getTag().toString());
                        startActivity(editTruck);
                    }
                });
            }catch (Exception e){

            }
        }

        @Override
        public int getItemCount() {
            return list.length();
        }

        public class RCViewHolder extends RecyclerView.ViewHolder{

            public TextView vehicle_no,exp_on_tv;
            public LinearLayout title_lay;
            public RCViewHolder(View itemView) {
                super(itemView);
                vehicle_no=(TextView)itemView.findViewById(R.id.vehicle_tv);
                exp_on_tv=(TextView)itemView.findViewById(R.id.exp_on_tv);
                title_lay = (LinearLayout)itemView.findViewById(R.id.title_rc);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TruckApp.getInstance().trackScreenView("Dashboard Screen");
        /*if (adView != null) {
            adView.resume();
        }*/
    }


    @Override
    protected void onPause() {
        /*if (adView != null) {
            adView.pause();
        }*/
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        /*if (adView != null)
        { adView.destroy(); }*/
        super.onDestroy();
    }


    public class GetDashboard extends AsyncTask<Void,Void,JSONObject> {

        private String accountid;
        private Context mContex;
        private JSONParser parser;
        public GetDashboard( String accountid, Context mContex) {
            this.accountid = accountid;
            this.mContex = mContex;
            parser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Fecthing the dashboard...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject json = null;
            try {
                StringBuilder builder= new StringBuilder();
                builder.append("accountID=").append(URLEncoder.encode(accountid, "UTF-8"));
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
                String res = parser.excutePost(TruckApp.dashboardURL,builder.toString());
                json = new JSONObject(res);
            }catch(Exception e){
                Log.e("Login DoIN EX",e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            TruckApp.checkPDialog(pDialog);
            if(result!=null){
                try {
                    if (result.has("status") && 1 == result.getInt("status")){
                        if(result.has("data")){
                            JSONObject resultJson = result.getJSONObject("data");
                            if(!resultJson.has("servicingExpiry")){
                                servicing_cb.setVisibility(View.GONE);
                            }else{
                                ServicingAdapter serviceAdapter = new ServicingAdapter(resultJson.getJSONArray("servicingExpiry"));
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                servicing_rv.setLayoutManager(mLayoutManager);
                                servicing_rv.setItemAnimator(new DefaultItemAnimator());
                                servicing_rv.setAdapter(serviceAdapter);
                            }
                            if(!resultJson.has("eOilExpiry")){
                                e_oil_cb.setVisibility(View.GONE);
                            }else{
                                ServicingAdapter eOilAdapter = new ServicingAdapter(resultJson.getJSONArray("eOilExpiry"));
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                e_oil_rv.setLayoutManager(mLayoutManager);
                                e_oil_rv.setItemAnimator(new DefaultItemAnimator());
                                e_oil_rv.setAdapter(eOilAdapter);
                            }

                            if(!resultJson.has("greasingExpiry")){
                                gresing_cb.setVisibility(View.GONE);
                            }else{
                                ServicingAdapter eOilAdapter = new ServicingAdapter(resultJson.getJSONArray("greasingExpiry"));
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                gresing_rv.setLayoutManager(mLayoutManager);
                                gresing_rv.setItemAnimator(new DefaultItemAnimator());
                                gresing_rv.setAdapter(eOilAdapter);
                            }

                            if(!resultJson.has("tyresExpiry")){
                                tyres_cb.setVisibility(View.GONE);
                            }else {
                                TyreAdapter tyreAdapter = new TyreAdapter(resultJson.getJSONArray("tyresExpiry"));
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                tyres_rv.setLayoutManager(mLayoutManager);
                                tyres_rv.setItemAnimator(new DefaultItemAnimator());
                                tyres_rv.setAdapter(tyreAdapter);
                            }
                            if(!resultJson.has("nPermitExpiry")){
                                national_permit_cb.setVisibility(View.GONE);
                            }else {
                                RCAdapter nPermitAdapter = new RCAdapter(resultJson.getJSONArray("nPermitExpiry"));
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                national_permit_rv.setLayoutManager(mLayoutManager);
                                national_permit_rv.setItemAnimator(new DefaultItemAnimator());
                                national_permit_rv.setAdapter(nPermitAdapter);
                            }
                            if(!resultJson.has("rCExpiry")){
                                rc_cb.setVisibility(View.GONE);
                            }else{
                                RCAdapter rcAdapter = new RCAdapter(resultJson.getJSONArray("rCExpiry"));
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                rc_rv.setLayoutManager(mLayoutManager);
                                rc_rv.setItemAnimator(new DefaultItemAnimator());
                                rc_rv.setAdapter(rcAdapter);
                            }
                            if(!resultJson.has("pollutionExpiry")){
                                pollution_cb.setVisibility(View.GONE);
                            }else {
                                RCAdapter pollutionAdapter = new RCAdapter(resultJson.getJSONArray("pollutionExpiry"));
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                pollution_rv.setLayoutManager(mLayoutManager);
                                pollution_rv.setItemAnimator(new DefaultItemAnimator());
                                pollution_rv.setAdapter(pollutionAdapter);
                            }
                            if(!resultJson.has("fitnessExpiry")){
                                fitness_cb.setVisibility(View.GONE);
                            }else {
                                RCAdapter fitnessAdapter = new RCAdapter(resultJson.getJSONArray("fitnessExpiry"));
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                fitness_rv.setLayoutManager(mLayoutManager);
                                fitness_rv.setItemAnimator(new DefaultItemAnimator());
                                fitness_rv.setAdapter(fitnessAdapter);
                            }
                            if(!resultJson.has("insuranceExpiry")){
                                insurance_cb.setVisibility(View.GONE);
                            }else {
                                RCAdapter insuranceAdapter = new RCAdapter(resultJson.getJSONArray("insuranceExpiry"));
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                insurance_rv.setLayoutManager(mLayoutManager);
                                insurance_rv.setItemAnimator(new DefaultItemAnimator());
                                insurance_rv.setAdapter(insuranceAdapter);
                            }
                        }else{
                            hideAllViews();
                        }
                    }else  if (result.has("status") && 2 == result.getInt("status")){
                        //TruckApp.logoutAction(DashboardActivity.this);
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name), MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(Constants.FUEL_USERNAME_KEY,"").commit();
                        editor.putString(Constants.FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(Constants.FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(Constants.TOLL_USERNAME_KEY,"").commit();
                        editor.putString(Constants.TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(Constants.TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(mContext,
                                LoginActivity.class));
                        finish();
                    }else {
                        Toast.makeText(mContext,"Failed to fetch dashboard items..",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){

                    Toast.makeText(mContext,"Failed to fetch dashboard items..",Toast.LENGTH_LONG).show();                    }
            }else{
                Toast.makeText(mContext,"Failed to fetch dashboard items..",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void hideAllViews() {
        servicing_cb .setVisibility(View.GONE);
        gresing_cb .setVisibility(View.GONE);
        e_oil_cb .setVisibility(View.GONE);
        tyres_cb .setVisibility(View.GONE);
        national_permit_cb .setVisibility(View.GONE);
        rc_cb .setVisibility(View.GONE);
        pollution_cb .setVisibility(View.GONE);
        fitness_cb .setVisibility(View.GONE);
        insurance_cb.setVisibility(View.GONE);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
