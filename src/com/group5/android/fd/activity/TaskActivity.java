package com.group5.android.fd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.group5.android.fd.Main;

public class TaskActivity extends Activity {
	protected String m_csrfTokenPage = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//get intent from Main
		Intent intent = getIntent();
		m_csrfTokenPage = intent.getStringExtra(Main.INSTANCE_STATE_KEY_CSRF_TOKEN_PAGE);

		startTaskList();
	}

	protected void startTaskList() {
		Intent taskIntent = new Intent(this, TaskListActivity.class);
		startActivity(taskIntent);
	}
}
