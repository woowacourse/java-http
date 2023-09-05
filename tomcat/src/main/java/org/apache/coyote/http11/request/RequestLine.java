package org.apache.coyote.http11.request;

public class RequestLine {

    private static final String LINE_SPLITTER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private final String httpMethod;
    private final String requestURI;
    private final String httpVersion;

    private RequestLine(final String requestLine) {
        String[] requestLineSections = requestLine.split(LINE_SPLITTER);
        this.httpMethod = requestLineSections[METHOD_INDEX];
        this.requestURI = requestLineSections[URI_INDEX];
        this.httpVersion = requestLineSections[VERSION_INDEX];
    }

    public static RequestLine from(final String requestLine) {
        return new RequestLine(requestLine);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
