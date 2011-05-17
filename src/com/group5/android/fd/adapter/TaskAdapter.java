package com.group5.android.fd.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.view.TaskView;

public class TaskAdapter extends BaseAdapter {
	protected Context m_context;
	protected String m_csrfToken;
	protected List<TaskEntity> m_taskList;

	public TaskAdapter(Context context, String csrfToken,
			List<TaskEntity> taskList) {
		m_context = context;
		m_csrfToken = csrfToken;
		m_taskList = taskList;
	}

	public int getCount() {
		return m_taskList.size();
	}

	public Object getItem(int position) {
		return m_taskList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			return new TaskView(m_context, m_csrfToken, m_taskList
					.get(position));
		} else {
			TaskView taskView = (TaskView) convertView;
			taskView.setTask(m_taskList.get(position));

			return taskView;

		}
	}

	public void setNewTaskList(List<TaskEntity> tasks) {
		m_taskList = tasks;
		notifyDataSetChanged();

		Log.i(FdConfig.DEBUG_TAG, "TaskAdapter.setNewTaskList()");
	}
}
