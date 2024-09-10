package org.apache.coyote.http11.common;

public record VersionOfProtocol(String value) {

	public static VersionOfProtocol request(String requestLine) {
		return new VersionOfProtocol(requestLine.split(" ")[0]);
	}
}
