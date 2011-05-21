package com.group5.android.fd.view;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group5.android.fd.R;
import com.group5.android.fd.entity.AbstractEntity;
import com.group5.android.fd.helper.ImageHelper;

abstract public class AbstractView extends RelativeLayout {
	protected TextView m_vwName;
	protected Context m_context;
	protected ImageView m_vwImg, m_vwImg2;
	// densityDpi to get window size, use for choose suitable image
	protected int m_densityDpi;

	protected String m_lastRequestedImage = null;

	public AbstractView(Context context) {
		super(context);
		m_context = context;
		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.view_item, this, true);
		m_vwName = (TextView) findViewById(R.id.txtItemName);
		m_vwImg = (ImageView) findViewById(R.id.imgItem);
		m_vwImg2 = (ImageView) findViewById(R.id.imgItem2);

		DisplayMetrics metrics = new DisplayMetrics();
		((Activity) m_context).getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		m_densityDpi = metrics.densityDpi;
	}

	protected void setTextView(String text) {
		m_vwName.setText(text);
	}

	protected void setTextView(int index) {
		m_vwName.setText(index);
	}

	protected String chooseImageSize(AbstractEntity entity) {
		switch (m_densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			return entity.imageS;
		case DisplayMetrics.DENSITY_MEDIUM:
			return entity.imageM;
		case DisplayMetrics.DENSITY_HIGH:
			return entity.imageL;
		default:
			return entity.imageM;
		}
	}

	protected void setImage(String imageUrl, final ImageView imageView) {
		if (imageUrl == null) {
			return;
		}

		if (m_lastRequestedImage == null
				|| m_lastRequestedImage.equals(imageUrl) == false) {

			m_lastRequestedImage = imageUrl;

			File cachedFile = ImageHelper.getCachedFile(imageUrl);

			if (cachedFile != null) {
				imageView.setImageURI(Uri.fromFile(cachedFile));
			} else {
				new ImageHelper(imageUrl) {

					@Override
					protected void onSuccess(File cachedFile) {
						imageView.setImageURI(Uri.fromFile(cachedFile));
					}

				}.execute();
			}
		}
	}
}
