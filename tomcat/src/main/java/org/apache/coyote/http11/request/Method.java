package org.apache.coyote.http11.request;

public record Method(String value) {

	public static Method request(String requestLine) {
		return new Method(requestLine.split(" ")[0]);
	}
}
