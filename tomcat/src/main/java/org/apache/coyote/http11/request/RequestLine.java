package org.apache.coyote.http11.request;

import java.util.List;
import java.util.Objects;

public class RequestLine {
    private static final int ELEMENTS_COUNT = 3;
    private static final String ELEMENT_DELIMITER = " ";
    private static final int METHOD_IDX = 0;
    private static final int REQUEST_URI_IDX = 1;
    private static final int VERSION_IDX = 2;

    private final String method;
    private final RequestUri requestUri;
    private final String version;

    public RequestLine(String inputRequestLine) {
        validate(inputRequestLine);
        List<String> elements = List.of(inputRequestLine.split(ELEMENT_DELIMITER));
        this.method = elements.get(METHOD_IDX);
        this.requestUri = new RequestUri(elements.get(REQUEST_URI_IDX));
        this.version = elements.get(VERSION_IDX);
    }

    private void validate(String inputRequestLine) {
        checkNotNullOrBlank(inputRequestLine);
        checkElementsCount(inputRequestLine);
    }

    private void checkNotNullOrBlank(String inputRequestLine) {
        if (Objects.isNull(inputRequestLine) || inputRequestLine.isBlank()) {
            throw new IllegalArgumentException("Request Line은 비어있을 수 없습니다.");
        }
    }

    private void checkElementsCount(String inputRequestLine) {
        if (inputRequestLine.split(" ").length != ELEMENTS_COUNT) {
            throw new IllegalArgumentException("Request Line 형식이 올바르지 않습니다. 형식은 'METHOD PATH PROTOCOL_VERSION'이어야 합니다.");
        }
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri.getRequestUri();
    }

    public String getRequestUrl() {
        return requestUri.getRequestUrl();
    }

    public String getExtension() {
        return requestUri.getExtension();
    }
}
