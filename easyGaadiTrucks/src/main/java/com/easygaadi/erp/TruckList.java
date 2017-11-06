package com.easygaadi.erp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.PartyVo;
import com.easygaadi.models.TruckVo;
import com.easygaadi.trucksmobileapp.Driver_Activity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TruckApp;
import com.easygaadi.trucksmobileapp.TruckDetails;
import com.easygaadi.trucksmobileapp.Trunck_Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {} interface
 * to handle interaction events.
 * Use the {@link TruckList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TruckList extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // The URL to +1.  Must be a valid URL.
    private final String PLUS_ONE_URL = "http://developer.android.com";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<Integer> removedItems;

    private static ArrayList<TruckVo> data;
    private ConnectionDetector detectConnection;
    private static ImageView addImage;
    JSONParser parser;
    ProgressDialog pDialog;
    EditText etSearch;
    private int requestCode = 123;
    CustomAdapter partyadapter;

    public TruckList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TruckList.
     */
    // TODO: Rename and change types and number of parameters
    public static TruckList newInstance(String param1, String param2) {
        TruckList fragment = new TruckList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trunk_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.quotes_rc);
        recyclerView.setHasFixedSize(true);

        addImage = (ImageView)view.findViewById(R.id.addImage);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        detectConnection = new ConnectionDetector(getActivity());
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        if (detectConnection.isConnectingToInternet()) {
            new GetBuyingTrucks().execute();
        }else{
            Toast.makeText(getActivity(),getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
        }

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Trunck_Activity.class);
                intent.putExtra("hitupdate", "");
                startActivityForResult(intent, requestCode);
            }
        });
        etSearch=(EditText)view.findViewById(R.id.etSearch);
        etSearch.setText("");
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,             int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if(s.length()>0){
                if(partyadapter !=null)
                    partyadapter.getFilter().filter(s.toString());
                //}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
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

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements Filterable {

        private ArrayList<TruckVo> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textViewName,textViewTruckREG,textViewlastupadate;
            TextView textViewPermit_tv,textViewPoll_tv,textViewIns_tv,textViewFitness_tv,textViewmore,textViewcall;
            LinearLayout moreLL;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.textViewName = (TextView) itemView.findViewById(R.id.trunkDrName_tv);
                this.textViewTruckREG = (TextView) itemView.findViewById(R.id.truckRegNo_tv);
                this.textViewlastupadate = (TextView) itemView.findViewById(R.id.tv_lastupadate);
                this.textViewPermit_tv = (TextView) itemView.findViewById(R.id.permit_tv);
                this.textViewPoll_tv = (TextView) itemView.findViewById(R.id.poll_tv);
                this.textViewIns_tv = (TextView) itemView.findViewById(R.id.ins_tv);
                this.textViewmore = (TextView) itemView.findViewById(R.id.more_tv);
                this.textViewcall = (TextView) itemView.findViewById(R.id.call_tv);
                this.textViewFitness_tv = (TextView) itemView.findViewById(R.id.fitness_tv);
            }
        }

        public CustomAdapter(ArrayList<TruckVo> data) {
            this.dataSet = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.truckcatitem_layout, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView textViewName = holder.textViewName;
            TextView textViewTruckREG = holder.textViewTruckREG;
            TextView textViewlastupadate = holder.textViewlastupadate;
            TextView textViewPermit_tv = holder.textViewPermit_tv;
            TextView textViewPoll_tv = holder.textViewPoll_tv;
            TextView textViewIns_tv = holder.textViewIns_tv;
            TextView textViewmore = holder.textViewmore;
            TextView textViewcall = holder.textViewcall;
            TextView textViewFitness_tv = holder.textViewFitness_tv;

            textViewName.setText(dataSet.get(listPosition).getDrivername());
            textViewTruckREG.setText(dataSet.get(listPosition).getRegistrationNo());
            textViewlastupadate.setText(dataSet.get(listPosition).getTruckType()+ " "+""+dataSet.get(listPosition).getModelAndYear());


            if(Build.VERSION.SDK_INT  > 16)
            {
                textViewFitness_tv.setCompoundDrawablesRelativeWithIntrinsicBounds(getDays(dataSet.get(listPosition).getFitnessExpiry()), null, null, null );
                textViewPermit_tv.setCompoundDrawablesRelativeWithIntrinsicBounds( getDays(dataSet.get(listPosition).getPermitExpiry()), null, null, null );
                textViewPoll_tv.setCompoundDrawablesRelativeWithIntrinsicBounds( getDays(dataSet.get(listPosition).getPollutionExpiry()), null, null, null );
                textViewIns_tv.setCompoundDrawablesRelativeWithIntrinsicBounds( getDays(dataSet.get(listPosition).getInsuranceExpiry()), null, null, null );
            }else{
                textViewPermit_tv.setCompoundDrawables( getDays(dataSet.get(listPosition).getPermitExpiry()), null, null, null );
                textViewPoll_tv.setCompoundDrawables( getDays(dataSet.get(listPosition).getPollutionExpiry()), null, null, null );
                textViewIns_tv.setCompoundDrawables( getDays(dataSet.get(listPosition).getInsuranceExpiry()), null, null, null );
                textViewFitness_tv.setCompoundDrawables(getDays(dataSet.get(listPosition).getFitnessExpiry()), null, null, null );
            }


            if(dataSet.get(listPosition).getDrivercontact().contains("x"))
            {
                textViewcall.setVisibility(View.GONE);
            }

            textViewcall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onCall(v,dataSet.get(listPosition).getDrivercontact());
                }
            });
            textViewmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), TruckDetails.class);
                    intent.putExtra("hitupdate", dataSet.get(listPosition).get_id());
                    intent.putExtra("call", "");
                    startActivity(intent);
                }
            });

        }



        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        private Filter fRecords;
        @Override
        public Filter getFilter() {
            if(fRecords == null) {
                fRecords=new CustomAdapter.RecordFilter();
            }
            return fRecords;
        }

        private class RecordFilter extends Filter{

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                //Implement filter logic
                // if edittext is null return the actual list
                if (constraint == null || constraint.length() == 0) {
                    //No need for filter
                    results.values = data;
                    results.count = data.size();

                } else {
                    //Need Filter
                    // it matches the text  entered in the edittext and set the data in adapter list
                    ArrayList<TruckVo> fRecords = new ArrayList<TruckVo>();

                    for (TruckVo s : dataSet) {
                        if (s.getRegistrationNo().toString().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
                            fRecords.add(s);
                        }
                    }
                    results.values = fRecords;
                    results.count = fRecords.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                dataSet = (ArrayList<TruckVo>) results.values;
                notifyDataSetChanged();
            }
        }
    }


    public Drawable getDays(String fdate){

        Date date;
        long diff = 0;
        Log.i("start date",fdate);
        DateFormat dateFormat,formatter;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String newDates = null;
        try {
            date = dateFormat.parse(fdate);
            formatter = new SimpleDateFormat("yyyy-MM-dd"); //If you need time just put specific format for time like 'HH:mm:ss'
            newDates = formatter.format(date);
            Log.i("custom date",newDates);
            Date today = new Date();
            diff =  date.getTime()-today.getTime()  ;
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("err--"+e.getMessage());
        }

        //int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        long numOfDays = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
        Log.i("numOfDays-->",""+numOfDays);
        System.out.println("numOfDays-->"+numOfDays);
        if(numOfDays >  30)
        {
            Drawable img = getContext().getResources().getDrawable( R.drawable.orange );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }else if(numOfDays < 30)
        {
            Drawable img = getContext().getResources().getDrawable( R.drawable.orange );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }else
        {
            Drawable img = getContext().getResources().getDrawable( R.drawable.red );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }
    }



    public void onCall(View view,String caller) {
        Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
        callIntent.setData(Uri.parse("tel:"+caller));    //this is the phone number calling
        //check permission
        //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
        //the system asks the user to grant approval.
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //request permission from user if the app hasn't got the required permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                    10);
            return;
        }else {     //have got permission
            try{
                startActivity(callIntent);  //call activity and make phone call
            }
            catch (android.content.ActivityNotFoundException ex){
                Toast.makeText(getActivity(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
            }
        }
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
            pDialog.setMessage("");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                String res = parser.erpExecuteGet(getActivity(), TruckApp.truckListURL+"/get/accountTrucks/1");
                Log.i("trucksList",res.toString());
                json = new JSONObject(res);

            } catch (Exception e) {
                Log.e("truckListURL DoIN EX", e.toString());
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
                        Toast.makeText(getActivity(), "No records available",Toast.LENGTH_LONG).show();
                    }else
                    {
                        JSONArray partArray = result.getJSONArray("trucks");
                        if(partArray.length() > 0)
                        {
                            data = new ArrayList<TruckVo>();
                            for (int i = 0; i < partArray.length(); i++) {
                                JSONObject partData = partArray.getJSONObject(i);
                                TruckVo voData = new TruckVo();
                                JSONObject attributes = partData.getJSONObject("attrs");

                                if(attributes.has("fullName")){
                                    voData.setDrivername(attributes.getString("fullName"));
                                }else{
                                    voData.setDrivername("XYZ");
                                }

                                if(attributes.has("mobile")){
                                    voData.setDrivercontact(""+attributes.getInt("mobile"));
                                }else{
                                    voData.setDrivercontact("xxxxxxxxxx");
                                }
                                voData.set_id(partData.getString("_id"));
                                voData.setRegistrationNo(partData.getString("registrationNo"));
                                voData.setTruckType(partData.getString("truckType"));
                                voData.setModelAndYear(partData.getString("modelAndYear"));
                                voData.setFitnessExpiry(partData.getString("fitnessExpiry"));
                                voData.setPermitExpiry(partData.getString("permitExpiry"));
                                voData.setInsuranceExpiry(partData.getString("insuranceExpiry"));
                                voData.setPollutionExpiry(partData.getString("pollutionExpiry"));
                                System.out.println("leads" + voData.getFitnessExpiry());
                                data.add(voData);
                            }

                            partyadapter = new CustomAdapter(data);
                            recyclerView.setAdapter(partyadapter);
                            pDialog.dismiss();
                        }else{
                            Toast.makeText(getActivity(), "No records available",Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("trucksDAT" + e.toString());
                }

            } else {
                pDialog.dismiss();
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //in fragment class callback
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == this.requestCode){
            String addItem=data.getStringExtra("addItem");
            try {
                JSONObject partData = new JSONObject(addItem);
                TruckVo voData = new TruckVo();
                voData.setDrivername("Driver Name");

                if(partData.has("mobile")){
                    voData.setDrivercontact(partData.getString("mobile"));
                }else{
                    voData.setDrivercontact("xxxxxxxxxx");
                }


                voData.setRegistrationNo(partData.getString("registrationNo"));
                voData.setTruckType(partData.getString("truckType"));
                voData.setModelAndYear(partData.getString("modelAndYear"));
                voData.setFitnessExpiry(partData.getString("fitnessExpiry"));
                voData.setPermitExpiry(partData.getString("permitExpiry"));
                voData.setInsuranceExpiry(partData.getString("insuranceExpiry"));
                voData.setPollutionExpiry(partData.getString("pollutionExpiry"));
                this.data.add(voData);
                if(this.data.size() == 0)
                {
                    partyadapter = new CustomAdapter(this.data);
                    recyclerView.setAdapter(partyadapter);
                }else{
                    partyadapter.notifyDataSetChanged();
                }

                /*adapter = new CustomAdapter(this.data);
                recyclerView.setAdapter(adapter);*/
            }catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

    MyReceiver r;
    public void refresh() {
        //yout code in refresh.
        Log.i("Refresh", "YES--");
        if (detectConnection.isConnectingToInternet()) {
            new GetBuyingTrucks().execute();
        }else{
            Toast.makeText(getActivity(),getResources().getString(R.string.internet_str),Toast.LENGTH_LONG).show();
        }
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(r,
                new IntentFilter("TAG_REFRESH"));
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            TruckList.this.refresh();
        }
    }
}
