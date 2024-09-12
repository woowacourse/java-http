package org.apache.coyote.http11.common;

import java.util.Objects;

public record VersionOfProtocol(String value) {

	public static VersionOfProtocol request(String requestLine) {
		return new VersionOfProtocol(requestLine.split(" ")[0]);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		VersionOfProtocol that = (VersionOfProtocol)o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}
}
