package org.apache.coyote.http11;

import org.apache.coyote.MimeType;
import org.apache.coyote.exception.HttpParseException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = new LinkedHashMap<>();
        headers.forEach(this::add);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(Map.of());
    }

    public void addContentType(MimeType mimeType) {
        add(CONTENT_TYPE_HEADER, mimeType.getTypeName());
    }

    public void add(String name, String value) {
        this.headers.put(findName(name).orElse(name), value);
    }

    private Optional<String> findName(String name) {
        return this.headers.keySet().stream()
                .filter(headerName -> headerName.equalsIgnoreCase(name))
                .findFirst();
    }

    public int getContentLength() {
        return get(CONTENT_LENGTH_HEADER)
                .map(this::parseContentLength)
                .orElse(0);
    }

    public Optional<String> get(String name) {
        return findName(name).map(this.headers::get);
    }

    private int parseContentLength(String lengthValue) {
        try {
            int contentLength = Integer.parseInt(lengthValue.strip());
            if (contentLength < 0) {
                throw new HttpParseException("Content-Length는 음수가 될 수 없습니다.");
            }
            return contentLength;
        } catch (NumberFormatException e) {
            throw new HttpParseException("잘못된 Content-Length입니다.");
        }
    }

    public String toMessage() {
        StringBuilder builder = new StringBuilder();
        headers.forEach((name, value) -> builder.append(name).append(": ").append(value).append(" \r\n"));
        return builder.toString();
    }

    public void addContentLength(int length) {
        add(CONTENT_LENGTH_HEADER, String.valueOf(length));
    }
}
