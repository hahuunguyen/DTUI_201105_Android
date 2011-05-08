package com.group5.android.fd;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;

public class FdCursorAdapter extends CursorAdapter implements OnItemClickListener{
	/**
	 * The ID of the currently selected item.
	 */
	private long nSelectedID;
	private Context context;
	private Cursor itemCursor;
	
	public FdCursorAdapter (Context context, Cursor itemCursor){
		super (context,itemCursor);
		this.context = context;
		this.itemCursor = itemCursor;
		nSelectedID = Adapter.NO_SELECTION;
	}
	
	public long getSelectedID(){
		return nSelectedID;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		
		if ( view instanceof ItemViewTable){
			Intent intent = new Intent (context, CategoryView.class);
		}
		else {
			if ( view instanceof ItemViewCategory){
				Intent intent = new Intent (context, ItemItemView.class);
				String selectedCategory  = itemCursor.getString(position);
				intent.putExtra("selectedCategory", selectedCategory);
			}
		}
	}
	@Override
	public void bindView ( View view, Context context, Cursor cursor){
		String m_text = FdDBAdapter.getTextFromCursor(cursor);
		ItemView itemView = (ItemView) view;
		itemView.setTextView(m_text);
	}
	@Override
	public View newView (Context context, Cursor cursor, ViewGroup parent){
		String m_text = FdDBAdapter.getTextFromCursor(cursor);
		if (cursor.getColumnName(0).equalsIgnoreCase(FdDBAdapter.TABLELIST_KEY_TEXT)) {
			ItemViewTable tableView = new ItemViewTable ( context,m_text);
			return tableView;	
		}
		else {
			if (cursor.getColumnName(0).equalsIgnoreCase(FdDBAdapter.CATEGORIES_KEY_TEXT)){
				ItemViewCategory categoryView = new ItemViewCategory ( context,m_text);
				return categoryView;
			}
			else if (cursor.getColumnName(0).equalsIgnoreCase(FdDBAdapter.ITEM_KEY_TEXT)){
				ItemViewItem categoryView = new ItemViewItem ( context,m_text);
				return  categoryView;
			}
		}
		return null;
	}
	
}
