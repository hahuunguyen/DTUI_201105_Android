package com.group5.android.fd.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group5.android.fd.R;
import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.entity.OrderItemEntity;

public class ConfirmView extends RelativeLayout {
	protected TextView m_vwQuantity;
	protected TextView m_vwName;
	protected Context m_context;
	public OrderItemEntity item;

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
	
	protected void setItemQuantity(int quantity){
		m_vwQuantity.setText(String.format("%s", quantity));
	}

	public void setOrderItem(OrderItemEntity item) {
		this.item = item;
		setItemName(item.itemName);
		setItemQuantity(item.quantity);
	}
}
