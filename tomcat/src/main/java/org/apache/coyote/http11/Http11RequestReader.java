package org.apache.coyote.http11;

import static java.util.stream.Collectors.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.coyote.Cookie;
import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpProtocolVersion;
import org.apache.coyote.request.Request;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestHeader;
import org.apache.coyote.request.RequestLine;
import org.apache.coyote.request.RequestUri;
import org.apache.coyote.response.HeaderType;

public class Http11RequestReader {

	private static final String REQUEST_LINE_DELIMITER = " ";
	private static final String END_OF_LINE = "";
	private static final String HEADER_SEPARATOR = ": ";
	private static final String COOKIE_DELIMITER = "; ";
	private static final String COOKIE_KEY_VALUE_SEPARATOR = "=";

	private final BufferedReader reader;

	public Http11RequestReader(final BufferedReader reader) {
		this.reader = reader;
	}

	public Request read() throws IOException {
		final var requestLine = readRequestLine();
		final var requestHeader = readRequestHeader();
		final var requestBody = readRequestBody(requestHeader);
		return new Request(requestLine, requestHeader, requestBody);
	}

	private RequestLine readRequestLine() throws IOException {
		final var requestLine = reader.readLine();
		final var parts = requestLine.split(REQUEST_LINE_DELIMITER);
		final var method = HttpMethod.from(parts[0]);
		final var uri = RequestUri.from(parts[1]);
		final var version = HttpProtocolVersion.version(parts[2]);
		return new RequestLine(method, uri, version);
	}

	private RequestHeader readRequestHeader() throws IOException {
		final var headerString = new ArrayList<String>();
		String line;
		while ((!END_OF_LINE.equals(line = reader.readLine()))) {
			headerString.add(line);
		}
		final var headers = headerString.stream()
			.map(header -> header.split(HEADER_SEPARATOR, 2))
			.collect(toMap(parts -> parts[0], parts -> parts[1]));
		final var cookie = headers.remove(HeaderType.COOKIE.getName());
		return new RequestHeader(headers, readCookies(cookie));
	}

	private List<Cookie> readCookies(final String cookieString) {
		if (cookieString == null) {
			return new ArrayList<>();
		}
		return Arrays.stream(cookieString.split(COOKIE_DELIMITER))
			.map(field -> field.split(COOKIE_KEY_VALUE_SEPARATOR))
			.filter(parts -> parts.length == 2)
			.map(parts -> new Cookie(parts[0], parts[1]))
			.collect(toList());
	}

	private RequestBody readRequestBody(final RequestHeader header) throws IOException {
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
