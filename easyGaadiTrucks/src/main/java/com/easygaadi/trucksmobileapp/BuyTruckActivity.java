package com.easygaadi.trucksmobileapp;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.trucksmobileapp.BuyTruckActivity.TrucksAdapter.ViewHolder;
import com.easygaadi.utils.Constants;
import com.squareup.picasso.Picasso;

public class BuyTruckActivity extends Activity {

    Context context;
    SharedPreferences sharedPreferences;
    Editor editor;
    private ConnectionDetector detectConnection;
    JSONParser parser;
    ProgressDialog pDialog;
    ListView BuytrucksLV;
    JSONArray trucksArray;
    TrucksAdapter adapter;

    String Str_id_sell_truck;
    Dialog truckDialog;
    Button send_btn;
    ImageView close_btn;
    TextView truck_tv;
    EditText price_et;
    ViewHolder viewholder;

    Dialog uploadDocDialog;

    String Customerbookingstatus, str_finace, str_accident;

    String imag_urlF, imag_urlB, imag_urlTFLP, imag_urlTFRP, imag_urlTBLP,
            imag_urlTBRP, imag_urlOP1, imag_urlOP2;
    int totaltrucks, offset;
    boolean loadMore_st;

    ImageView Front, Back, FrontTyreLeft, FrontTyreRight, backTyreLeft,
            BackTyreRight, Otherpic1, Otherpic2,disclsImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buytruck);

        context = this;
        detectConnection = new ConnectionDetector(context);
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        uploadDocDialog = new Dialog(BuyTruckActivity.this,
                android.R.style.Theme_Dialog);
        uploadDocDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        uploadDocDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        uploadDocDialog.setContentView(R.layout.buy_imgs_docdialog);

        disclsImage = (ImageView)uploadDocDialog.findViewById(R.id.imgDia_cls);



        Front = (ImageView) uploadDocDialog.findViewById(R.id.truck_frontpic);
        Back = (ImageView) uploadDocDialog.findViewById(R.id.truck_backpic);

        FrontTyreLeft = (ImageView) uploadDocDialog
                .findViewById(R.id.truck_fronttyreleftpic);
        FrontTyreRight = (ImageView) uploadDocDialog
                .findViewById(R.id.truck_fronttyrerightpic);
        backTyreLeft = (ImageView) uploadDocDialog
                .findViewById(R.id.truck_backtyreleftpic);
        BackTyreRight = (ImageView) uploadDocDialog
                .findViewById(R.id.truck_backtyrerightpic);

        Otherpic1 = (ImageView) uploadDocDialog
                .findViewById(R.id.truck_otherpic1);
        Otherpic2 = (ImageView) uploadDocDialog
                .findViewById(R.id.truck_otherpic2);

        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        BuytrucksLV = (ListView) findViewById(R.id.lv_buy_turckslist);

        fetchTrucks(0);

        disclsImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(uploadDocDialog.isShowing())
                { uploadDocDialog.dismiss(); }
            }
        });

        BuytrucksLV.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {


                if (totaltrucks > 10) {
                    System.out.println("loadMore_st" + loadMore_st);
                    if (firstVisibleItem + visibleItemCount == totalItemCount
                            && totalItemCount != 0) {
                        if (loadMore_st == false) {
                            loadMore_st = true;
                            if (trucksArray.length() < totaltrucks) {
                                offset = offset + 10;
                                System.out.println("Value to be loaded:"
                                        + offset);
                                fetchTrucks(offset);

                            } else {
                                System.out.println("size is  less than 10");
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        TruckApp.getInstance().trackScreenView("Trucks Screen");
    }

    private void fetchTrucks(int offset) {

        if (detectConnection.isConnectingToInternet()) {

            if (0 == offset) {
                trucksArray = new JSONArray();
                adapter = new TrucksAdapter(trucksArray, BuyTruckActivity.this);
                BuytrucksLV.setAdapter(adapter);
            }
            try {
                new GetBuyingTrucks(sharedPreferences.getString("uid", ""),
                        sharedPreferences.getString("accountID", ""), offset).execute();
            } catch (Exception e) {
                System.out.println("Ex inn on resume:" + e.toString());
            }
        } else {
            Toast.makeText(context,
                    getResources().getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }
    }

    private class GetBuyingTrucks extends AsyncTask<String, String, JSONObject> {

        String uid, accountid, offset;

        public GetBuyingTrucks(String uid, String accountid, int offset) {
            this.uid = uid;
            this.accountid = accountid;
            this.offset = String.valueOf(offset);
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
                StringBuilder builder = new StringBuilder();

                builder.append("uid=").append(URLEncoder.encode(uid, "UTF-8"));
                builder.append("&offset=").append(
                        URLEncoder.encode(offset, "UTF-8"));
                builder.append("&accountid=").append(
                        URLEncoder.encode(accountid, "UTF-8"));
                builder.append("&access_token=").append(sharedPreferences.getString("access_token",""));

                String res = parser.excutePost(TruckApp.BuyTruckListURL,
                        builder.toString());
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
            TruckApp.checkPDialog(pDialog);
            if (result != null) {

                try {

                    if (0 == result.getInt("status")) {
                        Toast.makeText(context, "No records available",
                                Toast.LENGTH_LONG).show();
                    }else if (2 == result.getInt("status")) {
                        /*TruckApp.logoutAction(BuyTruckActivity.this);*/
                        SharedPreferences sharedPreferences = context.getSharedPreferences(
                                context.getResources().getString(R.string.app_name), MODE_PRIVATE);
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
                        startActivity(new Intent(context,
                                LoginActivity.class));
                        finish();
                    }  else if (1 == result.getInt("status")) {
                        totaltrucks = result.getInt("count");
                        if (totaltrucks == 0) {
                            Toast.makeText(context, "No records found",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray trucksArray1 = result.getJSONArray("data");
                            for (int i = 0; i < trucksArray1.length(); i++) {
                                trucksArray.put(trucksArray1.getJSONObject(i));
                            }
                            trucksArray1 = null;
                            if (totaltrucks > trucksArray.length()) {
                                loadMore_st = false;
                            } else {
                                loadMore_st = true;
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ex in get leads" + e.toString());
                }

            } else {
                TruckApp.checkPDialog(pDialog);
                Toast.makeText(context,
                        getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public class TrucksAdapter extends BaseAdapter {
        Activity activity;
        LayoutInflater inflater;
        JSONArray trucks;

        public TrucksAdapter(JSONArray loads, Activity activity) {
            this.trucks = loads;
            this.activity = activity;
            this.inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public class ViewHolder {
            TextView trucktype_tv, place_tv, price_tv, meter_tv, year_tv,
                    accident_tv, finance_tv,rowid,fitness_exp_tv,insurance_exp_tv;
            Button submit;
            ImageView Truck_MainIMG;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return trucks.length();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            try {
                return trucks.get(arg0);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            viewholder = null;
            if (convertView == null) {

                viewholder = new ViewHolder();

                convertView = inflater.inflate(R.layout.buy_list_item_row,parent, false);

                viewholder.submit = (Button) convertView.findViewById(R.id.btn_buy_truck);
                viewholder.place_tv = (TextView) convertView.findViewById(R.id.tv_place);
                viewholder.trucktype_tv = (TextView) convertView.findViewById(R.id.tv_trucktype);
                viewholder.meter_tv = (TextView) convertView.findViewById(R.id.tv_odometer);
                viewholder.price_tv = (TextView) convertView.findViewById(R.id.tv_price);
                viewholder.finance_tv = (TextView) convertView.findViewById(R.id.tv_finance);
                viewholder.accident_tv = (TextView) convertView.findViewById(R.id.tv_accident);
                viewholder.insurance_exp_tv = (TextView) convertView.findViewById(R.id.tv_insuranceExp);
                viewholder.fitness_exp_tv = (TextView) convertView.findViewById(R.id.tv_FitnessExp);
                viewholder.year_tv = (TextView) convertView.findViewById(R.id.tv_year);
                viewholder.Truck_MainIMG = (ImageView) convertView.findViewById(R.id.buy_truck_img);
                viewholder.rowid = (TextView) convertView.findViewById(R.id.rowid);

                convertView.setTag(viewholder);

            } else {
                viewholder = (ViewHolder) convertView.getTag();
            }

            setData(position, viewholder);
            return convertView;
        }

        private void setData(int position, final ViewHolder viewholder) {

            try {
                JSONObject jObj = trucksArray.getJSONObject(position);

                System.out.println("Truck Obj:"+jObj.toString());
                viewholder.place_tv.setText(jObj.getString("truck_reg_state"));

                viewholder.trucktype_tv.setText(jObj.getString("truck_type_title"));
                viewholder.meter_tv.setText(jObj.getString("odometer"));
                viewholder.year_tv.setText(jObj.getString("year_of_mfg"));
                viewholder.price_tv.setText(jObj.getString("expected_price"));
                viewholder.rowid.setText(jObj.getString("id_sell_truck"));
                viewholder.Truck_MainIMG.setTag(jObj.toString());

                if(jObj.getString("fitness_exp_date")!=null)
                    viewholder.fitness_exp_tv.setText(jObj.getString("fitness_exp_date"));

                if(jObj.getString("insurance_exp_date")!=null)
                    viewholder.insurance_exp_tv.setText(jObj.getString("insurance_exp_date"));

                imag_urlF    = jObj.getString("truck_front_pic");
                str_finace   = jObj.getString("in_finance");
                str_accident = jObj.getString("any_accidents");

                Customerbookingstatus = jObj.getString("customer_status");


                viewholder.submit.setTag(position);

                if(jObj.getString("customer_status").equalsIgnoreCase("1")){
                    viewholder.submit.setText("Processing");
                    viewholder.submit.setEnabled(false);

                }else
                if(jObj.getString("customer_status").equalsIgnoreCase("0")){
                    viewholder.submit.setText("Contact Again");
                    viewholder.submit.setEnabled(true);
                }else{
                    viewholder.submit.setText("Contact");
                    viewholder.submit.setEnabled(true);
                }

                if (str_accident.equals("1")) {
                    viewholder.accident_tv.setText("Yes");
                } else {
                    viewholder.accident_tv.setText("No");
                }
                if (str_finace.equals("1")) {
                    viewholder.finance_tv.setText("Yes");
                } else {
                    viewholder.finance_tv.setText("No");
                }


                if (imag_urlF.equals("")) {
                    Picasso.with(context).load(R.drawable.noimageicon)
                            .into(viewholder.Truck_MainIMG);
                } else {
                    Picasso.with(context).load(imag_urlF).into(viewholder.Truck_MainIMG);
                }

                viewholder.Truck_MainIMG.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
						/*						Str_id_sell_truck = viewholder.rowid.getText().toString().trim();
						 */
                        try {
                            Str_id_sell_truck = v.getTag().toString();
                            JSONObject JOBj = new JSONObject(Str_id_sell_truck);

							/*new GetImges(Str_id_sell_truck).execute();*/
                            imag_urlF = JOBj.getString("truck_front_pic");
                            imag_urlB = JOBj.getString("truck_back_pic");
                            imag_urlTFLP = JOBj.getString("tyres_front_left_pic");
                            imag_urlTFRP = JOBj.getString("tyres_front_right_pic");
                            imag_urlTBLP = JOBj.getString("tyres_back_left_pic");
                            imag_urlTBRP = JOBj.getString("tyres_back_right_pic");
                            imag_urlOP1 = JOBj.getString("other_pic_1");
                            imag_urlOP2 = JOBj.getString("other_pic_2");
                            displayDocDialog();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                viewholder.submit.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Str_id_sell_truck = viewholder.rowid.getText().toString().trim();
                        int position = Integer.parseInt(v.getTag().toString());
                        String price_str = "0";
                        try {
                            new BookTruck(sharedPreferences.
                                    getString("uid",""), sharedPreferences.getString("accountID", ""),
                                    Str_id_sell_truck, price_str,position).execute();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        viewholder.submit.setText("Processing");
                        viewholder.submit.setEnabled(false);

                    }
                });

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        private class BookTruck extends AsyncTask<String, String, JSONObject> {

            String uid, Str_id_sell_truck, price, accountid;
            int position;

            public BookTruck(String uid, String accountid,
                             String Str_id_sell_truck, String price,int position) {
                this.uid = uid;
                this.Str_id_sell_truck = Str_id_sell_truck;
                this.accountid = accountid;
                this.price = price;
                this.position = position;
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                pDialog.setMessage("Booking a truck...");
                pDialog.show();
            }

            @Override
            protected JSONObject doInBackground(String... params) {

                JSONObject json = null;
                try {
                    StringBuilder builder = new StringBuilder();
                    builder.append("uid=").append(URLEncoder.encode(uid, "UTF-8"))
                            .append("&accountid=")
                            .append(URLEncoder.encode(accountid, "UTF-8"))
                            .append("&expected_price=")
                            .append(URLEncoder.encode(price, "UTF-8"))
                            .append("&id_sell_truck=")
                            .append(URLEncoder.encode(Str_id_sell_truck, "UTF-8"))
                            .append(builder.append("&access_token=")).append(sharedPreferences.getString("access_token",""));

                    String res = parser.excutePost(TruckApp.ExpectedPriceURL,
                            builder.toString());
                    System.out.println("Params:" + builder.toString()
                            + "book truck o/p" + res);
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
                TruckApp.checkPDialog(pDialog);
                if (result != null) {
                    try {
                        if (0 == result.getInt("status")) {
							Toast.makeText(context, "Failed to book truck",
									Toast.LENGTH_LONG).show();
                        }else if (2 == result.getInt("status")) {
                            //TruckApp.logoutAction(BuyTruckActivity.this);
                            SharedPreferences sharedPreferences = context.getSharedPreferences(
                                    context.getResources().getString(R.string.app_name), MODE_PRIVATE);
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
                            startActivity(new Intent(context,
                                    LoginActivity.class));
                            finish();
                        }  else if (1 == result.getInt("status")) {
                            TruckApp.checkDialog(truckDialog);
                            JSONObject jsonObj = trucks.getJSONObject(position);
                            jsonObj.put("customer_status", "1");
                            trucks.put(position,jsonObj);
                            notifyDataSetChanged();
                            Toast.makeText(context,"Thanks for booking. Will contact you soon.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        System.out.println("ex in get leads" + e.toString());
                    }
                } else {
                    Toast.makeText(context,getResources().getString(R.string.exceptionmsg),
                            Toast.LENGTH_LONG).show();
                }
            }
        }

    }


    private class GetImges extends AsyncTask<String, String, JSONObject> {

        String Str_id_sell_truck;

        public GetImges(String Str_id_sell_truck) {

            this.Str_id_sell_truck = Str_id_sell_truck;

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Loading Images...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject json = null;
            try {
                StringBuilder builder = new StringBuilder();
                builder.append("&id_sell_truck=").append(URLEncoder.encode(Str_id_sell_truck, "UTF-8"))
                        .append(builder.append("&access_token=")).append(sharedPreferences.getString("access_token",""));


                String res = parser.excutePost(TruckApp.Truck_IMS,builder.toString());
                System.out.println("Params:" + builder.toString()
                        + "book truck o/p" + res);
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
            TruckApp.checkPDialog(pDialog);

            if (result != null) {
                try {
                    if (0 == result.getInt("status")) {
						Toast.makeText(context, "Failed to book truck",
								Toast.LENGTH_LONG).show();
                    }else if (2 == result.getInt("status")) {
                        //TruckApp.logoutAction(BuyTruckActivity.this);
                        SharedPreferences sharedPreferences = context.getSharedPreferences(
                                context.getResources().getString(R.string.app_name), MODE_PRIVATE);
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
                        startActivity(new Intent(context,
                                LoginActivity.class));
                        finish();
                    }  else if (1 == result.getInt("status")) {
                        TruckApp.checkDialog(pDialog);


                        JSONObject JOBj = result.getJSONObject("data");

                        imag_urlF = JOBj.getString("truck_front_pic");
                        imag_urlB = JOBj.getString("truck_back_pic");

                        imag_urlTFLP = JOBj.getString("tyres_front_left_pic");
                        imag_urlTFRP = JOBj.getString("tyres_front_right_pic");
                        imag_urlTBLP = JOBj.getString("tyres_back_left_pic");
                        imag_urlTBRP = JOBj.getString("tyres_back_right_pic");

                        imag_urlOP1 = JOBj.getString("other_pic_1");
                        imag_urlOP2 = JOBj.getString("other_pic_2");




                        displayDocDialog();

                    }
                } catch (Exception e) {

                    System.out.println("ex in get leads" + e.toString());

                }
            } else {
                Toast.makeText(context,
                        getResources().getString(R.string.exceptionmsg),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void displayDocDialog() {

        if(imag_urlF.equals("") && imag_urlB.equals("")  && imag_urlTFLP.equals("") && imag_urlTFRP.equals("")
                && imag_urlTBLP.equals("") 	&& imag_urlTBRP.equals("") && imag_urlOP1.equals("")
                && imag_urlOP2.equals("")){
            Toast.makeText(getApplicationContext(), "No pics to display", Toast.LENGTH_LONG).show();
        }else{
            uploadDocDialog.show();
            if (imag_urlF.equals("")) {
                Picasso.with(context).load(R.drawable.noimageicon).into(Front);
            } else {
                Picasso.with(context).load(imag_urlF).into(Front);
                Front.setVisibility(View.VISIBLE);
            }

            if (imag_urlB.equals("")) {
                Picasso.with(context).load(R.drawable.noimageicon).into(Back);
            } else {
                Picasso.with(context).load(imag_urlB).into(Back);
                Back.setVisibility(View.VISIBLE);
            }

            if (imag_urlTFLP.equals("")) {
                Picasso.with(context).load(R.drawable.noimageicon)
                        .into(FrontTyreLeft);
            } else {
                Picasso.with(context).load(imag_urlTFLP).into(FrontTyreLeft);
                FrontTyreLeft.setVisibility(View.VISIBLE);
            }

            if (imag_urlTFRP.equals("")) {
                Picasso.with(context).load(R.drawable.noimageicon)
                        .into(FrontTyreRight);
            } else {
                Picasso.with(context).load(imag_urlTFRP).into(FrontTyreRight);
                FrontTyreRight.setVisibility(View.VISIBLE);

            }

            if (imag_urlTBLP.equals("")) {
                Picasso.with(context).load(R.drawable.noimageicon)
                        .into(backTyreLeft);
            } else {
                Picasso.with(context).load(imag_urlTBLP).into(backTyreLeft);
                FrontTyreRight.setVisibility(View.VISIBLE);
            }

            if (imag_urlTBRP.equals("")) {
                Picasso.with(context).load(R.drawable.noimageicon)
                        .into(BackTyreRight);

            } else {
                Picasso.with(context).load(imag_urlTBRP).into(BackTyreRight);
                BackTyreRight.setVisibility(View.VISIBLE);
            }

            if (imag_urlOP1.equals("")) {
                Picasso.with(context).load(R.drawable.noimageicon).into(Otherpic1);
            } else {
                Picasso.with(context).load(imag_urlOP1).into(Otherpic1);
                Otherpic1.setVisibility(View.VISIBLE);
            }

            if (imag_urlOP2.equals("")) {
                Picasso.with(context).load(R.drawable.noimageicon).into(Otherpic2);
            } else {
                Picasso.with(context).load(imag_urlOP2).into(Otherpic2);
                Otherpic2.setVisibility(View.VISIBLE);
            }

        }
    }
}
