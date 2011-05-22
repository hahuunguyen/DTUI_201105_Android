package com.group5.android.fd.activity.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.group5.android.fd.R;

/**
 * Helper class to create <code>AlertDialog</code>
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class Alerts {
	protected Context m_context;
	protected int m_messageType;
	protected AlertDialog.Builder builderDialog;

	public Alerts(Context context) {
		m_context = context;
	}

	public Alerts(Context context, int messageType) {
		m_context = context;
		m_messageType = messageType;
		initAlerts();
	}

	// init option for alterts
	// built alert dialog with cancelable, 2 button
	protected void initAlerts() {
		builderDialog = new AlertDialog.Builder(m_context);
		builderDialog.setCancelable(false);
		builderDialog.setMessage(R.string.alters_confirm_delete);
		builderDialog.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						((Activity) m_context).finish();
					}
				});
		builderDialog.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						arg0.dismiss();
					}
				});

	}

	public void showAlert() {

		AlertDialog alert = builderDialog.create();
		alert.show();
	}
}
