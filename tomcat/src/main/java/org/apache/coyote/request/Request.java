package org.apache.coyote.request;

import java.util.Optional;
import org.apache.SessionManager;

public class Request {

    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    private final RequestLine requestLine;

    private final RequestHeaders headers;

    private final RequestBody body;

    public Request(RequestLine requestLine, RequestHeaders headers, RequestBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParamValue(String key) {
        return requestLine.getQueryParamValue(key);
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }

    public String getBodyValue(String key) {
        return body.getValue(key);
    }

    public boolean existQueryParams() {
        return requestLine.existQueryParams();
    }

    public Optional<String> findJSessionId() {
        return headers.findJSessionId();
    }
}
