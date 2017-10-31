package com.easygaadi.erp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import com.easygaadi.trucksmobileapp.BuyTruckActivity;
import com.easygaadi.trucksmobileapp.LoginActivity;
import com.easygaadi.trucksmobileapp.Maintenance_Activity;
import com.easygaadi.trucksmobileapp.Party_Activity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TruckApp;
import com.easygaadi.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.easygaadi.trucksmobileapp.R.id.etSearch;

/**
 * Created by ssv on 26-10-2017.
 */

public class PartList extends Fragment {
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
    private static ArrayList<PartyVo> data;
    private ConnectionDetector detectConnection;
    private static ImageView addImage;
    JSONParser parser;
    ProgressDialog pDialog;
    EditText etSearch;

    CustomAdapter partyadapter;


    public PartList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrunkList.
     */
    // TODO: Rename and change types and number of parameters
    public static PartList newInstance(String param1, String param2) {
        PartList fragment = new PartList();
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
        View view = inflater.inflate(R.layout.fragment_parties_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.quotes_rc);
        recyclerView.setHasFixedSize(true);


        addImage = (ImageView)view.findViewById(R.id.addImage);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<PartyVo>();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), Party_Activity.class), requestCode);
            }
        });

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
                if(s.length()>0){
                    if(partyadapter !=null)
                    partyadapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
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
            pDialog.setMessage("Fetching Trucks Please..");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                String res = parser.erpExecuteGet(getActivity(),TruckApp.payListURL);
                Log.e("paylist",res.toString());
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
                            JSONArray partArray = result.getJSONArray("parties");
                            if(partArray.length() > 0)
                            {
                                for (int i = 0; i < partArray.length(); i++) {
                                    JSONObject partData = partArray.getJSONObject(i);
                                    Log.v("contacts",partData.getString("contact"));
                                    PartyVo voData = new PartyVo();
                                    voData.setName(partData.getString("name"));
                                    voData.setContact(""+partData.getString("contact"));
                                    Log.v("contact",voData.getContact()+"res"+partData.getInt("contact"));
                                    voData.setOperatingLane(partData.getString("operatingLane"));
                                    data.add(voData);
                                }

                                partyadapter = new CustomAdapter(data);
                                recyclerView.setAdapter(partyadapter);
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

        private ArrayList<PartyVo> dataSet;


        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textViewPName,textVieCon,textViesrctodest_tv,textViewcall_tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.textViewPName = (TextView) itemView.findViewById(R.id.partyName_tv);
                this.textVieCon = (TextView) itemView.findViewById(R.id.partyContact_tv);
                this.textViesrctodest_tv = (TextView) itemView.findViewById(R.id.srctodest_tv);
                this.textViewcall_tv = (TextView) itemView.findViewById(R.id.call_tv);
            }
        }

        public CustomAdapter(ArrayList<PartyVo> data) {
            this.dataSet = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcatitem_layout, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }



        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView textViewPName = holder.textViewPName;
            TextView textVieCon = holder.textVieCon;
            TextView textViewlane = holder.textViesrctodest_tv;
            TextView textViewcall_tv = holder.textViewcall_tv;

            textViewPName.setText(dataSet.get(listPosition).getName());
            textVieCon.setText(""+dataSet.get(listPosition).getContact());
            textViewlane.setText(dataSet.get(listPosition).getOperatingLane());
            Log.v("com",""+dataSet.get(listPosition).getContact());


            textViewcall_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onCall(v,""+dataSet.get(listPosition).getContact());
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
                fRecords=new RecordFilter();
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
                    results.values = dataSet;
                    results.count = dataSet.size();

                } else {
                    //Need Filter
                    // it matches the text  entered in the edittext and set the data in adapter list
                    ArrayList<PartyVo> fRecords = new ArrayList<PartyVo>();

                    for (PartyVo s : dataSet) {
                        if (s.getName().toString().toUpperCase().trim().contains(constraint.toString().toUpperCase().trim())) {
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
                dataSet = (ArrayList<PartyVo>) results.values;
                notifyDataSetChanged();
            }
        }


    }

    public void onCall(View view,String callNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
        callIntent.setData(Uri.parse("tel:"+callNumber));    //this is the phone number calling
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //in fragment class callback
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == this.requestCode){
            String addItem=data.getStringExtra("addItem");
            try {
                JSONObject partData = new JSONObject(addItem);
                PartyVo voData = new PartyVo();
                voData.setName(partData.getString("name"));
                voData.setContact(""+partData.getInt("contact"));
                voData.setOperatingLane(partData.getString("operatingLane"));
                this.data.add(voData);
                partyadapter.notifyDataSetChanged();

                /*adapter = new CustomAdapter(this.data);
                recyclerView.setAdapter(adapter);*/
            }catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

}
