package org.apache.coyote.http11.request;

import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.exception.InvalidHttp11ProtocolException;
import org.apache.coyote.http11.exception.InvalidHttp11RequestLineException;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_LINE_ELEMENTS_SIZE = 3;
    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final HttpVersion httpVersion = HttpVersion.HTTP_1_1;
    private final HttpMethod method;
    private final RequestURI requestUri;

    private RequestLine(
        final HttpMethod method,
        final RequestURI target
    ) {
        this.method = method;
        this.requestUri = target;
    }

    public static RequestLine from(final String requestLine) {
        validate(requestLine);
        final String[] startParts = requestLine.split(REQUEST_LINE_DELIMITER);
        return new RequestLine(
            getMethod(startParts),
            getRequestUri(startParts)
        );
    }

    private static void validate(final String requestLine) {
        final String[] requestLineParts = requestLine.split(REQUEST_LINE_DELIMITER);
        if (requestLineParts.length != REQUEST_LINE_ELEMENTS_SIZE) {
            throw new InvalidHttp11RequestLineException();
        }

        final String httpVersion = requestLineParts[HTTP_VERSION_INDEX];
        if (!org.apache.coyote.HttpVersion.HTTP_1_1.getValue().equals(httpVersion)) {
            throw new InvalidHttp11ProtocolException(httpVersion);
        }
    }

    private static HttpMethod getMethod(final String[] requestLineParts) {
        return HttpMethod.find(requestLineParts[REQUEST_METHOD_INDEX]);
    }

    private static RequestURI getRequestUri(final String[] requestLineParts) {
        return RequestURI.from(requestLineParts[REQUEST_URI_INDEX]);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public RequestURI getRequestUri() {
        return requestUri;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
            "method=" + method +
            ", target=" + requestUri +
            ", protocolVersion=" + httpVersion +
            '}';
    }
}
