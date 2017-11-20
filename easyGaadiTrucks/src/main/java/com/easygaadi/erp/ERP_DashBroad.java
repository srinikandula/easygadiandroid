package com.easygaadi.erp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.models.PartyVo;
import com.easygaadi.trucksmobileapp.ERP_DashBroad_Elements;
import com.easygaadi.trucksmobileapp.ExpiryTruck_Activity;
import com.easygaadi.trucksmobileapp.PendingPayments_Activty;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TruckApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ssv i3-210 on 11/18/2017.
 */

public class ERP_DashBroad extends Fragment implements View.OnClickListener{
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
    TextView revenuetextView1,expensetextView1,paymentstextView1;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<PartyVo> data;
    private ConnectionDetector detectConnection;
    JSONParser parser;
    ProgressDialog pDialog;
    View mview;
    LinearLayout permit_LL,poll_LL,tax_ll,insurance_ll,fitness_ll;
    RelativeLayout revenueLayout1,expenseLayout1,paymentsLayout1;
    public ERP_DashBroad() {
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
    public static ERP_DashBroad newInstance(String param1, String param2) {
        ERP_DashBroad fragment = new ERP_DashBroad();
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
        mview = inflater.inflate(R.layout.erp_dashbroad_layout, container, false);

        //revenuetextView1,expensetextView1,expenseamt1;
        revenuetextView1 = (TextView)mview.findViewById(R.id.revenuetextView1);
        expensetextView1 = (TextView)mview.findViewById(R.id.expensetextView1);
        paymentstextView1 = (TextView)mview.findViewById(R.id.paymentstextView1);

        SpannableString content = new SpannableString(revenuetextView1.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, (revenuetextView1.getText().toString()).length(), 0);
        revenuetextView1.setText(content);

        SpannableString content1 = new SpannableString(expensetextView1.getText().toString());
        content1.setSpan(new UnderlineSpan(), 0, (expensetextView1.getText().toString()).length(), 0);
        expensetextView1.setText(content1);

        SpannableString content2 = new SpannableString(paymentstextView1.getText().toString());
        content2.setSpan(new UnderlineSpan(), 0, (paymentstextView1.getText().toString()).length(), 0);
        paymentstextView1.setText(content2);


        //permit_LL,poll_LL,tax_ll,insurance_ll,fitness_ll;
        permit_LL = (LinearLayout)mview.findViewById(R.id.permit_LL);
        poll_LL = (LinearLayout)mview.findViewById(R.id.poll_LL);
        tax_ll = (LinearLayout)mview.findViewById(R.id.tax_ll);
        insurance_ll = (LinearLayout)mview.findViewById(R.id.insurance_ll);
        fitness_ll = (LinearLayout)mview.findViewById(R.id.fitness_ll);
        revenueLayout1 = (RelativeLayout)mview.findViewById(R.id.revenueLayout1);
        expenseLayout1 = (RelativeLayout)mview.findViewById(R.id.expenseLayout1);
        paymentsLayout1 = (RelativeLayout)mview.findViewById(R.id.paymentsLayout1);

        permit_LL.setOnClickListener(this);
        poll_LL.setOnClickListener(this);
        tax_ll.setOnClickListener(this);
        insurance_ll.setOnClickListener(this);
        fitness_ll.setOnClickListener(this);
        revenueLayout1.setOnClickListener(this);
        expenseLayout1.setOnClickListener(this);
        paymentsLayout1.setOnClickListener(this);


        detectConnection = new ConnectionDetector(getActivity());
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(true);
        if (detectConnection.isConnectingToInternet()) {
            new GetPartyList().execute();
        }else{
            Toast.makeText(getActivity(),getResources().getString(R.string.internet_str),Toast.LENGTH_LONG).show();
        }
        return mview;
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.permit_LL){
            Intent intent = new Intent(getActivity(), ExpiryTruck_Activity.class);
            intent.putExtra("Header", "Permit Expiry");
            intent.putExtra("url", "permitExpiryTrucks");
            startActivity(intent);
        }else if(view.getId() == R.id.poll_LL)
        {
            Intent intent = new Intent(getActivity(), ExpiryTruck_Activity.class);
            intent.putExtra("Header", "Pollution Expiry");
            intent.putExtra("url", "pollutionExpiryTrucks");
            startActivity(intent);

        }else if(view.getId() == R.id.tax_ll)
        {
            Intent intent = new Intent(getActivity(), ExpiryTruck_Activity.class);
            intent.putExtra("Header", "Tax Due Expiry");
            intent.putExtra("url", "taxExpiryTrucks");
            startActivity(intent);
        }else if(view.getId() == R.id.insurance_ll)
        {
            Intent intent = new Intent(getActivity(), ExpiryTruck_Activity.class);
            intent.putExtra("Header", "Insurance Expiry");
            intent.putExtra("url", "insuranceExpiryTrucks");
            startActivity(intent);
        }else if(view.getId() == R.id.insurance_ll)
        {
            Intent intent = new Intent(getActivity(), ExpiryTruck_Activity.class);
            intent.putExtra("Header", "Fitness Expiry");
            intent.putExtra("url", "fitnessExpiryTrucks");
            startActivity(intent);
        }else if(view.getId() == R.id.revenueLayout1){
            Intent intent = new Intent(getActivity(), ERP_DashBroad_Elements.class);
            intent.putExtra("Header", "Revenue");
            intent.putExtra("url", "trips/find/revenueByVehicle");//revenueByParty
            startActivity(intent);
        }else if(view.getId() == R.id.expenseLayout1){
            Intent intent = new Intent(getActivity(), ERP_DashBroad_Elements.class);
            intent.putExtra("Header", "Expense");
            intent.putExtra("url", "expense/groupByVehicle");
            startActivity(intent);
        }else if(view.getId() == R.id.paymentsLayout1){
            Intent intent = new Intent(getActivity(), PendingPayments_Activty.class);
            intent.putExtra("Header", "Pending Payments");
            intent.putExtra("url", "payments/getDuesByParty");
            startActivity(intent);
        }
    }


