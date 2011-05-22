package com.group5.android.fd.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group5.android.fd.R;
import com.group5.android.fd.entity.OrderItemEntity;
import com.group5.android.fd.helper.FormattingHelper;

public class ConfirmView extends RelativeLayout {
	protected TextView m_vwQuantity;
	protected TextView m_vwName;
	protected Context m_context;

	public OrderItemEntity orderItem;

	/**
	 * Constructs itself. Get references of subviews.
	 * 
	 * @param context
	 * @param item
	 */
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

	/**
	 * Setup the view to display a new {@link OrderItemEntity}
	 * 
	 * @param orderItem
	 *            the new order item
	 */
	public void setOrderItem(OrderItemEntity orderItem) {
		this.orderItem = orderItem;

		m_vwName.setText(orderItem.itemName);
		m_vwQuantity.setText(String.format("%s x %s", orderItem.quantity,
				FormattingHelper.formatPrice(orderItem.price)));

	}
}
