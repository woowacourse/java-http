package org.apache.coyote.http11.request;

public class HttpRequest {

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private StartLine startLine;
    private RequestHeaders requestHeaders;
    private RequestBody requestBody;
    private Session session;

    public HttpRequest(StartLine startLine, RequestHeaders requestHeaders,
                       RequestBody requestBody) {
        this.startLine = startLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.session = SESSION_MANAGER.findSession(requestHeaders.getJSessionId());
    }

    public HttpRequest() {

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

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Session getSession() {
        return session;
    }

    public boolean isGet() {
        return startLine.getHttpMethod().isGetMethod();
    }

    public boolean isPost() {
        return startLine.getHttpMethod().isPostMethod();
    }

    public void setStartLine(StartLine startLine) {
        this.startLine = startLine;
    }

    public void setRequestHeaders(RequestHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }
}
