package com.group5.android.fd.entity;

import java.io.Serializable;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.FdConfig;

/**
 * The base / abstract entity class. All other entity classes in this project
 * extend this class
 * 
 * @author Dao Hoang Son
 * 
 */
abstract public class AbstractEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4642973671659659486L;

	protected OnUpdatedListener m_onUpdatedListener = null;

	public boolean syncedWithLocalDatabase = true;
	public boolean syncedWithRemoteServer = true;

	public String imageXH = null;
	public String imageH = null;
	public String imageM = null;
	public String imageL = null;

	final public static int TARGET_ALL = 0;
	final public static int TARGET_LOCAL_DATABASE = 1;
	final public static int TARGET_REMOTE_SERVER = 2;

	protected void parseImages(JSONObject jsonObject) {
		imageXH = null;
		imageH = null;
		imageM = null;
		imageL = null;

		try {
			// these properties are not included all the time
			JSONObject images = jsonObject.getJSONObject("images");

			imageXH = images.getString("xh");
			imageH = images.getString("h");
			imageM = images.getString("m");
			imageL = images.getString("l");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void parseImages(Cursor cursor, int indexBefore) {
		imageXH = cursor.getString(indexBefore + 1);
		imageH = cursor.getString(indexBefore + 2);
		imageM = cursor.getString(indexBefore + 3);
		imageL = cursor.getString(indexBefore + 4);
	}

	protected void saveImages(ContentValues values) {
		values.put(DbAdapter.ABSTRACT_KEY_IMAGES_XH, imageXH);
		values.put(DbAdapter.ABSTRACT_KEY_IMAGES_H, imageH);
		values.put(DbAdapter.ABSTRACT_KEY_IMAGES_M, imageM);
		values.put(DbAdapter.ABSTRACT_KEY_IMAGES_L, imageL);
	}

	protected String getString(JSONObject jsonObject, String name,
			String defaultValue) {
		try {
			return jsonObject.getString(name);
		} catch (Exception e) {
			Log.e(FdConfig.DEBUG_TAG, getClass().getSimpleName()
					+ ".parse(JSONObject): " + e.getMessage());
			return new String(defaultValue);
		}
	}

	protected int getInt(JSONObject jsonObject, String name, int defaultValue) {
		try {
			return jsonObject.getInt(name);
		} catch (Exception e) {
			Log.e(FdConfig.DEBUG_TAG, getClass().getSimpleName()
					+ ".parse(JSONObject): " + e.getMessage());
			return defaultValue;
		}
	}

	/**
	 * Checks if the entity is synchronized with a specific target. You can use
	 * {@link AbstractEntity#TARGET_ALL} to check for all available targets.
	 * 
	 * @param target
	 *            should be one of <code>TARGET_ALL</code>,
	 *            <code>TARGET_LOCAL_DATABASE</code> (to check againts the
	 *            client SQLite's database or <code>TARGET_REMOTE_SERVER</code>
	 *            (the server system)
	 * @return true if it's synchronized
	 */
	public boolean isSynced(int target) {
		switch (target) {
		case TARGET_LOCAL_DATABASE:
			return syncedWithLocalDatabase;
		case TARGET_REMOTE_SERVER:
			return syncedWithRemoteServer;
		case TARGET_ALL:
			return isSynced(AbstractEntity.TARGET_LOCAL_DATABASE)
					&& isSynced(AbstractEntity.TARGET_REMOTE_SERVER);
		}

		return false;
	}

	/**
	 * Marks the flag for target synchronized information
	 * 
	 * @param target
	 * @param synced
	 */
	protected void setTarget(int target, boolean synced) {
		switch (target) {
		case TARGET_LOCAL_DATABASE:
			syncedWithLocalDatabase = synced;
			break;
		case TARGET_REMOTE_SERVER:
			syncedWithRemoteServer = synced;
			break;
		}
	}

	/**
	 * Marks the target as invalidated (out of date)
	 * 
	 * @param target
	 */
	public void selfInvalidate(int target) {
		setTarget(target, false);
		notifyListener(target);
	}

	/**
	 * Marks the target as updated AND triggers the listener if any
	 * 
	 * @param target
	 */
	public void onUpdated(int target) {
		setTarget(target, true);
		notifyListener(target);
	}

	protected void notifyListener(int target) {
		if (m_onUpdatedListener != null) {
			m_onUpdatedListener.onEntityUpdated(this, target);

			Log.v(FdConfig.DEBUG_TAG, this.getClass().getSimpleName()
					+ ".notifyListener(" + target + ") --> "
					+ m_onUpdatedListener.getClass().getSimpleName());
		}
	}

	/**
	 * Assigns an listener for this entity. An entity can only have one listener
	 * at a time
	 * 
	 * @param onUpdatedListener
	 * @param triggerImmediately
	 */
	public void setOnUpdatedListener(OnUpdatedListener onUpdatedListener,
			boolean triggerImmediately) {
		if (m_onUpdatedListener != onUpdatedListener) {
			m_onUpdatedListener = onUpdatedListener;

			if (triggerImmediately) {
				onUpdatedListener.onEntityUpdated(this,
						AbstractEntity.TARGET_ALL);
			}
		}
	}

	public void setOnUpdatedListener(OnUpdatedListener onUpdatedListener) {
		setOnUpdatedListener(onUpdatedListener, true);
	}

	/**
	 * The interface which can be assign to an entity and get updated
	 * 
	 * @author Dao Hoang Son
	 * 
	 */
	public interface OnUpdatedListener {
		abstract public void onEntityUpdated(AbstractEntity entity, int target);
	}
}
