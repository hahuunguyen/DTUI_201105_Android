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
	public String categoryImageS;
	public String categoryImageM;
	public String categoryImageL;
	public String categoryImageU;

	public void parse(JSONObject jsonObject) throws JSONException {
		categoryId = jsonObject.getInt("category_id");
		categoryName = jsonObject.getString("category_name");
		categoryDescription = jsonObject.getString("category_description");
		JSONObject images = jsonObject.getJSONObject("images");
		categoryImageS = images.getString("l");
		categoryImageM = images.getString("m");
		categoryImageL = images.getString("s");
		categoryImageU = images.getString("u");

	}

	public void parse(Cursor cursor) {
		categoryId = cursor.getInt(DbAdapter.CATEGORY_INDEX_ID);
		categoryName = cursor.getString(DbAdapter.CATEGORY_INDEX_NAME);
		categoryDescription = cursor
				.getString(DbAdapter.CATEGORY_INDEX_DESCRIPTION);
		// categoryImageS = cursor.getString(DbAdapter.CATEGORY_IMAGES_S_ID);
		// categoryImageM = cursor.getString(DbAdapter.CATEGORY_IMAGES_M_ID);
		// categoryImageL = cursor.getString(DbAdapter.CATEGORY_IMAGES_L_ID);
		// categoryImageU = cursor.getString(DbAdapter.CATEGORY_IMAGES_U_ID);
	}

	public void save(DbAdapter dbAdapter) {
		ContentValues values = new ContentValues();
		values.put(DbAdapter.CATEGORY_KEY_ID, categoryId);
		values.put(DbAdapter.CATEGORY_KEY_NAME, categoryName);
		values.put(DbAdapter.CATEGORY_KEY_DESCRIPTION, categoryDescription);
		values.put(DbAdapter.CATEGORY_IMAGES_L_NAME, categoryImageS);
		values.put(DbAdapter.CATEGORY_IMAGES_M_NAME, categoryImageM);
		values.put(DbAdapter.CATEGORY_IMAGES_S_NAME, categoryImageL);
		values.put(DbAdapter.CATEGORY_IMAGES_U_NAME, categoryImageU);

		dbAdapter.getDb().insert(DbAdapter.DATABASE_TABLE_CATEGORY, null,
				values);
		onUpdated(AbstractEntity.TARGET_LOCAL_DATABASE);
	}
}
