package com.group5.android.fd.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.adapter.FdCursorAdapter;

abstract public class DbBasedActivity extends ListActivity {
	protected Cursor m_cursor;
	protected FdCursorAdapter m_cursorAdapter;
	protected DbAdapter m_dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initDb();
		initListeners();
	}

	@Override
	public void onResume() {
		super.onResume();
		initDb();
	}

	@Override
	public void onPause() {
		super.onPause();
		closeDb();
	}

	protected void initDb() {
		
	}

	protected void closeDb() {
		m_dbAdapter.close();
	}

	abstract protected Cursor initCursor();

	protected void initLayout() {
		// TODO
	}

	protected void initListeners() {
		// TODO
		getListView().setOnItemClickListener(m_cursorAdapter);
	}
}
