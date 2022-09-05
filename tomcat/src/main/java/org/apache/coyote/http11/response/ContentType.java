package org.apache.coyote.http11.response;

import java.util.Arrays;

import nextstep.jwp.exception.ExceptionType;
import nextstep.jwp.exception.InvalidHttpResponseException;

public enum ContentType {

	HTML("html", "text/html"),
	CSS("css", "text/css"),
	JS("js", "application/javascript"),
	SVG("svg", "image/svg+xml");

	private String type;
	private String value;

	ContentType(String type, String value) {
		this.type = type;
		this.value = value;
	}

	public static ContentType from(String url) {
		return Arrays.stream(values())
			.filter(it -> url.endsWith(it.type))
			.findFirst()
			.orElseThrow(() -> new InvalidHttpResponseException(ExceptionType.NOT_FOUND_CONTENT_TYPE_EXCEPTION));
	}

	public String getValue() {
		return value;
	}
}
