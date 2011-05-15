package com.group5.android.fd.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.activity.ItemListActivity;
import com.group5.android.fd.view.Item;

public class ItemCursorAdapter extends FdCursorAdapter implements OnItemLongClickListener{
	
	
	public ItemCursorAdapter(Context context, Cursor itemCursor){
		super(context, itemCursor);
	}
	
	/* Truong hop chi click 1 lan
	 * Mac dinh so luong la 1
	 *  gui di item id va item quantity
	 */
	 public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		 m_nSelectedPosition = position;
		 Intent intent = new Intent();
		 Cursor itemCursor= (Cursor)getItem(position);
		 intent.putExtra( ItemListActivity.ITEM_ENTITY_ID, itemCursor.getInt(DbAdapter.ITEM_INDEX_ID));
		 intent.putExtra(ItemListActivity.ITEM_ENTITY_QUANTITY, 1);
		 if ( m_context instanceof Activity){
			 Activity activity = (Activity)m_context;
			 activity.setResult(Activity.RESULT_OK, intent);
			 activity.finish();
		 }
	  }
	 
	 
	 public boolean onItemLongClick (AdapterView<?> parent, View view, int position, long id){
		 m_nSelectedPosition = position;
		 return false;
	 }
	 
	 public View newView(Context context, Cursor cursor, ViewGroup parent) {
		 String m_text = DbAdapter.getTextFromCursor(cursor);
		 Item itemView = new Item(context, m_text);
		 return itemView;
	 }
}
