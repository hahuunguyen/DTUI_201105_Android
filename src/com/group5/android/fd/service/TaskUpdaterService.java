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
import com.group5.android.fd.adapter.TaskAdapter;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;

/**
 * The core service which will constantly request new tasks information from
 * server and feed our activities
 * 
 * @author Tran Viet Son
 * 
 */
public class TaskUpdaterService extends Service {

	final public static String INTENT_ACTION_NEW_TASK = "com.group5.android.fd.intent.action.NEW_TASK";
	final public static String INTENT_BUNDLE_NAME_TASK_OBJ = "taskObj";

	protected IBinder m_binder = new TaskUpdaterBinder();
	protected Updater m_updater = null;
	protected int m_effectiveLastUpdated = 0;

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

	/**
	 * Starts working with a {@link TaskAdapter}. The server can start working
	 * with no adapter at first. In the next call to this method, the adapter
	 * will be updated and. So at any moment, there will be only one thread
	 * running.
	 * 
	 * @param taskAdapter
	 *            the task adapter currently in charge (possible to set to null)
	 * @param delay
	 *            the one time delay before starting to fetch data
	 * @param interval
	 *            the interval delay between each fetch request
	 * 
	 * @see Updater
	 */
	public void startWorking(TaskAdapter taskAdapter, int delay, int interval) {
		if (m_updater == null) {
			m_updater = new Updater(taskAdapter, delay, interval);
		} else {
			m_updater.setTaskAdapter(taskAdapter);
		}
	}

	/**
	 * The updater thread which will send fetch requests and parse it, etc.
	 * 
	 * @author Tran Viet SOn
	 * 
	 */
	protected class Updater extends Thread {
		protected TaskAdapter m_taskAdapter;
		protected int m_delay;
		protected int m_interval;

		protected boolean m_enabled = true;

		/**
		 * Construct an updater.
		 * 
		 * @param taskAdapter
		 *            the task adapter currently in charge (possible to set to
		 *            null)
		 * @param delay
		 *            the one time delay before starting to fetch data
		 * @param interval
		 *            the interval delay between each fetch request
		 */
		protected Updater(TaskAdapter taskAdapter, int delay, int interval) {
			m_taskAdapter = taskAdapter;
			m_delay = delay;
			m_interval = interval;

			start();
		}

		/**
		 * Changes the {@link TaskAdapter} of the updater.
		 * 
		 * @param taskAdapter
		 *            the new {@link TaskAdapter}
		 */
		protected void setTaskAdapter(TaskAdapter taskAdapter) {
			m_taskAdapter = taskAdapter;
		}

		/**
		 * Schedule a full stop. This method will set the flag
		 * {@link #m_enabled} to false to let the thread now that it should stop
		 * after the next run
		 */
		protected void scheduleStopSoon() {
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

		/**
		 * Sends fetch request to get new tasks from server and issue
		 * <code>Intent</code> as new task is found. This method is smart enough
		 * to include a <code>last_updated</code> field (base on its previous
		 * calls) in the fetch request to get newly updated tasks only. The
		 * <code>Intent</code> will have the action of
		 * {@link TaskUpdaterService#INTENT_ACTION_NEW_TASK}, the new
		 * {@link TaskEntity} will be attached in the <code>Intent</code> and
		 * can be access with the name of
		 * {@link TaskUpdaterService#INTENT_BUNDLE_NAME_TASK_OBJ}
		 */
		protected void getTasks() {
			String tasksUrl = UriStringHelper.buildUriString(
					TaskUpdaterService.this, "tasks");
			tasksUrl = UriStringHelper.addParam(tasksUrl, "last_updated",
					m_taskAdapter != null ? m_taskAdapter
							.getTaskListLastUpdated() : m_effectiveLastUpdated);

			new HttpRequestAsyncTask(null, tasksUrl) {

				@Override
				protected Object process(JSONObject jsonObject) {
					List<TaskEntity> taskList = new ArrayList<TaskEntity>();

					try {
						Object obj = jsonObject.get("tasks");
						if (obj instanceof JSONArray) {
							// this is the case when there are no tasks
						} else {
							JSONObject tasks = (JSONObject) obj;
							JSONArray taskIds = tasks.names();
							for (int i = 0; i < taskIds.length(); i++) {
								TaskEntity task = new TaskEntity();
								JSONObject jsonObject2 = tasks
										.getJSONObject(taskIds.getString(i));
								task.parse(jsonObject2);
								taskList.add(task);
							}
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

							m_effectiveLastUpdated = Math.max(
									m_effectiveLastUpdated, task.lastUpdated);

							Intent intent = new Intent(
									TaskUpdaterService.INTENT_ACTION_NEW_TASK);
							intent
									.putExtra(
											TaskUpdaterService.INTENT_BUNDLE_NAME_TASK_OBJ,
											task);
							sendBroadcast(intent);

							Log.v(FdConfig.DEBUG_TAG, "Broadcasting "
									+ TaskUpdaterService.INTENT_ACTION_NEW_TASK
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