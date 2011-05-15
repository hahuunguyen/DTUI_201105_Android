package com.group5.android.fd.helper;

import android.app.Activity;
import android.os.AsyncTask;

import com.group5.android.fd.DbAdapter;

public class SyncHelper extends AsyncTask<Void, Void, Void> {

	protected Activity m_activity;
	protected DbAdapter m_dbAdapter;

	public SyncHelper(Activity activity) {
		m_activity = activity;
	}

	@Override
	protected Void doInBackground(Void... params) {
		m_dbAdapter = new DbAdapter(m_activity);
		m_dbAdapter.open();

		m_dbAdapter.resetEverything();

		return null;
	}
}
