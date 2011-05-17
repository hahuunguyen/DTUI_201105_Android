package com.group5.android.fd.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskEntity extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int orderItemId;
	public int orderId;
	public int triggerUserId;
	public int targetUserId;
	public int itemId;
	public int orderItemDate;
	public int status;
	public String itemName;

	final public static int STATUS_WAITING = 0;
	final public static int STATUS_SERVED = 1;
	final public static int STATUS_PAID = 2;

	public void parse(JSONObject jsonObject) throws JSONException {
		orderItemId = jsonObject.getInt("order_item_id");
		orderId = jsonObject.getInt("order_id");
		triggerUserId = jsonObject.getInt("trigger_user_id");
		targetUserId = jsonObject.getInt("target_user_id");
		itemId = jsonObject.getInt("item_id");
		orderItemDate = jsonObject.getInt("order_item_date");
		itemName = jsonObject.getString("item_name");

		if (jsonObject.getString("status").equalsIgnoreCase("waiting")) {
			status = TaskEntity.STATUS_WAITING;
		} else if (jsonObject.getString("status").equalsIgnoreCase("served")) {
			status = TaskEntity.STATUS_SERVED;
		} else {
			status = TaskEntity.STATUS_PAID;
		}
	}

}
