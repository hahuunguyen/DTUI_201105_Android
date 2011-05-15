package com.group5.android.fd;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.group5.android.fd.activity.LoginDialog;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.SyncHelper;
import com.group5.android.fd.helper.UriStringHelper;

public class Main extends Activity implements OnClickListener,
		OnDismissListener {
	final public static int DIALOG_LOGIN_ID = 1;

	protected Button m_vwNewSession;
	protected Button m_vwTasks;
	protected DbAdapter m_dbAdapter;

	protected int m_userId = 0;
	protected String m_username = null;
	protected String m_csrfTokenPage = null;

	final public static String INSTANCE_STATE_KEY_USER_ID = "userId";
	final public static String INSTANCE_STATE_KEY_USERNAME = "username";
	final public static String INSTANCE_STATE_KEY_CSRF_TOKEN_PAGE = "csrfTokenPage";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initLayout();
		initListeners();
	}

	@Override
	public void onResume() {
		super.onResume();

		requireLoggedIn();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(Main.INSTANCE_STATE_KEY_USER_ID, m_userId);
		outState.putString(Main.INSTANCE_STATE_KEY_USERNAME, m_username);
		outState.putString(Main.INSTANCE_STATE_KEY_CSRF_TOKEN_PAGE,
				m_csrfTokenPage);

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		m_userId = savedInstanceState.getInt(Main.INSTANCE_STATE_KEY_USER_ID);
		m_username = savedInstanceState
				.getString(Main.INSTANCE_STATE_KEY_USERNAME);
		m_csrfTokenPage = savedInstanceState
				.getString(Main.INSTANCE_STATE_KEY_CSRF_TOKEN_PAGE);
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

	protected void sync() {
		new SyncHelper(this).execute();
	}

	protected void requireLoggedIn() {
		if (m_userId > 0) {
			// the user is logged in, nothing to do here...
			return;
		}

		// reset the flag and user info
		m_userId = 0;
		m_username = null;
		m_csrfTokenPage = null;
		// temporary disable the buttons
		m_vwNewSession.setEnabled(false);
		m_vwTasks.setEnabled(false);

		new HttpRequestAsyncTask(this, UriStringHelper
				.buildUriString("user-info")) {

			@Override
			protected void process(JSONObject jsonObject) {
				try {
					JSONObject user = jsonObject.getJSONObject("user");
					m_userId = user.getInt("user_id");
					m_username = user.getString("username");
					m_csrfTokenPage = user.getString("csrf_token_page");
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (m_userId == 0) {
					// display the login dialog again!
					showDialog(Main.DIALOG_LOGIN_ID);
				} else {
					// logged in
					Toast.makeText(
							Main.this,
							getResources().getString(R.string.hi) + " "
									+ m_username, Toast.LENGTH_SHORT).show();

					// re-enable the buttons
					m_vwNewSession.setEnabled(true);
					m_vwTasks.setEnabled(true);
				}
			}
		}.execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNewSession:
			// Intent intent = new Intent(this, TableListActivity.class);
			// startActivity(intent);
			break;
		case R.id.btnTasks:
			Toast.makeText(this, "To be built...", Toast.LENGTH_SHORT).show();
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
		mniLogin.setEnabled(m_userId == 0);
		if (m_username != null && m_username.length() > 0) {
			mniLogin.setTitle(getResources().getString(R.string.logged_in)
					+ ": " + m_username);
		} else {
			mniLogin.setTitle(R.string.login);
		}

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
				requireLoggedIn();
			} else {
				// it looks like the user canceled the login dialog
				// display a "friendly" reminder
				Toast.makeText(this, R.string.you_must_login_to_use_this_app,
						Toast.LENGTH_SHORT);
			}
		}
	}
}
