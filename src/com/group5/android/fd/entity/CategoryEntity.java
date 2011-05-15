package com.group5.android.fd.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.group5.android.fd.DbAdapter;

public class CategoryEntity extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8051542893103103746L;

	public int categoryId;
	public String categoryName;
	public String categoryDescription;

	public void parse(JSONObject jsonObject) throws JSONException {
		categoryId = jsonObject.getInt("category_id");
		categoryName = jsonObject.getString("category_name");
		categoryDescription = jsonObject.getString("category_description");
	}

	public void parse(Cursor cursor) {
		categoryId = cursor.getInt(DbAdapter.CATEGORY_INDEX_ID);
		categoryName = cursor.getString(DbAdapter.CATEGORY_INDEX_NAME);
		categoryDescription = cursor
				.getString(DbAdapter.CATEGORY_INDEX_DESCRIPTION);
	}

	public void save(DbAdapter dbAdapter) {
		ContentValues values = new ContentValues();
		values.put(DbAdapter.CATEGORY_KEY_ID, categoryId);
		values.put(DbAdapter.CATEGORY_KEY_NAME, categoryName);
		values.put(DbAdapter.CATEGORY_KEY_DESCRIPTION, categoryDescription);

		dbAdapter.getDb().insert(DbAdapter.DATABASE_TABLE_CATEGORY, null,
				values);
	}
}
