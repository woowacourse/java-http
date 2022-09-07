package org.apache.coyote.http11.request;

public class Request {

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private final StartLine startLine;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;
    private final Session session;

    public Request(StartLine startLine, RequestHeaders requestHeaders,
                   RequestBody requestBody) {
        this.startLine = startLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.session = SESSION_MANAGER.findSession(requestHeaders.getJSessionId());
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
}
