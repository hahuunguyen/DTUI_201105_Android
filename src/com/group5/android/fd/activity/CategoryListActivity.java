package com.group5.android.fd.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;
import com.group5.android.fd.adapter.CategoryAdapter;
import com.group5.android.fd.adapter.FdCursorAdapter;
import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.view.CategoryView;

/**
 * The activity to display a list of categories.
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class CategoryListActivity extends DbBasedActivity {

	final public static String ACTIVITY_RESULT_NAME_CATEGORY_OBJ = "categoryObj";
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
	protected void initLayout() {
		super.initLayout();

		setCustomTitle("hiii");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.category_list, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_category_list_confirm:
			setResult(CategoryListActivity.RESULT_OK_BEFORE_CONFIRM);
			finish();

			return true;
		case R.id.menu_category_list_change:
			setResult(Activity.RESULT_CANCELED);
			finish();

			return true;
		}

		return false;
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

	// when key back result different code to show confirm list
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(CategoryListActivity.RESULT_OK_BEFORE_CONFIRM);
			finish();
			return true;
		}
		return false;
	}

}
