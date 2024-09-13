package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.common.HttpDelimiter.*;

import java.util.Objects;

public record Path(String value) {
	private static final int INDEX_OF_PATH = 1;

	public static Path parseRequestToPath(String requestLine) {
		return new Path(requestLine.split(REQUEST_LINE_DELIMITER.getValue())[INDEX_OF_PATH]);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Path path = (Path)o;
		return Objects.equals(value, path.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}
}
