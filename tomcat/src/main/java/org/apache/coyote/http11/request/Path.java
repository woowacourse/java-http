package org.apache.coyote.http11.request;

public class Path {
	private final String value;

	public Path(String initialLine) {
		this.value = initialLine.split(" ")[1];
	}
}
