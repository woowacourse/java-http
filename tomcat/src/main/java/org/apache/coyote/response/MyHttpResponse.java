package org.apache.coyote.response;

import org.apache.coyote.EntityHeader;
import org.apache.coyote.http11.ContentType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

public class MyHttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final Set<String> SINGLE_VALUE_HEADERS = Set.of(
            EntityHeader.CONTENT_LENGTH.fieldName(),
            EntityHeader.CONTENT_TYPE.fieldName(),
            ResponseHeader.LOCATION.fieldName()
    );

    private String statusLine;
    private final Map<String, List<String>> headers = new LinkedHashMap<>();
    private String body;

    public void setStatusCode(StatusCode statusCode) {
        statusLine = String.join(" ",
                HTTP_VERSION,
                String.valueOf(statusCode.getStatusCode()),
                statusCode.getReasonPhrase()
        );
    }

    public void addHeader(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        if (isSingleValueHeader(name)) {
            throw new IllegalArgumentException("단일 값 헤더는 setHeader를 사용해야 합니다: " + name);
        }
        headers.computeIfAbsent(name, key -> new ArrayList<>())
                .add(value);
    }

    public void setHeader(String name, String value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
        headers.put(name, new ArrayList<>(List.of(value)));
    }

    public void sendRedirect(String redirectLocation) {
        Objects.requireNonNull(redirectLocation);
        setStatusCode(StatusCode.FOUND);
        setContentType(ContentType.HTML);
        setHeader(
                "Location",
                "http://localhost:8080/" + redirectLocation
        );
    }

    public void setContentType(ContentType contentType) {
        Objects.requireNonNull(contentType);
        setHeader(
                "Content-Type",
                contentType.toString()
        );
    }

    public void writeBody(String body) {
        Objects.requireNonNull(body);
        this.body = body;
        setContentLength(body.getBytes(StandardCharsets.UTF_8).length);
    }

    private void setContentLength(int contentLength) {
        setHeader(
                "Content-Length",
                String.valueOf(contentLength)
        );
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append(statusLine).append(" \r\n");

        if (body == null || body.isEmpty()) {
            setContentLength(0);
        }
        for (Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                sb.append(entry.getKey()).append(": ")
                        .append(value).append(" \r\n");
            }
        }

        sb.append("\r\n");
        if (body != null) {
            sb.append(body);
        }
        return sb.toString();
    }

    private boolean isSingleValueHeader(String name) {
        for (String fieldName : SINGLE_VALUE_HEADERS) {
            if (fieldName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
