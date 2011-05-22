package com.group5.android.fd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.view.ItemView;

public class ItemAdapter extends FdCursorAdapter {

	public ItemAdapter(Context context, Cursor itemCursor) {
		super(context, itemCursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ItemEntity item = new ItemEntity();
		item.parse(cursor);

		return new ItemView(context, item);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ItemEntity item = new ItemEntity();
		item.parse(cursor);

		((ItemView) view).setItem(item);
	}
}
