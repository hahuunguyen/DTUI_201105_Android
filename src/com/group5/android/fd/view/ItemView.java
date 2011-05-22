package com.group5.android.fd.view;

import android.content.Context;

import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.helper.FormattingHelper;

/**
 * A view for {@link ItemEntity}
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class ItemView extends AbstractView {
	public ItemEntity item;

	public ItemView(Context context, ItemEntity item) {
		super(context);

		setItem(item);
	}

	/**
	 * Setup the view to display a new {$link ItemEntity}
	 * 
	 * @param item
	 *            the new item
	 */
	public void setItem(ItemEntity item) {
		this.item = item;

		setTextViews(item.itemName, FormattingHelper.formatPrice(item.price));
		setImage(item);
	}

}
