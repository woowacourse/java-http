package org.apache.coyote.http11;

import java.util.Arrays;

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
			.filter(it -> method.equals(it.name()))
			.findAny()
			.orElseThrow(() -> new InvalidHttpMethodException(ExceptionType.NotFoundHttpMethodException));
	}
}
