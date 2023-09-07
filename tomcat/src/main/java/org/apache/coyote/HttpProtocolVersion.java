package org.apache.coyote;

public enum HttpProtocolVersion {
	HTTP11("HTTP/1.1"),
	;

	private final String version;

	HttpProtocolVersion(final String version) {
		this.version = version;
	}

	public static HttpProtocolVersion version(String version) {
		for (final HttpProtocolVersion value : values()) {
			if (value.version.equals(version)) {
				return value;
			}
		}
		throw new IllegalArgumentException("지원하지 않는 HTTP 프로토콜 버전입니다.");
	}

	public String getVersion() {
		return version;
	}
}
