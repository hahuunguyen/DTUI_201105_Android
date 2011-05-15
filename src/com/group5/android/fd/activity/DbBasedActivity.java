package com.group5.android.fd.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.FdCursorAdapter;

abstract public class DbBasedActivity extends ListActivity {
	protected Cursor m_cursor;
	protected FdCursorAdapter m_cursorAdapter;
	protected DbAdapter m_dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initLayout();
		initListeners();
	}

	@Override
	public void onResume() {
		initDb();
	}

	@Override
	public void onPause() {
		closeDb();
	}

	protected void initDb() {
		m_dbAdapter = new DbAdapter(this);
		m_dbAdapter.open();
		// m_dbAdapter.sync();
		m_cursor = initCursor();
		startManagingCursor(m_cursor);

		m_cursorAdapter = new FdCursorAdapter(this, m_cursor);
	}

	protected void closeDb() {
		m_dbAdapter.close();
	}

	abstract protected Cursor initCursor();

	protected void initLayout() {
		setListAdapter(m_cursorAdapter);
	}

	protected void initListeners() {
		// TODO
	}
}
