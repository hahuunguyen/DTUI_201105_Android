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

import com.group5.android.fd.activity.dialog.NumberPickerDialog;
import com.group5.android.fd.adapter.FdCursorAdapter;
import com.group5.android.fd.adapter.ItemAdapter;
import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.entity.OrderItemEntity;
import com.group5.android.fd.view.ItemView;

/**
 * The activity to display a list of items of a category
 * 
 * @author Nguyen Huu Ha
 * 
 */
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

	/**
	 * Finishes the activity with an item and a quantity. It simply creates a
	 * new {@link OrderItemEntity} and pass it to
	 * {@link #finish(OrderItemEntity)}
	 * 
	 * @param item
	 *            the selected <code>ItemEntity</code>
	 * @param quantity
	 *            the chosen quantity
	 * @see #finish()
	 */
	public void finish(ItemEntity item, int quantity) {
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setup(item, quantity);

		finish(orderItem);
	}

	/**
	 * Finishes the activity with an order item (item with a quantity).
	 * 
	 * @param orderItem
	 *            the constructed <code>OrderItemEntity</code>
	 * @see #finish()
	 * @see #finish(ItemEntity, int)
	 */
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
			dialog = new NumberPickerDialog(this);
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
			((NumberPickerDialog) dialog).setEntity(item);
			break;
		}
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		if (arg0 instanceof NumberPickerDialog) {
			NumberPickerDialog numberPickerDialog = (NumberPickerDialog) arg0;
			ItemEntity item = (ItemEntity) numberPickerDialog.getEntity();
			if (numberPickerDialog.isSet()) {
				OrderItemEntity orderItem = new OrderItemEntity();
				orderItem.setup(item, numberPickerDialog.getQuantity());
				finish(orderItem);
			}

		}
	}
}
