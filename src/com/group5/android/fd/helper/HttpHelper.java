package com.group5.android.fd.helper;

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
import android.util.Log;

import com.group5.android.fd.FdConfig;

public class HttpHelper {
	public static HashMap<String, HttpContext> contexts = new HashMap<String, HttpContext>();

	public static JSONObject get(Context context, String strUri) {
		AndroidHttpClient httpClient = null;
		JSONObject jsonResponse = null;

		try {
			Log.i(FdConfig.DEBUG_TAG, "HttpHelper.get(): " + strUri);

			httpClient = AndroidHttpClient
					.newInstance(FdConfig.HTTP_REQUEST_USER_AGENT);
			URI uri = new URI(strUri);
			HttpContext httpContext = HttpHelper.getContext(uri);
			HttpGet request = new HttpGet(uri);

			// execute the request now!
			HttpResponse response = httpClient.execute(request, httpContext);
			HttpEntity httpEntity = response.getEntity();
			InputStream inputStream = httpEntity.getContent();
			String string = HttpHelper.streamToString(inputStream);
			httpClient.close();

			Log.i(FdConfig.DEBUG_TAG, "HttpHelper.get(): " + string);

			jsonResponse = new JSONObject(string);
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

		if (httpClient != null) {
			// we want to make sure the client is closed properly
			httpClient.close();
		}

		return jsonResponse;
	}

	public static JSONObject post(Context context, String strUri,
			String csrfToken, List<NameValuePair> params) {
		AndroidHttpClient httpClient = null;
		JSONObject jsonResponse = null;

		try {
			Log.i(FdConfig.DEBUG_TAG, "HttpHelper.post(): " + strUri);

			httpClient = AndroidHttpClient
					.newInstance(FdConfig.HTTP_REQUEST_USER_AGENT);
			URI uri = new URI(strUri);
			HttpContext httpContext = HttpHelper.getContext(uri);
			HttpPost request = new HttpPost(uri);
			if (params == null) {
				// create new name value list if null is supplied
				params = new ArrayList<NameValuePair>();
			}
			if (csrfToken != null) {
				// set the csrf token
				// this is required for all POST requests
				// with the exception of login request, of course
				params.add(new BasicNameValuePair("_xfToken", csrfToken));
			}
			request.setEntity(new UrlEncodedFormEntity(params));

			// execute the request now!
			HttpResponse response = httpClient.execute(request, httpContext);
			HttpEntity httpEntity = response.getEntity();
			InputStream inputStream = httpEntity.getContent();
			String string = HttpHelper.streamToString(inputStream);
			httpClient.close();

			Log.i(FdConfig.DEBUG_TAG, "HttpHelper.post(): " + string);

			jsonResponse = new JSONObject(string);
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

		if (httpClient != null) {
			httpClient.close();
		}

		return jsonResponse;
	}

	/**
	 * Create or reuse HttpContext for each host. Also enables the context to
	 * store cookies between requests
	 * 
	 * @param uri
	 * @return an instance of HttpContext
	 */
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

	/**
	 * Simple way to turn an input stream to a string
	 * 
	 * @param is
	 *            the input stream
	 * @return the string
	 */
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
