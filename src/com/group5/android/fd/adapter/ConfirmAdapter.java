package com.group5.android.fd.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.group5.android.fd.entity.OrderItemEntity;
import com.group5.android.fd.view.ConfirmView;

public class ConfirmAdapter extends BaseAdapter {
	private Context m_context;
	private List<OrderItemEntity> m_orderItems;

	public ConfirmAdapter(Context context, List<OrderItemEntity> orderItems) {
		m_context = context;
		m_orderItems = orderItems;
	}

	public int getCount() {
		return m_orderItems.size();
	}

	public Object getItem(int position) {
		return m_orderItems.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			return new ConfirmView(m_context, m_orderItems.get(position));
		} else {
			ConfirmView confirmView = (ConfirmView) convertView;
			confirmView.setOrderItem(m_orderItems.get(position));

			return confirmView;

		}
	}

}
