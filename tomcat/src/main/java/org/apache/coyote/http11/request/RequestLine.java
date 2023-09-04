package org.apache.coyote.http11.request;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;

    private final String requestMethod;
    private final String requestUri;

    private RequestLine(final String requestMethod, final String requestUri) {
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
    }

    public static RequestLine from(final String line) {
        final String[] lineSplit = line.split(DELIMITER);
        return new RequestLine(lineSplit[METHOD_INDEX], lineSplit[URI_INDEX]);
    }

    public String getRequestUri() {
        return requestUri;
    }
}
