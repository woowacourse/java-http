package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.common.HttpDelimiter.*;

public record Method(String value) {
	private static final int INDEX_OF_METHOD = 0;

	public static Method parseRequestMethod(String requestLine) {
		return new Method(requestLine.split(REQUEST_LINE_DELIMITER.getValue())[INDEX_OF_METHOD]);
	}

	public boolean isGet() {
		return value.startsWith("GET");
	}

	public boolean isPost() {
		return value.startsWith("POST");
	}
}
