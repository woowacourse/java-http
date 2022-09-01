package org.apache.coyote.http11.http;

public enum HttpMethod {

	GET,
	POST
	;

	public boolean equals(String name) {
		return name().equals(name);
	}
}
