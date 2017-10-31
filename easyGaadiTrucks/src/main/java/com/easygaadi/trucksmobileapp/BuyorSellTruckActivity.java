package com.easygaadi.trucksmobileapp;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

@SuppressWarnings("deprecation")
public class BuyorSellTruckActivity extends TabActivity {

	TabHost tabHost;
	Context context;
	ConnectionDetector detectCnnection;
	private InterstitialAd mInterstitialAd;
	private AdView adView;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buysell);
		adView = (AdView)findViewById(R.id.adView);
		//intializeBannerAd();
		//intializeGoogleAd();

		 tabHost = getTabHost();
//		 tabHost.addTab(tabHost.newTabSpec("tab1")
//	                .setIndicator("Buy Truck")
//	                .setContent(new Intent(this, BuyTruckActivity.class)
//	                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
//		 
//		 tabHost.addTab(tabHost.newTabSpec("tab2")
//                .setIndicator("Sell Truck")
//                .setContent(new Intent(this, SellTruckActivity.class)
//                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
		 
			// Tab for Buy
			TabSpec Buy = tabHost.newTabSpec("Sell Truck");
			// setting Title and Icon for the Tab
			Buy.setIndicator("Buy Truck");
			Intent photosIntent = new Intent(this, BuyTruckActivity.class);
			Buy.setContent(photosIntent);

			// Tab for Sell
			TabSpec selltruck = tabHost.newTabSpec("Buy");
			selltruck.setIndicator("Sell Truck");
			Intent songsIntent = new Intent(this, SellTruckActivity.class);
			selltruck.setContent(songsIntent);

			tabHost.addTab(Buy); // Adding Buy
			tabHost.addTab(selltruck);

	}


	private void intializeGoogleAd() {
		mInterstitialAd = new InterstitialAd(this);

		// set the ad unit ID
		mInterstitialAd.setAdUnitId(getString(R.string.eg_interstital));

		AdRequest adRequest = new AdRequest.Builder().build();

		// Load ads into Interstitial Ads
		mInterstitialAd.loadAd(adRequest);

		mInterstitialAd.setAdListener(new AdListener() {
			public void onAdLoaded() {
				showInterstitial();
			}
		});

	}

	private void intializeBannerAd() {
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}


	private void showInterstitial() {
		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TruckApp.getInstance().trackScreenView("BuyOrSellTruck Screen");
		/*if (adView != null) {
			adView.resume();
		}*/

	}

	@Override
	protected void onPause() {
		/*if (adView != null) {
			adView.pause();
		}*/
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		/*if (adView != null)
		{ adView.destroy(); }*/
		super.onDestroy();
	}
}
