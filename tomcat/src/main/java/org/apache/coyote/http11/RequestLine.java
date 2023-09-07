package org.apache.coyote.http11;

public class RequestLine {

    private static final String SP = " ";
    private static final int STANDARD_ELEMENT_COUNT = 3;

    private final HttpMethod method;
    private final HttpTarget target;
    private final String httpVersion;

    public RequestLine(String rawRequestLine) {
        String[] elements = rawRequestLine.split(SP);
        validateLengthOf(elements);
        this.method = new HttpMethod(elements[0]);
        this.target = new HttpTarget(elements[1]);
        this.httpVersion = elements[2];
    }

    private void validateLengthOf(String[] elements) {
        if (elements.length != STANDARD_ELEMENT_COUNT) {
            throw new IllegalArgumentException("RequestLine의 요소 개수는 3개여야 합니다");
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpTarget getTarget() {
        return target;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
