package com.group5.android.fd.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group5.android.fd.R;

abstract public class AbstractView extends RelativeLayout {
	protected Button m_vwSelect;
	protected TextView m_vwName;
	protected Context m_context;
	protected ImageView m_vwImg;

	public AbstractView(Context context) {
		super(context);
		m_context = context;
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.view_item, this, true);
		m_vwSelect = (Button) findViewById(R.id.btnItemSelect);
		m_vwName = (TextView) findViewById(R.id.txtItemName);
		m_vwImg = (ImageView) findViewById(R.id.imgItem);
	}

	protected void setTextView(String text) {
		m_vwName.setText(text);
	}

	protected void setTextView(int index) {
		m_vwName.setText(index);
	}

	protected abstract void setImg(String url, ImageView imgView, int type);
}
