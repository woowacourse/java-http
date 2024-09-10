package org.apache.coyote.http11.request;

public class Method {
	private final String value;

	public Method(String initialLine) {
		this.value = initialLine.split(" ")[0];
	}
}
