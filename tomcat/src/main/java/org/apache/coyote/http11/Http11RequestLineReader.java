package org.apache.coyote.http11;

import static java.util.stream.Collectors.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpProtocolVersion;
import org.apache.coyote.request.RequestLine;
import org.apache.coyote.request.RequestUri;

public class Http11RequestLineReader {

	private static final String REQUEST_LINE_DELIMITER = " ";
	private static final String QUERY_STRING_SEPARATOR = "?";
	private static final String QUERY_PARAM_DELIMITER = "&";
	private static final String KEY_VALUE_SEPARATOR = "=";

	private final BufferedReader reader;

	public Http11RequestLineReader(final BufferedReader reader) {
		this.reader = reader;
	}

	public RequestLine read() throws IOException {
		final var requestLine = reader.readLine();
		final var parts = requestLine.split(REQUEST_LINE_DELIMITER);
		final var method = HttpMethod.from(parts[0]);
		final var uri = readRequestUri(parts[1]);
		final var version = HttpProtocolVersion.from(parts[2]);
		return new RequestLine(method, uri, version);
	}

	private RequestUri readRequestUri(String uri) {
		final var queryStringIndex = uri.indexOf(QUERY_STRING_SEPARATOR);

		if (hasNoQuery(queryStringIndex)) {
			return new RequestUri(uri);
		}

		final var path = uri.substring(0, queryStringIndex);
		final var queryString = uri.substring(queryStringIndex + 1);
		final var queryParams = parseQueryString(queryString);

		return new RequestUri(path, queryParams);
	}

	private boolean hasNoQuery(final int index) {
		return index == -1;
	}

	private Map<String, String> parseQueryString(final String queryString) {
		return Arrays.stream(queryString.split(QUERY_PARAM_DELIMITER))
			.map(queryParam -> queryParam.split(KEY_VALUE_SEPARATOR, 2))
			.filter(parts -> parts.length == 2)
			.collect(toMap(parts -> parts[0], parts -> parts[1]));
	}
}
