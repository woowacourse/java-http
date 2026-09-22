package org.apache.coyote.http11.request;

import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpHeaders;

public class HttpRequest {

    private final String method;
    private final String path;
    private final String queryString;
    private final String httpVersion;
    private final HttpHeaders headers;
    private final HttpRequestBody body;
    private final Session session;

    public HttpRequest(String requestLine, HttpHeaders headers, HttpRequestBody body) {
        validateNotBlank(requestLine);

        String[] requestParts = requestLine.split(" ");
        validatePartCount(requestParts);

        this.method = requestParts[0];
        this.httpVersion = requestParts[2];

        String requestTarget = requestParts[1];
        String path = requestTarget;
        String queryString = "";
        int index = requestTarget.indexOf("?");

        if (index != -1) {
            path = requestTarget.substring(0, index);
            queryString = requestTarget.substring(index + 1);
        }

        this.path = path;
        this.queryString = queryString;
        this.headers = headers;
        this.body = body;
        this.session = null;
    }

    public HttpRequest withSession(Session session) {
        return new HttpRequest(this, session);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpRequestBody getBody() {
        return body;
    }

    public Session getSession() {
        return session;
    }

    private HttpRequest(HttpRequest request, Session session) {
        this.method = request.method;
        this.path = request.path;
        this.queryString = request.queryString;
        this.httpVersion = request.httpVersion;
        this.headers = request.headers;
        this.body = request.body;
        this.session = session;
    }

    private void validateNotBlank(String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("요청 라인이 비어 있습니다.");
        }
    }

    private void validatePartCount(String[] requestParts) {
        if (requestParts.length != 3) {
            throw new IllegalArgumentException("요청 라인 형식이 올바르지 않습니다.");
        }
    }
}
