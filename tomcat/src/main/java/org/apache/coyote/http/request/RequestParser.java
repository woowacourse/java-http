package org.apache.coyote.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    private static final String HEADER_DELIMITER = ": ";
    private static final int VALID_KEY_VALUE_LENGTH = 2;
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    public Request parse(BufferedReader reader) throws IOException {
        RequestLine line = new RequestLine(reader.readLine());
        RequestHeaders headers = new RequestHeaders(getRequestHeaders(reader));
        RequestBody body = RequestBody.from(getRequestBody(reader, headers.contentLength()));
        return new Request(line, headers, body);
    }

    private Map<String, String> getRequestHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String header = reader.readLine();
        while (isDataRemaining(header)) {
            putHeader(headers, header);
            header = reader.readLine();
        }
        return headers;
    }

    private boolean isDataRemaining(String header) {
        return header != null && !header.isEmpty();
    }

    private void putHeader(Map<String, String> headers, String header) {
        String[] headerParts = header.split(HEADER_DELIMITER);
        if (headerParts.length != VALID_KEY_VALUE_LENGTH) {
            throw new IllegalArgumentException("올바르지 않은 Request header 포맷입니다. header: %s".formatted(header));
        }
        headers.put(headerParts[HEADER_KEY_INDEX], headerParts[HEADER_VALUE_INDEX]);
    }

    private String getRequestBody(BufferedReader reader, String contentLength) throws IOException {
        if (isEmptyBody(reader, contentLength)) {
            return null;
        }
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        reader.read(buffer, 0, length);
        return new String(buffer);
    }

    private boolean isEmptyBody(BufferedReader reader, String contentLength) throws IOException {
        return contentLength == null || !reader.ready();
    }
}
