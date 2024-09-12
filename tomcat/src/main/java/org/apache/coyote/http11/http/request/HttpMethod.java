package org.apache.coyote.http11.http.request;

public enum HttpMethod {
	GET, POST;

	public static HttpMethod from(String method) {
		return HttpMethod.valueOf(method);
	}
}
