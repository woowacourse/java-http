package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.Locale;

import nextstep.jwp.exception.ExceptionType;
import nextstep.jwp.exception.InvalidHttpMethodException;

public enum HttpMethod {

	GET,
	POST,
	PUT,
	PATCH,
	DELETE;

	public static HttpMethod From(String method) {
		return Arrays.stream(values())
			.filter(it -> method.toUpperCase(Locale.ROOT).equals(it.name()))
			.findAny()
			.orElseThrow(() -> new InvalidHttpMethodException(ExceptionType.INVALID_HTTP_METHOD_EXCEPTION));
	}

	public boolean isMatch(HttpMethod method) {
		return this.equals(method);
	}

	@Override
	public String toString() {
		return "HttpMethod{}" + this.name();
	}
}
