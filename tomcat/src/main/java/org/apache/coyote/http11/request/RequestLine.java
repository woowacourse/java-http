package org.apache.coyote.http11.request;

public class RequestLine {

    private static final int REQUEST_LINE_ELEMENTS_COUNT = 3;
    private static final String SPACE = " ";

    private final String method;
    private final String path;
    private final String version;

    public RequestLine(String requestLine) {
        String[] requestLineElements = requestLine.split(SPACE);
        validateRequestLineElementsCount(requestLineElements);
        this.method = requestLineElements[0];
        this.path = requestLineElements[1];
        this.version = requestLineElements[2];
    }

    private void validateRequestLineElementsCount(String[] requestLineElements) {
        if (requestLineElements.length != REQUEST_LINE_ELEMENTS_COUNT) {
            throw new IllegalArgumentException(
                    "request line은 공백 기준으로 쪼갰을 때, 3개의 요소로 이루어져야 합니다. " +
                            "현재 요소 갯수 = " + requestLineElements.length
            );
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
