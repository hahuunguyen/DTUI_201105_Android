package com.group5.android.fd.helper;

import java.util.ArrayList;
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
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.TaskEntity;

/**
 * Helper class to send task updating requests
 * 
 * @author Dao Hoang Son
 * 
 * @see HttpRequestAsyncTask
 * 
 */
public class TaskRequestHelper extends HttpRequestAsyncTask {

	final public static int ACTION_MARK_COMPLETED = 1;
	final public static int ACTION_REVERT_COMPLETED = 2;

	protected List<TaskEntity> m_tasks;

	/**
	 * Constructs itself. The constructor accepts both a single
	 * {@link TaskEntity} and a <code>List</code> of them. And just like normal
	 * POST requests, a CSRF token is required.
	 * 
	 * @param context
	 * @param action
	 * @param tasks
	 * @param csrfToken
	 */
	@SuppressWarnings("unchecked")
	public TaskRequestHelper(Context context, int action, Object tasks,
			String csrfToken) {
		super(context);

		if (tasks instanceof TaskEntity) {
			// single mode
			TaskEntity task = (TaskEntity) tasks;
			m_tasks = new ArrayList<TaskEntity>();
			m_tasks.add(task);
		} else {
			// assume it's a list if someone decides to outplay me and put
			// random stuff here he will get a lovely exception. I don't
			// give a shit about that!
			m_tasks = (List<TaskEntity>) tasks;
		}

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
		int count = m_tasks.size();
		for (int i = 0; i < count; i++) {
			m_params.add(new BasicNameValuePair("order_item_ids[" + i + "]", ""
					+ m_tasks.get(i).orderItemId));
		}
	}

	@Override
	protected void onSuccess(JSONObject jsonObject, Object preProcessed) {
		try {
			JSONObject orderItems = jsonObject.getJSONObject("orderItems");
			JSONArray orderItemIds = orderItems.names();
			TaskEntity task = new TaskEntity();
			for (int i = 0; i < orderItemIds.length(); i++) {
				JSONObject jsonObject2 = orderItems.getJSONObject(orderItemIds
						.getString(i));
				task.parse(jsonObject2);

				Iterator<TaskEntity> iterator = m_tasks.iterator();
				while (iterator.hasNext()) {
					TaskEntity existingTask = iterator.next();
					if (existingTask.equals(task)) {
						existingTask.parse(task);

						Log.v(FdConfig.DEBUG_TAG,
								"TaskUpdateRequest.onSuccess(): updated #"
										+ task.orderItemId);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		sendOnUpdate();
	}

	@Override
	protected void onError(JSONObject jsonObject, String message) {
		super.onError(jsonObject, message);

		sendOnUpdate();
	}

	@Override
	protected void onProgressUpdate(Void... arg0) {
		// do nothing here, we don't want to show the progress dialog or
		// the toast
	}

	/**
	 * Triggers the {@link AbstractEntity#onUpdated(int)} event for all tasks
	 */
	protected void sendOnUpdate() {
		Iterator<TaskEntity> iterator = m_tasks.iterator();
		while (iterator.hasNext()) {
			iterator.next().onUpdated(AbstractEntity.TARGET_REMOTE_SERVER);
		}
	}
}