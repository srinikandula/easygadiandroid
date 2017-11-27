package com.easygaadi.trucksmobileapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.acra.annotation.ReportsCrashes;
import org.apache.commons.io.IOUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore.MediaColumns;
import android.support.multidex.MultiDexApplication;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

//@ReportsCrashes(formKey = "",mailTo = "riyaz.m@mtwlabs.com")
public class TruckApp extends MultiDexApplication {



    public static final String TAG = TruckApp.class.getSimpleName();
    public static final String CHANGE_PASSWORD_V2_URL = "http://egcrm.cloudapp.net/operations/index.php/GPSapiV2/SetPassword";
    private static TruckApp mInstance;
    private RequestQueue mRequestQueue;
    // aded by Veda
    ///added by ajay ///

    public static String BASE_URL = "http://egcrm.cloudapp.net/operations/index.php/GPSapiV3/";
    //public static String BASE_URL = "http://192.168.1.39:3000/crm/operations/index.php/GPSapiV3/";


    public static String CHANGE_PASSWORD_URL = BASE_URL + "SetPassword";

    public static String tollCardSettingsURL = BASE_URL + "tollCardSettings";
    public static String tollCardCUGTransferURL = BASE_URL + "tollCardCUGToCardTransfer";
    public static String tollCard2CardTransferURL = BASE_URL + "tollCardToCardTransfer";
    public static String tollCardUsageReportURL = BASE_URL + "tollCardUsageReport";
    public static String tollCardgetCardsURL = BASE_URL + "tollCardgetCards";

    public static String dashboardURL = BASE_URL + "getDashboard";
    public static String dcardSettingsURL = BASE_URL + "dCardSettings";
    public static String dCardgetCardsURL = BASE_URL + "dCardgetCards";
    public static String dCardCCMSToCardTransferURL = BASE_URL + "dCardCCMSToCardTransfer";
    public static String dCardToCardTransferURL = BASE_URL + "dCardToCardTransfer";
    public static String dCardUsageReportURL = BASE_URL + "dCardUsageReport";
    public static String dCardSetCardLimit = BASE_URL + "dCardSetCardLimit";

    public static String INSURANCE_QUOTE_URL = BASE_URL + "InsuranceQuote";
    public static String SHARE_URL = BASE_URL + "ShareYourVehicle";
    public static String NOTIFICATIONS_URL = BASE_URL + "getNotifications";
    public static String PROFILE_URL = "http://easygaadi.cloudapp.net/api/gps/mobile_gps/profile/";
    public static String TRACK_ALL_VEHICLES_URL = BASE_URL + "trackallvehicles";
    public static String TRACK_TRUCK_URL = BASE_URL + "tracktruck";
    public static String loyaltypointsURL = BASE_URL + "getLoyalityPointsInfo";
    public static String sendGiftRequestURL = BASE_URL + "redeemPoints";
    public static String loginURL = BASE_URL + "Login";
    public static String logoutURL = BASE_URL + "logoutDevice";
    public static String frgotPasswordURL = BASE_URL + "forgotpassword";
    public static String getProfileURL = "http://easygaadi.cloudapp.net/api/gps/mobile_gps/profile";
    public static String setCustomerURL = BASE_URL + "SetCustomer";
    public static String setLoadURL = BASE_URL + "SetLoad";
    public static String getTruckTypesURL = "http://egcrm.cloudapp.net/operations/index.php/dcapi/GetTruckTypes";
    public static String getGoodsTypesURL = "http://egcrm.cloudapp.net/operations/index.php/GPSapi/GetGoodsType";
    public static String setTruckURL = BASE_URL + "SetTruck";
    public static String checkActiveStatusURL = BASE_URL + "isUserActive";
    public static String postLoadURL = BASE_URL + "SetPostLoad";
    public static String getPostedLoadsURL = BASE_URL + "GetPostLoads";
    public static String getQuotesURL = BASE_URL + "GetQuotes";
    public static String bookQuoteURL = BASE_URL + "selectLoadQuote";
    public static String getOrdersURL = BASE_URL + "GetOrders";
    public static String setTruckInfoURL = BASE_URL + "SetTruckInfo";
    public static String getTrucksURL = BASE_URL + "GetTrucks";
    public static String getLoadsURL = BASE_URL + "GetAlerts";
    public static String getAvaliableTrucksURL = BASE_URL + "getTrucksAvailable";
    public static String bookAvaliableTruck = BASE_URL + "selectTrucksAvailable";
    public static String bookTruckURL = BASE_URL + "BookTrucksAvailable";
    public static String cancelLoadURL = BASE_URL + "CancelPostedLoad";
    public static String deleteTruckURL = BASE_URL + "DeleteTruck";
    public static String getLoadInfo = BASE_URL + "getPostLoadDetails";
    public static String trackOrderURL = BASE_URL + "TrackOrderedTruck";
    public static String lookingforloadURL = BASE_URL + "setDeviceLoad";
    public static String updateDeviceInfoURL = BASE_URL + "setDeviceDetails";
    public static String BuyTruckListURL = BASE_URL + "GetSellTrucks";
    public static String SellTruckListURL = BASE_URL + "addSellTruck";
    public static String ExpectedPriceURL = BASE_URL + "ApplySellTruck";
    public static String FILE_UPLOAD_URL = BASE_URL + "UploadSellTruck?";
    public static String Truck_IMS = BASE_URL + "GetSellTruckPics?";
    public static String createTripURL = BASE_URL + "createTrip";
    public static String getTripsURL = BASE_URL + "getTripList";
    public static String deleteTripURL = BASE_URL + "deleteTrip";
    public static String stopTripURL = BASE_URL + "stopTrip";
    public static String getGroupsURL = "/operations/index.php/GPSapiV3/getGroupList";
    public static String getTripSummaryURL = BASE_URL + "getTripSummary";
    public static String getDevicesURL = BASE_URL + "getDevices";
    public static String setGroup = BASE_URL + "CreateGroup";
    public static String settingsURL = BASE_URL + "Settings";
    public static String assignDevicesURL = BASE_URL + "assignDevices";
    public static String distanceReportURL = BASE_URL + "getDistanceReport";
    public static String deviceDetailsURL = BASE_URL + "DeviceDetails";




