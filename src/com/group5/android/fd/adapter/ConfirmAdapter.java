package com.group5.android.fd.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.group5.android.fd.entity.OrderEntity;
import com.group5.android.fd.view.ConfirmView;

public class ConfirmAdapter extends BaseAdapter {
	private final Context m_context;
	private final OrderEntity m_order;

	public ConfirmAdapter(Context context, OrderEntity order) {
		m_context = context;
		m_order = order;
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
			return new ConfirmView(m_context, m_order.orderItems.get(position));
			/*
			 * ConfirmView confirmView = (ConfirmView) convertView;
			 * confirmView.setOrderItem(m_order.orderItems.get(position));
			 * 
			 * return confirmView;
			 */

		}
	}

}
