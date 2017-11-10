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
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

public class HomeScreenActivity extends RootActivity implements TrucksAsyncInterface{

    ImageView gps_btn, trucks_btn, info_btn, postload_btn, orders_btn,
            loadstatus_btn, truckavaliability;
    int locationCount = 0;

    LinearLayout mainLayout;
    RelativeLayout gpsLayout, trucksLayout, infoLayout, loadstatusLayout,
            ordersLayout, avaTrucksLayout, postLoadLayout;
    EditText contactName_et, contactMobile_et, contactEmail_et,
            contactDescription, new_pwd_et, repwd_et;
    //EditText old_pwd_et;
    ImageView closeDialog_iv, chnclsDialog_iv;
    Button updateProfile, changePassword;
    Context context;
    LinearLayout part2Layout, part3Layout;

    Editor editor;
    private ConnectionDetector detectCnnection;
    JSONParser parser;
    ProgressDialog pDialog;
    SharedPreferences sharedPreferences;
    Dialog profileDialog, chnPasswordDialog,generalSettingsDialog;
    //String[] menutitles;
    //TypedArray menuIcons;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CAMERA};


    // nav drawer title
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private List<RowItem> rowItems;
    private CustomAdapter adapter;

    ArrayList<String> names;
    ArrayList<Integer> images;
    ArrayList<Integer> ids;

    ArrayList<String> drawer_names;
    ArrayList<Integer> drawer_images;
    ArrayList<Integer> drawer_ids;

    GridView gridView;
    ProgressDialog progressDialog;
    boolean isProgressing = false;
    SpiceManager spiceManager = new SpiceManager(CustomRetrofitSpiceService.class);
    String cache = "";
    Toolbar toolbar;
    String groupID,groupName;

    private AdView adView;




    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
        if (isProgressing) {
            spiceManager.addListenerIfPending(GroupItemResponse.class, cache, new ListOfGroupsListener());
        }
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        names = new ArrayList<String>();
        images = new ArrayList<Integer>();
        ids = new ArrayList<Integer>();
        drawer_names = new ArrayList<String>();
        drawer_images = new ArrayList<Integer>();
        drawer_ids = new ArrayList<Integer>();
        gridView = (GridView) findViewById(R.id.gridView);
        detectCnnection = new ConnectionDetector(context);
        parser = JSONParser.getInstance();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        sharedPreferences = getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();


        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

       // intializeAd();
        //intializieProfileDialog();
        intializieSettingsDialog();

        int gps = sharedPreferences.getInt("gps", -1);
        int truck = sharedPreferences.getInt("truck", -1);
        int loads = sharedPreferences.getInt("loads", -1);
        groupID = sharedPreferences.getString("groupID", "0");
        groupName = sharedPreferences.getString("groupName","Group");
        int post = sharedPreferences.getInt("postload", -1);
        int avatruck = sharedPreferences.getInt("truckavailable", -1);
        int loadstatus = sharedPreferences.getInt("loadstatus", -1);
        int orders = sharedPreferences.getInt("orders", -1);
        int buyselltrucks = sharedPreferences.getInt("buyselltrucks", -1);
        int distanceReport = sharedPreferences.getInt("distanceReport", -1);
        int createGroup = sharedPreferences.getInt("createGroup", -1);
        int settings = sharedPreferences.getInt("settings", -1);
        int dashboard = sharedPreferences.getInt("dashboard", -1);
        int shareVehicle = sharedPreferences.getInt("shareVehicle", -1);
        if(dashboard == 1){
            drawer_names.add("Dashboard");
            drawer_images.add(R.drawable.drw_dashboard);
            drawer_ids.add(0);
        }

        if(groupID.equals("0")){
            drawer_names.add("Change\nPassword");
            drawer_images.add(R.drawable.drw_chn_pwd);
            drawer_ids.add(1);
        }

        if(distanceReport == 1){
            drawer_names.add("Distance\nReport");
            drawer_images.add(R.drawable.drw_report);
            drawer_ids.add(2);
        }

        if(settings == 1){
            drawer_names.add("Settings");
            drawer_images.add(R.drawable.drw_settings);
            drawer_ids.add(3);
        }

