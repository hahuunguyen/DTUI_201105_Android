package com.group5.android.fd.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.view.TableView;

public class TableAdapter extends BaseAdapter {
	private Context m_context;
	private List<TableEntity> m_tableList;

	public TableAdapter(Context context, List<TableEntity> tableList) {
		m_context = context;
		m_tableList = tableList;
	}

	public int getCount() {
		return m_tableList.size();
	}

	public Object getItem(int position) {
		return m_tableList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			return new TableView(m_context, m_tableList.get(position));
		} else {
			TableView tableView = (TableView) convertView;
			tableView.setTable(m_tableList.get(position));

			return tableView;

		}
	}

	public void setNewTableList(List<TableEntity> tables) {
		m_tableList = tables;
		notifyDataSetChanged();

		Log.i(FdConfig.DEBUG_TAG, "TableAdapter.setNewTableList()");
	}
}
