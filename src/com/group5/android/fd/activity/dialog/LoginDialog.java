package com.group5.android.fd.activity.dialog;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;

public class LoginDialog extends Dialog implements OnClickListener {
	protected EditText m_vwUsername;
	protected EditText m_vwPassword;
	protected boolean m_loggedIn = false;

	public LoginDialog(Context context) {
		super(context);

		initLayout();
	}

	protected void initLayout() {
		// setTitle(R.string.login);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_login);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		m_vwUsername = (EditText) findViewById(R.id.txtUsername);
		m_vwPassword = (EditText) findViewById(R.id.txtPassword);
		Button btn = (Button) findViewById(R.id.btnLogin);
		btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String username = getUsername();
		String password = getPassword();

		if (username.length() == 0) {
			Toast.makeText(getContext(),
					R.string.logindialog_please_enter_a_valid_username,
					Toast.LENGTH_SHORT).show();
			// focus the username
			m_vwUsername.requestFocus();
		}

		if (password.length() == 0) {
			Toast.makeText(getContext(),
					R.string.logindialog_please_enter_a_valid_password,
					Toast.LENGTH_SHORT).show();
			// focus the password
			m_vwPassword.requestFocus();
		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("login", username));
		params.add(new BasicNameValuePair("password", password));

		new HttpRequestAsyncTask(getContext(), UriStringHelper.buildUriString(
				"login", "login"), null, params) {

			@Override
			protected void process(JSONObject jsonObject) {
				try {
					if (jsonObject.getString("_redirectStatus").equals("ok")) {
						m_loggedIn = true;

						Log.i(FdConfig.DEBUG_TAG,
								"LoginDialog: WE ARE LOGGED IN!");
					}
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (m_loggedIn) {
					dismiss();
				} else {
					Toast.makeText(getOwnerActivity(),
							R.string.logindialog_login_failed,
							Toast.LENGTH_SHORT).show();

					// clear the password
					m_vwPassword.setText("");
					// focus the password
					m_vwPassword.requestFocus();
				}
			}
		}.execute();
	}

	public String getUsername() {
		return m_vwUsername.getText().toString().trim();
	}

	public String getPassword() {
		return m_vwPassword.getText().toString().trim();
	}

	public boolean isLoggedIn() {
		return m_loggedIn;
	}
}
