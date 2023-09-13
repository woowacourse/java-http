package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.Constants.CRLF;
import static org.apache.Constants.SPACE;

class HttpResponseHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";
    public static final String RESOURCE_PATH = "Resource-Path";

    private final Map<String, List<String>> headers;

    HttpResponseHeaders() {
        this.headers = new HashMap<>();
    }

    void addHeaderFieldAndValue(String field, String value) {
        Set<String> uniqueHeaderFields = Set.of(CONTENT_TYPE, CONTENT_LENGTH, LOCATION);

        if (headers.containsKey(field) && uniqueHeaderFields.contains(field)) {
            throw new IllegalStateException(String.format("헤더에 이미 %s에 대한 값이 존재합니다.", field));
        }

        if (headers.containsKey(field) && !uniqueHeaderFields.contains(field)) {
            headers.get(field).add(value);
            return;
        }

        headers.put(field, List.of(value));
    }

    boolean hasStaticResourcePath() {
        return headers.containsKey(RESOURCE_PATH);
    }

    String getStaticResourcePath() {
        return headers.get(RESOURCE_PATH).get(0);
    }

    public long getContentLength() {
        return Long.parseLong(headers.get(CONTENT_LENGTH).get(0));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, List<String>> fieldAndValue : headers.entrySet()) {
            for (String value : fieldAndValue.getValue()) {
                stringBuilder
                        .append(fieldAndValue.getKey())
                        .append(":")
                        .append(SPACE)
                        .append(value)
                        .append(SPACE)
                        .append(CRLF);
            }
        }

        return stringBuilder.toString();
    }
}
