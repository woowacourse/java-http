package org.apache.coyote.http11.headers;

public enum MimeType {

	HTML("text/html;charset=utf-8"),
	CSS("text/css;"),
	JS("text/javascript;");

	private final String value;

	MimeType(final String value) {
		this.value = value;
	}

	public static MimeType parsePath(final String path) {
		int index = path.indexOf(".");
		final String fileExtension = path.substring(index + 1);
		return MimeType.valueOf(fileExtension.toUpperCase());
	}

	public String getValue() {
		return value;
	}
}
