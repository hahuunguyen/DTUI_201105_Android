package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.adapter.TableAdapter;
import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.helper.HttpHelper;
import com.group5.android.fd.helper.UriStringHelper;
import com.group5.android.fd.view.TableView;

public class TableListActivity extends ListActivity implements
		OnItemClickListener {
	private TableAdapter m_tableAdapter;
	private List<TableEntity> m_tableList = new ArrayList<TableEntity>();

	final public static String ACTIVITY_RESULT_NAME_TABLE_OBJ = "tableObj";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTables();
		m_tableAdapter = new TableAdapter(this, m_tableList);
		initLayout();
		initListeners();
	}

	private void getTables() {
		new AsyncTask<Void, Void, List<TableEntity>>() {
			@Override
			protected List<TableEntity> doInBackground(Void... params) {
				String tablesUrl = UriStringHelper.buildUriString("tables");
				JSONObject response = HttpHelper.get(TableListActivity.this,
						tablesUrl);
				List<TableEntity> tableList = new ArrayList<TableEntity>();
				try {
					JSONObject tables = response.getJSONObject("tables");
					JSONArray tableIds = tables.names();
					for (int i = 0; i < tableIds.length(); i++) {
						TableEntity table = new TableEntity();
						JSONObject jsonObject = tables.getJSONObject(tableIds
								.getString(i));
						table.parse(jsonObject);
						tableList.add(table);

					}
				} catch (NullPointerException e) {
					Log.d(FdConfig.DEBUG_TAG, "syncCategory got NULL response");
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return tableList;
			}

			@Override
			protected void onPostExecute(List<TableEntity> tables) {
				setTableList(tables);
			}
		}.execute();
	}

	private void setTableList(List<TableEntity> tables) {
		m_tableList = tables;
		m_tableAdapter.setNewTableList(m_tableList);
		getListView().postInvalidate();
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	protected void initLayout() {
		setListAdapter(m_tableAdapter);
	}

	protected void initListeners() {
		getListView().setOnItemClickListener(this);
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

			Log.i(FdConfig.DEBUG_TAG, "A table has been selected: "
					+ table.tableName);

			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}
}