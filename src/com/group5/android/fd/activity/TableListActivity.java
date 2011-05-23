package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;
import com.group5.android.fd.adapter.TableAdapter;
import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;
import com.group5.android.fd.view.TableView;

/**
 * The activity to display a list of tables
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class TableListActivity extends ServerBasedActivity {

	final public static String ACTIVITY_RESULT_NAME_TABLE_OBJ = "tableObj";

	protected TableAdapter m_tableAdapter;
	protected static boolean m_showAll = false;

	@Override
	public Object onRetainNonConfigurationInstance() {
		// we want to preserve our order information when configuration is
		// change, say.. orientation change?
		return m_tableAdapter.getTableList();
	}

	@Override
	protected void onResume() {
		super.onResume();

		getTablesAndInitLayoutEverything();
	}

	/**
	 * Initiates the layout (inflate from a layout resource named
	 * activity_main). And then maps all the object properties with their view
	 * instance. Finally, initiates required listeners on those views.
	 * 
	 * @param tableList
	 *            a <code>List</code> of {@link TableEntity} to pre-populate the
	 *            list
	 * 
	 * @see #getTablesAndInitLayoutEverything()
	 */
	protected void initLayout(List<TableEntity> tableList) {
		m_tableAdapter = new TableAdapter(this, tableList);
		setListAdapter(m_tableAdapter);
		setCustomTitle(R.string.tablelist_choose_table);

		getListView().postInvalidate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.table_list, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(R.id.menu_table_list_show_all);

		if (TableListActivity.m_showAll) {
			item.setIcon(R.drawable.checkbox_on);
		} else {
			item.setIcon(R.drawable.checkbox_off);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_table_list_refresh:
			getTablesAndInitLayoutEverything();
			return true;
		case R.id.menu_table_list_show_all:
			TableListActivity.m_showAll = !TableListActivity.m_showAll;
			getTablesAndInitLayoutEverything();
			return true;
		}

		return false;
	}

	/**
	 * Gets the available tables from the server and set them up.
	 * 
	 * @see HttpRequestAsyncTask
	 */
	@SuppressWarnings("unchecked")
	protected void getTablesAndInitLayoutEverything() {
		Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
		List<TableEntity> tableList = null;
		if (lastNonConfigurationInstance != null
				&& lastNonConfigurationInstance instanceof List<?>) {
			// found our long lost table list, yay!
			tableList = (List<TableEntity>) lastNonConfigurationInstance;

			Log.i(FdConfig.DEBUG_TAG, "List<TableEntity> has been recovered");
		}

		if (tableList == null) {
			String tablesUrl = UriStringHelper.buildUriString(this, "tables");
			if (TableListActivity.m_showAll) {
				tablesUrl = UriStringHelper.addParam(tablesUrl, "all", 1);
			}

			new HttpRequestAsyncTask(this, tablesUrl) {

				@Override
				protected Object process(JSONObject jsonObject) {
					List<TableEntity> tableList = new ArrayList<TableEntity>();
					try {
						JSONObject tables = jsonObject.getJSONObject("tables");
						JSONArray tableIds = tables.names();
						for (int i = 0; i < tableIds.length(); i++) {
							TableEntity table = new TableEntity();
							JSONObject jsonObject2 = tables
									.getJSONObject(tableIds.getString(i));
							table.parse(jsonObject2);
							tableList.add(table);

						}
					} catch (NullPointerException e) {
						Log
								.d(FdConfig.DEBUG_TAG,
										"getTables got NULL response");
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

					return tableList;
				}

				@Override
				protected void onSuccess(JSONObject jsonObject, Object processed) {
					if (processed != null && processed instanceof List<?>) {
						initLayout((List<TableEntity>) processed);
					}
				}

			}.execute();
		} else {
			initLayout(tableList);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (view instanceof TableView) {
			TableView tableView = (TableView) view;
			TableEntity table = tableView.table;

			Intent intent = new Intent();
			intent.putExtra(TableListActivity.ACTIVITY_RESULT_NAME_TABLE_OBJ,
					table);

			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}
}