package com.picturestore.social.facebook;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import com.picturestore.social.facebook.AsyncFacebookRunner.RequestListener;

/**
 * Skeleton base class for RequestListeners, providing default error handling.
 * Applications should handle these error conditions.
 * 
 */
public abstract class BaseRequestListener implements RequestListener {

	@Override
	public void onFacebookError(FacebookError e) {
		// if(DirectvApplication.showLogs() == true){
		// Log.e("Facebook", e.getMessage());
		// }
		e.printStackTrace();
	}

	@Override
	public void onFileNotFoundException(FileNotFoundException e) {
		// if(DirectvApplication.showLogs() == true){
		// Log.e("Facebook", e.getMessage());
		// }
		e.printStackTrace();
	}

	@Override
	public void onIOException(IOException e) {
		// if(DirectvApplication.showLogs() == true){
		// Log.e("Facebook", e.getMessage());
		// }
		e.printStackTrace();
	}

	@Override
	public void onMalformedURLException(MalformedURLException e) {
		// if(DirectvApplication.showLogs() == true){
		// Log.e("Facebook", e.getMessage());
		// }
		e.printStackTrace();
	}

}
