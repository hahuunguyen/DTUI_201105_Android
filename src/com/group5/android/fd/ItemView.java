package com.group5.android.fd;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ItemView extends RelativeLayout{
	protected Button selectButton;
	protected TextView itemTextView;
	
	public ItemView ( Context context, String text){
		super(context);
		((LayoutInflater)context
				 .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				 .inflate(R.layout.item_view, this, true);
		selectButton = (Button)findViewById (R.id.selectButton);
		itemTextView = (TextView) findViewById ( R.id.itemTextView);
		itemTextView.setText(text);
	}
	
	public void setTextView ( String text){
		itemTextView.setText(text);
	}
}
