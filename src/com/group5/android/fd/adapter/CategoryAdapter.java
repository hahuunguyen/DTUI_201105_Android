package com.group5.android.fd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.view.CategoryView;

public class CategoryAdapter extends FdCursorAdapter {
	public CategoryAdapter(Context context, Cursor categoryCursor) {
		super(context, categoryCursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		CategoryEntity category = new CategoryEntity();
		category.parse(cursor);

		return new CategoryView(context, category);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		CategoryEntity category = new CategoryEntity();
		category.parse(cursor);

		((CategoryView) view).setCategory(category);
	}
}
