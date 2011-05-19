package com.group5.android.fd.adapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.entity.UserEntity;
import com.group5.android.fd.entity.AbstractEntity.OnUpdatedListener;
import com.group5.android.fd.view.TaskView;

public class TaskAdapter extends BaseAdapter implements OnUpdatedListener {
	protected Context m_context;
	protected UserEntity m_user;
	protected List<TaskEntity> m_taskList = null;

	public TaskAdapter(Context context, UserEntity user,
			List<TaskEntity> taskList) {
		m_context = context;
		m_user = user;

		setTaskList(taskList);
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
			return new TaskView(m_context, m_user, m_taskList.get(position));
		} else {
			TaskView taskView = (TaskView) convertView;
			taskView.setTask(m_taskList.get(position));

			return taskView;

		}
	}

	public void setTaskList(List<TaskEntity> taskList) {
		if (m_taskList != taskList) {
			m_taskList = taskList;

			Iterator<TaskEntity> i = taskList.iterator();
			while (i.hasNext()) {
				i.next().setOnUpdatedListener(this);
			}

			sortTaskList();
		}
	}

	public void sortTaskList() {
		Collections.sort(m_taskList, new Comparator<TaskEntity>() {

			@Override
			public int compare(TaskEntity task1, TaskEntity task2) {
				int task1Completed = task1.isCompleted(m_user) ? 1 : 0;
				int task2Completed = task2.isCompleted(m_user) ? 1 : 0;

				if (task1Completed == task2Completed) {
					if (task1.orderItemId == task2.orderItemId) {
						return 0;
					} else {
						return task1.orderItemId < task2.orderItemId ? -1 : 1;
					}
				} else {
					return task1Completed < task2Completed ? -1 : 1;
				}
			}

		});

		notifyDataSetChanged();
	}

	@Override
	public void onEntityUpdated(AbstractEntity entity, int target) {
		sortTaskList();
	}
}
