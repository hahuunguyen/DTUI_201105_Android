package com.group5.android.fd.entity;

import java.util.List;

public class TaskGroupEntity {
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