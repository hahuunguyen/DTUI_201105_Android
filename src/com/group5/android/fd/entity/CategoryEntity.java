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

		try {
			// these properties are not included all the time
			JSONObject images = jsonObject.getJSONObject("images");
			categoryImageL = images.getString("l");
			categoryImageM = images.getString("m");
			categoryImageS = images.getString("s");
			categoryImageU = images.getString("u");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parse(Cursor cursor) {
		categoryId = cursor.getInt(DbAdapter.CATEGORY_INDEX_ID);
		categoryName = cursor.getString(DbAdapter.CATEGORY_INDEX_NAME);
		categoryDescription = cursor
				.getString(DbAdapter.CATEGORY_INDEX_DESCRIPTION);
		categoryImageL = cursor.getString(DbAdapter.CATEGORY_INDEX_IMAGES_L);
		categoryImageM = cursor.getString(DbAdapter.CATEGORY_INDEX_IMAGES_M);
		categoryImageS = cursor.getString(DbAdapter.CATEGORY_INDEX_IMAGES_S);
		categoryImageU = cursor.getString(DbAdapter.CATEGORY_INDEX_IMAGES_U);
	}

	public void save(DbAdapter dbAdapter) {
		ContentValues values = new ContentValues();
		values.put(DbAdapter.CATEGORY_KEY_ID, categoryId);
		values.put(DbAdapter.CATEGORY_KEY_NAME, categoryName);
		values.put(DbAdapter.CATEGORY_KEY_DESCRIPTION, categoryDescription);

		values.put(DbAdapter.CATEGORY_KEY_IMAGES_S, categoryImageS);
		values.put(DbAdapter.CATEGORY_KEY_IMAGES_M, categoryImageM);
		values.put(DbAdapter.CATEGORY_KEY_IMAGES_L, categoryImageL);
		values.put(DbAdapter.CATEGORY_KEY_IMAGES_U, categoryImageU);

		dbAdapter.getDb().insert(DbAdapter.DATABASE_TABLE_CATEGORY, null,
				values);
		onUpdated(AbstractEntity.TARGET_LOCAL_DATABASE);
	}
}
