package org.apache.coyote.http11.request;

import java.util.Set;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

public class HttpRequest {
    private static final String JSESSIONID = "JSESSIONID";
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;
    private final HttpParams bodyParams;
    private final HttpCookie cookie;
    private Session session;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, HttpBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.bodyParams = parseBodyParams(requestLine, headers, body);
        this.cookie = headers.getCookie();
    }

    public static HttpRequest from(String requestLine, HttpHeaders headers, HttpBody body) {
        return new HttpRequest(RequestLine.from(requestLine), headers, body);
    }

    public static HttpRequest from(
            String requestLine,
            HttpHeaders headers,
            HttpBody body,
            Set<HttpMethod> supportedMethods
    ) {
        RequestLine parsedRequestLine = RequestLine.from(requestLine, supportedMethods);

        return new HttpRequest(parsedRequestLine, headers, body);
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public Session getSession(boolean create) {
        if (session != null) {
            return session;
        }

        SessionManager sessionManager = SessionManager.getInstance();
        session = sessionManager.findSession(cookie.get(JSESSIONID));
        if (session == null && create) {
            session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
        }

        return session;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getHttpPath() {
        return requestLine.getPath();
    }

    public String getQueryParams(String key) {
        return requestLine.getParams(key);
    }

    public String getBodyParams(String key) {
        return bodyParams.get(key);
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getHost() {
        String authority = requestLine.getAuthority();
        if (authority != null) {
            return authority;
        }
        return headers.get("Host");
    }

    public HttpBody getBody() {
        return body;
    }

    private static HttpParams parseBodyParams(RequestLine requestLine, HttpHeaders headers, HttpBody body) {
        if (requestLine.getMethod() != HttpMethod.POST) {
            return HttpParams.from(null);
        }

        if (!FORM_URLENCODED.equals(headers.getMediaType())) {
            return HttpParams.from(null);
        }

        return HttpParams.from(body.getContent());
    }
}
