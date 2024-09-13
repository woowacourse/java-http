package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.HttpDelimiter.*;

import java.util.Objects;

public record VersionOfProtocol(String value) {
	private static final int INDEX_OF_VERSION_OF_PROTOCOL = 2;

	public static VersionOfProtocol parseReqeustVersionOfProtocol(String requestLine) {
		return new VersionOfProtocol(requestLine.split(REQUEST_LINE_DELIMITER.getValue())[INDEX_OF_VERSION_OF_PROTOCOL]);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VersionOfProtocol that = (VersionOfProtocol)o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}
}
