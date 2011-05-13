package com.group5.android.fd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.group5.android.fd.helper.HttpHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DbAdapter {
	/******************/
	/****** STATIC DATA *******/

	/***** Database Information **********/
	public static final String DATABASE_NAME = "menuList.db";

	/** Database SQL **/
	public static final String TABLE_CREATE = "create table " + "Category"
			+ " (" + "category_id" + " integer primary key autoincrement, "
			+ "category_name" + " text not null, " + "category_description"
			+ " text not null); ";

	public static final String CATEGORY_CREATE = "create table " + "item"
			+ " (" + "item_id" + " integer primary key autoincrement, "
			+ "item_name" + " text not null, " + "item_description"
			+ " text not null, " + "price" + " float not null);";

	/********** Menu Information ********/
	public static final String DATABASE_TABLE_TABLELIST = "table";
	public static final String TABLELIST_KEY_TEXT = "table_name";

	public static final String DATABASE_TABLE_CATEGORIES = "category";
	public static final String CATEGORIES_KEY_ID = "category_id";
	public static final String CATEGORIES_KEY_TEXT = "category_name";

	public static final String DATABASE_TABLE_ITEM = "item";
	public static final String ITEM_KEY_TEXT = "item_name";

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
	}

	public void close() {
		v_db.close();
	}

	public void sync() {
		String categoryUri = "http://10.0.2.2/dtui/dtui-entry-point/categories.json";

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
	 * tra ve Cursor chua cac gia tri table
	 */
	public Cursor getAllTables() {
		String[] columns = new String[] { "table_name" };
		// Cursor result = v_db.query(DATABASE_TABLE_TABLELIST, columns, null,
		// null, null, null, null);
		return null;

	}

	/*
	 * tra ve Cursor chua cac gia tri category
	 */
	public Cursor getAllCategories() {
		Cursor result = v_db.query(DbAdapter.DATABASE_TABLE_CATEGORIES, null,
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

			_db.execSQL(DbAdapter.TABLE_CREATE);
			_db.execSQL(DbAdapter.CATEGORY_CREATE);
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
