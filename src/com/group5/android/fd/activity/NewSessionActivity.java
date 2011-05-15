package com.group5.android.fd.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class NewSessionActivity extends Activity {
	private static final int TABLE_LIST_REQUEST_CODE = 0;
	
	@Override
	public void onCreate (Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Intent tableIntent = new Intent ( this, TableListActivity.class);
		startActivityForResult(tableIntent, TABLE_LIST_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if ( requestCode == TABLE_LIST_REQUEST_CODE){
			
		}
	}
}
