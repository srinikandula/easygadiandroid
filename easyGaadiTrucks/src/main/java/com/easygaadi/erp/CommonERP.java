package com.easygaadi.erp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.WindowManager;

import com.easygaadi.trucksmobileapp.R;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by ssv i3-210 on 11/6/2017.
 */

public class CommonERP {


    public static Drawable getDays(Context context,String fdate){

        Date date;
        long diff = 0;
        Log.i("start date",fdate);
        DateFormat dateFormat,formatter;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String newDates = null;
        try {
            date = dateFormat.parse(fdate);
            formatter = new SimpleDateFormat("yyyy-MM-dd"); //If you need time just put specific format for time like 'HH:mm:ss'
            newDates = formatter.format(date);
            Log.i("custom date",newDates);
            Date today = new Date();
            diff =  date.getTime()-today.getTime()  ;
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("err--"+e.getMessage());
        }

        //int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        long numOfDays = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
        Log.i("numOfDays-->",""+numOfDays);
        System.out.println("numOfDays-->"+numOfDays);
        if(numOfDays >  30)
        {
            Drawable img = context.getResources().getDrawable( R.drawable.orange );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }else if(numOfDays < 30)
        {
            Drawable img = context.getResources().getDrawable( R.drawable.orange );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }else
        {
            Drawable img = context.getResources().getDrawable( R.drawable.red );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }
    }

    public static String getDate(String fdate)
    {
        Date date;
        String diff = "";
        System.out.println("getDate--"+"getDate"+fdate);
        DateFormat dateFormat,formatter;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
            date = dateFormat.parse(fdate);
            formatter = new SimpleDateFormat("yyyy-MM-dd"); //If you need time just put specific format for time like 'HH:mm:ss'
            diff = formatter.format(date);


        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("err--"+e.getMessage());
        }
        return diff;
    }

    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.erp_progressdialog);
        // dialog.setMessage(Message);
        return dialog;
    }

    protected int getIntFromJSON(JSONObject jsonObject, String key) {

        int defaultValue = 0;
        try {
            defaultValue = jsonObject.getInt(key);
        } catch (Exception e) {
            //Logger.print("While JSON Parsing, key named \"" + key + "\"  not found");
        }
        return defaultValue;
    }



    public static Drawable getDays(String fdate,Context context){

        Date date;
        long diff = 0;
        //Log.i("start date",fdate);
        DateFormat dateFormat,formatter;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String newDates = null;
        try {
            date = dateFormat.parse(fdate);
            formatter = new SimpleDateFormat("yyyy-MM-dd"); //If you need time just put specific format for time like 'HH:mm:ss'
            newDates = formatter.format(date);
            //Log.i("custom date",newDates);
            Date today = new Date();
            diff =  date.getTime()-today.getTime()  ;
        } catch (ParseException e) {
            e.printStackTrace();

        }

        //int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        long numOfDays = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);
        //Log.i("numOfDays-->",""+numOfDays);
        //System.out.println("numOfDays-->"+numOfDays);
        if(numOfDays >  15)
        {
            System.out.println("green--"+""+numOfDays+"--"+newDates);
            Drawable img = context.getResources().getDrawable( R.drawable.green );
            img.setBounds( 0, 0, 60, 60 );
            return img;

        }else if(numOfDays > 2 && numOfDays <= 15   )
        {
            System.out.println("orange--"+""+numOfDays+"--"+newDates);
            Drawable img = context.getResources().getDrawable( R.drawable.orange );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }else
        {
            System.out.println("red--"+""+numOfDays +"--"+newDates);
            Drawable img = context.getResources().getDrawable( R.drawable.red );
            img.setBounds( 0, 0, 60, 60 );
            return img;
        }
    }


}
