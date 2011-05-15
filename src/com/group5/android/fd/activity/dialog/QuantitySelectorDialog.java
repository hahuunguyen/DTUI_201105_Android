package com.group5.android.fd.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.group5.android.fd.R;
import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.entity.OrderItemEntity;

public class QuantitySelectorDialog extends Dialog implements OnClickListener {
	protected EditText m_vwQuantity;
	protected TextView m_vwItemName;

	protected ItemEntity item = null;

	public QuantitySelectorDialog(Context context) {
		super(context);

		initLayout();
	}

	protected void initLayout() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_quantity_selector);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		m_vwQuantity = (EditText) findViewById(R.id.txtQuantity);
		m_vwItemName = (TextView) findViewById(R.id.txtItemName);
		m_vwItemName.setText("");
		Button btn = (Button) findViewById(R.id.btnOrder);
		btn.setOnClickListener(this);
	}

	public void setItem(ItemEntity item) {
		this.item = item;
		m_vwItemName.setText(item.itemName);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOrder:
			if (getQuantity() == 0) {
				Toast
						.makeText(
								getContext(),
								R.string.quantityselectordialog_please_enter_a_valid_quantity,
								Toast.LENGTH_SHORT);
				m_vwQuantity.requestFocus();
			} else {
				dismiss();
			}
			break;
		}
	}

	public int getQuantity() {
		try {
			int quantity = Integer.valueOf(m_vwQuantity.getText().toString());
			if (quantity > 0) {
				return quantity;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public OrderItemEntity getOrderItem() {
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setup(item, getQuantity());

		return orderItem;
	}
}
