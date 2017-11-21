package com.easygaadi.erp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.DataModel;
import com.easygaadi.models.PartyVo;
import com.easygaadi.models.TripVo;
import com.easygaadi.models.TruckVo;
import com.easygaadi.trucksmobileapp.Driver_Activity;
import com.easygaadi.trucksmobileapp.Party_Activity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TollActivity;
import com.easygaadi.trucksmobileapp.TripsDetails_Activty;
import com.easygaadi.trucksmobileapp.Trips_Activty;
import com.easygaadi.trucksmobileapp.TruckApp;
import com.easygaadi.trucksmobileapp.TruckDetails;
import com.easygaadi.trucksmobileapp.Trunck_Activity;

import org.json.JSONArray;
import org.json.JSONException;
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
    private EditText fromdate_tv,amount_tv;
    String paymentType = "";
    private ConnectionDetector detectConnection;
    private static ImageView addImage;
    JSONParser parser;
    ProgressDialog pDialog;
    EditText etSearch;
    private int requestCode = 123;
    CustomAdapter partyadapter;
    private Dialog reportDialog;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
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
            new GetTripsList().execute();
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

            TextView tripID_tv,truckRegNo_tv,tv_lastupadate,triperName_tv,freightamt_tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.tripID_tv = (TextView) itemView.findViewById(R.id.tripID_tv);
                this.truckRegNo_tv = (TextView) itemView.findViewById(R.id.truckRegNo_tv);
                this.tv_lastupadate = (TextView) itemView.findViewById(R.id.tv_lastupadate);
                this.triperName_tv = (TextView) itemView.findViewById(R.id.triperName_tv);
                this.freightamt_tv = (TextView) itemView.findViewById(R.id.freightamt_tv);
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
            TextView freightamt_tv = holder.freightamt_tv;

            tripID_tv.setText(Html.fromHtml("<u>"+dataSet.get(listPosition).getTripId()+"<u>"));
            truckRegNo_tv.setText(dataSet.get(listPosition).getTruckName());

            triperName_tv.setText(dataSet.get(listPosition).getPartyName());
            freightamt_tv.setText(dataSet.get(listPosition).getFreightAmount());

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

            tripID_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), TripsDetails_Activty.class);
                    intent.putExtra("hitupdate", dataSet.get(listPosition).get_id());
                    intent.putExtra("call", "truck");
                    startActivityForResult(intent, requestCode);
                }
            });

            //tripID_tv
            /*edit_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setReportDialog(dataSet.get(listPosition).get_id());
                }
            });*/

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




        private void setReportDialog(final String tripID){
            String[] payment = { "Payment Type","Cheque", "Chash"  };

            reportDialog = null;
            reportDialog = new Dialog(getContext(), android.R.style.Theme_Dialog);
            reportDialog.requestWindowFeature(1);
            reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            reportDialog.setContentView(R.layout.erp_trips_addbal_dialog);
            ImageView cardDiaCls = (ImageView)reportDialog.findViewById(R.id.recharge_close);
            final Button go_btn = (Button)reportDialog.findViewById(R.id.go_btn);
            fromdate_tv = (EditText) reportDialog.findViewById(R.id.Date_et);
            amount_tv = (EditText) reportDialog.findViewById(R.id.amount_tv);
            fromdate_tv.setClickable(true);
            fromdate_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePicker(fromdate_tv);
                }
            });

            //Spinner trip_pymtbl = (Spinner) reportDialog.findViewById(R.id.spnr_paymnttype);
            Spinner payspin = (Spinner) reportDialog.findViewById(R.id.spnr_paymnttype);
            //Creating the ArrayAdapter instance having the country list
            ArrayAdapter paytypeeaa = new ArrayAdapter(getContext(),R.layout.erp_view_spinner_item,payment);
            paytypeeaa.setDropDownViewResource(R.layout.erp_view_spinner_item);
            //Setting the ArrayAdapter data on the Spinner
            payspin.setAdapter(paytypeeaa);


            reportDialog.show();
            cardDiaCls.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View paramAnonymousView)
                {
                    reportDialog.dismiss();
                }
            });
            go_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String fromDate = fromdate_tv.getText().toString().trim();
                    String amount = amount_tv.getText().toString().trim();
                    String paymentsType = paymentType;
                   Log.i("fromDate-->", fromDate);
                   Log.v("fromDate-->",  fromDate);
                   Log.e("fromDate-->",  fromDate);
                   Log.d("fromDate-->",  fromDate);
                    Toast.makeText(getActivity(),"--"+fromDate,Toast.LENGTH_LONG).show();
                    if(fromDate.contains("-")){
                        if(amount.length() > 0){
                            if(Integer.parseInt(amount) > 0){
                                if(paymentsType.length() > 0){
                                    if (detectConnection.isConnectingToInternet()) {
                                        new AddPayment(tripID,fromDate,amount,paymentsType).execute();
                                    } else {
                                        Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.internet_str),Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(getActivity(),"Please Select Payment Type",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getActivity(),"Please Enter VAlid Amount ",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getActivity(),"Please Enter Amount",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getActivity(),"Please Select Date",Toast.LENGTH_SHORT).show();
                    }
                }
            });


            AdapterView.OnItemSelectedListener countrySelectedListener = new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> spinner, View container,int position, long id) {
                    if(spinner.getId() == R.id.spnr_paymnttype){
                        String selected = spinner.getItemAtPosition(position).toString();
                        if(selected.equalsIgnoreCase("Payment Type"))
                        {
                            //trip_pymtbl.setVisibility(View.INVISIBLE);

                        }else{
                            //trip_pymtbl.setVisibility(View.VISIBLE);
                        }
                        if(position !=0)
                        {
                            paymentType = spinner.getItemAtPosition(position).toString();
                        }else {
                            paymentType = "";
                        }

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            };

            payspin.setOnItemSelectedListener(countrySelectedListener);

        }
    }



    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(),R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                int tempmonth = view.getMonth()+1;
                String temp = ""+tempmonth;
                if(temp.length()>2){
                    temp ="0"+temp;
                }
                if(Tview.getId() == R.id.Date_et){
                    fromdate_tv.setText(view.getYear()+"-"+temp+"-"+view.getDayOfMonth());

                }
            }
        }, year, month, day);

        dpd.show();


    }


    private class GetTripsList extends AsyncTask<String, String, JSONObject> {

        //String uid, accountid, offset;

        public GetTripsList() {
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
                String res = parser.erpExecuteGet(getActivity(), TruckApp.tripsListURL+"/getAllAccountTrips");
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
                                voData.set_id(""+partData.getString("_id"));
                                voData.setTripId(""+partData.getString("tripId"));

                                if(partData.has("freightAmount")){
                                    voData.setFreightAmount(""+partData.getInt("freightAmount"));
                                }else{
                                    voData.setFreightAmount("XXXXX");
                                }
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
                    new GetTripsList().execute();
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


    private class AddPayment extends AsyncTask<String, String, JSONObject> {
        String tripId, paymentDate,amount, paymentType;

        public AddPayment(String tripId,String  paymentDate,String  amount,String  paymentType) {
            this.tripId = tripId;
            this.paymentDate = paymentDate;
            this.amount = amount;
            this.paymentType = paymentType;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject res = null;
            try {

                JSONObject post_dict = new JSONObject();
                JSONObject salary = new JSONObject();

                try {
                    post_dict.put("tripId",tripId);
                    post_dict.put("paymentDate", paymentDate);
                    post_dict.put("paymentType", paymentType);
                    post_dict.put("amount", amount);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("" + String.valueOf(post_dict));
                String result="";
                    result = parser.ERPexcutePut(getContext(), TruckApp.PaymentURL, String.valueOf(post_dict));
                    System.out.println("PaymentURL Trip"+result );
                    res = new JSONObject(result);

            } catch (Exception e) {
                Log.e("PaymentURL DoIN EX", e.toString());
                res = null;
            }
            return res;
        }
        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            // login_btn.setEnabled(true);
            pDialog.dismiss();
            Log.v("PaymentURL","res"+s.toString());
            if (s != null) {

                try {
                    //JSONObject js = new JSONObject(s);
                    if (!s.getBoolean("status")) {
                        Toast.makeText(getContext(), "fail",Toast.LENGTH_LONG).show();
                    } else {
                        reportDialog.dismiss();
                        Toast.makeText(getContext(), "Successfully Amount Addedd", Toast.LENGTH_SHORT).show();
                        if (detectConnection.isConnectingToInternet()) {
                            new GetTripsList().execute();
                        }else{
                            Toast.makeText(getActivity(),
                                    getResources().getString(R.string.internet_str),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                } catch (JSONException e) {
                    System.out.println("Exception while extracting the response:"+ e.toString());
                }
            } else {
                Toast.makeText(getContext(), getActivity().getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    MyReceiver r;
    public void refresh() {
        //yout code in refresh.
        Log.i("Refresh", "YES--");
            if (detectConnection.isConnectingToInternet()) {
                new GetTripsList().execute();
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
            TripList.this.refresh();
        }
    }

}
