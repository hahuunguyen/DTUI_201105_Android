package com.group5.android.fd.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.group5.android.fd.FdConfig;

public class ImageHelper extends AsyncTask<Void, Void, File> {
	protected String strURL;
	protected ImageView imgView;
	public static final int CATEGORY_TYPE = 1;
	protected final String CATEGORY_PREFIX = FdConfig.SERVER_ROOT
			+ "data/dtui/category/";
	public static final int ITEM_TYPE = 2;
	protected final String ITEM_PREFIX = FdConfig.SERVER_ROOT
			+ "data/dtui/item/";
	protected final String packageDirectory = Environment
			.getExternalStorageDirectory().toString()
			+ "/Android/data/com.group5.android.fd/cache/";

	public ImageHelper(String url, ImageView imageView) {
		this.strURL = url;
		this.imgView = imageView;

	}

	@Override
	protected File doInBackground(Void... arg0) {

		File file = new File(packageDirectory, this.getFileNameFromURL(strURL));

		if (!file.exists()) {
			File directory = new File(packageDirectory);
			if (!directory.isDirectory()) {
				directory.mkdirs();
			}
			try {
				Log.i(FdConfig.DEBUG_TAG, strURL);
				FileOutputStream out = new FileOutputStream(file);
				InputStream in = HttpHelper.getRaw(strURL);
				// create buffer
				byte[] buffer = new byte[1024];
				int bufferTemp = 0;
				// writting
				while ((bufferTemp = in.read(buffer)) > 0) {
					out.write(buffer, 0, bufferTemp);
				}

				out.close();
				Log.i(FdConfig.DEBUG_TAG, "file size: " + file.length());

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		Log.i(FdConfig.DEBUG_TAG, "file path:" + file.getAbsolutePath());
		return file;
	}

	@Override
	protected void onPostExecute(File result) {

		if (result.exists()) {
			imgView.setImageURI(Uri.fromFile(result));
		}

	}

	protected String getFileNameFromURL(String url) {
		String[] parts = url.split("/");
		return parts[parts.length - 1];

	}
}
