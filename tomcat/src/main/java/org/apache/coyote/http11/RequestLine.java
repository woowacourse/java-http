package org.apache.coyote.http11;

import java.util.Map;

public class RequestLine {
    private static final String REQUESTLINE_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final String DEFAULT_REQUEST_URI = "/";

    private final HttpMethod method;
    private final RequestURI requestURI;
    private final String protocol;
    private final double version;

    public RequestLine(String requestLine) {
        checkNull(requestLine);
        String[] parsedRequestLine = requestLine.split(REQUESTLINE_DELIMITER);
        this.method = HttpMethod.valueOf(parsedRequestLine[HTTP_METHOD_INDEX]);
        this.requestURI = new RequestURI(parsedRequestLine[REQUEST_URI_INDEX], new QueryStringParser());
        this.protocol = parseProtocol(parsedRequestLine);
        this.version = parseVersion(parsedRequestLine);
    }

    private void checkNull(String requestLine) {
        if (requestLine == null) {
            throw new IllegalArgumentException("requestLine is null");
        }
    }

    private String parseProtocol(String[] parsedRequestLine) {
        return parsedRequestLine[PROTOCOL_INDEX].split("/")[0];
    }

    private double parseVersion(String[] parsedRequestLine) {
        return Double.valueOf(parsedRequestLine[2].split("/")[1]);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI.getUri();
    }

    public Map<String, String> getParameters() {
        return requestURI.getParameters();
    }
}
