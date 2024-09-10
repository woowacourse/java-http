package org.apache.coyote.http11.common;

public enum ContentType {
	APPLICATION_JSON("application/json"),
	TEXT_CSS("text/css"),
	TEXT_HTML("text/html"),
	APPLICATION_JAVASCRIPT("text/javascript"),
	;

	private final String value;

	ContentType(String value) {
		this.value = value;
	}

	public static ContentType fromPath(String path) {
		if (path.endsWith("css")) {
			return ContentType.TEXT_CSS;
		}
		if (path.endsWith("js")) {
			return ContentType.APPLICATION_JSON;
		}
		if (path.endsWith("html")) {
			return ContentType.TEXT_HTML;
		}
		return ContentType.APPLICATION_JSON;
	}
}
