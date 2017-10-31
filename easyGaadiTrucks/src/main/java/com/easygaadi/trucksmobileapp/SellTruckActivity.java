package com.easygaadi.trucksmobileapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.trucksmobileapp.AndroidMultiPartEntity.ProgressListener;
import com.easygaadi.utils.Constants;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class SellTruckActivity extends Activity {

	private final int PICTURE_TAKEN_FROM_CAMERA = 1;
	private final int PICTURE_TAKEN_FROM_GALLERY = 2;
	private String selectedImagePath = null;

	FrameLayout progressFrame;
	JSONParser parser;
	Resources res;

	TabHost tabHost;
	Context context;
	SharedPreferences preferences;
	Editor editor;
	ConnectionDetector detectCnnection;
	ProgressDialog pDialog;
	Dialog uploadDocDialog;

	Button btn_Finish, Submit;
	static EditText InsuranceExoDate;
	static EditText FitnessExpDate;
	EditText YearofMFg, Odometer, AnyAccidents, ExpectedPrice, VehicleRegNo,
			ContactName, Mobile;
	Spinner VehicleType, RegState;
	RadioButton Infinance_YES, Infinace_NO, AnyAccident_YES, AnyAccident_NO;

	RadioGroup radioAccidentGroup, radioInfinaceGroup;
	RadioButton radioAccidentButton, radioInfinaceButton;

	ImageView imagTruckFront, imgTruckBack, imgTyrefrontLeftpic,
			imgTyrefrontRightpic, imgTyreBackRightpic, picture,
			imgTyreBackLeftpic, Otherpic1, Otherpic2;
	Button pick_btn, capture_btn, save_pic, upload_pic;
	ImageView closeDialog_iv;
	TextView docTitle, imageName_tv, truck_FrontPic, truck_BackPic;

	Uri selectedImageUri;
	String contentUri;
	String Str_Uid, Str_GPSAccountId, Str_VehicleRegNo, Str_Id_trucktype,
			Str_ContactName, Str_Mobile, Str_InsuranceExoDate,
			Str_FitnessExpDate, Str_YearofMFg, Str_Odometer, Str_AnyAccidents,
			Str_InFinance, Str_ExpectedPrice, Str_VehicleType, Str_RegState,
			ContentUri;

	String idTtype, Uidd, GPSAccid, Vg, id_truckype, Cn, Mobilee, IED, FED,
			YearMg, Odo, Accidnt, anyFinance, price, state, VType, value;

	String typeofpic, Str_truck_frontPic, pictypeid, id_sellTruck;


	int pYear, pMonth,pDay ,selectedIdAccident,selectedIdInfinance ,position_VehicleType,position_RegSate;

	Bitmap bitmap;
	String AccRBValue, InfinaceRBValue;

	private static final String TAG = ImageCaptureActivity.class
			.getSimpleName();

	ProgressBar progressBar;
	TextView txtPercentage;
	long totalSize = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selltruck);

		tabHost = (TabHost) findViewById(R.id.tabHost);
		tabHost.setup();

		// Tab 1
		TabHost.TabSpec spec1 = tabHost.newTabSpec("Tab One");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Details");

		// Tab 2
		TabHost.TabSpec spec2 = tabHost.newTabSpec("Tab Two");
		spec2.setContent(R.id.tab2);
		spec2.setIndicator("Upload Pic");

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);

