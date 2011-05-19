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

public class TaskView extends RelativeLayout implements OnCheckedChangeListener {

	final public static int STATE_WAITING = 0;
	final public static int STATE_NOT_COMPLETED = 1;
	final public static int STATE_COMPLETED = 2;

	public TaskEntity task;
	protected CheckBox m_vwCompleted;
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
		m_vwCompleted = (CheckBox) findViewById(R.id.chkCompleted);
		m_vwTaskName = (TextView) findViewById(R.id.txtTaskName);
		m_vwCompleted.setOnCheckedChangeListener(this);

		setTask(task);
	}

	public void setTask(TaskEntity task) {
		this.task = task;

		m_vwTaskName.setText(task.itemName + " (#" + task.orderItemId + ")");

		int state = getCompletedState();
		m_vwCompleted.setEnabled(state != TaskView.STATE_WAITING);
		m_vwCompleted.setChecked(state == TaskView.STATE_COMPLETED);
	}

	public int getCompletedState() {
		if (task.isSynced(AbstractEntity.TARGET_ALL)) {
			return task.isCompleted(m_user) ? TaskView.STATE_COMPLETED
					: TaskView.STATE_NOT_COMPLETED;
		} else {
			return TaskView.STATE_WAITING;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (arg1 != task.isCompleted(m_user)) {
			if (arg1 == true) {
				task.markCompleted(m_context, m_user.csrfToken);
			} else {
				task.revertCompleted(m_context, m_user.csrfToken);
			}
		}
	}

}
