package org.apache.coyote.http11;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";

    private final String httpMethod;
    private final String uri;
    private final String httpVersion;

    public RequestLine(String requestLine) {
        String[] input = requestLine.split(REQUEST_LINE_DELIMITER);
        this.httpMethod = input[0];
        this.uri = input[1];
        this.httpVersion = input[2];
    }

    public String getUri() {
        return uri;
    }
}
