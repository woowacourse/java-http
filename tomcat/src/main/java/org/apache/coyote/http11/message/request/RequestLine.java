package org.apache.coyote.http11.message.request;

import org.apache.coyote.http11.message.HttpMethod;

public class RequestLine {

    private static final String REQUEST_HEADER_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final RequestURI requestURI;
    private final String httpVersion;

    public RequestLine(final HttpMethod httpMethod, final RequestURI requestURI, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final String requestHeaderFirstLine) {
        final String[] splitedLine = requestHeaderFirstLine.split(REQUEST_HEADER_DELIMITER);
        return new RequestLine(
                HttpMethod.valueOf(splitedLine[HTTP_METHOD_INDEX]),
                new RequestURI(splitedLine[REQUEST_URI_INDEX]),
                splitedLine[HTTP_VERSION_INDEX]
        );
    }

    public boolean isExistRequestFile() {
        return this.requestURI.isExistFile();
    }

    public String getPath() {
        return this.requestURI.getPath();
    }

    public RequestURI getRequestURI() {
        return requestURI;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
