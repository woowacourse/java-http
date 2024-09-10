package org.apache.coyote.http11.response;

public record StatusMessage(String value) {

	public static StatusMessage request(String requestLine) {
		return new StatusMessage(requestLine.split(" ")[2]);
	}
}
