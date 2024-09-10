package org.apache.coyote.http11.response;

public record StatusCode(int value) {

	public static StatusCode request(String initialLine) {
		return new StatusCode(Integer.parseInt(initialLine.split(" ")[1]));
	}
}
