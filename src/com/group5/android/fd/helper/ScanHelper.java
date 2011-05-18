package com.group5.android.fd.helper;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.entity.TableEntity;

abstract public class ScanHelper implements OnCancelListener, OnClickListener {

	public AbstractEntity entity = null;
	public boolean isMatched = false;

	@SuppressWarnings("unchecked")
	public ScanHelper(Context context, int requestCode, int resultCode,
			Intent data, Class[] classes) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, data);

		if (scanResult != null) {
			String exceptionMessage = null;

			try {
				entity = ScanHelper.parseScannedContents(scanResult
						.getContents());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				exceptionMessage = e.getMessage();
			}

			AlertDialog.Builder b = new AlertDialog.Builder(context);
			b.setOnCancelListener(this);
			b.setPositiveButton(R.string.ok, this);

			if (entity != null) {
				b.setTitle(R.string.qrcode_found);

				if (classes.length > 0) {
					for (int i = 0; i < classes.length; i++) {
						if (entity.getClass().equals(classes[i])) {
							isMatched = true;
						}
					}
				} else {
					// no filter is set
					// easy stuff
					isMatched = true;
				}

				if (isMatched) {
					b.setMessage(R.string.press_ok_to_proceed);
				} else {
					b.setMessage(entity.getClass().getSimpleName());
				}
			} else if (exceptionMessage == null) {
				b.setTitle(R.string.please_scan_a_valid_qrcode);
				b.setMessage(scanResult.getContents());
			} else {
				b.setTitle(R.string.problem_reading_qrcode);
				b.setMessage(exceptionMessage);
			}

			b.show();
		} else {
			onInvalid();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (entity != null) {
			if (isMatched) {
				onMatched(entity);
			} else {
				onMismatched(entity);
			}
		} else {
			onNotFound();
		}
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		onCancel();
	}

	abstract protected void onMatched(AbstractEntity entity);

	protected void onMismatched(AbstractEntity entity) {
		// auto fallback
		onInvalid();
	}

	protected void onNotFound() {
		// auto fallback
		onInvalid();
	}

	abstract protected void onInvalid();

	protected void onCancel() {
		// auto fallback
		onInvalid();
	}

	public static AbstractEntity parseScannedContents(String contents)
			throws Exception {
		AbstractEntity entity = null;

		Log.d(FdConfig.DEBUG_TAG, "Trying to parse scanned contents: "
				+ contents);

		JSONObject jsonObject = new JSONObject(contents);
		JSONArray names = jsonObject.names();

		for (int i = 0; i < names.length(); i++) {
			String name = names.getString(i);

			if (name.equals("table")) {
				TableEntity table = new TableEntity();
				table.parse(jsonObject.getJSONObject(name));

				return table;
			} else if (name.equals("item")) {
				ItemEntity item = new ItemEntity();
				item.parse(jsonObject.getJSONObject(name));

				return item;
			}
		}

		return entity;
	}
}
