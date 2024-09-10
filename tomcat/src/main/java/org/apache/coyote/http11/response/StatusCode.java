package org.apache.coyote.http11.response;

public record StatusCode(int value) {

	public static StatusCode request(String requestLine) {
		return new StatusCode(Integer.parseInt(requestLine.split(" ")[1]));
	}
}
