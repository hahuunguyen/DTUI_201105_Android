package com.group5.android.fd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.entity.OrderEntity;
import com.group5.android.fd.entity.TableEntity;

public class NewSessionActivity extends Activity {
	private static final int TABLE_LIST_REQUEST_CODE = 0;
	private static final int CATEGORY_LIST_REQUEST_CODE = 1;
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
		if ( requestCode == TABLE_LIST_REQUEST_CODE){
			Log.v(FdConfig.DEBUG_TAG, "onResult");
			String tableName = data.getStringExtra(TableEntity.TABLE_ENTITY_NAME);
			order.setTable(tableName);
			Log.v(FdConfig.DEBUG_TAG, "tableName:"+tableName);
			Intent categoryIntent = new Intent(NewSessionActivity.this, CategoryListActivity.class);
			startActivityForResult(categoryIntent,CATEGORY_LIST_REQUEST_CODE);
		}
	}
}
