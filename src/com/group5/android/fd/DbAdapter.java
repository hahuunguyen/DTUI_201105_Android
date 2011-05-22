package com.group5.android.fd;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Database manager for the whole app. It utilizes a
 * <code>SQLiteOpenHelper</code> to create the database the first time it gets
 * running (or when a database change is required, which happened quite a lot of
 * times during development)
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class DbAdapter {
	// Tables and columns information
	public static final String ABSTRACT_KEY_IMAGES_XH = "image_xh";
	public static final String ABSTRACT_KEY_IMAGES_H = "image_h";
	public static final String ABSTRACT_KEY_IMAGES_M = "image_m";
	public static final String ABSTRACT_KEY_IMAGES_L = "image_l";

	/**
	 * The name of Category table
	 */
	public static final String DATABASE_TABLE_CATEGORY = "dtui_category";
	public static final String CATEGORY_KEY_ID = "_id";
	public static final String CATEGORY_KEY_NAME = "category_name";
	public static final String CATEGORY_KEY_DESCRIPTION = "category_description";
	public static final int CATEGORY_INDEX_ID = 0;
	public static final int CATEGORY_INDEX_NAME = DbAdapter.CATEGORY_INDEX_ID + 1;
	public static final int CATEGORY_INDEX_DESCRIPTION = DbAdapter.CATEGORY_INDEX_ID + 2;

	/**
	 * The name of Item table
	 */
	public static final String DATABASE_TABLE_ITEM = "dtui_item";
	public static final String ITEM_KEY_ID = "_id";
	public static final String ITEM_KEY_NAME = "item_name";
	public static final String ITEM_KEY_DESCRIPTION = "item_description";
	public static final String ITEM_KEY_PRICE = "price";
	public static final String ITEM_KEY_CATEGORY_ID = "category_id";
	public static final int ITEM_INDEX_ID = 0;
	public static final int ITEM_INDEX_NAME = DbAdapter.ITEM_INDEX_ID + 1;
	public static final int ITEM_INDEX_DESCRIPTION = DbAdapter.ITEM_INDEX_ID + 2;
	public static final int ITEM_INDEX_PRICE = DbAdapter.ITEM_INDEX_ID + 3;
	public static final int ITEM_INDEX_CATEGORY_ID = DbAdapter.ITEM_INDEX_ID + 4;

	/**
	 * The filename which will be used to store the database.
	 */
	public static final String DATABASE_NAME = "summerisalmosthere.db";

	/**
	 * Current version of the database, it should be incremented everytime a
	 * database schema change is required
	 */
	public static final int DATABASE_VERSION = 16;

	/**
	 * The SQL statement which will create the Category table
	 */
	public static final String SQL_CREATE_TABLE_CATEGORY = "CREATE TABLE "
			+ DbAdapter.DATABASE_TABLE_CATEGORY + " ("
			+ DbAdapter.CATEGORY_KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ DbAdapter.CATEGORY_KEY_NAME + " TEXT NOT NULL, "
			+ DbAdapter.CATEGORY_KEY_DESCRIPTION + " TEXT NOT NULL, "
			+ DbAdapter.ABSTRACT_KEY_IMAGES_XH + " TEXT,"
			+ DbAdapter.ABSTRACT_KEY_IMAGES_H + " TEXT,"
			+ DbAdapter.ABSTRACT_KEY_IMAGES_M + " TEXT,"
			+ DbAdapter.ABSTRACT_KEY_IMAGES_L + " TEXT);";

	/**
	 * The SQL statement which will create the Item table
	 */
	public static final String SQL_CREATE_TABLE_ITEM = "CREATE TABLE "
			+ DbAdapter.DATABASE_TABLE_ITEM + " (" + DbAdapter.ITEM_KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DbAdapter.ITEM_KEY_NAME
			+ " TEXT NOT NULL, " + DbAdapter.ITEM_KEY_DESCRIPTION
			+ " TEXT NOT NULL, " + DbAdapter.ITEM_KEY_PRICE
			+ " FLOAT NOT NULL, " + DbAdapter.ITEM_KEY_CATEGORY_ID
			+ " INTEGER NOT NULL," + DbAdapter.ABSTRACT_KEY_IMAGES_XH
			+ " TEXT," + DbAdapter.ABSTRACT_KEY_IMAGES_H + " TEXT,"
			+ DbAdapter.ABSTRACT_KEY_IMAGES_M + " TEXT,"
			+ DbAdapter.ABSTRACT_KEY_IMAGES_L + " TEXT);";

	protected SQLiteDatabase v_db;
	protected FdDbHelper v_dbHelper;

	/**
	 * Construct an adapter and make it ready to work. Basically just setup the
	 * helper here.
	 * 
	 * @param context
	 */
	public DbAdapter(Context context) {
		v_dbHelper = new FdDbHelper(context, DbAdapter.DATABASE_NAME, null,
				DbAdapter.DATABASE_VERSION);
	}

	/**
	 * Gets a writable database handler from the helper and keep it for further
	 * usage.
	 */
	public void open() {
		v_db = v_dbHelper.getWritableDatabase();
	}

	/**
	 * Closes the database handler associated with this adapter. You should call
	 * this when you finish your database stuff or you will get not-so-friendly
	 * warning in Logcat
	 */
	public void close() {
		v_db.close();
	}

	/**
	 * Gets the database handler directly. If you need to execute the query
	 * yourself, or the predefined method is not good enough for you, you can
	 * use this method and do whatever you want with it.
	 * 
	 * @return the <code>SQLiteDatabase</code> which is setup in this adapter
	 */
	public SQLiteDatabase getDb() {
		return v_db;
	}

	/**
	 * Helper method: Counts the number of row in any given table name. Be
	 * careful with this method. If you give it an incorrect table name, an
	 * exception will come to your face. The method is smart enough to clean up
	 * its footprint (close the <code>Cursor</code> etc.)
	 * 
	 * @param tableName
	 *            the table name, in full. You may want to consider to use the
	 *            constant like {@link #DATABASE_TABLE_CATEGORY} or
	 *            {@link #DATABASE_TABLE_ITEM}
	 * @return the number of rows in the given table name
	 */
	public int countRowsInTable(String tableName) {
		int count = 0;

		Cursor result = v_db
				.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
		result.moveToFirst();
		count = result.getInt(0);
		result.close();

		return count;
	}

	/**
	 * Helper method: Queries the database for categories. This method returns a
	 * <code>Cursor</code> so PLEASE remember to close it when you finish using
	 * it. I suggest using method like
	 * {@link Activity#startManagingCursor(Cursor)} for peace in mind. You will
	 * also need to call {@link Cursor#moveToFirst()} yourself. All columns will
	 * be included in the result so you can use the constants like
	 * {@link #CATEGORY_INDEX_NAME} to get data.
	 * 
	 * @return the queried <code>Cursor</code>
	 */
	public Cursor getCategories() {
		Cursor result = v_db.query(DbAdapter.DATABASE_TABLE_CATEGORY, null,
				null, null, null, null, null);
		return result;
	}

	/**
	 * Helper method: Queries the database for items of a category. This method
	 * returns a <code>Cursor</code> so PLEASE remember to close it when you
	 * finish using it. I suggest using method like
	 * {@link Activity#startManagingCursor(Cursor)} for peace in mind. You will
	 * also need to call {@link Cursor#moveToFirst()} yourself. All columns will
	 * be included in the result so you can use the constants like
	 * {@link #ITEM_INDEX_NAME} to get data.
	 * 
	 * @param categoryId
	 *            the id of the category
	 * @return a queried <code>Cursor</code> or null if no category id is
	 *         supplied
	 */
	public Cursor getItems(int categoryId) {
		if (categoryId > 0) {
			Cursor result = v_db.query(DbAdapter.DATABASE_TABLE_ITEM, null,
					DbAdapter.ITEM_KEY_CATEGORY_ID + " = ?",
					new String[] { String.valueOf(categoryId) }, null, null,
					null);
			return result;
		} else {
			return null;
		}
	}

	/**
	 * Helper method: Simply truncates every single table in the database via
	 * SQL statement "DELETE FROM". I tried to use "TRUNCATE" but it looks like
	 * SQLite doesn't support that statement. Up to this date, this method will
	 * delete rows from Category and Item tables.
	 */
	public void truncateEverything() {
		v_db.execSQL("DELETE FROM " + DbAdapter.DATABASE_TABLE_CATEGORY);
		v_db.execSQL("DELETE FROM " + DbAdapter.DATABASE_TABLE_ITEM);
	}

	/**
	 * Helper class to manage creating and upgrading the database
	 * 
	 * @author Nguyen Huu Ha
	 * 
	 */
	private static class FdDbHelper extends SQLiteOpenHelper {
		/**
		 * A basic constructor, it just passes the variable to its parent.
		 * Please read
		 * {@link SQLiteOpenHelper#SQLiteOpenHelper(Context, String, CursorFactory, int)}
		 * for more information.
		 * 
		 * @param context
		 * @param name
		 * @param factory
		 * @param version
		 */
		public FdDbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DbAdapter.SQL_CREATE_TABLE_CATEGORY);
			_db.execSQL(DbAdapter.SQL_CREATE_TABLE_ITEM);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			if (_newVersion > _oldVersion) {
				_db.execSQL("DROP TABLE IF EXISTS "
						+ DbAdapter.DATABASE_TABLE_CATEGORY);
				_db.execSQL("DROP TABLE IF EXISTS "
						+ DbAdapter.DATABASE_TABLE_ITEM);

				onCreate(_db);
			}

		}
	}
}
