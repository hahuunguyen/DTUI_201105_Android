package com.group5.android.fd;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class ItemItemView extends Activity{
	private Cursor arrCategoryList;
	private FdCursorAdapter itemAdapter;
	private FdDBAdapter itemDB;
	private ListView itemLayout;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTableLayouts();
    	//Initialize JokeDBAdapter
		itemDB = new FdDBAdapter(this);
		itemDB.open();
		Bundle extraBundle = this.getIntent().getExtras();
		String categoryFilter = (String) extraBundle.get("selectedCategory");
		arrCategoryList = itemDB.getItems(categoryFilter);
		startManagingCursor(arrCategoryList);
		
		itemLayout.setAdapter(itemAdapter);
		itemLayout.setOnItemClickListener(itemAdapter);
		registerForContextMenu(itemLayout);
		
		itemAdapter = new FdCursorAdapter(this, arrCategoryList);
        initTableListeners();
        
    }
	
	private void initTableLayouts(){
		 setContentView(R.layout.listview);
		 itemLayout = (ListView)findViewById(R.id.listItem);
	}
	
	private void initTableListeners() {
		
	}
}

