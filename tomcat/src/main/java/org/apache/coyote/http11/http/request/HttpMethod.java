package org.apache.coyote.http11.http.request;

public enum HttpMethod {
	GET, POST, PUT, DELETE, HEAD, OPTIONS;

	public static HttpMethod from(String method) {
		return HttpMethod.valueOf(method);
	}
}
