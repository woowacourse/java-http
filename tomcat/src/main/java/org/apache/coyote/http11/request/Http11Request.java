package org.apache.coyote.http11.request;

public class Http11Request {

    private final HttpMethod httpMethod;
    private final String path;
    private final String query;
    private final HttpHeader httpHeader;
    private final RequestBody requestBody;

    public Http11Request(final HttpMethod httpMethod, final String path, final String query, final HttpHeader httpHeader, final RequestBody requestBody) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.query = query;
        this.httpHeader = httpHeader;
        this.requestBody = requestBody;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
