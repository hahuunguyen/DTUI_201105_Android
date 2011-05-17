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
import com.group5.android.fd.entity.AbstractEntity.OnUpdatedListener;

public class TaskView extends RelativeLayout implements
		OnCheckedChangeListener, OnUpdatedListener {
	public TaskEntity task;
	protected CheckBox m_vwServed;
	protected TextView m_vwTaskName;

	protected Context m_context;
	protected String m_csrfToken;

	public TaskView(Context context, String csrfToken, TaskEntity task) {
		super(context);

		m_context = context;
		m_csrfToken = csrfToken;

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

		if (task.isSynced(AbstractEntity.TARGET_ALL)) {
			if (task.getStatus() != TaskEntity.STATUS_WAITING) {
				m_vwServed.setEnabled(false);
			} else {
				m_vwServed.setEnabled(true);
			}
			m_vwServed
					.setChecked(task.getStatus() == TaskEntity.STATUS_WAITING ? false
							: true);
		} else {
			m_vwServed.setEnabled(false);
			m_vwServed.setChecked(false);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (arg1 == true) {
			if (task.getStatus() == TaskEntity.STATUS_WAITING) {
				task
						.setStatus(m_context, m_csrfToken,
								TaskEntity.STATUS_SERVED);
				setTask(task);
			}
		} else {
			if (task.getStatus() != TaskEntity.STATUS_WAITING) {
				m_vwServed.setChecked(true);
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
