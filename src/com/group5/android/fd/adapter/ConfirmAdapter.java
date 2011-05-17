package com.group5.android.fd.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.group5.android.fd.entity.OrderEntity;
import com.group5.android.fd.view.ConfirmView;

public class ConfirmAdapter extends BaseAdapter {
	private Context m_context;
	private OrderEntity m_order;

	public ConfirmAdapter(Context context, OrderEntity order) {
		m_context = context;
		m_order = order;
	}

	public int getCount() {
		return m_order.orderItems.size();
	}

	public Object getItem(int position) {
		return m_order.orderItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			return new ConfirmView(m_context, m_order.orderItems.get(position));
		} else {
			ConfirmView confirmView = (ConfirmView) convertView;
			confirmView.setOrderItem(m_order.orderItems.get(position));

			return confirmView;

		}
	}

}
