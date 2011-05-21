package com.group5.android.fd.view;

import android.content.Context;

import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.helper.FormattingHelper;

public class ItemView extends AbstractView {
	public ItemEntity item;

	public ItemView(Context context, ItemEntity item) {
		super(context);

		setItem(item);
	}

	public void setItem(ItemEntity item) {
		this.item = item;

		setTextViews(item.itemName, FormattingHelper.formatPrice(item.price));
		setImage(chooseImageSize(item));
	}

}
