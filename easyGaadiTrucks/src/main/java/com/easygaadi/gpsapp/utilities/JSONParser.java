package com.easygaadi.gpsapp.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.Proxy;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.easygaadi.trucksmobileapp.LoginActivity;
import com.easygaadi.trucksmobileapp.R;

import static android.content.Context.MODE_PRIVATE;


public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	private static JSONParser instance = null;
	//private static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.3.100.207", 8080));
	// Set the timeout in milliseconds until a connection is established.
	// The default value is zero, that means the timeout is not used.
	//	int timeoutConnection = 1;

	// Set the default socket timeout (SO_TIMEOUT)
	// in milliseconds which is the timeout for waiting for data.
	//int timeoutSocket = 2;

	int TIMEOUT = 15000;

	private JSONParser() {

	}


	public static JSONParser getInstance() {
		if(instance == null) {
			instance = new JSONParser();
			System.out.println("JSON PARSER CREATED");
		}
		return instance;
	}




	public String executeGet(String targetURL){
		String result = null;
		URL url;
		HttpURLConnection urlConnection = null;
		for (int retries = 0; retries < 5; retries++) {
			try {
				int time = TIMEOUT * (retries+1);
				System.setProperty("http.keepAlive", "false");
				url = new URL(targetURL);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setRequestProperty("Connection", "close");
				urlConnection.setChunkedStreamingMode(0);
				urlConnection.setConnectTimeout(time);
				urlConnection.setReadTimeout(time);
				int responseCode = urlConnection.getResponseCode();
				System.out.println("RES CODE:"+responseCode);
				if(responseCode == HttpURLConnection.HTTP_OK){
					result = readStream(urlConnection.getInputStream());
					Log.v("JSONParser", result);
					return result;
				}else{
					Log.v("JSONParser", "Response code:"+ responseCode);
				}

			}catch(SocketTimeoutException e){
				System.out.println("SocketException :"+e.toString());
				continue;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(urlConnection != null)
					urlConnection.disconnect();
			}
		}
		return result;
	}

	private String readStream(InputStream in) {
		BufferedReader reader = null;
		StringBuffer response = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response.toString();
	} 
	
	public  String excutePut(String targetURL, String urlParameters)
	{
		URL url;
		HttpURLConnection connection = null;
		for (int retries = 0; retries < 5; retries++) {

			try {
				int time = TIMEOUT * (retries+1);
				//Create connection
				url = new URL(targetURL);
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Connection", "close");
				connection.setChunkedStreamingMode(0);
				connection.setConnectTimeout(time);
				connection.setReadTimeout(time);
				connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
				connection.setRequestProperty("Content-Language", "en-US");  

				connection.setUseCaches (false);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				//Send request
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
				wr.writeBytes (urlParameters);
				wr.flush ();
				wr.close ();

				//Get Response	
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				String line;
				StringBuffer response = new StringBuffer(); 
				while((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
				System.out.println(response.toString());
				return response.toString();

			}catch(SocketTimeoutException e){
				System.out.println("SocketException :"+e.toString());
				continue;
			} catch (Exception e) {
				System.out.println("Error in get"+e.toString());
				e.printStackTrace();
				return null;

			} finally {

				if(connection != null) {
					connection.disconnect(); 
				}
			}
		}
		return null;
	}
	
	public  String excutePost(String targetURL, String urlParameters)
	{
		URL url;
		HttpURLConnection connection = null;  
		for (int retries = 0; retries < 5; retries++) {

		try {
			int time = TIMEOUT * (retries+1);
			//Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Connection", "close");
			connection.setChunkedStreamingMode(0);
			connection.setConnectTimeout(time);
			connection.setReadTimeout(time);
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");  

			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();

			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			System.out.println("Buffer ..................."+ response.toString());
			return response.toString();

		} catch(SocketTimeoutException e){
			System.out.println("Socket exception "+e.toString());
			continue;
		}catch (Exception e) {
			System.out.println("Error in get"+e.toString());
			e.printStackTrace();
			return null;

		} finally {

			if(connection != null) {
				connection.disconnect(); 
			}
		}
		}
		return null;
	}


	public  String easyyExcutePost(Context context,String targetURL, String urlParameters)
	{
		String JsonResponse = null;
		String erpToken = "";
		URL url;
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		for (int retries = 0; retries < 5; retries++) {

			try {
				int time = TIMEOUT * (retries+1);
				//Create connection

				SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.shareP_erp), MODE_PRIVATE);
				String restoredText = prefs.getString("token", null);
				if (restoredText != null) {
					erpToken = prefs.getString("token", "No name defined");//"No name defined" is the default value.

				}
				 url = new URL(targetURL);
				urlConnection = (HttpURLConnection) url.openConnection();
				//urlConnection.setDoOutput(true);
				// is output buffer writter
				urlConnection.setRequestMethod("POST");
				urlConnection.setRequestProperty("Content-Type", "application/json");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestProperty("token", erpToken);
				urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
				urlConnection.setUseCaches (false);
				urlConnection.setDoInput(true);
				urlConnection.setDoOutput(true);
				//set headers and method
				Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
				writer.write(urlParameters);
				// json data
				writer.close();
				InputStream inputStream = urlConnection.getInputStream();
				//input stream
				StringBuffer buffer = new StringBuffer();
				if (inputStream == null) {
					// Nothing to do.
					return null;
				}
				reader = new BufferedReader(new InputStreamReader(inputStream));

				String inputLine;
				while ((inputLine = reader.readLine()) != null)
					buffer.append(inputLine + "\n");
				if (buffer.length() == 0) {
					// Stream was empty. No point in parsing.
					return null;
				}
				//send to post execute
				JsonResponse = buffer.toString();
				//response data
				Log.i("TAG",""+JsonResponse.toString());
				return JsonResponse;
			} catch(SocketTimeoutException e){
				System.out.println("Socket exception "+e.toString());
				continue;
			}catch (Exception e) {
				System.out.println("Error in get"+e.toString());
				e.printStackTrace();
				return null;

			} finally {

				if(urlConnection != null) {
					urlConnection.disconnect();
				}
			}
		}
		return null;
	}

	public String erpExecuteGet(Context context,String targetURL){
		String result = null;
		String erpToken = "";
		URL url;
		HttpURLConnection urlConnection = null;
		for (int retries = 0; retries < 5; retries++) {
			try {
				int time = TIMEOUT * (retries+1);
				SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.shareP_erp), MODE_PRIVATE);
				String restoredText = prefs.getString("token", null);
				if (restoredText != null) {
					erpToken = prefs.getString("token", "No name defined");//"No name defined" is the default value.
				}

				System.setProperty("http.keepAlive", "false");
				url = new URL(targetURL);
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setRequestProperty("Content-Type", "application/json");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestProperty("token", erpToken);
				/*urlConnection.setChunkedStreamingMode(0);
				urlConnection.setConnectTimeout(time);
				urlConnection.setReadTimeout(time);*/
				int responseCode = urlConnection.getResponseCode();
				System.out.println("RES CODE:"+responseCode);
				if(responseCode == HttpURLConnection.HTTP_OK){
					result = readStream(urlConnection.getInputStream());
					Log.v("JSONParser", result);
					return result;
				} else if(responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
					Toast.makeText(context, "Login Session expires.Please Login again", Toast.LENGTH_SHORT).show();
					context.startActivity(new Intent(context, LoginActivity.class));
				}else{
					Log.v("JSONParser", "Response code:"+ responseCode);
				}

			}catch(SocketTimeoutException e){
				System.out.println("SocketException :"+e.toString());
				continue;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(urlConnection != null)
					urlConnection.disconnect();
			}
		}
		return result;
	}


	public  String ERPexcutePut(Context context,String targetURL, String urlParameters)
	{
		String JsonResponse = null;
		String erpToken = "";
		URL url;
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		for (int retries = 0; retries < 5; retries++) {

			try {
				int time = TIMEOUT * (retries+1);

				SharedPreferences prefs = context.getSharedPreferences(context.getResources().getString(R.string.shareP_erp), MODE_PRIVATE);
				String restoredText = prefs.getString("token", null);
				if (restoredText != null) {
					erpToken = prefs.getString("token", "No name defined");//"No name defined" is the default value.
				}
				//Create connection
				url = new URL(targetURL);
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("PUT");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestProperty("token", erpToken);
				connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
				connection.setUseCaches (false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				//set headers and method
				Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
				writer.write(urlParameters);


				writer.close ();
				InputStream inputStream = connection.getInputStream();
				//input stream
				StringBuffer buffer = new StringBuffer();
				if (inputStream == null) {
					// Nothing to do.
					return null;
				}
				reader = new BufferedReader(new InputStreamReader(inputStream));

				String inputLine;
				while ((inputLine = reader.readLine()) != null)
					buffer.append(inputLine + "\n");
				if (buffer.length() == 0) {
					// Stream was empty. No point in parsing.
					return null;
				}
				//send to post execute
				JsonResponse = buffer.toString();
				//response data
				Log.i("TAG",""+JsonResponse.toString());
				return JsonResponse;

			}catch(SocketTimeoutException e){
				System.out.println("SocketException :"+e.toString());
				continue;
			} catch (Exception e) {
				System.out.println("Error in get"+e.toString());
				e.printStackTrace();
				return null;

			} finally {

				if(connection != null) {
					connection.disconnect();
				}
			}
		}
		return null;
	}
}


