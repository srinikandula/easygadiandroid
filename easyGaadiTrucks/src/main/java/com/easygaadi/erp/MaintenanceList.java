package com.easygaadi.erp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.easygaadi.models.DataModel;
import com.easygaadi.models.DriverVo;
import com.easygaadi.models.MaitenanceVo;
import com.easygaadi.models.PartyVo;
import com.easygaadi.trucksmobileapp.Driver_Activity;
import com.easygaadi.trucksmobileapp.Maintenance_Activity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TruckApp;
import com.easygaadi.trucksmobileapp.Trunck_Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MaintenanceList extends Fragment {
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



        private int requestCode = 123;
        private static RecyclerView.Adapter adapter;
        private RecyclerView.LayoutManager layoutManager;
        private static RecyclerView recyclerView;
        private static ArrayList<MaitenanceVo> data;
        private ConnectionDetector detectConnection;
        JSONParser parser;
        ProgressDialog pDialog;
        EditText etSearch;
        CustomAdapter partyadapter;

        private static ImageView addImage;


        public MaintenanceList() {
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
        public static MaintenanceList newInstance(String param1, String param2) {
            MaintenanceList fragment = new MaintenanceList();
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
            pDialog.setCancelable(true);
            if (detectConnection.isConnectingToInternet()) {
                new GetMaitenaceList().execute();
            }else{
                Toast.makeText(getActivity(),getResources().getString(R.string.internet_str),Toast.LENGTH_LONG).show();
            }
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

            addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*startActivity(new Intent(getActivity(), Driver_Activity.class));*/
                    Intent intent = new Intent(getActivity(), Maintenance_Activity.class);
                    intent.putExtra("hitupdate", "");

                    startActivityForResult(intent, requestCode);
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

        public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> implements Filterable{

            private ArrayList<MaitenanceVo> dataSet;

            public class MyViewHolder extends RecyclerView.ViewHolder {

                TextView textViewName,textViewDate,textViewamt,textViewRArea,textViewRType;

                public MyViewHolder(View itemView) {
                    super(itemView);
                    this.textViewName = (TextView) itemView.findViewById(R.id.truckRegNo_tv);
                    this.textViewDate = (TextView) itemView.findViewById(R.id.tv_lastupadate);
                    this.textViewRType = (TextView) itemView.findViewById(R.id.repairtype_tv);
                    this.textViewRArea = (TextView) itemView.findViewById(R.id.repairarea_tv);
                    this.textViewamt = (TextView) itemView.findViewById(R.id.repairamt_tv);
                }
            }

            public CustomAdapter(ArrayList<MaitenanceVo> data) {
                this.dataSet = data;
            }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mcatitem_layout, parent, false);
                MyViewHolder myViewHolder = new MyViewHolder(view);
                return myViewHolder;
            }

            @Override
            public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

                TextView textViewName = holder.textViewName;
                TextView textViewDate = holder.textViewDate;
                TextView textViewRType = holder.textViewRType;
                TextView textViewRArea = holder.textViewRArea;
                TextView textViewamt = holder.textViewamt;

                textViewName.setText(dataSet.get(listPosition).getVehicleNumber());
                textViewDate.setText(getFormatDate(dataSet.get(listPosition).getDate()));
                textViewRType.setText(dataSet.get(listPosition).getDescription());
                textViewRArea.setText(dataSet.get(listPosition).getCity());
                textViewamt.setText(dataSet.get(listPosition).getCostString());
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
                        ArrayList<MaitenanceVo> fRecords = new ArrayList<MaitenanceVo>();

                        for (MaitenanceVo s : dataSet) {
                            if (s.getVehicleNumber().toString().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
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
                    dataSet = (ArrayList<MaitenanceVo>) results.values;
                    notifyDataSetChanged();
                }
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


        private class GetMaitenaceList extends AsyncTask<String, String, JSONObject> {

            //String uid, accountid, offset;

            public GetMaitenaceList() {
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
                    String res = parser.erpExecuteGet(getActivity(), TruckApp.maintenanceListURL+"/all/accountMaintenance");
                    json = new JSONObject(res);

                } catch (Exception e) {
                    Log.e("Login DoIN EX", e.toString());
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
                            JSONArray partArray = result.getJSONArray("maintanenceCosts");
                            if(partArray.length() > 0)
                            {
                                data = new ArrayList<MaitenanceVo>();
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    MaitenanceVo voData = new MaitenanceVo();

                                    voData.set_id(partData.getString("_id"));
                                    voData.setCostString(partData.getString("cost"));
                                    if(partData.has("shedArea")){
                                        voData.setCity(partData.getString("shedArea"));
                                    }else{
                                        voData.setCity("Location not found");
                                    }
                                    voData.setDate(""+partData.getString("date"));
                                    voData.setDescription(partData.getString("description"));


                                    JSONObject attributes = partData.getJSONObject("attrs");
                                    if(attributes.has("truckName"))
                                    {
                                        voData.setVehicleNumber(attributes.getString("truckName"));
                                    }else{
                                        voData.setVehicleNumber("XXXXXX");
                                    }



                                    data.add(voData);
                                }

                                partyadapter = new CustomAdapter(data);
                                recyclerView.setAdapter(partyadapter);
                                recyclerView.invalidate();
                                pDialog.dismiss();
                            }else{
                                Toast.makeText(getActivity(), "No records available",Toast.LENGTH_LONG).show();
                                pDialog.dismiss();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("ex in get leads" + e.toString());
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
                if(!addItem.isEmpty())
                {
                    for (int i = 0; i < this.data.size(); i++) {
                        JSONObject partData = new JSONObject(addItem);
                        if( partData.getString("_id").equalsIgnoreCase(this.data.get(i).get_id()))
                        {
                            MaitenanceVo voData = new MaitenanceVo();
                            voData.set_id(partData.getString("_id"));
                            voData.setVehicleNumber(partData.getString("vehicleNumber"));
                            voData.setCostString(partData.getString("cost"));
                            if(partData.has("location")){
                                voData.setCity(partData.getString("location"));
                            }else{
                                voData.setCity("Location not found");
                            }
                            voData.setDate(""+partData.getString("date"));
                            voData.setDescription(partData.getString("description"));
                            this.data.add(voData);
                            if(this.data.size() == 0)
                            {
                                partyadapter = new CustomAdapter(this.data);
                                recyclerView.setAdapter(partyadapter);

                            }else{
                                partyadapter.notifyDataSetChanged();
                            }
                            //partyadapter.notifyDataSetChanged();
                            break;
                        }
                    }

                }else{
                    if (detectConnection.isConnectingToInternet()) {
                        new GetMaitenaceList().execute();
                    }else{
                        Toast.makeText(getActivity(),getResources().getString(R.string.internet_str),Toast.LENGTH_LONG).show();
                    }
                }
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
                new GetMaitenaceList().execute();
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
                MaintenanceList.this.refresh();
            }
        }




    }

