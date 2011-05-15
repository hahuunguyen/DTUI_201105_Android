package com.group5.android.fd;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.view.Table;

public class TableListAdapter extends BaseAdapter implements OnItemClickListener{
	private Context m_context;
	private List<TableEntity> m_tableList;
	private int m_nSelectedPosition;
	
	public TableListAdapter( Context context, List<TableEntity> tableList) {
		m_context = context;
		m_tableList = tableList;
		m_nSelectedPosition = Adapter.NO_SELECTION;

	}
	
	public int getSelectedPosition(){
		return m_nSelectedPosition;
	}
	
	public int getCount(){
		Log.v(FdConfig.DEBUG_TAG, "counted");
		return m_tableList.size();
	}
	
	public Object getItem(int position){
		return m_tableList.get(position);
	}
	
	public long getItemId(int position){
		return position;
	}
	
	public View getView (int position, View convertView, ViewGroup parent){
		if ( convertView == null){
			return new Table(m_context,m_tableList.get(position).getName());
		}
		else {
			Table temp = (Table) convertView;
			temp.setTextView(m_tableList.get(position).getName());
			return temp;
			
		}
	}
	
	public void setNewTableList(List<TableEntity> tables){
		m_tableList = tables;
		notifyDataSetChanged();
		Log.v(FdConfig.DEBUG_TAG, "setNewTableList");
	}
	
	public void onItemClick (AdapterView<?> parent, View view, int position, long id){
		Log.v(FdConfig.DEBUG_TAG, "onclick");
		m_nSelectedPosition = position;
		Intent intent = new Intent();
		TableEntity tableObject= (TableEntity)getItem(position);
		intent.putExtra(TableEntity.TABLE_ENTITY_NAME, tableObject.tableName );
		if ( m_context instanceof Activity){
			Activity tableActivity = (Activity)m_context;
			tableActivity.setResult(Activity.RESULT_OK, intent);
			tableActivity.finish();
		}
		
	}
}
