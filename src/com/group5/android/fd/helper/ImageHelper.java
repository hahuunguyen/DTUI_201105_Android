package com.group5.android.fd.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.group5.android.fd.FdConfig;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class ImageHelper extends AsyncTask<Void, Void, File> {
	protected String strURL;
	protected ImageView imgView;
	protected int m_type;
	public static final int CATEGORY_TYPE = 1;
	protected final String CATEGORY_PREFIX = "http://localhost/dtui/data/dtui/category/";
	public static final int ITEM_TYPE = 2;
	protected final String ITEM_PREFIX = "http://localhost/dtui/data/dtui/item/";

	public ImageHelper(String url, ImageView imageView, int type) {
		this.strURL = url;
		this.imgView = imageView;
		m_type = type;
	}

	@Override
	protected File doInBackground(Void... arg0) {
		File file = new File(Environment.getExternalStorageDirectory(),
				this.getFileNameFromURL(strURL, m_type));
		if (file.exists()) {

		} else {

			try {
				URL url = new URL(strURL);
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoInput(true);
				urlConnection.connect();

				FileOutputStream out = new FileOutputStream(file);
				InputStream in = urlConnection.getInputStream();
				// create buffer
				byte[] buffer = new byte[1024];
				int bufferTemp = 0;
				// writting
				while ((bufferTemp = in.read(buffer)) > 0) {
					out.write(buffer, 0, bufferTemp);
				}
				out.close();
				

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		Log.i(FdConfig.DEBUG_TAG, "file path:"+ file.getAbsolutePath());
		return file;
	}

	@Override
	protected void onPostExecute(File result) {

		if (result.exists()) {
			imgView.setImageURI(Uri.fromFile(result));
		}

	}

	protected String getFileNameFromURL(String url, int type) {
		if (type == CATEGORY_TYPE) {
			url.replace(CATEGORY_PREFIX, "");
		} else if (type == ITEM_TYPE) {
			url.replace(ITEM_PREFIX, "");
		}
		return null;
	}
}