        if(createGroup == 1){
            drawer_names.add("Create\nGroup");
            drawer_images.add(R.drawable.drw_create_grp);
            drawer_ids.add(4);
        }
        drawer_names.add("Notifications");
        drawer_images.add(R.drawable.drw_noti);
        drawer_ids.add(5);

        if(shareVehicle==1){
            drawer_names.add("Share Vehicle");
            drawer_images.add(R.drawable.drw_share);
            drawer_ids.add(6);
        }

        drawer_names.add("Logout");
        drawer_images.add(R.drawable.drw_logout);
        drawer_ids.add(7);

        if (gps == 1) {
            names.add("GPS");
            images.add(R.drawable.n_gps);
            ids.add(1);
        }

        if (truck == 1) {
            names.add("Trucks");
            images.add(R.drawable.n_trucks);
            ids.add(2);
        }

        if (loads == 1) {
            names.add("Loads");
            images.add(R.drawable.n_loads);
            ids.add(3);
        }

       /* if (post == 1) {
            names.add("Post Load");
            images.add(R.drawable.n_postload);
            ids.add(4);
        }

        if (loadstatus == 1) {
            names.add("LoadStatus");
            images.add(R.drawable.n_loadstatus);
            ids.add(5);
        }

        if (avatruck == 1) {
            names.add("Avaliable Trucks");
            images.add(R.drawable.n_avaliabletruck);
            ids.add(6);
        }*/

        if (orders == 1) {
            names.add("Orders");
            images.add(R.drawable.n_orders);
            ids.add(7);
        }

        if (buyselltrucks == 1) {
            names.add("Buy/SellTruck");
            images.add(R.drawable.buysellicon);
            ids.add(8);
        }

        names.add("ERP");
        images.add(R.drawable.n_fms);
        ids.add(9);
        /*names.add("Fuel Card");
        images.add(R.drawable.fuel_card);
        ids.add(9);*/

        names.add("Insurance");
        images.add(R.drawable.n_insurance);
        ids.add(10);

        names.add("Toll/Fuel Cards ");
        images.add(R.drawable.n_tollcard);
        ids.add(11);

        /* names.add("E-Toll ");
        images.add(R.drawable.e_toll);
        ids.add(11);*/



