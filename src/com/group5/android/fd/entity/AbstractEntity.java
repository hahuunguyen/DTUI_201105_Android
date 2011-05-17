package com.group5.android.fd.entity;

import java.io.Serializable;

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

	protected void selfInvalidate(int target) {
		setTarget(target, false);
	}

	protected void onUpdated(int target) {
		if (m_onUpdatedListener != null) {
			setTarget(target, true);
			m_onUpdatedListener.onEntityUpdated(this, target);
		}
	}

	public void setOnUpdatedListener(OnUpdatedListener onUpdatedListener) {
		m_onUpdatedListener = onUpdatedListener;
	}

	public interface OnUpdatedListener {
		abstract public void onEntityUpdated(AbstractEntity entity, int target);
	}
}
