package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequestParser {

    private static final char REQUEST_URI_DELIMITER = '?';
    private static final String CHUNK_DELIMITER = " ";
    private static final int VALID_CHUNK_COUNT = 2;
    private static final int REQUEST_URI_INDEX = 1;

    public static HttpRequest parse(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final var requestLine = bufferedReader.readLine();

        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Empty request line");
        }

        final var headers = getHeaders(bufferedReader);

        final var body = getBody(headers, bufferedReader);

        return parseRequestLine(requestLine, headers, body);
    }

    private static Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while((line = bufferedReader.readLine()) != null && !line.isBlank()) {
            int headerDelimiterIndex = line.indexOf(":");

            if (headerDelimiterIndex != -1) {
                String key = line.substring(0, headerDelimiterIndex).trim();
                String value = line.substring(headerDelimiterIndex + 1).trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    private static String getBody(Map<String, String> headers, BufferedReader bufferedReader) throws IOException {
        if(headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));

            char[] bodyChars = new char[contentLength];
            int read = bufferedReader.read(bodyChars);

            return new String(bodyChars, 0, read);
        }
        return null;
    }

    private static HttpRequest parseRequestLine(final String requestLine,
                                                final Map<String, String> headers,
                                                final String body
    ) {
        final var chunks = requestLine.split(CHUNK_DELIMITER);
        if (chunks.length < VALID_CHUNK_COUNT) {
            throw new IllegalArgumentException("Invalid request line : " + requestLine);
        }

        final var requestUri = chunks[REQUEST_URI_INDEX];
        final var delimiterIndex = requestUri.indexOf(REQUEST_URI_DELIMITER);

        String resourcePath = requestUri;
        QueryParameter queryParameter = new QueryParameter();

        if (delimiterIndex != -1) {
            resourcePath = requestUri.substring(0, delimiterIndex);
            queryParameter = new QueryParameter(requestUri.substring(delimiterIndex + 1));
        }

        if(body != null && Objects.equals(headers.get("Content-Type"), "application/x-www-form-urlencoded")) {
            queryParameter.addParameterFromBody(body);
        }

        return new HttpRequest(resourcePath, queryParameter, headers, body);
    }
}
