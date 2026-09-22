package org.apache.coyote.http11;

import org.apache.coyote.exception.HttpParseException;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class HttpHeaders {
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.headers.putAll(headers);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(Map.of());
    }

    public void set(String name, String value) {
        this.headers.put(name, value);
    }

    public int getContentLength() {
        return get(CONTENT_LENGTH_HEADER)
                .map(this::parseContentLength)
                .orElse(0);
    }

    public Optional<String> get(String name) {
        return Optional.ofNullable(this.headers.get(name));
    }

    private int parseContentLength(String lengthValue) {
        try {
            int contentLength = Integer.parseInt(lengthValue.strip());
            if (contentLength < 0) {
                throw new HttpParseException("Content-Length는 음수가 될 수 없습니다.");
            }
        } catch (NumberFormatException e) {
            throw new HttpParseException("잘못된 Content-Length입니다.");
        }
        return 0;
    }
}
