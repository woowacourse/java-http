package org.apache.coyote.http11.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.exception.InvalidHttpFormException;

public class HttpRequestReader {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String REQUEST_HEADER_DELIMITER = ": ";

    private HttpRequestReader() {
    }

    public static HttpRequest read(final BufferedReader bufferedReader) throws IOException {
        final var requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            throw new InvalidHttpFormException();
        }
        final var requestLineAttributes = requestLine.split(REQUEST_LINE_DELIMITER);
        Map<String, String> headers = readRequestHeaders(bufferedReader);
        String body = readRequestBody(headers, bufferedReader);
        return HttpRequest.Builder.builder()
                .httpMethod(HttpMethod.valueOf(requestLineAttributes[0]))
                .uri(requestLineAttributes[1])
                .path(HttpParser.parsePath(requestLineAttributes[1]))
                .parameters(HttpParser.parseQueryParameters(requestLineAttributes[1]))
                .protocol(requestLineAttributes[2])
                .headers(new HttpHeaders(headers))
                .body(body)
                .build();
    }

    private static Map<String, String> readRequestHeaders(BufferedReader bufferedReader)
            throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        while (bufferedReader.ready()) {
            final var header = bufferedReader.readLine();
            if (header.isEmpty()) {
                break;
            }
            final var keyValuePair = header.split(REQUEST_HEADER_DELIMITER);
            headers.put(keyValuePair[0], keyValuePair[1]);
        }
        return headers;
    }

    private static String readRequestBody(Map<String, String> headers,
            BufferedReader bufferedReader)
            throws IOException {
        if (!headers.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            return null;
        }
        int contentLength = Integer.parseInt(headers.get(HttpHeaders.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
