package com.group5.android.fd.activity;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.R;
import com.group5.android.fd.adapter.FdCursorAdapter;

/**
 * The activity to display a list of rows from database.
 * 
 * @author Dao Hoang Son
 * 
 */
abstract public class DbBasedActivity extends ListActivity implements
		OnItemClickListener, OnItemLongClickListener {
	protected Cursor m_cursor;
	protected FdCursorAdapter m_cursorAdapter;
	protected DbAdapter m_dbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initLayout();

		initDb();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// initDb();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// closeDb();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		closeDb();
	}

	/**
	 * Prepares and get rows from database.
	 */
	protected void initDb() {
		m_dbAdapter = new DbAdapter(this);
		m_dbAdapter.open();

		m_cursor = initCursor();
		startManagingCursor(m_cursor);

		m_cursorAdapter = initAdapter();
		setListAdapter(m_cursorAdapter);
	}

	/**
	 * Closes the {@link DbAdapter} linked with this activity.
	 */
	protected void closeDb() {
		m_dbAdapter.close();
	}

	/**
	 * Gets the <code>Cursor</code> for our data. Subclass should implement this
	 * method to get the appropriate information.
	 * 
	 * @return a <code>Cursor</code>
	 */
	abstract protected Cursor initCursor();

	/**
	 * Gets the <code>CursorAdapter</code> for our data. Subclass should
	 * implement this method to get the appropriate class.
	 * 
	 * @return a <code>CursorAdapter</code>
	 */
	abstract protected FdCursorAdapter initAdapter();

	/**
	 * Initiates the layout (inflate from a layout resource named
	 * activity_main). And then maps all the object properties with their view
	 * instance. Finally, initiates required listeners on those views.
	 */
	protected void initLayout() {
		setContentView(R.layout.activity_list);

		ListView listView = getListView();

		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// subclass shoud implement this

		return false;
	}
}
