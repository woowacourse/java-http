package org.apache.coyote.http11.http.header;

import java.util.Arrays;

public enum ContentType {

	HTML("html", "text/html;charset=utf-8"),
	CSS("css", "text/css;charset=utf-8"),
	JS("js", "application/javascript;charset=utf-8"),
	FORM_DATA("form-data", "application/x-www-form-urlencoded"),
	ALL("", "*/*");

	private final String extension;
	private final String value;

	ContentType(String extension, String value) {
		this.extension = extension;
		this.value = value;
	}

	public static ContentType from(String extension) {
		return Arrays.stream(values())
			.filter(type -> type.extension.equals(extension))
			.findAny()
			.orElse(ALL);
	}

	public String value() {
		return value;
	}
}
