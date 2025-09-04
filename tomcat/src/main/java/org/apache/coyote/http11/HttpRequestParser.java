package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestParser {

    private static final char REQUEST_URI_DELIMITER = '?';

    public static HttpRequest parse(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final var requestLine = bufferedReader.readLine();

        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Empty request line");
        }
        return parseRequestLine(requestLine);
    }

    private static HttpRequest parseRequestLine(final String requestLine) {
        final var chunks = requestLine.split(" ");
        if (chunks.length < 2) {
            throw new IllegalArgumentException("Invalid request line : " + requestLine);
        }

        final var requestUri = chunks[1];
        int delimiterIndex = requestUri.indexOf(REQUEST_URI_DELIMITER);

        if (delimiterIndex == -1) {
            return new HttpRequest(requestUri, new QueryParameters());
        }
        final var resourcePath = requestUri.substring(0, delimiterIndex);
        final var queryParameters = new QueryParameters(requestUri.substring(delimiterIndex + 1));

        return new HttpRequest(resourcePath, queryParameters);
    }
}
