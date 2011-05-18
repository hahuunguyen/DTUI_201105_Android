package com.group5.android.fd;

import java.io.Serializable;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
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
		HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller,
		SyncHelper.SyncHelperCaller,
		android.content.DialogInterface.OnClickListener {
	final public static int DIALOG_LOGIN_ID = 1;

	protected Button m_vwNewSession;
	protected Button m_vwTasks;
	protected DbAdapter m_dbAdapter;
	protected UserEntity m_user = new UserEntity();

	protected boolean m_triedAutoLogin = false;
	protected Dialog m_loginDialog = null;
	protected boolean m_loginDialogLoggedIn = false;
	protected boolean m_loginDialogCanceled = false;
	protected Dialog m_zxingAlertDialog = null;
	protected Dialog m_syncDialog = null;

	protected HttpRequestAsyncTask m_hrat = null;
	protected SyncHelper m_sh = null;

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

		if (m_sh != null) {
			m_sh.dismissProgressDialog();
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

	protected void syncSuggestion() {
		if (SyncHelper.needSync(this)) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setPositiveButton(R.string.sync_data, this);
			b.setTitle(R.string.sync_data);
			b.setMessage(R.string.sync_data_now);

			m_syncDialog = b.show();
		}
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

					// suggest sync
					syncSuggestion();
				}
			}

			@Override
			protected void onError(JSONObject jsonObject, String message) {
				showLoginDialog();
			}
		}.execute();
	}

	protected void showLoginDialog() {
		// only if user hasn't canceled it before
		if (!m_loginDialogCanceled) {
			showDialog(Main.DIALOG_LOGIN_ID);
		}
	}

	protected void doLogout() {
		String logoutUri = UriStringHelper.buildUriString("logout", "index");

		new HttpRequestAsyncTask(this, logoutUri, m_user.csrfToken, null) {

			@Override
			protected void onSuccess(JSONObject jsonObject, Object preProcessed) {
				m_loginDialogCanceled = true;
				m_user.resetEverything();
				requireLoggedIn();
			}
		}.execute();
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
			mniLogin.setTitle(getResources().getString(R.string.logout) + ": "
					+ m_user.username);
		} else {
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
			if (!m_user.isLoggedIn()) {
				showDialog(Main.DIALOG_LOGIN_ID);
			} else {
				doLogout();
			}
			break;
		case R.id.menu_main_sync:
			sync();
			break;
		case R.id.menu_main_preferences:
			Intent preferencesIntent = new Intent(this,
					FdPreferenceActivity.class);
			startActivity(preferencesIntent);
			break;
		case R.id.menu_main_scan:
			m_zxingAlertDialog = IntentIntegrator.initiateScan(this);
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

			m_loginDialog = dialog;
			break;
		}

		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (dialog == m_loginDialog) {
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
		if (dialog == m_loginDialog) {
			m_loginDialogCanceled = true;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == m_syncDialog) {
			sync();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (m_zxingAlertDialog == null) {
			// only check for scan result if no alert dialog was issued the last
			// time we initiated it
			new ScanHelper(this, requestCode, resultCode, data,
					new Class[] { TableEntity.class }) {

				@Override
				protected void onMatched(AbstractEntity entity) {
					TableEntity table = (TableEntity) entity;

					Intent newSessionIntent = new Intent(Main.this,
							NewSessionActivity.class);
					newSessionIntent
							.putExtra(
									NewSessionActivity.EXTRA_DATA_NAME_TABLE_OBJ,
									table);
					newSessionIntent.putExtra(Main.INSTANCE_STATE_KEY_USER_OBJ,
							m_user);
					newSessionIntent.putExtra(
							NewSessionActivity.EXTRA_DATA_NAME_USE_SCANNER,
							true);

					startActivity(newSessionIntent);
				}

				@Override
				protected void onInvalid() {
					// TODO Auto-generated method stub

				}
			};
		}
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

	@Override
	public void addSyncHelper(SyncHelper sh) {
		if (m_sh != null && m_sh != sh) {
			m_sh.dismissProgressDialog();
		}

		m_sh = sh;
	}

	@Override
	public void removeSyncHelper(SyncHelper sh) {
		if (m_sh == sh) {
			m_sh = null;
		}
	}
}
