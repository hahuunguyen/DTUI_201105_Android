package com.group5.android.fd.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;

import com.group5.android.fd.FdCursorAdapter;
import com.group5.android.fd.DbAdapter;

abstract public class DbBasedActivity extends ListActivity {
	protected Cursor m_cursor;
	protected FdCursorAdapter m_cursorAdapter;
	protected DbAdapter m_dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initDb();
		initLayout();
		initListeners();
	}

	protected void initDb() {
		m_dbAdapter = new DbAdapter(this);
		m_dbAdapter.open();
		m_cursor = initCursor();
		startManagingCursor(m_cursor);

		m_cursorAdapter = new FdCursorAdapter(this, m_cursor);
	}

	abstract protected Cursor initCursor();

	protected void initLayout() {
		setListAdapter(m_cursorAdapter);
	}

	protected void initListeners() {
		// TODO
	}
}
