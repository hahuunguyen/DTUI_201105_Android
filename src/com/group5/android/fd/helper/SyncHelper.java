package com.group5.android.fd.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;
import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.entity.ItemEntity;

public class SyncHelper extends AsyncTask<Void, Integer, Void> {

	protected Activity m_activity;
	protected ProgressDialog m_progressDialog;
	protected DbAdapter m_dbAdapter;

	public SyncHelper(Activity activity) {
		m_activity = activity;

		m_progressDialog = ProgressDialog.show(activity,
				getResourceString(R.string.sync_data),
				getResourceString(R.string.sync_data_start), true, false);
	}

	@Override
	protected Void doInBackground(Void... params) {
		initDb();
		truncate();
		publishProgress(R.string.sync_data_db_ok);

		syncCategory();
		publishProgress(R.string.sync_data_categories_ok);

		syncItem();
		publishProgress(R.string.sync_data_items_ok);

		closeDb();
		// done

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... params) {
		m_progressDialog.setMessage(getResourceString(params[0]));
	}

	@Override
	protected void onPostExecute(Void param) {
		m_progressDialog.dismiss();
	}

	protected void initDb() {
		m_dbAdapter = new DbAdapter(m_activity);
		m_dbAdapter.open();
	}

	protected void closeDb() {
		m_dbAdapter.close();
	}

	protected void truncate() {
		m_dbAdapter.truncateEverything();
	}

	protected void syncCategory() {
		String categoriesUrl = UriStringHelper.buildUriString("categories");
		JSONObject response = HttpHelper.get(categoriesUrl);

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
		} catch (NullPointerException e) {
			Log.d(FdConfig.DEBUG_TAG, "syncCategory got NULL response");
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void syncItem() {
		String itemsUrl = UriStringHelper.buildUriString("items");
		Cursor categoryCursor = m_dbAdapter.getAllCategories();
		CategoryEntity category = new CategoryEntity();

		categoryCursor.moveToFirst();
		while (!categoryCursor.isAfterLast()) {
			category.parse(categoryCursor);
			Log.i(FdConfig.DEBUG_TAG, "syncItem: " + category.categoryName);

			String categoryItemsUrl = UriStringHelper.addParam(itemsUrl,
					"category_id", category.categoryId);
			JSONObject response = HttpHelper.get(categoryItemsUrl);

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
			} catch (NullPointerException e) {
				Log.d(FdConfig.DEBUG_TAG, "syncItem got NULL response");
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			categoryCursor.moveToNext();
		}
	}

	protected String getResourceString(int id) {
		return m_activity.getResources().getString(id);
	}
}
