package com.group5.android.fd.activity;

import android.content.Intent;
import android.database.Cursor;

public class ItemListActivity extends DbBasedActivity {
	final public static String EXTRA_DATA_NAME_CATEGORY_ID = "categoryId";

	@Override
	protected Cursor initCursor() {
		Intent intent = getIntent();
		String categoryId = intent
				.getStringExtra(ItemListActivity.EXTRA_DATA_NAME_CATEGORY_ID);

		return m_dbAdapter.getItems(categoryId);
	}

}