    public static final String IMAGE_DIRECTORY_NAME = "Truck Pics Upload";


    //erp url

    //public static String ERP_URL= "http://35.154.47.181:3000/v1/";
    //development
    //public static String ERP_URL= "http://demo.easygaadi.com/v1/";
    //production
    public static String ERP_URL= "http://erp.easygaadi.com/v1/";

    //public static String ERP_URL= "http://192.168.1.39:3000/v1/";
    public static String userLoginURL =  ERP_URL+"group/login";
    public static String paryListURL =  ERP_URL+"party/get/accountParties";
    public static String addPayURL =  ERP_URL+"party";
    public static String driverListURL =  ERP_URL+"drivers/";
    public static String driverFreshURL =  ERP_URL+"drivers/get/";
    public static String truckListURL =  ERP_URL+"trucks";
    public static String maintenanceListURL =  ERP_URL+"maintenance";
    public static String tripsListURL =  ERP_URL+"trips";
    public static String PaymentURL =  ERP_URL+"payments";
    public static String ExpenseURL =  ERP_URL+"expenseMaster";
    public static String ExpensesURL =  ERP_URL+"expense";



    public static String ERP_DASHURL =  ERP_URL+"trips/find";




    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yy HH:mm:ss");

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0)
            return 1;
        else
            return k;
    }

    public static boolean copyFile(String srFile, String dtFile) {
        File f1 = new File(srFile);
        File f2 = new File(dtFile);

        try {
            InputStream in = new FileInputStream(f1);
            OutputStream out = new FileOutputStream(f2, true); // appending output stream

            try {
                try {
                    IOUtils.copy(in, out);
                    return true;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
                f1 = f2 = null;
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    public static boolean checkTime(String dateStr) {
        long convertedTime = TruckApp.convert2seconds(dateStr);
        long currentTime = System.currentTimeMillis();
        if (currentTime > convertedTime || convertedTime < (currentTime + (7 * 24 * 60 * 60 * 1000))) {
            return false;
        } else {
            return true;
        }

    }

    @SuppressLint("SimpleDateFormat")
    private static long convert2seconds(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date date;
        try {
            date = sdf.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDateString(boolean currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        if (!currentDate) {
            cal.add(Calendar.HOUR_OF_DAY, -12);
        }
        return dateFormat.format(cal.getTime());
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static String getCompleteAddressString(Context context,
                                                  double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE,
                    LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i)).append(
                            "\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Cur loc Add",
                        "" + strReturnedAddress.toString());
            } else {
                Log.w("My Cur loc Add", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Cur loc Add", "Canont get Address!");
        }
        return strAdd;
    }

    /**
     * Return date in specified format.
     *
     * @param milliSeconds Date in milliseconds
     * @param dateFormat   Date format
     * @return String representing date in specified format
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified
        // format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static Date convertStringToDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = formatter.parse(dateString);
            System.out.println(date);
            System.out.println(formatter.format(date));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mInstance = this;
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread()
                                            .getName(), e)).setFatal(false)
                    .build());
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category)
                .setAction(action).setLabel(label).build());
    }

    public static void hide_keyboard_from(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static String secToDate(int seconds, Context context) {
        long millis = seconds * 1000L;
        TimeZone tz = TimeZone.getTimeZone("IST");
        SimpleDateFormat df = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss",
                context.getResources().getConfiguration().locale);
        df.setTimeZone(tz);
        String time = df.format(new Date(millis));
        System.out.println(time);
        return time;
    }

    public static int getDifBetDates(String fromdate, String todate) {
        Date fromDate = null, toDate = null;
        try {
            fromDate = sdf.parse(fromdate);
            toDate = sdf.parse(todate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static String getCompleteAddressString(double LATITUDE,
                                                  double LONGITUDE, Context context) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE,
                    LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress
                            .append(returnedAddress.getAddressLine(i)).append(
                            "\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Cur loc Add", strReturnedAddress.toString());
            } else {
                Log.w("My Cur loc Add", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Cur loc Add", "Canont get Address!");
        }
        return strAdd;
    }

    @SuppressLint("SimpleDateFormat")
    public static boolean checkDate(String fromdate, String todate) {
        Date fromDate, toDate;
        try {
            fromDate = sdf.parse(fromdate);
            toDate = sdf.parse(todate);
            if (fromDate.getTime() <= toDate.getTime()) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String createDate(int year, int month, int day) {
        StringBuilder builder = new StringBuilder();
        builder.append(year).append("-").append(set2Digit(month)).append("-")
                .append(set2Digit(day));
        return builder.toString();
    }

    public static String set2Digit(int val) {
        String no = String.valueOf(val);
        if (1 == no.length()) {
            no = "0" + no;
        }
        return no;
    }

    public static synchronized TruckApp getInstance() {
        return mInstance;
    }

    public static void checkPDialog(ProgressDialog pDialog) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public static void checkDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void editTextValidation(EditText et, String value,
                                          String errorMessage) {
        if (value.length() == 0) {
            et.setError(Html.fromHtml("<font color='red'>" + errorMessage
                    + "</font>"));
        }
    }

    public static void autocompleteValidation(AutoCompleteTextView actv, boolean isSet, String errorMessage) {
        if (!isSet) actv.setError(Html.fromHtml("<font color='red'>" + errorMessage + "</font>"));
    }

    public static void setMandatory(TextView tv, String val) {
        String colored = "*";
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(val);
        int start = builder.length();
        builder.append(colored);
        int end = builder.length();

        builder.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(builder);
    }

    public static void spanTextview(TextView tv, String val1, String val2) {

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(val1);
        int start = builder.length();
        builder.append(val2);
        int end = builder.length();

        builder.setSpan(new ForegroundColorSpan(Color.DKGRAY), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv.setText(builder);
    }

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public static String getRealPathFromURI(Uri contentUri, Context context) {
        String res = null;
        String[] proj = {MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static Bitmap getThumbnail(Uri uri, Context context, int thumbNail) throws FileNotFoundException, IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > thumbNail) ? (originalSize / thumbNail) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }


    public static void showToast(String message,Activity activity){
        LayoutInflater mInflater=LayoutInflater.from(activity);
        View view=mInflater.inflate(R.layout.custom_toast,null);
        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(message);
        Toast toast=new Toast(activity);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
