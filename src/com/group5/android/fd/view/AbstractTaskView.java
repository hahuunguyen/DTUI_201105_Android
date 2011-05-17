package com.group5.android.fd.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group5.android.fd.R;

abstract public class AbstractTaskView extends RelativeLayout {
	protected CheckBox m_chk;
	protected TextView m_vwName;
	protected Context m_context;

	public AbstractTaskView(Context context) {
		super(context);
		m_context = context;
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.view_task, this, true);
		m_chk = (CheckBox) findViewById(R.id.check);
		m_vwName = (TextView) findViewById(R.id.txtItemName);
	}

	protected void setTextView(String text) {
		m_vwName.setText(text);
	}
}
