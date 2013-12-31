package com.picturestore.common.social;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SocialManager {

	private static final String FACEBOOK_USER_INFO_URL = "https://graph.facebook.com/me?access_token=:accessToken";
	private static final String FACEBOOK_USER_INFO_ACCESSTOKEN_VAR = ":accessToken";

	public static JSONObject getFacebookInfo(String accessToken)
			throws SocialException {
		JSONObject jso;
		try {
			String url = FACEBOOK_USER_INFO_URL.replace(
					FACEBOOK_USER_INFO_ACCESSTOKEN_VAR, accessToken);
			HttpGet get = new HttpGet(url);

			String response = getResponse(get);
			jso = new JSONObject(response);
		} catch (JSONException e) {
			throw new SocialException("Failure parsing response", e);
		}
		return jso;
	}

	private static final String FACEBOOK_POST_URL = "https://graph.facebook.com/me/feed?message=:message&format=json&access_token=:accessToken";
	private static final String FACEBOOK_POST_ACCESSTOKEN_VAR = ":accessToken";
	private static final String FACEBOOK_POST_MESSAGE_VAR = ":message";

	public static JSONObject postOnFacebook(String accessToken, String message)
			throws SocialException {
		JSONObject jso;
		try {
			message = URLEncoder.encode(message, "UTF-8");
			String url = FACEBOOK_POST_URL.replace(
					FACEBOOK_POST_ACCESSTOKEN_VAR, accessToken);
			url = url.replace(FACEBOOK_POST_MESSAGE_VAR, message);
			HttpPost post = new HttpPost(url);

			String response = getResponse(post);
			jso = new JSONObject(response);
		} catch (JSONException e) {
			throw new SocialException("Failure parsing response", e);
		} catch (UnsupportedEncodingException e) {
			throw new SocialException("Failure encode data", e);
		}
		return jso;
	}

	private static final String TWITTER_VERIFY_CREDS = "https://api.twitter.com/1.1/account/verify_credentials.json";

	public static JSONObject getTwitterInfo(OAuthConsumer consumer)
			throws SocialException {
		JSONObject jso;
		try {
			HttpGet get = new HttpGet(TWITTER_VERIFY_CREDS);
			consumer.sign(get);

			String response = getResponse(get);
			jso = new JSONObject(response);
		} catch (JSONException e) {
			throw new SocialException("Failure parsing response", e);
		} catch (OAuthException e) {
			throw new SocialException("Failure processing auth", e);
		}
		return jso;
	}

	private static final String TWITTER_POST_URL = "https://api.twitter.com/1.1/statuses/update.json";

	public static JSONObject postOnTwitter(OAuthConsumer consumer,
			String message) throws SocialException {
		JSONObject jso;
		try {
			HttpPost post = new HttpPost(TWITTER_POST_URL);
			LinkedList<BasicNameValuePair> out = new LinkedList<BasicNameValuePair>();

			out.add(new BasicNameValuePair("status", message));
			post.setEntity(new UrlEncodedFormEntity(out, HTTP.UTF_8));

			consumer.sign(post);

			String response = getResponse(post);
			jso = new JSONObject(response);
		} catch (IOException e) {
			throw new SocialException("Failure processing request", e);
		} catch (JSONException e) {
			throw new SocialException("Failure parsing response", e);
		} catch (OAuthException e) {
			throw new SocialException("Failure processing auth", e);
		}
		return jso;
	}

	public static JSONObject replyOnTwitter(OAuthConsumer consumer,
			String tweetId, String userName, String message)
			throws SocialException {
		JSONObject jso;
		try {
			HttpPost post = new HttpPost(TWITTER_POST_URL);
			LinkedList<BasicNameValuePair> out = new LinkedList<BasicNameValuePair>();

			message = "@" + userName + " " + message;
			out.add(new BasicNameValuePair("status", message));
			out.add(new BasicNameValuePair("in_reply_to_status_id", tweetId));
			post.setEntity(new UrlEncodedFormEntity(out, HTTP.UTF_8));

			consumer.sign(post);

			String response = getResponse(post);
			jso = new JSONObject(response);
		} catch (IOException e) {
			throw new SocialException("Failure processing request", e);
		} catch (JSONException e) {
			throw new SocialException("Failure parsing response", e);
		} catch (OAuthException e) {
			throw new SocialException("Failure processing auth", e);
		}
		return jso;
	}

	private static final String TWITTER_RETWEET_URL = "https://api.twitter.com/1.1/statuses/retweet/:id.json";

	public static JSONObject retweetOnTwitter(OAuthConsumer consumer,
			String tweetId) throws SocialException {
		JSONObject jso;
		try {
			String newURL = TWITTER_RETWEET_URL.replace(":id", tweetId);
			HttpPost post = new HttpPost(newURL);
			LinkedList<BasicNameValuePair> out = new LinkedList<BasicNameValuePair>();
			post.setEntity(new UrlEncodedFormEntity(out, HTTP.UTF_8));

			consumer.sign(post);

			String response = getResponse(post);
			jso = new JSONObject(response);
		} catch (IOException e) {
			throw new SocialException("Failure processing request", e);
		} catch (JSONException e) {
			throw new SocialException("Failure parsing response", e);
		} catch (OAuthException e) {
			throw new SocialException("Failure processing auth", e);
		}
		return jso;
	}

	private static final String TWITTER_FAVORITE_URL = "https://api.twitter.com/1.1/favorites/create.json";

	public static JSONObject favoriteOnTwitter(OAuthConsumer consumer,
			String tweetId) throws SocialException {
		JSONObject jso;
		try {
			HttpPost post = new HttpPost(TWITTER_FAVORITE_URL);
			LinkedList<BasicNameValuePair> out = new LinkedList<BasicNameValuePair>();

			out.add(new BasicNameValuePair("id", tweetId));
			post.setEntity(new UrlEncodedFormEntity(out, HTTP.UTF_8));

			consumer.sign(post);

			String response = getResponse(post);
			jso = new JSONObject(response);
		} catch (IOException e) {
			throw new SocialException("Failure processing request", e);
		} catch (JSONException e) {
			throw new SocialException("Failure parsing response", e);
		} catch (OAuthException e) {
			throw new SocialException("Failure processing auth", e);
		}
		return jso;
	}

	private static final String TWITTER_PRIVACY_URL = "https://api.twitter.com/1.1/help/privacy.json";

	public static Long retrieveTwitterTime() throws SocialException {
		Long time = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			// Use GET help/privacy, time will be included in header [date]
			HttpGet httpget = new HttpGet(TWITTER_PRIVACY_URL);
			HttpResponse response = httpclient.execute(httpget);
			Header[] headers = response.getHeaders("date");
			if (headers != null && headers.length > 0) {
				String dateStr = headers[0].getValue();
				try {
					DateFormat utcFormat = new SimpleDateFormat(
							"EEE, dd MMM yyyy HH:mm:ss", Locale.US);
					utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					Date date = utcFormat.parse(dateStr);
					time = date.getTime() - System.currentTimeMillis();
					if (time > 0) {
						time = (System.currentTimeMillis() + time) / 1000;
					} else {
						time = (System.currentTimeMillis() - time) / 1000;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			throw new SocialException("Failure processing request", e);
		}
		return time;
	}

	private static String getResponse(HttpUriRequest request)
			throws SocialException {
		try {
			HttpParams parameters = new BasicHttpParams();
			HttpProtocolParams.setVersion(parameters, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(parameters,
					HTTP.DEFAULT_CONTENT_CHARSET);
			HttpProtocolParams.setUseExpectContinue(parameters, false);
			HttpConnectionParams.setTcpNoDelay(parameters, true);
			HttpConnectionParams.setSocketBufferSize(parameters, 8192);

			SchemeRegistry schemRegistry = new SchemeRegistry();
			schemRegistry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schemRegistry.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));
			ClientConnectionManager tsccm = new ThreadSafeClientConnManager(
					parameters, schemRegistry);
			HttpClient httpClient = new DefaultHttpClient(tsccm, parameters);

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			String str = EntityUtils.toString(entity);

			return str;
		} catch (IOException e) {
			throw new SocialException("Failure processing request", e);
		}
	}
}
