package com.group5.android.fd.activity.dialog;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.group5.android.fd.R;
import com.group5.android.fd.helper.LoginRequestHelper;

public class LoginDialog extends Dialog implements OnClickListener,
		OnShowListener {
	protected EditText m_vwUsername;
	protected EditText m_vwPassword;
	protected Button m_vwLogin;

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
		m_vwLogin = (Button) findViewById(R.id.btnLogin);
		m_vwLogin.setOnClickListener(this);

		setOnShowListener(this);
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

	protected void setViewWidgetsState(boolean enabled) {
		// m_vwUsername.setEnabled(enabled);
		// m_vwPassword.setEnabled(enabled);
		m_vwLogin.setEnabled(enabled);

		m_vwLogin.setText(enabled ? R.string.login : R.string.logging_in);
	}

	protected void doLogin() {
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

		// everything seems to be fine, disable the widgets
		setViewWidgetsState(false);

		new LoginRequestHelper(getContext(), username, password) {

			@Override
			protected void onError(JSONObject jsonObject) {
				Toast.makeText(getOwnerActivity(),
						R.string.logindialog_login_failed, Toast.LENGTH_SHORT)
						.show();

				// clear the password
				m_vwPassword.setText("");
				// focus the password
				m_vwPassword.requestFocus();
				// reenable the widgets
				setViewWidgetsState(true);
			}

			@Override
			protected void onSuccess(JSONObject jsonObject) {
				m_loggedIn = true;

				dismiss();
			}

		}.execute();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnLogin) {
			doLogin();
		}
	}

	@Override
	public void onShow(DialogInterface dialog) {
		if (dialog == this) {
			setViewWidgetsState(true);
		}
	}
}
