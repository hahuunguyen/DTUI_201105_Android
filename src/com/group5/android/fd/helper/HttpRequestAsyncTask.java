package com.group5.android.fd.helper;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;

import com.group5.android.fd.R;

abstract public class HttpRequestAsyncTask extends AsyncTask<Void, Void, JSONObject>
		implements OnDismissListener {

	protected int mode = 0;
	protected Context m_context;
	protected String m_uri;
	protected String m_csrfToken;
	protected List<NameValuePair> m_params;
	protected ProgressDialog m_progressDialog;
	protected HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller m_caller = null;

	protected Object m_processed = null;
	protected String m_errorMessage = null;
	protected Dialog m_errorDialog = null;

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
		publishProgress(); // trigger our progress dialog

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
			m_processed = process(jsonObject);
		}

		return jsonObject;
	}

	@Override
	protected void onProgressUpdate(Void... arg0) {
		if (m_context != null
				&& m_context instanceof HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller) {
			m_progressDialog = ProgressDialog.show(m_context, "",
					getProgressDialogMessage(), true, false);

			m_caller = (OnHttpRequestAsyncTaskCaller) m_context;
			m_caller.addHttpRequestAsyncTask(this);
		}
	}

	@Override
	protected void onPostExecute(JSONObject jsonObject) {
		if (m_errorMessage == null) {
			onSuccess(jsonObject, m_processed);
		} else {
			onError(jsonObject, m_errorMessage);
		}

		dismissProgressDialog();

		if (m_caller != null) {
			m_caller.removeHttpRequestAsyncTask(this);
		}
	}

	protected String getProgressDialogMessage() {
		if (m_context != null) {
			return m_context.getString(R.string.please_wait);
		} else {
			return "";
		}
	}

	protected Object process(JSONObject jsonObject) {
		// subclass should implement this method to do lengthy stuff

		return null;
	}

	protected boolean lookForErrorMessages(JSONObject jsonObject) {
		m_errorMessage = HttpHelper.lookForErrorMessages(jsonObject, m_context);

		return m_errorMessage != null;
	}

	abstract protected void onSuccess(JSONObject jsonObject, Object preProcessed);

	protected void onError(JSONObject jsonObject, String message) {
		if (m_context != null) {
			createErrorDialog(message).show();
		}
	}

	public void dismissProgressDialog() {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}
	}

	protected Dialog createErrorDialog(String message) {
		AlertDialog.Builder adb = new AlertDialog.Builder(m_context);
		adb.setTitle(R.string.httprequestasynctask_error);

		if (message != null) {
			adb.setMessage(message);
		} else {
			adb.setMessage(R.string.httprequestasynctask_unknown_error);
		}

		m_errorDialog = adb.create();
		m_errorDialog.setOnDismissListener(this);

		return m_errorDialog;
	}

	public interface OnHttpRequestAsyncTaskCaller {
		public void addHttpRequestAsyncTask(HttpRequestAsyncTask hrat);

		public void removeHttpRequestAsyncTask(HttpRequestAsyncTask hrat);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// subclass should implement this method if action should be taken when
		// action is required when the dialog is dismissed
	}
}
