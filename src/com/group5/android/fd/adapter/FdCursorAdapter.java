package com.group5.android.fd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;

abstract public class FdCursorAdapter extends CursorAdapter {
	protected Context m_context;
	protected Cursor itemCursor;

	public FdCursorAdapter(Context context, Cursor itemCursor) {
		super(context, itemCursor);
		m_context = context;
		this.itemCursor = itemCursor;
	}
}
