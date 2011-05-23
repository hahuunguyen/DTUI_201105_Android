package com.group5.android.fd.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
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
	public ArrayList<OrderItemEntity> existingOrderItems = new ArrayList<OrderItemEntity>();
	public ArrayList<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();

	/**
	 * Sets table for this order
	 * 
	 * @param table
	 * @return true if the set request is complete, false otherwise (normally
	 *         because it has to get existing order data)
	 */
	public boolean setTable(Context context, TableEntity table) {
		this.table = table;

		Log.i(FdConfig.DEBUG_TAG, "Order.setTable: " + table.tableName + " ("
				+ table.tableId + ")");

		if (table.isBusy) {
			getExistingData(context, table.lastOrderId);

			return false;
		}

		selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);

		return true;
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
			int existingPosition = existingOrderItems.indexOf(orderItem);
			if (existingPosition > -1) {
				OrderItemEntity newOrderItem = new OrderItemEntity();
				newOrderItem.parse(orderItem);
				newOrderItem.quantity = quantity - orderItem.quantity;
				newOrderItem.orderItemId = 0;

				if (newOrderItem.quantity > 0) {
					orderItems.add(newOrderItem);

					selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Adds a new {@link OrderItemEntity} to the order. If an existing order
	 * item is found with the same item id, it will be updated instead
	 * 
	 * @param orderItem
	 * @param list
	 */
	protected void addOrderItemIntoList(OrderItemEntity orderItem,
			List<OrderItemEntity> list) {
		if (orderItem.itemId > 0 && orderItem.quantity > 0) {
			// check if duplicate add more
			Iterator<OrderItemEntity> iterator = list.iterator();
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
				duplicateItem.orderItemId = Math.min(duplicateItem.orderItemId,
						orderItem.orderItemId);
			} else {
				list.add(orderItem);
			}
		} else {
			// do nothing
		}
	}

	/**
	 * Adds a new {@link OrderItemEntity}
	 * 
	 * @param orderItem
	 */
	public void addOrderItem(OrderItemEntity orderItem) {
		addOrderItemIntoList(orderItem, orderItems);

		Log.i(FdConfig.DEBUG_TAG, "OrderEntity.addOrderItem(): "
				+ orderItem.itemName + " (#" + orderItem.itemId
				+ ", quantity: " + orderItem.quantity + ", count: "
				+ orderItems.size() + ")");

		selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
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

		if (orderId > 0) {
			params.add(new BasicNameValuePair("order_id", String
					.valueOf(orderId)));
		}

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
		String submitUrl;
		if (orderId == 0) {
			submitUrl = UriStringHelper.buildUriString("new-order");
		} else {
			submitUrl = UriStringHelper.buildUriString("update-order");
		}
		List<NameValuePair> params = getOrderAsParams();

		new HttpRequestAsyncTask(context, submitUrl, csrfToken, params) {

			@Override
			protected Object process(JSONObject jsonObject) {
				try {
					JSONObject order = jsonObject.getJSONObject("order");
					orderId = order.getInt("order_id");

					if (orderId > 0) {
						// move ordered items into existing list
						Iterator<OrderItemEntity> iterator = orderItems
								.iterator();
						while (iterator.hasNext()) {
							addOrderItemIntoList(iterator.next(),
									existingOrderItems);
						}
						orderItems.clear();

						return true;
					}
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

	/**
	 * Gets existing order data from server
	 * 
	 * @param context
	 * @param existingOrderId
	 */
	protected void getExistingData(Context context, int existingOrderId) {
		String orderUrl = UriStringHelper.buildUriString("order/"
				+ existingOrderId);

		new HttpRequestAsyncTask(context, orderUrl) {
			@Override
			protected Object process(JSONObject jsonObject) {
				try {
					JSONObject order = jsonObject.getJSONObject("order");
					if (order.getInt("table_id") == table.tableId) {
						orderId = order.getInt("order_id");

						JSONObject orderItems = jsonObject
								.getJSONObject("orderItems");
						JSONArray orderItemIds = orderItems.names();

						ItemEntity item = new ItemEntity();
						for (int i = 0; i < orderItemIds.length(); i++) {
							JSONObject orderItemRaw = orderItems
									.getJSONObject(orderItemIds.getString(i));
							// get the item
							item.parse(orderItemRaw);
							// prepare the order item
							OrderItemEntity orderItem = new OrderItemEntity();
							orderItem.setup(item, 1, orderItemRaw
									.getInt("order_item_id"));
							// add to existing list
							addOrderItemIntoList(orderItem, existingOrderItems);
						}
					}

					Collections.sort(existingOrderItems,
							new Comparator<OrderItemEntity>() {
								@Override
								public int compare(OrderItemEntity orderItem1,
										OrderItemEntity orderItem2) {
									if (orderItem1.orderItemId == orderItem2.orderItemId) {
										return 0;
									} else {
										return orderItem1.orderItemId < orderItem2.orderItemId ? -1
												: 1;
									}
								}
							});

					return true;
				} catch (JSONException e) {
					// invalid response from server!
				}

				return false;
			}

			@Override
			protected void onSuccess(JSONObject jsonObject, Object processed) {
				selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
			}
		}.execute();
	}

	/**
	 * Gets the total price
	 * 
	 * @return
	 */
	public double getPriceTotal() {
		return getPriceTotalOfList(existingOrderItems)
				+ getPriceTotalOfList(orderItems);
	}

	/**
	 * Gets the total price of a <code>List</code> of {@link OrderItemEntity}
	 * 
	 * @param list
	 * @return the total price (price * quantity)
	 */
	protected double getPriceTotalOfList(List<OrderItemEntity> list) {
		double total = 0;

		Iterator<OrderItemEntity> iterator = list.iterator();
		OrderItemEntity item = null;

		while (iterator.hasNext()) {
			item = iterator.next();
			total += item.quantity * item.price;
		}

		return total;
	}
}
