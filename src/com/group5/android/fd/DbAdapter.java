package com.group5.android.fd;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter {
	/******************/
	/****** STATIC DATA *******/

	/***** Database Information **********/
	public static final String DATABASE_NAME = "menuList.db";

	/********** Menu Information ********/
	public static final String DATABASE_TABLE_CATEGORY = "dtui_category";
	public static final String CATEGORY_KEY_ID = "_id";
	public static final String CATEGORY_KEY_NAME = "category_name";
	public static final String CATEGORY_KEY_DESCRIPTION = "category_description";
	public static final String CATEGORY_IMAGES_S_NAME = "category_images_s_name";
	public static final String CATEGORY_IMAGES_M_NAME = "category_images_m_name";
	public static final String CATEGORY_IMAGES_L_NAME = "category_images_l_name";
	public static final String CATEGORY_IMAGES_U_NAME = "category_images_u_name";
	public static final int CATEGORY_INDEX_ID = 0;
	public static final int CATEGORY_INDEX_NAME = DbAdapter.CATEGORY_INDEX_ID + 1;
	public static final int CATEGORY_INDEX_DESCRIPTION = DbAdapter.CATEGORY_INDEX_ID + 2;
	public static final int CATEGORY_IMAGES_S_ID = DbAdapter.CATEGORY_INDEX_ID + 3;
	public static final int CATEGORY_IMAGES_M_ID = DbAdapter.CATEGORY_INDEX_ID + 4;
	public static final int CATEGORY_IMAGES_L_ID = DbAdapter.CATEGORY_INDEX_ID + 5;
	public static final int CATEGORY_IMAGES_U_ID = DbAdapter.CATEGORY_INDEX_ID + 6;

	public static final String DATABASE_TABLE_ITEM = "dtui_item";
	public static final String ITEM_KEY_ID = "_id";
	public static final String ITEM_KEY_NAME = "item_name";
	public static final String ITEM_KEY_DESCRIPTION = "item_description";
	public static final String ITEM_KEY_PRICE = "price";
	public static final String ITEM_KEY_CATEGORY_ID = "category_id";
	public static final String ITEM_IMAGES_S_NAME = "item_images_s_name";
	public static final String ITEM_IMAGES_M_NAME = "item_images_m_name";
	public static final String ITEM_IMAGES_L_NAME = "item_images_l_name";
	public static final String ITEM_IMAGES_U_NAME = "item_images_u_name";
	public static final int ITEM_INDEX_ID = 0;
	public static final int ITEM_INDEX_NAME = DbAdapter.ITEM_INDEX_ID + 1;
	public static final int ITEM_INDEX_DESCRIPTION = DbAdapter.ITEM_INDEX_ID + 2;
	public static final int ITEM_INDEX_PRICE = DbAdapter.ITEM_INDEX_ID + 3;
	public static final int ITEM_INDEX_CATEGORY_ID = DbAdapter.ITEM_INDEX_ID + 4;
	public static final int ITEM_IMAGES_S_ID = DbAdapter.CATEGORY_INDEX_ID + 5;
	public static final int ITEM_IMAGES_M_ID = DbAdapter.CATEGORY_INDEX_ID + 6;
	public static final int ITEM_IMAGES_L_ID = DbAdapter.CATEGORY_INDEX_ID + 7;
	public static final int ITEM_IMAGES_U_ID = DbAdapter.CATEGORY_INDEX_ID + 8;

	/** Database SQL **/
	public static final int DATABASE_VERSION = 10;
	public static final String SQL_CREATE_TABLE_CATEGORY = "create table "
			+ DbAdapter.DATABASE_TABLE_CATEGORY + " ("
			+ DbAdapter.CATEGORY_KEY_ID
			+ " integer primary key autoincrement, "
			+ DbAdapter.CATEGORY_KEY_NAME + " text not null, "
			+ DbAdapter.CATEGORY_KEY_DESCRIPTION + " text not null, "
			+ DbAdapter.CATEGORY_IMAGES_S_NAME + " text not null,"
			+ DbAdapter.CATEGORY_IMAGES_M_NAME + " text not null,"
			+ DbAdapter.CATEGORY_IMAGES_L_NAME + " text not null,"
			+ DbAdapter.CATEGORY_IMAGES_U_NAME + " text not null);";

	public static final String SQL_DROP_TABLE_CATEGORY = "drop table if exists "
			+ DbAdapter.DATABASE_TABLE_CATEGORY;

	public static final String SQL_CREATE_TABLE_ITEM = "create table "
			+ DbAdapter.DATABASE_TABLE_ITEM + " (" + DbAdapter.ITEM_KEY_ID
			+ " integer primary key autoincrement, " + DbAdapter.ITEM_KEY_NAME
			+ " text not null, " + DbAdapter.ITEM_KEY_DESCRIPTION
			+ " text not null, " + DbAdapter.ITEM_KEY_PRICE
			+ " float not null, " + DbAdapter.ITEM_KEY_CATEGORY_ID
			+ " integer not null," + DbAdapter.ITEM_IMAGES_S_NAME
			+ " text not null," + DbAdapter.ITEM_IMAGES_M_NAME
			+ " text not null," + DbAdapter.ITEM_IMAGES_L_NAME
			+ " text not null," + DbAdapter.ITEM_IMAGES_U_NAME
			+ " text not null);";

	public static final String SQL_DROP_TABLE_ITEM = "drop table if exists "
			+ DbAdapter.DATABASE_TABLE_ITEM;

	/******* DATABASE INSTANCE ********/
	private SQLiteDatabase v_db;
	private final fastDBHelper v_dbHelper;

	public DbAdapter(Context context) {
		v_dbHelper = new fastDBHelper(context, DbAdapter.DATABASE_NAME, null,
				DbAdapter.DATABASE_VERSION);
	}

	public void open() {
		v_db = v_dbHelper.getWritableDatabase();
	}

	public void close() {
		v_db.close();
	}

	public SQLiteDatabase getDb() {
		return v_db;
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

	public Cursor getItems(int categoryId) {
		if (categoryId < 0) {
			return null;
		} else {
			Cursor result = v_db.query(DbAdapter.DATABASE_TABLE_ITEM, null,
					DbAdapter.ITEM_KEY_CATEGORY_ID + " = ?",
					new String[] { String.valueOf(categoryId) }, null, null,
					null);
			return result;
		}

	}

	public void truncateEverything() {
		v_db.execSQL("DELETE FROM " + DbAdapter.DATABASE_TABLE_CATEGORY);
		v_db.execSQL("DELETE FROM " + DbAdapter.DATABASE_TABLE_ITEM);
	}

	/*
	 * lay gia tri item trong Cursor
	 */

	public static String getTextFromCursor(Cursor cursor) {
		String text = cursor.getString(1);
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
			_db.execSQL(DbAdapter.SQL_CREATE_TABLE_CATEGORY);
			_db.execSQL(DbAdapter.SQL_CREATE_TABLE_ITEM);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			if (_newVersion > _oldVersion) {
				_db.execSQL(DbAdapter.SQL_DROP_TABLE_CATEGORY);
				_db.execSQL(DbAdapter.SQL_DROP_TABLE_ITEM);
				onCreate(_db);
			}

		}
	}
}
