package com.group5.android.fd.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group5.android.fd.R;

abstract public class Abstract extends RelativeLayout {
	protected Button m_vwSelect;
	protected TextView m_vwName;

	public Abstract(Context context, String text) {
		super(context);

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.view_item, this, true);
		m_vwSelect = (Button) findViewById(R.id.btnItemSelect);
		m_vwName = (TextView) findViewById(R.id.txtItemName);
		m_vwName.setText(text);
	}

	public void setTextView(String text) {
		m_vwName.setText(text);
	}
}
