package com.group5.android.fd.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;

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
	protected int status;
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

	public int getStatus() {
		return status;
	}

	public String getStatusAsString() {
		switch (status) {
		case STATUS_SERVED:
			return "served";
		case STATUS_PAID:
			return "paid";
		default:
			return "waiting";
		}
	}

	public void setStatus(Context context, String csrfToken, int newStatus) {
		int oldStatus = status;
		status = newStatus;

		if (newStatus != oldStatus) {
			String updateTaskUri = UriStringHelper
					.buildUriString("update-task");
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("order_item_id", "" + orderItemId));
			params.add(new BasicNameValuePair("status", getStatusAsString()));

			new HttpRequestAsyncTask(context, updateTaskUri, csrfToken, params) {

				@Override
				protected void process(JSONObject jsonObject,
						Object preProcessed) {
					// TODO
				}
			};
		}
	}
}
