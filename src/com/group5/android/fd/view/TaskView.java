package com.group5.android.fd.view;

import android.content.Context;

import com.group5.android.fd.entity.TaskEntity;

public class TaskView extends AbstractTaskView {
	public TaskEntity task;

	public TaskView(Context context, TaskEntity task) {
		super(context);
		setTask(task);
	}

	public void setTask(TaskEntity task) {
		this.task = task;
		setTextView(task.orderItemId + "");
		m_chk.setChecked(task.status == TaskEntity.STATUS_WAITING ? false : true);
	}
}
