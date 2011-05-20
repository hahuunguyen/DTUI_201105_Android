package com.group5.android.fd.helper;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

import com.group5.android.fd.R;

abstract public class LoginRequestHelper extends HttpRequestAsyncTask {

	public LoginRequestHelper(Context context, String username, String password) {
		super(context);

		mode = HttpRequestAsyncTask.MODE_POST;
		m_uri = UriStringHelper.buildUriString("login", "login");
		m_csrfToken = "";

		m_params = new ArrayList<NameValuePair>();
		m_params.add(new BasicNameValuePair("login", username));
		m_params.add(new BasicNameValuePair("password", password));
	}

	@Override
	protected String getProgressDialogMessage() {
		return m_context.getResources().getString(R.string.logging_in);
	}

	@Override
	protected Object process(JSONObject jsonObject) {
		try {
			if (jsonObject.getString("_redirectStatus").equals("ok")) {
				return true;
			}
		} catch (Exception e) {
			// many reason for an exception here
			// but nothing important, ignore it
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
