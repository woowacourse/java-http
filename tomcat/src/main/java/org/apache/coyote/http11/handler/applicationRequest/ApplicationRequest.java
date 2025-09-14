package org.apache.coyote.http11.handler.applicationRequest;

import org.apache.coyote.http11.general.HttpBody;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.httpRequest.HttpMethod;
import org.apache.coyote.http11.httpRequest.HttpRequest;

public class ApplicationRequest {

    private final HttpMethod method;
    private final HttpHeaders headers;
    private final HttpBody body;

    private ApplicationRequest(HttpMethod method, HttpHeaders headers, HttpBody body) {
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public static ApplicationRequest from(HttpRequest httpRequest) {
        HttpMethod method = httpRequest.getMethod();
        HttpHeaders headers = httpRequest.getHeaders();
        HttpBody body = httpRequest.getBody();
        return new ApplicationRequest(method, headers, body);
    }

    public String getBodyValueOf(String key) {
        return this.body.get(key);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }
}
