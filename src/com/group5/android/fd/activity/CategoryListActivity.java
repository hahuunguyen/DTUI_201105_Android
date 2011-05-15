package com.group5.android.fd.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.adapter.CategoryCursorAdapter;

public class CategoryListActivity extends DbBasedActivity {
	
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	@Override
	protected Cursor initCursor() {
		return m_dbAdapter.getAllCategories();
	}

	protected void initDb() {
		m_dbAdapter = new DbAdapter(this);
		m_dbAdapter.open();

		m_cursor = initCursor();
		startManagingCursor(m_cursor);

		Log.i(FdConfig.DEBUG_TAG, "Cursor is init'd. Rows: "
				+ m_cursor.getCount());

		m_cursorAdapter = new CategoryCursorAdapter(this, m_cursor);
		setListAdapter(m_cursorAdapter);
	}
	
	
}


