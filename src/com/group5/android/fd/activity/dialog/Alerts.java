package com.group5.android.fd.activity.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Alerts {
	protected Context m_context;
	protected String mess;

	public Alerts(Context context, String mess) {
		m_context = context;
		this.mess = mess;
	}

	public Alerts(Context context) {
		m_context = context;
		this.mess = "Do you want to cancel this order ?";
	}

	public void showAlert() {

		AlertDialog.Builder builderDialog = new AlertDialog.Builder(m_context);
		builderDialog.setMessage(mess);
		builderDialog.setCancelable(true);
		builderDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						((Activity) m_context).finish();
					}
				});
		builderDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						arg0.dismiss();
					}
				});
		AlertDialog alert = builderDialog.create();
		alert.show();
	}
}
