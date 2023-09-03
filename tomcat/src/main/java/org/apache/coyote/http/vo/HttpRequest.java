package org.apache.coyote.http.vo;

import org.apache.coyote.http.HttpMethod;

public class HttpRequest {

    private static final String REQUEST_LINE_DELIMITER = "\n";
    private static final String START_LINE_DELIMITER = " ";

    private final HttpMethod method;
    private final Url url;
    private final HttpHeaders headers;

    private HttpRequest(
            final HttpMethod method,
            final Url url,
            final HttpHeaders headers
    ) {
        this.method = method;
        this.url = url;
        this.headers = headers;
    }

    public static HttpRequest of(final String fullRawRequest) {
        return new HttpRequest(
                getHttpMethod(fullRawRequest),
                getUrl(fullRawRequest),
                getHeaders(fullRawRequest)
        );
    }

    private static HttpMethod getHttpMethod(final String fullRawRequest) {
        return HttpMethod.valueOf(getRawStartLine(fullRawRequest)[0]);
    }

    private static String[] getRawStartLine(final String fullRawRequest) {
        int startLineIndex = fullRawRequest.indexOf(REQUEST_LINE_DELIMITER);
        return fullRawRequest.substring(0, startLineIndex).split(START_LINE_DELIMITER);
    }

    private static Url getUrl(final String fullRawRequest) {
        return new Url(getRawStartLine(fullRawRequest)[1]);
    }

    private static HttpHeaders getHeaders(final String fullRawRequest) {
        final String rawHeaders = fullRawRequest.substring(fullRawRequest.indexOf(REQUEST_LINE_DELIMITER)+1);
        return new HttpHeaders(rawHeaders);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Url getUrl() {
        return url;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}
