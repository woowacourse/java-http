package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    public Request parse(BufferedReader reader) throws IOException {
        RequestLine line = getRequestLine(reader);
        RequestHeaders headers = getRequestHeaders(reader);
        RequestBody body = getRequestBody(reader, headers.contentLength());
        return new Request(line, headers, body);
    }

    private RequestLine getRequestLine(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IllegalArgumentException("Request line은 필수입니다.");
        }
        return new RequestLine(requestLine);
    }

    private RequestHeaders getRequestHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        while (true) {
            String header = reader.readLine();
            if (header == null || header.isEmpty()) {
                break;
            }
            String[] parts = header.split(": ");
            if (parts.length != 2) {
                throw new IllegalArgumentException("올바르지 않은 Request header 포맷입니다. header: %s".formatted(header));
            }
            headers.put(parts[0], parts[1]);
        }
        return new RequestHeaders(headers);
    }

    private RequestBody getRequestBody(BufferedReader reader, String contentLength) throws IOException {
        if (contentLength == null || !reader.ready()) {
            return RequestBody.EMPTY;
        }
        int length = Integer.parseInt(contentLength);
        char[] body = new char[length];
        reader.read(body);
        return RequestBody.from(new String(body));
    }
}
