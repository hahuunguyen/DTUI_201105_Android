package com.group5.android.fd.view;

import android.content.Context;

import com.group5.android.fd.entity.TableEntity;

/**
 * A view for {@link TableEntity}
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class TableView extends AbstractView {
	public TableEntity table;

	public TableView(Context context, TableEntity table) {
		super(context);

		setTable(table);
	}

	/**
	 * Setup the view to display a new {$link TableEntity}
	 * 
	 * @param table
	 *            the new table
	 */
	public void setTable(TableEntity table) {
		this.table = table;

		setTextViews(table.tableName, "");
	}

}
