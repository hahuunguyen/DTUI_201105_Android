package com.group5.android.fd.view;

import android.content.Context;

import com.group5.android.fd.entity.CategoryEntity;

/**
 * A view for {@link CategoryEntity}
 * 
 * @author Nguyen Huu Ha
 * 
 */
public class CategoryView extends AbstractView {
	public CategoryEntity category = null;

	public CategoryView(Context context, CategoryEntity category) {
		super(context);
		setCategory(category);
	}

	/**
	 * Setup the view to display a new {$link CategoryEntity}
	 * 
	 * @param category
	 *            the new category
	 */
	public void setCategory(CategoryEntity category) {
		this.category = category;

		setTextViews(category.categoryName, "");
		setImage(category);
	}
}
