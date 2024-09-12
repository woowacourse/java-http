package org.apache.coyote.http11.http.request;

public class HttpRequestUrl {

	private final String path;
	private final HttpRequestQuery httpRequestQuery;

	public static HttpRequestUrl from(String requestUrl) {
		String[] urlParts = requestUrl.split("\\?");
		if (urlParts.length == 1) {
			return new HttpRequestUrl(urlParts[0], null);
		}
		return new HttpRequestUrl(urlParts[0], urlParts[1]);
	}

	public HttpRequestUrl(String path, String query) {
		this.path = path;
		this.httpRequestQuery = HttpRequestQuery.from(query);
	}

	public String getPath() {
		return path;
	}

	public HttpRequestQuery getQuery() {
		return httpRequestQuery;
	}

	@Override
	public String toString() {
		return "RequestUrl{" +
			"path='" + path + '\'' +
			", query=" + httpRequestQuery +
			'}';
	}
}
