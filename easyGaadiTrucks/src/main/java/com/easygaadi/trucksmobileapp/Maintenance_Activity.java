package com.easygaadi.trucksmobileapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class Maintenance_Activity extends AppCompatActivity {
    TextView maintDatelblTV,maintnce_dateTV,maintnce_trunknum_lbl,maintnce_lbl,maintnce_rType_lbl,maintnce_cost_lbl,
            maintnce_shed_lbl,maintnce_are_lbl;
    String[] country = { "Trunck Number","India", "USA", "China", "Japan", "Other",  };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_);



        final Spinner spin = (Spinner) findViewById(R.id.spnr_trunknum);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.erp_view_spinner_item,country);
        aa.setDropDownViewResource(R.layout.erp_view_spinner_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        maintnce_dateTV = (TextView)findViewById(R.id.maintnce_date);
        maintDatelblTV = (TextView)findViewById(R.id.maintnce_date_lbl);
        maintnce_trunknum_lbl = (TextView)findViewById(R.id.maintnce_trunknum_lbl);
        maintnce_lbl = (TextView)findViewById(R.id.maintnce_rType_lbl);
        maintnce_rType_lbl = (TextView)findViewById(R.id.maintnce_rType_lbl);
        maintnce_cost_lbl = (TextView)findViewById(R.id.maintnce_cost_lbl);
        maintnce_shed_lbl = (TextView)findViewById(R.id.maintnce_shed_lbl);
        maintnce_are_lbl = (TextView)findViewById(R.id.maintnce_are_lbl);
        initilizationView();
        maintnce_dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(maintnce_dateTV);
            }
        });

        AdapterView.OnItemSelectedListener countrySelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container,int position, long id) {
                if(spinner.getId() == R.id.spnr_trunknum){
                    String selected = spinner.getItemAtPosition(position).toString();
                    if(selected.equalsIgnoreCase("Trunck Number"))
                    {
                        maintnce_trunknum_lbl.setVisibility(View.INVISIBLE);
                    }else{
                        maintnce_trunknum_lbl.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

                if(spin.getId() == arg0.getId()) {
                    if (arg0.getSelectedItemPosition() != 0) {
                        maintnce_trunknum_lbl.setVisibility(View.VISIBLE);
                    }
                }

            }
        };
        spin.setOnItemSelectedListener(countrySelectedListener);
    }

    EditText maintnce_idET,maintnce_rTypeET,maintnce_costET,maintnce_shedET,maintnce_areET;
    public void initilizationView() {
        //frghtET,AdvnceET,BalnceET
        //erp_frghtamt,erp_advamt,erp_balamt
        maintnce_idET = (EditText) findViewById(R.id.maintnce_PType);
        maintnce_rTypeET = (EditText) findViewById(R.id.maintnce_rType);
        maintnce_costET = (EditText) findViewById(R.id.maintnce_cost);
        maintnce_shedET = (EditText) findViewById(R.id.maintnce_shed);
        maintnce_areET = (EditText) findViewById(R.id.maintnce_are);
        chnageTextView(maintnce_idET);
        chnageTextView(maintnce_rTypeET);
        chnageTextView(maintnce_costET);
        chnageTextView(maintnce_shedET);
        chnageTextView(maintnce_areET);
    }


    public void chnageTextView(final EditText etview){

        etview.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if(R.id.maintnce_PType == etview.getId()) {
                    if (string.trim().length() != 0) {
                        maintnce_lbl.setVisibility(View.VISIBLE);
                        slideUp(maintnce_lbl);
                    }else{
                        maintnce_lbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.maintnce_rType == etview.getId()) {
                    if (string.trim().length() != 0) {
                        maintnce_rType_lbl.setVisibility(View.VISIBLE);
                        slideUp(maintnce_rType_lbl);
                    }else{
                        maintnce_rType_lbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.maintnce_cost == etview.getId()) {
                    if (string.trim().length() != 0) {
                        maintnce_cost_lbl.setVisibility(View.VISIBLE);
                        slideUp(maintnce_cost_lbl);
                    }else{
                        maintnce_cost_lbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.maintnce_shed == etview.getId()) {
                    if (string.trim().length() != 0) {
                        maintnce_shed_lbl.setVisibility(View.VISIBLE);
                        slideUp(maintnce_shed_lbl);
                    }else{
                        maintnce_shed_lbl.setVisibility(View.INVISIBLE);
                    }
                }else if(R.id.maintnce_are == etview.getId()) {
                    if (string.trim().length() != 0) {
                        maintnce_are_lbl.setVisibility(View.VISIBLE);
                        slideUp(maintnce_are_lbl);
                    }else{
                        maintnce_are_lbl.setVisibility(View.INVISIBLE);
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
    public void callback(View view){
        finish();
    }
    public void callTruckAct(View view){
        startActivity(new Intent(Maintenance_Activity.this,Trunck_Activity.class));
    }
    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(Maintenance_Activity.this,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //(view).setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                if(Tview.getId() == R.id.maintnce_date){
                    maintnce_dateTV.setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                    if(maintnce_dateTV.getText().toString().length()>0){
                        maintDatelblTV.setVisibility(View.VISIBLE);
                        slideUp(maintDatelblTV);
                    }else {
                        maintDatelblTV.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }, year, month, day);


        dpd.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    if(Tview.getId() == R.id.trip_id){
                        if(maintnce_dateTV.getText().toString().length()>0){
                            maintnce_dateTV.setVisibility(View.VISIBLE);
                        }else {
                            maintnce_dateTV.setVisibility(View.INVISIBLE);
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
}
