package com.group5.android.fd.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.entity.TaskGroupEntity;
import com.group5.android.fd.entity.UserEntity;
import com.group5.android.fd.entity.AbstractEntity.OnUpdatedListener;
import com.group5.android.fd.service.TaskUpdaterService;
import com.group5.android.fd.service.TaskUpdaterService.TaskUpdaterBinder;
import com.group5.android.fd.view.TaskGroupView;
import com.group5.android.fd.view.TaskView;

/**
 * Adapter for {@link TaskEntity}s
 * 
 * @author Tran Viet Son
 * 
 */
public class TaskAdapter extends BaseAdapter implements OnUpdatedListener,
		ServiceConnection {
	protected Context m_context;
	protected UserEntity m_user;
	protected List<TaskEntity> m_taskList = null;

	protected int m_taskListLastUpdated = 0;
	protected List<Object> m_abstractedList = new ArrayList<Object>();

	/**
	 * Constructs itself
	 * 
	 * @param context
	 * @param user
	 * @param taskList
	 */
	public TaskAdapter(Context context, UserEntity user,
			List<TaskEntity> taskList) {
		m_context = context;
		m_user = user;
		m_taskList = taskList;

		notifyDataSetChanged();
	}

	/**
	 * Adds a task to the current list. This method will try to update the task
	 * if it exists in the list already
	 * 
	 * @param task
	 */
	public void addTask(TaskEntity task) {
		Iterator<TaskEntity> i = m_taskList.iterator();
		boolean isFound = false;

		// try to update the current task
		while (i.hasNext()) {
			TaskEntity existingTask = i.next();
			if (existingTask.equals(task)) {
				isFound = true;
				existingTask.parse(task);

				Log.v(FdConfig.DEBUG_TAG, "TaskAdapter.addTask(): updated #"
						+ task.orderItemId);
			}
		}

		// if existing task not found, add new task
		if (!isFound) {
			m_taskList.add(task);

			Log.v(FdConfig.DEBUG_TAG, "TaskAdapter.addTask(): added #"
					+ task.orderItemId);
		}

		notifyDataSetChanged();
	}

	/**
	 * Gets the <code>List</code> of {@link TaskEntity}
	 * 
	 * @return the list
	 */
	public List<TaskEntity> getTaskList() {
		return m_taskList;
	}

	/**
	 * Gets the last updated value of the list (larger last updated value of
	 * tasks)
	 * 
	 * @return
	 */
	public int getTaskListLastUpdated() {
		return m_taskListLastUpdated;
	}

	@Override
	public int getCount() {
		return m_abstractedList.size();
	}

	@Override
	public Object getItem(int position) {
		return m_abstractedList.get(position);
	}

	@Override
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object object = m_abstractedList.get(position);

		if (convertView == null) {
			if (object instanceof TaskEntity) {
				return new TaskView(m_context, m_user, (TaskEntity) object);
			} else {
				return new TaskGroupView(m_context, m_user,
						(TaskGroupEntity) object);
			}
		} else {
			if (object instanceof TaskEntity) {
				TaskView taskView = (TaskView) convertView;
				taskView.setTask((TaskEntity) object);

				return taskView;
			} else {
				TaskGroupView taskGroupView = (TaskGroupView) convertView;
				taskGroupView.setGroup((TaskGroupEntity) object);

				return taskGroupView;
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		Collections.sort(m_taskList, new Comparator<TaskEntity>() {
			@Override
			public int compare(TaskEntity task1, TaskEntity task2) {
				if (task1.lastUpdated == task2.lastUpdated) {
					if (task1.orderItemId == task2.orderItemId) {
						return 0;
					} else {
						return task1.orderItemId < task2.orderItemId ? -1 : 1;
					}
				} else {
					return task1.lastUpdated < task2.lastUpdated ? -1 : 1;
				}
			}

		});

		m_abstractedList.clear();
		TaskGroupEntity taskGroup = new TaskGroupEntity();
		TaskGroupEntity taskGroup2;

		Iterator<TaskEntity> i = m_taskList.iterator();
		m_taskListLastUpdated = 0;
		while (i.hasNext()) {
			TaskEntity task = i.next();
			m_taskListLastUpdated = Math.max(m_taskListLastUpdated,
					task.lastUpdated);
			task.setOnUpdatedListener(this, false);

			if (task.groupId == 0) {
				m_abstractedList.add(task);
			} else {
				taskGroup.groupId = task.groupId;
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
					taskGroup2 = new TaskGroupEntity();
					taskGroup2.groupId = task.groupId;
					taskGroup2.tasks = new ArrayList<TaskEntity>();

					m_abstractedList.add(taskGroup2);
				} else {
					// this group exists
					taskGroup2 = (TaskGroupEntity) m_abstractedList
							.get(taskGroupIndex);
				}
				// add this task to the group
				taskGroup2.tasks.add(task);
			}
		}

		super.notifyDataSetChanged();
	}

	@Override
	public void onEntityUpdated(AbstractEntity entity, int target) {
		notifyDataSetChanged();
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		if (service instanceof TaskUpdaterService.TaskUpdaterBinder) {
			TaskUpdaterService.TaskUpdaterBinder binder = (TaskUpdaterBinder) service;
			binder.getService().startWorking(this, 0,
					FdConfig.NEW_TASK_INTERVAL);
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub

	}
}
