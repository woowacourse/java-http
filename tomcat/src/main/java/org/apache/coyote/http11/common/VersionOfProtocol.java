package org.apache.coyote.http11.common;

public class VersionOfProtocol {
	private final String value;

	public VersionOfProtocol(String initialLine) {
		this.value = initialLine.split(" ")[0];
	}
}
