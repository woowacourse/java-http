package org.apache.coyote.http;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final String NEW_LINE = "\r\n";

    private final Map<String, String> values;

    public ResponseHeader() {
        this.values = new LinkedHashMap<>();
    }

    public void addContentType(final String contentType) {
        values.put("Content-Type", contentType + ";charset=utf-8");
    }

    public void addContentLength(final int contentLength) {
        values.put("Content-Length", String.valueOf(contentLength));
    }

    public void addLocation(final String resource) {
        values.put("Location", resource);
    }

    public void addCookie(final Cookie cookie) {
        values.put("Set-Cookie", cookie.toHeaderForm());
    }

    public String toHeaderString() {
        return values.entrySet()
                .stream()
                .map(it -> it.getKey() + HEADER_DELIMITER + it.getValue() + " ")
                .collect(Collectors.joining(NEW_LINE, "", NEW_LINE));
    }
}
