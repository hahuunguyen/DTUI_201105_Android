package com.group5.android.fd;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class FastDelivering extends Activity {
    /** Called when the activity is first created. */
	private Button loginButton;
	private EditText userName;
	private EditText password;
	protected boolean m_loggedIn = false;
	protected final String m_strUri = "http://10.0.2.2/xf1b/index.json"; // default uri
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLoginLayouts();
        initLoginListeners();
        
    }
    
    protected void initLoginLayouts(){
    	setContentView(R.layout.login);
    	loginButton = (Button)findViewById(R.id.loginButton);
    	userName = (EditText)findViewById ( R.id.userName);
    	password = (EditText)findViewById (R.id.password);
    }
    
    protected void initLoginListeners(){
    	loginButton.setOnClickListener( new OnClickListener(){
    		@Override
			public void onClick(View view) {
    			new AsyncTask<Void, Void, JSONObject>() {

    				@Override
    				protected JSONObject doInBackground(Void... arg0) {
    					List<NameValuePair> params = new ArrayList<NameValuePair>();
    					params.add(new BasicNameValuePair("login", "hanh"));//userName.toString()));
    					params.add(new BasicNameValuePair("password","12345"));// password.toString()));
    					
    					// maybe a bug
    					JSONObject jsonObject = HttpHelper.post(FastDelivering.this, m_strUri, "", params);
    					return jsonObject;
    				}

    				@Override
    				protected void onPostExecute(JSONObject jsonObject) {
    					m_loggedIn = true;
    					Intent intent = new Intent(FastDelivering.this,SessionView.class);
    					startActivity(intent);
    				}

    			}.execute();
    		}
    			
    	});
    }
}