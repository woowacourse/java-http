package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.coyote.request.Request;

public class Http11RequestReader {

	private final Http11RequestLineReader requestLineReader;
	private final Http11RequestHeaderReader requestHeaderReader;
	private final Http11RequestBodyReader requestBodyReader;

	public Http11RequestReader(final BufferedReader reader) {
		this.requestLineReader = new Http11RequestLineReader(reader);
		this.requestHeaderReader = new Http11RequestHeaderReader(reader);
		this.requestBodyReader = new Http11RequestBodyReader(reader);
	}

	public Request read() throws IOException {
		final var requestLine = requestLineReader.read();
		final var requestHeader = requestHeaderReader.read();
		final var requestBody = requestBodyReader.read(requestHeader);
		return new Request(requestLine, requestHeader, requestBody);
	}
}
