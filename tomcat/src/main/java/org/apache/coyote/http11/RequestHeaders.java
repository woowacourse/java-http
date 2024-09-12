package org.apache.coyote.http11;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private static final String MESSAGE_HEADER_SEPARATOR = ":";
    private static final String COOKIE_HEADER_NAME = "Cookie";

    private final Map<String, String> headers;

    public RequestHeaders(List<String> rawRequestHeaders) {
        headers = rawRequestHeaders.stream()
                .collect(Collectors.toMap(this::parseFieldName, this::parseFieldValue));
    }

    private String parseFieldName(String requestHeaderPart) {
        int separatorIndex = requestHeaderPart.indexOf(MESSAGE_HEADER_SEPARATOR);
        return requestHeaderPart.substring(0, separatorIndex).toLowerCase();
    }

    private String parseFieldValue(String requestHeaderPart) {
        int separatorIndex = requestHeaderPart.indexOf(MESSAGE_HEADER_SEPARATOR);
        return requestHeaderPart.substring(separatorIndex + 1).trim();
    }

    public String getHeaderValue(String name) {
        return headers.getOrDefault(name.toLowerCase(), "");
    }

    public HttpCookies getCookies() {
        return new HttpCookies(getHeaderValue(COOKIE_HEADER_NAME));
    }
}
