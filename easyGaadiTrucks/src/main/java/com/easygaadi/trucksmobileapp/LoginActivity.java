package com.easygaadi.trucksmobileapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;

public class LoginActivity extends Activity {

	private EditText username_et, password_et,mobile_et_lgn;
	private CheckBox rememberme_cb;
	private TextView forgot_tv, signup_tv, note_tv;
	private Button login_btn, signup_btn, signupcnl_btn;
	private ConnectionDetector detectCnnection;
	Context context;
	Resources res;
	JSONParser parser;
	FrameLayout progressFrame;
	Dialog forgotdialog, signupdialog;
	EditText fullname_et, mobile_et, address_et;
	EditText forgot_et, phone_et;
	Button send_btn, cancel_btn;
	SharedPreferences preferences;
	Editor editor;
	List<String> customerTypes, customerValues;
	Spinner customerTypeSpn;
	protected String selectedCustomerType;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		username_et = (EditText) findViewById(R.id.et_Username);
		password_et = (EditText) findViewById(R.id.et_Password);
		mobile_et_lgn  = (EditText) findViewById(R.id.et_Mobile);
		rememberme_cb = (CheckBox) findViewById(R.id.cb_Rememberme);
		forgot_tv = (TextView) findViewById(R.id.tv_Forgotpwd);
		signup_tv = (TextView) findViewById(R.id.tv_signup);
		login_btn = (Button) findViewById(R.id.btn_login);
		progressFrame = (FrameLayout) findViewById(R.id.progressFrame);
		context = getApplicationContext();
		detectCnnection = new ConnectionDetector(context);
		res = getResources();
		parser = JSONParser.getInstance();
		preferences = getApplicationContext().getSharedPreferences(
				getResources().getString(R.string.app_name), MODE_PRIVATE);
		editor = preferences.edit();
		customerTypes = Arrays.asList(getResources().getStringArray(
				R.array.customertypes));
		customerValues = Arrays.asList(getResources().getStringArray(
				R.array.customerTypeValues));

		rememberme_cb.setChecked(true);

		username_et.setText(preferences.getString("username", ""));

		forgotdialog = new Dialog(LoginActivity.this,
				android.R.style.Theme_Dialog);
		forgotdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		forgotdialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		forgotdialog.setContentView(R.layout.forgetdialog);

		forgot_et = (EditText) forgotdialog.findViewById(R.id.forgotpwd_et);
		phone_et = (EditText) forgotdialog.findViewById(R.id.forgotmob_et);
		send_btn = (Button) forgotdialog.findViewById(R.id.send_btn);
		cancel_btn = (Button) forgotdialog.findViewById(R.id.cancel_btn);

		forgot_et.clearFocus();

