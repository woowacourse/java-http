package org.apache.coyote.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.session.SessionManager;

public class HttpRequestParser {

    private final SessionManager sessionManager;

    public HttpRequestParser(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public HttpRequest parse(BufferedReader reader) throws IOException {
        final var requestLine = parseRequestLine(reader);
        final var headers = parseHeaders(reader);
        final var body = parseBody(reader, headers);

        return new HttpRequest(
                requestLine.method(),
                requestLine.endpoint(),
                headers,
                body,
                sessionManager
        );
    }

    private RequestLine parseRequestLine(BufferedReader reader) throws IOException {
        final var line = reader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("요청 라인이 없음");
        }

        final var parts = line.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("잘못된 요청 라인: " + line);
        }

        return new RequestLine(parts[0], parts[1]);
    }

    private Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        final var headers = new HashMap<String, String>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0].trim(), headerParts[1].trim());
            }
        }

        return headers;
    }

    private String parseBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return "";
        }

        try {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 Content-Length: " + headers.get("Content-Length"));
        }
    }

    private record RequestLine(String method, String endpoint) {

    }
}
