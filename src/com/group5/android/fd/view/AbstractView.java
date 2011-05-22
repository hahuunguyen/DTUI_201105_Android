package com.group5.android.fd.view;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.helper.ImageHelper;

abstract public class AbstractView extends LinearLayout {

	protected Context m_context;

	protected ImageView m_vwImg;
	protected TextView m_vwName;
	protected TextView m_vwInfo;

	// densityDpi to get window size, use for choose suitable image
	protected static int m_densityDpi = 0;

	protected String m_lastRequestedImage = null;

	public AbstractView(Context context) {
		super(context);

		m_context = context;
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		li.inflate(getLayoutResourceId(), this, true);

		m_vwImg = (ImageView) findViewById(R.id.imgItem);
		m_vwName = (TextView) findViewById(R.id.txtItemName);
		m_vwInfo = (TextView) findViewById(R.id.txtItemInfo);

		if (AbstractView.m_densityDpi == 0) {
			DisplayMetrics metrics = new DisplayMetrics();
			((Activity) m_context).getWindowManager().getDefaultDisplay()
					.getMetrics(metrics);
			AbstractView.m_densityDpi = metrics.densityDpi;
		}
	}

	protected int getLayoutResourceId() {
		return R.layout.view_abstract;
	}

	protected void setTextViews(String name, String info) {
		m_vwName.setText(name);
		m_vwInfo.setText(info);
	}

	protected String chooseImageSize(AbstractEntity entity) {
		switch (AbstractView.m_densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			return entity.imageL;
		case DisplayMetrics.DENSITY_MEDIUM:
			return entity.imageM;
		case DisplayMetrics.DENSITY_HIGH:
			return entity.imageH;
		case 320:
			// DENSITY_XHIGH
			return entity.imageXH;
		default:
			return entity.imageM;
		}
	}

	protected void setImage(String imageUrl) {
		if (imageUrl == null) {
			return;
		}

		if (m_lastRequestedImage == null
				|| m_lastRequestedImage.equals(imageUrl) == false) {

			m_lastRequestedImage = imageUrl;

			File cachedFile = ImageHelper.getCachedFile(imageUrl);

			if (cachedFile != null) {
				setImage(cachedFile);
			} else {
				new ImageHelper(imageUrl) {

					@Override
					protected void onSuccess(File cachedFile) {
						if (cachedFile != null) {
							setImage(cachedFile);
						} else {
							Toast.makeText(m_context,
									R.string.imagehelper_sdcard_unavailable,
									Toast.LENGTH_LONG);
						}

					}

				}.execute();
			}
		}
	}

	protected void setImage(File cachedFile) {
		if (cachedFile != null) {
			try {
				Bitmap image = BitmapFactory.decodeFile(cachedFile
						.getAbsolutePath());

				switch (AbstractView.m_densityDpi) {
				case DisplayMetrics.DENSITY_LOW:
				case DisplayMetrics.DENSITY_MEDIUM:
				case DisplayMetrics.DENSITY_HIGH:
				case 320:
					image.setDensity(AbstractView.m_densityDpi);
					break;
				default:
					// don't set density for bitmap if we don't recognize the
					// density value
				}

				m_vwImg.setImageBitmap(image);
			} catch (Exception e) {
				Log.e(FdConfig.DEBUG_TAG, getClass().getSimpleName()
						+ ".setImage(File): " + e.getMessage());
			}
		}
	}
}
