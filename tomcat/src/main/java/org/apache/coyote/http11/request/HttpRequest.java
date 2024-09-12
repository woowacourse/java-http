package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.exception.ContentLengthExceededException;

public class HttpRequest {

    public static final String FIELD_VALUE_SEPARATOR = ": ";
    public static final int FIELD_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(
            final RequestLine requestLine,
            final Map<String, String> headers,
            final String body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest form(final InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        if (requestLine.isNull()) {
            return null;
        }
        final Map<String, String> headers = parseHeaders(bufferedReader);
        final String body = parseBody(bufferedReader, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    private static Map<String, String> parseHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String header;
        while ((header = bufferedReader.readLine()) != null) {
            if (header.isBlank()) {
                break;
            }
            final String[] parsedHeader = header.split(FIELD_VALUE_SEPARATOR);
            headers.put(parsedHeader[FIELD_INDEX], parsedHeader[VALUE_INDEX]);
        }
        return headers;
    }

    private static String parseBody(final BufferedReader bufferedReader, final Map<String, String> headers)
            throws IOException {
        if (headers.containsKey("Content-Length")) {
            int expectLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[expectLength];
            int actualLength = bufferedReader.read(buffer, 0, expectLength);
            if (actualLength != expectLength) {
                throw new ContentLengthExceededException("Content-Length 와 Request Body 길이가 같지 않습니다.");
            }
            return new String(buffer);
        }
        return null;
    }

    public String findHeaderValue(String headerTitle) {
        return headers.getOrDefault(headerTitle, null);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
