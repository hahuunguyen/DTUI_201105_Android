package com.group5.android.fd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SessionView extends Activity{
	private Button newSession;
	private Button task;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSessionLayouts();
        initTableListeners();
        
    }
	
	private void initSessionLayouts(){
		 setContentView(R.layout.session);
		 newSession = (Button) findViewById (R.id.newSession);
		 task = (Button) findViewById ( R.id.task);
	}
	
	private void initTableListeners() {
		newSession.setOnClickListener(  new OnClickListener(){
    		@Override
			public void onClick(View view) {
    			Intent intent = new Intent ( SessionView.this,TableView.class);
    			startActivity(intent);
    			
    		}});
		task.setOnClickListener(  new OnClickListener(){
    		@Override
			public void onClick(View view) {
    			
    			
    		}});
	}
}
