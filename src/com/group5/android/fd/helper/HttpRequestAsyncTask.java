package com.group5.android.fd.helper;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.group5.android.fd.R;

abstract public class HttpRequestAsyncTask extends
		AsyncTask<Void, Void, JSONObject> {

	protected int mode;
	protected Context mContext;
	protected String mUri;
	protected String mCsrfToken;
	protected List<NameValuePair> mParams;
	protected ProgressDialog m_progressDialog;

	protected Object preProcessed = null;

	final public static int MODE_POST = 1;
	final public static int MODE_GET = 2;

	/**
	 * Constructor to initiate a GET request
	 * 
	 * @param context
	 * @param uri
	 */
	public HttpRequestAsyncTask(Context context, String uri) {
		mode = HttpRequestAsyncTask.MODE_GET;
		mContext = context;
		mUri = uri;

		m_progressDialog = ProgressDialog.show(mContext, "", mContext
				.getResources().getString(R.string.please_wait), true, false);
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
		mContext = context;
		mUri = uri;
		mCsrfToken = csrfToken;
		mParams = params;
	}

	@Override
	protected JSONObject doInBackground(Void... arg0) {
		JSONObject jsonObject = null;

		switch (mode) {
		case MODE_GET:
			jsonObject = HttpHelper.get(mContext, mUri);
			break;
		case MODE_POST:
			jsonObject = HttpHelper.post(mContext, mUri, mCsrfToken, mParams);
			break;
		}

		preProcessed = preProcess(jsonObject);

		return jsonObject;
	}

	@Override
	protected void onPostExecute(JSONObject jsonObject) {
		process(jsonObject, preProcessed);

		if (m_progressDialog != null) {
			// this will happen if the progress dialog is invoked
			// while another dialog is visible
			m_progressDialog.dismiss();
		}
	}

	protected Object preProcess(JSONObject jsonObject) {
		// subclass should implement this method to do lengthy stuff

		return null;
	}

	abstract protected void process(JSONObject jsonObject, Object preProcessed);
}