        gridView.setAdapter(new GridAdapter(names, images,HomeScreenActivity.this));
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int id = ids.get(position);
                switch (id) {
                    case 1:
                        if(groupID.equals("0")) {
                            getListOfGroups();
                        }else {
                            Intent intent = new Intent(context, ListOfTrackingTrucks.class);
                            intent.putExtra("home","home");
                            intent.putExtra(Constants.GROUP_ID, groupID);
                            intent.putExtra(Constants.GROUP_NAME, groupName);
                            startActivity(intent);
                        }
                        break;
                    case 2:
                        startActivity(new Intent(context, TrucksActivity.class));
                        break;
                    case 3:
                        Intent next_in = new Intent(context, LoadActivity.class);//
                        next_in.putExtra("accountId",sharedPreferences.getString("accountID", ""));
                        startActivity(next_in);
                        break;
                    case 4:
                        startActivity(new Intent(context, PostLoadActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(context, LoadsStatus.class));
                        break;
                    case 6:
                        startActivity(new Intent(context,AvaliableTrucksActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(context, OrdersActivity.class));
                        break;

                    case 8:
                        startActivity(new Intent(context, BuyorSellTruckActivity.class));
                        //	startActivity(new Intent(context, SampleBuySellActivity.class));
                        break;
                    case 9:
                       /* if(!sharedPreferences.getString(FUEL_USERNAME_KEY,"").isEmpty() &&
                                !sharedPreferences.getString(FUEL_PASSWORD_KEY,"").isEmpty() &&
                                !sharedPreferences.getString(FUEL_CUSTOMERID_KEY,"").isEmpty()){
                            next_in(decrypt(sharedPreferences.getString(FUEL_USERNAME_KEY,"")),
                                    decrypt(sharedPreferences.getString(FUEL_PASSWORD_KEY,"")),
                                    decrypt(sharedPreferences.getString(FUEL_CUSTOMERID_KEY,"")),"Fuel Card");
                        }else{
                            showSettingsDialog("Fuel Card");
                        }*/
                        startActivity(new Intent(context, ERP_Activitys.class));
                        break;
                    case 10:
                        startActivity(new Intent(context, InsuranceActivity.class));
                        break;
                    case 11:
                        Toast.makeText(context, "Comming Soon", Toast.LENGTH_SHORT).show();
                      /*  if(!sharedPreferences.getString(TOLL_USERNAME_KEY,"").isEmpty() &&
                                !sharedPreferences.getString(TOLL_PASSWORD_KEY,"").isEmpty() &&
                                !sharedPreferences.getString(TOLL_CUSTOMERID_KEY,"").isEmpty()){
                            next_in(decrypt(sharedPreferences.getString(TOLL_USERNAME_KEY,"")),
                                    decrypt(sharedPreferences.getString(TOLL_PASSWORD_KEY,"")),
                                    decrypt(sharedPreferences.getString(TOLL_CUSTOMERID_KEY,"")),"Toll Gate");
                        }else{
                            showSettingsDialog("Toll Gate");
                        }*/
                        break;
//				case 9:
//					startActivity(new Intent(context, SellTruckActivity.class));
//
//					break;
                    default:
                        Toast.makeText(context, "Selected invalid option",Toast.LENGTH_LONG).show();
                        break;
                }

            }
        });

        mTitle = mDrawerTitle = getTitle();

        if (gps == 0) {
            drawer_names.clear();
            drawer_images.clear();
            drawer_ids.clear();
            drawer_names.add("Logout");
            drawer_images.add(R.drawable.drw_logout);
            drawer_ids.add(5);
            //menutitles = getResources().getStringArray(R.array.titles);
            //menuIcons  = getResources().obtainTypedArray(R.array.icons);
        } else {
            // menutitles = getResources().getStringArray(R.array.titles_gps);
            //menuIcons  = getResources().obtainTypedArray(R.array.icons_gps);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.slider_list);

        rowItems = new ArrayList<RowItem>();

        for (int i = 0; i < drawer_names.size(); i++) {
            RowItem items = new RowItem(drawer_names.get(i), drawer_images.get(i));
            rowItems.add(items);
        }

        //menuIcons.recycle();

        adapter = new CustomAdapter(getApplicationContext(), rowItems);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new SlideitemListener());

        // enabling action bar app icon and behaving it as toggle button
        // TODO: Remove the redundant calls to getSupportActionBar()
        // and use variable actionBar instead
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, // nav menu toggle icon
                R.string.app_name
                // accessibility
        ) {
            @Override
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void intializeAd() {
        try{
            adView = (AdView)findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void next_in(String username, String password, String customerID, String s) {
        Intent nextIntent = null;
        if(s.equalsIgnoreCase("Fuel Card")){
            nextIntent = new Intent(context, FuelCardActivity.class);
        }else{
            nextIntent = new Intent(context, TollActivity.class);
        }
        nextIntent.putExtra(USERNAME,username);
        nextIntent.putExtra(PASSWORD,password);
        nextIntent.putExtra(CUSTOMERID,customerID);
        startActivity(nextIntent);
    }

    @Override
    public void callStarted() {
        pDialog.setMessage("Saving the details...");
        pDialog.show();
    }

    @Override
    public void saveSettingsCompleted(JSONObject jsonObject, String cardType,String card_username,String card_password,String customerID) {
        TruckApp.checkPDialog(pDialog);
        try {
            if (jsonObject != null) {
                if (jsonObject.has("status") && jsonObject.getInt("status") == 1) {
                    if(cardType.equalsIgnoreCase("Fuel Card")){
                        editor.putString(FUEL_USERNAME_KEY,encrypt(card_username));
                        editor.putString(FUEL_PASSWORD_KEY,encrypt(card_password));
                        editor.putString(FUEL_CUSTOMERID_KEY,encrypt(customerID));
                    }else {
                        editor.putString(TOLL_USERNAME_KEY,encrypt(card_username));
                        editor.putString(TOLL_PASSWORD_KEY,encrypt(card_password));
                        editor.putString(TOLL_CUSTOMERID_KEY,encrypt(customerID));
                    }
                    editor.commit();
                    TruckApp.checkDialog(generalSettingsDialog);
                    next_in(card_username,card_password,customerID,cardType);
                }else if (jsonObject.has("status") && jsonObject.getInt("status") == 2) {
                    SharedPreferences sharedPreferences = getSharedPreferences(
                            getResources().getString(R.string.app_name),MODE_PRIVATE);
                    SharedPreferences.Editor
                            editor = sharedPreferences.edit();
                    editor.putInt("login", 0).commit();
                    editor.putString("accountID", "").commit();
                    editor.putString(FUEL_USERNAME_KEY,"").commit();
                    editor.putString(FUEL_PASSWORD_KEY,"").commit();
                    editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                    editor.putString(TOLL_USERNAME_KEY,"").commit();
                    editor.putString(TOLL_PASSWORD_KEY,"").commit();
                    editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                    startActivity(new Intent(context,
                            LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(context, "Failed to save the settings", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(context, "Failed to save the settings", Toast.LENGTH_SHORT).show();

            }
        }catch (Exception exe) {
            Toast.makeText(context, "Failed to save the settings", Toast.LENGTH_SHORT).show();
        }
    }



    private void showSettingsDialog(final String title) {
        TextView titleTv = (TextView)generalSettingsDialog.findViewById(R.id.dialogtitle_tv);
        titleTv.setText(getString(R.string.settings)+"("+title+")");
        final ImageView settingsClose = (ImageView)generalSettingsDialog
                .findViewById(R.id.close_iv);
        final EditText  usernameEt = (EditText) generalSettingsDialog
                .findViewById(R.id.uname_et);
        final EditText  passwordEt = (EditText) generalSettingsDialog
                .findViewById(R.id.pwd_et);
        final EditText  customerIDEt = (EditText) generalSettingsDialog
                .findViewById(R.id.customerid_et);
        Button saveBtn = (Button)generalSettingsDialog.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSettings(usernameEt,passwordEt,customerIDEt,title);
            }
        });
        settingsClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                generalSettingsDialog.dismiss();
            }
        });
        usernameEt.setText("");
        passwordEt.setText("");
        customerIDEt.setText("");
        generalSettingsDialog.show();

    }

    private void saveSettings(EditText usernameEt, EditText passwordEt, EditText customerIDEt,String title) {
        if(detectCnnection.isConnectingToInternet()) {
            String username = usernameEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();
            String customerID = customerIDEt.getText().toString().trim();
            if (!username.isEmpty() && !password.isEmpty() && !customerID.isEmpty()) {
                new SaveCardSettings(this,context,title, username, password, customerID).execute();
            } else {
                TruckApp.editTextValidation(usernameEt, username, getString(R.string.username_error));
                TruckApp.editTextValidation(passwordEt, password, getString(R.string.password_err));
                TruckApp.editTextValidation(customerIDEt, customerID, getString(R.string.customerid_error));
            }
        }else{
            Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
        }
    }

    private void intializieProfileDialog() {
        profileDialog = new Dialog(HomeScreenActivity.this,
                android.R.style.Theme_Dialog);
        profileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        profileDialog.setContentView(R.layout.profiledialog);

        closeDialog_iv = (ImageView) profileDialog.findViewById(R.id.close_iv);
        updateProfile = (Button) profileDialog.findViewById(R.id.update_btn);
        contactName_et = (EditText) profileDialog
                .findViewById(R.id.contactName_et);
        contactMobile_et = (EditText) profileDialog
                .findViewById(R.id.contactPhone_et);
        contactEmail_et = (EditText) profileDialog
                .findViewById(R.id.contactEmail_et);
        contactDescription = (EditText) profileDialog
                .findViewById(R.id.description_et);
    }


    private void intializieSettingsDialog() {
        generalSettingsDialog = new Dialog(HomeScreenActivity.this,android.R.style.Theme_Dialog);
        generalSettingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        generalSettingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        generalSettingsDialog.setContentView(R.layout.general_settings);
    }

    private void getListOfGroups() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getString(R.string.fetching_groups));
        if (detectCnnection.isConnectingToInternet()) {
            isProgressing = true;
            showProgressDialogNoTitle(context, progressDialog, true);
            String accountId = sharedPreferences.getString("accountID", "");
            GroupItemsSpiceRequest groupItemsSpiceRequest = new GroupItemsSpiceRequest(accountId);
            spiceManager.execute(groupItemsSpiceRequest, "GroupItems" + accountId, DurationInMillis.ONE_MINUTE * 2, new ListOfGroupsListener());
        } else {
            Toast.makeText(context, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
        }
    }

    class SlideitemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            /*if (sharedPreferences.getInt("gps", -1) == 1) {*/
            updateDisplay(drawer_ids.get(position));
            /*} else if (sharedPreferences.getInt("gps", -1) == 0) {
                logoutAction();
            }*/
        }
    }

    public static void showProgressDialogNoTitle(Context context, ProgressDialog progressDialog, boolean showProgress) {
        if (progressDialog != null) {
            progressDialog.setCanceledOnTouchOutside(false);
            if (showProgress) {
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            } else {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    }

    private void updateDisplay(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(context,DashboardActivity.class));
                break;
            case 1:
                changePwdDialog(sharedPreferences.getString("accountID", ""));
                break;
            case 2:
                startActivity(new Intent(context, DistanceReport.class));
                break;
            case 3:
                startActivity(new Intent(context, SettingsActivity.class));
                break;
            case 4:
                startActivity(new Intent(context, CreateGroupActivity.class));
                break;
            /*case 4:
                startActivity(new Intent(context, LoyaltyPointsActivity.class));
                ;
                break;*/
            case 5:
                startActivity(new Intent(context, NotificationsActivity.class));
                break;
            case 6:
                startActivity(new Intent(context, ShareActivity.class));
                break;
            case 7:
                logoutAction();
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_profile).setVisible(false);
        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_chgpassword).setVisible(false);
        return super.onPrepareOptionsMenu(menu);

    }

    ;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            if (detectCnnection.isConnectingToInternet()) {
                try {
                    System.out.println("AccId:"
                            + sharedPreferences.getString("accountID",
                            "no accountid "));
                    new GetProfile(
                            sharedPreferences.getString("accountID", ""),
                            "Getting the profile").execute();
                } catch (Exception e) {
                    System.out.println("Ex inn on resume:" + e.toString());
                }
            } else {
                Toast.makeText(context,
                        getResources().getString(R.string.internet_str),
                        Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (id == R.id.action_logout) {
            logoutAction();
            return true;
        } else if (id == R.id.action_chgpassword) {
            changePwdDialog(sharedPreferences.getString("accountID", ""));
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void logoutAction() {
        if (detectCnnection.isConnectingToInternet()) {
            try {
                new Logout(sharedPreferences.getString("uid", "NAN")).execute();
            } catch (Exception e) {
                System.out.println("Ex inn on resume:" + e.toString());
            }
        } else {
            Toast.makeText(context,
                    getResources().getString(R.string.internet_str),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void changePwdDialog(String string) {
        changePassword = null;
        chnPasswordDialog = new Dialog(HomeScreenActivity.this,
                android.R.style.Theme_Dialog);
        chnPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        chnPasswordDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(0));
        chnPasswordDialog.setContentView(R.layout.changepwddialog);
        chnclsDialog_iv = (ImageView) chnPasswordDialog
                .findViewById(R.id.close_iv);
        changePassword = (Button) chnPasswordDialog
                .findViewById(R.id.chnpwd_btn);
        new_pwd_et = (EditText) chnPasswordDialog.findViewById(R.id.new_pwd_et);
        repwd_et = (EditText) chnPasswordDialog
                .findViewById(R.id.reenternew_pwd_et);
        /*old_pwd_et = (EditText) chnPasswordDialog.findViewById(old_pwd_et);
        old_pwd_et.setText("");*/
        new_pwd_et.setText("");
        repwd_et.setText("");
        chnPasswordDialog.show();
        chnclsDialog_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //old_pwd_et.setText("");
                new_pwd_et.setText("");
                repwd_et.setText("");
                chnPasswordDialog.dismiss();
                changePassword = null;
            }
        });

        changePassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //String oldPwd = old_pwd_et.getText().toString().trim();
                String newPwd = new_pwd_et.getText().toString().trim();
                String rePwd = repwd_et.getText().toString().trim();
                if (newPwd.length() != 0 && newPwd.length() >= 6 && newPwd.equals(rePwd)) {
                    new ChangePassword(sharedPreferences.getString("accountID", ""), sharedPreferences.getString("uid", ""), newPwd, "Changing password").execute();
                } else {
                    TruckApp.editTextValidation(new_pwd_et, newPwd, "Enter new password");
                    if (newPwd.length() > 0 && newPwd.length() < 6) {
                        new_pwd_et.setError(Html
                                .fromHtml("<font color='red'>New password must be atleast 6 characters</font>"));
                    }
                    if (!newPwd.equals(rePwd)) {
                        repwd_et.setError(Html
                                .fromHtml("<font color='red'>New password and re enter password must be same</font>"));
                    }
                }
            }
        });
    }

    private class GetProfile extends AsyncTask<String, String, JSONObject> {

        String id, message;

        public GetProfile(String id, String message) {
            this.id = id;
            this.message = message;

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage(message + " data...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject json = null;
            try {
                String res = parser
                        .executeGet(TruckApp.PROFILE_URL
                                + id);
                System.out.println(res + "DRIVERS o/p" + res);
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
            pDialog.dismiss();
            if (result != null) {
                try {
                    if (0 == result.getInt("status")) {
                        Toast.makeText(
                                context,
                                getResources()
                                        .getString(R.string.failedProfile),
                                Toast.LENGTH_LONG).show();
                    } else if (1 == result.getInt("status")) {
                        final JSONObject profileObj = result
                                .getJSONObject("success");
                        setProfileDialog(profileObj);
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

    public void setProfileDialog(final JSONObject profileObj) {
        try {
            contactName_et.setText(profileObj.getString("contactName"));
            contactMobile_et.setText(profileObj.getString("contactPhone"));
            contactEmail_et.setText(profileObj.getString("contactEmail"));
            contactDescription.setText(profileObj.getString("description"));
            profileDialog.show();
            closeDialog_iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    profileDialog.dismiss();
                }
            });
            updateProfile.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String name = contactName_et.getText().toString();
                    String mob = contactMobile_et.getText().toString();
                    String email = contactEmail_et.getText().toString();
                    String des = contactDescription.getText().toString();
                    if (name.length() != 0 && mob.length() != 0
                            && email.length() != 0 && mob.length() == 10
                            && TruckApp.emailValidator(email)) {
                        try {
                            new SetProfile(profileObj.getString("accountID"),
                                    name, mob, email, des, "Updating profile")
                                    .execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        TruckApp.editTextValidation(
                                contactName_et,
                                name,
                                getResources().getString(
                                        R.string.username_error));
                        TruckApp.editTextValidation(contactMobile_et, mob,
                                getResources().getString(R.string.mobile_error));
                        TruckApp.editTextValidation(contactEmail_et, email,
                                getResources().getString(R.string.email_error));
                        if (mob.length() != 0 && mob.length() < 10) {
                            contactMobile_et.setError(Html
                                    .fromHtml("<font color='red'>"
                                            + getResources()
                                            .getString(
                                                    R.string.mobilelength_error)
                                            + "</font>"));
                        }
                        if (email.length() != 0
                                && TruckApp.emailValidator(email)) {
                            contactEmail_et.setError(Html
                                    .fromHtml("<font color='red'>"
                                            + getResources().getString(
                                            R.string.emailvalid_error)
                                            + "</font>"));
                        }
                    }
                }
            });
        } catch (JSONException E) {
            System.out.println("Ex" + E.toString());
        }
    }

    private class SetProfile extends AsyncTask<String, String, JSONObject> {

        String id, message, name, mob, email, des;

        public SetProfile(String id, String name, String pho, String email,
                          String des, String message) {
            this.id = id;
            this.name = name;
            this.mob = pho;
            this.email = email;
            this.des = des;
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage(message + " data...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject json = null;
            try {
                String urlParameters = "contactName="
                        + URLEncoder.encode(name, "UTF-8") + "&contactPhone="
                        + URLEncoder.encode(mob, "UTF-8") + "&contactEmail="
                        + URLEncoder.encode(email, "UTF-8") + "&description="
                        + URLEncoder.encode(des, "UTF-8");
                String res = parser.excutePut(
                        TruckApp.PROFILE_URL
                                + id, urlParameters);
                System.out.println(res + "DRIVERS o/p" + res);
                json = new JSONObject(res);
            } catch (Exception e) {
                Log.e("Login DoIN EX", e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            if (result != null) {
                try {
                    if (0 == result.getInt("status")) {
                        Toast.makeText(
                                context,
                                getResources()
                                        .getString(R.string.failedProfile),
                                Toast.LENGTH_LONG).show();
                    } else if (1 == result.getInt("status")) {
                        Toast.makeText(context, "Successfully updated profile",
                                Toast.LENGTH_LONG).show();
                        if (profileDialog != null && profileDialog.isShowing()) {
                            profileDialog.dismiss();
                        }
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

    @Override
    protected void onPause() {
        TruckApp.checkDialog(profileDialog);
        TruckApp.checkDialog(chnPasswordDialog);
       /* if (adView != null) {
            adView.pause();
        }*/
        super.onPause();
    }

    public class GridAdapter extends BaseAdapter {

        ArrayList<String> names;
        ArrayList<Integer> images;
        Activity activity;
        LayoutInflater inflater;

        public GridAdapter(ArrayList<String> names, ArrayList<Integer> images,
                           Activity activity) {
            super();
            this.names = names;
            this.images = images;
            this.activity = activity;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return names.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return names.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        class ViewHolder {
            public ImageView icon_iv;
            public TextView name_tv;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater
                        .inflate(R.layout.griditem, parent, false);
                holder.icon_iv = (ImageView) convertView
                        .findViewById(R.id.grid_image);
                holder.name_tv = (TextView) convertView
                        .findViewById(R.id.grid_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            setData(position, holder);
            return convertView;
        }

        private void setData(int position, ViewHolder holder) {
            holder.icon_iv.setImageResource(images.get(position));
            holder.name_tv.setText(names.get(position));
        }

    }

    private class Logout extends AsyncTask<Void, Void, JSONObject> {

        String uid;

        public Logout(String uid) {
            super();
            this.uid = uid;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage("Logging out...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            JSONObject json = null;
            try {
                String urlParameters = "uid=" + URLEncoder.encode(uid, "UTF-8")
                        +"&access_token="+URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8");
                String res = parser.excutePost(TruckApp.logoutURL,
                        urlParameters);
                // System.out.println("URL:"+TruckApp.logoutURL+
                // urlParameters+"logout o/p"+res);
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
            pDialog.dismiss();
            if (result != null) {
                try {
                    if (0 == result.getInt("status")) {
                        Toast.makeText(context,
                                "Failed to logout .Please try again",
                                Toast.LENGTH_LONG).show();
                    }else if (2 == result.getInt("status")) {
                        //TruckApp.logoutAction(HomeScreenActivity.this);
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name), MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(FUEL_USERNAME_KEY,"").commit();
                        editor.putString(FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(TOLL_USERNAME_KEY,"").commit();
                        editor.putString(TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(context,
                                LoginActivity.class));
                        finish();
                    } else if (1 == result.getInt("status")) {
                        System.out.println("add");
                        editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(FUEL_USERNAME_KEY,"").commit();
                        editor.putString(FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(TOLL_USERNAME_KEY,"").commit();
                        editor.putString(TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(getApplicationContext(),
                                LoginActivity.class));
                        finish();
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

    public class ChangePassword extends AsyncTask<String, String, JSONObject> {

        String accountId, message, newpwd, uid;

        public ChangePassword(String accountId, String uid, String new_pwd,
                              String message) {
            this.accountId = accountId;
            this.message = message;
            this.uid = uid;
            this.newpwd = new_pwd;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pDialog.setMessage(message + " data...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject json = null;
            try {
               /* String urlParameters = "new_pass="
                        + URLEncoder.encode(newpwd, "UTF-8") + "&re_pass="
                        + URLEncoder.encode(newpwd, "UTF-8") + "&type="
                        + URLEncoder.encode("truck", "UTF-8");
                String res = parser.excutePost(
                        "http://easygaadi.cloudapp.net/api/gps/mobile_gps/changepassword/"
                                + id, urlParameters);
                System.out.println(res + "changepassword o/p" + res);*/
                StringBuilder builder = new StringBuilder();
                builder.append("accountid=").append(URLEncoder.encode(accountId, "UTF-8"));
                builder.append("&password=").append(URLEncoder.encode(newpwd, "UTF-8"));
                builder.append("&uid=").append(URLEncoder.encode(uid, "UTF-8"));
                builder.append("&access_token=").append(sharedPreferences.getString("access_token",""));
                String res = parser.excutePost(TruckApp.CHANGE_PASSWORD_URL, builder.toString());
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
            pDialog.dismiss();
            if (result != null) {
                try {
                    if (0 == result.getInt("status")) {
                        final JSONArray errorObj = result.getJSONArray("error");
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < errorObj.length(); i++) {
                            builder.append(
                                    errorObj.getJSONObject(i).getString("msg"))
                                    .append("\n");
                        }
                        Toast.makeText(context, builder.toString(), Toast.LENGTH_LONG).show();
                    }else if(2== result.getInt("status")){
                        //TruckApp.logoutAction(HomeScreenActivity.this);
                        SharedPreferences sharedPreferences = getSharedPreferences(
                                getResources().getString(R.string.app_name), MODE_PRIVATE);
                        SharedPreferences.Editor
                                editor = sharedPreferences.edit();
                        editor.putInt("login", 0).commit();
                        editor.putString("accountID", "").commit();
                        editor.putString(FUEL_USERNAME_KEY,"").commit();
                        editor.putString(FUEL_PASSWORD_KEY,"").commit();
                        editor.putString(FUEL_CUSTOMERID_KEY,"").commit();
                        editor.putString(TOLL_USERNAME_KEY,"").commit();
                        editor.putString(TOLL_PASSWORD_KEY,"").commit();
                        editor.putString(TOLL_CUSTOMERID_KEY,"").commit();
                        startActivity(new Intent(context,
                                LoginActivity.class));
                        finish();
                    } else if (1 == result.getInt("status")) {
                        Toast.makeText(context, context.getString(R.string.password_changed_successfully), Toast.LENGTH_LONG).show();
                        if (chnPasswordDialog != null
                                && chnPasswordDialog.isShowing()) {
                            chnPasswordDialog.dismiss();
                        }
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

    @Override
    protected void onDestroy() {
        /*if (adView != null)
        { adView.destroy(); }*/
        editor = null;
        detectCnnection = null;
        parser = null;
        pDialog = null;
        sharedPreferences = null;
        profileDialog = null;
        chnPasswordDialog = null;
        drawer_images = null;
        drawer_ids = null;
        drawer_ids = null;
        // nav drawer title
        mDrawerTitle = null;
        mTitle = null;
        mDrawerLayout = null;
        mDrawerList = null;
        mDrawerToggle = null;
        rowItems = null;
        adapter = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        TruckApp.getInstance().trackScreenView("Home Screen");
        /*if (adView != null) {
            adView.resume();
        }*/
    }



    private class ListOfGroupsListener implements PendingRequestListener<GroupItemResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            isProgressing = false;
            showProgressDialogNoTitle(context, progressDialog, false);
            Toast.makeText(context, "No groups exits, contact support!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(GroupItemResponse groupItemResponse) {
            isProgressing = false;
            showProgressDialogNoTitle(context, progressDialog, false);
            if (groupItemResponse.status == 1) {
                if (groupItemResponse.redirect == 1) {
                    Intent intent = new Intent(context, ListOfTrackingTrucks.class);
                    intent.putExtra("home","home");
                    intent.putExtra(Constants.GROUP_ID, groupItemResponse.data[0].id);
                    intent.putExtra(Constants.GROUP_NAME, groupItemResponse.data[0].groupname);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, GroupsActivity.class);
                    intent.putExtra("home","home");
                    intent.putExtra(Constants.GROUP_RESPONSE, groupItemResponse);
                    startActivity(intent);
                }
            }else if(groupItemResponse.status == 2){
                //TruckApp.logoutAction(HomeScreenActivity.this);
                SharedPreferences sharedPreferences = getSharedPreferences(
                        getResources().getString(R.string.app_name), MODE_PRIVATE);
                SharedPreferences.Editor
                        editor = sharedPreferences.edit();
                editor.putInt("login", 0).commit();
                editor.putString("accountID", "").commit();
                startActivity(new Intent(context,
                        LoginActivity.class));
                finish();
            } else {
                Toast.makeText(context, "No groups exits, contact support!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onRequestNotFound() {
            getListOfGroups();
        }
    }
}
