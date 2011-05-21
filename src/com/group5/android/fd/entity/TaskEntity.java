package com.group5.android.fd.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.group5.android.fd.helper.TaskRequestHelper;

public class TaskEntity extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int orderItemId;
	public int orderId;
	public int targetUserId;
	public int itemId;
	public int lastUpdated;
	public int status;

	public int groupId = 0;
	public String itemName = "";
	public double price = 0;
	public String tableName = "";

	final public static int STATUS_WAITING = 0;
	final public static int STATUS_PREPARED = 1;
	final public static int STATUS_SERVED = 2;
	final public static int STATUS_PAID = 3;

	public void parse(JSONObject jsonObject) throws JSONException {
		orderItemId = jsonObject.getInt("order_item_id");
		orderId = jsonObject.getInt("order_id");
		targetUserId = jsonObject.getInt("target_user_id");
		itemId = jsonObject.getInt("item_id");
		lastUpdated = jsonObject.getInt("last_updated");
		status = TaskEntity.getStatusCode(jsonObject.getString("status"));

		itemName = getString(jsonObject, "item_name", itemName);
		price = getDouble(jsonObject, "price", price);
		tableName = getString(jsonObject, "table_name", tableName);

		if (status == TaskEntity.STATUS_SERVED
				|| status == TaskEntity.STATUS_PAID) {
			groupId = orderId;
		}

		parseImages(jsonObject);
	}

	public void parse(TaskEntity other) {
		orderItemId = other.orderItemId;
		orderId = other.orderId;
		targetUserId = other.targetUserId;
		itemId = other.itemId;
		lastUpdated = other.lastUpdated;
		status = other.status;

		if (other.itemName.length() > 0) {
			itemName = other.itemName;
		}
		if (other.tableName.length() > 0) {
			tableName = other.tableName;
		}

		groupId = other.groupId;

		parseImages(other);
	}

	public boolean isCompleted(UserEntity user) {
		return targetUserId != user.userId;
	}

	public void markCompleted(Context context, String csrfToken) {
		selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
		new TaskRequestHelper(context, TaskRequestHelper.ACTION_MARK_COMPLETED,
				this, csrfToken).execute();
	}

	public void revertCompleted(Context context, String csrfToken) {
		selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
		new TaskRequestHelper(context,
				TaskRequestHelper.ACTION_REVERT_COMPLETED, this, csrfToken)
				.execute();
	}

	public static int getStatusCode(String status) {
		if (status.equalsIgnoreCase("waiting")) {
			return TaskEntity.STATUS_WAITING;
		} else if (status.equalsIgnoreCase("prepared")) {
			return TaskEntity.STATUS_PREPARED;
		} else if (status.equalsIgnoreCase("served")) {
			return TaskEntity.STATUS_SERVED;
		} else {
			return TaskEntity.STATUS_PAID;
		}
	}

	public static String getStatusString(int status) {
		switch (status) {
		case STATUS_PREPARED:
			return "prepared";
		case STATUS_SERVED:
			return "served";
		case STATUS_PAID:
			return "paid";
		default:
			return "waiting";
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof TaskEntity) {
			return orderItemId == ((TaskEntity) other).orderItemId;
		} else {
			return false;
		}
	}
}
