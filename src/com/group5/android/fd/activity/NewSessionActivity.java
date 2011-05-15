package com.group5.android.fd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.entity.OrderEntity;
import com.group5.android.fd.entity.OrderItemEntity;
import com.group5.android.fd.entity.TableEntity;

public class NewSessionActivity extends Activity {
	final public static int REQUEST_CODE_TABLE = 1;
	final public static int REQUEST_CODE_CATEGORY = 2;
	final public static int REQUEST_CODE_ITEM = 3;

	protected OrderEntity order = new OrderEntity();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
		if (lastNonConfigurationInstance != null
				&& lastNonConfigurationInstance instanceof OrderEntity) {
			// found our long lost order, yay!
			order = (OrderEntity) lastNonConfigurationInstance;

			Log.i(FdConfig.DEBUG_TAG, "OrderEntity has been recovered;");
		}

		// this method should take care of the table for us
		startCategoryList();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// we want to preserve our order information when configuration is
		// change, say.. orientation change?
		return order;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		CategoryEntity pendingCategory = null;

		if (resultCode == Activity.RESULT_OK && data != null) {
			switch (requestCode) {
			case REQUEST_CODE_TABLE:
				TableEntity table = (TableEntity) data
						.getSerializableExtra(TableListActivity.ACTIVITY_RESULT_NAME_TABLE_OBJ);
				order.setTable(table);
				break;
			case REQUEST_CODE_CATEGORY:
				pendingCategory = (CategoryEntity) data
						.getSerializableExtra(CategoryListActivity.ACTIVITY_RESULT_NAME_CATEGORY_OBJ);
				break;
			case REQUEST_CODE_ITEM:
				OrderItemEntity orderItem = (OrderItemEntity) data
						.getSerializableExtra(ItemListActivity.ACTIVITY_RESULT_NAME_ORDER_ITEM_OBJ);
				order.addOrderItem(orderItem);
				break;
			}
		}

		if (pendingCategory == null) {
			// no pending category, yet. Display the category list
			startCategoryList();
		} else {
			// a category is pending, display the item list of that category
			startItemList(pendingCategory);
		}
	}

	protected void startTableList() {
		Intent tableIntent = new Intent(this, TableListActivity.class);
		startActivityForResult(tableIntent,
				NewSessionActivity.REQUEST_CODE_TABLE);
	}

	protected void startCategoryList() {
		if (order.getTableId() == 0) {
			// before display the category list
			// we should have a valid table set
			startTableList();
		} else {
			Intent categoryIntent = new Intent(this, CategoryListActivity.class);
			startActivityForResult(categoryIntent,
					NewSessionActivity.REQUEST_CODE_CATEGORY);
		}
	}

	protected void startItemList(CategoryEntity category) {
		Intent itemIntent = new Intent(this, ItemListActivity.class);
		itemIntent.putExtra(ItemListActivity.EXTRA_DATA_NAME_CATEGORY_ID,
				category.categoryId);
		startActivityForResult(itemIntent, NewSessionActivity.REQUEST_CODE_ITEM);
	}
}
