package com.group5.android.fd;

import android.net.Uri;

public class URIHelper {
	public static String addParam(String uri, String key, String value) {
		if (uri.indexOf('?') == -1) {
			uri += '?' + key + '=' + Uri.encode(value);
		} else {
			uri += '&' + key + '=' + Uri.encode(value);
		}

		return uri;
	}
}
