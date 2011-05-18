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
	protected Object process(JSONObject jsonObject) {
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
	protected void onSuccess(JSONObject jsonObject, Object processed) {
		boolean loggedIn = false;

		if (processed instanceof Boolean) {
			loggedIn = (Boolean) processed;
		}

		if (loggedIn) {
			onLoginSuccess(jsonObject);
		} else {
			onLoginError(jsonObject);
		}
	}

	@Override
	protected void onError(JSONObject jsonObject, String message) {
		onLoginError(jsonObject);
	}

	abstract protected void onLoginSuccess(JSONObject jsonObject);

	abstract protected void onLoginError(JSONObject jsonObject);

}
