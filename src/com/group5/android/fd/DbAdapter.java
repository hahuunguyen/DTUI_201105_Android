package com.group5.android.fd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.group5.android.fd.helper.HttpHelper;
import com.group5.android.fd.helper.UriStringHelper;

public class DbAdapter {
	/******************/
	/****** STATIC DATA *******/

	/***** Database Information **********/
	public static final String DATABASE_NAME = "menuList.db";

	/********** Menu Information ********/
	public static final String DATABASE_TABLE_CATEGORY = "dtui_category";
	public static final String CATEGORIES_KEY_ID = "category_id";
	public static final String CATEGORIES_KEY_NAME = "category_name";
	public static final String CATEGORIES_KEY_DESCRIPTION = "category_description";

	public static final String DATABASE_TABLE_ITEM = "dtui_item";
	public static final String ITEM_KEY_ID = "item_id";
	public static final String ITEM_KEY_NAME = "item_name";
	public static final String ITEM_KEY_DESCRIPTION = "item_description";
	public static final String ITEM_KEY_PRICE = "price";

	/** Database SQL **/
	public static final String SQL_CREATE_TABLE_CATEGORIES = "create table "
			+ DbAdapter.DATABASE_TABLE_CATEGORY + " ("
			+ DbAdapter.CATEGORIES_KEY_ID
			+ " integer primary key autoincrement, "
			+ DbAdapter.CATEGORIES_KEY_NAME + " text not null, "
			+ DbAdapter.CATEGORIES_KEY_DESCRIPTION + " text not null); ";

	public static final String SQL_CREATE_TABLE_ITEM = "create table "
			+ DbAdapter.DATABASE_TABLE_ITEM + " (" + DbAdapter.ITEM_KEY_ID
			+ " integer primary key autoincrement, " + DbAdapter.ITEM_KEY_NAME
			+ " text not null, " + DbAdapter.ITEM_KEY_DESCRIPTION
			+ " text not null, " + DbAdapter.ITEM_KEY_PRICE
			+ " float not null);";

	/******* DATABASE INSTANCE ********/
	private SQLiteDatabase v_db;
	private fastDBHelper v_dbHelper;
	private Context _context;

	public DbAdapter(Context context) {
		/*
		 * checking database version from server
		 */
		int DATABASE_VERSION = 1;
		v_dbHelper = new fastDBHelper(context, DbAdapter.DATABASE_NAME, null,
				DATABASE_VERSION + 2);
		_context = context;
	}

	public void open() {
		v_db = v_dbHelper.getWritableDatabase();
		sync();
	}

	public void close() {
		v_db.close();
	}

	// lay du lieu database tu server
	public void sync() {

		String categoryUri = UriStringHelper.buildUriString("categories");

		try {
			JSONObject jsonObject = HttpHelper.get(_context, categoryUri);
			JSONObject categories = jsonObject.getJSONObject("categories");
			JSONArray names = categories.names();
			for (int i = 0; i < names.length(); i++) {
				JSONObject category = categories.getJSONObject(names
						.getString(i));

				String categoryName = category.getString("category_name");
				Log.v("dtui", categoryName);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * tra ve Cursor chua cac gia tri category
	 */
	public Cursor getAllCategories() {
		Cursor result = v_db.query(DbAdapter.DATABASE_TABLE_CATEGORY, null,
				null, null, null, null, null);
		return result;
	}

	/*
	 * tra ve Cursor chua cac mon an trong mot category
	 */

	public Cursor getItems(String categoryId) {
		if (categoryId == null) {
			return null;
		} else {
			String selection = DbAdapter.CATEGORIES_KEY_ID + " = ?";
			Cursor result = v_db.query(DbAdapter.DATABASE_TABLE_ITEM, null,
					selection, new String[] { categoryId }, null, null, null);
			return result;
		}

	}

	public void resetEverything() {
		v_db.execSQL("TRUNCATE TABLE " + DbAdapter.DATABASE_TABLE_CATEGORY);
		v_db.execSQL("TRUNCATE TABLE " + DbAdapter.DATABASE_TABLE_ITEM);
	}

	/*
	 * lay gia tri item trong Cursor
	 */

	public static String getTextFromCursor(Cursor cursor) {
		String text = cursor.getString(0);
		if (text != null) {
			return text;
		} else {
			return null;
		}
	}

	/**
	 * Helper class used to wrap the logic necessary for Creating and Upgrading
	 */
	private static class fastDBHelper extends SQLiteOpenHelper {
		/*** constructor **/
		public fastDBHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		/**
		 * Called when the Database is created for the first time.
		 */
		@Override
		public void onCreate(SQLiteDatabase _db) {
			// TODO
			_db.execSQL(DbAdapter.SQL_CREATE_TABLE_CATEGORIES);
			_db.execSQL(DbAdapter.SQL_CREATE_TABLE_ITEM);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// TODO
			// if ( _newVersion > _oldVersion){
			// _db.execSQL(DATABASE_DROP);
			// onCreate(_db);
			// }

		}
	}
}
