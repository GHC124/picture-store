package com.picturestore.common.util;

import java.util.List;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

public class PictureStoreDownloadManager {
	private static PictureStoreDownloadManager INSTANCE;

	public static boolean isDownloadManagerAvailable(Context context) {
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setClassName("com.android.providers.downloads.ui",
					"com.android.providers.downloads.ui.DownloadList");
			List<ResolveInfo> list = context.getPackageManager()
					.queryIntentActivities(intent,
							PackageManager.MATCH_DEFAULT_ONLY);
			return list.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}
	
	private PictureStoreDownloadManager(){
		
	}

	public static PictureStoreDownloadManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PictureStoreDownloadManager();
		}
		return INSTANCE;
	}

	public void download(Context context, String url, String fileName,
			String title, String description) {
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(url));
		request.setDescription(description);
		request.setTitle(title);
		// in order for this if to run, you must use the android 3.2 to compile
		// your app
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			request.allowScanningByMediaScanner();
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, fileName);

		// get download service and enqueue file
		DownloadManager manager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
	}
}
