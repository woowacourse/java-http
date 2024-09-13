package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.common.HTTP_DELIMITER.*;

public record Method(String value) {
	public static Method parseRequestMethod(String requestLine) {
		return new Method(requestLine.split(REQUEST_LINE_DELIMITER.getValue())[0]);
	}

	public boolean isGet() {
		return value.startsWith("GET");
	}

	public boolean isPost() {
		return value.startsWith("POST");
	}
}
