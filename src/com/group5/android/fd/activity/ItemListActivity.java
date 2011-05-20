package com.group5.android.fd.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.group5.android.fd.activity.dialog.QuantitySelectorDialog;
import com.group5.android.fd.adapter.FdCursorAdapter;
import com.group5.android.fd.adapter.ItemAdapter;
import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.entity.OrderItemEntity;
import com.group5.android.fd.view.ItemView;

public class ItemListActivity extends DbBasedActivity implements
		OnDismissListener {
	final public static String EXTRA_DATA_NAME_CATEGORY_ID = "categoryId";
	final public static int DIALOG_QUANTITY_SELECTOR = 1;
	final public static String DIALOG_QUANTITY_SELECTOR_DUNBLE_NAME_ITEM_OBJ = "itemObj";
	public static final String ACTIVITY_RESULT_NAME_ORDER_ITEM_OBJ = "orderItemObj";

	@Override
	protected Cursor initCursor() {
		Intent intent = getIntent();
		int categoryId = intent.getIntExtra(
				ItemListActivity.EXTRA_DATA_NAME_CATEGORY_ID, 0);

		return m_dbAdapter.getItems(categoryId);
	}

	@Override
	protected FdCursorAdapter initAdapter() {
		return new ItemAdapter(this, m_cursor);
	}

	public void finish(ItemEntity item, int quantity) {
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setup(item, quantity);

		finish(orderItem);
	}

	public void finish(OrderItemEntity orderItem) {
		if (orderItem == null) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		} else {
			Intent intent = new Intent();
			intent.putExtra(
					ItemListActivity.ACTIVITY_RESULT_NAME_ORDER_ITEM_OBJ,
					orderItem);

			setResult(Activity.RESULT_OK, intent);
			finish();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (view instanceof ItemView) {
			ItemView itemView = (ItemView) view;
			ItemEntity item = itemView.item;

			finish(item, 1);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (view instanceof ItemView) {
			ItemView itemView = (ItemView) view;
			ItemEntity item = itemView.item;

			Bundle args = new Bundle();
			args
					.putSerializable(
							ItemListActivity.DIALOG_QUANTITY_SELECTOR_DUNBLE_NAME_ITEM_OBJ,
							item);

			showDialog(ItemListActivity.DIALOG_QUANTITY_SELECTOR, args);

			return true;
		}

		return super.onItemLongClick(parent, view, position, id);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		switch (id) {
		case DIALOG_QUANTITY_SELECTOR:
			dialog = new QuantitySelectorDialog(this);
			dialog.setOnDismissListener(this);
			break;
		}

		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		switch (id) {
		case DIALOG_QUANTITY_SELECTOR:
			ItemEntity item = (ItemEntity) args
					.getSerializable(ItemListActivity.DIALOG_QUANTITY_SELECTOR_DUNBLE_NAME_ITEM_OBJ);
			((QuantitySelectorDialog) dialog).setItem(item);
			break;
		}
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		if (arg0 instanceof QuantitySelectorDialog) {
			OrderItemEntity orderItem = ((QuantitySelectorDialog) arg0)
					.getOrderItem();
			if (orderItem != null) {
				finish(orderItem);
			}

		}
	}
}
