package com.group5.android.fd;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.group5.android.fd.activity.LoginDialog;
import com.group5.android.fd.activity.NewSessionActivity;

public class Main extends Activity implements OnClickListener,
		OnMenuItemClickListener, OnDismissListener {
	final public static int DIALOG_LOGIN_ID = 1;

	protected Button m_vwNewSession;
	protected Button m_vwTasks;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLayout();
		initListeners();
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

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNewSession:
			Intent intent = new Intent(this, NewSessionActivity.class);
			startActivity(intent);
			break;
		case R.id.btnTasks:
			Toast.makeText(this, "To be built...", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.menu_main_login).setOnMenuItemClickListener(this);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem arg0) {
		switch (arg0.getItemId()) {
		case R.id.menu_main_login:
			showDialog(Main.DIALOG_LOGIN_ID);
			break;
		case R.id.menu_main_sync:
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
			// TODO
		}
	}
}
