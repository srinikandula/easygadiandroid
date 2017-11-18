package com.easygaadi.trucksmobileapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.erp.DriverList;
import com.easygaadi.erp.ERP_DashBroad;
import com.easygaadi.erp.ExpenseList;
import com.easygaadi.erp.ExpensesList;
import com.easygaadi.erp.MaintenanceList;
import com.easygaadi.erp.PartList;
import com.easygaadi.erp.PaymentsList;
import com.easygaadi.erp.TripList;
import com.easygaadi.erp.TruckList;
import com.google.android.gms.ads.AdRequest;

import com.easygaadi.activities.GroupsActivity;
import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.interfaces.TrucksAsyncInterface;
import com.easygaadi.models.GroupItemResponse;
import com.easygaadi.network.CustomRetrofitSpiceService;
import com.easygaadi.network.GroupItemsSpiceRequest;
import com.easygaadi.network.SaveCardSettings;
import com.easygaadi.utils.Constants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.PendingRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ERP_Activitys extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    Context context;
    Editor editor;
    private ConnectionDetector detectCnnection;
    JSONParser parser;

    // nav drawer title
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;


    private Menu menu;
    Toolbar toolbar;

    private AdView adView;

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.erp_layouts);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;


        // enabling action bar app icon and behaving it as toggle button
        // TODO: Remove the redundant calls to getSupportActionBar()
        // and use variable actionBar instead
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
       /* if (savedInstanceState == null) {
            Fragment fragment = new TruckList();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame_container, fragment, "Truck");
            fragmentTransaction.commitAllowingStateLoss();

            setTitle("Truck List");
        }*/
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.erp_menu, menu);
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_reports:
                startActivity(new Intent(ERP_Activitys.this,ERP_Report.class));
                break;
            case R.id.action_home:
                finish();
                break;
        /*case R.id.action_report:
            reportproblem();
			break;*/
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //TruckApp.getInstance().trackScreenView("Home Screen");
        /*if (adView != null) {
            adView.resume();
        }*/
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        setTitle(item.getTitle() +"List");
        Fragment fragment = null;
        if (id == R.id.nav_dash) {
            fragment =  new ERP_DashBroad();
        } else if (id == R.id.nav_truck) {
            fragment =  new TruckList();
        }else if (id == R.id.nav_driver) {
            fragment =  new DriverList();
        } else if (id == R.id.nav_party) {
            fragment =  new PartList();
        } else if (id == R.id.nav_trip) {
            fragment =  new TripList();
        }else if (id == R.id.nav_maint) {
            fragment =  new MaintenanceList();
        }else if (id == R.id.nav_exps) {
            fragment =  new ExpenseList();
        }else if (id == R.id.nav_payment) {
            fragment =  new PaymentsList();
        }


        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame_container, fragment, "ERP");
        fragmentTransaction.commitAllowingStateLoss();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}