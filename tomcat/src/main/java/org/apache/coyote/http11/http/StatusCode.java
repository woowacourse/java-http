package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum StatusCode {

	OK(200, " OK "),
	NOT_FOUND(404, " Not Found "),
	UNAUTHORIZED(401, " Unauthorized "),
	FOUND(302, " Found ")

	;

	private final int value;
	private final String message;

	StatusCode(int value, String message) {
		this.value = value;
		this.message = message;
	}

	public static StatusCode from(int code) {
		return Arrays.stream(values())
			.filter(statusCode -> statusCode.value == code)
			.findAny()
			.orElseThrow(() -> new NoSuchElementException("해당하는 상태 코드가없습니다."));
	}

	public int getValue() {
		return value;
	}

	public String getMessage() {
		return message;
	}

	public int value() {
		return this.value;
	}
}
