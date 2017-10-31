package com.easygaadi.trucksmobileapp.insurance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.easygaadi.gpsapp.utilities.ConnectionDetector;
import com.easygaadi.gpsapp.utilities.JSONParser;
import com.easygaadi.trucksmobileapp.LoginActivity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.TruckApp;
import com.easygaadi.utils.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RequestQuote.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestQuote#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestQuote extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String selectedPolicyPath="";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final int PICTURE_TAKEN_FROM_GALLERY = 2;

    private EditText vehicle_no_et,idv_price_et,vehicle_age_et,ncb_per_et,grs_veh_wei_et;
    private Spinner itm_spinner,pa_spinner,nil_dep_spinner;
    private Button get_quote_btn,current_policy_btn,selectPolicy;
    private Context mContext;
    private ConnectionDetector detectCnnection;
    private SharedPreferences sharedPreferences;
    private ProgressDialog pDialog;
    private Bitmap bitmap;
    private ImageView imageView;
    /*private OnFragmentInteractionListener mListener;*/

    public RequestQuote() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *x
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestQuote.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestQuote newInstance(String param1, String param2) {
        RequestQuote fragment = new RequestQuote();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        sharedPreferences = mContext.getApplicationContext().getSharedPreferences(
                getResources().getString(R.string.app_name),mContext.MODE_PRIVATE);
        detectCnnection = new ConnectionDetector(mContext);
        pDialog  = new ProgressDialog(getActivity());
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_request_quote, container, false);
        vehicle_no_et = (EditText)fragmentView.findViewById(R.id.vehicle_no_et);
        idv_price_et = (EditText)fragmentView.findViewById(R.id.idv_price_et);
        vehicle_age_et = (EditText)fragmentView.findViewById(R.id.vehicle_age_et);
        ncb_per_et = (EditText)fragmentView.findViewById(R.id.ncb_per_et);
        grs_veh_wei_et = (EditText)fragmentView.findViewById(R.id.grs_veh_wei_et);
        itm_spinner = (Spinner)fragmentView.findViewById(R.id.itm_spinner);
        pa_spinner = (Spinner)fragmentView.findViewById(R.id.pa_spinner);
        nil_dep_spinner = (Spinner)fragmentView.findViewById(R.id.nil_dep_spinner);
        get_quote_btn = (Button) fragmentView.findViewById(R.id.get_quote_btn);
        current_policy_btn = (Button) fragmentView.findViewById(R.id.current_policy_btn);
        selectPolicy = (Button) fragmentView.findViewById(R.id.selectImage_btn);
        imageView = (ImageView) fragmentView.findViewById(R.id.current_policy_iv);
        get_quote_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(detectCnnection.isConnectingToInternet()){
                    createQuote();
                }else {
                    Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
                }
            }
        });
        selectPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        current_policy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(detectCnnection.isConnectingToInternet()){
                    if(selectedPolicyPath!=null && !selectedPolicyPath.isEmpty()){
                        new SetQuote(new HashMap<String, String>()).execute();
                    }else {
                        Toast.makeText(mContext,"Please select the current policy", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(mContext, getResources().getString(R.string.internet_str), Toast.LENGTH_LONG).show();
                }
            }
        });

        return fragmentView;
    }


    private void selectImage() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Pic"), PICTURE_TAKEN_FROM_GALLERY);
        } catch (Exception e) {
            System.out.println("Exception when calling the gallery");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Request Code:" + requestCode);
        System.out.println("Result Code:" + resultCode);
        if (requestCode == PICTURE_TAKEN_FROM_GALLERY) {
            String selectedImagePathStr = null;
            try {
                if (resultCode == getActivity().RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    selectedPolicyPath = TruckApp.getRealPathFromURI(selectedImageUri, mContext);
                    System.out.println("Image Path : " + selectedImagePathStr);
                    if (selectedPolicyPath == null) {
                        Toast.makeText(mContext, "Please select image",
                                Toast.LENGTH_SHORT).show();
                    } else {
                       /* selectedImageView.setImageUrl(selectedImagePathStr,
                                new ImageLoader(Volley.newRequestQueue(getApplicationContext()), new MyCache()));*/
                        imageView.setImageBitmap(TruckApp.getThumbnail(selectedImageUri, mContext, 100));
                    }
                }
            } catch (Exception e) {
                selectedImagePathStr = null;
                Toast.makeText(mContext, "Please select another image ", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void createQuote() {
        String vehicle_number,idv,age,ncb,imt,weight,pa_owner_driver,nil_dep;
        vehicle_number = vehicle_no_et.getText().toString().trim();
        idv = idv_price_et.getText().toString().trim();
        age = vehicle_age_et.getText().toString().trim();
        ncb = ncb_per_et.getText().toString().trim();
        imt = (itm_spinner.getSelectedItem().toString().trim().equalsIgnoreCase("Yes"))?"1":"0";
        weight = grs_veh_wei_et.getText().toString().trim();
        pa_owner_driver = (pa_spinner.getSelectedItem().toString().trim().equalsIgnoreCase("Yes"))?"1":"0";
        nil_dep = (nil_dep_spinner.getSelectedItem().toString().trim().equalsIgnoreCase("Yes"))?"1":"0";
        if(!vehicle_number.isEmpty() && !idv.isEmpty() && !age.isEmpty() && !ncb.isEmpty() && !weight.isEmpty()){
            /*HashMap<String,String> values = new HashMap<>();
            values.put("uid",sharedPreferences.getString("uid",""));
            values.put("accountid",sharedPreferences.getString("accountID",""));
            values.put("access_token",sharedPreferences.getString("access_token",""));
            values.put("vehicle_number",vehicle_number);
            values.put("idv",idv);
            values.put("age",age);
            values.put("ncb",ncb);
            values.put("imt",imt);
            values.put("weight",weight);
            values.put("pa_owner_driver",pa_owner_driver);
            values.put("nil_dep",nil_dep);
            */new CreateQuote(vehicle_number,idv,age,ncb,imt,weight,pa_owner_driver,nil_dep).execute();
        }else{
            TruckApp.editTextValidation(vehicle_no_et,vehicle_number,getString(R.string.vehicle_no_error));
            TruckApp.editTextValidation(idv_price_et,idv,getString(R.string.idv_no_error));
            TruckApp.editTextValidation(vehicle_age_et,age,getString(R.string.vehicle_age_error));
            TruckApp.editTextValidation(ncb_per_et,ncb,getString(R.string.ncb_error));
            TruckApp.editTextValidation(grs_veh_wei_et,weight,getString(R.string.grs_wei_error));
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
       /* mListener = null;*/
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private class CreateQuote extends AsyncTask<Void,Void,JSONObject>{

        private String vehicle_number,idv,age,ncb,imt,weight,pa_owner_driver,nil_dep;
        private JSONParser jsonParser;
        public CreateQuote(String vehicle_number, String idv, String age, String ncb, String imt, String weight, String pa_owner_driver, String nil_dep) {
            this.vehicle_number = vehicle_number;
            this.idv = idv;
            this.age = age;
            this.ncb = ncb;
            this.imt = imt;
            this.weight = weight;
            this.pa_owner_driver = pa_owner_driver;
            this.nil_dep = nil_dep;
            jsonParser = JSONParser.getInstance();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Creating quote...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject json = null;
            try {
                String stringRequest = setUrlParameters(vehicle_number,idv,age,ncb,imt,weight,pa_owner_driver,nil_dep);
                String res = jsonParser.excutePost(TruckApp.INSURANCE_QUOTE_URL,stringRequest);
                json = new JSONObject(res);
            }catch(Exception e){
                Log.e("Login DoIN EX",e.toString());
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            TruckApp.checkPDialog(pDialog);
            if(jsonObject!=null && jsonObject.has("status")){
                try {
                    if (jsonObject.getInt("status") == 0) {
                        Toast.makeText(mContext,"Failed to create a quote",Toast.LENGTH_LONG).show();
                    }else if (jsonObject.getInt("status") == 1) {
                        Toast.makeText(mContext,"Successfully created the quote,Will get back to you soon!!",Toast.LENGTH_LONG).show();
                    }else if (jsonObject.getInt("status") == 2) {
                        //TruckApp.logoutAction(getActivity());
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                getResources().getString(R.string.app_name), getActivity().MODE_PRIVATE);
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
                        startActivity(new Intent(getActivity(),
                                LoginActivity.class));
                        getActivity().finish();
                    }
                }catch (Exception e){
                    Toast.makeText(mContext,"Failed to create a quote",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(mContext,"Failed to create a quote",Toast.LENGTH_LONG).show();
            }
        }

        protected String setUrlParameters(String vehicle_number, String idv, String age, String ncb, String imt,
                                          String weight, String pa_owner_driver, String nil_dep){

            StringBuilder builder= new StringBuilder();
            try {
                builder.append("uid=").
                        append(URLEncoder.encode(sharedPreferences.getString("uid",""), "UTF-8"));
                builder.append("&access_token=").
                        append(URLEncoder.encode(sharedPreferences.getString("access_token",""), "UTF-8"));
                builder.append("&accountid=").
                        append(URLEncoder.encode(sharedPreferences.getString("accountID","no account id"), "UTF-8"));
                builder.append("&vehicle_number=").append(URLEncoder.encode(vehicle_number, "UTF-8"));
                builder.append("&idv=").append(URLEncoder.encode(idv, "UTF-8"));
                builder.append("&age=").append(URLEncoder.encode(age, "UTF-8"));
                builder.append("&ncb=").append(URLEncoder.encode(ncb, "UTF-8"));
                builder.append("&imt=").append(URLEncoder.encode(imt, "UTF-8"));
                builder.append("&weight=").append(URLEncoder.encode(weight, "UTF-8"));
                builder.append("&pa_owner_driver=").append(URLEncoder.encode(pa_owner_driver, "UTF-8"));
                builder.append("&nil_dep=").append(URLEncoder.encode(nil_dep, "UTF-8"));
                builder.append("&type=set");

                return builder.toString();
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }

    public class SetQuote extends AsyncTask<Void, Void, JSONObject> {

        HashMap<String, String> values;
        Set<String> keys;

        public SetQuote(HashMap<String, String> values) {
            this.values = values;
            keys = values.keySet();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Uploading the current policy...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject jsonResponse = null;
            String urlString = TruckApp.INSURANCE_QUOTE_URL;
            try {
                HttpEntity resEntity;
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(urlString);
                MultipartEntity reqEntity = new MultipartEntity();
                if (selectedPolicyPath != null && !selectedPolicyPath.isEmpty()) {
                    File fileRC = new File(selectedPolicyPath);
                    FileBody binRc = new FileBody(fileRC);
                    reqEntity.addPart("file", binRc);
                }
                reqEntity.addPart("type", new StringBody("set"));

                for (String key : keys) {
                    reqEntity.addPart(key, new StringBody(values.get(key)));
                }
                reqEntity.addPart("uid",new StringBody(sharedPreferences.getString("uid","")));
                reqEntity.addPart("accountid",new StringBody(sharedPreferences.getString("accountID","")));
                reqEntity.addPart("access_token",new StringBody(sharedPreferences.getString("access_token","")));

                //reqEntity.addPart("user", new StringBody("User"));
                post.setEntity(reqEntity);
                HttpResponse response = client.execute(post);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                final String response_str = convertStreamToString(is);
                if (is != null) {
                    jsonResponse = new JSONObject(response_str);
                }
            } catch (Exception ex) {
                Log.e("Debug", "error: " + ex.getMessage(), ex);
            }
            return jsonResponse;
        }

        private String convertStreamToString(InputStream is) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append((line + "\n"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            TruckApp.checkPDialog(pDialog);
            try {
                if (jsonObject != null) {
                    if (jsonObject.has("status") && jsonObject.getInt("status") == 1) {
                        //Toast.makeText(mContext, "Successfully created the  quote", Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext, "Successfully uploaded the current quote.Will get back to you soon!!", Toast.LENGTH_SHORT).show();
                    }else if (jsonObject.has("status") && jsonObject.getInt("status") == 2) {
                        //TruckApp.logoutAction(getActivity());
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                getResources().getString(R.string.app_name), getActivity().MODE_PRIVATE);
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
                        startActivity(new Intent(getActivity(),
                                LoginActivity.class));
                        getActivity().finish();
                    } else {
                        //Toast.makeText(mContext, "Failed to create the  quote", Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext, "Failed to upload the current quote", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Failed to upload the current quote", Toast.LENGTH_SHORT).show();                }
            } catch (Exception exe) {
                    Toast.makeText(mContext, "Failed to upload the current quote", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
