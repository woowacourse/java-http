package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpMethod {

	GET,
	POST;

	public static HttpMethod from(String name) {
		return Arrays.stream(values())
			.filter(method -> method.name().equals(name))
			.findAny()
			.orElseThrow(() -> new NoSuchElementException("존재하지 않는 메서드 입니다."));
	}

	public boolean equals(String name) {
		return name().equals(name);
	}
}
