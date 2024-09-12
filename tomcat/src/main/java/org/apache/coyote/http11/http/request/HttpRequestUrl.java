package org.apache.coyote.http11.http.request;

public class HttpRequestUrl {

	private static final String REQUEST_URL_DELIMITER = "\\?";
	private static final int REQUEST_PATH_INDEX = 0;
	private static final int REQUEST_QUERY_INDEX = 1;

	private final String path;
	private final HttpRequestQuery httpRequestQuery;

	public static HttpRequestUrl from(String requestUrl) {
		String[] urlParts = requestUrl.split(REQUEST_URL_DELIMITER);
		if (urlParts.length == 1) {
			return new HttpRequestUrl(urlParts[REQUEST_PATH_INDEX], null);
		}
		return new HttpRequestUrl(urlParts[REQUEST_PATH_INDEX], urlParts[REQUEST_QUERY_INDEX]);
	}

	public HttpRequestUrl(String path, String query) {
		this.path = path;
		this.httpRequestQuery = HttpRequestQuery.from(query);
	}

	public boolean isQueryExist() {
		return httpRequestQuery.isExist();
	}

	public String getPath() {
		return path;
	}

	public HttpRequestQuery getQuery() {
		return httpRequestQuery;
	}

	public String getRequestUrl() {
		if (httpRequestQuery.isExist()) {
			return path + "?" + httpRequestQuery.toUrl();
		}
		return path;
	}
}
