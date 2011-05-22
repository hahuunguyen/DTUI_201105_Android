package com.group5.android.fd.helper;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;
import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.entity.ItemEntity;

/**
 * Helper class to synchronize data
 * 
 * @author Dao Hoang Son
 * 
 */
public class SyncHelper extends AsyncTask<Void, Integer, Void> {

	protected Activity m_activity;
	protected ProgressDialog m_progressDialog;
	protected DbAdapter m_dbAdapter;
	protected SyncHelper.SyncHelperCaller m_caller = null;
	protected String m_errorMessage = null;
	protected String m_itemSyncingCategoryName = null;

	/**
	 * Constructs everything. Display a progress dialog if the caller is a
	 * {@link SyncHelperCaller}, it's required as a safe machenism to make sure
	 * the caller calls {@link #dismissProgressDialog()} if something goes wrong
	 * with it (like the activity is paused)
	 * 
	 * @param activity
	 */
	public SyncHelper(Activity activity) {
		m_activity = activity;

		if (activity instanceof SyncHelper.SyncHelperCaller) {
			m_progressDialog = ProgressDialog.show(activity,
					getResourceString(R.string.sync_data),
					getResourceString(R.string.sync_data_start), true, false);

			m_caller = (SyncHelper.SyncHelperCaller) activity;
			m_caller.addSyncHelper(this);
		}
	}

