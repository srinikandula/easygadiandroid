package com.easygaadi.trucksmobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Toast.makeText(getApplicationContext(),"This page will be redirected to your payment gateway",Toast.LENGTH_LONG).show();
    }
}
