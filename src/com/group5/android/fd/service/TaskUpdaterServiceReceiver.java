package com.group5.android.fd.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.group5.android.fd.entity.TaskEntity;

abstract public class TaskUpdaterServiceReceiver extends BroadcastReceiver {

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

	abstract protected void onReceive(Context context, TaskEntity task);

}
