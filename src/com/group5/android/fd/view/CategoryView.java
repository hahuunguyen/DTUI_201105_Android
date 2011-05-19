package com.group5.android.fd.view;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.entity.CategoryEntity;
import com.group5.android.fd.helper.ImageHelper;

public class CategoryView extends AbstractView {
	public CategoryEntity category = null;

	public CategoryView(Context context, CategoryEntity category) {
		super(context);
		setCategory(category);
	}

	public void setCategory(CategoryEntity category) {
		this.category = category;
		setTextView(category.categoryName);
		setImg(category.categoryImageS, m_vwImg, ImageHelper.CATEGORY_TYPE);
		Log.v(FdConfig.DEBUG_TAG, "setImg" + category.categoryName);
	}

	@Override
	protected void setImg(String url, ImageView imgView, int type) {
		new ImageHelper(url, imgView, type);

	}
}
