package com.group5.android.fd.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.activity.TaskListActivity;
import com.group5.android.fd.adapter.TaskAdapter;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;

public class TaskUpdaterService extends Service {
	protected IBinder m_binder = new TaskUpdaterBinder();
	protected Updater m_updater = null;

	@Override
	public IBinder onBind(Intent intent) {
		return m_binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(FdConfig.DEBUG_TAG, getClass().getSimpleName() + " is created!");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (m_updater != null) {
			m_updater.scheduleStopSoon();
			m_updater = null;
		}

		Log
				.i(FdConfig.DEBUG_TAG, getClass().getSimpleName()
						+ " is destroyed!");
	}

	public void startWorking(TaskAdapter taskAdapter, int delay, int interval) {
		if (m_updater == null) {
			m_updater = new Updater(taskAdapter, delay, interval);
		} else {
			m_updater.setTaskAdapter(taskAdapter);
		}
	}

	class Updater extends Thread {
		protected TaskAdapter m_taskAdapter;
		protected int m_delay;
		protected int m_interval;

		protected boolean m_enabled = true;

		public Updater(TaskAdapter taskAdapter, int delay, int interval) {
			m_taskAdapter = taskAdapter;
			m_delay = delay;
			m_interval = interval;

			// start();
		}

		public void setTaskAdapter(TaskAdapter taskAdapter) {
			m_taskAdapter = taskAdapter;
		}

		public void scheduleStopSoon() {
			m_enabled = false;
		}

		@Override
		public void run() {
			try {
				// wait for an initial delay (one time only)
				Thread.sleep(m_delay);

				while (m_enabled) {
					Log.v(FdConfig.DEBUG_TAG, getClass().getSimpleName()
							+ " loop hits");

					getTasks();

					// wait for an interval (everytime)
					Thread.sleep(m_interval);
				}

				Log.i(FdConfig.DEBUG_TAG, getClass().getSimpleName()
						+ ".run() finished its job...");
			} catch (Exception e) {
				e.printStackTrace();

				Log.e(FdConfig.DEBUG_TAG, getClass().getSimpleName()
						+ " got an Exception. Halted!");
			}
		}

		protected void getTasks() {
			String tasksUrl = UriStringHelper.buildUriString("tasks");
			tasksUrl = UriStringHelper.addParam(tasksUrl, "last_updated",
					m_taskAdapter.getTaskListLastUpdated());

			new HttpRequestAsyncTask(null, tasksUrl) {

				@Override
				protected Object process(JSONObject jsonObject) {
					List<TaskEntity> taskList = new ArrayList<TaskEntity>();

					try {
						JSONObject tasks = jsonObject.getJSONObject("tasks");
						JSONArray taskIds = tasks.names();
						for (int i = 0; i < taskIds.length(); i++) {
							TaskEntity task = new TaskEntity();
							JSONObject jsonObject2 = tasks
									.getJSONObject(taskIds.getString(i));
							task.parse(jsonObject2);
							taskList.add(task);
						}
					} catch (NullPointerException e) {
						Log.d(FdConfig.DEBUG_TAG,
								"getTasks/preProcess got NULL response");
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return taskList;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void onSuccess(JSONObject jsonObject, Object processed) {
					if (processed != null && processed instanceof List<?>) {
						List<TaskEntity> taskList = (List<TaskEntity>) processed;

						Iterator<TaskEntity> i = taskList.iterator();
						while (i.hasNext()) {
							TaskEntity task = i.next();

							Intent intent = new Intent(
									TaskListActivity.INTENT_ACTION_NEW_TASK);
							intent.putExtra(
									TaskListActivity.EXTRA_DATA_NAME_TASK_OBJ,
									task);
							sendBroadcast(intent);

							Log.v(FdConfig.DEBUG_TAG, "Broadcasting "
									+ TaskListActivity.INTENT_ACTION_NEW_TASK
									+ ": task #" + task.orderItemId);
						}
					}
				}
			}.execute();
		}
	}

	public class TaskUpdaterBinder extends Binder {
		public TaskUpdaterService getService() {
			return TaskUpdaterService.this;
		}
	}
}