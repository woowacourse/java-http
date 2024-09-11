package org.apache.coyote.http.request;

import java.util.Optional;
import org.apache.coyote.http.HttpMethod;

public class Request {

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
