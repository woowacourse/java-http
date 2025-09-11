package org.apache.coyote.http11.request.startline;

public class RequestLineParser {

    private static final String REQUEST_HEADER_SEPARATOR = " ";
    private static final RequestLineParser INSTANCE = new RequestLineParser();

    public static RequestLineParser getInstance() {
        return INSTANCE;
    }

    public HttpMethod parseMethod(final String rawRequestLine) {
        final String[] requestLineElements = rawRequestLine.split(REQUEST_HEADER_SEPARATOR);
        return HttpMethod.from(requestLineElements[0]);
    }

    public RequestUri parseUri(final String rawRequestLine) {
        final String[] requestLineElements = rawRequestLine.split(REQUEST_HEADER_SEPARATOR);
        return RequestUri.from(requestLineElements[1]);
    }

    public String parseVersion(final String rawRequestLine) {
        final String[] requestLineElements = rawRequestLine.split(REQUEST_HEADER_SEPARATOR);
        return requestLineElements[2];
    }

    private RequestLineParser() {
    }
}
