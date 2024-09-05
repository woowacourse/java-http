package org.apache.coyote.http11.request;

import org.apache.coyote.http11.Path;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.queryparam.QueryParams;

public class HttpRequest {
    private final HttpMethod method;
    private final Path path;
    private final QueryParams queryParams;
    private final String version;
    private final Headers headers;

    public HttpRequest(final RequestLine requestLine, final Headers headers) {
        this.method = requestLine.getHttpMethod();
        this.path = requestLine.getPath();
        this.queryParams = requestLine.getQueryParams();
        this.version = requestLine.getVersion();
        this.headers = headers;
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getPath() {
        return path.value();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getVersion() {
        return version;
    }

    public QueryParams getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(final String name) {
        return queryParams.getQueryParam(name);
    }
}
