package org.apache.coyote.http11.response;

public class StatusCode {
	private final int value;

	public StatusCode(String initialLine) {
		this.value = Integer.parseInt(initialLine.split(" ")[1]);
	}
}
