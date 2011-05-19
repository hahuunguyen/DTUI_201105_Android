package com.group5.android.fd.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.group5.android.fd.R;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.entity.UserEntity;
import com.group5.android.fd.entity.AbstractEntity.OnUpdatedListener;

public class TaskView extends RelativeLayout implements
		OnCheckedChangeListener, OnUpdatedListener {
	public TaskEntity task;
	protected CheckBox m_vwServed;
	protected TextView m_vwTaskName;

	protected Context m_context;
	protected UserEntity m_user;

	public TaskView(Context context, UserEntity user, TaskEntity task) {
		super(context);

		m_context = context;
		m_user = user;

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.view_task, this, true);
		m_vwServed = (CheckBox) findViewById(R.id.chkServed);
		m_vwTaskName = (TextView) findViewById(R.id.txtTaskName);
		m_vwServed.setOnCheckedChangeListener(this);

		setTask(task);
	}

	public void setTask(TaskEntity task) {
		this.task = task;

		m_vwTaskName.setText(task.orderItemId + " " + task.itemName);
		task.setOnUpdatedListener(this);

		m_vwServed.setEnabled(isTaskCompleted());
		m_vwServed.setChecked(!isTaskCompleted());
	}

	public boolean isTaskCompleted() {
		if (task.isSynced(AbstractEntity.TARGET_ALL)) {
			return task.targetUserId != m_user.userId;
		} else {
			return false;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (arg1 == true) {
			if (task.targetUserId == m_user.userId) {
				task.updateStatus(m_context, m_user.csrfToken);
				setTask(task);
			}
		}
	}

	@Override
	public void onEntityUpdated(AbstractEntity entity, int target) {
		if (entity instanceof TaskEntity) {
			TaskEntity task = (TaskEntity) entity;
			if (task.orderItemId == this.task.orderItemId) {
				setTask(task);
			}
		}
	}

}
