package com.group5.android.fd.view;

import android.content.Context;

import com.group5.android.fd.entity.ItemEntity;

public class ItemView extends AbstractView {
	public ItemEntity item;

	public ItemView(Context context, ItemEntity item) {
		super(context);
		setItem(item);
	}

	public void setItem(ItemEntity item) {
		this.item = item;
		setTextView(item.itemName);
		setImage(chooseImageSize(item), m_vwImg);
	}

}
