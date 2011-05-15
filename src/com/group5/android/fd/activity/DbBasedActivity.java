package com.group5.android.fd.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.FdCursorAdapter;

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
		m_dbAdapter = new DbAdapter(this);
		m_dbAdapter.open();

		m_cursor = initCursor();
		startManagingCursor(m_cursor);

		Log.i(FdConfig.DEBUG_TAG, "Cursor is init'd. Rows: "
				+ m_cursor.getCount());

		m_cursorAdapter = new FdCursorAdapter(this, m_cursor);
		setListAdapter(m_cursorAdapter);
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
	}
}
