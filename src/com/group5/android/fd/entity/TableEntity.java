package com.group5.android.fd.entity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import com.group5.android.fd.DbAdapter;

public class TableEntity {
	public int tableId;
	public String tableName;
	public static final String TABLE_ENTITY_NAME = "Table_Entity_Name";
	public void parse(JSONObject jsonObject) throws JSONException {
		tableId = jsonObject.getInt("table_id");
		tableName = jsonObject.getString("table_name");
	}
	
	public int getId(){
		return tableId;
	}
	
	public String getName(){
		return tableName;
	}
}
