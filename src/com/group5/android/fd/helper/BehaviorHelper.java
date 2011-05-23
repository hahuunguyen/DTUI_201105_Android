package com.group5.android.fd.helper;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;

import com.group5.android.fd.FdConfig;

/**
 * Behavior setup manager for common use elements
 * 
 * @author Dao Hoang Son
 * 
 */
abstract public class BehaviorHelper {

	protected static boolean flingPrepared = false;
	protected static int distanceMin = -1;
	protected static int offPathMax = -1;
	protected static int velocityThreshold = -1;

	public static Dialog setup(Dialog dialog) {
		// make our dialog to be a little more friendly
		dialog.setCanceledOnTouchOutside(true);

		return dialog;
	}

	public static void setupFling(Context context, final FlingReady flingReady) {
		if (!BehaviorHelper.flingPrepared) {
			// only set these things up once
			BehaviorHelper.flingPrepared = true;

			DisplayMetrics dm = context.getResources().getDisplayMetrics();

			BehaviorHelper.distanceMin = (int) (120 * dm.density);
			BehaviorHelper.offPathMax = (int) (250 * dm.density);
			BehaviorHelper.velocityThreshold = (int) (200 * dm.density);
		}

		final GestureDetector gestureDetector = new GestureDetector(
				new SimpleOnGestureListener() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						try {
							if (Math.abs(e1.getY() - e2.getY()) > BehaviorHelper.offPathMax) {
								return false;
							}

							if (e1.getX() - e2.getX() > BehaviorHelper.distanceMin
									&& Math.abs(velocityX) > BehaviorHelper.velocityThreshold) {
								Log.i(FdConfig.DEBUG_TAG, flingReady.getClass()
										.getSimpleName()
										+ " is getting a LEFT FLING");

								flingReady.onFlingLeft();
								return true;
							} else if (e2.getX() - e1.getX() > BehaviorHelper.distanceMin
									&& Math.abs(velocityX) > BehaviorHelper.velocityThreshold) {
								Log.i(FdConfig.DEBUG_TAG, flingReady.getClass()
										.getSimpleName()
										+ " is getting a RIGHT FLING");

								flingReady.onFlighRight();
								return true;
							} else if (e1.getY() - e2.getY() > BehaviorHelper.distanceMin
									&& Math.abs(velocityY) > BehaviorHelper.velocityThreshold) {
								Log.i(FdConfig.DEBUG_TAG, flingReady.getClass()
										.getSimpleName()
										+ " is getting a UP FLING");

								flingReady.onFlingUp();
								return true;
							} else if (e2.getY() - e1.getY() > BehaviorHelper.distanceMin
									&& Math.abs(velocityY) > BehaviorHelper.velocityThreshold) {
								Log.i(FdConfig.DEBUG_TAG, flingReady.getClass()
										.getSimpleName()
										+ " is getting a DOWN FLING");

								flingReady.onFlingDown();
								return true;
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

						return false;
					}
				});

		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (gestureDetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		};

		flingReady.addFlingListener(gestureListener);
	}

	public interface FlingReady {
		public void addFlingListener(View.OnTouchListener gestureListener);

		public void onFlingLeft();

		public void onFlighRight();

		public void onFlingUp();

		public void onFlingDown();
	}
}
