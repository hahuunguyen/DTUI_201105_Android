package com.group5.android.fd.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.adapter.CategoryAdapter;
import com.group5.android.fd.adapter.FdCursorAdapter;
import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.view.CategoryView;

public class CategoryListActivity extends DbBasedActivity {

	final public static String ACTIVITY_RESULT_NAME_CATEGORY_OBJ = "categoryObj";
	public static final int CONFIRM_MENU_ITEM = Menu.FIRST;
	public static final String CONFIRM_STRING = "Confirm";
	public static final int RESULT_OK_BEFORE_CONFIRM = -5;
	
	@Override
	protected Cursor initCursor() {
		return m_dbAdapter.getAllCategories();
	}

	@Override
	protected FdCursorAdapter initAdapter() {
		return new CategoryAdapter(this, m_cursor);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (view instanceof CategoryView) {
			CategoryView categoryView = (CategoryView) view;
			CategoryEntity category = categoryView.category;

			Intent intent = new Intent();
			intent.putExtra(
					CategoryListActivity.ACTIVITY_RESULT_NAME_CATEGORY_OBJ,
					category);

			Log.i(FdConfig.DEBUG_TAG, "A category has been selected: "
					+ category.categoryName);

			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		MenuItem confirmMenu = menu.add(Menu.NONE, CONFIRM_MENU_ITEM, Menu.NONE, CONFIRM_STRING);
		confirmMenu.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			public boolean onMenuItemClick(MenuItem item){
				CategoryListActivity.this.setResult(RESULT_OK_BEFORE_CONFIRM);
				CategoryListActivity.this.finish();
				return true;
				
			}
		});
		return true;
	}
	
}
