package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestHeader;
import org.apache.coyote.response.HeaderType;

public class Http11RequestReader {

	private static final String END_OF_LINE = "";

	private final BufferedReader reader;

	public Http11RequestReader(final BufferedReader reader) {
		this.reader = reader;
	}

	public Request read() throws IOException {
		final var requestLine = Http11RequestLineParser.parse(readRequestLine());
		final var requestHeader = Http11RequestHeaderParser.parse(readRequestHeaders());
		final var requestBody = readRequestBody(requestHeader);
		return new Request(requestLine, requestHeader, requestBody);
	}

	private String readRequestLine() throws IOException {
		return reader.readLine();
	}

	private List<String> readRequestHeaders() throws IOException {
		final var requestHeaders = new ArrayList<String>();
		String line;
		while ((!END_OF_LINE.equals(line = reader.readLine()))) {
			requestHeaders.add(line);
		}
		return requestHeaders;
	}

	public RequestBody readRequestBody(final RequestHeader header) throws IOException {
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
