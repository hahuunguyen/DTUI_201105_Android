package com.group5.android.fd.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.view.TableView;

public class TableAdapter extends BaseAdapter {
	protected Context m_context;
	protected List<TableEntity> m_tableList;

	public TableAdapter(Context context, List<TableEntity> tableList) {
		m_context = context;
		m_tableList = tableList;
	}

	public List<TableEntity> getTableList() {
		return m_tableList;
	}

	@Override
	public int getCount() {
		return m_tableList.size();
	}

	@Override
	public Object getItem(int position) {
		return m_tableList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			return new TableView(m_context, m_tableList.get(position));
		} else {
			TableView tableView = (TableView) convertView;
			tableView.setTable(m_tableList.get(position));

			return tableView;

		}
	}
}
