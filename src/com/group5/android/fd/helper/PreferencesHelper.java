package com.group5.android.fd.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesHelper {
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

	public static boolean putString(Context context, int id, String value) {
		return PreferencesHelper.getPreferences(context).edit().putString(
				PreferencesHelper.getPreferenceKey(context, id), value)
				.commit();
	}

	public static boolean putBoolean(Context context, int id, boolean value) {
		return PreferencesHelper.getPreferences(context).edit().putBoolean(
				PreferencesHelper.getPreferenceKey(context, id), value)
				.commit();
	}

	public static String getPreferenceKey(Context context, int id) {
		return context.getResources().getString(id);
	}
}
