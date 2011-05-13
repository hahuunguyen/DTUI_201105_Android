package com.group5.android.fd.activity;

import android.database.Cursor;

public class TableListActivity extends DbBasedActivity {

	@Override
	protected Cursor initCursor() {
		return m_dbAdapter.getAllTables();
	}

}
