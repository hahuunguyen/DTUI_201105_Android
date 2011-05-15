package com.group5.android.fd.activity;

import android.database.Cursor;
import android.os.Bundle;

public class TableListActivity extends DbBasedActivity {
	
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	protected Cursor initCursor() {
		return m_dbAdapter.getAllTables();
	}

}
