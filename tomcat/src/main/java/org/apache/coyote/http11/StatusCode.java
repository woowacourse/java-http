package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum StatusCode {

	OK(200, " OK "),
	NOT_FOUND(404, " Not Found ");

	private final int code;
	private final String message;

	StatusCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static StatusCode from(int code) {
		return Arrays.stream(values())
			.filter(statusCode -> statusCode.code == code)
			.findAny()
			.orElseThrow(() -> new NoSuchElementException("해당하는 상태 코드가없습니다."));
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
