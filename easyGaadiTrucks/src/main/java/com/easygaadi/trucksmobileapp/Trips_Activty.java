package com.easygaadi.trucksmobileapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class Trips_Activty extends AppCompatActivity  {

    String[] country = { "Trunck Number","India", "USA", "China", "Japan", "Other",  };
    String[] loadarr = { "Book Load","5000", "20000", "40000", "355122", "1000",  };
    String[] dribverName = { "Driver Name","Sunil", "Vignesh", "Riyaz", "Sharrath", "Ayyyapa",  };
    String[] payment = { "Payment Type","Cheque", "Chash"  };

    TextView tripDate,tripFromDate,tripToDate,trip_lbl,trip_frmdatelbl,trip_todatelbl,trip_trunklbl, trip_bkloadlbl,
            trip_drnmelbl, trip_pymtbl,trip_diesalamtlbl,trip_tollgateamtlbl,trip_tonnagelbl,trip_ratelbl,trip_frghtbl,trip_advbalbl,trip_balbl,trip_remrksl;
    EditText trip_diesalamtET,trip_tollgateamtET,trip_tonnageamtET,trip_rateET,frghtET,AdvnceET,BalnceET,erp_remarkET;
    Button formLL,submit_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips__activty);

        trip_lbl =(TextView)findViewById(R.id.trip_lbl);
        trip_trunklbl=(TextView)findViewById(R.id.trip_trunklbl);
        trip_frmdatelbl=(TextView)findViewById(R.id.trip_frmdatelbl);
        trip_todatelbl=(TextView)findViewById(R.id.trip_todatelbl);

        trip_bkloadlbl=(TextView)findViewById(R.id.trip_bkloadlbl);
        trip_drnmelbl=(TextView)findViewById(R.id.trip_drnmelbl);
        trip_diesalamtlbl=(TextView)findViewById(R.id.trip_diesalamtlbl);
        trip_tonnagelbl=(TextView)findViewById(R.id.trip_tonnagelbl);
        trip_ratelbl=(TextView)findViewById(R.id.trip_ratelbl);
        trip_tollgateamtlbl=(TextView)findViewById(R.id.trip_tollgateamtlbl);
        trip_frghtbl=(TextView)findViewById(R.id.trip_frghtbl);
        trip_advbalbl=(TextView)findViewById(R.id.trip_advbalbl);
        trip_balbl=(TextView)findViewById(R.id.trip_balbl);
        trip_pymtbl=(TextView)findViewById(R.id.trip_pymtbl);
        trip_remrksl=(TextView)findViewById(R.id.trip_remrksl);
        initilizationView();

        final Spinner spin = (Spinner) findViewById(R.id.spnr_trunknum);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.erp_view_spinner_item,country);
        aa.setDropDownViewResource(R.layout.erp_view_spinner_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);


        Spinner load = (Spinner) findViewById(R.id.spnr_load);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter loadaa = new ArrayAdapter(this,R.layout.erp_view_spinner_item,loadarr);
        loadaa.setDropDownViewResource(R.layout.erp_view_spinner_item);
        //Setting the ArrayAdapter data on the Spinner
        load.setAdapter(loadaa);


        Spinner drspin = (Spinner) findViewById(R.id.spnr_drivername);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter dribverNameaa = new ArrayAdapter(this,R.layout.erp_view_spinner_item,dribverName);
        dribverNameaa.setDropDownViewResource(R.layout.erp_view_spinner_item);
        //Setting the ArrayAdapter data on the Spinner
        drspin.setAdapter(dribverNameaa);

        Spinner payspin = (Spinner) findViewById(R.id.spnr_paymnttype);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter paytypeeaa = new ArrayAdapter(this,R.layout.erp_view_spinner_item,payment);
        paytypeeaa.setDropDownViewResource(R.layout.erp_view_spinner_item);
        //Setting the ArrayAdapter data on the Spinner
        payspin.setAdapter(paytypeeaa);

        tripDate = (TextView)findViewById(R.id.trip_id);
        tripFromDate = (TextView)findViewById(R.id.trip_frm_date);
        tripToDate = (TextView)findViewById(R.id.trip_to_date);

        tripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(tripDate);
            }
        });

        tripFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(tripFromDate);
            }
        });
        tripToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(tripToDate);
            }
        });


        AdapterView.OnItemSelectedListener countrySelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,int position, long id) {
                if(spinner.getId() == R.id.spnr_trunknum){
                    String selected = spinner.getItemAtPosition(position).toString();
                    if(selected.equalsIgnoreCase("Trunck Number"))
                    {
                        trip_trunklbl.setVisibility(View.INVISIBLE);
                    }else{
                        trip_trunklbl.setVisibility(View.VISIBLE);
                    }
                }else if(spinner.getId() == R.id.spnr_load){
                    String selected = spinner.getItemAtPosition(position).toString();
                    if(selected.equalsIgnoreCase("Book Load"))
                    {
                        trip_bkloadlbl.setVisibility(View.INVISIBLE);
                    }else{
                        trip_bkloadlbl.setVisibility(View.VISIBLE);
                    }
                }else if(spinner.getId() == R.id.spnr_drivername){
                    String selected = spinner.getItemAtPosition(position).toString();
                    if(selected.equalsIgnoreCase("Driver Name"))
                    {
                        trip_drnmelbl.setVisibility(View.INVISIBLE);
                    }else{
                        trip_drnmelbl.setVisibility(View.VISIBLE);
                    }
                }else if(spinner.getId() == R.id.spnr_paymnttype){
                    String selected = spinner.getItemAtPosition(position).toString();
                    if(selected.equalsIgnoreCase("Payment Type"))
                    {
                        trip_pymtbl.setVisibility(View.INVISIBLE);
                    }else{
                        trip_pymtbl.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

                if(spin.getId() == arg0.getId()) {
                    if (arg0.getSelectedItemPosition() != 0) {
                        trip_trunklbl.setVisibility(View.VISIBLE);
                    }
                }else if(R.id.spnr_load == arg0.getId()) {
                    if (arg0.getSelectedItemPosition() != 0) {
                        trip_bkloadlbl.setVisibility(View.VISIBLE);
                    }
                }else if(R.id.spnr_drivername == arg0.getId()) {
                    if (arg0.getSelectedItemPosition() != 0) {
                        trip_drnmelbl.setVisibility(View.VISIBLE);
                    }
                }else if(R.id.spnr_paymnttype == arg0.getId()) {
                    if (arg0.getSelectedItemPosition() != 0) {
                        trip_pymtbl.setVisibility(View.VISIBLE);
                    }
                }

            }
        };

        // Setting ItemClick Handler for Spinner Widget
        spin.setOnItemSelectedListener(countrySelectedListener);
        load.setOnItemSelectedListener(countrySelectedListener);
        drspin.setOnItemSelectedListener(countrySelectedListener);
        payspin.setOnItemSelectedListener(countrySelectedListener);
    }



    public void initilizationView(){
        //frghtET,AdvnceET,BalnceET
        //erp_frghtamt,erp_advamt,erp_balamt
        trip_diesalamtET = (EditText)findViewById(R.id.trip_diesalamt);
        trip_tollgateamtET = (EditText)findViewById(R.id.trip_tollgateamt);
        trip_tonnageamtET = (EditText)findViewById(R.id.trip_tonnageamt);
        trip_rateET = (EditText)findViewById(R.id.trip_rate);
        frghtET = (EditText)findViewById(R.id.erp_frghtamt);
        AdvnceET = (EditText)findViewById(R.id.erp_advamt);
        BalnceET = (EditText)findViewById(R.id.erp_balamt);
        erp_remarkET = (EditText)findViewById(R.id.erp_remark);
        chnageTextView(trip_diesalamtET);
        chnageTextView(trip_tollgateamtET);
        chnageTextView(trip_tonnageamtET);
        chnageTextView(trip_rateET);
        chnageTextView(frghtET);
        chnageTextView(AdvnceET);
        chnageTextView(BalnceET);
        chnageTextView(erp_remarkET);

        submit_btn = (Button)findViewById(R.id.submit_btn);
        formLL = (Button)findViewById(R.id.clr_btn);
        formLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup group = (ViewGroup)findViewById(R.id.formLL);
                clearForm(group);

                if (Build.VERSION.SDK_INT >= 11) {
                    recreate();
                } else {
                    Intent intent = getIntent();
                    intent.replaceExtras(new Bundle());
                    getIntent().setAction("");
                    getIntent().setData(null);
                    getIntent().setFlags(0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);

                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Trips_Activty.this,Driver_Activity.class));
            }
        });


    }

    public void chnageTextView(final EditText etview){

        etview.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if(R.id.trip_diesalamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_diesalamtlbl.setVisibility(View.VISIBLE);
                        slideUp(trip_diesalamtlbl);
                    }else{
                        trip_diesalamtlbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.trip_tollgateamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_tollgateamtlbl.setVisibility(View.VISIBLE);
                        slideUp(trip_tollgateamtlbl);
                    }else{
                        trip_tollgateamtlbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.trip_tonnageamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_tonnagelbl.setVisibility(View.VISIBLE);
                        slideUp(trip_tonnagelbl);
                    }else{
                        trip_tonnagelbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.trip_rate == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_ratelbl.setVisibility(View.VISIBLE);
                        slideUp(trip_ratelbl);
                    }else{
                        trip_ratelbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.erp_frghtamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_frghtbl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_frghtbl)));
                    }else{
                        trip_frghtbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.erp_advamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_advbalbl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_advbalbl)));
                    }else{
                        trip_advbalbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.erp_balamt == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_balbl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_balbl)));
                    }else{
                        trip_balbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.erp_remark == etview.getId()) {
                    if (string.trim().length() != 0) {
                        trip_remrksl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_remrksl)));
                    }else{
                        trip_remrksl.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

    }


    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(Trips_Activty.this,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //(view).setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                if(Tview.getId() == R.id.trip_id){
                    tripDate.setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                    if(tripDate.getText().toString().length()>0){
                        trip_lbl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_lbl)));
                    }else {
                        trip_lbl.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.trip_frm_date)
                {
                    tripFromDate.setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                    if(tripFromDate.getText().toString().length()>0){
                        trip_frmdatelbl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_frmdatelbl)));
                    }else {
                        trip_frmdatelbl.setVisibility(View.INVISIBLE);

                    }

                }else if(Tview.getId() == R.id.trip_to_date)
                {
                    tripToDate.setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                    if(tripToDate.getText().toString().length()>0){
                        trip_todatelbl.setVisibility(View.VISIBLE);
                        slideUp(((TextView)findViewById(R.id.trip_todatelbl)));
                    }else {
                        trip_todatelbl.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }, year, month, day);


        dpd.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    if(Tview.getId() == R.id.trip_id){
                        if(tripDate.getText().toString().length()>0){
                            trip_lbl.setVisibility(View.VISIBLE);
                        }else {
                            trip_lbl.setVisibility(View.INVISIBLE);
                        }
                    }else if(Tview.getId() == R.id.trip_frm_date)
                    {
                        if(tripFromDate.getText().toString().length()>0){
                            trip_frmdatelbl.setVisibility(View.VISIBLE);
                        }else {
                            trip_frmdatelbl.setVisibility(View.INVISIBLE);
                        }
                    }else if(Tview.getId() == R.id.trip_to_date)
                    {
                        if(tripToDate.getText().toString().length()>0){
                            trip_todatelbl.setVisibility(View.VISIBLE);

                        }else {
                            trip_todatelbl.setVisibility(View.INVISIBLE);

                        }
                    }
                }
            }
        });

        dpd.show();


    }

    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }




    private void clearForm(ViewGroup group)
    {

        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }else if (view instanceof TextView) {
                ((TextView)view).setText("");
            }else if (view instanceof Spinner) {
                ((Spinner)view).setSelection(0);
            }


            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }


    }
}

