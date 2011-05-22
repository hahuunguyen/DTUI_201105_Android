package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

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
public class TableListActivity extends ListActivity implements
		OnItemClickListener, HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller {

	final public static String ACTIVITY_RESULT_NAME_TABLE_OBJ = "tableObj";

	protected TableAdapter m_tableAdapter;

	protected HttpRequestAsyncTask m_hrat = null;

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

	@Override
	protected void onPause() {
		super.onPause();

		if (m_hrat != null) {
			m_hrat.dismissProgressDialog();
		}
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
		setContentView(R.layout.activity_list);

		m_tableAdapter = new TableAdapter(this, tableList);
		setListAdapter(m_tableAdapter);

		ListView listView = getListView();
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(this);
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
			String tablesUrl = UriStringHelper.buildUriString("tables");

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

	@Override
	public void addHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat != null && m_hrat != hrat) {
			m_hrat.dismissProgressDialog();
		}

		m_hrat = hrat;
	}

	@Override
	public void removeHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat == hrat) {
			m_hrat = null;
		}
	}
}