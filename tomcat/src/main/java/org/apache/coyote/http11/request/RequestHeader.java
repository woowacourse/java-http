package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeader {
    private static final String DEFAULT_CONTENT_LENGTH = "0";
    private static final String HEADER_SPLIT_REGEX = ": ";

    private final Map<String, String> headers;

    private RequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(List<String> headerValues) {
        Map<String, String> headers = headerValues.stream()
            .map(value -> value.split(HEADER_SPLIT_REGEX))
            .collect(Collectors.toMap(value -> value[0], value -> value[1]));

        return new RequestHeader(headers);
    }

    public int getContentLength() {
        String contentLength = headers.getOrDefault("Content-Length", DEFAULT_CONTENT_LENGTH);
        return Integer.parseInt(contentLength);
    }

    public String get(String input) {
        return headers.get(input);
    }
}
