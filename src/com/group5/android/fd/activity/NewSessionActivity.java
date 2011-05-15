package com.group5.android.fd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.entity.OrderEntity;
import com.group5.android.fd.entity.TableEntity;

public class NewSessionActivity extends Activity {
	private static final int TABLE_LIST_REQUEST_CODE = 1;
	private static final int CATEGORY_LIST_REQUEST_CODE = 2;
	public static final String CATEGORY_ENTITY_ID = "category_id";
	private static final int ITEM_LIST_REQUEST_CODE = 3;
	protected OrderEntity order;
	
	
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		order = new OrderEntity();
		Intent tableIntent = new Intent ( this, TableListActivity.class);
		startActivityForResult(tableIntent, TABLE_LIST_REQUEST_CODE);
	}
	
	public void onResume(){
		super.onResume();
		
	}
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){

		if ( requestCode == TABLE_LIST_REQUEST_CODE  ){
			String tableName = data.getStringExtra(TableEntity.TABLE_ENTITY_NAME);
			order.setTable(tableName);
			Log.v(FdConfig.DEBUG_TAG, "tableName:"+tableName);
			Intent categoryIntent = new Intent(NewSessionActivity.this, CategoryListActivity.class);
			startActivityForResult(categoryIntent,CATEGORY_LIST_REQUEST_CODE);
		}
		
		if ( requestCode == CATEGORY_LIST_REQUEST_CODE){
			
			int categoryId = data.getIntExtra(CATEGORY_ENTITY_ID, 0);
			Intent itemIntent = new Intent ( this, ItemListActivity.class);
			itemIntent.putExtra(ItemListActivity.EXTRA_DATA_NAME_CATEGORY_ID, categoryId);
			startActivityForResult(itemIntent,ITEM_LIST_REQUEST_CODE);
			
		}
		
		if ( requestCode == ITEM_LIST_REQUEST_CODE){
			Log.v(FdConfig.DEBUG_TAG, "onItemResult");
			int itemName = data.getIntExtra(ItemListActivity.ITEM_ENTITY_ID, 0);
			int itemQuantity = data.getIntExtra(ItemListActivity.ITEM_ENTITY_QUANTITY, 0);
			order.addItem(itemName, itemQuantity);
			Intent categoryIntent = new Intent(NewSessionActivity.this, CategoryListActivity.class);
			startActivityForResult(categoryIntent,CATEGORY_LIST_REQUEST_CODE);
		}
	}
}
