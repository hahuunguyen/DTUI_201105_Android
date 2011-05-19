package com.group5.android.fd.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.group5.android.fd.entity.TaskEntity;
import com.group5.android.fd.helper.HttpHelper;
import com.group5.android.fd.helper.UriStringHelper;

public class TaskUpdaterService extends Service {

	private static final String TAG_SERVICE = TaskUpdaterService.class
			.getSimpleName();
	private Updater updater;
	public boolean isRunning = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		updater = new Updater();

		Log.d(TAG_SERVICE, "onCreate'd");
	}

	@Override
	public synchronized void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		// Start the updater
		if (!this.isRunning) {
			updater.start();
			this.isRunning = true;
		}

		Log.d(TAG_SERVICE, "onStart'd");
	}

	@Override
	public synchronized void onDestroy() {
		super.onDestroy();

		// Stop the updater
		if (this.isRunning) {
			updater.interrupt();
		}

		updater = null;

		Log.d(TAG_SERVICE, "onDestroy'd");
	}

	// Updater thread
	class Updater extends Thread {
		static final long DELAY = 10000; // delay time
		private boolean isRunning = false;

		@Override
		public void run() {
			isRunning = true;
			while (isRunning) {
				try {
					Log.d(TAG_SERVICE, "Updater run'ing");

					// UPDATE the tasks here
					getTasks();

					// Sleep
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
					// Interrupted
					isRunning = false;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} // while
		}

		public boolean isRunning() {
			return this.isRunning;
		}

		private void getTasks() throws JSONException {
			String taskUrl = UriStringHelper.buildUriString("tasks");
			JSONObject response = HttpHelper.get(taskUrl);
			List<TaskEntity> taskList = new ArrayList<TaskEntity>();

			try {
				JSONObject tasks = response.getJSONObject("task");
				JSONArray taskIds = tasks.names();
				for (int i = 0; i < taskIds.length(); i++) {
					TaskEntity task = new TaskEntity();
					JSONObject jsonObject = tasks.getJSONObject(taskIds
							.getString(i));
					task.parse(jsonObject);
					taskList.add(task);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}