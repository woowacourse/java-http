package org.apache.coyote.http11.common;

public record VersionOfProtocol(String value) {

	public static VersionOfProtocol request(String initialLine) {
		return new VersionOfProtocol(initialLine.split(" ")[0]);
	}
}
