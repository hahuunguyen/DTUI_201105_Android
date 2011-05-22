package com.group5.android.fd.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.group5.android.fd.R;
import com.group5.android.fd.helper.HttpRequestAsyncTask;

/**
 * The activity to display a list of information from remote server.
 * 
 * @author Dao Hoang Son
 * 
 */
abstract public class ServerBasedActivity extends ListActivity implements
		HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller, OnItemClickListener {

	protected LinearLayout m_vwCustomTitleContainer;
	protected TextView m_vwCustomTitle;

	protected HttpRequestAsyncTask m_hrat = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initLayout();
	}

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
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setOnItemClickListener(this);
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
	protected void onPause() {
		super.onPause();

		if (m_hrat != null) {
			m_hrat.dismissProgressDialog();
		}
	}

	@Override
	final public void addHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat != null && m_hrat != hrat) {
			m_hrat.dismissProgressDialog();
		}

		m_hrat = hrat;
	}

	@Override
	final public void removeHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat == hrat) {
			m_hrat = null;
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// subclass shoud implement this
	}
}
