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

	final public static int MODE_POST = 1;
	final public static int MODE_GET = 2;

	public HttpRequestAsyncTask(Context context, String uri) {
		mode = HttpRequestAsyncTask.MODE_GET;
		mContext = context;
		mUri = uri;

		m_progressDialog = ProgressDialog.show(mContext, "", mContext
				.getResources().getString(R.string.please_wait), true, false);
	}

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
		switch (mode) {
		case MODE_GET:
			return HttpHelper.get(mContext, mUri);
		case MODE_POST:
			return HttpHelper.post(mContext, mUri, mCsrfToken, mParams);
		}

		return null;
	}

	@Override
	protected void onPostExecute(JSONObject jsonObject) {
		process(jsonObject);

		m_progressDialog.dismiss();
	}

	abstract protected void process(JSONObject jsonObject);
}
