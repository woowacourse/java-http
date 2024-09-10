package org.apache.coyote.http11.response;

public record StatusMessage(String value) {

	public static StatusMessage request(String initialLine) {
		return new StatusMessage(initialLine.split(" ")[2]);
	}
}
