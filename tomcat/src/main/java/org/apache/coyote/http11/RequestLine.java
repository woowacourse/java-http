package org.apache.coyote.http11;

public class RequestLine {
    private static final String DELIMITER = " ";
    private static final String DEFAULT_REQUEST_URI = "/";

    private final HttpMethod method;
    private final String uri;
    private final String protocol;
    private final double version;

    public RequestLine(String requestLine) {
        String[] parsedRequestLine = requestLine.split(DELIMITER);
        this.method = HttpMethod.valueOf(parsedRequestLine[0]);
        this.uri = parsedRequestLine[1];
        this.protocol = parsedRequestLine[2].split("/")[0];
        this.version = Double.valueOf(parsedRequestLine[2].split("/")[1]);
    }
}
