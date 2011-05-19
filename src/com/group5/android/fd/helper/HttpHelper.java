package com.group5.android.fd.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;

public class HttpHelper {

	final public static String ERROR_MESSAGE_CONNECT_TIMEOUT = "Oops, connection timed out!";

	public static HashMap<String, HttpContext> contexts = new HashMap<String, HttpContext>();

	/**
	 * Sends a HTTP GET request to the target uri and parses the response as an
	 * <code>JSONObject</code>
	 * 
	 * @param strUri
	 *            the target uri
	 * @return an <code>JSONObject</code>
	 */
	public static JSONObject get(String strUri) {
		JSONObject jsonResponse = null;

		Log.i(FdConfig.DEBUG_TAG, "HttpHelper.get(): " + strUri);

		try {
			URI uri = new URI(strUri);
			HttpGet request = new HttpGet(uri);

			jsonResponse = HttpHelper.execute(uri, request);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonResponse;
	}

	/**
	 * Sends a HTTP POST request and parses the response as an
	 * <code>JSONObject</code>. It's required that all POST request must include
	 * a CSRF token so this method is designed to receive the token as a
	 * parameter for easy access. However, you can pass NULL to params if you
	 * want
	 * 
	 * @param strUri
	 *            the target uri
	 * @param csrfToken
	 *            the required CSRF token
	 * @param params
	 *            a list of POST parameters
	 * @return an <code>JSONObject</code>
	 */
	public static JSONObject post(String strUri, String csrfToken,
			List<NameValuePair> params) {
		JSONObject jsonResponse = null;

		Log.i(FdConfig.DEBUG_TAG, "HttpHelper.post(): " + strUri);

		try {
			URI uri = new URI(strUri);
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

			for (int pi = 0; pi < params.size(); pi++) {
				Log.d(FdConfig.DEBUG_TAG, "HttpHelper.post(): "
						+ params.get(pi).getName() + " = "
						+ params.get(pi).getValue());
				// this debug loop is very ineffective
				// we should remove it entirely at somepoint
				// TODO: remove this debug loop
			}

			jsonResponse = HttpHelper.execute(uri, request);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonResponse;
	}

	protected static JSONObject execute(URI uri, HttpUriRequest request) {
		JSONObject jsonResponse = null;
		AndroidHttpClient httpClient = AndroidHttpClient
				.newInstance(FdConfig.HTTP_REQUEST_USER_AGENT);

		try {
			HttpContext httpContext = HttpHelper.getContext(uri);

			HttpResponse response = httpClient.execute(request, httpContext);
			HttpEntity httpEntity = response.getEntity();
			InputStream inputStream = httpEntity.getContent();
			String string = HttpHelper.streamToString(inputStream);

			Log.d(FdConfig.DEBUG_TAG, "HttpHelper.execute(): " + string);

			jsonResponse = new JSONObject(string);
		} catch (ConnectTimeoutException e) {
			jsonResponse = new JSONObject();
			try {
				JSONArray error = new JSONArray();
				error.put(HttpHelper.ERROR_MESSAGE_CONNECT_TIMEOUT);
				jsonResponse.put("error", error);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Log.d(FdConfig.DEBUG_TAG, "HttpHelper.execute(): Timeout");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		httpClient.close();

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

	public static String lookForErrorMessages(JSONObject jsonObject,
			Context context) {
		String errorMessage = null;

		try {
			if (jsonObject != null) {
				Object error = jsonObject.get("error");

				if (error instanceof JSONArray) {
					JSONArray errorArray = (JSONArray) error;
					StringBuilder sb = new StringBuilder();

					for (int i = 0; i < errorArray.length(); i++) {
						if (i > 0) {
							sb.append(", ");
						}
						sb.append(errorArray.getString(i));
					}

					errorMessage = sb.toString();
				} else {
					// if error is not a String, an exception will be thrown
					// and we will catch it anyway
					errorMessage = (String) error;
				}
			} else {
				errorMessage = context.getResources().getString(
						R.string.httphelper_invalid_response_from_server);
			}
		} catch (JSONException e) {
			// it's a good thing actually!
		} catch (Exception e) {
			errorMessage = e.getMessage();
		}

		if (errorMessage != null) {
			Log.d(FdConfig.DEBUG_TAG, "HttpHelper.lookForErrorMessages(): "
					+ errorMessage);
		}

		return errorMessage;
	}
}
