package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpStatus {

	OK(200, " OK "),
	NOT_FOUND(404, " Not Found "),
	UNAUTHORIZED(401, " Unauthorized "),
	FOUND(302, " Found ")

	;

	private final int code;
	private final String message;

	HttpStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static HttpStatus from(int code) {
		return Arrays.stream(values())
			.filter(httpStatus -> httpStatus.code == code)
			.findAny()
			.orElseThrow(() -> new NoSuchElementException("해당하는 상태 코드가없습니다."));
	}

	public String getMessage() {
		return message;
	}

	public int value() {
		return this.code;
	}
}
