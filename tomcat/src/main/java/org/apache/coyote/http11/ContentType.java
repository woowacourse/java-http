package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum ContentType {

	HTML("html", "text/html"),
	CSS("css", "text/css"),
	JS("js", "application/javascript");

	private final String extension;
	private final String type;

	ContentType(String extension, String type) {
		this.extension = extension;
		this.type = type;
	}

	public static ContentType from(String extension) {
		return Arrays.stream(values())
			.filter(type -> type.extension.equals(extension))
			.findAny()
			.orElseThrow(() -> new NoSuchElementException("해당하는 Content-Type이 없습니다."));
	}

	public String getType() {
		return type;
	}
}
