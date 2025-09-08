package org.apache.coyote.http11;

import java.io.IOException;

public class RequestLine {

    private static final String HTTP_VERSION = "HTTP1/1.1";
    public static final String REQUEST_LINE_SEPARATOR = " ";
    public static final int METHOD_INDEX = 0;
    public static final int REQUEST_URI_INDEX = 1;

    private final HttpMethod method;
    private final String requestURI;

    public RequestLine(final String requestLine) throws IOException {
        try {
            String[] splitBySP = requestLine.split(REQUEST_LINE_SEPARATOR);
            this.method = HttpMethod.valueOf(splitBySP[METHOD_INDEX]);
            this.requestURI = splitBySP[REQUEST_URI_INDEX];
        } catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new IOException("Request Line 파싱 오휴", e);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }
}
