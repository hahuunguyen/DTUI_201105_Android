package com.group5.android.fd.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.group5.android.fd.entity.TaskEntity;

/**
 * Kind of a helper class which will register itself to
 * {@link TaskUpdaterService#INTENT_ACTION_NEW_TASK} and with every incoming
 * <code>Intent</code>, it will extract the {@link TaskEntity} and feed an
 * abstract method (subclass should implement it)
 * 
 * @author Dao Hoang Son
 * 
 */
abstract public class TaskUpdaterServiceReceiver extends BroadcastReceiver {

	/**
	 * Construct the receiver and register itself
	 * 
	 * @param context
	 *            the calling context
	 */
	public TaskUpdaterServiceReceiver(Context context) {
		IntentFilter filter = new IntentFilter(
				TaskUpdaterService.INTENT_ACTION_NEW_TASK);
		context.registerReceiver(this, filter);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction()
				.equals(TaskUpdaterService.INTENT_ACTION_NEW_TASK)) {
			TaskEntity task = (TaskEntity) intent
					.getSerializableExtra(TaskUpdaterService.INTENT_BUNDLE_NAME_TASK_OBJ);
			onReceive(context, task);
		}
	}

	/**
	 * Abstract method that subclass should implement. This will be called when
	 * a new {@link TaskEntity} is available to process
	 * 
	 * @param context
	 *            the <code>Intent</code> context
	 * @param task
	 *            the new {@link TaskEntity}
	 */
	abstract protected void onReceive(Context context, TaskEntity task);

}
