package com.group5.android.fd.activity.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;

import com.group5.android.fd.R;

public class Alerts {
	protected Context m_context;

	public Alerts(Context context) {
		m_context = context;
	}

	// built alert dialog with cancelable, 2 button
	public void showAlert() {
		Resources r = m_context.getResources();
		AlertDialog.Builder builderDialog = new AlertDialog.Builder(m_context);
		builderDialog.setMessage(r.getString(R.string.alters_confirm_delete));
		builderDialog.setCancelable(true);
		builderDialog.setPositiveButton(r.getString(R.string.yes),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						((Activity) m_context).finish();
					}
				});
		builderDialog.setNegativeButton(r.getString(R.string.no),
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
