package nextstep.jwp.httpserver.domain.request;

import nextstep.jwp.httpserver.domain.Body;
import nextstep.jwp.httpserver.domain.Headers;
import nextstep.jwp.httpserver.domain.HttpMethod;

public class HttpRequest {
    private final StartLine startLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(StartLine startLine, Headers headers, Body body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public String getRequestUri() {
        return startLine.getRequestTarget();
    }

    public HttpMethod getHttpMethod() {
        return startLine.getHttpMethod();
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getRequestBody() {
        return body;
    }
}
