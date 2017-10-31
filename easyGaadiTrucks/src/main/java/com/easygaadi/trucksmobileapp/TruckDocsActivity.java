package com.easygaadi.trucksmobileapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.JSONParser;

/**Code to load an imageview from serverurl
 *
 *  // Image url
 String image_url = "http://api.androidhive.info/images/sample.jpg";

 // ImageLoader class instance
 ImageLoader imgLoader = new ImageLoader(getApplicationContext());

 // whenever you want to load an image from url
 // call DisplayImage function
 // url - image url to load
 // loader - loader image, will be displayed before getting image
 // image - ImageView
 imgLoader.DisplayImage(image_url, loader, image);
 *
 * **/


public class TruckDocsActivity extends Activity {

	String id,truck_reg,id_customer;
	List<String> docTypesList,docTypesNames;
	JSONObject truckObj;
	ListView docsLV;
	Button  pick_btn,capture_btn,save_pic,upload_pic;
	ImageView picture;
	private final int PICTURE_TAKEN_FROM_CAMERA = 1;
	private final int PICTURE_TAKEN_FROM_GALLERY = 2;
	private String selectedImagePath = null;

	Dialog uploadDocDialog;
	ImageView closeDialog_iv;
	TextView  docTitle,imageName_tv;
	ProgressDialog pDialog;
	JSONParser parser;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_docs);
		docsLV          = (ListView)findViewById(R.id.docsLV);
		docTypesList    = Arrays.asList(getResources().getStringArray(R.array.truckdocs));
		docTypesNames   = Arrays.asList(getResources().getStringArray(R.array.truckdocs_names));

		context         = this;
		pDialog         = new ProgressDialog(this);
		parser          = JSONParser.getInstance();

		uploadDocDialog = new Dialog(TruckDocsActivity.this, android.R.style.Theme_Dialog);
		uploadDocDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		uploadDocDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		uploadDocDialog.setContentView(R.layout.docdialog);

		closeDialog_iv     = (ImageView) uploadDocDialog.findViewById(R.id.close_iv);
		pick_btn           = (Button)uploadDocDialog.findViewById(R.id.pick_btn);
		save_pic           = (Button)uploadDocDialog.findViewById(R.id.saveButton);
		upload_pic         = (Button)uploadDocDialog.findViewById(R.id.uploadButton);
		capture_btn        = (Button)uploadDocDialog.findViewById(R.id.capture_btn);
		picture            = (ImageView)uploadDocDialog.findViewById(R.id.img_preview);
		docTitle           = (TextView)uploadDocDialog.findViewById(R.id.doc_title);
		imageName_tv       = (TextView)uploadDocDialog.findViewById(R.id.imageName_tv);


		if(getIntent().hasExtra("json")){
			try {
				truckObj    = new JSONObject(getIntent().getStringExtra("json"));
				id          = truckObj.getString("id_truck");
				truck_reg   = truckObj.getString("truck_reg_no");
				id_customer = truckObj.getString("id_customer");
				TruckDocsActivity.this.setTitle((new StringBuilder().append(truckObj.getString("id_truck")).append("-").
						append(truckObj.getString("truck_reg_no")).append("/Docs")).toString());

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		docsLV.setAdapter(new TrucksAdapter(TruckDocsActivity.this, docTypesList));
		docsLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
					long arg3) {
				String value = docTypesList.get(postion);
				System.out.println("Selected value "+value);
				Toast.makeText(context, "Selected value "+value, Toast.LENGTH_SHORT).show();
				displayDocDialog(value, id ,id_customer);
			}
		});
	}

	public void displayDocDialog(final String value,final String id,final String id_customer){
		imageName_tv.setText("");
		picture.setImageDrawable(null);
		selectedImagePath=null;
		uploadDocDialog.show();
		docTitle.setText(value+"/"+id+"_"+truck_reg);
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
				try{
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_PICK);
					startActivityForResult(Intent.createChooser(intent, "Select Pic"), PICTURE_TAKEN_FROM_GALLERY);
				}catch (Exception e) {
					System.out.println("Exception when calling the gallery");
				}}
		});

		capture_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent,PICTURE_TAKEN_FROM_CAMERA);
				}catch (Exception e) {
					System.out.println("Exception when calling the gallery");
				}}
		});

		save_pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(selectedImagePath!=null){	
					try{
						saveImage(selectedImagePath,value,TruckDocsActivity.this.id,truckObj.getString("truck_reg_no"),id_customer);
					}catch (Exception e) {
						System.out.println("Exception when calling the gallery");
					}
				}else {
					Toast.makeText(context, "Please select image",
							Toast.LENGTH_SHORT).show();
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
			try{
				if (resultCode == RESULT_OK) {
					Uri selectedImageUri = data.getData();
					selectedImagePath = TruckApp.getRealPathFromURI(selectedImageUri,context);
					System.out.println("Image Path : " + selectedImagePath);
					if (selectedImagePath == null) {
						Toast.makeText(context, "Please select image",
								Toast.LENGTH_SHORT).show();
					} else {
						File file = new  File(selectedImagePath);						
						imageName_tv.setText("FileName:"+file.getName());
						file=null;
						picture.setImageBitmap(TruckApp.getThumbnail(selectedImageUri, context, 100));
					}
				}
			}catch(Exception e){
				selectedImagePath = null;
				Toast.makeText(context, "Please select another image ",Toast.LENGTH_SHORT).show();
			}
		}else if (requestCode == PICTURE_TAKEN_FROM_CAMERA){
			try{
				if (resultCode == RESULT_OK) {
					Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					thumbnail.compress(Bitmap.CompressFormat.JPEG, 100 , bytes);
					File destination = new File(Environment.getExternalStorageDirectory(),
							System.currentTimeMillis() + ".jpg");
					FileOutputStream fo;
					try {
						destination.createNewFile();
						fo = new FileOutputStream(destination);
						fo.write(bytes.toByteArray());
						fo.close();
						selectedImagePath = destination.getAbsolutePath();
						destination=null;
						System.out.println("File path:"+selectedImagePath);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (selectedImagePath == null) {
						Toast.makeText(context, "Please capture image properly",Toast.LENGTH_SHORT).show();
					} else {
						File file = new  File(selectedImagePath);						
						imageName_tv.setText("FileName:"+file.getName());
						file=null;
						picture.setImageBitmap(thumbnail);
					}
				}
			}catch(Exception e){
				selectedImagePath = null;
				Toast.makeText(context, "Please capture another image",Toast.LENGTH_SHORT).show();
			}
		}
	}


	public void saveImage(String srcPath,String doc_name,String truck_id,String truckReg,String id_customer){
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(context, getResources().getString(R.string.sdcard_error), Toast.LENGTH_LONG).show();
		} else {
			String dir = Environment.getExternalStorageDirectory()+File.separator+getResources().getString(R.string.app_name)
					+File.separator+"Trucks";

			/*//create folder for the above
			Eg:SDcard path/EasyGaadi/Trucks/truck-front_pic-90.jpeg
			 */
			File folder = new File(dir); //folder name
			folder.mkdirs();

			//create file
			File file = new File(dir, "truck-"+doc_name+"-"+truck_id+".jpeg");

			boolean status = TruckApp.copyFile(srcPath, file.getAbsolutePath());
			if(status){
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
					Intent mediaScanIntent = new Intent(
							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					Uri contentUri = Uri.fromFile(file); //out is your output file
					mediaScanIntent.setData(contentUri);
					this.sendBroadcast(mediaScanIntent);
				} else {
					sendBroadcast(new Intent(
							Intent.ACTION_MEDIA_MOUNTED,
							Uri.parse("file://"
									+ Environment.getExternalStorageDirectory())));
				}
				file=null;
				Toast.makeText(context, "Image saved", Toast.LENGTH_LONG).show();
				TruckApp.checkDialog(uploadDocDialog);
			}else{
				Toast.makeText(context, "Image not saved", Toast.LENGTH_LONG).show();
			}
		}
	}

	private class TrucksAdapter extends BaseAdapter{
		Activity activity;
		List<String> docsTypes;
		LayoutInflater inflater ;
		public class ViewHolder{
			private TextView doc_tv;
			private CheckBox doc_cb;
		}
		public TrucksAdapter(Activity act,List<String> docsTypes){
			this.activity = act;
			this.docsTypes=docsTypes;
			this.inflater  = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return docsTypes.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return docsTypes.get(position);
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewholder = null;
			if(convertView == null){
				viewholder               = new ViewHolder();
				convertView              = inflater.inflate(R.layout.doclistitem, parent, false); 
				viewholder.doc_tv  = (TextView)convertView.findViewById(R.id.docName);
				viewholder.doc_cb   = (CheckBox)convertView.findViewById(R.id.doc_cb);
				convertView.setTag(viewholder);			
			}else{
				viewholder = (ViewHolder)convertView.getTag();
			}
			viewholder.doc_tv.setText(docsTypes.get(position));
			String key= ((docsTypes.get(position)).toLowerCase(Locale.getDefault())).replace(" ", "_");
			try {
				if(truckObj!=null && truckObj.has(key) && (truckObj.getString(key)).trim().length()!=0){
					viewholder.doc_cb.setChecked(true);
				}else{
					viewholder.doc_cb.setChecked(false);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return convertView;
		}
	}

}
