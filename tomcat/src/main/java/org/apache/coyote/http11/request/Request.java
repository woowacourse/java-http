package org.apache.coyote.http11.request;

public class Request {

    private final StartLine startLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    public Request(StartLine startLine, RequestHeaders requestHeaders,
                   RequestBody requestBody) {
        this.startLine = startLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public HttpMethod getHttpMethod() {
        return this.startLine.getHttpMethod();
    }

    public Path getPath() {
        return this.startLine.getPath();
    }

    public QueryParameters getQueryParameters() {
        return this.startLine.getQueryParameters();
    }

    public boolean checkRequestPath(String path) {
        return this.startLine.checkRequest(path);
    }
}
