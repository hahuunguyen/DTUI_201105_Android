package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;
import com.group5.android.fd.helper.HttpHelper;
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
		setContentView(R.layout.dialog_login);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		setTitle(R.string.login);

		m_vwUsername = (EditText) findViewById(R.id.txtUsername);
		m_vwPassword = (EditText) findViewById(R.id.txtPassword);
		Button btn = (Button) findViewById(R.id.btnLogin);
		btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		new AsyncTask<Void, Void, JSONObject>() {

			@Override
			protected JSONObject doInBackground(Void... arg0) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("login", getUsername()));
				params.add(new BasicNameValuePair("password", getPassword()));

				JSONObject jsonObject = HttpHelper.post(LoginDialog.this
						.getContext(), UriStringHelper.buildUriString("login",
						"login"), "", params);
				return jsonObject;
			}

			@Override
			protected void onPostExecute(JSONObject jsonObject) {
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
		return m_vwUsername.getText().toString();
	}

	public String getPassword() {
		return m_vwPassword.getText().toString();
	}

	public boolean isLoggedIn() {
		return m_loggedIn;
	}
}