    private class GetPartyList extends AsyncTask<String, String, JSONObject> {

        //String uid, accountid, offset;
        public GetPartyList() {
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
                String res = parser.erpExecuteGet(getActivity(), TruckApp.ERP_URL+"admin/erpDashboard");
                json = new JSONObject(res);

            } catch (Exception e) {
                Log.e("findExpiryCount DoIN EX", e.toString());
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
                        JSONObject resultObj = result.getJSONObject("result");

                        ((TextView)mview.findViewById(R.id.revenueamt1)).setText(""+resultObj.getInt("totalRevenue"));
                        ((TextView)mview.findViewById(R.id.expenseamt1)).setText(""+resultObj.getInt("expensesTotal"));
                        ((TextView)mview.findViewById(R.id.paymentsamt1)).setText(""+resultObj.getInt("pendingDue"));

                        JSONObject partArray = resultObj.getJSONObject("expiring");
                        ((TextView)mview.findViewById(R.id.permit_num)).setText(""+partArray.getInt("permitExpiryCount"));
                        ((TextView)mview.findViewById(R.id.poll_num)).setText(""+partArray.getInt("pollutionExpiryCount"));
                        ((TextView)mview.findViewById(R.id.tax_num)).setText(""+partArray.getInt("taxExpiryCount"));
                        ((TextView)mview.findViewById(R.id.insurance_num)).setText(""+partArray.getInt("insuranceExpiryCount"));
                        ((TextView)mview.findViewById(R.id.fitness_num)).setText(""+partArray.getInt("fitnessExpiryCount"));
                    }
                    pDialog.dismiss();
                } catch (Exception e) {
                    pDialog.dismiss();
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

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

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
        public void onBindViewHolder(final CustomAdapter.MyViewHolder holder, final int listPosition) {

            TextView textViewPName = holder.textViewPName;
            TextView textVieCon = holder.textVieCon;
            TextView textViewlane = holder.textViesrctodest_tv;
            TextView textViewcall_tv = holder.textViewcall_tv;

            textViewPName.setText(dataSet.get(listPosition).getName());
            textVieCon.setText(""+dataSet.get(listPosition).getContact());
            // textViewlane.setText(dataSet.get(listPosition).getOperatingLane());
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
                if (detectConnection.isConnectingToInternet()) {
                    new GetPartyList().execute();
                }else{
                    Toast.makeText(getActivity(),getResources().getString(R.string.internet_str),Toast.LENGTH_LONG).show();
                }
            }catch (Exception e)
            {
                e.getMessage();
            }
        }
    }




}

