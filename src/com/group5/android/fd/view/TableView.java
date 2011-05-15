package com.group5.android.fd.view;

import android.content.Context;

import com.group5.android.fd.entity.TableEntity;

public class TableView extends AbstractView {
	public TableEntity table;

	public TableView(Context context, TableEntity table) {
		super(context);
		setTable(table);
	}

	public void setTable(TableEntity table) {
		this.table = table;
		setTextView(table.tableName);
	}
}
