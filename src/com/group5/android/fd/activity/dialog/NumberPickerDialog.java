package com.group5.android.fd.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.group5.android.fd.R;

public class NumberPickerDialog extends Dialog implements OnClickListener {
	protected EditText m_vwQuantity;
	protected Button m_vwPlus;
	protected Button m_vwSubtract;
	protected Button m_vwbtnSet;
	protected Button m_vwCancel;

	/*
	 * oldQuantity va quantity luu tru gia tri so luong truoc va sau khi duoc
	 * thay doi
	 */
	protected int oldQuantity = -1;
	protected int quantity = -1;

	public NumberPickerDialog(Context context) {
		super(context);

		initLayout();
	}

	protected void initLayout() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_quantity_set);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		m_vwQuantity = (EditText) findViewById(R.id.txtQuantity);
		m_vwPlus = (Button) findViewById(R.id.btnPlus);
		m_vwSubtract = (Button) findViewById(R.id.btnSubtract);
		m_vwbtnSet = (Button) findViewById(R.id.btnSet);
		m_vwCancel = (Button) findViewById(R.id.btnCancel);

		/*
		 * thiet lap gia tri mac dinh cho text hien thi o EditText
		 */

		m_vwbtnSet.setOnClickListener(this);
		m_vwPlus.setOnClickListener(this);
		m_vwSubtract.setOnClickListener(this);
		m_vwCancel.setOnClickListener(this);
	}

	protected void onQuantityChange() {
		oldQuantity = Integer.valueOf(m_vwQuantity.getText().toString());
		quantity = oldQuantity;
	}

	@Override
	public void onClick(View v) {

	}

	public int getQuantity() {
		try {
			// int quantity =
			// Integer.valueOf(m_vwQuantity.getText().toString());
			return quantity;

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			quantity = oldQuantity;
			dismiss();
		}
		return true;
	}

}
