package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static common.Constants.CRLF;
import static common.Constants.SPACE;

class HttpResponseHeaders {

    private final Map<String, List<String>> headers;

    HttpResponseHeaders() {
        this.headers = new HashMap<>();
    }

    void addHeaderFieldAndValue(String field, String value) {
        Set<String> uniqueHeaderFields = Set.of("Content-Type", "Content-Length", "Location");

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
        return headers.containsKey("Resource-Path");
    }

    String getStaticResourcePath() {
        return headers.get("Resource-Path").get(0);
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
