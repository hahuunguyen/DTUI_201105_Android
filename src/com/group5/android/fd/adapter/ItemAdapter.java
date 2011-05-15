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

	/*
	 * 
	 * public void onItemClick(AdapterView<?> parent, View view, int position,
	 * long id) { m_nSelectedPosition = position; Intent intent = new Intent();
	 * Cursor itemCursor = (Cursor) getItem(position);
	 * intent.putExtra(ItemListActivity.ITEM_ENTITY_ID, itemCursor
	 * .getInt(DbAdapter.ITEM_INDEX_ID));
	 * intent.putExtra(ItemListActivity.ITEM_ENTITY_QUANTITY, 1); if (m_context
	 * instanceof Activity) { Activity activity = (Activity) m_context;
	 * activity.setResult(Activity.RESULT_OK, intent); activity.finish(); } }
	 * 
	 * public boolean onItemLongClick(AdapterView<?> parent, View view, int
	 * position, long id) { m_nSelectedPosition = position; return false; }
	 */

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
