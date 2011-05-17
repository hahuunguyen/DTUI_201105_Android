package com.group5.android.fd.helper;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.group5.android.fd.R;

abstract public class LoginRequestHelper extends HttpRequestAsyncTask {

	public LoginRequestHelper(Context context, String username, String password) {
		super(context);

		mode = HttpRequestAsyncTask.MODE_POST;
		mUri = UriStringHelper.buildUriString("login", "login");
		mCsrfToken = "";

		mParams = new ArrayList<NameValuePair>();
		mParams.add(new BasicNameValuePair("login", username));
		mParams.add(new BasicNameValuePair("password", password));
	}

	@Override
	protected String getProgressDialogMessage() {
		return mContext.getResources().getString(R.string.logging_in);
	}

	@Override
	protected Object preProcess(JSONObject jsonObject) {
		try {
			if (jsonObject.getString("_redirectStatus").equals("ok")) {
				return true;
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	@Override
	protected void process(JSONObject jsonObject, Object preProcessed) {
		boolean loggedIn = false;

		if (preProcessed instanceof Boolean) {
			loggedIn = (Boolean) preProcessed;
		}

		if (loggedIn) {
			onSuccess(jsonObject);
		} else {
			onError(jsonObject);
		}
	}

	abstract protected void onSuccess(JSONObject jsonObject);

	abstract protected void onError(JSONObject jsonObject);

}
