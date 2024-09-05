package org.apache.coyote.request;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final int VALID_REQUEST_LENGTH = 3;

    private final HttpMethod httpMethod;

    private final String path; // todo object

    private final String httpVersion;

    protected RequestLine(String requestLine) {
        String[] requestLines = requestLine.split(DELIMITER);
        validLength(requestLines);

        this.httpMethod = HttpMethod.from(requestLines[0]);
        this.path = requestLines[1];
        this.httpVersion = requestLines[2];
    }

    private void validLength(String[] requestLines) {
        if (requestLines.length != VALID_REQUEST_LENGTH) {
            throw new IllegalArgumentException("Request line is invalid");
        }
    }

    protected HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    protected String getPath() {
        return this.path;
    }

    protected String getHttpVersion() {
        return this.httpVersion;
    }
}