	/**
	 * Dismisses the progress dialog if it's being shown
	 */
	public void dismissProgressDialog() {
		if (m_progressDialog != null) {
			m_progressDialog.dismiss();
		}

		if (m_errorMessage != null) {
			// we got a recent error message
			// display it now
			Toast.makeText(m_activity, m_errorMessage, Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		if (!initDb()) {
			return null;
		}

		if (!truncate()) {
			return null;
		}
		publishProgress(R.string.sync_data_db_ok);

		if (!syncCategory()) {
			return null;
		}
		publishProgress(R.string.sync_data_categories_ok);

		if (!syncItems()) {
			return null;
		}
		// publishProgress() will be called inside syncItems()
		// publishProgress(R.string.sync_data_items_ok);

		// done
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... params) {
		int messageId = R.string.please_wait;
		if (params.length > 0) {
			messageId = params[0];
		}

		if (m_progressDialog != null && m_progressDialog.isShowing()) {
			String message = getResourceString(messageId);
			if (messageId == R.string.sync_data_items_ok
					&& m_itemSyncingCategoryName != null) {
				message += ": " + m_itemSyncingCategoryName;
			}

			m_progressDialog.setMessage(message);
		} else {
			Toast.makeText(m_activity, messageId, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPostExecute(Void param) {
		closeDb();
		dismissProgressDialog();
	}

	/**
	 * Get a string from resources
	 * 
	 * @param id
	 * @return the string
	 */
	protected String getResourceString(int id) {
		return m_activity.getString(id);
	}

	/**
	 * Open a database connection with {@link DbAdapter}
	 * 
	 * @return true if succeeds
	 */
	protected boolean initDb() {
		m_dbAdapter = new DbAdapter(m_activity);
		m_dbAdapter.open();

		return true;
	}

	/**
	 * Close the database connection
	 * 
	 * @return true if succeeds
	 */
	protected boolean closeDb() {
		m_dbAdapter.close();

		return true;
	}

	/**
	 * Truncates old data like rows in database (via
	 * {@link DbAdapter#truncateEverything()}) or cached images (via
	 * {@link ImageHelper#removeCachedFiles()})
	 * 
	 * @return true if succeeds
	 */
	protected boolean truncate() {
		m_dbAdapter.truncateEverything();
		ImageHelper.removeCachedFiles();

		return true;
	}

	/**
	 * Gets the categories from server (with {@link HttpHelper}), parses it and
	 * put into database
	 * 
	 * @return true if succeeds
	 * 
	 * @see CategoryEntity#parse(JSONObject)
	 * @see CategoryEntity#save(DbAdapter)
	 */
	protected boolean syncCategory() {
		String categoriesUrl = UriStringHelper.buildUriString("categories");
		JSONObject response = HttpHelper.get(categoriesUrl);
		m_errorMessage = HttpHelper.lookForErrorMessages(response, m_activity);

		if (m_errorMessage != null) {
			return false;
		}

		try {
			JSONObject categories = response.getJSONObject("categories");
			JSONArray categoryIds = categories.names();
			CategoryEntity category = new CategoryEntity();
			for (int i = 0; i < categoryIds.length(); i++) {
				JSONObject jsonObject = categories.getJSONObject(categoryIds
						.getString(i));
				category.parse(jsonObject);
				category.save(m_dbAdapter);

				Log.i(FdConfig.DEBUG_TAG, "synced: " + category.categoryName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			m_errorMessage = e.getMessage();

			return false;
		}

		return true;
	}

	/**
	 * Gets the list of categories, goes through it, gets the items of that
	 * category from server (with {@link HttpHelper}), parses it and put into
	 * database. This method also triggers {@link #onProgressUpdate(Integer...)}
	 * by itself to notify that a category has just been synchronized
	 * 
	 * @return true if succeeds
	 * 
	 * @see ItemEntity#parse(JSONObject)
	 * @see ItemEntity#save(DbAdapter)
	 */
	protected boolean syncItems() {
		String itemsUrl = UriStringHelper.buildUriString("items");
		Cursor categoryCursor = m_dbAdapter.getCategories();
		CategoryEntity category = new CategoryEntity();

		categoryCursor.moveToFirst();
		while (!categoryCursor.isAfterLast()) {
			category.parse(categoryCursor);

			Log.i(FdConfig.DEBUG_TAG, "syncItem: " + category.categoryName);
			m_itemSyncingCategoryName = category.categoryName;

			String categoryItemsUrl = UriStringHelper.addParam(itemsUrl,
					"category_id", category.categoryId);
			JSONObject response = HttpHelper.get(categoryItemsUrl);
			m_errorMessage = HttpHelper.lookForErrorMessages(response,
					m_activity);

			if (m_errorMessage != null) {
				return false;
			}

			try {
				JSONObject items = response.getJSONObject("items");
				JSONArray itemIds = items.names();
				ItemEntity item = new ItemEntity();
				for (int i = 0; i < itemIds.length(); i++) {
					JSONObject jsonObject = items.getJSONObject(itemIds
							.getString(i));
					item.parse(jsonObject);
					item.save(m_dbAdapter);

					Log.i(FdConfig.DEBUG_TAG, "synced: " + item.itemName);
				}
			} catch (Exception e) {
				e.printStackTrace();
				m_errorMessage = e.getMessage();

				return false;
			}

			categoryCursor.moveToNext();
			publishProgress(R.string.sync_data_items_ok);
		}

		categoryCursor.close();

		return true;
	}

	/**
	 * Checks if the current database needs synchronization. It checks by open a
	 * database connection and query for rows from category and item table. If
	 * any of them is empty, it assumes that the database needs to be
	 * synchronized.
	 * 
	 * @param context
	 * @return true if needed
	 */
	public static boolean needSync(Context context) {
		boolean needed = false;
		DbAdapter dbAdapter = new DbAdapter(context);
		dbAdapter.open();

		if (dbAdapter.countRowsInTable(DbAdapter.DATABASE_TABLE_CATEGORY) == 0
				|| dbAdapter.countRowsInTable(DbAdapter.DATABASE_TABLE_ITEM) == 0) {
			needed = true;
		}

		dbAdapter.close();

		Log.i(FdConfig.DEBUG_TAG, "needSync: " + needed);

		return needed;
	}

	/**
	 * Interface that caller has to implement to enable progress dialog in
	 * {@link SyncHelper}. It's possible to call it without implementing this
	 * method anyway.
	 * 
	 * @author Dao Hoang Son
	 * 
	 */
	public interface SyncHelperCaller {
		public void addSyncHelper(SyncHelper sh);

		public void removeSyncHelper(SyncHelper sh);
	}
}
