package com.group5.android.fd;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CursorAdapter;

import com.group5.android.fd.view.Abstract;
import com.group5.android.fd.view.Category;
import com.group5.android.fd.view.Item;

public class FdCursorAdapter extends CursorAdapter { // implements
	// OnItemClickListener {
	/**
	 * The ID of the currently selected item.
	 */
	private long nSelectedID;
	private Context context;
	private Cursor itemCursor;

	public FdCursorAdapter(Context context, Cursor itemCursor) {
		super(context, itemCursor);
		this.context = context;
		this.itemCursor = itemCursor;
		nSelectedID = Adapter.NO_SELECTION;
	}

	public long getSelectedID() {
		return nSelectedID;
	}

	/*
	 * @Override public void onItemClick(AdapterView<?> parent, View view, int
	 * position, long id) {
	 * 
	 * if ( view instanceof Table){ Intent intent = new Intent (context,
	 * CategoryView.class); } else { if ( view instanceof Category){ Intent
	 * intent = new Intent (context, ItemItemView.class); String
	 * selectedCategory = itemCursor.getString(position);
	 * intent.putExtra("selectedCategory", selectedCategory); } }
	 * 
	 * }
	 */

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String m_text = DbAdapter.getTextFromCursor(cursor);
		//Log.v(FdConfig., msg)
		Abstract itemView = (Abstract) view;

		Log.d(FdConfig.DEBUG_TAG, "view is null: " + (view == null));
		Log.d(FdConfig.DEBUG_TAG, "itemView is null: " + (itemView == null));
		Log.d(FdConfig.DEBUG_TAG, "m_text is null: " + (m_text == null));
		Log.d(FdConfig.DEBUG_TAG, "m_text: " + m_text);

		itemView.setTextView(m_text);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		String m_text = DbAdapter.getTextFromCursor(cursor);
		if (cursor.getColumnName(1).equalsIgnoreCase(
				DbAdapter.CATEGORY_KEY_NAME)) {
			Category categoryView = new Category(context, m_text);
			return categoryView;
		} else if (cursor.getColumnName(1).equalsIgnoreCase(
				DbAdapter.ITEM_KEY_NAME)) {
			Item itemView = new Item(context, m_text);
			return itemView;
		}
		return null;
	}

}
