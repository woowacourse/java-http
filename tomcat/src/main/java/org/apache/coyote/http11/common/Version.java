package org.apache.coyote.http11.common;

import static nextstep.jwp.exception.ExceptionType.INVALID_HTTP_VERSION_EXCEPTION;

import java.util.Arrays;

import nextstep.jwp.exception.InvalidHttpResponseException;

public enum Version {

	HTTP_1_1("HTTP/1.1"),
	HTTP_2("HTTP/2");

	private final String version;

	Version(String version) {
		this.version = version;
	}

	public static Version from(String metaData) {
		return Arrays.stream(values())
			.filter(it -> it.name().equals(metaData))
			.findFirst()
			.orElseThrow(() -> new InvalidHttpResponseException(INVALID_HTTP_VERSION_EXCEPTION));
	}

	public String getVersion() {
		return version;
	}
}
