package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.header.Headers;

import java.util.List;

public class HttpRequest {

    private final String uri;
    private final Headers headers;

    private HttpRequest(final String uri, final Headers headers) {
        this.uri = uri;
        this.headers = headers;
    }

    public static HttpRequest from(final List<String> request) {
        String uri = parseUri(request);
        Headers headers = Headers.from(request.subList(1, request.size()));

        return new HttpRequest(uri, headers);
    }

    private static String parseUri(final List<String> request) {
        return request.get(0).split(" ")[1];
    }

    public String getUri() {
        return uri;
    }

    public String getHeaders(final String header) {
        return this.headers.getHeader(header);
    }
}
