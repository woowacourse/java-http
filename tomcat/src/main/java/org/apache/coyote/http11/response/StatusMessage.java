package org.apache.coyote.http11.response;

public class StatusMessage {
	private final String value;

	public StatusMessage(String initialLine) {
		this.value = initialLine.split(" ")[2];
	}
}
