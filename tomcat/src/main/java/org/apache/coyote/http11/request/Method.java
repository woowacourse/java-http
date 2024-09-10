package org.apache.coyote.http11.request;

public record Method(String value) {

	public static Method request(String initialLine) {
		return new Method(initialLine.split(" ")[0]);
	}
}
