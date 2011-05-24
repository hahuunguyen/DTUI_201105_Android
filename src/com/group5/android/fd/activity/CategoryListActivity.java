package com.group5.android.fd.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
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
		// set title for category list
		setCustomTitle(R.string.activitylist_choose_category);
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
}
