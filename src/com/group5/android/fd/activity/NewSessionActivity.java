package com.group5.android.fd.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.google.zxing.integration.android.IntentIntegrator;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.Main;
import com.group5.android.fd.R;
import com.group5.android.fd.activity.dialog.Alerts;
import com.group5.android.fd.activity.dialog.NumberPickerDialog;
import com.group5.android.fd.adapter.ConfirmAdapter;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.entity.OrderEntity;
import com.group5.android.fd.entity.OrderItemEntity;
import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.entity.UserEntity;
import com.group5.android.fd.entity.AbstractEntity.OnUpdatedListener;
import com.group5.android.fd.helper.FormattingHelper;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.ScanHelper;
import com.group5.android.fd.view.ConfirmView;

/**
 * The activity to display the confirm list and manage / call other activities
 * like {@link TableListActivity}, {@link CategoryListActivity} and
 * {@link ItemListActivity}
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class NewSessionActivity extends Activity implements OnDismissListener,
		OnClickListener, OnUpdatedListener,
		HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller,
		OnItemLongClickListener {

	final public static String EXTRA_DATA_NAME_TABLE_OBJ = "tableObj";
	final public static String EXTRA_DATA_NAME_USE_SCANNER = "useScanner";

	final public static int DIALOG_QUANTITY_REMOVER = 1;
	final public static String DIALOG_QUANTITY_SELECTOR_DUNBLE_NAME_ORDER_ITEM_OBJ = "orderItemObj";

	final public static int REQUEST_CODE_TABLE = 1;
	final public static int REQUEST_CODE_CATEGORY = 2;
	final public static int REQUEST_CODE_ITEM = 3;
	final public static int REQUEST_CODE_CONFIRM = 4;

	protected OrderEntity m_order = new OrderEntity();
	protected UserEntity m_user = null;
	protected boolean m_useScanner = false;
	protected HttpRequestAsyncTask m_hrat = null;

	// For display confirm View
	protected ConfirmAdapter m_confirmAdapter;
	protected ListView m_vwListView;
	protected Button m_vwConfirm;
	protected Button m_vwContinue;
	protected TextView m_vwTableName;
	protected TextView m_vwTotal;

	// a root activity, take care all new session actions
	// tableList activity, categoryList activity and itemList activity start and
	// end from here
	// get result and choose what to do
	// confirmList start in this activity

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get intent from Main
		Intent intent = getIntent();
		m_user = (UserEntity) intent
				.getSerializableExtra(Main.EXTRA_DATA_NAME_USER_OBJ);
		m_useScanner = intent.getBooleanExtra(
				NewSessionActivity.EXTRA_DATA_NAME_USE_SCANNER, false);
		Object tmpObj = intent
				.getSerializableExtra(NewSessionActivity.EXTRA_DATA_NAME_TABLE_OBJ);
		if (tmpObj != null && tmpObj instanceof TableEntity) {
			TableEntity table = (TableEntity) tmpObj;
			m_order.setTable(this, table);
		}

		boolean isRecovered = false;
		Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
		if (lastNonConfigurationInstance != null
				&& lastNonConfigurationInstance instanceof OrderEntity) {
			// found our long lost order, yay!
			m_order = (OrderEntity) lastNonConfigurationInstance;
			isRecovered = true;

			Log.i(FdConfig.DEBUG_TAG, "OrderEntity has been recovered");
		}

		initLayout();
		if (!isRecovered) {
			// this method should take care of the table for us
			// the additional check is used to prevent the category list to
			// display when orientation changes
			startCategoryList();
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// we want to preserve our order information when configuration is
		// change, say.. orientation change?
		return m_order;
	}

	@Override
	protected void onResume() {
		super.onResume();

		m_order.setOnUpdatedListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (m_hrat != null) {
			m_hrat.dismissProgressDialog();
		}
	}

	// when result_ok: choose what to do base on request_code
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		CategoryEntity pendingCategory = null;

		if (resultCode == Activity.RESULT_OK && data != null) {
			switch (requestCode) {
			case REQUEST_CODE_TABLE:
				TableEntity table = (TableEntity) data
						.getSerializableExtra(TableListActivity.ACTIVITY_RESULT_NAME_TABLE_OBJ);
				if (m_order.setTable(this, table)
						&& m_order.orderItems.isEmpty()) {
					// immediately display the category list if the order is
					// empty (for convenience reason)
					startCategoryList();
				}
				break;
			case REQUEST_CODE_CATEGORY:
				pendingCategory = (CategoryEntity) data
						.getSerializableExtra(CategoryListActivity.ACTIVITY_RESULT_NAME_CATEGORY_OBJ);
				startItemList(pendingCategory);
				break;
			case REQUEST_CODE_ITEM:
				OrderItemEntity orderItem = (OrderItemEntity) data
						.getSerializableExtra(ItemListActivity.ACTIVITY_RESULT_NAME_ORDER_ITEM_OBJ);
				m_order.addOrderItem(orderItem);
				startCategoryList();
				break;
			case IntentIntegrator.REQUEST_CODE:
				processScannedContents(requestCode, resultCode, data);
				break;
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			switch (requestCode) {
			case REQUEST_CODE_TABLE:
				if (m_order.getTableId() == 0 && m_order.orderItems.isEmpty()) {
					// user canceled the table list without previous table set
					// or any order item in place
					// so we have nothing to lose, just finish this activity
					finish();
				}
				break;
			case IntentIntegrator.REQUEST_CODE:
				// mark the flag that user have canceled the scanner
				m_useScanner = false;
				break;

			}
		}
	}

	/**
	 * Starts the {@link TableListActivity} activity for result.
	 * 
	 * @see #REQUEST_CODE_TABLE
	 */
	protected void startTableList() {
		Intent tableIntent = new Intent(this, TableListActivity.class);
		startActivityForResult(tableIntent,
				NewSessionActivity.REQUEST_CODE_TABLE);
	}

	/**
	 * Starts the {@link CategoryListActivity} activity for result. There are 2
	 * edge cases:
	 * <ol>
	 * <li>If no table has been selected, it will call {@link #startTableList()}
	 * instead</li>
	 * <li>If the last item was scanned, it will call {@link #startScanner()}
	 * again (and again)</li>
	 * </ol>
	 * 
	 * @see #REQUEST_CODE_CATEGORY
	 */
	protected void startCategoryList() {
		if (m_order.getTableId() == 0) {
			// before display the category list
			// we should have a valid table set
			startTableList();
		} else if (m_useScanner) {
			startScanner();
		} else {
			Intent categoryIntent = new Intent(this, CategoryListActivity.class);
			startActivityForResult(categoryIntent,
					NewSessionActivity.REQUEST_CODE_CATEGORY);
		}
	}

	/**
	 * Starts the {@link ItemListActivity} activity for result. The new activity
	 * will get a {@link CategoryEntity} via Intent.
	 * 
	 * @param category
	 *            the selected {@link CategoryEntity}
	 * @see ItemListActivity#EXTRA_DATA_NAME_CATEGORY_ID
	 * @see #REQUEST_CODE_ITEM
	 */
	protected void startItemList(CategoryEntity category) {
		Intent itemIntent = new Intent(this, ItemListActivity.class);
		itemIntent.putExtra(ItemListActivity.EXTRA_DATA_NAME_CATEGORY_ID,
				category.categoryId);
		startActivityForResult(itemIntent, NewSessionActivity.REQUEST_CODE_ITEM);
	}

	/**
	 * Starts the scanner activity using
	 * {@link IntentIntegrator#initiateScan(Activity)}
	 */
	protected void startScanner() {
		// this flag will be turned off if the user cancel the scanner
		// so we have to turn it on here to reset the check
		m_useScanner = true;
		IntentIntegrator.initiateScan(this);
	}

	/**
	 * Updates the views to display latest information about the
	 * {@link OrderEntity}
	 * 
	 * @see OrderEntity#orderItems
	 * @see OrderEntity#getTableName()
	 * @see OrderEntity#getPriceTotal()
	 */
	protected void startConfirmList() {
		m_confirmAdapter.notifyDataSetChanged();

		m_vwTableName.setText(m_order.getTableName());
		m_vwTotal
				.setText(FormattingHelper.formatPrice(m_order.getPriceTotal()));
		m_vwConfirm.setEnabled(m_order.orderItems.size() > 0);
	}

	/**
	 * Process scanned contents
	 */
	protected void processScannedContents(int requestCode, int resultCode,
			Intent data) {
		new ScanHelper(this, requestCode, resultCode, data, new Class[] {
				TableEntity.class, ItemEntity.class }) {

			@Override
			protected void onMatched(AbstractEntity entity) {
				boolean okToContinue = true;
				if (entity instanceof TableEntity) {
					okToContinue = m_order.setTable(NewSessionActivity.this,
							(TableEntity) entity);
				} else {
					m_order.addItem((ItemEntity) entity);
				}
				if (okToContinue) {
					startCategoryList();
				}
			}

			@Override
			protected void showAlertBox(AlertDialog dialog,
					AbstractEntity entity, boolean isMatched) {
				if (isMatched) {
					if (entity instanceof TableEntity) {
						dialog.setMessage(getString(
								R.string.press_ok_to_change_table_to_x,
								((TableEntity) entity).tableName));
					} else {
						dialog.setMessage(getString(
								R.string.press_ok_to_add_item_x,
								((ItemEntity) entity).itemName));
					}
				}

				super.showAlertBox(dialog, entity, isMatched);
			}

			@Override
			protected void onInvalid() {
				startCategoryList();
			}

			@Override
			protected void onCancel() {
				m_useScanner = false;
			}
		};
	}

	/**
	 * Initiates the layout (inflate from a layout resource named
	 * activity_main). And then maps all the object properties with their view
	 * instance. Finally, initiates required listeners on those views.
	 */
	public void initLayout() {
		setContentView(R.layout.activity_confirm);

		m_vwListView = (ListView) findViewById(R.id.lvOrderItems);
		m_vwConfirm = (Button) findViewById(R.id.btnConfirm);
		m_vwContinue = (Button) findViewById(R.id.btnContinue);
		m_vwTableName = (TextView) findViewById(R.id.txtTableName);
		m_vwTotal = (TextView) findViewById(R.id.totalPaid);

		m_confirmAdapter = new ConfirmAdapter(this, m_order);
		m_vwListView.setAdapter(m_confirmAdapter);

		m_vwTableName.setOnClickListener(this);
		m_vwConfirm.setOnClickListener(this);
		m_vwContinue.setOnClickListener(this);
		m_vwListView.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_session, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_new_session_change_table:
			startTableList();
			return true;
		case R.id.menu_new_session_scan:
			startScanner();
			return true;
		}

		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		switch (id) {
		case DIALOG_QUANTITY_REMOVER:
			dialog = new NumberPickerDialog(this);
			dialog.setOnDismissListener(this);
			break;
		}

		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		switch (id) {
		case DIALOG_QUANTITY_REMOVER:
			OrderItemEntity item = (OrderItemEntity) args
					.getSerializable(NewSessionActivity.DIALOG_QUANTITY_SELECTOR_DUNBLE_NAME_ORDER_ITEM_OBJ);
			((NumberPickerDialog) dialog).setEntity(item);
			break;
		}
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		if (arg0 instanceof NumberPickerDialog) {
			NumberPickerDialog numberPickerDialog = (NumberPickerDialog) arg0;
			OrderItemEntity orderItem = (OrderItemEntity) numberPickerDialog
					.getEntity();
			int newQuantity = numberPickerDialog.getQuantity();
			if (newQuantity < 0) {
				Toast.makeText(NewSessionActivity.this,
						R.string.numberpickerdialog_invalid_number_entered,
						Toast.LENGTH_SHORT);
			} else if (numberPickerDialog.isSet()) {
				m_order.setOrderItemQuantity(orderItem, newQuantity);
			}
		}
	}

	// listener for option menu
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.txtTableName:
			startTableList();
			break;
		case R.id.btnConfirm:
			m_order.submit(this, m_user.csrfToken);
			break;
		case R.id.btnContinue:
			startCategoryList();
			break;
		}
	}

	@Override
	public void onEntityUpdated(AbstractEntity entity, int target) {
		if (m_order == entity) {
			startConfirmList();

			if (m_order.isSynced(AbstractEntity.TARGET_REMOTE_SERVER)
					&& m_order.orderId > 0) {
				// order is submitted
				Toast.makeText(this,
						R.string.newsessionactivity_order_has_been_submitted,
						Toast.LENGTH_SHORT).show();

				finish();
			}
		}
	}

	@Override
	public void addHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat != null && m_hrat != hrat) {
			m_hrat.dismissProgressDialog();

		}

		m_hrat = hrat;
	}

	@Override
	public void removeHttpRequestAsyncTask(HttpRequestAsyncTask hrat) {
		if (m_hrat == hrat) {
			m_hrat = null;
		}
	}

	// listen Keycode_Back event and show alerts dialog if not empty
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!m_order.orderItems.isEmpty()) {
				new Alerts(this, R.string.alters_confirm_delete).showAlert();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	// show NumberPicker dialog for set quantity
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View v, int arg2,
			long arg3) {
		if (v instanceof ConfirmView) {
			ConfirmView confirmView = (ConfirmView) v;

			Bundle args = new Bundle();
			args
					.putSerializable(
							NewSessionActivity.DIALOG_QUANTITY_SELECTOR_DUNBLE_NAME_ORDER_ITEM_OBJ,
							confirmView.orderItem);
			showDialog(NewSessionActivity.DIALOG_QUANTITY_REMOVER, args);

			return true;
		}

		return false;
	}
}
