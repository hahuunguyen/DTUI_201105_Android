package com.group5.android.fd.entity;

import java.util.ArrayList;

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
	public int targetUserId;
	public int itemId;
	public int lastUpdated;
	public int status;

	public int groupId = 0;
	public String itemName = "";

	final public static int STATUS_WAITING = 0;
	final public static int STATUS_PREPARED = 1;
	final public static int STATUS_SERVED = 2;
	final public static int STATUS_PAID = 3;

	final public static int ACTION_MARK_COMPLETED = 1;
	final public static int ACTION_REVERT_COMPLETED = 2;

	public void parse(JSONObject jsonObject) throws JSONException {
		orderItemId = jsonObject.getInt("order_item_id");
		orderId = jsonObject.getInt("order_id");
		targetUserId = jsonObject.getInt("target_user_id");
		itemId = jsonObject.getInt("item_id");
		lastUpdated = jsonObject.getInt("last_updated");
		status = TaskEntity.getStatusCode(jsonObject.getString("status"));

		itemName = getString(jsonObject, "item_name", itemName);

		if (status == TaskEntity.STATUS_SERVED) {
			groupId = orderId;
		} else {
			// groupId = 1;
		}
	}

	public void parse(TaskEntity other) {
		orderItemId = other.orderItemId;
		orderId = other.orderId;
		targetUserId = other.targetUserId;
		itemId = other.itemId;
		lastUpdated = other.lastUpdated;
		status = other.status;

		itemName = other.itemName;
		groupId = other.groupId;
	}

	public boolean isCompleted(UserEntity user) {
		return targetUserId != user.userId;
	}

	public void markCompleted(Context context, String csrfToken) {
		selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
		new TaskRequest(context, TaskEntity.ACTION_MARK_COMPLETED, csrfToken)
				.execute();
	}

	public void revertCompleted(Context context, String csrfToken) {
		selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
		new TaskRequest(context, TaskEntity.ACTION_REVERT_COMPLETED, csrfToken)
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

	protected class TaskRequest extends HttpRequestAsyncTask {

		public TaskRequest(Context context, int action, String csrfToken) {
			super(context);

			mode = HttpRequestAsyncTask.MODE_POST;
			switch (action) {
			case ACTION_MARK_COMPLETED:
				m_uri = UriStringHelper.buildUriString("task-mark-completed");
				break;
			case ACTION_REVERT_COMPLETED:
				m_uri = UriStringHelper.buildUriString("task-revert-completed");
				break;
			}
			m_csrfToken = csrfToken;

			m_params = new ArrayList<NameValuePair>();
			m_params.add(new BasicNameValuePair("order_item_id", ""
					+ orderItemId));
		}

		@Override
		protected void onSuccess(JSONObject jsonObject, Object preProcessed) {
			try {
				JSONObject orderItem = jsonObject.getJSONObject("orderItem");
				parse(orderItem);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			onUpdated(AbstractEntity.TARGET_REMOTE_SERVER);
		}

		@Override
		protected void onError(JSONObject jsonObject, String message) {
			super.onError(jsonObject, message);

			onUpdated(AbstractEntity.TARGET_REMOTE_SERVER);
		}

		@Override
		protected void onProgressUpdate(Void... arg0) {
			// do nothing here, we don't want to show the progress dialog or
			// the toast
		}

	}
}
