package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestHeader;
import org.apache.coyote.response.HeaderType;

public class Http11RequestBodyReader {

	private final BufferedReader reader;

	public Http11RequestBodyReader(final BufferedReader reader) {
		this.reader = reader;
	}

	public RequestBody read(final RequestHeader header) throws IOException {
		final var contentLength = header.find(HeaderType.CONTENT_LENGTH.getName());
		if (contentLength == null) {
			return RequestBody.empty();
		}
		final var bodySize = Integer.parseInt(contentLength);
		final var buffer = new char[bodySize];
		reader.read(buffer, 0, bodySize);
		return RequestBody.from(new String(buffer));
	}
}
