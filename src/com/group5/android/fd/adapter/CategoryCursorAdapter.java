package com.group5.android.fd.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.activity.NewSessionActivity;
import com.group5.android.fd.view.Category;

public class CategoryCursorAdapter extends FdCursorAdapter {
	public CategoryCursorAdapter(Context context, Cursor categoryCursor) {
		super(context, categoryCursor);
	}
	
	/* listener khi mot category duoc click
	 *  lay id category do va truyen putExtra cho gia tri tra ve, ket thuc Activity
	 */
	 public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		 Intent intent = new Intent();
		 Cursor categoryCursor= (Cursor) getItem(position);
		 int categoryId = categoryCursor.getInt(DbAdapter.CATEGORY_INDEX_ID);
		 Log.v(FdConfig.DEBUG_TAG, "categoryName:"+categoryId );
		 intent.putExtra(NewSessionActivity.CATEGORY_ENTITY_ID, categoryId );
		 if ( m_context instanceof Activity){
			 Activity activity = (Activity)m_context;
			 activity.setResult(Activity.RESULT_OK, intent);
			 activity.finish();
		 }
	  }
	 
	 public View newView(Context context, Cursor cursor, ViewGroup parent) {
		 String m_text = DbAdapter.getTextFromCursor(cursor);
		 Category categoryView = new Category(context, m_text);
		 return categoryView;
	 }
}
