package org.apache.coyote.http11.request;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

import java.util.Map;

public class HttpHeaders {

	private final Map<String, String> headers;

	private HttpHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public static HttpHeaders from(final String httpRequest) {
		final Map<String, String> headerMaps = stream(httpRequest.split(System.lineSeparator()))
			.skip(1)
			.map(headerLine -> headerLine.split(":", 2))
			.collect(toMap(
				header -> header[0].trim(),
				header -> header[1].trim()
			));
		return new HttpHeaders(headerMaps);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}
