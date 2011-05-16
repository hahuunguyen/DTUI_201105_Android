package com.group5.android.fd.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.group5.android.fd.FdConfig;

public class OrderEntity extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2436126416686043271L;

	public TableEntity table = null;
	public ArrayList<OrderItemEntity> orderItems = new ArrayList<OrderItemEntity>();

	public void setTable(TableEntity table) {
		this.table = table;

		Log.i(FdConfig.DEBUG_TAG, "Order.setTable: " + table.tableName + " ("
				+ table.tableId + ")");
	}

	public int getTableId() {
		if (table != null) {
			return table.tableId;
		} else {
			return 0;
		}
	}

	public String getTableName() {
		if (table != null) {
			return table.tableName;
		} else {
			return "";
		}
	}

	public List<OrderItemEntity> getOrderItems() {
		return orderItems;
	}

	/*
	 * them vao 1 item
	 */
	public void addOrderItem(OrderItemEntity orderItem) {
		if (orderItem.itemId > 0 && orderItem.quantity > 0) {
			orderItems.add(orderItem);

			Log.i(FdConfig.DEBUG_TAG, "Order.addItem: " + orderItem.itemName
					+ " (#" + orderItem.itemId + ", quantity: "
					+ orderItem.quantity + ", total items now: "
					+ orderItems.size() + ")");
		} else {
			// do nothing
		}
	}

	/*
	 * tra ve list kieu NameValuePair duoc su dung de post du lieu cua 1 order
	 * len server
	 */
	public List<NameValuePair> getOrderAsParams() {
		if (orderItems.isEmpty()) {
			// an order without any items? INVALID!
			return null;
		} else {
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("table_id", String
					.valueOf(table.tableId)));

			Iterator<OrderItemEntity> i = orderItems.iterator();
			int count = 0;
			while (i.hasNext()) {
				OrderItemEntity orderItem = i.next();
				for (int j = 0; j < orderItem.quantity; j++) {
					params.add(new BasicNameValuePair("item_ids[" + count++
							+ "]", String.valueOf(orderItem.itemId)));
				}
			}

			return params;
		}
	}
}
