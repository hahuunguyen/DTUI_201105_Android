package com.group5.android.fd.entity;

import java.io.Serializable;

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

	final public static int TARGET_ALL = 0;
	final public static int TARGET_LOCAL_DATABASE = 1;
	final public static int TARGET_REMOTE_SERVER = 2;

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
	protected void selfInvalidate(int target) {
		setTarget(target, false);
	}

	/**
	 * Marks the target as updated AND triggers the listener if any
	 * 
	 * @param target
	 */
	protected void onUpdated(int target) {
		if (m_onUpdatedListener != null) {
			setTarget(target, true);
			m_onUpdatedListener.onEntityUpdated(this, target);
		}
	}

	/**
	 * Assigns an listener for this entity. An entity can only have one listener
	 * at a time
	 * 
	 * @param onUpdatedListener
	 */
	public void setOnUpdatedListener(OnUpdatedListener onUpdatedListener) {
		m_onUpdatedListener = onUpdatedListener;

		onUpdatedListener.onEntityUpdated(this, AbstractEntity.TARGET_ALL);
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
