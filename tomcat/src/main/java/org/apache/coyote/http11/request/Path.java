package org.apache.coyote.http11.request;

public record Path(String value) {

	public static Path request(String initialLine) {
		return new Path(initialLine.split(" ")[1]);
	}
}
