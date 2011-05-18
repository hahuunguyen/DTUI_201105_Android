package com.group5.android.fd.helper;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.group5.android.fd.R;

abstract public class HttpRequestAsyncTask extends AsyncTask<Void, Void, JSONObject> {

	protected int mode = 0;
	protected Context m_context;
	protected String m_uri;
	protected String m_csrfToken;
	protected List<NameValuePair> m_params;
	protected ProgressDialog m_progressDialog;
	protected HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller m_caller = null;

	protected Object processed = null;
	protected String errorMessage = null;

	final public static int MODE_POST = 1;
	final public static int MODE_GET = 2;

	public HttpRequestAsyncTask(Context context) {
		m_context = context;
	}

	/**
	 * Constructor to initiate a GET request
	 * 
	 * @param context
	 * @param uri
	 */
	public HttpRequestAsyncTask(Context context, String uri) {
		mode = HttpRequestAsyncTask.MODE_GET;
		m_context = context;
		m_uri = uri;

		if (m_context instanceof HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller) {
			m_progressDialog = ProgressDialog.show(m_context, "",
					getProgressDialogMessage(), true, false);

			m_caller = (OnHttpRequestAsyncTaskCaller) m_context;
			m_caller.addHttpRequestAsyncTask(this);
		}
	}

	/**
	 * Constructor to initiate a POST request, a csrf token is required
	 * 
	 * @param context
	 * @param uri
	 * @param csrfToken
	 * @param params
	 */
	public HttpRequestAsyncTask(Context context, String uri, String csrfToken,
			List<NameValuePair> params) {
		mode = HttpRequestAsyncTask.MODE_POST;
		m_context = context;
		m_uri = uri;
		m_csrfToken = csrfToken;
		m_params = params;
	}

	@Override
	protected JSONObject doInBackground(Void... arg0) {
		JSONObject jsonObject = null;

		switch (mode) {
		case MODE_GET:
			jsonObject = HttpHelper.get(m_uri);
			break;
		case MODE_POST:
			jsonObject = HttpHelper.post(m_uri, m_csrfToken, m_params);
			break;
		}

		if (!lookForErrorMessages(jsonObject)) {
			processed = process(jsonObject);
		}

		return jsonObject;
	}

	@Override
	protected void onPostExecute(JSONObject jsonObject) {
		if (errorMessage == null) {
			onSuccess(jsonObject, processed);
		} else {
			onError(jsonObject, errorMessage);
		}

		dismissProgressDialog();

		if (m_caller != null) {
			m_caller.removeHttpRequestAsyncTask(this);
		}
	}

	protected String getProgressDialogMessage() {
		return m_context.getResources().getString(R.string.please_wait);
	}

	protected Object process(JSONObject jsonObject) {
		// subclass should implement this method to do lengthy stuff

		return null;
	}

	protected boolean lookForErrorMessages(JSONObject jsonObject) {
		try {
			JSONArray error = jsonObject.getJSONArray("error");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < error.length(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(error.getString(i));
			}

			errorMessage = sb.toString();
			return true;
		} catch (JSONException e) {
			// it's a good thing actually!
		}

		return false;
	}

	abstract protected void onSuccess(JSONObject jsonObject, Object preProcessed);

	protected void onError(JSONObject jsonObject, String message) {
		createErrorDialog(message).show();
	}

	public void dismissProgressDialog() {
		if (m_progressDialog != null) {
			// this will happen if the progress dialog is invoked
			// while another dialog is visible
			m_progressDialog.dismiss();
		}
	}

	protected AlertDialog createErrorDialog(String message) {
		AlertDialog.Builder adb = new AlertDialog.Builder(m_context);
		adb.setTitle(R.string.httprequestasynctask_error);
		adb.setMessage(message);

		return adb.create();
	}

	public interface OnHttpRequestAsyncTaskCaller {
		public void addHttpRequestAsyncTask(HttpRequestAsyncTask hrat);

		public void removeHttpRequestAsyncTask(HttpRequestAsyncTask hrat);
	}
}
