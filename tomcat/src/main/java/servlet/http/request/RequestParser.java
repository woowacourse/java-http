package servlet.http.request;

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
        String header = reader.readLine();
        while (isDataRemaining(header)) {
            putHeader(headers, header);
            header = reader.readLine();
        }
        return new RequestHeaders(headers);
    }

    private boolean isDataRemaining(String header) {
        return header != null && !header.isEmpty();
    }

    private void putHeader(Map<String, String> headers, String header) {
        String[] headerParts = header.split(": ");
        if (headerParts.length != 2) {
            throw new IllegalArgumentException("올바르지 않은 Request header 포맷입니다. header: %s".formatted(header));
        }
        headers.put(headerParts[0], headerParts[1]);
    }

    private RequestBody getRequestBody(BufferedReader reader, String contentLength) throws IOException {
        if (isEmptyBody(reader, contentLength)) {
            return RequestBody.EMPTY;
        }
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        reader.read(buffer, 0, length);
        return RequestBody.from(new String(buffer));
    }

    private boolean isEmptyBody(BufferedReader reader, String contentLength) throws IOException {
        return contentLength == null || !reader.ready();
    }
}
