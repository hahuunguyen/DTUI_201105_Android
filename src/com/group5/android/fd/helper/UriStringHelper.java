package com.group5.android.fd.helper;

import android.net.Uri;

import com.group5.android.fd.FdConfig;

public class UriStringHelper {
	public static String addParam(String uri, String key, String value) {
		if (uri.indexOf('?') == -1) {
			uri += '?' + key + '=' + Uri.encode(value);
		} else {
			uri += '&' + key + '=' + Uri.encode(value);
		}

		return uri;
	}

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

	public static String buildUriString(String action) {
		return UriStringHelper.buildUriString(FdConfig.SERVER_ENTRY_POINT_PATH,
				action);
	}
}
