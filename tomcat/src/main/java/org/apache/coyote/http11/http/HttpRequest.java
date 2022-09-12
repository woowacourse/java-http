package org.apache.coyote.http11.http;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final RequestBody requestBody;
    private final HttpCookie httpCookie;

    public HttpRequest(HttpMethod httpMethod, String requestUri) {
        this(new RequestLine(httpMethod, requestUri, Map.of()), new HttpHeaders(), new RequestBody(), new HttpCookie());
    }

    public HttpRequest(HttpMethod httpMethod, String requestUri, HttpHeaders headers, RequestBody requestBody,
                       HttpCookie httpCookie) {
        this(new RequestLine(httpMethod, requestUri, Map.of()), headers, requestBody, httpCookie);
    }

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, RequestBody requestBody, HttpCookie httpCookie) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
        this.httpCookie = httpCookie;
    }

    public static HttpRequest of(RequestLine requestLine, HttpHeaders headers, RequestBody requestBody,
                                 HttpCookie httpCookie)
            throws IOException {
        return new HttpRequest(requestLine, headers, requestBody, httpCookie);
    }

    public boolean isRoot() {
        return requestLine.getRequestUri().equals("/");
    }

    public boolean isGet() {
        return requestLine.getHttpMethod().isGet();
    }

    public boolean isPost() {
        return requestLine.getHttpMethod().isPost();
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getRequestUri() {
        return requestLine.getRequestUri();
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getQueryParams();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Session getSession() {
        String uuid = UUID.randomUUID().toString();
        Session session = new Session(uuid);
        SessionManager.add(session);
        return session;
    }

    public String getJSessionId() {
        return httpCookie.getJSessionId();
    }
}
