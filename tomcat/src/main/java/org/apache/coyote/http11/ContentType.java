package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

	HTML("html", "text/html"),
	CSS("css", "text/css"),
	JAVASCRIPT("js", "text/javascript"),
	SVG("svg", "image/svg+xml"),
	;

	private final String extension;
	private final String contentType;

	ContentType(final String extension, final String contentType) {
		this.extension = extension;
		this.contentType = contentType;
	}

	public static String getContentType(String extension) {
		return Arrays.stream(values())
			.filter(value -> value.extension.equals(extension))
			.map(value -> value.contentType)
			.findFirst()
			.orElseGet(() -> HTML.contentType);
	}
}
