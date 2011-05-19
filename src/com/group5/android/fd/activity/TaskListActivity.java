package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.Main;
import com.group5.android.fd.adapter.TaskAdapter;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.entity.UserEntity;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;

public class TaskListActivity extends ListActivity implements
		HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller, OnClickListener {

	protected UserEntity m_user = null;
	List<TaskEntity> m_taskList = null;

	protected HttpRequestAsyncTask m_hrat = null;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		m_user = (UserEntity) intent
				.getSerializableExtra(Main.INSTANCE_STATE_KEY_USER_OBJ);

		Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
		if (lastNonConfigurationInstance != null
				&& lastNonConfigurationInstance instanceof List<?>) {
			// found our long lost task list, yay!
			m_taskList = (List<TaskEntity>) lastNonConfigurationInstance;

			Log.i(FdConfig.DEBUG_TAG, "List<TaskEntity> has been recovered");
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// we want to preserve our order information when configuration is
		// change, say.. orientation change?
		return m_taskList;
	}

	@Override
	protected void onResume() {
		super.onResume();

		getTasksAndInitLayoutEverything();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (m_hrat != null) {
			m_hrat.dismissProgressDialog();
		}
	}

	private void getTasksAndInitLayoutEverything() {
		if (m_taskList == null) {
			String tasksUrl = UriStringHelper.buildUriString("tasks");

			new HttpRequestAsyncTask(this, tasksUrl) {

				@Override
				protected Object process(JSONObject jsonObject) {
					m_taskList = new ArrayList<TaskEntity>();

					try {
						JSONObject tasks = jsonObject.getJSONObject("tasks");
						JSONArray taskIds = tasks.names();
						for (int i = 0; i < taskIds.length(); i++) {
							TaskEntity task = new TaskEntity();
							JSONObject jsonObject2 = tasks
									.getJSONObject(taskIds.getString(i));
							task.parse(jsonObject2);
							m_taskList.add(task);
						}
					} catch (NullPointerException e) {
						Log.d(FdConfig.DEBUG_TAG,
								"getTasks/preProcess got NULL response");
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return m_taskList;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void onSuccess(JSONObject jsonObject, Object processed) {
					if (processed != null && processed instanceof List<?>) {
						initLayout((List<TaskEntity>) processed);
					}
				}

			}.execute();
		} else {
			initLayout(m_taskList);
		}
	}

	protected void initLayout(List<TaskEntity> taskList) {
		m_taskList = taskList;

		TaskAdapter taskAdapter = new TaskAdapter(this, m_user, m_taskList);
		setListAdapter(taskAdapter);
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
	public void onClick(DialogInterface arg0, int arg1) {
		Log.d(FdConfig.DEBUG_TAG, "clicked " + arg1);
	}

}