package com.group5.android.fd.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.group5.android.fd.R;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.entity.UserEntity;

public class TaskView extends AbstractView implements OnCheckedChangeListener {

	final public static int STATE_WAITING = 0;
	final public static int STATE_NOT_COMPLETED = 1;
	final public static int STATE_COMPLETED = 2;

	protected Context m_context;
	protected CheckBox m_vwCompleted;

	protected UserEntity m_user;
	public TaskEntity task;

	public TaskView(Context context, UserEntity user, TaskEntity task) {
		super(context);

		m_context = context;
		m_user = user;

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.view_task, this, true);

		m_vwCompleted = (CheckBox) findViewById(R.id.chkCompleted);

		m_vwCompleted.setOnCheckedChangeListener(this);

		setTask(task);
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.view_task;
	}

	public void setTask(TaskEntity task) {
		this.task = task;

		setTextViews(task.itemName, task.tableName);
		setImage(chooseImageSize(task));

		m_vwCompleted.setEnabled(task.isSynced(AbstractEntity.TARGET_ALL));
		m_vwCompleted.setChecked(task.isCompleted(m_user));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked != task.isCompleted(m_user)) {
			if (isChecked == true) {
				task.markCompleted(m_context, m_user.csrfToken);
			} else {
				task.revertCompleted(m_context, m_user.csrfToken);
			}
		}
	}

}
