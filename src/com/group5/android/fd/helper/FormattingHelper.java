package com.group5.android.fd.helper;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FormattingHelper {
	public static String formatPrice(double price) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
		decimalFormat.setGroupingSize(3);

		return numberFormat.format(price);
	}
}
