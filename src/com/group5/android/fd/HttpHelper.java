package com.group5.android.fd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.telephony.TelephonyManager;
import android.util.Log;

public class HttpHelper {
	final static String USER_AGENT = "XenForo Reader for Android Devices";
	final static String VERSION = "20110406";
	final static String TAG = "xfreader";

	static HashMap<String, HttpContext> contexts = new HashMap<String, HttpContext>();

	public static JSONObject get(Context context, String strUri) {
		try {
			Log.i(HttpHelper.TAG, "HttpHelper.get(): " + strUri);

			AndroidHttpClient httpClient = AndroidHttpClient
					.newInstance(HttpHelper.USER_AGENT);
			
			strUri = URIHelper.addParam(strUri, "_xfReaderVersion",
					HttpHelper.VERSION);
			URI uri = new URI(strUri);
			HttpContext httpContext = HttpHelper.getContext(uri);
			HttpGet request = new HttpGet(uri);
			HttpResponse response = httpClient.execute(request, httpContext);
			HttpEntity httpEntity = response.getEntity();
			InputStream inputStream = httpEntity.getContent();
			String string = HttpHelper.streamToString(inputStream);
			httpClient.close();
			Log.v("dtui", string);
			return new JSONObject(string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static JSONObject post(Context context, String strUri,
			String csrfToken, List<NameValuePair> params) {
		try {
			Log.i(HttpHelper.TAG, "HttpHelper.post(): " + strUri);

			AndroidHttpClient httpClient = AndroidHttpClient
					.newInstance(HttpHelper.USER_AGENT);
			URI uri = new URI(strUri);
			HttpContext httpContext = HttpHelper.getContext(uri);
			HttpPost request = new HttpPost(uri);
			if (params == null) {
				// sometime generate the list here instead of
				// using the passed in
				params = new ArrayList<NameValuePair>();
			}
			params.add(new BasicNameValuePair("_xfToken", csrfToken));
			
			params.add(new BasicNameValuePair("_xfReaderVersion",
					HttpHelper.VERSION));
			request.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse response = httpClient.execute(request, httpContext);
			HttpEntity httpEntity = response.getEntity();
			InputStream inputStream = httpEntity.getContent();
			String string = HttpHelper.streamToString(inputStream);
			httpClient.close();

			return new JSONObject(string);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	protected static HttpContext getContext(URI uri) {
		String host = uri.getHost();

		if (HttpHelper.contexts.containsKey(host)) {
			return HttpHelper.contexts.get(host);
		} else {
			HttpContext context = new BasicHttpContext();
			CookieStore cookieStore = new BasicCookieStore();
			context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			HttpHelper.contexts.put(host, context);
			return context;
		}
	}

	protected static String streamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
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

	
	
}
