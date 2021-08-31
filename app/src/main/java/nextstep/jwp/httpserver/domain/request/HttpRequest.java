package nextstep.jwp.httpserver.domain.request;

import java.util.Map;

import nextstep.jwp.httpserver.domain.Body;
import nextstep.jwp.httpserver.domain.Headers;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(RequestLine requestLine, Headers headers, Body body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String getRequestUri() {
        return requestLine.getRequestTarget();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public Map<String, String> getBodyToMap() {
        return body.getBody();
    }

    public RequestLine getStartLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }
}
