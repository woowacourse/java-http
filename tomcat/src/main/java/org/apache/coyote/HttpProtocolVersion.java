package org.apache.coyote;

import java.util.Arrays;

public enum HttpProtocolVersion {
	HTTP11("HTTP/1.1"),
	;

	private final String version;

	HttpProtocolVersion(final String version) {
		this.version = version;
	}

	public static HttpProtocolVersion from(final String version) {
		return Arrays.stream(values())
			.filter(value -> value.version.equals(version))
			.findAny()
			.orElseThrow(() -> new IllegalArgumentException("지원하지 않는 HTTP 프로토콜 버전입니다."));
	}

	public String getVersion() {
		return version;
	}
}
