package org.apache.coyote.http11.http;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum ContentType {

	HTML("html", "text/html;charset=utf-8"),
	CSS("css", "text/css;charset=utf-8"),
	JS("js", "application/javascript;charset=utf-8"),
	FORM_DATA("form-data", "application/x-www-form-urlencoded"),
	ALL("", "*/*")

	;

	private final String extension;
	private final String format;

	ContentType(String extension, String format) {
		this.extension = extension;
		this.format = format;
	}

	public static ContentType from(String extension) {
		return Arrays.stream(values())
			.filter(type -> type.extension.equals(extension))
			.findAny()
			.orElse(ALL);
	}

	public String getFormat() {
		return format;
	}
}
