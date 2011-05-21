package com.group5.android.fd.entity;

import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.group5.android.fd.helper.TaskRequestHelper;

public class TaskGroupEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5765104604551749064L;

	public int groupId = 0;
	public List<TaskEntity> tasks = null;

	@Override
	public boolean equals(Object other) {
		if (other instanceof TaskGroupEntity) {
			return groupId == ((TaskGroupEntity) other).groupId;
		} else {
			return false;
		}
	}

	public boolean isCompleted(UserEntity user) {
		Iterator<TaskEntity> iterator = tasks.iterator();
		while (iterator.hasNext()) {
			if (!iterator.next().isCompleted(user)) {
				return false;
			}
		}

		return true;
	}

	public void markCompleted(Context context, String csrfToken) {
		selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
		new TaskRequestHelper(context, TaskRequestHelper.ACTION_MARK_COMPLETED,
				tasks, csrfToken).execute();
	}

	public void revertCompleted(Context context, String csrfToken) {
		selfInvalidate(AbstractEntity.TARGET_REMOTE_SERVER);
		new TaskRequestHelper(context,
				TaskRequestHelper.ACTION_REVERT_COMPLETED, tasks, csrfToken)
				.execute();
	}

	protected void selfInvalidate(int target) {
		Iterator<TaskEntity> iterator = tasks.iterator();
		while (iterator.hasNext()) {
			iterator.next().selfInvalidate(target);
		}
	}
}