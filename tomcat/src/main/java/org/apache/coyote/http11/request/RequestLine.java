package org.apache.coyote.http11.request;

import java.net.URI;

public class RequestLine {

	private static final String DELIMITER = " ";

	private final HttpMethod httpMethod;
	private final String path;
	private final QueryParam queryParam;
	private final HttpVersion httpVersion;

	public RequestLine(final HttpMethod httpMethod, final String path, final QueryParam queryParam,
		final HttpVersion httpVersion) {
		this.httpMethod = httpMethod;
		this.path = path;
		this.queryParam = queryParam;
		this.httpVersion = httpVersion;
	}

	public static RequestLine from(final String requestLine) {
		final String[] splitRequest = requestLine.split(DELIMITER);

		final HttpMethod httpMethod = HttpMethod.valueOf(splitRequest[0]);
		final URI uri = URI.create(splitRequest[1]);
		final QueryParam queryParam = QueryParam.from(uri.getQuery());
		final String path = uri.getPath();
		final HttpVersion httpVersion = HttpVersion.from(splitRequest[2]);

		return new RequestLine(httpMethod, path, queryParam, httpVersion);
	}

	public boolean equalPath(final String path) {
		return this.path.equals(path);
	}

	public QueryParam getQueryParam() {
		return queryParam;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public String getPath() {
		return path;
	}
}
