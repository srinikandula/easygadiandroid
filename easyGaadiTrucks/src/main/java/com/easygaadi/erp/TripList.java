package com.easygaadi.erp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.DataModel;
import com.easygaadi.models.PartyVo;
import com.easygaadi.models.TripVo;
import com.easygaadi.models.TruckVo;
import com.easygaadi.trucksmobileapp.Party_Activity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.Trips_Activty;
import com.easygaadi.trucksmobileapp.TruckApp;
import com.easygaadi.trucksmobileapp.Trunck_Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link TripList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripList extends Fragment {
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
    private static ArrayList<TripVo> data;
    private static ArrayList<Integer> removedItems;

    private ConnectionDetector detectConnection;
    private static ImageView addImage;
    JSONParser parser;
    ProgressDialog pDialog;
    EditText etSearch;
    private int requestCode = 123;
    CustomAdapter partyadapter;

    public TripList() {
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
    public static TripList newInstance(String param1, String param2) {
        TripList fragment = new TripList();
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
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
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
                startActivityForResult(new Intent(getActivity(), Trips_Activty.class), requestCode);
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

        private ArrayList<TripVo> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tripID_tv,truckRegNo_tv,tv_lastupadate,triperName_tv,dieselamt_tv,tollamt_tv,freightamt_tv,advamt_tv,balanceamt_tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.tripID_tv = (TextView) itemView.findViewById(R.id.tripID_tv);
                this.truckRegNo_tv = (TextView) itemView.findViewById(R.id.truckRegNo_tv);
                this.tv_lastupadate = (TextView) itemView.findViewById(R.id.tv_lastupadate);
                this.triperName_tv = (TextView) itemView.findViewById(R.id.triperName_tv);
                this.dieselamt_tv = (TextView) itemView.findViewById(R.id.dieselamt_tv);
                this.tollamt_tv = (TextView) itemView.findViewById(R.id.tollamt_tv);
                this.freightamt_tv = (TextView) itemView.findViewById(R.id.freightamt_tv);
                this.advamt_tv = (TextView) itemView.findViewById(R.id.advamt_tv);
                this.balanceamt_tv = (TextView) itemView.findViewById(R.id.balanceamt_tv);
            }
        }

        public CustomAdapter(ArrayList<TripVo> data) {
            this.dataSet = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tripcatitem_layout, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }



        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView tripID_tv = holder.tripID_tv;
            TextView truckRegNo_tv = holder.truckRegNo_tv;
            TextView tv_lastupadate = holder.tv_lastupadate;
            TextView triperName_tv = holder.triperName_tv;
            TextView dieselamt_tv = holder.dieselamt_tv;
            TextView tollamt_tv = holder.tollamt_tv;
            TextView freightamt_tv = holder.freightamt_tv;
            TextView advamt_tv = holder.advamt_tv;
            TextView balanceamt_tv = holder.balanceamt_tv;

            tripID_tv.setText(dataSet.get(listPosition).getTripId());
            truckRegNo_tv.setText(dataSet.get(listPosition).getTruckName());

            triperName_tv.setText(dataSet.get(listPosition).getPartyName());
            dieselamt_tv.setText(dataSet.get(listPosition).getDieselAmount());
            tollamt_tv.setText(dataSet.get(listPosition).getTollgateAmount());
            freightamt_tv.setText(dataSet.get(listPosition).getFreightAmount());
            advamt_tv.setText(dataSet.get(listPosition).getAdvance());
            balanceamt_tv.setText(dataSet.get(listPosition).getBalance());

            Date date;

            DateFormat dateFormat,formatter;
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            String newDates = null;
            try {
                date = dateFormat.parse(dataSet.get(listPosition).getUpdatedAt());
                formatter = new SimpleDateFormat("yyyy-MM-dd"); //If you need time just put specific format for time like 'HH:mm:ss'
                newDates = formatter.format(date);

                tv_lastupadate.setText(newDates);
            } catch (ParseException e) {
                tv_lastupadate.setText("xx-yyy-zzz");
                e.printStackTrace();
                System.out.println("err--"+e.getMessage());
            }

            //tripID_tv



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
                    ArrayList<TripVo> fRecords = new ArrayList<TripVo>();

                    for (TripVo s : dataSet) {
                        if (s.getTripId().toString().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
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
                dataSet = (ArrayList<TripVo>) results.values;
                notifyDataSetChanged();
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
            pDialog.setMessage("Fetching Trips Please..");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                String res = parser.erpExecuteGet(getActivity(), TruckApp.tripsListURL+"/getAll/1");
                Log.e("getAll",res.toString());
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
                        JSONArray partArray = result.getJSONArray("trips");
                        if(partArray.length() > 0)
                        {
                            data = new ArrayList<TripVo>();
                            for (int i = 0; i < partArray.length(); i++) {
                                JSONObject partData = partArray.getJSONObject(i);

                                TripVo voData = new TripVo();
                                voData.setTripId(""+partData.getString("tripId"));
                                voData.setAdvance(""+partData.getInt("advance"));
                                voData.setDieselAmount(""+partData.getInt("dieselAmount"));

                                if(partData.has("freightAmount")){
                                    voData.setFreightAmount(""+partData.getInt("freightAmount"));
                                }else{
                                    voData.setFreightAmount("XXXXX");
                                }

                                voData.setTollgateAmount(""+partData.getInt("tollgateAmount"));
                                voData.setBalance(""+partData.getInt("balance"));
                                voData.setUpdatedAt(""+partData.getString("updatedAt"));

                                JSONObject perObj = partData.getJSONObject("attrs");
                                voData.setPartyName(perObj.getString("partyName"));
                                voData.setTruckName(perObj.getString("truckName"));


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
                    System.out.println("ex in get leads" + e.toString());
                    pDialog.dismiss();
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

                if (detectConnection.isConnectingToInternet()) {
                    new GetBuyingTrucks().execute();
                }else{
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.internet_str),
                            Toast.LENGTH_LONG).show();
                }
            }catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

}
