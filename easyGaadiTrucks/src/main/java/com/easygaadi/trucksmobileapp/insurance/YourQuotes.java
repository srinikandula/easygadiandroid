package com.easygaadi.trucksmobileapp.insurance;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.trucksmobileapp.LoginActivity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TruckApp;
import com.easygaadi.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YourQuotes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YourQuotes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YourQuotes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;
    private ConnectionDetector detectCnnection;
    private SharedPreferences sharedPreferences;
    private ProgressDialog pDialog;
    private RecyclerView quotes_rv;
    private TextView message_tv;
    private RecyclerView od_rv;
    private JSONArray quotesArray;
    private InsQuotesAdapter insAdapter;
    /*private OnFragmentInteractionListener mListener;*/

    public YourQuotes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YourQuotes.
     */
    // TODO: Rename and change types and number of parameters
    public static YourQuotes newInstance(String param1, String param2) {
        YourQuotes fragment = new YourQuotes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        sharedPreferences = mContext.getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name),mContext.MODE_PRIVATE);
        detectCnnection = new ConnectionDetector(mContext);
        quotesArray = new JSONArray();
        insAdapter = new InsQuotesAdapter(quotesArray);
        pDialog  = new ProgressDialog(getActivity());
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView =inflater.inflate(R.layout.fragment_your_quotes, container, false);
        quotes_rv = (RecyclerView)fragmentView.findViewById(R.id.quotes_rc);
        if(detectCnnection.isConnectingToInternet()){
            new GetQuotes().execute();
        }else {
            Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
        }
        setAdapter();
        return fragmentView;
    }

    private void setAdapter() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        quotes_rv.setLayoutManager(mLayoutManager);
        quotes_rv.setItemAnimator(new DefaultItemAnimator());
        quotes_rv.setAdapter(insAdapter);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        /*mListener = null;*/
    }

    private class GetQuotes extends AsyncTask<Void,Void,JSONObject> {
        private JSONParser jsonParser;
        public GetQuotes() {
            jsonParser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Fetching quotes...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject json = null;
            try {
                String stringRequest = setUrlParameters();
                String res = jsonParser.excutePost(TruckApp.INSURANCE_QUOTE_URL,stringRequest);
                json = new JSONObject(res);
            }catch(Exception e){
                Log.e("Login DoIN EX",e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            TruckApp.checkPDialog(pDialog);
            if(jsonObject!=null && jsonObject.has("status")){
                try {
                    if (jsonObject.getInt("status") == 0) {
                        Toast.makeText(mContext,"No quotes found",Toast.LENGTH_LONG).show();
                    }else if (jsonObject.getInt("status") == 1) {
                        if(jsonObject.has("data")){
                            insAdapter.swap(jsonObject.getJSONArray("data"));
                        }
                    }else if (jsonObject.getInt("status") == 2) {
                        //TruckApp.logoutAction(getActivity());
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                getResources().getString(R.string.app_name), getActivity().MODE_PRIVATE);
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
                        startActivity(new Intent(getActivity(),
                                LoginActivity.class));
                        getActivity().finish();
                    }
                }catch (Exception e){
                    Toast.makeText(mContext,"Failed to get the quotes",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(mContext,"Failed to get the quotes",Toast.LENGTH_LONG).show();
            }
        }

        protected String setUrlParameters(){
            StringBuilder builder= new StringBuilder();
            try {
                builder.append("uid=").
                        append(URLEncoder.encode(sharedPreferences.getString("uid",""), "UTF-8"));
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
                builder.append("&accountid=").
                        append(URLEncoder.encode(sharedPreferences.getString("accountID","no account id"), "UTF-8"));
                builder.append("&type=get");
                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }


    private class ODAdapter extends RecyclerView.Adapter<ODAdapter.ODViewHolder>{
        HashMap<String,String> values;
        List<String> keys;

        public ODAdapter(HashMap<String, String> values) {
            this.values = values;
            keys = new ArrayList<>();
            keys.addAll(values.keySet());
        }

        @Override
        public ODViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.od_dialog_item,parent,false);
            return new ODViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ODViewHolder holder, int position) {
            try{
                holder.key_tv.setText(keys.get(position));
                holder.value_tv.setText(values.get(keys.get(position)));
                int textStyle ;
                if(keys.get(position).equalsIgnoreCase("Total Liabilty Premium") || keys.get(position).equalsIgnoreCase("Total OD Premium")){
                    textStyle = Typeface.BOLD;
                }else {
                    textStyle = Typeface.NORMAL;
                }
                holder.value_tv.setTypeface(null, textStyle);
                holder.value_tv.setTypeface(null, textStyle);
            }catch (Exception e){

            }
        }

        @Override
        public int getItemCount() {
            return keys.size();
        }

        public class ODViewHolder extends RecyclerView.ViewHolder{
            TextView key_tv,value_tv;
            public ODViewHolder(View itemView) {
                super(itemView);
                key_tv = (TextView)itemView.findViewById(R.id.key_tv);
                value_tv = (TextView)itemView.findViewById(R.id.value_tv);
            }
        }
    }

    private class InsQuotesAdapter extends RecyclerView.Adapter<InsQuotesAdapter.InsQuoteViewHolder>{
        JSONArray quotesArray;

        public InsQuotesAdapter(JSONArray quotesArray) {
            this.quotesArray = quotesArray;
        }

        public void swap(JSONArray quotesArray){
            this.quotesArray = quotesArray;
            notifyDataSetChanged();
        }

        @Override
        public InsQuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ins_quote_item,parent,false);
            return new InsQuoteViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(InsQuoteViewHolder holder, int position) {
            try{
                JSONObject jsonObject = quotesArray.getJSONObject(position);
                holder.vehicle_no.setText(jsonObject.getString("vehicle_number"));
                holder.totalpremium.setText("Rs."+jsonObject.getString("total_premium"));
                holder.date_tv.setText(jsonObject.getString("date_created"));
                holder.contact_iv.setTag(jsonObject.getString("id_customer_insurance")+":"+position);
                holder.details.setTag(jsonObject.toString());
                holder.od_premium.setTag(jsonObject.toString());
                holder.details.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LinkedHashMap<String,String> values = convertString2Map(view.getTag().toString().trim(),3);
                        confirmDialog(values,"Quote Details");
                    }
                });

                holder.od_premium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LinkedHashMap<String,String> values = convertString2Map(view.getTag().toString().trim(),1);
                        confirmDialog(values,"OD Premium");
                    }
                });
                holder.liability_premium.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LinkedHashMap<String,String> values = convertString2Map(view.getTag().toString().trim(),2);
                        confirmDialog(values,"Liability Premium");

                    }
                });
                holder.liability_premium.setTag(jsonObject.toString());
                if(jsonObject.getString("status").equalsIgnoreCase("New")){
                    holder.totalpremium.setVisibility(View.INVISIBLE);
                    holder.third_layout.setVisibility(View.GONE);
                    holder.middle_layout.setVisibility(View.GONE);
                    holder.preparing_quote_tv.setVisibility(View.VISIBLE);
                }else {
                    holder.totalpremium.setVisibility(View.VISIBLE);
                    holder.third_layout.setVisibility(View.VISIBLE);
                    holder.middle_layout.setVisibility(View.VISIBLE);
                    holder.preparing_quote_tv.setVisibility(View.GONE);
                }

                if(jsonObject.getString("status").equalsIgnoreCase("Interested")){
                    holder.contact_iv.setImageResource(R.drawable.icon_phone_booken);
                    holder.contact_iv.setEnabled(false);
                }else{
                    holder.contact_iv.setImageResource(R.drawable.icon_phone);
                    holder.contact_iv.setEnabled(true);
                }
                holder.contact_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] values = (view.getTag().toString()).split(":");
                        new ApplyQuote(values[0],"Interested",Integer.parseInt(values[1])).execute();
                    }
                });
            }catch (Exception e){

            }
        }


        private class ApplyQuote extends AsyncTask<Void,Void,JSONObject>{

            String id_customer_interested,status;
            int position;
            JSONParser jsonParser;

            public ApplyQuote(String id_customer_interested, String status,int position) {
                this.id_customer_interested = id_customer_interested;
                this.status = status;
                this.position = position;
                jsonParser = JSONParser.getInstance();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog.setMessage("Contacting for quote...");
                pDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject json = null;
                StringBuilder builder= new StringBuilder();
                try {
                    builder.append("uid=").
                            append(URLEncoder.encode(sharedPreferences.getString("uid", ""), "UTF-8"));
                    builder.append("&access_token=").
                            append(URLEncoder.encode(sharedPreferences.getString("access_token", ""), "UTF-8"));
                    builder.append("&accountid=").
                            append(URLEncoder.encode(sharedPreferences.getString("accountID", "no account id"), "UTF-8"));
                    builder.append("&id_customer_insurance=").
                            append(URLEncoder.encode(id_customer_interested, "UTF-8"));
                    builder.append("&status=").
                            append(URLEncoder.encode(status, "UTF-8"));
                    builder.append("&type=update");
                    String res = jsonParser.excutePost(TruckApp.INSURANCE_QUOTE_URL,builder.toString());
                    json = new JSONObject(res);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return json;
            }


            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                TruckApp.checkPDialog(pDialog);
                if(jsonObject!=null && jsonObject.has("status")){
                    try {
                        if (jsonObject.getInt("status") == 0) {
                            Toast.makeText(mContext,"Your request failed",Toast.LENGTH_LONG).show();
                        }else if (jsonObject.getInt("status") == 1) {
                            Toast.makeText(mContext,"Your request is successful,We will get back to you soon!!",Toast.LENGTH_LONG).show();
                            JSONObject  json = quotesArray.getJSONObject(position);
                            json.put("status","Interested");
                            quotesArray.put(position,json);
                            insAdapter.swap(quotesArray);
                        }else if (jsonObject.getInt("status") == 2) {
                            //TruckApp.logoutAction(getActivity());
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                    getResources().getString(R.string.app_name), getActivity().MODE_PRIVATE);
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
                            startActivity(new Intent(getActivity(),
                                    LoginActivity.class));
                            getActivity().finish();
                        }
                    }catch (Exception e){
                        Toast.makeText(mContext,"Your request failed",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(mContext,"Your request failed",Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public int getItemCount() {
            return quotesArray.length();
        }

        public class InsQuoteViewHolder extends RecyclerView.ViewHolder{

            Button od_premium,liability_premium,details;
            ImageView contact_iv;
            TextView vehicle_no,totalpremium,date_tv,preparing_quote_tv;
            LinearLayout middle_layout,third_layout;

            public InsQuoteViewHolder(View itemView) {
                super(itemView);
                od_premium = (Button)itemView.findViewById(R.id.od_premium_btn);
                liability_premium = (Button)itemView.findViewById(R.id.liability_premium_btn);
                details = (Button)itemView.findViewById(R.id.details_btn);
                vehicle_no = (TextView) itemView.findViewById(R.id.vehicle_no_tv);
                totalpremium = (TextView) itemView.findViewById(R.id.total_premimum_tv);
                date_tv = (TextView) itemView.findViewById(R.id.date_tv);
                contact_iv = (ImageView) itemView.findViewById(R.id.contact_tbn);
                preparing_quote_tv = (TextView) itemView.findViewById(R.id.preparing_quote_tv);
                middle_layout = (LinearLayout) itemView.findViewById(R.id.middle_layout);
                third_layout = (LinearLayout) itemView.findViewById(R.id.third_layout);
            }
        }
    }

    private LinkedHashMap<String,String> convertString2Map(String data,int value) {
        LinkedHashMap<String,String> values = new LinkedHashMap<>();
        try{
            JSONObject jsonObj = new JSONObject(data);
            if(value == 1){
                values.put("Rate",jsonObj.getString("od_rate"));
                values.put("Basic OD",jsonObj.getString("od_basic_od_premium"));
                values.put("GVM Premium",jsonObj.getString("od_gvw_premium"));
                values.put("Total Basic OD Premium",jsonObj.getString("od_total_basic_od_premium"));
                values.put("Fittings",jsonObj.getString("od_elec_fitting"));
                values.put("Bi-fuel system Premium",jsonObj.getString("od_bi_fuel_system_premium"));
                values.put("Discount Amount",jsonObj.getString("od_discount_amount"));
                values.put("Post Discount Amount",jsonObj.getString("od_post_disount_amount"));
                values.put("IMT -23",jsonObj.getString("od_imt_23"));
                values.put("Post IMT -23 Premium",jsonObj.getString("od_post_imt_23_premium"));

                values.put("NCB Amount",jsonObj.getString("od_ncb_amount"));
                values.put("Total OD Premium",jsonObj.getString("od_total_od_premium"));
            }

            if(value == 2){
                values.put("Basic TP Premium",jsonObj.getString("lb_basic_tp_premium"));
                values.put("Compulsory \n Owner\\Driver",jsonObj.getString("lb_compulsory_owner_driver"));
                values.put("Paid Drivers\\Cleaners \n \\Any Rs.50 parameter",jsonObj.getString("lb_paid_drivers_clearners"));
                values.put("TP Premium For Bi-\nFuel System",jsonObj.getString("lb_tp_premium_bi_fuel_system"));
                values.put("NFPP Premium",jsonObj.getString("lb_nfpp_premium"));
                values.put("Total Liability Premium",jsonObj.getString("lb_total_liability_premium"));
                values.put("Gross Premium",jsonObj.getString("lb_gross_premium"));
                values.put("Service Tax",jsonObj.getString("lb_service_tax"));
            }

            if(value == 3){
                values.put(getString(R.string.vehicle_no_f),jsonObj.getString("vehicle_number"));
                values.put(getString(R.string.idv_hint),"Rs."+jsonObj.getString("idv"));
                values.put(getString(R.string.vehicle_age_hint),jsonObj.getString("age"));
                values.put(getString(R.string.ncb_per_hint),jsonObj.getString("ncb"));
                values.put(getString(R.string.imt_required_hint),(jsonObj.getString("imt").equalsIgnoreCase("1")?"Yes":"No"));
                values.put(getString(R.string.gross_vehicle_weight_hint),jsonObj.getString("weight"));
                values.put(getString(R.string.pa_o_d_hint),(jsonObj.getString("pa_owner_driver").equalsIgnoreCase("1")?"Yes":"No"));
                values.put(getString(R.string.nil_dep_hint),(jsonObj.getString("nil_dep").equalsIgnoreCase("1")?"Yes":"No"));
            }

        }catch (Exception e){

        }

        return values;
    }

    protected void confirmDialog(LinkedHashMap<String,String> values, String title) {
        try {
            final Dialog confirmDialog = new Dialog(mContext, android.R.style.Theme_Dialog);
            confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            confirmDialog.setContentView(R.layout.od_premium_dialog);
            TextView title_tv = (TextView) confirmDialog.findViewById(R.id.title_tv);
            ImageView cancel_btn = (ImageView) confirmDialog.findViewById(R.id.close_iv);
            title_tv.setText(title);
            od_rv = (RecyclerView)confirmDialog.findViewById(R.id.od_rc);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            od_rv.setLayoutManager(mLayoutManager);
            od_rv.setItemAnimator(new DefaultItemAnimator());
            od_rv.setAdapter(new ODAdapter(values));
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDialog.dismiss();
                }
            });
            confirmDialog.show();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
