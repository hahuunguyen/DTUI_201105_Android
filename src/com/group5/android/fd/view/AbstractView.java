package com.group5.android.fd.view;

import java.io.File;
import java.io.InputStream;

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

	// choose appropriate image based on size on screen
	// default Medium for Medium density
	protected String chooseImage(AbstractEntity entity) {
		String image = chooseImageNoDefault(entity);
		if (image == null) {
			image = entity.imageM;
		}

		return image;
	}

	// choose appropriate image based on size on screen, no default
	protected String chooseImageNoDefault(AbstractEntity entity) {
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
			return null;
		}
	}

	protected void setImage(final AbstractEntity entity) {
		String imageUrl = chooseImage(entity);

		if (imageUrl == null) {
			return;
		}

		// check to make sure we don't request an image twice
		if (m_lastRequestedImage == null
				|| m_lastRequestedImage.equals(imageUrl) == false) {

			m_lastRequestedImage = imageUrl;

			new ImageHelper(imageUrl) {

				@Override
				protected void onSuccess(File cachedFile) {
					setImage(entity, cachedFile);
				}

				@Override
				protected void onSuccess(InputStream inputStream) {
					setImage(entity, inputStream);
				}

			}.smartExecute();
		}
	}

	protected void setImage(AbstractEntity entity, File cachedFile) {
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(cachedFile
					.getAbsolutePath());

			setImage(entity, bitmap);
		} catch (Exception e) {
			Log.e(FdConfig.DEBUG_TAG, getClass().getSimpleName()
					+ ".setImage(File): " + e.getMessage());
		}
	}

	protected void setImage(AbstractEntity entity, InputStream inputStream) {
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

			setImage(entity, bitmap);
		} catch (Exception e) {
			Log.e(FdConfig.DEBUG_TAG, getClass().getSimpleName()
					+ ".setImage(InputStream): " + e.getMessage());
		}
	}

	protected void setImage(AbstractEntity entity, Bitmap bitmap) {
		String imageUrl = chooseImageNoDefault(entity);
		if (imageUrl != null) {
			// we recognized this screen density
			bitmap.setDensity(AbstractView.m_densityDpi);
		} else {
			// we don't recognized this screen density
			// this image is actually the MDPI one
			// we won't setDensity for the bitmap and let it auto-scale
		}

		m_vwImg.setImageBitmap(bitmap);
	}
}
