package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.Main;
import com.group5.android.fd.adapter.TaskAdapter;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.UriStringHelper;

public class TaskActivity extends ListActivity implements
		HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller {

	protected String m_csrfTokenPage = null;
	protected int m_directionFrom;
	protected int m_directionTo;
	protected HttpRequestAsyncTask m_hrat = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		m_csrfTokenPage = intent
				.getStringExtra(Main.INSTANCE_STATE_KEY_CSRF_TOKEN_PAGE);
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
						JSONObject jsonObject2 = tasks.getJSONObject(taskIds
								.getString(i));
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

				Collections.sort(taskList, new Comparator<Object>() {

					@Override
					public int compare(Object o1, Object o2) {
						TaskEntity t1 = (TaskEntity) o1;
						TaskEntity t2 = (TaskEntity) o2;
						if (t1.orderItemId == t2.orderItemId) {
							return 0;
						} else if (t1.orderItemId < t2.orderItemId) {
							return -1;
						} else {
							return 1;
						}
					}

				});
				return taskList;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void onSuccess(JSONObject jsonObject, Object processed) {
				try {
					JSONObject direction = jsonObject
							.getJSONObject("direction");
					String directionFrom = direction.getString("from");
					String directionTo = direction.getString("to");
					m_directionFrom = TaskEntity.getStatusCode(directionFrom);
					m_directionTo = TaskEntity.getStatusCode(directionTo);

					if (processed instanceof List<?>) {
						initLayout((List<TaskEntity>) processed);
					}
				} catch (NullPointerException e) {
					Log.d(FdConfig.DEBUG_TAG,
							"getTasks/process got NULL response");
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}.execute();
	}

	protected void initLayout(List<TaskEntity> taskList) {
		TaskAdapter taskAdapter = new TaskAdapter(this, m_csrfTokenPage,
				taskList, m_directionFrom, m_directionTo);

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

}