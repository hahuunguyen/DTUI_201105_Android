package com.group5.android.fd.entity;

import java.util.List;

public class TaskGroupEntity extends AbstractEntity {

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
}