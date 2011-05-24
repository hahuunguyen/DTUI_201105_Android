package com.group5.android.fd.helper;

import android.content.Context;
import android.net.Uri;

import com.group5.android.fd.FdConfig;
import com.group5.android.fd.R;

/**
 * Helper class for URI string
 * 
 * @author Dao Hoang Son
 * 
 */
public class UriStringHelper {
	/**
	 * Adds a parameter to an existing uri for GET request
	 * 
	 * @param uri
	 *            the existing uri
	 * @param key
	 *            the key for new param
	 * @param value
	 *            the value for new param
	 * 
	 * @return the uri with new param attached
	 */
	public static String addParam(String uri, String key, String value) {
		if (uri.indexOf('?') == -1) {
			uri += '?' + key + '=' + Uri.encode(value);
		} else {
			uri += '&' + key + '=' + Uri.encode(value);
		}

		return uri;
	}

	/**
	 * Adds a parameter to an existing uri for GET request
	 * 
	 * @param uri
	 *            the existing uri
	 * @param key
	 *            the key for new param
	 * @param value
	 *            the value for new param
	 * @return the uri with new param attached
	 */
	public static String addParam(String uri, String key, int value) {
		return UriStringHelper.addParam(uri, key, "" + value);
	}

	/**
	 * Builds uri to our server which consists of 2 parts: a major section and
	 * an action
	 * 
	 * @param context
	 * @param majorSection
	 * @param action
	 * @return the uri
	 */
	public static String buildUriString(Context context, String majorSection,
			String action) {
		String prefServerAddress = PreferencesHelper.getString(context,
				R.string.pref_server_address);
		String serverAddress;
		if (prefServerAddress == null
				|| prefServerAddress.equals(PreferencesHelper
						.getServerAddressConfiguration(context))) {
			serverAddress = FdConfig.SERVER_ADDRESS;
		} else {
			serverAddress = prefServerAddress;
		}

		StringBuilder sb = new StringBuilder(serverAddress);
		sb.append(majorSection);
		sb.append('/');
		sb.append(action);

		if (action.indexOf('.') == -1) {
			// no extension have been specified
			// apply our default (required) extension
			sb.append('.');
			sb.append(FdConfig.SERVER_EXTENSION);
		}

		return sb.toString();
	}

	/**
	 * Builds uri to our server entry point's action
	 * 
	 * @param context
	 * @param action
	 * @return the uri
	 */
	public static String buildUriString(Context context, String action) {
		return UriStringHelper.buildUriString(context,
				FdConfig.SERVER_ENTRY_POINT_PATH, action);
	}
}
