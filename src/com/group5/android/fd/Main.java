package com.group5.android.fd;

import java.io.Serializable;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.group5.android.fd.activity.FdPreferenceActivity;
import com.group5.android.fd.activity.NewSessionActivity;
import com.group5.android.fd.activity.TaskActivity;
import com.group5.android.fd.activity.dialog.LoginDialog;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.entity.UserEntity;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.LoginRequestHelper;
import com.group5.android.fd.helper.PreferencesHelper;
import com.group5.android.fd.helper.ScanHelper;
import com.group5.android.fd.helper.SyncHelper;
import com.group5.android.fd.helper.UriStringHelper;

public class Main extends Activity implements OnClickListener,
		OnDismissListener, OnCancelListener,
		HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller {
	final public static int DIALOG_LOGIN_ID = 1;

	protected Button m_vwNewSession;
	protected Button m_vwTasks;
	protected DbAdapter m_dbAdapter;
	protected HttpRequestAsyncTask m_hrat = null;

	protected UserEntity m_user = new UserEntity();
	protected boolean m_triedAutoLogin = false;
	protected boolean m_loginDialogLoggedIn = false;
	protected boolean m_loginDialogCanceled = false;

	final public static String INSTANCE_STATE_KEY_USER_OBJ = "userObj";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initLayout();
		initListeners();
	}

	@Override
	protected void onResume() {
		super.onResume();

		requireLoggedIn();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (m_hrat != null) {
			m_hrat.dismissProgressDialog();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(Main.INSTANCE_STATE_KEY_USER_OBJ, m_user);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		Serializable tmp = savedInstanceState
				.getSerializable(Main.INSTANCE_STATE_KEY_USER_OBJ);
		if (tmp != null && tmp instanceof UserEntity) {
			m_user = (UserEntity) tmp;
		}
	}

	protected void initLayout() {
		setContentView(R.layout.activity_session);
		m_vwNewSession = (Button) findViewById(R.id.btnNewSession);
		m_vwTasks = (Button) findViewById(R.id.btnTasks);
	}

	protected void initListeners() {
		m_vwNewSession.setOnClickListener(this);
		m_vwTasks.setOnClickListener(this);
	}

	protected void setLayoutEnabled(boolean enabled) {
		m_vwNewSession.setEnabled(enabled);
		m_vwTasks.setEnabled(enabled);
	}

	protected void sync() {
		new SyncHelper(this).execute();
	}

	protected boolean doAutoLogin() {
		if (m_triedAutoLogin) {
			// only try to auto login once
			return false;
		}
		m_triedAutoLogin = true;

		boolean prefAutoLogin = PreferencesHelper.getBoolean(this,
				R.string.pref_auto_login);
		String prefUsername = PreferencesHelper.getString(this,
				R.string.pref_username);
		String prefPassword = PreferencesHelper.getString(this,
				R.string.pref_password);

		if (prefAutoLogin) {
			// the auto login feature is enabled
			if (prefUsername != null && prefUsername.length() > 0
					&& prefPassword != null && prefPassword.length() > 0) {
				// valid username and password are found

				new LoginRequestHelper(this, prefUsername, prefPassword) {

					@Override
					protected void onLoginSuccess(JSONObject jsonObject) {
						requireLoggedIn();
					}

					@Override
					protected void onLoginError(JSONObject jsonObject) {
						requireLoggedIn();
					}
				}.execute();

				return true;
			}
		}

		return false;
	}

	protected void requireLoggedIn() {
		if (doAutoLogin()) {
			// wait for auto login...
			// it should call back soon
			return;
		}

		if (m_user.isLoggedIn()) {
			// the user is logged in, nothing to do here...
			return;
		}

		// temporary disable the buttons
		setLayoutEnabled(false);

		new HttpRequestAsyncTask(this, UriStringHelper
				.buildUriString("user-info")) {

			@Override
			protected void onSuccess(JSONObject jsonObject, Object processed) {
				try {
					JSONObject user = jsonObject.getJSONObject("user");
					m_user.parse(user);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (!m_user.isLoggedIn()) {
					// display the login dialog again!
					showLoginDialog();
				} else {
					// logged in
					Toast.makeText(
							Main.this,
							getResources().getString(R.string.hi) + " "
									+ m_user.username, Toast.LENGTH_SHORT)
							.show();

					// re-enable the buttons
					setLayoutEnabled(true);
				}
			}

			@Override
			protected void onError(JSONObject jsonObject, String message) {
				if (m_loginDialogLoggedIn) {
					// the login dialog reported that the user is logged in
					// but somehow we still got error message here
					// so... stop trigger the login dialog here
					super.onError(jsonObject, message);
				} else {
					showLoginDialog();
				}
			}
		}.execute();
	}

	protected void showLoginDialog() {
		// only if user hasn't canceled it before
		if (!m_loginDialogCanceled) {
			showDialog(Main.DIALOG_LOGIN_ID);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNewSession:
			Intent intentNewSession = new Intent(this, NewSessionActivity.class);
			intentNewSession.putExtra(Main.INSTANCE_STATE_KEY_USER_OBJ, m_user);
			startActivity(intentNewSession);
			break;
		case R.id.btnTasks:
			Intent intentTask = new Intent(this, TaskActivity.class);
			intentTask.putExtra(Main.INSTANCE_STATE_KEY_USER_OBJ, m_user);
			startActivity(intentTask);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem mniLogin = menu.findItem(R.id.menu_main_login);
		if (m_user.isLoggedIn()) {
			mniLogin.setEnabled(false);
			mniLogin.setTitle(getResources().getString(R.string.logged_in)
					+ ": " + m_user.username);
		} else {
			mniLogin.setEnabled(true);
			mniLogin.setTitle(R.string.login);
		}

		menu.findItem(R.id.menu_main_sync).setEnabled(m_user.isLoggedIn());
		menu.findItem(R.id.menu_main_scan).setEnabled(m_user.isLoggedIn());

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem arg0) {
		switch (arg0.getItemId()) {
		case R.id.menu_main_login:
			Log.i(FdConfig.DEBUG_TAG, "Login...");
			showDialog(Main.DIALOG_LOGIN_ID);
			break;
		case R.id.menu_main_sync:
			Log.i(FdConfig.DEBUG_TAG, "Sync...");
			sync();
			break;
		case R.id.menu_main_preferences:
			Intent preferencesIntent = new Intent(this,
					FdPreferenceActivity.class);
			startActivity(preferencesIntent);
			break;
		case R.id.menu_main_scan:
			IntentIntegrator.initiateScan(this);
			break;
		}

		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		switch (id) {
		case DIALOG_LOGIN_ID:
			dialog = new LoginDialog(this);
			dialog.setOnDismissListener(this);
			dialog.setOnCancelListener(this);
			break;
		}

		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (dialog instanceof LoginDialog) {
			LoginDialog loginDialog = (LoginDialog) dialog;

			if (loginDialog.isLoggedIn()) {
				// if the login dialog annouces logged in
				// we will do an additional check, just to make sure
				m_loginDialogLoggedIn = true;
				requireLoggedIn();
			} else {
				// it looks like the user canceled the login dialog
				// display a "friendly" reminder
				Toast.makeText(this, R.string.you_must_login_to_use_this_app,
						Toast.LENGTH_SHORT);
			}
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (dialog instanceof LoginDialog) {
			m_loginDialogCanceled = true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		new ScanHelper(this, requestCode, resultCode, data,
				new Class[] { TableEntity.class }) {

			@Override
			protected void onMatched(AbstractEntity entity) {
				TableEntity table = (TableEntity) entity;

				Intent newSessionIntent = new Intent(Main.this,
						NewSessionActivity.class);
				newSessionIntent.putExtra(
						NewSessionActivity.EXTRA_DATA_NAME_TABLE_OBJ, table);
				newSessionIntent.putExtra(Main.INSTANCE_STATE_KEY_USER_OBJ,
						m_user);
				newSessionIntent.putExtra(
						NewSessionActivity.EXTRA_DATA_NAME_USE_SCANNER, true);

				startActivity(newSessionIntent);
			}

			@Override
			protected void onInvalid() {
				// TODO Auto-generated method stub

			}
		};
	}

	@Override
	public void addHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat != null && m_hrat != hrat) {
			m_hrat.dismissProgressDialog();
		}

		m_hrat = hrat;
	}

	@Override
	public void removeHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat == hrat) {
			m_hrat = null;
		}
	}
}
