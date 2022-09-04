package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_TARGET_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;
    private final HttpVersion httpVersion;

    private RequestLine(final HttpMethod httpMethod, final RequestTarget requestTarget, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        List<String> requestLines = Arrays.asList(requestLine.split(REQUEST_LINE_DELIMITER));

        return new RequestLine(
                HttpMethod.valueOf(requestLines.get(HTTP_METHOD_INDEX)),
                RequestTarget.from(requestLines.get(REQUEST_TARGET_INDEX)),
                HttpVersion.from(requestLines.get(HTTP_VERSION_INDEX)));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestTarget getRequestTarget() {
        return requestTarget;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
