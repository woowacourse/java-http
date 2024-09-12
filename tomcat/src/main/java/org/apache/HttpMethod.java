package org.apache;

import java.util.Arrays;

public enum HttpMethod {
	GET,
	POST,
	PUT,
	DELETE,
	HEAD,
	OPTIONS,
	PATCH,
	TRACE;

	public static HttpMethod from(String method) {
		return Arrays.stream(values())
			.filter(httpMethod -> httpMethod.name().equalsIgnoreCase(method))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("invalid HTTP Method"));
	}
}
