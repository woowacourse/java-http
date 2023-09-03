package org.apache.coyote.http11.request;

import java.util.List;

public class HttpRequest {

    private final String uri;
    private final Headers headers;
    private final Params params;

    private HttpRequest(final String uri, final Headers headers, final Params params) {
        this.uri = uri;
        this.headers = headers;
        this.params = params;
    }

    public static HttpRequest from(final List<String> request) {
        String uri = getUri(request);
        Headers headers = Headers.from(getHeaders(request));
        return getHttpRequest(uri, headers);
    }

    private static List<String> getHeaders(final List<String> request) {
        return request.subList(1, request.size());
    }

    private static String getUri(final List<String> request) {
        return request.get(0).split(" ")[1];
    }

    private static HttpRequest getHttpRequest(final String uri, final Headers headers) {
        int queryIndex = uri.indexOf("?");

        if (queryIndex == -1) {
            return new HttpRequest(uri, headers, Params.createEmpty());
        }

        String query = uri.substring(queryIndex + 1);
        return new HttpRequest(getPath(uri, queryIndex), headers, Params.from(query));
    }

    private static String getPath(final String uri, final int queryStartIndex) {
        return uri.substring(0, queryStartIndex);
    }

    public boolean isSameUri(final String uri) {
        return this.uri.equals(uri);
    }

    public boolean hasResource() {
        return uri.contains(".");
    }

    public String getHeaders(final String header) {
        return headers.getHeader(header);
    }

    public String getUri() {
        return uri;
    }

    public Params getParams() {
        return params;
    }
}
