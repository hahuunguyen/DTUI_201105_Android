package com.group5.android.fd;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.group5.android.fd.view.Abstract;
import com.group5.android.fd.view.Category;
import com.group5.android.fd.view.Item;
import com.group5.android.fd.view.Table;

public class FdCursorAdapter extends CursorAdapter implements
		OnItemClickListener {
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/*
		 * if ( view instanceof Table){ Intent intent = new Intent (context,
		 * CategoryView.class); } else { if ( view instanceof Category){ Intent
		 * intent = new Intent (context, ItemItemView.class); String
		 * selectedCategory = itemCursor.getString(position);
		 * intent.putExtra("selectedCategory", selectedCategory); } }
		 */
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String m_text = DbAdapter.getTextFromCursor(cursor);
		Abstract itemView = (Abstract) view;
		itemView.setTextView(m_text);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		String m_text = DbAdapter.getTextFromCursor(cursor);
		if (cursor.getColumnName(0).equalsIgnoreCase(
				DbAdapter.TABLELIST_KEY_TEXT)) {
			Table tableView = new Table(context, m_text);
			return tableView;
		} else {
			if (cursor.getColumnName(0).equalsIgnoreCase(
					DbAdapter.CATEGORIES_KEY_TEXT)) {
				Category categoryView = new Category(context, m_text);
				return categoryView;
			} else if (cursor.getColumnName(0).equalsIgnoreCase(
					DbAdapter.ITEM_KEY_TEXT)) {
				Item categoryView = new Item(context, m_text);
				return categoryView;
			}
		}
		return null;
	}

}
