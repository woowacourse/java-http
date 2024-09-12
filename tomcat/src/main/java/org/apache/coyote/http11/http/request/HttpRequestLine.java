package org.apache.coyote.http11.http.request;

public class HttpRequestLine {

	private static final String PART_DELIMITER = " ";
	private static final int METHOD_INDEX = 0;
	private static final int REQUEST_URL_INDEX = 1;
	private static final int HTTP_VERSION_INDEX = 2;

	private final HttpMethod method;
	private final HttpRequestUrl httpRequestUrl;
	private final String httpVersion;

	public HttpRequestLine(String method, HttpRequestUrl httpRequestUrl, String httpVersion) {
		this.method = HttpMethod.valueOf(method);
		this.httpRequestUrl = httpRequestUrl;
		this.httpVersion = httpVersion;
	}

	public static HttpRequestLine from(String requestLine) {
		String[] parts = requestLine.split(PART_DELIMITER);
		return new HttpRequestLine(
			parts[METHOD_INDEX],
			HttpRequestUrl.from(parts[REQUEST_URL_INDEX]),
			parts[HTTP_VERSION_INDEX]
		);
	}

	public boolean isSameMethod(HttpMethod method) {
		return this.method == method;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getRequestPath() {
		return httpRequestUrl.getPath();
	}

	public HttpRequestQuery getQuery() {
		return httpRequestUrl.getQuery();
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	@Override
	public String toString() {
		return "HttpRequestLine{" +
			"method='" + method + '\'' +
			", requestUrl=" + httpRequestUrl +
			", httpVersion='" + httpVersion + '\'' +
			'}';
	}
}
