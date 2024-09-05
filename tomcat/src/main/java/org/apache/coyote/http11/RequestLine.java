package org.apache.coyote.http11;

import java.util.Map;

public class RequestLine {

    private static final String REQUESTLINE_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final String DEFAULT_REQUEST_URI = "/";
    private static final String WELCOME_PAGE_URI = "/index.html";
    private static final String DEFAULT_PROTOCOL = "HTTP";
    private static final double DEFAULT_VERSION = 1.1;

    private final HttpMethod method;
    private final RequestURI requestURI;
    private final String protocol;
    private final double version;

    public RequestLine(String requestLine) {
        checkNull(requestLine);
        String[] parsedRequestLine = requestLine.split(REQUESTLINE_DELIMITER);
        this.method = HttpMethod.valueOf(parsedRequestLine[HTTP_METHOD_INDEX]);
        this.requestURI = initializeRequestURI(parsedRequestLine);
        this.protocol = parseProtocol(parsedRequestLine);
        this.version = parseVersion(parsedRequestLine);
    }

    private void checkNull(String requestLine) {
        if (requestLine == null) {
            throw new IllegalArgumentException("requestLine is null");
        }
    }

    private RequestURI initializeRequestURI(String [] requestLine) {
        if(requestLine.length < REQUEST_URI_INDEX){
            return null;
        }

        String requestUri = requestLine[REQUEST_URI_INDEX];
        if (DEFAULT_REQUEST_URI.equals(requestUri)) {
            return new RequestURI(WELCOME_PAGE_URI);
        }
        return new RequestURI(requestUri);
    }

    private String parseProtocol(String[] parsedRequestLine) {
        if (parsedRequestLine.length < PROTOCOL_INDEX) {
            return DEFAULT_PROTOCOL;
        }
        return parsedRequestLine[PROTOCOL_INDEX].split("/")[0];
    }

    private double parseVersion(String[] parsedRequestLine) {
        if (parsedRequestLine.length < PROTOCOL_INDEX) {
            return DEFAULT_VERSION;
        }
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

    public boolean isQueryStringRequest() {
        return requestURI.isQueryStringUri();
    }
}
