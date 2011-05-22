package com.group5.android.fd.activity.dialog;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.group5.android.fd.R;
import com.group5.android.fd.helper.LoginRequestHelper;
import com.group5.android.fd.helper.PreferencesHelper;

/**
 * Dialog which allow user to login
 * 
 * @author Dao Hoang Son
 * 
 */
public class LoginDialog extends Dialog implements OnClickListener,
		OnShowListener, OnEditorActionListener {
	protected EditText m_vwUsername;
	protected EditText m_vwPassword;
	protected CheckBox m_vwRemember;
	protected Button m_vwLogin;

	protected boolean m_loggedIn = false;

	public LoginDialog(Context context) {
		super(context);

		initLayout();
		initAutoLogin();
	}

	/**
	 * Initiates the layout (inflate from a layout resource named
	 * activity_main). And then maps all the object properties with their view
	 * instance. Finally, initiates required listeners on those views.
	 */
	protected void initLayout() {
		// setTitle(R.string.login);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_login);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		m_vwUsername = (EditText) findViewById(R.id.txtUsername);
		m_vwUsername.setImeOptions(EditorInfo.IME_ACTION_DONE);
		m_vwUsername.setSelectAllOnFocus(true);
		m_vwPassword = (EditText) findViewById(R.id.txtPassword);
		m_vwPassword.setImeOptions(EditorInfo.IME_ACTION_DONE);
		m_vwPassword.setSelectAllOnFocus(true);
		m_vwRemember = (CheckBox) findViewById(R.id.chkRemember);
		m_vwLogin = (Button) findViewById(R.id.btnLogin);

		m_vwUsername.setOnEditorActionListener(this);
		m_vwPassword.setOnEditorActionListener(this);
		m_vwLogin.setOnClickListener(this);

		setOnShowListener(this);
	}

	/**
	 * Checks for auto login preferences and setup views
	 */
	protected void initAutoLogin() {
		boolean prefAutoLogin = PreferencesHelper.getBoolean(getContext(),
				R.string.pref_auto_login);
		String prefUsername = PreferencesHelper.getString(getContext(),
				R.string.pref_username);
		String prefPassword = PreferencesHelper.getString(getContext(),
				R.string.pref_password);

		if (prefAutoLogin && prefUsername != null && prefUsername.length() > 0
				&& prefPassword != null && prefPassword.length() > 0) {
			m_vwUsername.setText(prefUsername);
			m_vwPassword.setText(prefPassword);
			m_vwRemember.setChecked(true);
		}
	}

	/**
	 * Saves auto login informatio to preferences if the remember checkbox is
	 * checked
	 */
	protected void saveAutoLogin() {
		if (m_vwRemember.isChecked()) {
			PreferencesHelper.putBoolean(getContext(),
					R.string.pref_auto_login, true);
			PreferencesHelper.putString(getContext(), R.string.pref_username,
					getUsername());
			PreferencesHelper.putString(getContext(), R.string.pref_password,
					getPassword());
		} else {
			PreferencesHelper.putBoolean(getContext(),
					R.string.pref_auto_login, false);
		}
	}

	/**
	 * Gets the entered username
	 * 
	 * @return
	 */
	public String getUsername() {
		return m_vwUsername.getText().toString().trim();
	}

	/**
	 * Gets the entered password
	 * 
	 * @return
	 */
	public String getPassword() {
		return m_vwPassword.getText().toString().trim();
	}

	/**
	 * Checks if the login request has been made and accepted
	 * 
	 * @return true if logged in
	 */
	public boolean isLoggedIn() {
		return m_loggedIn;
	}

	/**
	 * Sets the views enabled states
	 * 
	 * @param enabled
	 */
	protected void setViewWidgetsState(boolean enabled) {
		// m_vwUsername.setEnabled(enabled);
		// m_vwPassword.setEnabled(enabled);
		m_vwLogin.setEnabled(enabled);

		m_vwLogin.setText(enabled ? R.string.login : R.string.logging_in);
	}

	/**
	 * Sends the login request using {@link LoginRequestHelper}
	 */
	protected void doLogin() {
		String username = getUsername();
		String password = getPassword();

		if (username.length() == 0) {
			Toast.makeText(getContext(),
					R.string.logindialog_please_enter_a_valid_username,
					Toast.LENGTH_SHORT).show();
			// focus the username
			m_vwUsername.requestFocus();

			return;
		}

		if (password.length() == 0) {
			Toast.makeText(getContext(),
					R.string.logindialog_please_enter_a_valid_password,
					Toast.LENGTH_SHORT).show();
			// focus the password
			m_vwPassword.requestFocus();

			return;
		}

		// everything seems to be fine, disable the widgets
		setViewWidgetsState(false);

		new LoginRequestHelper(getContext(), username, password) {

			@Override
			protected void onLoginError(JSONObject jsonObject) {
				Toast.makeText(getOwnerActivity(),
						R.string.logindialog_login_failed, Toast.LENGTH_SHORT)
						.show();

				// clear the password
				m_vwPassword.setText("");
				// focus the password
				m_vwPassword.requestFocus();
				// re-enable the widgets
				setViewWidgetsState(true);
			}

			@Override
			protected void onLoginSuccess(JSONObject jsonObject) {
				m_loggedIn = true;

				saveAutoLogin();

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

			if (getUsername().length() > 0) {
				m_vwPassword.requestFocus();
			}
		}
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		if (arg1 == EditorInfo.IME_ACTION_DONE
				|| arg2.getAction() == KeyEvent.ACTION_DOWN
				&& (arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER || arg2
						.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER)) {
			doLogin();
		}

		return false;
	}
}