		forgot_tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (detectCnnection.isConnectingToInternet()) {
					forgot_et.clearFocus();
					forgotpwd_act(v);
				} else {
					Toast.makeText(context,
							res.getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		login_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String username = username_et.getText().toString().trim();
				String password = password_et.getText().toString().trim();
				String mobile   = mobile_et_lgn.getText().toString().trim();

				if (detectCnnection.isConnectingToInternet() && username.length() > 0 && password.length() > 0
						&& mobile.length() >0)
				{
					new Login(username, password,mobile, preferences.getString(
							SplashActivity.REG_ID, "")).execute();
				} else {
					if (!detectCnnection.isConnectingToInternet()) {
						Toast.makeText(context,
								res.getString(R.string.internet_str),
								Toast.LENGTH_LONG).show();
					}
					TruckApp.editTextValidation(username_et, username,
							res.getString(R.string.username_error));
					TruckApp.editTextValidation(password_et, password,
							res.getString(R.string.password_error));
					TruckApp.editTextValidation(mobile_et_lgn, mobile,
							res.getString(R.string.mobile_error));

				}
			}
		});

		signupdialog = new Dialog(LoginActivity.this,
				android.R.style.Theme_Dialog);
		signupdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		signupdialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		signupdialog.setContentView(R.layout.signupdialog);

		fullname_et = (EditText) signupdialog.findViewById(R.id.fullname_et);
		mobile_et = (EditText) signupdialog.findViewById(R.id.mobile_et);
		address_et = (EditText) signupdialog.findViewById(R.id.address_et);
		signup_btn = (Button) signupdialog.findViewById(R.id.send_btn);
		signupcnl_btn = (Button) signupdialog.findViewById(R.id.cancel_btn);
		note_tv = (TextView) signupdialog.findViewById(R.id.note_tv);
		customerTypeSpn = (Spinner) signupdialog
				.findViewById(R.id.customertype_spn);

		customerTypeSpn.setAdapter(new ArrayAdapter<String>(context,
				R.layout.simplelistitem, R.id.text1, customerTypes));
		customerTypeSpn.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int arg2, long arg3) {
				selectedCustomerType = customerValues.get(customerTypeSpn
						.getSelectedItemPosition());
				System.out.println("CustomerType:" + selectedCustomerType);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		note_tv.setText(Html
				.fromHtml("<b>NOTE:</b>Password will be sent to your mobile"));

		signup_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (detectCnnection.isConnectingToInternet()) {
					fullname_et.clearFocus();
					signup_act(v);
				} else {
					Toast.makeText(context,
							res.getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}

			}
		});
	}

	@Override
	protected void onDestroy() {
		signupdialog = null;
		forgotdialog = null;
		super.onDestroy();
	}

	private class Login extends AsyncTask<String, String, JSONObject> {
		String un, pwd, deviceId,mobileNo;

		public Login(String uName, String pWord,String mobile, String dId) {
			un = uName;
			pwd = pWord;
			deviceId = dId;
			mobileNo = mobile;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressFrame.setVisibility(View.VISIBLE);
		}

		@Override
		protected JSONObject doInBackground(String... strings) {
			JSONObject json = null;
			try {
				Log.e("DeviceId", deviceId);
				String urlParameters = "userName="
						+ URLEncoder.encode(un, "UTF-8") + "&password="
						+ URLEncoder.encode(pwd, "UTF-8") + "&mobile="
						+ URLEncoder.encode(mobileNo, "UTF-8") + "&deviceid="
						+ URLEncoder.encode(deviceId, "UTF-8") + "&type="
						+ URLEncoder.encode("truck", "UTF-8");
				String res = parser.excutePost(TruckApp.loginURL, urlParameters);
				System.out.println("EG eeurlParameters o/p"+urlParameters);
				json = new JSONObject(res);
			} catch (Exception e) {
				Log.e("EG Login DoIN EX", e.toString());
			}
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject s) {
			super.onPostExecute(s);
			login_btn.setEnabled(true);

			if (s != null) {
				try {
					if (0 == s.getInt("status")) {
						progressFrame.setVisibility(View.GONE);
						Toast.makeText(getApplicationContext(),res.getString(R.string.loginFailed),Toast.LENGTH_LONG).show();
						//new ERPLogin(un, pwd,mobileNo).execute();
					} else {
						if (rememberme_cb.isChecked()) {
							editor.putString("username", un);
						} else {
							editor.putString("username", "");
						}
						editor.putString("userdata",
								(s.getJSONObject("success")).toString());


						editor.putString("phone", s.getJSONObject("success")
								.getString("contactPhone"));
						editor.putString("accountID", s
								.getJSONObject("success")
								.getString("accountID"));
						editor.putString("uid", s.getJSONObject("success")
								.getString("uid"));
						editor.putString("type", s.getJSONObject("success")
								.getString("type"));

						editor.putInt("gps",
								s.getJSONObject("success").getInt("gps"));
						editor.putInt("truck", s.getJSONObject("success")
								.getInt("truck"));
						editor.putInt("loads", s.getJSONObject("success")
								.getInt("loads"));
						editor.putInt("postload", s.getJSONObject("success")
								.getInt("postload"));
						editor.putInt("loadstatus", s.getJSONObject("success")
								.getInt("loadstatus"));
						editor.putInt("orders", s.getJSONObject("success")
								.getInt("orders"));
						editor.putInt("truckavailable",s.getJSONObject("success").getInt(
								"truckavailable"));
						editor.putInt("distanceReport",s.getJSONObject("success").getInt(
								"DistanceReport"));
						editor.putInt("createGroup",s.getJSONObject("success").getInt(
								"CreateGroup"));

						editor.putInt("dashboard",s.getJSONObject("success").getInt(
								"Dashboard"));
						editor.putInt("settings",s.getJSONObject("success").getInt(
								"Settings"));
						editor.putInt("shareVehicle",s.getJSONObject("success").getInt(
								"ShareVehicle"));

						editor.putString("groupID",s.getJSONObject("success").getString(
								"groupID"));
						editor.putString("groupName",s.getJSONObject("success").getString(
								"groupName"));

						editor.putInt("buyselltrucks",s.getJSONObject("success").getInt(
								"buyselltrucks"));
						editor.putInt("egAccount",s.getInt("egAccount"));
						editor.putInt("login", 1);

						editor.putString("contactName", s.getJSONObject("success")
								.getString("contactName"));
						editor.putString("contactPhone", s.getJSONObject("success")
								.getString("contactPhone"));
						if(s.has("access_token")){
							editor.putString("access_token",s.getString("access_token"));
						}
						editor.commit();
						//startActivity(new Intent(context,HomeScreenActivity.class));
						//finish();
						//username, password,mobile, preferences.getString(SplashActivity.REG_ID, ""
						String username = username_et.getText().toString().trim();
						String password = password_et.getText().toString().trim();
						String mobile   = mobile_et_lgn.getText().toString().trim();

						new ERPLogin(username, password,mobile).execute();




					}
				} catch (Exception e) {
					progressFrame.setVisibility(View.GONE);
				}
			} else {
				progressFrame.setVisibility(View.GONE);
				//new ERPLogin(un, pwd,mobileNo).execute();
				Toast.makeText(getApplicationContext(), res.getString(R.string.exceptionmsg),
						Toast.LENGTH_LONG).show();
			}
		}
	}


	@Override
	public void onBackPressed() {
		Intent homeIntent = new Intent(Intent.ACTION_MAIN);
		homeIntent.addCategory( Intent.CATEGORY_HOME );
		homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeIntent);
		finish();
	}

	private class ForgotPassword extends AsyncTask<String, String, JSONObject> {
		String un;

		public ForgotPassword(String uName) {
			un = uName;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			login_btn.setEnabled(false);
			progressFrame.setVisibility(View.VISIBLE);
		}

		@Override
		protected JSONObject doInBackground(String... strings) {
			JSONObject res = null;
			try {

				String urlParameters = "userName="
						+ URLEncoder.encode(un, "UTF-8") + "&type="
						+ URLEncoder.encode("truck", "UTF-8");
				System.out.println("" + urlParameters);
				String result = parser.excutePost(TruckApp.frgotPasswordURL,
						urlParameters);
				res = new JSONObject(result);

			} catch (Exception e) {
				Log.e("Login DoIN EX", e.toString());
				res = null;
			}
			return res;
		}

		@Override
		protected void onPostExecute(JSONObject s) {
			super.onPostExecute(s);
			login_btn.setEnabled(true);
			progressFrame.setVisibility(View.GONE);

			if (s != null) {
				try {
					if (0 == s.getInt("status")) {
						final JSONArray errorObj = s.getJSONArray("error");
						StringBuilder builder = new StringBuilder();
						for (int i = 0; i < errorObj.length(); i++) {
							builder.append(
									errorObj.getJSONObject(i).getString("msg"))
									.append("\n");
						}
						Toast.makeText(context, builder.toString(),
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(context, s.getString("success"),
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					System.out.println("Exception while extracting the response:"+ e.toString());
				}
			} else {
				Toast.makeText(context, res.getString(R.string.exceptionmsg),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void forgotpwd_act(View v) {
		try {
			forgot_et.setText("");
			phone_et.setText("");
			forgotdialog.show();
			send_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String forgotemail = forgot_et.getText().toString().trim();
					/* String forgotmob = phone_et.getText().toString().trim(); */
					if (forgotemail.length() != 0 /*
												 * && forgotmob.length() != 0 &&
												 * forgotmob.length() == 10
												 */) {
						if (detectCnnection.isConnectingToInternet()) {
							new ForgotPassword(forgotemail).execute();
							forgotdialog.dismiss();
						} else {
							Toast.makeText(context,
									res.getString(R.string.internet_str),
									Toast.LENGTH_LONG).show();
						}
					} else {
						TruckApp.editTextValidation(
								forgot_et,
								forgotemail,
								getResources().getString(
										R.string.username_error));
						/*
						 * TruckApp.editTextValidation(phone_et, forgotmob,
						 * getResources().getString(R.string.mobile_error));
						 * if(forgotmob.length()!=0 && forgotmob.length()<10){
						 * Toast.makeText(context,
						 * res.getString(R.string.mobilelength_error),
						 * Toast.LENGTH_LONG).show(); }
						 */
					}
				}
			});

			cancel_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					forgotdialog.dismiss();
				}
			});
		} catch (Exception e) {
			Log.e("Error", e.toString());
			System.out.println("Error:" + e.toString());
		}
	}

	public void signup_act(View v) {
		try {
			fullname_et.setText("");
			mobile_et.setText("");
			address_et.setText("");
			signupdialog.show();
			signup_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String forgotemail = fullname_et.getText().toString()
							.trim();
					String forgotmob = mobile_et.getText().toString().trim();
					String forgotaddress = address_et.getText().toString()
							.trim();

					if (forgotemail.length() != 0
							&& forgotaddress.length() != 0
							&& forgotmob.length() != 0
							&& forgotmob.length() == 10) {
						if (detectCnnection.isConnectingToInternet()) {
							System.out.println("FN:" + forgotemail + "MN:"
									+ forgotmob + "Add:" + forgotaddress);
							new SignUp(forgotemail, forgotmob, forgotaddress,
									selectedCustomerType).execute();
							signupdialog.dismiss();
						} else {
							Toast.makeText(context,
									res.getString(R.string.internet_str),
									Toast.LENGTH_LONG).show();
						}
					} else {
						TruckApp.editTextValidation(
								fullname_et,
								forgotemail,
								getResources().getString(
										R.string.fullname_error));
						TruckApp.editTextValidation(address_et, forgotaddress,
								getResources()
										.getString(R.string.address_error));
						TruckApp.editTextValidation(mobile_et, forgotmob,
								getResources().getString(R.string.mobile_error));
						if (forgotmob.length() != 0 && forgotmob.length() < 10) {
							Toast.makeText(context,
									res.getString(R.string.mobilelength_error),
									Toast.LENGTH_LONG).show();
						}
					}
				}
			});

			signupcnl_btn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					signupdialog.dismiss();
				}
			});
		} catch (Exception e) {
			Log.e("Error", e.toString());
			System.out.println("Error:" + e.toString());
		}
	}

	private class SignUp extends AsyncTask<String, String, String> {
		String fn, phn, address, type;

		public SignUp(String fName, String phone, String address,
					  String customerType) {
			this.fn = fName;
			this.phn = phone;
			this.address = address;
			this.type = customerType;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			login_btn.setEnabled(false);
			progressFrame.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... strings) {
			try {

				String urlParameters = "fullname="
						+ URLEncoder.encode(fn, "UTF-8") + "&mobile="
						+ URLEncoder.encode(phn, "UTF-8") + "&address="
						+ URLEncoder.encode(address, "UTF-8") + "&type="
						+ URLEncoder.encode(type, "UTF-8");
				String result = parser.excutePost(TruckApp.setCustomerURL,
						urlParameters);
				System.out.println("" + urlParameters + "res" + result);

				return result;
			} catch (Exception e) {
				Log.e("Login DoIN EX", e.toString());
				res = null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			login_btn.setEnabled(true);
			progressFrame.setVisibility(View.GONE);

			if (s != null) {
				try {
					int length = s.trim().length();
					if (length > 22) {
						Toast.makeText(
								context,
								"Account created successfully.Password sent to your mobile",
								Toast.LENGTH_LONG).show();
					} else {
						JSONObject json = new JSONObject(s);
						if (0 == json.getInt("status")) {
							Toast.makeText(context, "Account created failed",
									Toast.LENGTH_LONG).show();
						} else if (-1 == json.getInt("status")) {
							Toast.makeText(
									context,
									"Account with this mobile no already exists.",
									Toast.LENGTH_LONG).show();
						}
					}
				} catch (JSONException e) {
					System.out
							.println("Exception while extracting the response:"
									+ e.toString());
				}
			} else {
				Toast.makeText(context, res.getString(R.string.exceptionmsg),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TruckApp.getInstance().trackScreenView("Login Screen");
	}


	private class ERPLogin extends AsyncTask<String, String, JSONObject> {
		String un, pwd, deviceId,mobileNo;

		public ERPLogin(String uName, String pWord,String mobile) {
			un = uName;
			pwd = pWord;
			mobileNo = mobile;
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressFrame.setVisibility(View.VISIBLE);
		}

		@Override
		protected JSONObject doInBackground(String... strings) {
			JSONObject res = null;
			try {

				JSONObject post_dict = new JSONObject();

				try {
					post_dict.put("userName" , "test");
					post_dict.put("name","easyGaadi");
					post_dict.put("password", "password");

				} catch (JSONException e) {
					e.printStackTrace();
				}
				System.out.println("ERPlogin" + "--"+TruckApp.userLoginURL);
				String result = parser.easyyExcutePost(LoginActivity.this,TruckApp.userLoginURL,String.valueOf(post_dict));
				System.out.println("ERPlogin o/p"+result);
				res = new JSONObject(result);

			} catch (Exception e) {
				Log.e("ERP Login DoIN EX", e.toString());
				res = null;
			}
			return res;
		}
		@Override
		protected void onPostExecute(JSONObject s) {
			super.onPostExecute(s);
			login_btn.setEnabled(true);
			progressFrame.setVisibility(View.GONE);
			//Log.v("response","res"+s.toString());
			if (s != null) {

				try {
					//JSONObject js = new JSONObject(s);
					if (!s.getBoolean("status")) {
						Toast.makeText(context, "fail",Toast.LENGTH_LONG).show();

					} else {


						SharedPreferences.Editor editor = getSharedPreferences(getResources().getString(R.string.shareP_erp), MODE_PRIVATE).edit();
						editor.putString("token", s.getString("token"));
						//editor.putString("role", s.getString("role"));
						editor.apply();
						startActivity(new Intent(context,HomeScreenActivity.class));//HomeScreenActivity
						finish();
						Toast.makeText(context, s.getString("success"),
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					System.out.println("Exception while extracting the response:"+ e.toString());
				}
			} else {
				Toast.makeText(context, res.getString(R.string.exceptionmsg),
						Toast.LENGTH_LONG).show();
			}
		}
	}


}
