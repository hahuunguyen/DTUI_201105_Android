package com.group5.android.fd.activity;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.group5.android.fd.FdConfig;
import com.group5.android.fd.Main;
import com.group5.android.fd.R;
import com.group5.android.fd.activity.dialog.QuantityRemoverDialog;
import com.group5.android.fd.adapter.ConfirmAdapter;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.entity.ItemEntity;
import com.group5.android.fd.entity.OrderEntity;
import com.group5.android.fd.entity.OrderItemEntity;
import com.group5.android.fd.entity.TableEntity;
import com.group5.android.fd.entity.UserEntity;
import com.group5.android.fd.entity.AbstractEntity.OnUpdatedListener;
import com.group5.android.fd.helper.HttpRequestAsyncTask;
import com.group5.android.fd.helper.ScanHelper;
import com.group5.android.fd.helper.UriStringHelper;

public class NewSessionActivity extends Activity implements OnDismissListener,
		OnClickListener, OnUpdatedListener,
		HttpRequestAsyncTask.OnHttpRequestAsyncTaskCaller {

	final public static String EXTRA_DATA_NAME_TABLE_OBJ = "tableObj";
	final public static String EXTRA_DATA_NAME_USE_SCANNER = "useScanner";

	final public static int REQUEST_CODE_TABLE = 1;
	final public static int REQUEST_CODE_CATEGORY = 2;
	final public static int REQUEST_CODE_ITEM = 3;
	final public static int REQUEST_CODE_CONFIRM = 4;

	public static final String POST_ORDER_STRING = "Go";
	public static final String CHANGE_ORDER_STRING = "Change";
	public static final int REMOVE_ITEM_MENU = Menu.FIRST;
	public static final String REMOVE_ITEM_MENU_STRING = "Remove";

	protected OrderEntity order = new OrderEntity();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get intent from Main
		Intent intent = getIntent();
		m_user = (UserEntity) intent
				.getSerializableExtra(Main.INSTANCE_STATE_KEY_USER_OBJ);
		m_useScanner = intent.getBooleanExtra(
				NewSessionActivity.EXTRA_DATA_NAME_USE_SCANNER, false);

		Object tmpObj = intent
				.getSerializableExtra(NewSessionActivity.EXTRA_DATA_NAME_TABLE_OBJ);
		if (tmpObj != null && tmpObj instanceof TableEntity) {
			TableEntity table = (TableEntity) tmpObj;
			order.setTable(table);
		}

		Object lastNonConfigurationInstance = getLastNonConfigurationInstance();
		if (lastNonConfigurationInstance != null
				&& lastNonConfigurationInstance instanceof OrderEntity) {
			// found our long lost order, yay!
			order = (OrderEntity) lastNonConfigurationInstance;

			Log.i(FdConfig.DEBUG_TAG, "OrderEntity has been recovered;");
		}

		initLayout();
		initListeners();

		// this method should take care of the table for us
		startCategoryList();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// we want to preserve our order information when configuration is
		// change, say.. orientation change?
		return order;
	}

	@Override
	protected void onResume() {
		super.onResume();

		order.setOnUpdatedListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (m_hrat != null) {
			m_hrat.dismissProgressDialog();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		CategoryEntity pendingCategory = null;

		if (resultCode == Activity.RESULT_OK && data != null) {
			switch (requestCode) {
			case REQUEST_CODE_TABLE:
				TableEntity table = (TableEntity) data
						.getSerializableExtra(TableListActivity.ACTIVITY_RESULT_NAME_TABLE_OBJ);
				order.setTable(table);
				startCategoryList();
				break;
			case REQUEST_CODE_CATEGORY:
				pendingCategory = (CategoryEntity) data
						.getSerializableExtra(CategoryListActivity.ACTIVITY_RESULT_NAME_CATEGORY_OBJ);
				startItemList(pendingCategory);
				break;
			case REQUEST_CODE_ITEM:
				OrderItemEntity orderItem = (OrderItemEntity) data
						.getSerializableExtra(ItemListActivity.ACTIVITY_RESULT_NAME_ORDER_ITEM_OBJ);
				order.addOrderItem(orderItem);
				startCategoryList();
				break;
			case IntentIntegrator.REQUEST_CODE:
				new ScanHelper(this, requestCode, resultCode, data,
						new Class[] { ItemEntity.class }) {

					@Override
					protected void onMatched(AbstractEntity entity) {
						order.addItem((ItemEntity) entity);
						startCategoryList();
					}

					@Override
					protected void onMismatched(AbstractEntity entity) {
						// we don't want ti fallback to onInvalid
						// because we want to let user try again :)
						startCategoryList();
					}

					@Override
					protected void onInvalid() {
						m_useScanner = false;
						startCategoryList();
					}
				};
				break;
			}
		} else if (resultCode == Activity.RESULT_CANCELED) {
			// xu ly khi activity bi huy boi back
			switch (requestCode) {
			case REQUEST_CODE_TABLE:
				finish();
				break;
			case REQUEST_CODE_CATEGORY:
				startTableList();
				break;
			case REQUEST_CODE_ITEM:
				startCategoryList();
				break;
			case IntentIntegrator.REQUEST_CODE:
				m_useScanner = false;
				break;

			}
		}
	}

	protected void startTableList() {
		Intent tableIntent = new Intent(this, TableListActivity.class);
		startActivityForResult(tableIntent,
				NewSessionActivity.REQUEST_CODE_TABLE);
	}

	protected void startCategoryList() {
		if (order.getTableId() == 0) {
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

	protected void startItemList(CategoryEntity category) {
		Intent itemIntent = new Intent(this, ItemListActivity.class);
		itemIntent.putExtra(ItemListActivity.EXTRA_DATA_NAME_CATEGORY_ID,
				category.categoryId);
		startActivityForResult(itemIntent, NewSessionActivity.REQUEST_CODE_ITEM);
	}

	protected void startScanner() {
		IntentIntegrator.initiateScan(this);
	}

	protected void startConfirmList() {
		m_confirmAdapter.notifyDataSetChanged();

		m_vwConfirm.setText(NewSessionActivity.POST_ORDER_STRING);
		m_vwContinue.setText(NewSessionActivity.CHANGE_ORDER_STRING);
		m_vwTableName.setText(order.getTableName());
		m_vwTotal.setText(String.format("%s", order.getPriceTotal()));
	}

	protected void postOrder() {
		String newOrderUrl = UriStringHelper.buildUriString("new-order");
		List<NameValuePair> params = order.getOrderAsParams();

		new HttpRequestAsyncTask(this, newOrderUrl, m_user.csrfToken, params) {

			@Override
			protected Object process(JSONObject jsonObject) {
				try {
					JSONObject order = jsonObject.getJSONObject("order");
					int orderId = order.getInt("order_id");
					return orderId > 0;
				} catch (JSONException e) {
					// invalid response from server!
				}

				return false;
			}

			@Override
			protected void onSuccess(JSONObject jsonObject, Object processed) {
				boolean confirmed = false;

				if (processed != null && processed instanceof Boolean) {
					confirmed = (Boolean) processed;
				}

				if (confirmed) {
					Toast
							.makeText(
									NewSessionActivity.this,
									R.string.newsessionactivity_order_has_been_submitted,
									Toast.LENGTH_LONG).show();
					NewSessionActivity.this.finish();
				} else {
					onError(
							jsonObject,
							getResources()
									.getString(
											R.string.newsessionactivity_order_can_not_submitted));
				}
			}
		}.execute();
	}

	/*
	 * Cai dat danh cho confirm list Bao gom cac thiet lap lay out, listener va
	 * ham post du lieu order toi server
	 */
	public void initLayout() {
		setContentView(R.layout.activity_confirm);
		m_vwListView = (ListView) findViewById(R.id.m_vwListView);
		m_vwConfirm = (Button) findViewById(R.id.btnConfirm);
		m_vwContinue = (Button) findViewById(R.id.btnContinue);
		m_vwTableName = (TextView) findViewById(R.id.tblName);
		m_vwTotal = (TextView) findViewById(R.id.totalPaid);

		m_confirmAdapter = new ConfirmAdapter(this, order);
		m_vwListView.setAdapter(m_confirmAdapter);

	}

	public void initListeners() {
		m_vwConfirm.setOnClickListener(this);
		m_vwContinue.setOnClickListener(this);
		registerForContextMenu(m_vwListView);
		m_vwListView.setOnItemLongClickListener(m_confirmAdapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		showDialog(ItemListActivity.DIALOG_QUANTITY_SELECTOR);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		switch (id) {
		case ItemListActivity.DIALOG_QUANTITY_SELECTOR:
			dialog = new QuantityRemoverDialog(this);
			dialog.setOnDismissListener(this);
			break;
		}

		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		switch (id) {
		case ItemListActivity.DIALOG_QUANTITY_SELECTOR:

			((QuantityRemoverDialog) dialog).setDialogDefault();
			break;
		}
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		if (arg0 instanceof QuantityRemoverDialog) {
			int selectedPosition = m_confirmAdapter.getSelectedPosition();
			order.removeOrderItem(selectedPosition,
					((QuantityRemoverDialog) arg0).getQuantity());
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnConfirm:
			postOrder();
			break;
		case R.id.btnContinue:
			startCategoryList();
			break;
		}
	}

	@Override
	public void onEntityUpdated(AbstractEntity entity, int target) {
		startConfirmList();
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
}
