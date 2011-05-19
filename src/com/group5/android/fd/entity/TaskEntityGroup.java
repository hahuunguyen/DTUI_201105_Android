package com.group5.android.fd.entity;

import java.util.List;

public class TaskEntityGroup {
	public int group = 0;
	public List<TaskEntity> tasks = null;

	@Override
	public boolean equals(Object other) {
		if (other instanceof TaskEntityGroup) {
			return group == ((TaskEntityGroup) other).group;
		} else {
			return false;
		}
	}
}