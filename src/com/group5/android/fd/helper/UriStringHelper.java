package com.group5.android.fd.helper;

import android.net.Uri;

import com.group5.android.fd.FdConfig;

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
	 * @param majorSection
	 * @param action
	 * @return the uri
	 */
	public static String buildUriString(String majorSection, String action) {
		StringBuilder sb = new StringBuilder(FdConfig.SERVER_ROOT);
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
	 * @param action
	 * @return the uri
	 */
	public static String buildUriString(String action) {
		return UriStringHelper.buildUriString(FdConfig.SERVER_ENTRY_POINT_PATH,
				action);
	}
}
