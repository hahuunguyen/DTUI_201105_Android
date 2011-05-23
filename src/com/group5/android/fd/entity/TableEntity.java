package com.group5.android.fd.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A table
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class TableEntity extends AbstractEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4512857746232755898L;

	public int tableId;
	public String tableName;
	public boolean isBusy;
	public int lastOrderId;

	// get from JSONObject, from server
	public void parse(JSONObject jsonObject) throws JSONException {
		tableId = jsonObject.getInt("table_id");
		tableName = jsonObject.getString("table_name");
		isBusy = jsonObject.getInt("is_busy") > 0;
		lastOrderId = jsonObject.getInt("last_order_id");
	}
}
