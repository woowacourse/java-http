package org.apache.coyote.http11.request;

import java.util.List;

public class HttpRequest {

    private final Uri uri;
    private final Headers headers;
    private final Params params;

    private HttpRequest(final Uri uri, final Headers headers, final Params params) {
        this.uri = uri;
        this.headers = headers;
        this.params = params;
    }

    public static HttpRequest from(final List<String> request) {
        Uri uri = Uri.from(request);
        Headers headers = Headers.from(getHeaders(request));
        return getHttpRequest(uri, headers);
    }

    private static List<String> getHeaders(final List<String> request) {
        return request.subList(1, request.size());
    }

    private static HttpRequest getHttpRequest(final Uri uri, final Headers headers) {
        if (!uri.hasQueryParams()) {
            return new HttpRequest(uri, headers, Params.createEmpty());
        }

        String query = uri.getQueryParams();
        return new HttpRequest(uri, headers, Params.from(query));
    }

    public boolean isSamePath(final String path) {
        return this.uri.isSamePath(path);
    }

    public boolean hasResource() {
        return uri.hasResource();
    }

    public HttpMethod getHttpMethod() {
        return uri.getHttpMethod();
    }

    public String getHeaders(final String header) {
        return headers.getHeader(header);
    }

    public String getPath() {
        return uri.getPath();
    }

    public Params getParams() {
        return params;
    }
}
