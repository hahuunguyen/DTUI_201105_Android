package com.group5.android.fd.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;
import com.group5.android.fd.helper.PreferencesHelper;

/**
 * The activity to display our app's preferences
 * 
 * @author Dao Hoang Son
 * 
 */
public class FdPreferenceActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		int count = getPreferenceScreen().getPreferenceCount();
		for (int i = 0; i < count; i++) {
			initPrefSummary(getPreferenceScreen().getPreference(i));
		}

		getListView().setCacheColorHint(0);
	}

	@Override
	protected void onResume() {
		super.onResume();

		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
		updatePrefSummary(findPreference(key));
	}

	/**
	 * Gets preference key from a resource id
	 * 
	 * @param id
	 *            the resource id, should be a string with the prefix "pref_"
	 * @return a <code>String</code> of the key
	 */
	protected String getPrefKey(int id) {
		return PreferencesHelper.getPreferenceKey(this, id);
	}

	/**
	 * Updates the summary text for a preference dynamically
	 * 
	 * @param pref
	 *            the target <code>Preference</code>
	 */
	protected void updatePrefSummary(Preference pref) {
		if (pref instanceof ListPreference) {
			ListPreference listPref = (ListPreference) pref;
			String entry = listPref.getEntry().toString();
			if (pref.getKey().equals(getPrefKey(R.string.pref_server_address))
					&& listPref.getValue().equals(
							PreferencesHelper
									.getServerAddressConfiguration(this))) {
				entry += " (" + FdConfig.SERVER_ADDRESS + ")";
			} else {
				entry += " (" + listPref.getValue() + ")";
			}
			pref.setSummary(entry);
		} else if (pref instanceof EditTextPreference
				&& !pref.getKey().equals(getPrefKey(R.string.pref_password))) {
			pref.setSummary(((EditTextPreference) pref).getText());
		}
	}

	/**
	 * Initiates preference summary. Most of the case, this method call
	 * {@link #updatePrefSummary(Preference)} but if it runs into a
	 * <code>PreferenceCategory</code>, it will go deep inside and call itself
	 * recursively with the category's preferences.
	 * 
	 * @param pref
	 *            the target <code>Preference</code>
	 */
	protected void initPrefSummary(Preference pref) {
		if (pref instanceof PreferenceCategory) {
			PreferenceCategory prefCategory = (PreferenceCategory) pref;

			int count = prefCategory.getPreferenceCount();
			for (int i = 0; i < count; i++) {
				initPrefSummary(prefCategory.getPreference(i));
			}
		} else {
			updatePrefSummary(pref);
		}
	}
}
