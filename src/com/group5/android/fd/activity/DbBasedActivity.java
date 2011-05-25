package com.group5.android.fd.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.group5.android.fd.DbAdapter;
import com.group5.android.fd.R;
import com.group5.android.fd.adapter.FdCursorAdapter;
import com.group5.android.fd.helper.BehaviorHelper;
import com.group5.android.fd.helper.BehaviorHelper.FlingReady;

/**
 * The activity to display a list of rows from database.
 * 
 * @author Dao Hoang Son
 * 
 */
abstract public class DbBasedActivity extends ListActivity implements
		OnItemClickListener, OnItemLongClickListener, FlingReady {

	protected LinearLayout m_vwCustomTitleContainer;
	protected TextView m_vwCustomTitle;

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

		m_vwCustomTitleContainer = (LinearLayout) findViewById(R.id.llCustomTitleContainer);
		m_vwCustomTitle = (TextView) findViewById(R.id.txtCustomTitle);

		ListView listView = getListView();
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);

		BehaviorHelper.setupFling(this, this);
	}

	/**
	 * Sets the custom title and turn the container's visibility ON
	 * 
	 * @param title
	 *            the custom title
	 */
	protected void setCustomTitle(String title) {
		m_vwCustomTitle.setText(title);
		m_vwCustomTitleContainer.setVisibility(View.VISIBLE);
	}

	/**
	 * Sets the custom title and turn the container's visibility ON
	 * 
	 * @param titleId
	 *            the resource id for the custom title
	 */
	protected void setCustomTitle(int titleId) {
		setCustomTitle(getString(titleId));
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// subclass shoud implement this

		return false;
	}

	@Override
	public void addFlingListener(OnTouchListener gestureListener) {
		getListView().setOnTouchListener(gestureListener);
	}

	@Override
	public void onFlighRight() {
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	@Override
	public void onFlingLeft() {
		// do nothing
	}

	@Override
	public void onFlingUp() {
		openOptionsMenu();
	}

	@Override
	public void onFlingDown() {
		// do nothing
	}
}
