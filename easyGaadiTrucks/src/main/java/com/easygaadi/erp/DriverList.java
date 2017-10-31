package com.easygaadi.erp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.DataModel;
import com.easygaadi.models.DriverVo;
import com.easygaadi.models.PartyVo;
import com.easygaadi.trucksmobileapp.Driver_Activity;
import com.easygaadi.trucksmobileapp.Party_Activity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TruckApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link DriverList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverList extends Fragment {
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
    private static ArrayList<DriverVo> data;
    private ConnectionDetector detectConnection;
    JSONParser parser;
    ProgressDialog pDialog;
    EditText etSearch;

    CustomAdapter partyadapter;
    private static ImageView addImage;


    public DriverList() {
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
    public static DriverList newInstance(String param1, String param2) {

        
        DriverList fragment = new DriverList();
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
        View view = inflater.inflate(R.layout.fragment_driver_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.quotes_rc);
        recyclerView.setHasFixedSize(true);


        addImage = (ImageView)view.findViewById(R.id.addImage);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DriverVo>();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), Party_Activity.class), requestCode);
            }
        });

        detectConnection = new ConnectionDetector(getActivity());
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(true);
        if (detectConnection.isConnectingToInternet()) {
            new GetDriverList().execute();
        }else{
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }



        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*startActivity(new Intent(getActivity(), Driver_Activity.class));*/
                Intent intent = new Intent(getActivity(), Driver_Activity.class);
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

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private ArrayList<DriverVo> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textViewDriverID,textViewDriverName,textViewDriverCon,textViewDriverLic_tv,textViewDriverLicExp_tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.textViewDriverID = (TextView) itemView.findViewById(R.id.driverID_tv);
                this.textViewDriverName = (TextView) itemView.findViewById(R.id.driverName_tv);
                this.textViewDriverCon = (TextView) itemView.findViewById(R.id.driverContact_tv);
                this.textViewDriverLic_tv = (TextView) itemView.findViewById(R.id.driverLic_tv);
                this.textViewDriverLicExp_tv = (TextView) itemView.findViewById(R.id.driverLicExp_tv);
            }
        }

        public CustomAdapter(ArrayList<DriverVo> data) {
            this.dataSet = data;
        }

        @Override
        public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dcatitem_layout, parent, false);
            CustomAdapter.MyViewHolder myViewHolder = new CustomAdapter.MyViewHolder(view);
            return myViewHolder;
        }



        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView textViewDriverID = holder.textViewDriverID;
            TextView textViewDriverName = holder.textViewDriverName;
            TextView textViewDriverCon = holder.textViewDriverCon;
            TextView textViewDriverLic_tv = holder.textViewDriverLic_tv;
            TextView textViewDriverLicExp_tv = holder.textViewDriverLicExp_tv;

            textViewDriverName.setText(dataSet.get(listPosition).getFullName());
            textViewDriverCon.setText(dataSet.get(listPosition).getMobile());
            textViewDriverLic_tv.setText(dataSet.get(listPosition).getLicenseNumber());
            Log.v("check",dataSet.get(listPosition).getLicenseNumber()+""+dataSet.get(listPosition).getLicenseValidity());
            System.out.println("check"+dataSet.get(listPosition).getLicenseNumber()+""+dataSet.get(listPosition).getLicenseValidity());

            textViewDriverID.setText(dataSet.get(listPosition).getDriverId());
            textViewDriverID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /*startActivity(new Intent(getActivity(), Driver_Activity.class));*/
                            Intent intent = new Intent(getActivity(), Driver_Activity.class);
                            intent.putExtra("hitupdate", dataSet.get(listPosition).get_id());
                            //intent.putExtra("hitupdate", "loo");
                            startActivityForResult(intent, requestCode);

                }
            });


            Date date;
            long diff = 0;

            DateFormat dateFormat,formatter;
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            String newDates = null;
            try {
                date = dateFormat.parse(dataSet.get(listPosition).getLicenseValidity());
                formatter = new SimpleDateFormat("yyyy-MM-dd"); //If you need time just put specific format for time like 'HH:mm:ss'
                newDates = formatter.format(date);
                Log.d("custom date",newDates);

                Date today = new Date();
                 diff =  today.getTime() - date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("err--"+e.getMessage());
            }

            int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            System.out.println("difference-"+listPosition+""+numOfDays);
            if(numOfDays < 0)
            {
                Drawable img = getContext().getResources().getDrawable( R.drawable.red );
                img.setBounds( 0, 0, 60, 60 );
                textViewDriverLic_tv.setCompoundDrawables( img, null, null, null );
            }else if(numOfDays < 30)
            {
                Drawable img = getContext().getResources().getDrawable( R.drawable.orange );
                img.setBounds( 0, 0, 60, 60 );
                textViewDriverLic_tv.setCompoundDrawables( img, null, null, null );
            }else
            {
                Drawable img = getContext().getResources().getDrawable( R.drawable.green );
                img.setBounds( 0, 0, 60, 60 );
                textViewDriverLic_tv.setCompoundDrawables( img, null, null, null );

            }


            textViewDriverLicExp_tv.setText(newDates);
            //System.out.println("check--"+newDates);
        }



        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }



    private class GetDriverList extends AsyncTask<String, String, JSONObject> {

        //String uid, accountid, offset;

        public GetDriverList() {
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
                String res = parser.erpExecuteGet(getActivity(), TruckApp.driverListURL);
                Log.e("driverlist",res.toString());
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
                        JSONArray partArray = result.getJSONArray("drivers");
                        if(partArray.length() > 0)
                        {
                            for (int i = 0; i < partArray.length(); i++) {
                                JSONObject partData = partArray.getJSONObject(i);
                                DriverVo voData = new DriverVo();
                                voData.set_id(partData.getString("_id"));
                                voData.setDriverId(partData.getString("driverId"));

                                voData.setFullName(partData.getString("fullName"));
                                voData.setMobile(""+partData.getString("mobile"));
                                voData.setLicenseNumber(partData.getString("licenseNumber"));

                                voData.setLicenseValidity(partData.getString("licenseValidity"));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //in fragment class callback
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == this.requestCode){
            String addItem=data.getStringExtra("addItem");
            try {
                JSONObject partData = new JSONObject(addItem);
                DriverVo voData = new DriverVo();

                if(data.getStringExtra("updated").equalsIgnoreCase("update"))
                {
                    for (int i = 0; i < this.data.size(); i++) {
                        if( partData.getString("_id").equalsIgnoreCase(this.data.get(i).get_id()))
                        {
                            voData.setDriverId(partData.getString("driverId"));
                            voData.setFullName(partData.getString("fullName"));
                            voData.setMobile(""+partData.getString("mobile"));
                            voData.setLicenseNumber(partData.getString("licenseNumber"));
                            voData.setLicenseValidity(partData.getString("licenseValidity"));
                            //this.data.add(voData);
                            this.data.set(i,voData);
                            partyadapter.notifyDataSetChanged();
                            break;
                        }
                    }

                }else{
                    voData.setDriverId(partData.getString("driverId"));
                    voData.setFullName(partData.getString("fullName"));
                    voData.setMobile(""+partData.getString("mobile"));
                    voData.setLicenseNumber(partData.getString("licenseNumber"));

                    voData.setLicenseValidity(partData.getString("licenseValidity"));
                    this.data.add(voData);
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

}
