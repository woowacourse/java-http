package org.apache.coyote.http11.request;

import java.util.Objects;

public record Path(String value) {

	public static Path request(String requestLine) {
		return new Path(requestLine.split(" ")[1]);
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