//		tabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#ffbf00"));
//		tabHost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#7392B5"));
//		
//		tabHost.setOnTabChangedListener(new OnTabChangeListener(){
//			@Override
//			public void onTabChanged(String tabId) {
//			    // TODO Auto-generated method stub
//				
//			     for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
//			        {
//			        tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ffbf00")); //unselected
//			        }
//			        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#FFFFFF")); // selected
//			}
//			});


		context = this;
		new ConnectionDetector(context);
		detectCnnection = new ConnectionDetector(context);

		pDialog = new ProgressDialog(this);
		parser = JSONParser.getInstance();


		uploadDocDialog = new Dialog(SellTruckActivity.this,
				android.R.style.Theme_Dialog);
		uploadDocDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		uploadDocDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		uploadDocDialog.setContentView(R.layout.docdialog);
		closeDialog_iv = (ImageView) uploadDocDialog
				.findViewById(R.id.close_iv);
		pick_btn = (Button) uploadDocDialog.findViewById(R.id.pick_btn);
		save_pic = (Button) uploadDocDialog.findViewById(R.id.saveButton);
		upload_pic = (Button) uploadDocDialog.findViewById(R.id.uploadButton);
		capture_btn = (Button) uploadDocDialog.findViewById(R.id.capture_btn);
		picture = (ImageView) uploadDocDialog.findViewById(R.id.img_preview);
		docTitle = (TextView) uploadDocDialog.findViewById(R.id.doc_title);
		imageName_tv = (TextView) uploadDocDialog
				.findViewById(R.id.imageName_tv);
		progressBar = (ProgressBar) uploadDocDialog
				.findViewById(R.id.progressBar);
		txtPercentage = (TextView) uploadDocDialog
				.findViewById(R.id.txtPercentage);

		VehicleRegNo = (EditText) findViewById(R.id.et_VehicleRegNo);
		ContactName = (EditText) findViewById(R.id.et_ContacNamet);
		Mobile = (EditText) findViewById(R.id.et_Mobile);
		InsuranceExoDate = (EditText) findViewById(R.id.et_InsuranceExpdate);
		FitnessExpDate = (EditText) findViewById(R.id.et_FitnessExpDate);
		YearofMFg = (EditText) findViewById(R.id.et_YearOfManufacture);
		Odometer = (EditText) findViewById(R.id.et_Odometer);
		ExpectedPrice = (EditText) findViewById(R.id.et_ExpectedPrice);

		preferences = getApplicationContext().getSharedPreferences(
				getResources().getString(R.string.app_name), MODE_PRIVATE);
		editor = preferences.edit();

		radioAccidentGroup = (RadioGroup) findViewById(R.id.radioaccGroup);
		radioInfinaceGroup = (RadioGroup) findViewById(R.id.radiofinaceGroup);

		VehicleType = (Spinner) findViewById(R.id.sp_VehicleType);
		RegState = (Spinner) findViewById(R.id.sp_State);

		// truck upload images
		imagTruckFront = (ImageView) findViewById(R.id.img_UploadTruckFront);
		imgTruckBack = (ImageView) findViewById(R.id.img_UploadTruckBack);

		imgTyrefrontLeftpic = (ImageView) findViewById(R.id.img_UploadfrontTyresLeft);
		imgTyrefrontRightpic = (ImageView) findViewById(R.id.img_UploadfrontTyresRight);
		imgTyreBackLeftpic = (ImageView) findViewById(R.id.img_UploadbackTyresLeft);
		imgTyreBackRightpic = (ImageView) findViewById(R.id.img_UploadbackTyresRight);

		Otherpic1 = (ImageView) findViewById(R.id.img_anyotherpic1);
		Otherpic2 = (ImageView) findViewById(R.id.img_anyotherpic2);

		Submit = (Button) findViewById(R.id.btn_selltruck_submit);
		btn_Finish = (Button) findViewById(R.id.btn_selltruck_finish);

		ContactName.setText(preferences.getString("contactName", ""));
		Mobile.setText(preferences.getString("contactPhone", ""));



		//tab disable code
		tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(false);

		VehicleRegNo.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				VehicleRegNo.setError(null);
			}
		});


		imagTruckFront.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Str_truck_frontPic = "truck_front_pic";

				if (detectCnnection.isConnectingToInternet()) {
					selectedImagePath = null;
					displayDocDialog(Str_truck_frontPic);
				} else {
					Toast.makeText(context,
							getResources().getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}
			}
		});
		imgTruckBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				typeofpic = "truck_back_pic";

				if (detectCnnection.isConnectingToInternet()) {
					selectedImagePath = null;
					displayDocDialog(typeofpic);
				} else {
					Toast.makeText(context,
							getResources().getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}
			}
		});
		imgTyrefrontLeftpic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				typeofpic = "tyres_front_left_pic";

				if (detectCnnection.isConnectingToInternet()) {
					selectedImagePath = null;
					displayDocDialog(typeofpic);
				} else {
					Toast.makeText(context,
							getResources().getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}
			}
		});
		imgTyrefrontRightpic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				typeofpic = "tyres_front_right_pic";

				if (detectCnnection.isConnectingToInternet()) {
					selectedImagePath = null;
					displayDocDialog(typeofpic);
				} else {
					Toast.makeText(context,
							getResources().getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		imgTyreBackLeftpic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				typeofpic = "tyres_back_left_pic";

				if (detectCnnection.isConnectingToInternet()) {
					selectedImagePath = null;
					displayDocDialog(typeofpic);
				} else {
					Toast.makeText(context,
							getResources().getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}
			}
		});
		imgTyreBackRightpic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				typeofpic = "tyres_back_right_pic";

				if (detectCnnection.isConnectingToInternet()) {
					selectedImagePath = null;
					displayDocDialog(typeofpic);
				} else {
					Toast.makeText(context,
							getResources().getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}
			}
		});
		Otherpic1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				typeofpic = "other_pic_1";

				if (detectCnnection.isConnectingToInternet()) {
					selectedImagePath = null;
					displayDocDialog(typeofpic);
				} else {
					Toast.makeText(context,
							getResources().getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}
			}
		});
		Otherpic2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				typeofpic = "other_pic_2";

				if (detectCnnection.isConnectingToInternet()) {
					selectedImagePath = null;
					displayDocDialog(typeofpic);
				} else {
					Toast.makeText(context,
							getResources().getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		InsuranceExoDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("IN");
				pYear = pMonth = pDay = 0;
				DatePickerDialog dialog = new DatePickerDialog(context,
						pDateSetListenerIn, pYear, pMonth, pDay);
				setCurDatetoDia(dialog);
				dialog.show();
			}

		});

		FitnessExpDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("FIT");
				pYear = pMonth = pDay = 0;
				DatePickerDialog dialog = new DatePickerDialog(context,
						pDateSetListenerFit, pYear, pMonth, pDay);
				setCurDatetoDia(dialog);
				dialog.show();
			}

		});

		Submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Str_Uid = preferences.getString("uid", "");
				Str_GPSAccountId = preferences.getString("accountID", "");
				// Str_Id_trucktype = "sampleid1";

				Str_VehicleRegNo = VehicleRegNo.getText().toString().trim();
				Str_ContactName = ContactName.getText().toString().trim();
				Str_Mobile = Mobile.getText().toString().trim();
				Str_InsuranceExoDate = InsuranceExoDate.getText().toString()
						.trim();
				Str_FitnessExpDate = FitnessExpDate.getText().toString().trim();
				Str_YearofMFg = YearofMFg.getText().toString().trim();
				Str_Odometer = Odometer.getText().toString().trim();
				Str_ExpectedPrice = ExpectedPrice.getText().toString().trim();

				position_VehicleType = VehicleType.getSelectedItemPosition();
				position_RegSate =RegState.getSelectedItemPosition();

				Str_RegState = RegState.getSelectedItem().toString().trim();
				Str_VehicleType = VehicleType.getSelectedItem().toString().trim();
				// Radio Button Values
				selectedIdAccident = radioAccidentGroup.getCheckedRadioButtonId();
				radioAccidentButton = (RadioButton) findViewById(selectedIdAccident);

				selectedIdInfinance = radioInfinaceGroup.getCheckedRadioButtonId();
				radioInfinaceButton = (RadioButton) findViewById(selectedIdInfinance);

				// Vhehicle Spiiner type id

				if (position_VehicleType == 1) {
					Str_Id_trucktype = "17";
				} else if (position_VehicleType == 2) {
					Str_Id_trucktype = "18";
				} else if (position_VehicleType == 3) {
					Str_Id_trucktype = "10";
				} else if (position_VehicleType == 4) {
					Str_Id_trucktype = "38";
				} else if (position_VehicleType == 5) {
					Str_Id_trucktype = "12";
				} else if (position_VehicleType == 6) {
					Str_Id_trucktype = "14";
				} else if (position_VehicleType == 7) {
					Str_Id_trucktype = "15";
				} else if (position_VehicleType == 8) {
					Str_Id_trucktype = "19";
				} else if (position_VehicleType == 9) {
					Str_Id_trucktype = "20";
				} else if (position_VehicleType == 10) {
					Str_Id_trucktype = "39";
				} else if (position_VehicleType == 11) {
					Str_Id_trucktype = "21";
				} else if (position_VehicleType == 12) {
					Str_Id_trucktype = "30";
				} else if (position_VehicleType == 13) {
					Str_Id_trucktype = "27";
				} else if (position_VehicleType == 14) {
					Str_Id_trucktype = "22";
				} else if (position_VehicleType == 15) {
					Str_Id_trucktype = "31";
				} else if (position_VehicleType == 16) {
					Str_Id_trucktype = "28";
				} else if (position_VehicleType == 17) {
					Str_Id_trucktype = "29";
				} else if (position_VehicleType == 18) {
					Str_Id_trucktype = "36";
				} else if (position_VehicleType == 19) {
					Str_Id_trucktype = "32";
				} else if (position_VehicleType == 20) {
					Str_Id_trucktype = "24";
				} else if (position_VehicleType == 21) {
					Str_Id_trucktype = "23";
				} else if (position_VehicleType == 22) {
					Str_Id_trucktype = "26";
				} else if (position_VehicleType == 23) {
					Str_Id_trucktype = "25";
				} else if (position_VehicleType == 24) {
					Str_Id_trucktype = "16";
				} else if (position_VehicleType == 25) {
					Str_Id_trucktype = "37";
				} else if (position_VehicleType == 26) {
					Str_Id_trucktype = "9";
				} else if (position_VehicleType == 27) {
					Str_Id_trucktype = "8";
				}

				if (radioAccidentButton.getText().toString().trim()
						.equals("Yes")) {
					AccRBValue = "1";
				} else if (radioAccidentButton.getText().toString().trim()
						.equals("No")) {
					AccRBValue = "0";
				}
				if (radioInfinaceButton.getText().toString().trim()
						.equals("Yes")) {
					InfinaceRBValue = "1";
				} else if (radioInfinaceButton.getText().toString().trim()
						.equals("No")) {
					InfinaceRBValue = "0";
				}

				if (detectCnnection.isConnectingToInternet()) {

					if (Str_VehicleRegNo.isEmpty()) {

						VehicleRegNo.setError(Html.fromHtml("<font color='red'>"
								+ "Please enter Vehicle RegNo"
								+ "</font>"));
						VehicleRegNo.requestFocus();

					} else if (position_VehicleType == 0) {
						((TextView) VehicleType.getSelectedView())
								.setError(Html.fromHtml("<font color='red'>"
										+ "Please Select Type" + "</font>"));
						Toast.makeText(context, "please Select Vehicle Type", Toast.LENGTH_LONG).show();
						VehicleType.requestFocus();

					} else if (Str_ContactName.isEmpty()) {

						ContactName.setError(Html.fromHtml("<font color='red'>"
								+ "Please enter Contact Name" + "</font>"));
						ContactName.requestFocus();
					} else if (Str_Mobile.isEmpty()) {
						Mobile.setError(Html.fromHtml("<font color='red'>"
								+ "Please enter Mobile Number" + "</font>"));
						Mobile.requestFocus();
					}
					else if (position_RegSate == 0) {
						((TextView) RegState.getSelectedView())
								.setError(Html.fromHtml("<font color='red'>"
										+ "Please Select Sate" + "</font>"));
						Toast.makeText(context, "please Select Sate", Toast.LENGTH_LONG).show();
						RegState.requestFocus();
					}

					else {

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								context);
						alertDialogBuilder
								.setMessage("Have you given required details?Do you want to proceed further to upload pics?");

						alertDialogBuilder.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
														int arg1) {
										// Toast.makeText(SellTruckActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();

										submit_act(
												"Creating truck",
												getResources()
														.getString(
																R.string.failcreateTr_message),
												getResources()
														.getString(
																R.string.successcreateTr_message));

									}
								});

						alertDialogBuilder.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										dialog.dismiss();
									}
								});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();

					}

				} else {
					Toast.makeText(context,
							getResources().getString(R.string.internet_str),
							Toast.LENGTH_LONG).show();
				}

			}
		});

		btn_Finish.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub




				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder
						.setMessage("Have you uploaded pics..? Do you want to complete the process.");

				alertDialogBuilder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {

								Toast.makeText(
										SellTruckActivity.this,
										"You have successfully posted your truck. Will get back to you soon.",
										Toast.LENGTH_LONG).show();

								tabHost.setCurrentTab(0);

							}
						});

				alertDialogBuilder.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
							}
						});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

				YearofMFg.setText("");
				Odometer.setText("");
				//AnyAccidents.setText("");
				ExpectedPrice.setText("");
				VehicleRegNo.setText("");
				//ContactName.setText("");
				//Mobile.setText("");
			}
		});

	}

	protected void displayDocDialog(final String pictype) {
		// TODO Auto-generated method stub

		imageName_tv.setText("");
		picture.setImageDrawable(null);
		selectedImagePath = null;
		progressBar.setVisibility(View.INVISIBLE);
		txtPercentage.setVisibility(View.INVISIBLE);
		uploadDocDialog.show();
		// docTitle.setText(keyValue.getValue()+"/"+id+"_"+lead_name);
		closeDialog_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				uploadDocDialog.dismiss();
			}
		});

		pick_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_PICK);
					startActivityForResult(
							Intent.createChooser(intent, "Select Pic"),
							PICTURE_TAKEN_FROM_GALLERY);
				} catch (Exception e) {
					System.out.println("Exception when calling the gallery");
				}
			}
		});

		capture_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, PICTURE_TAKEN_FROM_CAMERA);
				} catch (Exception e) {
					System.out.println("Exception when calling the gallery");
				}
			}
		});

		save_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (selectedImagePath != null) {
					new UploadFileToServer(pictype).execute();
				} else {
					Toast.makeText(context, "Please select image",
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		System.out.println("Request Code:" + requestCode);
		System.out.println("Result Code:" + resultCode);

		if (requestCode == PICTURE_TAKEN_FROM_GALLERY) {
			try {
				if (resultCode == RESULT_OK) {
					selectedImageUri = data.getData();
					selectedImagePath = TruckApp.getRealPathFromURI(
							selectedImageUri, context);
					System.out.println("Image Path : " + selectedImagePath);
					if (selectedImagePath == null) {
						Toast.makeText(context, "Please select image",
								Toast.LENGTH_SHORT).show();
					} else {
						File file = new File(selectedImagePath);
						imageName_tv.setText("FileName:" + file.getName());
						file = null;
						picture.setImageBitmap(TruckApp.getThumbnail(
								selectedImageUri, context, 100));
					}
				}
			} catch (Exception e) {
				selectedImagePath = null;
				Toast.makeText(context, "Please select another image ",
						Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == PICTURE_TAKEN_FROM_CAMERA) {
			try {
				if (resultCode == RESULT_OK) {
					Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
					File destination = new File(
							Environment.getExternalStorageDirectory(),
							System.currentTimeMillis() + ".jpg");
					FileOutputStream fo;
					try {
						destination.createNewFile();
						fo = new FileOutputStream(destination);
						fo.write(bytes.toByteArray());
						fo.close();
						selectedImagePath = destination.getAbsolutePath();
						destination = null;
						System.out.println("File path:" + selectedImagePath);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (selectedImagePath == null) {
						Toast.makeText(context,
								"Please capture image properly",
								Toast.LENGTH_SHORT).show();
					} else {
						File file = new File(selectedImagePath);
						imageName_tv.setText("FileName:" + file.getName());
						file = null;
						picture.setImageBitmap(thumbnail);

						// down sizing image as it throws OutOfMemory Exception
						// for larger
						// images

					}
				}
			} catch (Exception e) {
				selectedImagePath = null;
				Toast.makeText(context, "Please capture another image",
						Toast.LENGTH_SHORT).show();
			}

		}

	}

	public void submit_act(String message, String errorMsg, String successMsg) {

		new SetTruck(Str_Uid, Str_GPSAccountId, Str_VehicleRegNo,
				Str_Id_trucktype, Str_ContactName, Str_Mobile,
				Str_InsuranceExoDate, Str_FitnessExpDate, Str_YearofMFg,
				Str_Odometer, AccRBValue, InfinaceRBValue, Str_ExpectedPrice,
				Str_RegState, Str_VehicleType).execute();
	}

	private class SetTruck extends AsyncTask<String, String, JSONObject> {

		public SetTruck(String uid, String GPsid, String Vgno,
						String idtrucktype, String Cname, String MNo, String InED,
						String FtED, String Mgyear, String odomtr, String Acc,
						String finace, String Eprice, String Rstate, String VhType) {

			Uidd = uid;
			// ContentUri =COntentUri;
			GPSAccid = GPsid;
			Vg = Vgno;
			id_truckype = idtrucktype;
			Cn = Cname;
			Mobilee = MNo;
			IED = InED;
			FED = FtED;
			YearMg = Mgyear;
			Odo = odomtr;
			Accidnt = Acc;
			anyFinance = finace;
			price = Eprice;
			state = Rstate;
			VType = VhType;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("Please wait");
			pDialog.show();

		}

		@Override
		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				// Log.e("DeviceId", deviceId);

				String urlParameters = setUrlParameters(Uidd, GPSAccid, Vg,
						id_truckype, VType, Cn, Mobilee, state, IED, FED,
						YearMg, Odo, Accidnt, anyFinance, price);

				String res = parser.excutePost(TruckApp.SellTruckListURL,
						urlParameters);
				// String res = res1.substring(0, 24);
				json = new JSONObject(res);

				id_sellTruck = json.getString("id_sell_truck");

			} catch (Exception e) {
				Log.e("Login DoIN EX", e.toString());
				Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
			}
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			TruckApp.checkPDialog(pDialog);
			if (result != null) {
				try {

					if (0 == result.getInt("status")) {
						Toast.makeText(context, "Error", Toast.LENGTH_LONG)
								.show();
					}else if (2 == result.getInt("status")) {
						//TruckApp.logoutAction(SellTruckActivity.this);
						SharedPreferences sharedPreferences = getSharedPreferences(
								getResources().getString(R.string.app_name), MODE_PRIVATE);
						SharedPreferences.Editor
								editor = sharedPreferences.edit();
						editor.putInt("login", 0).commit();
						editor.putString("accountID", "").commit();
						editor.putString(Constants.FUEL_USERNAME_KEY,"").commit();
						editor.putString(Constants.FUEL_PASSWORD_KEY,"").commit();
						editor.putString(Constants.FUEL_CUSTOMERID_KEY,"").commit();
						editor.putString(Constants.TOLL_USERNAME_KEY,"").commit();
						editor.putString(Constants.TOLL_PASSWORD_KEY,"").commit();
						editor.putString(Constants.TOLL_CUSTOMERID_KEY,"").commit();
						startActivity(new Intent(context,
								LoginActivity.class));
						finish();
					}  else if (1 == result.getInt("status")) {
						Toast.makeText(context,
								"Successfully Added, Please Upload Truck Pics",
								Toast.LENGTH_LONG).show();

						tabHost.getTabWidget().getChildTabViewAt(0)
								.setEnabled(false);
						tabHost.getTabWidget().getChildTabViewAt(1)
								.setEnabled(true);
						tabHost.setCurrentTab(1);
					}
				} catch (Exception e) {
					System.out
							.println("exception in add trucks" + e.toString());
				}
			} else {
				Toast.makeText(context,
						getResources().getString(R.string.exceptionmsg),
						Toast.LENGTH_LONG).show();
			}
		}

		protected String setUrlParameters(String Uidd, String GPSAccid,
										  String Vg, String id_truckype, String VType, String Cn,
										  String Mobilee, String state, String IED, String FED,
										  String YearMg, String Odo, String Accidnt, String anyFinance,
										  String price) {

			StringBuilder builder = new StringBuilder();
			try {
				builder.append("uid=").append(URLEncoder.encode(Uidd, "UTF-8"));
				builder.append("&gps_account_id=").append(
						URLEncoder.encode(GPSAccid, "UTF-8"));
				builder.append("&truck_reg_no=").append(
						URLEncoder.encode(Vg, "UTF-8"));
				builder.append("&id_truck_type=").append(
						URLEncoder.encode(id_truckype, "UTF-8"));
				builder.append("&truck_type_title=").append(
						URLEncoder.encode(VType, "UTF-8"));
				builder.append("&contact_name=").append(
						URLEncoder.encode(Cn, "UTF-8"));
				builder.append("&contact_mobile=").append(
						URLEncoder.encode(Mobilee, "UTF-8"));
				builder.append("&truck_reg_state=").append(
						URLEncoder.encode(state, "UTF-8"));
				builder.append("&insurance_exp_date=").append(
						URLEncoder.encode(IED, "UTF-8"));
				builder.append("&fitness_exp_date=").append(
						URLEncoder.encode(FED, "UTF-8"));
				builder.append("&year_of_mfg=").append(
						URLEncoder.encode(YearMg, "UTF-8"));
				builder.append("&odometer=").append(
						URLEncoder.encode(Odo, "UTF-8"));
				builder.append("&any_accidents=").append(
						URLEncoder.encode(Accidnt, "UTF-8"));
				builder.append("&in_finance=").append(
						URLEncoder.encode(anyFinance, "UTF-8"));
				builder.append("&expected_price=").append(
						URLEncoder.encode(price, "UTF-8"));
				builder.append("&access_token=").append(preferences.getString("access_token",""));
				return builder.toString();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	// TODO Auto-generated method stub
	private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
		public UploadFileToServer(String Pictype) {
			// TODO Auto-generated constructor stub
			pictypeid = Pictype;

		}

		@Override
		protected void onPreExecute() {
			// setting progress bar to zero
			progressBar.setProgress(0);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// Making progress bar visible
			progressBar.setVisibility(View.VISIBLE);
			txtPercentage.setVisibility(View.VISIBLE);
			// updating progress bar value
			progressBar.setProgress(progress[0]);
			// updating percentage value
			txtPercentage.setText(String.valueOf(progress[0]) + "%");
		}

		@Override
		protected String doInBackground(Void... params) {
			return uploadFile();
		}

		@SuppressWarnings("deprecation")
		private String uploadFile() {
			String responseString = null;

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(TruckApp.FILE_UPLOAD_URL);

			try {
				AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
						new ProgressListener() {

							@Override
							public void transferred(long num) {
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

				File sourceFile = new File(selectedImagePath);

				// Adding file data to http body
				entity.addPart("image", new FileBody(sourceFile));
				// Extra parameters if you want to pass to server
				entity.addPart("field", new StringBody(pictypeid));
				entity.addPart("id_sell_truck", new StringBody(id_sellTruck));

				totalSize = entity.getContentLength();
				httppost.setEntity(entity);

				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					// Server response
					responseString = EntityUtils.toString(r_entity);
				} else {
					responseString = "Error occurred! Http Status Code: "
							+ statusCode;
				}

			} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}


			return responseString;

		}

		@Override
		protected void onPostExecute(String result) {
			Log.e(TAG, "Response from server: " + result);

			Toast.makeText(getApplicationContext(),
					"Pic success fully Uploaded", Toast.LENGTH_LONG).show();

			if(pictypeid.equals("truck_front_pic")){
				Picasso.with(context).load(R.drawable.uploadokicon).into(imagTruckFront);

			}else if(pictypeid.equals("truck_back_pic")){
				Picasso.with(context).load(R.drawable.uploadokicon).into(imgTruckBack);
			}
			else if(pictypeid.equals("tyres_front_left_pic")){
				Picasso.with(context).load(R.drawable.uploadokicon).into(imgTyrefrontLeftpic);
			}
			else if(pictypeid.equals("tyres_front_right_pic")){
				Picasso.with(context).load(R.drawable.uploadokicon).into(imgTyrefrontRightpic);
			}
			else if(pictypeid.equals("tyres_back_right_pic")){
				Picasso.with(context).load(R.drawable.uploadokicon).into(imgTyreBackRightpic);
			}
			else if(pictypeid.equals("tyres_back_left_pic")){
				Picasso.with(context).load(R.drawable.uploadokicon).into(imgTyreBackLeftpic);
			}
			else if(pictypeid.equals("other_pic_1")){
				Picasso.with(context).load(R.drawable.uploadokicon).into(Otherpic1);
			}
			else if(pictypeid.equals("other_pic_2")){
				Picasso.with(context).load(R.drawable.uploadokicon).into(Otherpic2);
			}

			uploadDocDialog.dismiss();
			super.onPostExecute(result);
		}

		/**
		 * Method to show alert dialog
		 * */
		// private void showAlert(String message) {
		// AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// builder.setMessage(message)
		// .setTitle("Response from Servers")
		// .setCancelable(false)
		// .setPositiveButton("OK",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog,
		// int id) {
		// // do nothing
		// Toast.makeText(getApplicationContext(),
		// "Pic success fully Uploaded", Toast.LENGTH_LONG)
		// .show();
		//
		// }
		// });
		// AlertDialog alert = builder.create();
		// alert.show();
		// //uploadDocDialog.dismiss();
		// }

	}

	// Date dailogs
	public void setCurDatetoDia(DatePickerDialog datepicker) {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// set current date into datepicker
		datepicker.updateDate(year, month, day);
	}

	/**
	 * This integer will uniquely define the dialog to be used for displaying
	 * date picker.
	 */
	static final int DATE_DIALOG_ID = 0;

	/** Callback received when the user "picks" a date in the dialog */
	private DatePickerDialog.OnDateSetListener pDateSetListenerIn = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
							  int dayOfMonth) {
			InsuranceExoDate.setText(TruckApp.createDate(year, monthOfYear + 1,
					dayOfMonth));
		}
	};

	private DatePickerDialog.OnDateSetListener pDateSetListenerFit = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
							  int dayOfMonth) {
			FitnessExpDate.setText(TruckApp.createDate(year, monthOfYear + 1,
					dayOfMonth));
		}
	};

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.b
		getMenuInflater().inflate(R.menu.locate_truck_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}*/

}
