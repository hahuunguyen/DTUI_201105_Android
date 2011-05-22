package com.group5.android.fd.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.view.TableView;

/**
 * Adapter for {@link TableEntity}s
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class TableAdapter extends BaseAdapter {

	protected Context m_context;
	// list of table from server
	protected List<TableEntity> m_tableList;

	/**
	 * Constructs itself
	 * 
	 * @param context
	 * @param tableList
	 */
	public TableAdapter(Context context, List<TableEntity> tableList) {
		m_context = context;
		m_tableList = tableList;
	}

	/**
	 * Gets a <code>List</code> of {@link TableEntity}
	 * 
	 * @return the list
	 */
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
