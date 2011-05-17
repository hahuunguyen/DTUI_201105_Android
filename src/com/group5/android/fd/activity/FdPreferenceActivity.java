package com.group5.android.fd.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

import com.group5.android.fd.R;
import com.group5.android.fd.helper.PreferencesHelper;

public class FdPreferenceActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	protected String getPrefKey(int id) {
		return PreferencesHelper.getPreferenceKey(this, id);
	}

	protected void updatePrefSummary(Preference pref) {
		if (pref.getKey().equals(getPrefKey(R.string.pref_username))) {
			pref.setSummary(((EditTextPreference) pref).getText());
		}
	}

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		int count = getPreferenceScreen().getPreferenceCount();
		for (int i = 0; i < count; i++) {
			initPrefSummary(getPreferenceScreen().getPreference(i));
		}
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
}
