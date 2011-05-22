package com.group5.android.fd.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;

/**
 * An order
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class OrderEntity extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2436126416686043271L;

	// an OrderEntity has information about table and items for this table
	public int orderId = 0;
	public TableEntity table = null;
	public ArrayList<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();

	/**
	 * Sets table for this order
	 * 
	 * @param table
	 */
	public void setTable(TableEntity table) {
		this.table = table;

		Log.i(FdConfig.DEBUG_TAG, "Order.setTable: " + table.tableName + " ("
				+ table.tableId + ")");

		selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
	}

	/**
	 * Gets the id of the current table for this order. If no table has been
	 * set, it will return 0
	 * 
	 * @return set table's id or 0
	 */
	public int getTableId() {
		if (table != null) {
			return table.tableId;
		} else {
			return 0;
		}
	}

	/**
	 * Gets the id of the current table for this order.
	 * 
	 * @return set table's name
	 */
	public String getTableName() {
		if (table != null) {
			return table.tableName;
		} else {
			return "";
		}
	}

	/**
	 * Update the quantity of an {@link OrderItemEntity}. If the quantity
	 * happens to be 0, the order item will be removed
	 * 
	 * @param orderItem
	 * @param quantity
	 * @return true if the change is applied
	 */
	public boolean setOrderItemQuantity(OrderItemEntity orderItem, int quantity) {
		int position = orderItems.indexOf(orderItem);

		// add if exists and more than 0, remove if exists and less than 0
		if (position > -1) {
			if (quantity <= 0) {
				orderItems.remove(position);
			} else {
				OrderItemEntity order = orderItems.get(position);
				order.quantity = quantity;
			}

			selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds a new {@link OrderItemEntity} to the order. If an existing order
	 * item is found with the same item id, it will be updated instead
	 * 
	 * @param orderItem
	 */
	public void addOrderItem(OrderItemEntity orderItem) {
		if (orderItem.itemId > 0 && orderItem.quantity > 0) {
			// check if duplicate add more
			Iterator<OrderItemEntity> iterator = orderItems.iterator();
			OrderItemEntity existingItem = null;
			OrderItemEntity duplicateItem = null;

			while (iterator.hasNext()) {
				existingItem = iterator.next();

				if (existingItem.itemId == orderItem.itemId) {
					duplicateItem = existingItem;
					break; // get better performance here, a little
				}
			}

			if (duplicateItem != null) {
				duplicateItem.quantity += orderItem.quantity;
			} else {
				orderItems.add(orderItem);
			}

			Log.i(FdConfig.DEBUG_TAG, "Order.addItem: " + orderItem.itemName
					+ " (#" + orderItem.itemId + ", quantity: "
					+ orderItem.quantity + ", total items now: "
					+ orderItems.size() + ")");

			selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
		} else {
			// do nothing
		}
	}

	/**
	 * Adds a new {@link OrderItemEntity} of a {@link ItemEntity} with the
	 * quality of 1
	 * 
	 * @param item
	 */
	public void addItem(ItemEntity item) {
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setup(item, 1);

		addOrderItem(orderItem);
	}

	/**
	 * Gets a <code>List</code> of <code>NameValuePair</code> ready to be POST
	 * to server
	 * 
	 * @return the list
	 */
	protected List<NameValuePair> getOrderAsParams() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		if (table != null) {
			params.add(new BasicNameValuePair("table_id", String
					.valueOf(table.tableId)));
		}

		if (!orderItems.isEmpty()) {
			Iterator<OrderItemEntity> i = orderItems.iterator();
			int count = 0;
			while (i.hasNext()) {
				OrderItemEntity orderItem = i.next();
				// if quantity is n, add n item which amount is one
				for (int j = 0; j < orderItem.quantity; j++) {
					params.add(new BasicNameValuePair("item_ids[" + count++
							+ "]", String.valueOf(orderItem.itemId)));
				}
			}
		}

		return params;
	}

	/**
	 * Submits the order to server using {@link HttpRequestAsyncTask}
	 * 
	 * @param context
	 * @param csrfToken
	 */
	public void submit(Context context, String csrfToken) {
		String newOrderUrl = UriStringHelper.buildUriString("new-order");
		List<NameValuePair> params = getOrderAsParams();

		new HttpRequestAsyncTask(context, newOrderUrl, csrfToken, params) {

			@Override
			protected Object process(JSONObject jsonObject) {
				try {
					JSONObject order = jsonObject.getJSONObject("order");
					orderId = order.getInt("order_id");

					return orderId > 0;
				} catch (JSONException e) {
					// invalid response from server!
				}

				return false;
			}

			@Override
			protected void onSuccess(JSONObject jsonObject, Object processed) {
				boolean confirmed = false;

				if (processed != null && processed instanceof Boolean) {
					confirmed = (Boolean) processed;
				}

				if (confirmed) {
					onUpdated(AbstractEntity.TARGET_REMOTE_SERVER);
				} else {
					selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
				}
			}
		}.execute();
	}

	/*
	 * calculate total price
	 */
	public double getPriceTotal() {
		double total = 0;

		Iterator<OrderItemEntity> iterator = orderItems.iterator();
		OrderItemEntity item = null;

		while (iterator.hasNext()) {
			item = iterator.next();
			total += item.quantity * item.price;
		}

		return total;
	}
}
