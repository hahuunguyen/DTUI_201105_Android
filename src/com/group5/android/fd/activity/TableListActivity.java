package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;
import com.group5.android.fd.TableListAdapter;
import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.helper.HttpHelper;
import com.group5.android.fd.helper.UriStringHelper;

public class TableListActivity extends ListActivity {
	private TableListAdapter m_tableAdapter;
	private List<TableEntity> m_tableList = new ArrayList<TableEntity>();
	private Button btnItemSelect; 
	
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getTables();
		m_tableAdapter =  new TableListAdapter(this,m_tableList);
		initLayout();
		initListeners();
	}
	
	private void getTables(){
		new AsyncTask<Void, Void, List<TableEntity>>(){
			@Override
			protected List<TableEntity> doInBackground(Void... params){
				String tablesUrl = UriStringHelper.buildUriString("tables");
				JSONObject response = HttpHelper.get(TableListActivity.this, tablesUrl);
				List<TableEntity> tableList = new ArrayList<TableEntity>();
				try {
					JSONObject tables= response.getJSONObject("tables");
					JSONArray tableIds = tables.names();
					for (int i = 0; i < tableIds.length(); i++) {
						TableEntity table = new TableEntity();
						JSONObject jsonObject = tables.getJSONObject(tableIds
								.getString(i));
						table.parse(jsonObject);
						tableList.add(table);

						Log.i(FdConfig.DEBUG_TAG, "got table: " + table.tableName);
						
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
			
			protected void onPostExecute(List<TableEntity> tables) {
				setTableList (tables);
			}
		}.execute();
	}
	
	private void setTableList(List<TableEntity> tables){
		m_tableList = tables;
		m_tableAdapter.setNewTableList(m_tableList);
		getListView().postInvalidate();
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}
		
	protected  void initLayout(){
		setListAdapter(m_tableAdapter);
		btnItemSelect = (Button)findViewById(R.id.btnItemSelect);
	}
	protected void initListeners(){
		this.getListView().setOnItemClickListener(m_tableAdapter);
	}
}