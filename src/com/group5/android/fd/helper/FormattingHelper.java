package com.group5.android.fd.helper;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Helper class to format stuff
 * 
 * @author Dao Hoang Son
 * 
 */
public class FormattingHelper {

	protected static NumberFormat m_numberFormat = null;

	/**
	 * Format the price for display
	 * 
	 * @param price
	 *            the price
	 * @return a <code>String</code> of the price
	 */
	public static String formatPrice(double price) {
		if (FormattingHelper.m_numberFormat == null) {
			FormattingHelper.m_numberFormat = NumberFormat.getInstance();
			if (FormattingHelper.m_numberFormat instanceof DecimalFormat) {
				DecimalFormat decimalFormat = (DecimalFormat) FormattingHelper.m_numberFormat;
				decimalFormat.setGroupingSize(3);
			}
		}

		return FormattingHelper.m_numberFormat.format(price);
	}
}
