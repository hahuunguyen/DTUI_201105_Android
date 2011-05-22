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

/**
 * A generic view which will be use by all other views of the app (for
 * <code>ListView</code> as an item)
 * 
 * @author Dao Hoang Son
 * 
 */
abstract public class AbstractView extends LinearLayout {

	protected Context m_context;

	protected ImageView m_vwImg;
	protected TextView m_vwName;
	protected TextView m_vwInfo;

	// densityDpi to get window size, use for choose suitable image
	protected static int m_densityDpi = 0;

	protected String m_lastRequestedImage = null;

	/**
	 * Constructs itself. Get references of subviews. Calculate the
	 * {@link #m_densityDpi} if it hasn't calculated yet.
	 * 
	 * @param context
	 */
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

	/**
	 * Get the layout resource id. Default is
	 * <code>R.layout.view_abstract</code>. Subclass can implement this method
	 * and change the layout to whatever it want but the new layout should have
	 * 3 important items:
	 * <ol>
	 * <li>An <code>ImageView</code>: imgItem</li>
	 * <li>An <code>TextView</code>: txtItemName</li>
	 * <li>Another <code>TextView</code>: txtItemInfo</li>
	 * </ol>
	 * 
	 * @return the resource id
	 */
	protected int getLayoutResourceId() {
		return R.layout.view_abstract;
	}

	/**
	 * Sets the name and info view to associated contents
	 * 
	 * @param name
	 * @param info
	 */
	protected void setTextViews(String name, String info) {
		m_vwName.setText(name);
		m_vwInfo.setText(info);
	}

	/**
	 * Chooses an image to display from an {@link AbstractEntity} by calling
	 * {@link #chooseImageNoDefault(AbstractEntity)}. If the device dpi can not
	 * be recognized, fallback to MDPI version of the image.
	 * 
	 * @param entity
	 *            the entity
	 * @return the image url
	 */
	protected String chooseImage(AbstractEntity entity) {
		String image = chooseImageNoDefault(entity);
		if (image == null) {
			image = entity.imageM;
		}

		return image;
	}

	/**
	 * Chooses the image url base on device dpi
	 * 
	 * @param entity
	 *            the entity to get image from
	 * @return the image url or null if dpi is not recognized
	 */
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

	/**
	 * Uses image from an entity. This method utilizes {@link ImageHelper} to
	 * get the image smartly and effectively
	 * 
	 * @param entity
	 *            the entity
	 */
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

	/**
	 * Sets the image from a cached <code>File</code>
	 * 
	 * @param entity
	 * @param cachedFile
	 */
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

	/**
	 * Sets the image from an <code>InputStream</code>
	 * 
	 * @param entity
	 * @param inputStream
	 */
	protected void setImage(AbstractEntity entity, InputStream inputStream) {
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

			setImage(entity, bitmap);
		} catch (Exception e) {
			Log.e(FdConfig.DEBUG_TAG, getClass().getSimpleName()
					+ ".setImage(InputStream): " + e.getMessage());
		}
	}

	/**
	 * Sets the image from a <code>Bitmap</code>
	 * 
	 * @param entity
	 * @param bitmap
	 */
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
