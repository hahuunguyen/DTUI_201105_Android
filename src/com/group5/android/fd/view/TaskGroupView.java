package com.group5.android.fd.view;

import java.util.Iterator;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.group5.android.fd.R;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.entity.TaskGroupEntity;
import com.group5.android.fd.entity.UserEntity;

public class TaskGroupView extends LinearLayout {

	final public static int TASK_VIEW_PADDING_LEFT = 20;

	protected Context m_context;
	protected TextView m_vwTaskGroupName;
	protected CheckBox m_vwGroupCompleted;
	protected LinearLayout m_vwTasks;

	protected UserEntity m_user;
	public TaskGroupEntity group;

	public TaskGroupView(Context context, UserEntity user, TaskGroupEntity group) {
		super(context);

		m_context = context;
		m_user = user;

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.view_task_group, this, true);

		m_vwTaskGroupName = (TextView) findViewById(R.id.txtTaskGroupName);
		m_vwGroupCompleted = (CheckBox) findViewById(R.id.chkGroupCompleted);
		m_vwTasks = (LinearLayout) findViewById(R.id.llTasks);

		setGroup(group);
	}

	public void setGroup(TaskGroupEntity group) {
		this.group = group;

		m_vwTasks.removeAllViews();

		StringBuilder sb = new StringBuilder();
		boolean someAreWaiting = false;
		boolean someAreNotCompleted = false;

		Iterator<TaskEntity> iterator = group.tasks.iterator();
		while (iterator.hasNext()) {
			TaskEntity task = iterator.next();

			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(task.itemName);

			if (!task.isSynced(AbstractEntity.TARGET_ALL)) {
				someAreWaiting = true;
			}

			if (!task.isCompleted(m_user)) {
				someAreNotCompleted = true;
			}

			TaskView taskView = new TaskView(m_context, m_user, task);
			taskView.setPadding(TaskGroupView.TASK_VIEW_PADDING_LEFT, 0, 0, 0);
			m_vwTasks.addView(taskView);
		}

		m_vwTaskGroupName.setText(sb.toString());
		m_vwGroupCompleted.setEnabled(!someAreWaiting);
		m_vwGroupCompleted.setChecked(!someAreNotCompleted);

		m_vwTasks.postInvalidate();
		postInvalidate();
	}
}
