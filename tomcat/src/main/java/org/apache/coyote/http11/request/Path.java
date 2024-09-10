package org.apache.coyote.http11.request;

public record Path(String value) {

	public static Path request(String requestLine) {
		return new Path(requestLine.split(" ")[1]);
	}
}
