package com.group5.android.fd.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.group5.android.fd.R;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.entity.OrderItemEntity;

public class NumberPickerDialog extends Dialog implements OnClickListener,
		OnKeyListener {
	protected EditText m_vwQuantity;
	protected Button m_vwPlus;
	protected Button m_vwSubtract;
	protected Button m_vwSet;
	protected Button m_vwCancel;
	protected boolean m_isSet = false;
	protected AbstractEntity m_entity = null;

	/*
	 * oldQuantity va quantity luu tru gia tri so luong truoc va sau khi duoc
	 * thay doi
	 */

	public NumberPickerDialog(Context context) {
		super(context);
		initLayout();
	}

	protected void initLayout() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_number_picker);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		m_vwQuantity = (EditText) findViewById(R.id.txtQuantity);
		m_vwPlus = (Button) findViewById(R.id.btnPlus);
		m_vwSubtract = (Button) findViewById(R.id.btnSubtract);
		m_vwSet = (Button) findViewById(R.id.btnSet);
		m_vwCancel = (Button) findViewById(R.id.btnCancel);

		/*
		 * thiet lap gia tri mac dinh cho text hien thi o EditText
		 */

		m_vwQuantity.setOnKeyListener(this);
		m_vwPlus.setOnClickListener(this);
		m_vwSubtract.setOnClickListener(this);
		m_vwSet.setOnClickListener(this);
		m_vwCancel.setOnClickListener(this);
	}

	public void setEntity(AbstractEntity entity) {
		m_entity = entity;
		if (entity instanceof ItemEntity) {
			setQuantity(2);

		} else if (entity instanceof OrderItemEntity) {
			setQuantity(((OrderItemEntity) m_entity).quantity);
		}

	}

	public AbstractEntity getEntity() {
		return m_entity;
	}

	protected void setQuantity(int quantity) {
		m_vwQuantity.setText(String.valueOf(quantity));
		m_vwQuantity.selectAll();
		m_isSet = false;
	}

	public int getQuantity() {
		try {
			int quantity = Integer.valueOf(m_vwQuantity.getText().toString());
			if (quantity > 50) {
				Toast.makeText(getContext(),
						R.string.numberpickerdialog_invalid_number_entered,
						Toast.LENGTH_SHORT).show();
				return 50;
			} else {
				return quantity;
			}

		} catch (NumberFormatException e) {
			Toast.makeText(getContext(),
					R.string.numberpickerdialog_invalid_number_entered,
					Toast.LENGTH_SHORT).show();
			return 0;
		}
	}

	public boolean isSet() {
		return m_isSet;
	}

	public void triggerSet() {
		m_isSet = true;
		dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSet:
			triggerSet();
			break;
		case R.id.btnPlus:
			setQuantity(getQuantity() + 1);
			break;
		case R.id.btnSubtract:
			setQuantity(Math.max(0, getQuantity() - 1));
			break;
		case R.id.btnCancel:
			dismiss();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dismiss();
			return true;
		}

		return false;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_ENTER) {
			triggerSet();
			return true;
		}

		return false;
	}
}
