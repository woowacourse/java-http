package org.apache.coyote.http11;

public enum HttpMethod {
	GET,
	POST,
	;

	public static HttpMethod from(String method) {
		try {
			return valueOf(method.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다.");
		}
	}
}
