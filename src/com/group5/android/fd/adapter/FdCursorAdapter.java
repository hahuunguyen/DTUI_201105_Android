package com.group5.android.fd.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.view.Abstract;
import com.group5.android.fd.view.Category;
import com.group5.android.fd.view.Item;
import com.group5.android.fd.view.Table;

public class FdCursorAdapter extends CursorAdapter  implements OnItemClickListener {
	/**
	 * The ID of the currently selected item.
	 */
	protected int m_nSelectedPosition;
	protected Context m_context;
	protected Cursor itemCursor;

	public FdCursorAdapter(Context context, Cursor itemCursor) {
		super(context, itemCursor);
		m_context = context;
		this.itemCursor = itemCursor;
		m_nSelectedPosition = Adapter.NO_SELECTION;
	}

	public long getSelectedID() {
		return m_nSelectedPosition;
	}

	
	  @Override 
	  public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		  
	  }
	 

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String m_text = DbAdapter.getTextFromCursor(cursor);
		//Log.v(FdConfig., msg)
		Abstract itemView = (Abstract) view;
		itemView.setTextView(m_text);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		/*String m_text = DbAdapter.getTextFromCursor(cursor);
		if (cursor.getColumnName(DbAdapter.CATEGORY_INDEX_NAME).equalsIgnoreCase(
				DbAdapter.CATEGORY_KEY_NAME)) {
			Category categoryView = new Category(context, m_text);
			return categoryView;
		} else if (cursor.getColumnName(DbAdapter.ITEM_INDEX_NAME).equalsIgnoreCase(
				DbAdapter.ITEM_KEY_NAME)) {
			Item itemView = new Item(context, m_text);
			return itemView;
		}*/
		return null;
	}

}
