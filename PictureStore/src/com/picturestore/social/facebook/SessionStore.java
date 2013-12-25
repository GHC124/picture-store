/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.picturestore.social.facebook;

import android.content.Context;

import com.picturestore.BaseApplication;
import com.picturestore.prefs.UserPreferences;

public class SessionStore {
	public static boolean save(Facebook session, Context context) {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();
		userPreferences.setFacebookUserToken(session.getAccessToken());
		userPreferences.setFacebookExpires(session.getAccessExpires());
		return true;
	}

	public static boolean restore(Facebook session, Context context) {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();
		session.setAccessToken(userPreferences.getFacebookUserToken());
		session.setAccessExpires(userPreferences.getFacebookExpires());
		return session.isSessionValid();
	}

	public static void clear(Context context) {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();
		userPreferences.setFacebookUserToken(null);
		userPreferences.setFacebookExpires(0);
	}

	public static String getAccessToken(Context context) {
		final UserPreferences userPreferences = BaseApplication.getInstance()
				.getUserPreferences();
		return userPreferences.getFacebookUserToken();
	}
}
