package com.group5.android.fd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;

import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.entity.ItemEntity;

/**
 * Adapter for cursor based entities (like {@link CategoryEntity} and
 * {@link ItemEntity})
 * 
 * @author Dao Hoang Son
 * 
 */
abstract public class FdCursorAdapter extends CursorAdapter {
	protected Context m_context;
	protected Cursor itemCursor;

	// based adapter
	public FdCursorAdapter(Context context, Cursor itemCursor) {
		super(context, itemCursor);
		m_context = context;
		this.itemCursor = itemCursor;
	}
}
