package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.Main;
import com.group5.android.fd.R;
import com.group5.android.fd.adapter.TaskAdapter;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.entity.UserEntity;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;
import com.group5.android.fd.service.TaskUpdaterService;

public class TaskListActivity extends ListActivity implements
		HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller {

	final public static String EXTRA_DATA_NAME_TASK_OBJ = "taskObj";
	final public static String INTENT_ACTION_NEW_TASK = "com.group5.android.fd.intent.action.NEW_TASK";

	protected UserEntity m_user;
	protected TaskAdapter m_taskAdapter;

	protected BroadcastReceiver m_broadcastReceiverForNewTask = null;
	protected HttpRequestAsyncTask m_hrat = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		m_user = (UserEntity) intent
				.getSerializableExtra(Main.EXTRA_DATA_NAME_USER_OBJ);

		initLayout();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// we want to preserve our order information when configuration is
		// change, say.. orientation change?
		return m_taskAdapter.getTaskList();
	}

	@Override
	protected void onResume() {
		super.onResume();

		getTasksAndInitLayoutEverything();

		Intent service = new Intent(this, TaskUpdaterService.class);
		bindService(service, m_taskAdapter, Context.BIND_AUTO_CREATE);

		IntentFilter intentFilter = new IntentFilter(
				TaskListActivity.INTENT_ACTION_NEW_TASK);
		m_broadcastReceiverForNewTask = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(
						TaskListActivity.INTENT_ACTION_NEW_TASK)) {
					Log.v(FdConfig.DEBUG_TAG, "Intent received: "
							+ intent.getAction());

					TaskEntity task = (TaskEntity) intent
							.getSerializableExtra(TaskListActivity.EXTRA_DATA_NAME_TASK_OBJ);
					m_taskAdapter.addTask(task);
				}
			}

		};

		registerReceiver(m_broadcastReceiverForNewTask, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (m_hrat != null) {
			m_hrat.dismissProgressDialog();
		}

		unbindService(m_taskAdapter);

		if (m_broadcastReceiverForNewTask != null) {
			unregisterReceiver(m_broadcastReceiverForNewTask);
		}
	}

	@SuppressWarnings("unchecked")
	private void getTasksAndInitLayoutEverything() {
		Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
		List<TaskEntity> taskList = null;
		if (lastNonConfigurationInstance != null
				&& lastNonConfigurationInstance instanceof List<?>) {
			// found our long lost task list, yay!
			taskList = (List<TaskEntity>) lastNonConfigurationInstance;

			Log.i(FdConfig.DEBUG_TAG, "List<TaskEntity> has been recovered");
		}

		if (taskList == null) {
			String tasksUrl = UriStringHelper.buildUriString("tasks");

			new HttpRequestAsyncTask(this, tasksUrl) {

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

						e.printStackTrace();
					}

					return taskList;
				}

				@Override
				protected void onSuccess(JSONObject jsonObject, Object processed) {
					if (processed != null && processed instanceof List<?>) {
						setTaskList((List<TaskEntity>) processed);
					}
				}

			}.execute();
		} else {
			setTaskList(taskList);
		}
	}

	protected void initLayout() {
		m_taskAdapter = new TaskAdapter(this, m_user);
		setListAdapter(m_taskAdapter);
	}

	protected void setTaskList(List<TaskEntity> taskList) {
		m_taskAdapter.setTaskList(taskList);
	}

	@Override
	public void addHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat != null && m_hrat != hrat) {
			m_hrat.dismissProgressDialog();
		}

		m_hrat = hrat;
	}

	@Override
	public void removeHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat == hrat) {
			m_hrat = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.task, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.itemServiceStart:
			startService(new Intent(this, TaskUpdaterService.class));
			break;
		case R.id.itemServiceStop:
			stopService(new Intent(this, TaskUpdaterService.class));
			break;
		}

		return true;
	}
}