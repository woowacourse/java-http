package org.apache.coyote.http11;

import java.util.List;

public class HttpRequest {
    private final List<String> request;
    private final HttpMethod method;
    private final String requestUri;
    private final String path;
    private final QueryString queryString;
    private final String protocol;
    private final String host;
    private final String connection;

    public HttpRequest(final List<String> request) {
        this.request = request;
        final String[] firstLine = request.get(0).split(" ");
        this.method = HttpMethod.of(firstLine[0]);
        this.requestUri = firstLine[1];
        this.protocol = firstLine[2];
        final List<String> requestTokens = List.of(requestUri.split("\\?"));
        this.path = requestTokens.get(0);
        this.queryString = QueryString.of(requestTokens);
        this.host = request.get(1).split(" ")[1];
        this.connection = request.get(2).split(" ")[1];
    }

    public List<String> getRequest() {
        return request;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getPath() {
        return path;
    }

    public QueryString getQueryString() {
        return queryString;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getConnection() {
        return connection;
    }
}
