package com.easygaadi.trucksmobileapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;

public class RootActivity extends AppCompatActivity {

    public String USERNAME="username";
    public String PASSWORD="password";
    public String CUSTOMERID="customerID";
    public String FUEL_USERNAME_KEY   = "fuenulfueleg";
    public String FUEL_PASSWORD_KEY   = "fpuwedlfueleg";
    public String FUEL_CUSTOMERID_KEY = "fcuedilfueleg";

    public String TOLL_USERNAME_KEY   = "tnolultolleg";
    public String TOLL_PASSWORD_KEY   = "tdowlpltolleg";
    public String TOLL_CUSTOMERID_KEY = "todlilctolleg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public String encrypt(String input) {
        // Simple encryption, not very strong!
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }
}
