package com.group5.android.fd.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.group5.android.fd.R;

/**
 * Helper class to access preferences
 * 
 * @author Dao Hoang Son
 * 
 */
public class PreferencesHelper {
	public static void setDefaultValues(Context context) {
		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
	}

	public static SharedPreferences getPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static String getString(Context context, int id) {
		return PreferencesHelper.getPreferences(context).getString(
				PreferencesHelper.getPreferenceKey(context, id), null);
	}

	public static boolean getBoolean(Context context, int id) {
		return PreferencesHelper.getPreferences(context).getBoolean(
				PreferencesHelper.getPreferenceKey(context, id), false);
	}

	public static int getInt(Context context, int id) {
		int value = 0;
		try {
			value = PreferencesHelper.getPreferences(context).getInt(
					PreferencesHelper.getPreferenceKey(context, id), 0);
		} catch (ClassCastException e) {
			// EditTextPreference saved our preference as String
			// we will cast it ourself
			try {
				value = Integer.valueOf(PreferencesHelper
						.getString(context, id));
			} catch (NumberFormatException e2) {
				// seriously, not a number?
			}
		}

		return value;
	}

	public static boolean putString(Context context, int id, String value) {
		return PreferencesHelper
				.getPreferences(context)
				.edit()
				.putString(PreferencesHelper.getPreferenceKey(context, id),
						value).commit();
	}

	public static boolean putBoolean(Context context, int id, boolean value) {
		return PreferencesHelper
				.getPreferences(context)
				.edit()
				.putBoolean(PreferencesHelper.getPreferenceKey(context, id),
						value).commit();
	}

	public static boolean putInt(Context context, int id, int value) {
		return PreferencesHelper.getPreferences(context).edit()
				.putInt(PreferencesHelper.getPreferenceKey(context, id), value)
				.commit();
	}

	public static String getPreferenceKey(Context context, int id) {
		return context.getString(id);
	}

	public static String getServerAddressConfiguration(Context context) {
		return context.getResources().getStringArray(
				R.array.entriesvalues_server_address)[0];
	}
}
