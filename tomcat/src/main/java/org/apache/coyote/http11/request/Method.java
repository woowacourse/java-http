package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.common.HttpDelimiter.*;

import java.util.Objects;

public record Method(String value) {
	private static final int INDEX_OF_METHOD = 0;

	public static Method parseRequestToMethod(String requestLine) {
		return new Method(requestLine.split(REQUEST_LINE_DELIMITER.getValue())[INDEX_OF_METHOD]);
	}

	public boolean isGet() {
		return value.startsWith("GET");
	}

	public boolean isPost() {
		return value.startsWith("POST");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Method method = (Method)o;
		return Objects.equals(value, method.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}
}
