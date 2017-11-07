package com.easygaadi.erp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
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
import java.util.concurrent.TimeUnit;

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
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<DriverVo> data;
    private ConnectionDetector detectConnection;
    JSONParser parser;
    ProgressDialog pDialog;
    EditText etSearch;

    CustomAdapter partyadapter;
    private static ImageView addImage;
    Boolean hit = false;


    public DriverList() {
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

        if(partyadapter !=null)
        {
            partyadapter.notifyDataSetChanged();
        }


        detectConnection = new ConnectionDetector(getActivity());
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(true);
        if (detectConnection.isConnectingToInternet()) {
            hit = true;
            new GetDriverList().execute();
        }else{
            Toast.makeText(getActivity(),getResources().getString(R.string.internet_str),Toast.LENGTH_LONG).show();
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

        etSearch=(EditText)view.findViewById(R.id.etSearch);
       // etSearch.setHint("Enter Driver ID");
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

        private ArrayList<DriverVo> dataSet;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textViewDriverID,textViewDriverName,textViewDriverCon,textViewDriverLic_tv,textViewDriverLicExp_tv,textViewCall_tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.textViewDriverID = (TextView) itemView.findViewById(R.id.driverID_tv);
                this.textViewDriverName = (TextView) itemView.findViewById(R.id.driverName_tv);
                this.textViewDriverCon = (TextView) itemView.findViewById(R.id.driverContact_tv);
                this.textViewDriverLic_tv = (TextView) itemView.findViewById(R.id.driverLic_tv);
                this.textViewDriverLicExp_tv = (TextView) itemView.findViewById(R.id.driverLicExp_tv);
                this.textViewCall_tv = (TextView) itemView.findViewById(R.id.call_tv);
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
            TextView textViewCall_tv = holder.textViewCall_tv;

            textViewDriverName.setText(dataSet.get(listPosition).getFullName());
            textViewDriverCon.setText(dataSet.get(listPosition).getMobile());
            textViewDriverLic_tv.setText(dataSet.get(listPosition).getLicenseNumber());


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

            textViewCall_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCall(v,""+dataSet.get(listPosition).getMobile());


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
                 diff =  date.getTime() -today.getTime()  ;
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("err--"+e.getMessage());
            }

            //int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
            long numOfDays = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
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


            SpannableString mySpannableString = new SpannableString(textViewDriverID.getText().toString().trim());
            mySpannableString.setSpan(new UnderlineSpan(), 0, mySpannableString.length(), 0);
            textViewDriverID.setText(mySpannableString);
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
                    ArrayList<DriverVo> fRecords = new ArrayList<DriverVo>();

                    for (DriverVo s : dataSet) {
                        if (s.getDriverId().toString().trim().contains(constraint.toString().trim())) {
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
                dataSet = (ArrayList<DriverVo>) results.values;
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
            pDialog.setMessage("");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                String res = parser.erpExecuteGet(getActivity(), TruckApp.driverListURL+"/account/drivers");
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
                        pDialog.dismiss();
                        JSONArray partArray = result.getJSONArray("drivers");
                        if(partArray.length() > 0)
                        {
                            data = new ArrayList<DriverVo>();
                            for (int i = 0; i < partArray.length(); i++) {
                                JSONObject partData = partArray.getJSONObject(i);
                                DriverVo voData = new DriverVo();
                                voData.set_id(partData.getString("_id"));
                                voData.setDriverId(partData.getString("driverId"));
                                if(partData.has("fullName")){
                                    voData.setFullName(partData.getString("fullName"));
                                }else{
                                    voData.setFullName("XYZ");
                                }
                                voData.setMobile(""+partData.getString("mobile"));
                                voData.setLicenseNumber(partData.getString("licenseNumber"));

                                voData.setLicenseValidity(partData.getString("licenseValidity"));
                                data.add(voData);
                            }

                            partyadapter = new CustomAdapter(data);
                            recyclerView.setAdapter(partyadapter);
                            recyclerView.invalidate();

                        }else{
                            Toast.makeText(getActivity(), "No records available",Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("GetDriverList in get leads" + e.toString());
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
                    if(this.data.size() == 0)
                    {
                        partyadapter = new CustomAdapter(this.data);
                        recyclerView.setAdapter(partyadapter);
                    }else{
                        partyadapter.notifyDataSetChanged();
                    }
                    //partyadapter.notifyDataSetChanged();
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
        Log.i("Refresh", "YES--"+hit);
        if(hit){
            if (detectConnection.isConnectingToInternet()) {
                new GetDriverList().execute();
            }else{
                Toast.makeText(getActivity(),getResources().getString(R.string.internet_str),Toast.LENGTH_LONG).show();
            }
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
            DriverList.this.refresh();
        }
    }

}
