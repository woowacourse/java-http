package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

	HTML("html", "text/html"),
	CSS("css", "text/css"),
	JS("js", "application/javascript"),
	ICO("ico", "image/x-icon");

	private final String extension;
	private final String contentType;

	ContentType(String extension, String contentType) {
		this.extension = extension;
		this.contentType = contentType;
	}

	public String getExtension() {
		return extension;
	}

	public String getContentType() {
		return contentType;
	}

	public static String getByExtension(String extension) {
		return Arrays.stream(values())
			.filter(type -> type.getExtension().equals(extension))
			.findFirst()
			.map(ContentType::getContentType)
			.orElseThrow(() -> new IllegalArgumentException("cannot find content-type by extension : " + extension));
	}

}
