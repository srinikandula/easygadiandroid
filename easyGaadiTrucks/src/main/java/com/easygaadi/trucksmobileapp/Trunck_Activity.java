package com.easygaadi.trucksmobileapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
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

public class Trunck_Activity extends AppCompatActivity {

    String[] country = { "Trunck Type","India", "USA", "China", "Japan", "Other",  };
    TextView truck_type_lbl,truck_modelTV,truck_modellblTV,truck_duetaxTV,truck_duetaxlbltv,
            truck_fexpireTV, truck_fexpirelbltv,truck_insexpirelbltv,truck_insexpireTV,
            truck_perexpirelblTV,truck_perexpireTV,truck_pollexpirelblTV,truck_pollexpireTV,
            truck_reg_lblTV;
    EditText truck_regET;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trunck_);

        truck_type_lbl = (TextView)findViewById(R.id.truck_type_lbl);
        truck_modelTV = (TextView)findViewById(R.id.truck_model);
        truck_modellblTV = (TextView)findViewById(R.id.truck_modellbl);
        truck_duetaxTV = (TextView)findViewById(R.id.truck_duetax);
        truck_duetaxlbltv = (TextView)findViewById(R.id.truck_duetaxlbl);
        truck_fexpireTV = (TextView)findViewById(R.id.truck_fexpire);
        truck_fexpirelbltv = (TextView)findViewById(R.id.truck_fexpirelbl);
        truck_insexpirelbltv = (TextView)findViewById(R.id.truck_insexpirelbl);
        truck_insexpireTV = (TextView)findViewById(R.id.truck_insexpire);
        truck_perexpireTV = (TextView)findViewById(R.id.truck_perexpire);
        truck_perexpirelblTV = (TextView)findViewById(R.id.truck_perexpirelbl);
        truck_pollexpirelblTV = (TextView)findViewById(R.id.truck_pollexpirelbl);
        truck_pollexpireTV = (TextView)findViewById(R.id.truck_pollexpire);
        truck_reg_lblTV = (TextView)findViewById(R.id.truck_reg_lbl);


        truck_regET = (EditText)findViewById(R.id.truck_reg);

        chnageTextView(truck_regET);

        ListenerDate(truck_modelTV);
        ListenerDate(truck_duetaxTV);
        ListenerDate(truck_fexpireTV);
        ListenerDate(truck_insexpireTV);
        ListenerDate(truck_perexpireTV);
        ListenerDate(truck_pollexpireTV);

        final Spinner spin = (Spinner) findViewById(R.id.spnr_trunknum);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.erp_view_spinner_item,country);
        aa.setDropDownViewResource(R.layout.erp_view_spinner_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        AdapterView.OnItemSelectedListener countrySelectedListener = new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> spinner, View container, int position, long id) {
                if(spinner.getId() == R.id.spnr_trunknum){
                    String selected = spinner.getItemAtPosition(position).toString();
                    if(selected.equalsIgnoreCase("Trunck Type"))
                    {
                        truck_type_lbl.setVisibility(View.INVISIBLE);
                    }else{
                        truck_type_lbl.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

                if(spin.getId() == arg0.getId()) {
                    if (arg0.getSelectedItemPosition() != 0) {
                        truck_type_lbl.setVisibility(View.VISIBLE);
                    }
                }

            }
        };
        spin.setOnItemSelectedListener(countrySelectedListener);
    }

    public void callback(View view){
        finish();
    }

    public void chnageTextView(final EditText etview){

        etview.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                if(R.id.truck_reg == etview.getId()) {
                    if (string.trim().length() != 0) {
                        truck_reg_lblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_reg_lblTV);
                    }else{
                        truck_reg_lblTV.setVisibility(View.INVISIBLE);
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



    public void ListenerDate(final TextView dateTV)
    {
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(dateTV);
            }
        });
    }

    public void showDatePicker(final View Tview)
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(Trunck_Activity.this,R.style.MyDialogTheme ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //(view).setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                if(Tview.getId() == R.id.truck_model){
                    truck_modelTV.setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                    if(truck_modelTV.getText().toString().length()>0){
                        truck_modellblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_modellblTV);
                    }else {
                        truck_modellblTV.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_duetax){
                    truck_duetaxTV.setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                    if(truck_duetaxTV.getText().toString().length()>0){
                        truck_duetaxlbltv.setVisibility(View.VISIBLE);
                        slideUp(truck_duetaxlbltv);
                    }else {
                        truck_duetaxlbltv.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_fexpire){
                    truck_fexpireTV.setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                    if(truck_fexpireTV.getText().toString().length()>0){
                        truck_fexpirelbltv.setVisibility(View.VISIBLE);
                        slideUp(truck_fexpirelbltv);
                    }else {
                        truck_fexpirelbltv.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_insexpire){
                    truck_insexpireTV.setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                    if(truck_insexpireTV.getText().toString().length()>0){
                        truck_insexpirelbltv.setVisibility(View.VISIBLE);
                        slideUp(truck_insexpirelbltv);
                    }else {
                        truck_insexpirelbltv.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_perexpire){
                    truck_perexpireTV.setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                    if(truck_perexpireTV.getText().toString().length()>0){
                        truck_perexpirelblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_perexpirelblTV);
                    }else {
                        truck_perexpirelblTV.setVisibility(View.INVISIBLE);
                    }
                }else if(Tview.getId() == R.id.truck_pollexpire){
                    truck_pollexpireTV.setText(view.getDayOfMonth()+"/"+view.getMonth()+"/"+view.getYear());
                    if(truck_pollexpireTV.getText().toString().length()>0){
                        truck_pollexpirelblTV.setVisibility(View.VISIBLE);
                        slideUp(truck_pollexpirelblTV);
                    }else {
                        truck_pollexpirelblTV.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }, year, month, day);


        dpd.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    if(Tview.getId() == R.id.truck_model){
                        if(truck_modelTV.getText().toString().length()>0){
                            truck_modellblTV.setVisibility(View.VISIBLE);
                        }else {
                            truck_modellblTV.setVisibility(View.INVISIBLE);
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
