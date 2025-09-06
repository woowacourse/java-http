package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

record RequestLine(
        String method,
        String uri,
        String protocol
) {
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final int EXPECTED_PARTS_COUNT = 3;
    
    public static RequestLine from(InputStream inputStream) throws IOException {
        String[] parts = parseParts(inputStream);
        return new RequestLine(
            parts[METHOD_INDEX],
            parts[URI_INDEX], 
            parts[PROTOCOL_INDEX]
        );
    }
    
    private static String[] parseParts(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine;
        do {
            requestLine = reader.readLine();
        } while (requestLine != null && requestLine.trim().isEmpty());

        if (requestLine == null) {
            throw new IllegalArgumentException("잘못된 요청 입니다.");
        }

        String[] parts = requestLine.trim().split(" ");
        if (parts.length != EXPECTED_PARTS_COUNT) {
            throw new IllegalArgumentException("HTTP 요청 라인은 METHOD URI PROTOCOL 형식이어야 합니다.");
        }
        return parts;
    }
    
    String getMethod() { return method; }
    String getUri() { return uri; }
}
