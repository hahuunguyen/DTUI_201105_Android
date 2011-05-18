package com.group5.android.fd.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.entity.OrderEntity;
import com.group5.android.fd.view.ConfirmView;

public class ConfirmAdapter extends BaseAdapter implements
		OnItemLongClickListener {
	private final Context m_context;
	private final OrderEntity m_order;
	private int m_nSelectedPosition;

	public ConfirmAdapter(Context context, OrderEntity order) {
		m_context = context;
		m_order = order;
		m_nSelectedPosition = Adapter.NO_SELECTION;
	}

	@Override
	public int getCount() {
		return m_order.orderItems.size();
	}

	@Override
	public Object getItem(int position) {
		return m_order.orderItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			return new ConfirmView(m_context, m_order.orderItems.get(position));
		} else {
			ConfirmView confirmView = (ConfirmView) convertView;
			confirmView.setOrderItem(m_order.orderItems.get(position));

			return confirmView;

		}
	}

	/*
	 * get position of Item which is selected
	 */
	public int getSelectedPosition() {
		return m_nSelectedPosition;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		m_nSelectedPosition = position;
		Log.v(FdConfig.DEBUG_TAG, "position to remove:" + m_nSelectedPosition);
		return false;

	}

}
