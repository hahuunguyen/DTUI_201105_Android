package com.group5.android.fd.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group5.android.fd.R;
import com.group5.android.fd.entity.OrderItemEntity;

public class ConfirmView extends RelativeLayout {
	protected TextView m_vwQuantity;
	protected TextView m_vwName;
	protected Context m_context;

	protected OrderItemEntity m_item;

	public ConfirmView(Context context, OrderItemEntity item) {
		super(context);

		m_context = context;
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.view_confirm, this, true);
		m_vwQuantity = (TextView) findViewById(R.id.btnItemQuantity);
		m_vwName = (TextView) findViewById(R.id.txtItemName);

		setOrderItem(item);
	}

	protected void setItemName(String text) {
		m_vwName.setText(text);
	}

	protected void setItemQuantity(int quantity, double price) {
		m_vwQuantity.setText(String.format("%s x %s", quantity, price));
	}

	public void setOrderItem(OrderItemEntity item) {
		m_item = item;

		setItemName(item.itemName);
		setItemQuantity(item.quantity, item.price);
	}

	public OrderItemEntity getOrderItem() {
		return m_item;
	}
}
