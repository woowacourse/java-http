package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestHeader {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final Map<String, String> headers;

    private RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(final List<String> lines) {
        final Map<String, String> headers = lines.stream()
                .map(it -> it.split(": "))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
        return new RequestHeader(headers);
    }

    public boolean isNotFormContentType() {
        return !Objects.equals(this.getValue(CONTENT_TYPE), FORM_CONTENT_TYPE);
    }

    public String getValue(final String key) {
        return headers.getOrDefault(key, null);
    }
}
