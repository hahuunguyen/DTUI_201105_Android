package com.group5.android.fd.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.TableEntity;

public class ScanHelper {
	public static AbstractEntity parseScannedContents(String contents) {
		AbstractEntity entity = null;

		Log.d(FdConfig.DEBUG_TAG, "Trying to parse scanned contents: "
				+ contents);

		try {
			JSONObject jsonObject = new JSONObject(contents);
			JSONArray names = jsonObject.names();

			for (int i = 0; i < names.length(); i++) {
				String name = names.getString(i);
				if (name.equals("table")) {
					TableEntity table = new TableEntity();
					table.parse(jsonObject.getJSONObject(name));

					return table;
				}
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return entity;
	}
}
