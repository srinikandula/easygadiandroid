package com.easygaadi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.easygaadi.fragments.GroupsFragment;
import com.easygaadi.trucksmobileapp.CreateGroupActivity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TripsActivity;
import com.easygaadi.trucksmobileapp.TruckApp;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by ibraincpu6 on 05-12-2016.
 */

public class GroupsActivity extends AppCompatActivity {
    Toolbar toolbar;
    private InterstitialAd mInterstitialAd;
    private AdView adView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        adView = (AdView)findViewById(R.id.adView);
        intializeBannerAd();
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new GroupsFragment()).commit();
        }

        intializeGoogleAd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_groups, menu);
        return true;
    }

    private void intializeGoogleAd() {
        mInterstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        mInterstitialAd.setAdUnitId(Constants.EG_INTERSTITAL_ID);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (adView != null)
        { adView.destroy(); }
        super.onDestroy();
    }


    private void intializeBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        TruckApp.getInstance().trackScreenView("Groups Screen");
        if (adView != null) {
            adView.resume();
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_trips:
                startActivity(new Intent(this, TripsActivity.class));
                break;
            case R.id.action_add:
                startActivity(new Intent(this, CreateGroupActivity.class));
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
