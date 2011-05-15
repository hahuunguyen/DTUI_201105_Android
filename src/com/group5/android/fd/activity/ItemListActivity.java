package com.group5.android.fd.activity;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.adapter.ItemCursorAdapter;

public class ItemListActivity extends DbBasedActivity {
	final public static String EXTRA_DATA_NAME_CATEGORY_ID = "categoryId";
	public static final int QUANTITY_SUBMENUITEM = Menu.FIRST;
	public static final String ITEM_ENTITY_ID = "itemName";
	public static final String ITEM_ENTITY_QUANTITY = "itemQuantity";
	
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		registerForContextMenu(getListView());
	}
	
	
	@Override
	protected Cursor initCursor() {
		Intent intent = getIntent();
		int categoryId = intent
				.getIntExtra(ItemListActivity.EXTRA_DATA_NAME_CATEGORY_ID, 0);
		Log.v(FdConfig.DEBUG_TAG, "categoryName:"+categoryId);
		return m_dbAdapter.getItems(categoryId);
	}
	
	
	protected void initDb() {
		m_dbAdapter = new DbAdapter(this);
		m_dbAdapter.open();

		m_cursor = initCursor();
		startManagingCursor(m_cursor);

		Log.i(FdConfig.DEBUG_TAG, "Cursor is init'd. Rows: "
				+ m_cursor.getCount());

		m_cursorAdapter = new ItemCursorAdapter(this, m_cursor);
		setListAdapter(m_cursorAdapter);
	}
	
	
	
	
	/*
	 * tao submenu gom 5 lua chon ve so luong
	 * so luong tang tu 2->6 va 1 lua chon nhap so
	 */
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		SubMenu sm = menu.addSubMenu( QUANTITY_SUBMENUITEM,QUANTITY_SUBMENUITEM, Menu.NONE, "Quantity");
		MenuItem quantity_2 = sm.add(QUANTITY_SUBMENUITEM, QUANTITY_SUBMENUITEM+2, QUANTITY_SUBMENUITEM+2, "itemName"+2);
		MenuItem quantity_3 = sm.add(QUANTITY_SUBMENUITEM, QUANTITY_SUBMENUITEM+3, QUANTITY_SUBMENUITEM+3, "itemName"+3);
		MenuItem quantity_4 = sm.add(QUANTITY_SUBMENUITEM, QUANTITY_SUBMENUITEM+4, QUANTITY_SUBMENUITEM+4, "itemName"+4);
		MenuItem quantity_5 = sm.add(QUANTITY_SUBMENUITEM, QUANTITY_SUBMENUITEM+5, QUANTITY_SUBMENUITEM+5, "itemName"+5);
		MenuItem more = sm.add(QUANTITY_SUBMENUITEM, QUANTITY_SUBMENUITEM+5, QUANTITY_SUBMENUITEM+5, "more");
		
		quantity_2.setOnMenuItemClickListener((new OnMenuItemClickListener(){
			/*
			 * su dung Adapter de lay ten Item 
			 * putExtra itemName va itemQuantity
			 */
			public boolean onMenuItemClick (MenuItem item){
				Intent intent = new Intent();
				
				 
				return true;
			}
		}));
		
	}
	
	

}
