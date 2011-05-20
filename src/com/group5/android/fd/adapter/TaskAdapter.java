package com.group5.android.fd.adapter;

import java.util.ArrayList;
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
import com.group5.android.fd.entity.TaskEntityGroup;
import com.group5.android.fd.entity.UserEntity;
import com.group5.android.fd.entity.AbstractEntity.OnUpdatedListener;
import com.group5.android.fd.view.TaskGroupView;
import com.group5.android.fd.view.TaskView;

public class TaskAdapter extends BaseAdapter implements OnUpdatedListener {
	protected Context m_context;
	protected UserEntity m_user;
	protected List<TaskEntity> m_taskList = null;
	protected List<Object> m_abstractedList = new ArrayList<Object>();

	public TaskAdapter(Context context, UserEntity user,
			List<TaskEntity> taskList) {
		m_context = context;
		m_user = user;

		setTaskList(taskList);
	}

	public int getCount() {
		return m_abstractedList.size();
	}

	public Object getItem(int position) {
		return m_abstractedList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		Object object = m_abstractedList.get(position);
		if (object instanceof TaskEntity) {
			return 0;
		} else {
			return 1;
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Object object = m_abstractedList.get(position);

		if (convertView == null) {
			if (object instanceof TaskEntity) {
				return new TaskView(m_context, m_user, (TaskEntity) object);
			} else {
				return new TaskGroupView(m_context, m_user,
						(TaskEntityGroup) object);
			}
		} else {
			if (object instanceof TaskEntity) {
				TaskView taskView = (TaskView) convertView;
				taskView.setTask((TaskEntity) object);

				return taskView;
			} else {
				TaskGroupView taskGroupView = (TaskGroupView) convertView;
				taskGroupView.setGroup((TaskEntityGroup) object);

				return taskGroupView;
			}
		}
	}

	public void setTaskList(List<TaskEntity> taskList) {
		if (m_taskList != taskList) {
			m_taskList = taskList;

			sortTaskList();
		}
	}

	@Override
	public void notifyDataSetChanged() {
		m_abstractedList.clear();
		TaskEntityGroup taskGroup = new TaskEntityGroup();
		TaskEntityGroup taskGroup2;

		Iterator<TaskEntity> i = m_taskList.iterator();
		while (i.hasNext()) {
			TaskEntity task = i.next();
			task.setOnUpdatedListener(this, false);

			if (task.group == 0) {
				m_abstractedList.add(task);
			} else {
				taskGroup.group = task.group;
				int taskGroupIndex = -1;

				Iterator<Object> i2 = m_abstractedList.iterator();
				int indexTmp = 0;
				while (i2.hasNext()) {
					if (i2.next().equals(taskGroup)) {
						taskGroupIndex = indexTmp;
						break;
					}
					indexTmp++;
				}

				if (taskGroupIndex == -1) {
					// no group yet
					taskGroup2 = new TaskEntityGroup();
					taskGroup2.group = task.group;
					taskGroup2.tasks = new ArrayList<TaskEntity>();
					m_abstractedList.add(taskGroup2);
				} else {
					// this group exists
					taskGroup2 = (TaskEntityGroup) m_abstractedList
							.get(taskGroupIndex);
				}
				// add this task to the group
				taskGroup2.tasks.add(task);
			}
		}

		super.notifyDataSetChanged();
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
