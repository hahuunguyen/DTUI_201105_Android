package com.group5.android.fd.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.group5.android.fd.DbAdapter;

/**
 * An item
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class ItemEntity extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6704341940680459268L;

	public int itemId;
	public String itemName;
	public String itemDescription;
	public double price;
	public int categoryId;

	// get values from JSONObject , from server for sync()
	public void parse(JSONObject jsonObject) throws JSONException {
		itemId = jsonObject.getInt("item_id");
		itemName = jsonObject.getString("item_name");
		itemDescription = jsonObject.getString("item_description");
		price = jsonObject.getDouble("price");
		categoryId = jsonObject.getInt("category_id");

		parseImages(jsonObject);
	}

	// get values from cursor
	public void parse(Cursor cursor) {
		itemId = cursor.getInt(DbAdapter.ITEM_INDEX_ID);
		itemName = cursor.getString(DbAdapter.ITEM_INDEX_NAME);
		itemDescription = cursor.getString(DbAdapter.ITEM_INDEX_DESCRIPTION);
		price = cursor.getDouble(DbAdapter.ITEM_INDEX_PRICE);
		categoryId = cursor.getInt(DbAdapter.ITEM_INDEX_CATEGORY_ID);

		parseImages(cursor, DbAdapter.ITEM_INDEX_CATEGORY_ID);
	}

	// save values to database
	public void save(DbAdapter dbAdapter) {
		ContentValues values = new ContentValues();
		values.put(DbAdapter.ITEM_KEY_ID, itemId);
		values.put(DbAdapter.ITEM_KEY_NAME, itemName);
		values.put(DbAdapter.ITEM_KEY_DESCRIPTION, itemDescription);
		values.put(DbAdapter.ITEM_KEY_PRICE, price);
		values.put(DbAdapter.ITEM_KEY_CATEGORY_ID, categoryId);

		saveImages(values);

		dbAdapter.getDb().insert(DbAdapter.DATABASE_TABLE_ITEM, null, values);
		onUpdated(AbstractEntity.TARGET_LOCAL_DATABASE);
	}
}
