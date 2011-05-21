package com.group5.android.fd.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.adapter.CategoryAdapter;
import com.group5.android.fd.adapter.FdCursorAdapter;
import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.view.CategoryView;

public class CategoryListActivity extends DbBasedActivity {

	final public static String ACTIVITY_RESULT_NAME_CATEGORY_OBJ = "categoryObj";
	public static final int CONFIRM_MENU_ITEM = Menu.FIRST;
	public static final String CONFIRM_MENU_STRING = "Confirm";
	public static final int CHANGE_MENU_ITEM = Menu.FIRST + 1;
	public static final String CHANGE_MENU_STRING = "Change Table";
	public static final int RESULT_OK_BEFORE_CONFIRM = -5;

	@Override
	protected Cursor initCursor() {
		return m_dbAdapter.getCategories();
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(CategoryListActivity.RESULT_OK_BEFORE_CONFIRM);
			finish();
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem confirmMenu = menu.add(Menu.NONE,
				CategoryListActivity.CONFIRM_MENU_ITEM, Menu.NONE,
				CategoryListActivity.CONFIRM_MENU_STRING);
		confirmMenu.setIcon(android.R.drawable.ic_menu_agenda);
		confirmMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				CategoryListActivity.this
						.setResult(CategoryListActivity.RESULT_OK_BEFORE_CONFIRM);
				CategoryListActivity.this.finish();
				return true;

			}
		});

		MenuItem changeMenu = menu.add(Menu.NONE,
				CategoryListActivity.CHANGE_MENU_ITEM, Menu.NONE,
				CategoryListActivity.CHANGE_MENU_STRING);
		changeMenu.setIcon(android.R.drawable.ic_menu_revert);
		changeMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				CategoryListActivity.this.setResult(Activity.RESULT_CANCELED);
				CategoryListActivity.this.finish();
				return true;

			}
		});
		return true;
	}

}
