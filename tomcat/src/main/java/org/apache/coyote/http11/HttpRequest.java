package org.apache.coyote.http11;

public class HttpRequest {

    private final HttpStartLine startLine;
    private final HttpHeader header;
    private final HttpRequestBody body;
    private final HttpQueryParameter queryParameter;

    public HttpRequest(HttpStartLine startLine, HttpHeader header, HttpRequestBody body, HttpQueryParameter queryParameter) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
        this.queryParameter = queryParameter;
    }

    public HttpMethod getHttpMethod() {
        return startLine.getHttpMethod();
    }

    public HttpUri getUri() {
        return startLine.getUri();
    }

    public String getResourcePath() {
        return startLine.getResourcePath();
    }

    public HttpProtocol getHttpProtocol() {
        return startLine.getProtocol();
    }

    public String getQueryParameter(String name) {
        return queryParameter.getValue(name);
    }
}
