package com.easygaadi.trucksmobileapp.gcm;

import java.util.Date;

import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.easygaadi.trucksmobileapp.AvaliableTrucksActivity;
import com.easygaadi.trucksmobileapp.DashboardActivity;
import com.easygaadi.trucksmobileapp.InsuranceActivity;
import com.easygaadi.trucksmobileapp.ListOfTrackingTrucks;
import com.easygaadi.trucksmobileapp.LoadActivity;
import com.easygaadi.trucksmobileapp.LoadsStatus;
import com.easygaadi.trucksmobileapp.NotificationsActivity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.SplashActivity;
import com.easygaadi.utils.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GCMNotificationIntentService() {
		super("GcmIntentService");
	}

	public static final String TAG = "GCMNotiIntService";

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				for (int i = 0; i < 3; i++) {
					Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}

				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				/*sendNotification("Message Received from Google GCM Server: "
						+ extras.get(Config.MESSAGE_KEY));*/
				if(extras.get(Config.MESSAGE_KEY)!=null){
					sendNotification(extras.get(Config.MESSAGE_KEY).toString());
				}else{
					sendNotification("");
				}
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		WakefulBroadcastReceiver.completeWakefulIntent(intent);
	}

	@SuppressWarnings("deprecation")
	private void sendNotification(String msgJson) {
		try{
			Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.large_icon);
			JSONObject json = new JSONObject(msgJson);
			String msg=json.getString("message");
			String type=json.getString("type");
			Log.d(TAG, "Preparing to send notification...: " + msg);
			mNotificationManager = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);

			SharedPreferences    preferences     = getApplicationContext().getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
			PendingIntent contentIntent=null;


			if(1 == preferences.getInt("login", 0)){
				if(type.equalsIgnoreCase("load")){
					contentIntent = PendingIntent.getActivity(this, 0,
							new Intent(this, LoadActivity.class), PendingIntent.FLAG_ONE_SHOT);
				}else if(type.equalsIgnoreCase("truckavailable")){
					contentIntent = PendingIntent.getActivity(this, 0,
							new Intent(this, AvaliableTrucksActivity.class), PendingIntent.FLAG_ONE_SHOT);
				}else if(type.equalsIgnoreCase("quote")){
					contentIntent = PendingIntent.getActivity(this, 0,
							new Intent(this, LoadsStatus.class), PendingIntent.FLAG_ONE_SHOT);
				}else if(type.equalsIgnoreCase("dashboard")){
					contentIntent = PendingIntent.getActivity(this, 0,
							new Intent(this, DashboardActivity.class), PendingIntent.FLAG_ONE_SHOT);
				}else if(type.equalsIgnoreCase("general")){
					contentIntent = PendingIntent.getActivity(this, 0,
							new Intent(this, NotificationsActivity.class), PendingIntent.FLAG_ONE_SHOT);
				}else if(type.equalsIgnoreCase("insurance")){
					Intent intent = new Intent(this,InsuranceActivity.class);
					intent.putExtra("noti","noti");
					contentIntent = PendingIntent.getActivity(this, 0,
							intent, PendingIntent.FLAG_ONE_SHOT);
				}else if(type.equalsIgnoreCase("route")){
					Intent intent = new Intent(this, ListOfTrackingTrucks.class);
					intent.putExtra(Constants.GROUP_ID, preferences.getString("groupID", "0"));
					intent.putExtra(Constants.GROUP_NAME, preferences.getString("groupName","Group"));
					contentIntent = PendingIntent.getActivity(this, 0,
							intent, PendingIntent.FLAG_ONE_SHOT);
				}
			}else{
				contentIntent = PendingIntent.getActivity(this, 0,
						new Intent(this, SplashActivity.class), PendingIntent.FLAG_ONE_SHOT);
			}

			NotificationCompat.Builder mBuilder = null;

			if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				mBuilder = new NotificationCompat.Builder(
						this).setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle("Easygaadi")
						.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
						.setLargeIcon(bitmap)
						.setContentText(msg).setAutoCancel(true).
								setDefaults(Notification.DEFAULT_SOUND |Notification.DEFAULT_LIGHTS);
			} else {
				int color = getResources().getColor(R.color.grey_clr);
				mBuilder = new NotificationCompat.Builder(
						this).setSmallIcon(R.drawable.ic_launcher)
						.setContentTitle("Easygaadi")
						.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
						.setLargeIcon(bitmap)
						.setContentText(msg).setAutoCancel(true)
						.setDefaults(Notification.DEFAULT_SOUND |Notification.DEFAULT_LIGHTS)
						.setColor(color);
			}

			String tmpStr      = String.valueOf(new Date().getTime());
			int notificationId = Integer.valueOf(tmpStr.substring(tmpStr.length() - 5));
			tmpStr=null;
			mBuilder.setContentIntent(contentIntent);
			mNotificationManager.notify(notificationId, mBuilder.build());
			bitmap=null;
			Log.d(TAG, "Notification sent successfully.");
		}catch(Exception e){
			Log.e("Error in notification", e.toString());
		}
	}


}
