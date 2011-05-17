package com.group5.android.fd.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group5.android.fd.R;

abstract public class AbstractView extends RelativeLayout {
	protected Button m_vwSelect;
	protected TextView m_vwName;
	protected Context m_context;

	public AbstractView(Context context) {
		super(context);
		m_context = context;
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.view_item, this, true);
		m_vwSelect = (Button) findViewById(R.id.btnItemSelect);
		m_vwName = (TextView) findViewById(R.id.txtItemName);
	}

	protected void setTextView(String text) {
		m_vwName.setText(text);
	}
	
	protected void setTextView(int index) {
		m_vwName.setText(index);
	}
}
