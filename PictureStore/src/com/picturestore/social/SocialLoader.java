package com.picturestore.social;

import java.util.concurrent.Callable;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthException;

import org.json.JSONObject;

import picturestore.common.social.SocialException;
import picturestore.common.social.SocialManager;
import android.content.Context;
import android.content.Loader;

import com.picturestore.loader.BaseLoader;
import com.picturestore.loader.LoaderResult;

public class SocialLoader extends BaseLoader<LoaderResult> {

	private final SocialMethodCaller<?> mTarget;

	private SocialLoader(Context context, SocialMethodCaller<?> target) {
		super(context);
		mTarget = target;
	}

	public static Loader<LoaderResult> getFacebookUsername(
			Context context, final boolean sessionValid, final String accessToken) {
		return new SocialLoader(context, new SocialMethodCaller<JSONObject>() {
			@Override
			protected JSONObject execute() throws SocialException {
				return SocialManager.getFacebookInfo(accessToken);
			}
		});
	}
	
	public static SocialLoader getTwitterUserInfo(Context context,
			final OAuthConsumer consumer) {
		return new SocialLoader(context, new SocialMethodCaller<JSONObject>() {
			@Override
			protected JSONObject execute() throws SocialException {
				return SocialManager.getTwitterInfo(consumer);
			}
		});
	}

	public static SocialLoader replyOnTwitter(Context context,
			final OAuthConsumer consumer, final String tweetId,
			final String userName, final String message) {
		return new SocialLoader(context, new SocialMethodCaller<JSONObject>() {
			@Override
			protected JSONObject execute() throws SocialException {
				return SocialManager.replyOnTwitter(consumer, tweetId,
						userName, message);
			}
		});
	}

	public static SocialLoader retweetOnTwitter(Context context,
			final OAuthConsumer consumer, final String tweetId) {
		return new SocialLoader(context, new SocialMethodCaller<JSONObject>() {
			@Override
			protected JSONObject execute() throws SocialException {
				return SocialManager.retweetOnTwitter(consumer, tweetId);
			}
		});
	}

	public static SocialLoader favoriteOnTwitter(Context context,
			final OAuthConsumer consumer, final String tweetId) {
		return new SocialLoader(context, new SocialMethodCaller<JSONObject>() {
			@Override
			protected JSONObject execute() throws SocialException {
				return SocialManager.favoriteOnTwitter(consumer, tweetId);
			}
		});
	}

	public static SocialLoader retrieveTwitterTime(Context context) {
		return new SocialLoader(context, new SocialMethodCaller<Long>() {
			@Override
			protected Long execute() throws SocialException {
				return SocialManager.retrieveTwitterTime();
			}
		});
	}

	public static SocialLoader retrieveTwitterAccessToken(Context context,
			final OAuthConsumer consumer, final OAuthProvider provider,
			final String url) {
		return new SocialLoader(context, new SocialMethodCaller<String>() {
			@Override
			protected String execute() throws SocialException {
				try {
					provider.retrieveAccessToken(consumer, url);
					return url; // have to return something
				} catch (OAuthException e) {
					throw new SocialException(
							"Failure retrieving access token", e);
				}
			}
		});
	}

	public static SocialLoader retrieveTwitterRequestToken(Context context,
			final OAuthConsumer consumer, final OAuthProvider provider,
			final String url) {
		return new SocialLoader(context, new SocialMethodCaller<String>() {
			@Override
			protected String execute() throws SocialException {
				String authUrl = null;
				try {
					authUrl = provider.retrieveRequestToken(consumer, url);
				} catch (OAuthException e) {
					throw new SocialException(
							"Failure retrieving request token", e);
				}
				return authUrl;
			}
		});
	}

	@Override
	public LoaderResult loadInBackground() {
		return mTarget.call();
	}

	private static abstract class SocialMethodCaller<T> implements
			Callable<LoaderResult> {
		@Override
		public LoaderResult call() {
			LoaderResult result;
			try {
				result = new LoaderResult(execute());
			} catch (SocialException e) {
				result = new LoaderResult(e);
			}
			return result;
		}

		protected abstract T execute() throws SocialException;
	}
}
