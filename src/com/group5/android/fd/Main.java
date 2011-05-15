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
	protected boolean m_loggedIn = false;

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
		new HttpRequestAsyncTask(this, UriStringHelper
				.buildUriString("user-info")) {

			@Override
			protected void process(JSONObject jsonObject) {
				int userId = 0;
				String username = "";

				try {
					JSONObject user = jsonObject.getJSONObject("user");
					userId = user.getInt("user_id");
					username = user.getString("username");
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (userId == 0) {
					showDialog(Main.DIALOG_LOGIN_ID);
				} else {
					Toast.makeText(
							Main.this,
							getResources().getString(R.string.hi) + " "
									+ username, Toast.LENGTH_SHORT).show();
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
	public void onDismiss(DialogInterface arg0) {
		if (arg0 instanceof LoginDialog) {
			requireLoggedIn();
		}
	}
}
