package com.group5.android.fd.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.group5.android.fd.adapter.TaskAdapter;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.helper.HttpHelper;
import com.group5.android.fd.helper.UriStringHelper;

public class TaskListActivity extends ListActivity implements
		OnItemClickListener {
	private TaskAdapter m_taskAdapter;
	private List<TaskEntity> m_taskList = new LinkedList<TaskEntity>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTasks();
		m_taskAdapter = new TaskAdapter(this, m_taskList);
		initLayout();
		initListeners();
	}

	private void getTasks() {
		new AsyncTask<Void, Void, List<TaskEntity>>() {
			@Override
			protected List<TaskEntity> doInBackground(Void... params) {
				String tasksUrl = UriStringHelper.buildUriString("tasks");
				JSONObject response = HttpHelper.get(TaskListActivity.this,
						tasksUrl);
				List<TaskEntity> taskList = new ArrayList<TaskEntity>();
				try {
					JSONObject tasks = response.getJSONObject("tasks");
					JSONArray taskIds = tasks.names();
					for (int i = 0; i < taskIds.length(); i++) {
						TaskEntity task = new TaskEntity();
						JSONObject jsonObject = tasks.getJSONObject(taskIds
								.getString(i));
						task.parse(jsonObject);
						taskList.add(task);

					}
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return taskList;
			}

			@Override
			protected void onPostExecute(List<TaskEntity> tasks) {
				setTaskList(tasks);
			}
		}.execute();
	}

	private void setTaskList(List<TaskEntity> tasks) {
		m_taskList = tasks;
		m_taskAdapter.setNewTaskList(m_taskList);
		getListView().postInvalidate();
	}

	protected void initLayout() {
		setListAdapter(m_taskAdapter);
	}

	protected void initListeners() {
		getListView().setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}
}