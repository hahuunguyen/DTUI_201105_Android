package com.group5.android.fd.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.group5.android.fd.FdConfig;

abstract public class ImageHelper extends AsyncTask<Void, Void, Object> {

	protected static HashMap<String, File> m_cachedFiles = new HashMap<String, File>();

	// where to store in SD card
	public static File packageDirectory = new File(Environment
			.getExternalStorageDirectory().toString()
			+ "/Android/data/com.group5.android.fd/cache/");

	public static boolean isExternalStorageAvailable = Environment.MEDIA_MOUNTED
			.equals(Environment.getExternalStorageState());

	protected String imageUrl;

	// if SD card is available, get images file from server and save in cache in
	// SD card
	// if not, get images directly from server and display

	public ImageHelper(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	// do some optimization in checking images have been in cache
	// for better display performance
	//
	public void smartExecute() {
		File file = null;

		if (imageUrl != null) {
			if (ImageHelper.m_cachedFiles.containsKey(imageUrl)) {
				// optimization 1: check for cached file in our HashMap
				file = ImageHelper.m_cachedFiles.get(imageUrl);

				Log.d(FdConfig.DEBUG_TAG, "ImageHelper / Hit HashMap cache: "
						+ imageUrl + " -> " + file.getAbsolutePath());
			} else if (ImageHelper.isExternalStorageAvailable) {
				// optimization 2: check for cached file in sd card
				File cachedFile = ImageHelper.getTargetFile(imageUrl);

				if (cachedFile.exists()) {
					file = cachedFile;

					Log.d(FdConfig.DEBUG_TAG, "ImageHelper / Hit cache: "
							+ imageUrl + " -> " + file.getAbsolutePath());

					// optimization 2.1: add the found file to HashMap to use
					// later (optimization 1)
					ImageHelper.m_cachedFiles.put(imageUrl, cachedFile);
				}
			}
		}

		if (file != null) {
			// found an image, bypass the whole execute stack
			onPostExecute(file);
		} else {
			execute();
		}
	}

	// normal execution
	// receive file from server and save in cache, if no SD card ,
	// return InputStream
	@Override
	protected Object doInBackground(Void... arg0) {
		Object obj = null;

		if (ImageHelper.isExternalStorageAvailable) {
			File file = ImageHelper.getTargetFile(imageUrl);
			if (file != null) {
				if (file.exists()) {
					obj = file;

					Log.d(FdConfig.DEBUG_TAG,
							"ImageHelper / Hit cache: " + imageUrl + " -> "
									+ ((File) obj).getAbsolutePath());
					Log.e(FdConfig.DEBUG_TAG,
							"THIS SHOULD NOT HAPPEN! YOU SHOULD CALL ImageHelper.smartExecute() INSTEAD OF ImageHelper.execute()");

				} else if (cacheImage(file)) {
					obj = file;

					Log.d(FdConfig.DEBUG_TAG,
							"ImageHelper / Image has been cached: " + imageUrl
									+ " -> " + ((File) obj).getAbsolutePath());
				} else {
					Log.e(FdConfig.DEBUG_TAG,
							"ImageHelper / Unable to cache image: " + imageUrl
									+ " -> " + file.getAbsolutePath());
				}
			}

			if (obj != null) {
				ImageHelper.m_cachedFiles.put(imageUrl, (File) obj);
			} else {
				ImageHelper.m_cachedFiles.put(imageUrl, null);
			}
		} else {
			obj = HttpHelper.getRaw(imageUrl);

			Log.d(FdConfig.DEBUG_TAG,
					"External storage is not avalable. Image is not cached: "
							+ imageUrl);

			// caching is disabled when we get data from URI
			// it's probably too memory extensive to cache input stream
			// (is that even possible?)
			// ImageHelper.m_cachedFiles.put(imageUrl, obj);
		}

		return obj;

	}

	@Override
	protected void onPostExecute(Object obj) {
		if (obj != null) {
			if (obj instanceof File) {
				onSuccess((File) obj);
			} else if (obj instanceof InputStream) {
				onSuccess((InputStream) obj);

				try {
					((InputStream) obj).close();
				} catch (IOException e) {
					Log.e(FdConfig.DEBUG_TAG, "ImageHelper.onPostExecute(): "
							+ e.getMessage());
				}
			}
		}
	}

	protected boolean cacheImage(File target) {
		boolean success = false;
		ImageHelper.packageDirectory.mkdirs();

		try {
			// receive image from server
			InputStream in = HttpHelper.getRaw(imageUrl);

			try {
				FileOutputStream out = new FileOutputStream(target);

				try {
					// create buffer
					// please update FdConfig.java if you get
					// compile
					// error
					byte[] buffer = new byte[FdConfig.BUFFER_SIZE];
					int bufferTemp = 0;

					// writting
					while ((bufferTemp = in.read(buffer)) > 0) {
						out.write(buffer, 0, bufferTemp);
					}

					success = true;
				} catch (IOException e) {
					Log.e(FdConfig.DEBUG_TAG, "ImageHelper / writing file "
							+ target.getAbsolutePath() + ": " + e.getMessage());
				}

				out.close();
			} catch (IOException e) {
				Log.e(FdConfig.DEBUG_TAG, "ImageHelper / creating file "
						+ target.getAbsolutePath() + ": " + e.getMessage());
			}

			in.close();
		} catch (Exception e) {
			Log.e(FdConfig.DEBUG_TAG, "ImageHelper / processing InputStream: "
					+ e.getMessage());
		}

		return success;
	}

	abstract protected void onSuccess(File file);

	abstract protected void onSuccess(InputStream inputStream);

	protected static File getTargetFile(String url) {
		if (url == null) {
			return null;
		}

		String[] parts = url.split("/");

		return new File(ImageHelper.packageDirectory, parts[parts.length - 1]);
	}

	public static void removeCachedFiles() {
		String[] files = ImageHelper.packageDirectory.list();
		File file;
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				file = new File(ImageHelper.packageDirectory, files[i]);
				file.delete();

				Log.d(FdConfig.DEBUG_TAG, files[i]);
			}
		}

		ImageHelper.m_cachedFiles.clear();
	}
}
