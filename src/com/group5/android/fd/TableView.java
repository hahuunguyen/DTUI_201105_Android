package com.group5.android.fd;



import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class TableView extends Activity{
	private Cursor arrTableList;
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
		arrTableList = itemDB.getAllTables();
		startManagingCursor(arrTableList);
		
		itemLayout.setAdapter(itemAdapter);
		itemLayout.setOnItemClickListener(itemAdapter);
		registerForContextMenu(itemLayout);
		
		itemAdapter = new FdCursorAdapter(this, arrTableList);
        initTableListeners();
        
        sync();
    }
	
	private void initTableLayouts(){
		 setContentView(R.layout.listview);
		 itemLayout = (ListView)findViewById(R.id.listItem);
	}
	
	private void initTableListeners() {
		
	}
	
	private void sync() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void...voids) {
				itemDB.sync();
				
				return null;
			}
			
		}.execute();
	}
}
